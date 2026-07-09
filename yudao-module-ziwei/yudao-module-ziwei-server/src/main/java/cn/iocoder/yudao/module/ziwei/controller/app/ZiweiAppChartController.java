package cn.iocoder.yudao.module.ziwei.controller.app;

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
 * 用户端 - 紫微斗数命盘 Controller
 *
 * @author JTWORLD
 */
@Tag(name = "用户端 - 紫微斗数命盘")
@RestController
@RequestMapping("/app-api/ziwei/chart")
@Validated
public class ZiweiAppChartController {

    @Resource
    private ZiweiChartService chartService;

    @PostMapping("/calculate")
    @Operation(summary = "输入出生信息，排盘")
    public CommonResult<ZiweiChartRespVO> calculate(@Valid @RequestBody ZiweiChartCreateReqVO reqVO) {
        return success(chartService.calculateAndSave(reqVO));
    }

    @GetMapping("/get")
    @Operation(summary = "获取命盘详情")
    public CommonResult<ZiweiChartRespVO> getChart(@RequestParam("id") Long id) {
        return success(chartService.getChart(id));
    }

    @GetMapping("/my-list")
    @Operation(summary = "我的命盘列表")
    public CommonResult<PageResult<ZiweiChartRespVO>> myList(@Valid ZiweiChartPageReqVO reqVO) {
        // TODO: 从当前登录用户获取 userId
        return success(chartService.getChartPage(reqVO));
    }

}
