# 紫微斗数 - 后台管理 API 文档

> 供前端开发者参考。本文档仅覆盖后台管理端接口，用户端接口见 `api-reference.md`。

## 基础信息

| 项目 | 说明 |
|---|---|
| **Base URL** | `http://{host}:48080/admin-api` |
| **统一响应** | `CommonResult<T>` = `{ code: 0, msg: "success", data: ... }` |
| **Content-Type** | `application/json`（上传接口用 `multipart/form-data`） |
| **分页响应** | `PageResult<T>` = `{ code: 0, data: { list: [...], total: 100 } }` |

---

## 一、RAG 知识库管理（古籍上传/检索）

> **Base**: `/admin-api/ziwei/rag`

### 1.1 上传古籍 PDF

```
POST /admin-api/ziwei/rag/upload
Content-Type: multipart/form-data
```

| 参数 | 类型 | 必填 | 说明 |
|---|---|---|---|
| `file` | File | ✅ | PDF 文件（支持 pdf/txt/md/doc/docx） |
| `bookTitle` | String | ✅ | 书名，如《紫微斗数全集》 |
| `bookAuthor` | String | ❌ | 作者/来源，如"陈抟" |

上传后自动完成：PDF 解析 → 古文智能切分 → 向量化 → 存入 Milvus。

**响应**:

```json
{
  "code": 0,
  "data": {
    "documentId": 1,
    "fileName": "紫微斗数全集.pdf",
    "bookTitle": "紫微斗数全集"
  }
}
```

### 1.2 分页查询文档列表

```
GET /admin-api/ziwei/rag/page
```

| 参数 | 类型 | 必填 | 默认值 | 说明 |
|---|---|---|---|---|
| `pageNo` | int | ❌ | 1 | 页码 |
| `pageSize` | int | ❌ | 10 | 每页条数 |
| `bookTitle` | String | ❌ | — | 书名模糊搜索 |

**响应**:

```json
{
  "code": 0,
  "data": {
    "list": [
      {
        "id": 1,
        "bookTitle": "紫微斗数全集",
        "bookAuthor": "陈抟",
        "fileName": "紫微斗数全集.pdf",
        "fileUrl": "http://127.0.0.1:9000/ziwei-books/ziwei-books/20260710/a1b2c3d4_紫微斗数全集.pdf",
        "fileSize": 15234567,
        "segmentCount": 342,
        "status": 1,
        "createTime": "2026-07-10T18:00:00"
      }
    ],
    "total": 5
  }
}
```

**文档对象字段**:

| 字段 | 类型 | 说明 |
|---|---|---|
| `id` | Long | 文档 ID |
| `bookTitle` | String | 书名 |
| `bookAuthor` | String | 作者/来源 |
| `fileName` | String | 原始文件名 |
| `fileUrl` | String | MinIO 文件 URL |
| `fileSize` | Long | 文件大小（bytes） |
| `segmentCount` | Integer | 切分段数 |
| `status` | Integer | 状态：0-禁用，1-启用 |
| `createTime` | DateTime | 上传时间 |

### 1.3 检索古籍知识

```
GET /admin-api/ziwei/rag/search
```

| 参数 | 类型 | 必填 | 默认值 | 说明 |
|---|---|---|---|---|
| `query` | String | ✅ | — | 检索关键词/问题 |
| `topK` | int | ❌ | 5 | 返回结果数量 |

**响应**:

```json
{
  "code": 0,
  "data": [
    {
      "content": "紫微星属土，乃中天之星，为北斗主星，化气为尊贵...",
      "bookTitle": "紫微斗数全集",
      "chapter": "卷之一",
      "score": 0.923
    },
    {
      "content": "紫微入命宫，主其人相貌厚重，气质高雅...",
      "bookTitle": "天纪",
      "chapter": "",
      "score": 0.876
    }
  ]
}
```

| 字段 | 类型 | 说明 |
|---|---|---|
| `content` | String | 古籍原文段落 |
| `bookTitle` | String | 出处书名 |
| `chapter` | String | 章节（可能为空） |
| `score` | double | 相似度分数（0~1，越高越相关） |

### 1.4 删除古籍文档

```
DELETE /admin-api/ziwei/rag/document/{id}
```

| 参数 | 类型 | 必填 | 说明 |
|---|---|---|---|
| `id` | Long | ✅ | 文档 ID（路径参数） |

同时删除：MinIO 文件 + Milvus 向量 + 数据库记录。

**响应**: `{ "code": 0, "data": true }`

---

## 二、AI 命盘解读（RAG 增强）

> **Base**: `/admin-api/ziwei/ai`

所有解读接口自动启用 RAG：先检索古籍原文，再结合命盘数据由 AI 分析，引用古籍为依据。

### 2.1 整体命盘解读

```
POST /admin-api/ziwei/ai/interpret/{chartId}
```

