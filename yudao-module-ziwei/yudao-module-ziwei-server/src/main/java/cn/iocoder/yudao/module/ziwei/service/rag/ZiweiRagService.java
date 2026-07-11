package cn.iocoder.yudao.module.ziwei.service.rag;

import cn.iocoder.yudao.module.ziwei.dal.dataobject.ZiweiRagDocumentDO;

import java.util.List;

/**
 * 紫微斗数 RAG 服务接口
 * <p>
 * 提供命理古籍的上传、向量化、检索和删除功能。
 * 上传接口为同步操作（仅完成 MinIO 上传 + 数据库记录），
 * PDF 解析和向量化由 {@link ZiweiRagProcessService} 异步执行。
 *
 * @author JTWORLD
 */
public interface ZiweiRagService {

    /**
     * 上传 PDF 书籍（同步部分）
     * <p>
     * 仅完成文件校验、MinIO 上传和数据库记录写入。
     * 返回后文档状态为"处理中"，PDF 解析/切分/向量化由后台异步执行。
     *
     * @param fileBytes  文件字节数组
     * @param fileName   原始文件名
     * @param bookTitle  书名
     * @param bookAuthor 作者/来源
     * @return 保存后的文档记录（状态为处理中）
     */
    ZiweiRagDocumentDO uploadBook(byte[] fileBytes, String fileName, String bookTitle, String bookAuthor);

    /**
     * 检索相关知识段落
     *
     * @param query 查询文本
     * @param topK  返回结果数
     * @return 相关文段列表
     */
    List<ZiweiRagResult> searchKnowledge(String query, int topK);

    /**
     * 删除文档及其向量
     *
     * @param documentId 文档 ID
     */
    void deleteDocument(Long documentId);

    /**
     * 检索结果
     */
    record ZiweiRagResult(
            String content,
            String bookTitle,
            String chapter,
            double score
    ) {}
}
