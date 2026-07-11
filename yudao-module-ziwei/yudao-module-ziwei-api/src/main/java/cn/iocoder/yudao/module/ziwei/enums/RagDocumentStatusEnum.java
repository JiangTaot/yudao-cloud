package cn.iocoder.yudao.module.ziwei.enums;

import cn.hutool.core.util.ObjUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * RAG 古籍文档处理状态枚举
 *
 * @author JTWORLD
 */
@Getter
@AllArgsConstructor
public enum RagDocumentStatusEnum {

    /** 处理中 —— 文件已上传，正在解析/切分/向量化 */
    PROCESSING(0, "处理中"),

    /** 已完成 —— 解析、切分、向量化全部完成 */
    COMPLETED(1, "已完成"),

    /** 失败 —— 处理过程中出现异常 */
    FAILED(2, "失败");

    public static final Integer[] ARRAYS = Arrays.stream(values())
            .map(RagDocumentStatusEnum::getStatus).toArray(Integer[]::new);

    private final Integer status;
    private final String name;

    public static boolean isProcessing(Integer status) {
        return ObjUtil.equal(PROCESSING.status, status);
    }

    public static boolean isCompleted(Integer status) {
        return ObjUtil.equal(COMPLETED.status, status);
    }

    public static boolean isFailed(Integer status) {
        return ObjUtil.equal(FAILED.status, status);
    }

}