| 参数 | 类型 | 必填 | 说明 |
|---|---|---|---|
| `chartId` | Long | ✅ | 命盘 ID（路径参数） |
| `question` | String | ❌ | 自定义问题，不传则全面解读 |

**响应**: `{ "code": 0, "data": "解读文本..." }`

解读维度：整体特点 → 命宫星曜 → 财帛/官禄/夫妻 → 四化 → 大限走势 → 格局分析 → 综合建议。

### 2.2 流式解读（SSE）

```
POST /admin-api/ziwei/ai/interpret/{chartId}/stream
Accept: text/event-stream
```

参数同上。Server-Sent Events 逐字流式输出。

**SSE 事件格式**:
```
data: 解读文字片段...

data: 解读文字片段...
```

前端示例（JavaScript）:
```js
const eventSource = new EventSource('/admin-api/ziwei/ai/interpret/1/stream');
eventSource.onmessage = (event) => {
  console.log(event.data); // 追加到文本框
};
eventSource.onerror = () => eventSource.close();
```

### 2.3 宫位解读

```
POST /admin-api/ziwei/ai/interpret/{chartId}/palace/{palaceType}
```

| 参数 | 类型 | 必填 | 说明 |
|---|---|---|---|
| `chartId` | Long | ✅ | 命盘 ID（路径参数） |
| `palaceType` | int | ✅ | 宫位类型（路径参数，见下表） |
| `question` | String | ❌ | 自定义问题 |

**宫位类型 palaceType**:

| 值 | 宫位 | 值 | 宫位 |
|---|---|---|---|
| 1 | 命宫 | 7 | 迁移宫 |
| 2 | 兄弟宫 | 8 | 交友宫（仆役宫） |
| 3 | 夫妻宫 | 9 | 官禄宫（事业宫） |
| 4 | 子女宫 | 10 | 田宅宫 |
| 5 | 财帛宫 | 11 | 福德宫 |
| 6 | 疾厄宫 | 12 | 父母宫 |

**响应**: `{ "code": 0, "data": "宫位解读文本..." }` — 包含主星/辅星/四化/对宫综合分析。

---

## 三、命盘管理

> **Base**: `/admin-api/ziwei/chart`

### 3.1 录入出生信息并排盘

```
POST /admin-api/ziwei/chart/create
Content-Type: application/json
```

**请求体**:

