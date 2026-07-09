package cn.iocoder.yudao.module.ziwei.framework.rpc.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

/**
 * 紫微斗数模块 - RPC 配置
 * <p>
 * 后期如需调用其他模块的 Feign 接口，在此处注册
 *
 * @author JTWORLD
 */
@Configuration(value = "ziweiRpcConfiguration", proxyBeanMethods = false)
@EnableFeignClients(clients = {})
public class RpcConfiguration {
}
