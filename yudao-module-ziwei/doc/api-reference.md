# 紫微斗数 API 对接文档

> 供前端开发者参考。前端仅需调用 API，无需理解排盘算法。

## 基础信息

- 管理后台前缀: `/admin-api/ziwei`
- 用户端前缀: `/app-api/ziwei`
- 响应格式: `CommonResult<T>` = `{ code: 0, data: ..., msg: "成功" }`

## 接口列表

### 1. 排盘（核心接口）

**POST** `/admin-api/ziwei/chart/create`
**POST** `/app-api/ziwei/chart/calculate`

请求体:
```json
{
  "solarYear": 1990,
  "solarMonth": 6,
  "solarDay": 15,
  "solarHour": 14,
  "solarMinute": 30,
  "gender": 1,
  "birthPlace": "北京",
  "isDst": false
}
```

响应参见下方"命盘响应结构"。

### 2. 查询命盘

**GET** `/admin-api/ziwei/chart/get?id={id}`
**GET** `/app-api/ziwei/chart/get?id={id}`

### 3. 分页查询

**GET** `/admin-api/ziwei/chart/page?pageNo=1&pageSize=10&gender=1`

### 4. 删除

**DELETE** `/admin-api/ziwei/chart/delete?id={id}`

## 命盘响应结构

```json
{
  "code": 0,
  "data": {
    "id": 1,
    "solarYear": 1990, "solarMonth": 6, "solarDay": 15,
    "solarHour": 14, "solarMinute": 30,
    "gender": 1, "birthPlace": "北京",
    "yearPillar": "庚午", "monthPillar": "壬午",
    "dayPillar": "辛亥", "hourPillar": "乙未",
    "mingGong": "寅宫", "shenGong": "子宫",
    "wuxingJu": "木三局",
    "palaces": [
      {
        "palaceType": 1, "palaceName": "命宫",
        "dizhi": "寅", "tianGan": "丙",
        "isShenGong": false, "daXianLabel": "3-12岁",
        "majorStars": [
          {"starCode": "ziwei", "starName": "紫微", "brightness": "庙", "sihuaType": null}
        ],
        "auxiliaryStars": [
          {"starCode": "zuofu", "starName": "左辅", "brightness": null, "sihuaType": null}
        ],
        "minorStars": [...]
      }
    ],
    "natalSihua": {
      "huaLu": {"starCode": "taiyang", "starName": "太阳"},
      "huaQuan": {"starCode": "wuqu", "starName": "武曲"},
      "huaKe": {"starCode": "taiyin", "starName": "太阴"},
      "huaJi": {"starCode": "tiantong", "starName": "天同"}
    },
    "daxians": [
      {"ageStart": 3, "ageEnd": 12, "palaceName": "命宫", "direction": "顺行"}
    ],
    "patterns": ["紫微朝垣格"]
  }
}
```

## 十二宫枚举

| 值 | 名称 | 值 | 名称 |
|----|------|----|------|
| 1 | 命宫 | 7 | 迁移宫 |
| 2 | 兄弟宫 | 8 | 交友宫 |
| 3 | 夫妻宫 | 9 | 官禄宫 |
| 4 | 子女宫 | 10 | 田宅宫 |
| 5 | 财帛宫 | 11 | 福德宫 |
| 6 | 疾厄宫 | 12 | 父母宫 |

## 前端 UI 建议

1. **排盘页**：表单（年月日时选择器 + 性别）+ 提交按钮
2. **命盘图**：12宫圆形排列，命宫在顶部，逆时针排列其余11宫
3. **星曜颜色**：主星(#333)、吉星(#e74c3c)、煞星(#3498db)、四化(绿-禄/紫-权/蓝-科/红-忌)
4. **大限标注**：在宫格外侧显示年龄段
