package cn.iocoder.yudao.module.ziwei.controller.admin;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.ziwei.controller.admin.vo.ZiweiChartCreateReqVO;
import cn.iocoder.yudao.module.ziwei.controller.admin.vo.ZiweiChartPageReqVO;
import cn.iocoder.yudao.module.ziwei.controller.admin.vo.ZiweiChartRespVO;
import cn.iocoder.yudao.module.ziwei.service.chart.ZiweiChartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

/**
 * 管理后台 - 紫微斗数命盘 Controller
 *
 * @author JTWORLD
 */
@Tag(name = "管理后台 - 紫微斗数命盘")
@RestController
@RequestMapping("/ziwei/chart")
@Validated
public class ZiweiChartController {

    @Resource
    private ZiweiChartService chartService;

    @PostMapping("/create")
    @Operation(summary = "录入出生信息并排盘")
    public CommonResult<ZiweiChartRespVO> createChart(@Valid @RequestBody ZiweiChartCreateReqVO reqVO) {
        return success(chartService.calculateAndSave(reqVO));
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询命盘")
    public CommonResult<PageResult<ZiweiChartRespVO>> getChartPage(@Valid ZiweiChartPageReqVO reqVO) {
        return success(chartService.getChartPage(reqVO));
    }

    @GetMapping("/get")
    @Operation(summary = "获取命盘详情")
    public CommonResult<ZiweiChartRespVO> getChart(@RequestParam("id") Long id) {
        return success(chartService.getChart(id));
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除命盘")
    public CommonResult<Boolean> deleteChart(@RequestParam("id") Long id) {
        chartService.deleteChart(id);
        return success(true);
    }

}
