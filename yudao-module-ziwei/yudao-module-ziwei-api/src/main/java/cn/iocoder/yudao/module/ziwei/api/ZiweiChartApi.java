package cn.iocoder.yudao.module.ziwei.api;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.ziwei.dto.ZiweiBirthInfoDTO;
import cn.iocoder.yudao.module.ziwei.dto.ZiweiChartSummaryDTO;
import cn.iocoder.yudao.module.ziwei.enums.ApiConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 紫微斗数命盘 API（Feign 接口）
 *
 * @author JTWORLD
 */
@FeignClient(name = ApiConstants.NAME)
@Tag(name = "RPC 服务 - 紫微斗数")
public interface ZiweiChartApi {

    String PREFIX = ApiConstants.PREFIX + "/chart";

    @PostMapping(PREFIX + "/calculate")
    @Operation(summary = "计算命盘")
    CommonResult<ZiweiChartSummaryDTO> calculateChart(@RequestBody ZiweiBirthInfoDTO birthInfo);

    @GetMapping(PREFIX + "/get")
    @Operation(summary = "获取命盘摘要")
    CommonResult<ZiweiChartSummaryDTO> getChart(@RequestParam("id") Long id);

}
