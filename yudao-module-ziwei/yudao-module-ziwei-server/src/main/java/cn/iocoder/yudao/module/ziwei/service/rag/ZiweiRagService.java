package cn.iocoder.yudao.module.ziwei.service.rag;

import java.util.List;

/**
 * 紫微斗数 RAG 服务接口
 * <p>
 * 提供命理古籍的上传、向量化、检索和删除功能。
 *
 * @author JTWORLD
 */
public interface ZiweiRagService {

    /**
     * 上传 PDF 书籍，自动解析、切分并向量化存入 Milvus
     *
     * @param fileBytes  文件字节数组
     * @param fileName   原始文件名
     * @param bookTitle  书名
     * @param bookAuthor 作者/来源
     * @return 文档 ID
     */
    Long uploadBook(byte[] fileBytes, String fileName, String bookTitle, String bookAuthor);

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
