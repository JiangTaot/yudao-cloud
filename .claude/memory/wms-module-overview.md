---
name: wms-module-overview
description: WMS 仓库管理系统模块的架构、功能、技术栈与完成度概览
metadata:
  type: project
---

# yudao-module-wms 模块概览

## 定位
yudao-cloud 平台的仓库管理系统（WMS）模块，目前处于 **MVP 阶段**，共 **165 个 Java 文件**。

## 模块结构（-api / -server 双模块）
- `yudao-module-wms-api`: Feign 接口、DTO、枚举（单据类型、状态、错误码）
- `yudao-module-wms-server`: Controller、Service、Mapper、DO
  - 服务端口: **48092**
  - 启动类: `cn.iocoder.yudao.module.wms.WmsServerApplication`

## 业务功能划分

### 基础资料 (MD) — `service/md/`
| 领域 | Controller | Service | 说明 |
|------|-----------|---------|------|
| 仓库 | WmsWarehouseController | WmsWarehouseServiceImpl | 仓库 CRUD |
| 商户/货主 | WmsMerchantController | WmsMerchantServiceImpl | 货主、供应商、客户 |
| 物料 | WmsItemController | WmsItemServiceImpl | 商品主档 |
| 品牌 | WmsItemBrandController | WmsItemBrandServiceImpl | 品牌管理 |
| 品类 | WmsItemCategoryController | WmsItemCategoryServiceImpl | 树形品类 |
| SKU | WmsItemSkuController | WmsItemSkuServiceImpl | 商品 SKU |

### 订单 — `service/order/`
| 领域 | Controller | Service | 单据类型 |
|------|-----------|---------|----------|
| 入库单 | WmsReceiptOrderController + DetailController | WmsReceiptOrderServiceImpl + DetailServiceImpl | 采购/退货/调拨入库 |
| 出库单 | WmsShipmentOrderController + DetailController | WmsShipmentOrderServiceImpl + DetailServiceImpl | 销售/领料出库 |
| 移库单 | WmsMovementOrderController + DetailController | WmsMovementOrderServiceImpl + DetailServiceImpl | 仓库间调拨 |
| 盘库单 | WmsCheckOrderController + DetailController | WmsCheckOrderServiceImpl + DetailServiceImpl | 库存盘点 |

### 库存 — `service/inventory/`
- **库存余额**: `WmsInventoryServiceImpl` — 按 SKU+仓库维度存储，支持分页/列表查询
- **库存变更**: `changeInventory()` — 统一入口，所有单据完成时调用
- **库存盘点**: `checkInventory()` — 账面 vs 实盘对比，盈亏生成调整流水
- **库存流水**: `WmsInventoryHistoryServiceImpl` — 变更前/后数量、金额、关联单据号

### 首页统计 — `service/home/`
- `WmsHomeStatisticsServiceImpl` — 单据状态分布、每日趋势、库存排行
- 复杂统计通过手写 Mapper XML 实现（UNION ALL 多表聚合）

## 核心技术实现

### 库存并发控制（最关键的设计）
文件: `WmsInventoryServiceImpl.java:139-167`
1. 按 SKU+仓库维度创建或锁定库存行
2. `selectListByIdsForUpdate()` 加 `FOR UPDATE` 行锁
3. 内存中校验库存充足性（出库不能为负）
4. 批量 `updateBatch` 更新数量
5. 并发插入通过捕获 `DuplicateKeyException` + 回查处理唯一索引冲突

### 单据状态机
- PREPARE(草稿) → FINISHED(已完成) / CANCELED(已作废)
- 完成操作通过 `updateByIdAndStatus` CAS 更新，防止并发重复完成
- 只有草稿状态可编辑/删除/完成

### 库存流水审计
- 所有库存变动记录: `beforeQuantity` → `afterQuantity` + `quantity`(变更量) + `price` + `totalPrice`
- 关联: `orderId` + `orderNo` + `orderType`
- 入库为正数变更，出库为负数变更

### VO 拼接模式（防 N+1）
Controller 层批量查询关联数据（商户Map、仓库Map、用户Map、SKUMap、物料Map），通过 `MapUtils.findAndThen()` 批量填充，避免循环查询。

### 多租户
`application.yaml` 中 `yudao.tenant.enable=true`，MyBatis 拦截器自动注入 `tenant_id`。

## 技术栈
| 层次 | 技术 |
|------|------|
| 基础框架 | Spring Boot 3.x + Spring Cloud 2024.x, JDK 17/21 |
| 注册/配置 | Nacos Discovery + Nacos Config |
| 数据库 | MySQL + MyBatis Plus + MyBatis Plus Join |
| 缓存 | Redis (Redisson) |
| 安全 | Spring Security + Token + @PreAuthorize |
| RPC | OpenFeign |
| API 文档 | SpringDoc OpenAPI + Knife4j |
| Excel | FastExcel |
| 任务调度 | XXL-Job |
| 监控 | Spring Boot Admin + SkyWalking |

## 当前完成度
- ✅ 基础资料 CRUD（仓库/商户/物料/品牌/品类/SKU）
- ✅ 四大单据流（入库/出库/移库/盘点）
- ✅ 库存余额 + 库存流水
- ✅ 首页统计看板
- ⚠️ 单元测试部分覆盖（8 个测试类）
- ❌ 策略引擎（波次/拣货/上架策略）
- ❌ 库位管理（库区/库位/储位）
- ❌ 批次/效期管理（FIFO/FEFO）
- ❌ 硬件对接（PDA/电子标签/分拣机）