```json
{
  "userId": 1,
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

| 字段 | 类型 | 必填 | 校验 | 说明 |
|---|---|---|---|---|
| `userId` | Long | ❌ | — | 用户 ID |
| `solarYear` | Integer | ✅ | — | 公历出生年 |
| `solarMonth` | Integer | ✅ | 1~12 | 公历月 |
| `solarDay` | Integer | ✅ | 1~31 | 公历日 |
| `solarHour` | Integer | ✅ | 0~23 | 公历时 |
| `solarMinute` | Integer | ❌ | 0~59 | 公历分 |
| `gender` | Integer | ✅ | 0/1 | 0=女，1=男 |
| `birthPlace` | String | ❌ | — | 出生地点 |
| `isDst` | Boolean | ❌ | — | 是否夏令时 |

**响应**: `ZiweiChartRespVO` — 完整命盘数据，结构见附录。

### 3.2 分页查询命盘

```
GET /admin-api/ziwei/chart/page
```

| 参数 | 类型 | 必填 | 说明 |
|---|---|---|---|
| `pageNo` | int | ❌ | 页码，默认 1 |
| `pageSize` | int | ❌ | 每页条数，默认 10 |
| `userId` | Long | ❌ | 按用户筛选 |
| `gender` | Integer | ❌ | 按性别筛选 |

**响应**: `PageResult<ZiweiChartRespVO>`

### 3.3 获取命盘详情

```
GET /admin-api/ziwei/chart/get?id=1
```

| 参数 | 类型 | 必填 | 说明 |
|---|---|---|---|
| `id` | Long | ✅ | 命盘 ID |

### 3.4 删除命盘

```
DELETE /admin-api/ziwei/chart/delete?id=1
```

| 参数 | 类型 | 必填 | 说明 |
|---|---|---|---|
| `id` | Long | ✅ | 命盘 ID |

---

## 附录 A：ZiweiChartRespVO 数据结构

```json
{
  "id": 1,
  "solarYear": 1990, "solarMonth": 6, "solarDay": 15,
  "solarHour": 14, "solarMinute": 30,
  "gender": 1, "birthPlace": "北京",
  "yearPillar": "庚午", "monthPillar": "壬午",
  "dayPillar": "甲子", "hourPillar": "辛未",
  "mingGong": "寅", "shenGong": "戌",
  "wuxingJu": "金四局",
  "palaces": [
    {
      "palaceType": 1,
      "palaceName": "命宫",
      "dizhi": "寅",
      "tianGan": "甲",
      "isShenGong": false,
      "daXianLabel": "4-13",
      "majorStars": [
        { "starCode": "ziwei", "starName": "紫微", "brightness": "庙", "sihuaType": null }
      ],
      "auxiliaryStars": [
        { "starCode": "zuofu", "starName": "左辅", "brightness": null, "sihuaType": null }
      ],
      "minorStars": [
        { "starCode": "hongluan", "starName": "红鸾", "brightness": null, "sihuaType": null }
      ]
    }
  ],
  "natalSihua": {
    "huaLu": { "starCode": "tianfu", "starName": "天府", "brightness": null, "sihuaType": null },
    "huaQuan": { "starCode": "tianliang", "starName": "天梁", "brightness": null, "sihuaType": null },
    "huaKe": { "starCode": "taiyin", "starName": "太阴", "brightness": null, "sihuaType": null },
    "huaJi": { "starCode": "jumen", "starName": "巨门", "brightness": null, "sihuaType": null }
  },
  "daxians": [
    { "ageStart": 4, "ageEnd": 13, "palaceName": "命宫", "direction": "顺行" }
  ],
  "patterns": ["君臣庆会", "紫微入命"],
  "patternDetails": [
    {
      "name": "君臣庆会",
      "level": "上格",
      "description": "紫微与左辅右弼同宫或会照，主贵气，有领导才能。",
      "source": "紫微斗数全书",
      "required": ["紫微坐命", "左辅或右弼同宫/会照"],
      "bonus": ["文昌文曲加会"],
      "breaking": ["煞星冲破"]
    }
  ],
  "createTime": "2026-07-10T18:00:00"
}
```

### 内嵌类型

**StarVO**（星曜）:

| 字段 | 类型 | 说明 |
|---|---|---|
| `starCode` | String | 星曜编码 |
| `starName` | String | 星曜中文名 |
| `brightness` | String | 庙/旺/得/利/平/不/陷，可 null |
| `sihuaType` | String | 化禄/化权/化科/化忌，可 null |

**PalaceVO**（宫位）:

| 字段 | 类型 | 说明 |
|---|---|---|
| `palaceType` | Integer | 宫位类型 1-12 |
| `palaceName` | String | 宫位中文名 |
| `dizhi` | String | 地支 |
| `tianGan` | String | 宫干 |
| `isShenGong` | Boolean | 是否为身宫 |
| `daXianLabel` | String | 大限年龄段，如"4-13" |
| `majorStars` | StarVO[] | 主星列表 |
| `auxiliaryStars` | StarVO[] | 辅星列表 |
| `minorStars` | StarVO[] | 杂曜列表 |

**NatalSihuaVO**（本命四化）:

| 字段 | 类型 | 说明 |
|---|---|---|
| `huaLu` | StarVO | 化禄星 |
| `huaQuan` | StarVO | 化权星 |
| `huaKe` | StarVO | 化科星 |
| `huaJi` | StarVO | 化忌星 |

**DaxianVO**（大限）:

| 字段 | 类型 | 说明 |
|---|---|---|
| `ageStart` | Integer | 起始虚岁 |
| `ageEnd` | Integer | 结束虚岁 |
| `palaceName` | String | 对应宫位名 |
| `direction` | String | 顺行/逆行 |

**PatternDetailVO**（格局详情）:

| 字段 | 类型 | 说明 |
|---|---|---|
| `name` | String | 格局名称 |
| `level` | String | 上格/中格/平格/恶格 |
| `description` | String | 格局描述 |
| `source` | String | 古籍出处 |
| `required` | String[] | 必须满足的条件 |
| `bonus` | String[] | 加分条件 |
| `breaking` | String[] | 破格条件 |

---

## 附录 B：接口速查表

| 方法 | 路径 | 说明 |
|---|---|---|
| `POST` | `/admin-api/ziwei/rag/upload` | 上传古籍 PDF |
| `GET` | `/admin-api/ziwei/rag/page` | 文档分页列表 |
| `GET` | `/admin-api/ziwei/rag/search` | 检索古籍知识 |
| `DELETE` | `/admin-api/ziwei/rag/document/{id}` | 删除古籍文档 |
| `POST` | `/admin-api/ziwei/ai/interpret/{chartId}` | AI 整体解读（RAG 增强） |
| `POST` | `/admin-api/ziwei/ai/interpret/{chartId}/stream` | AI 流式解读（SSE） |
| `POST` | `/admin-api/ziwei/ai/interpret/{chartId}/palace/{palaceType}` | AI 宫位解读 |
| `POST` | `/admin-api/ziwei/chart/create` | 录入出生信息并排盘 |
| `GET` | `/admin-api/ziwei/chart/page` | 命盘分页查询 |
| `GET` | `/admin-api/ziwei/chart/get` | 命盘详情 |
| `DELETE` | `/admin-api/ziwei/chart/delete` | 删除命盘 |
