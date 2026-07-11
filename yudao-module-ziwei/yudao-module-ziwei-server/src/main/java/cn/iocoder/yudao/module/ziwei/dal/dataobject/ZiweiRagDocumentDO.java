package cn.iocoder.yudao.module.ziwei.dal.dataobject;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 紫微斗数 RAG 文档 DO
 * <p>
 * 记录上传到 MinIO 并完成向量化的命理古籍文档。
 *
 * @author JTWORLD
 */
@TableName("ziwei_rag_document")
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZiweiRagDocumentDO extends BaseDO {

    @TableId
    private Long id;

    /** 书名 */
    private String bookTitle;

    /** 作者 / 来源 */
    private String bookAuthor;

    /** 原始文件名 */
    private String fileName;

    /** MinIO 文件 URL */
    private String fileUrl;

    /** 文件大小（bytes） */
    private Long fileSize;

    /** 切分段数 */
    private Integer segmentCount;

    /** 状态：0-处理中 1-已完成 2-失败 */
    private Integer status;

    /** 失败原因（status=2 时记录） */
    private String errorMessage;

}
