package cn.iocoder.yudao.module.ziwei.enums;

import cn.iocoder.yudao.framework.common.enums.RpcConstants;

/**
 * API 相关的枚举
 *
 * @author JTWORLD
 */
public class ApiConstants {

    /**
     * 服务名
     * <p>
     * 注意，需要保证和 spring.application.name 保持一致
     */
    public static final String NAME = "ziwei-server";

    public static final String PREFIX = RpcConstants.RPC_API_PREFIX + "/ziwei";

    public static final String VERSION = "1.0.0";

}
