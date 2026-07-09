package cn.iocoder.yudao.module.ziwei.enums;

import cn.iocoder.yudao.framework.common.exception.ErrorCode;

/**
 * 紫微斗数错误码枚举类
 * <p>
 * ziwei 系统，使用 1-041-000-000 段
 *
 * @author JTWORLD
 */
public interface ErrorCodeConstants {

    // ========== 命盘 1-041-000-000 ==========
    ErrorCode CHART_NOT_EXISTS = new ErrorCode(1_041_000_000, "命盘不存在");
    ErrorCode CHART_CALCULATE_ERROR = new ErrorCode(1_041_000_001, "命盘计算失败：{}");

    // ========== 出生信息 1-041-001-000 ==========
    ErrorCode BIRTH_DATE_INVALID = new ErrorCode(1_041_001_000, "出生日期不合法");
    ErrorCode BIRTH_TIME_INVALID = new ErrorCode(1_041_001_001, "出生时间不合法（小时应在 0-23 之间）");
    ErrorCode GENDER_INVALID = new ErrorCode(1_041_001_002, "性别参数不合法");

    // ========== 阴历转换 1-041-002-000 ==========
    ErrorCode LUNAR_CONVERSION_ERROR = new ErrorCode(1_041_002_000, "阴历转换失败：{}");

}
