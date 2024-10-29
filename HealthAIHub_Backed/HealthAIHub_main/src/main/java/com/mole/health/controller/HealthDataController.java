package com.mole.health.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mole.health.dto.health.HealthDataDto;
import com.mole.health.dto.health.HealthDataSaveDto;
import com.mole.health.pojo.HealthData;
import com.mole.health.result.CommonPage;
import com.mole.health.result.CommonResult;
import com.mole.health.service.HealthDataService;
import com.mole.health.util.StpKit;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/health-data")
@Tag(name = "HealthDataController", description = "健康数据管理")
public class HealthDataController {

    private final HealthDataService healthDataService;

    @Autowired
    public HealthDataController(HealthDataService healthDataService) {
        this.healthDataService = healthDataService;
    }

    @GetMapping("/list")
    @Operation(summary = "获取所有健康数据")
    public CommonResult<CommonPage<HealthDataDto>> getAllHealthData(@RequestParam(defaultValue = "1") int pageNum, @RequestParam(defaultValue = "10") int pageSize) {
        IPage<HealthData> page = healthDataService.page(new Page<>(pageNum, pageSize), null);
        CommonPage<HealthDataDto> commonPage = CommonPage.restPage(page, healthDataService::convertToDto);
        return CommonResult.success(commonPage);
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询指定用户的最新健康数据")
    @ApiResponse(responseCode = "200", description = "查询成功")
    public CommonResult<HealthDataDto> getByUserId(@Parameter(description = "用户ID") @PathVariable String id) {
        HealthData healthData = healthDataService.getLatestByUserId(id);
        if (healthData == null) {
            return CommonResult.failed("未找到指定用户的健康数据");
        }
        HealthDataDto dto = healthDataService.convertToDto(healthData);
        return CommonResult.success(dto);
    }

    @PostMapping("/current")
    @Operation(summary = "当前用户录入健康数据")
    @ApiResponse(responseCode = "200", description = "健康数据录入成功")
    public CommonResult<Boolean> insertCurrentUserHealthData(@RequestBody @Valid HealthDataSaveDto healthDataDto) {
        Long userId = StpKit.USER.getLoginIdAsLong();
        HealthData healthData = healthDataService.convertToHealthData(healthDataDto);
        healthData.setUserId(userId);
        boolean result = healthDataService.saveOrUpdate(healthData);
        return result ? CommonResult.success(true) : CommonResult.failed("健康数据录入失败");
    }
}
