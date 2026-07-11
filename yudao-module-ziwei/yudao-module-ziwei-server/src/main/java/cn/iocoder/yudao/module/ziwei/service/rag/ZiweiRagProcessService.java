package cn.iocoder.yudao.module.ziwei.service.rag;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.module.ziwei.dal.dataobject.ZiweiRagDocumentDO;
import cn.iocoder.yudao.module.ziwei.dal.mysql.ZiweiRagDocumentMapper;
import cn.iocoder.yudao.module.ziwei.enums.RagDocumentStatusEnum;
import cn.iocoder.yudao.module.ziwei.service.rag.splitter.ZiweiTextSplitter;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * RAG 古籍文档异步处理服务
 * <p>
 * 负责 PDF 解析、古文切分、向量化等耗时操作，异步执行避免阻塞 HTTP 请求。
 *
 * @author JTWORLD
 */
@Service
@Slf4j
public class ZiweiRagProcessService {

    /** Milvus 向量元数据 key */
    private static final String META_DOCUMENT_ID = "documentId";
    private static final String META_BOOK_TITLE = "bookTitle";
    private static final String META_CHAPTER = "chapter";

    @Autowired(required = false)
    private VectorStore vectorStore;

    @Resource
    private ZiweiRagDocumentMapper documentMapper;

    /**
     * 异步处理古籍文档：PDF 解析 → 古文切分 → 向量化 → 更新状态
     *
     * @param documentId 数据库记录 ID
     * @param fileBytes  文件字节数组
     * @param bookTitle  书名
     */
    @Async("ragProcessExecutor")
    public void processAsync(Long documentId, byte[] fileBytes, String bookTitle) {
        log.info("[processAsync] 开始异步处理文档: documentId={}, bookTitle={}", documentId, bookTitle);

        try {
            // 1. Tika 解析 PDF 提取文本
            String content = parsePdf(fileBytes);
            if (StrUtil.isEmpty(content)) {
                markFailed(documentId, "PDF 解析失败，未能提取到文本内容");
                return;
            }
            log.info("[processAsync] PDF 解析完成, documentId={}, 文本长度: {} 字符", documentId, content.length());

            // 2. 古文定制切分
            ZiweiTextSplitter splitter = new ZiweiTextSplitter();
            List<String> segments = splitter.split(content);
            log.info("[processAsync] 文本切分完成, documentId={}, 共 {} 段", documentId, segments.size());

            // 3. 向量化并存入 Milvus
            if (vectorStore == null) {
                log.warn("[processAsync] VectorStore 未配置，跳过向量化, documentId={}", documentId);
                markCompleted(documentId, segments.size());
                return;
            }

            List<Document> docs = new ArrayList<>();
            for (int i = 0; i < segments.size(); i++) {
                String segmentText = segments.get(i);
                if (StrUtil.isBlank(segmentText)) {
                    continue;
                }
                Document doc = new Document(segmentText);
                doc.getMetadata().put(META_DOCUMENT_ID, documentId.toString());
                doc.getMetadata().put(META_BOOK_TITLE, bookTitle);
                doc.getMetadata().put(META_CHAPTER, detectChapter(segmentText));
                docs.add(doc);
            }

            if (CollUtil.isEmpty(docs)) {
                markFailed(documentId, "文本切分后无有效段落");
                return;
            }

            vectorStore.add(docs);
            log.info("[processAsync] 向量化完成, documentId={}, 共 {} 条向量", documentId, docs.size());

            // 4. 更新记录为已完成
            markCompleted(documentId, docs.size());

        } catch (Exception e) {
            log.error("[processAsync] 异步处理失败, documentId={}", documentId, e);
            markFailed(documentId, e.getMessage());
        }
    }

    // ========== 状态更新 ==========

    private void markCompleted(Long documentId, int segmentCount) {
        ZiweiRagDocumentDO update = new ZiweiRagDocumentDO();
        update.setId(documentId);
        update.setStatus(RagDocumentStatusEnum.COMPLETED.getStatus());
        update.setSegmentCount(segmentCount);
        documentMapper.updateById(update);
        log.info("[processAsync] 文档处理完成: documentId={}, segmentCount={}", documentId, segmentCount);
    }

    private void markFailed(Long documentId, String errorMessage) {
        ZiweiRagDocumentDO update = new ZiweiRagDocumentDO();
        update.setId(documentId);
        update.setStatus(RagDocumentStatusEnum.FAILED.getStatus());
        update.setErrorMessage(errorMessage != null && errorMessage.length() > 1000
                ? errorMessage.substring(0, 1000) : errorMessage);
        documentMapper.updateById(update);
        log.warn("[processAsync] 文档处理失败: documentId={}, error={}", documentId, errorMessage);
    }

    // ========== 文本处理私有方法 ==========

    /**
     * Tika 解析 PDF 提取纯文本
     */
    private String parsePdf(byte[] fileBytes) {
        ByteArrayResource resource = new ByteArrayResource(fileBytes);
        TikaDocumentReader reader = new TikaDocumentReader(resource);
        List<Document> documents = reader.get();
        if (CollUtil.isEmpty(documents)) {
            return "";
        }
        return documents.get(0).getText();
    }

    /**
     * 检测文本段所属章节
     */
    private String detectChapter(String text) {
        if (StrUtil.isBlank(text)) {
            return "";
        }
        java.util.regex.Matcher m = java.util.regex.Pattern.compile(
                "(卷[之第]?[一二三四五六七八九十百千0-9]+)").matcher(text);
        if (m.find()) {
            return m.group(1);
        }
        m = java.util.regex.Pattern.compile(
                "(第[一二三四五六七八九十百千0-9]+[篇章节])").matcher(text);
        if (m.find()) {
            return m.group(1);
        }
        return "";
    }

}
