package cn.iocoder.yudao.module.ziwei.controller.admin;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.ziwei.engine.model.DaxianCycle;
import cn.iocoder.yudao.module.ziwei.engine.model.LiuninResult;
import cn.iocoder.yudao.module.ziwei.service.cycle.ZiweiCycleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

/**
 * 管理后台 - 紫微斗数大限/流年
 *
 * @author JTWORLD
 */
@Tag(name = "管理后台 - 紫微斗数大限/流年")
@RestController
@RequestMapping("/ziwei/cycle")
@Validated
public class ZiweiAdminCycleController {

    @Resource
    private ZiweiCycleService cycleService;

    @GetMapping("/daxian/{chartId}")
    @Operation(summary = "获取命盘所有大限")
    public CommonResult<List<DaxianCycle>> getDaxianList(
            @Parameter(description = "命盘ID") @PathVariable("chartId") Long chartId) {
        return success(cycleService.getDaxianList(chartId));
    }

    @GetMapping("/liunian/{chartId}")
    @Operation(summary = "获取指定年份的流年信息")
    public CommonResult<LiuninResult> getLiunin(
            @Parameter(description = "命盘ID") @PathVariable("chartId") Long chartId,
            @Parameter(description = "流年年份") @RequestParam("year") int year) {
        return success(cycleService.getLiunin(chartId, year));
    }

    @GetMapping("/liunian/{chartId}/range")
    @Operation(summary = "获取年份范围内的流年信息")
    public CommonResult<List<LiuninResult>> getLiuninRange(
            @Parameter(description = "命盘ID") @PathVariable("chartId") Long chartId,
            @Parameter(description = "起始年份") @RequestParam("startYear") int startYear,
            @Parameter(description = "结束年份") @RequestParam("endYear") int endYear) {
        return success(cycleService.getLiuninRange(chartId, startYear, endYear));
    }

}
