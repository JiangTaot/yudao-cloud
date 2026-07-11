package cn.iocoder.yudao.module.ziwei.service.rag;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.module.ziwei.dal.dataobject.ZiweiRagDocumentDO;
import cn.iocoder.yudao.module.ziwei.dal.mysql.ZiweiRagDocumentMapper;
import cn.iocoder.yudao.module.ziwei.enums.RagDocumentStatusEnum;
import cn.iocoder.yudao.module.ziwei.framework.rag.config.ZiweiRagConfiguration;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 紫微斗数 RAG 服务实现
 * <p>
 * 全流程：PDF 上传 → MinIO 存储（同步） → 返回处理中状态
 * → Tika 解析 → 古文切分 → 向量化 → Milvus 存储（异步）
 *
 * @author JTWORLD
 */
@Service
@Slf4j
public class ZiweiRagServiceImpl implements ZiweiRagService {

    /** Milvus 向量元数据 key */
    private static final String META_DOCUMENT_ID = "documentId";
    private static final String META_BOOK_TITLE = "bookTitle";
    private static final String META_CHAPTER = "chapter";

    @Resource
    private S3Client s3Client;

    @Resource
    private ZiweiRagConfiguration ragConfig;

    @Autowired(required = false)
    private VectorStore vectorStore;

    @Resource
    private ZiweiRagDocumentMapper documentMapper;

    @Resource
    private ZiweiRagProcessService processService;

    @Override
    public ZiweiRagDocumentDO uploadBook(byte[] fileBytes, String fileName, String bookTitle, String bookAuthor) {
        log.info("[uploadBook] 开始上传书籍: bookTitle={}, fileName={}, fileSize={} bytes",
                bookTitle, fileName, fileBytes.length);

        // 1. 上传文件到 MinIO（同步）
        String objectKey = buildObjectKey(fileName);
        String fileUrl = uploadToMinio(fileBytes, fileName, objectKey);
        log.info("[uploadBook] 文件已上传到 MinIO: url={}", fileUrl);

        // 2. 保存文档记录到 MySQL，状态 = 处理中
        ZiweiRagDocumentDO docDO = ZiweiRagDocumentDO.builder()
                .bookTitle(bookTitle)
                .bookAuthor(bookAuthor)
                .fileName(fileName)
                .fileUrl(fileUrl)
                .fileSize((long) fileBytes.length)
                .segmentCount(0)
                .status(RagDocumentStatusEnum.PROCESSING.getStatus())
                .build();
        documentMapper.insert(docDO);
        Long documentId = docDO.getId();
        log.info("[uploadBook] 文档记录已保存: documentId={}, status=PROCESSING", documentId);

        // 3. 提交异步处理任务（PDF 解析 → 文本切分 → 向量化 → 更新状态）
        processService.processAsync(documentId, fileBytes, bookTitle);
        log.info("[uploadBook] 已提交异步处理任务: documentId={}", documentId);

        return docDO;
    }

    @Override
    public List<ZiweiRagResult> searchKnowledge(String query, int topK) {
        if (StrUtil.isBlank(query)) {
            return List.of();
        }

        log.info("[searchKnowledge] 检索: query={}, topK={}", query, topK);

        if (vectorStore == null) {
            log.warn("[searchKnowledge] VectorStore 未配置，返回空结果");
            return List.of();
        }

        SearchRequest request = SearchRequest.builder()
                .query(query)
                .topK(topK)
                .similarityThreshold(SearchRequest.SIMILARITY_THRESHOLD_ACCEPT_ALL)
                .build();

        List<org.springframework.ai.document.Document> documents = vectorStore.similaritySearch(request);
        if (CollUtil.isEmpty(documents)) {
            log.info("[searchKnowledge] 未检索到相关结果");
            return List.of();
        }

        List<ZiweiRagResult> results = documents.stream()
                .map(doc -> new ZiweiRagResult(
                        doc.getText(),
                        (String) doc.getMetadata().getOrDefault(META_BOOK_TITLE, ""),
                        (String) doc.getMetadata().getOrDefault(META_CHAPTER, ""),
                        doc.getScore() != null ? doc.getScore() : 0.0
                ))
                .collect(Collectors.toList());

        log.info("[searchKnowledge] 检索完成，返回 {} 条结果", results.size());
        return results;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDocument(Long documentId) {
        log.info("[deleteDocument] 删除文档: documentId={}", documentId);

        ZiweiRagDocumentDO docDO = documentMapper.selectById(documentId);
        if (docDO == null) {
            log.warn("[deleteDocument] 文档不存在: documentId={}", documentId);
            return;
        }

        // 1. 删除 MinIO 文件
        try {
            String objectKey = extractObjectKey(docDO.getFileUrl());
            DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                    .bucket(ragConfig.getBucket())
                    .key(objectKey)
                    .build();
            s3Client.deleteObject(deleteRequest);
            log.info("[deleteDocument] MinIO 文件已删除: key={}", objectKey);
        } catch (Exception e) {
            log.warn("[deleteDocument] MinIO 文件删除失败（可能已不存在）: {}", e.getMessage());
        }

        // 2. 删除 Milvus 向量（VectorStore 未就绪时跳过）
        if (vectorStore != null) {
            try {
                vectorStore.delete(new FilterExpressionBuilder()
                        .eq(META_DOCUMENT_ID, documentId.toString()).build());
                log.info("[deleteDocument] Milvus 向量已删除");
            } catch (Exception e) {
                log.warn("[deleteDocument] Milvus 向量删除失败: {}", e.getMessage());
            }
        }

        // 3. 删除数据库记录
        documentMapper.deleteById(documentId);
        log.info("[deleteDocument] 文档记录已删除: documentId={}", documentId);
    }

    // ========== 私有方法 ==========

    /**
     * 上传文件到 MinIO
     */
    private String uploadToMinio(byte[] fileBytes, String fileName, String objectKey) {
        String contentType = detectContentType(fileName);

        PutObjectRequest putRequest = PutObjectRequest.builder()
                .bucket(ragConfig.getBucket())
                .key(objectKey)
                .contentType(contentType)
                .contentDisposition("inline; filename=\"" + fileName + "\"")
                .build();

        s3Client.putObject(putRequest, RequestBody.fromBytes(fileBytes));

        // 返回访问 URL
        return ragConfig.getEndpoint() + "/" + ragConfig.getBucket() + "/" + objectKey;
    }

    /**
     * 生成 MinIO 对象 Key
     */
    private String buildObjectKey(String fileName) {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        return "ziwei-books/" + date + "/" + uuid + "_" + fileName;
    }

    /**
     * 从 URL 提取 object key
     */
    private String extractObjectKey(String fileUrl) {
        // URL 格式: http://127.0.0.1:9000/ziwei-books/ziwei-books/20260710/uuid_file.pdf
        String prefix = ragConfig.getBucket() + "/";
        int idx = fileUrl.indexOf(prefix);
        if (idx >= 0) {
            return fileUrl.substring(idx + prefix.length());
        }
        return fileUrl;
    }

    /**
     * 检测文件 MIME 类型
     */
    private String detectContentType(String fileName) {
        if (StrUtil.isEmpty(fileName)) {
            return "application/octet-stream";
        }
        String lower = fileName.toLowerCase();
        if (lower.endsWith(".pdf")) return "application/pdf";
        if (lower.endsWith(".txt")) return "text/plain; charset=utf-8";
        if (lower.endsWith(".md")) return "text/markdown; charset=utf-8";
        if (lower.endsWith(".doc")) return "application/msword";
        if (lower.endsWith(".docx")) return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
        return "application/octet-stream";
    }

}
