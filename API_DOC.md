# Claude Web 接口文档

> 最后更新：2026-05-20

---

# 一、SessionApiPostController 接口

**基础路径:** `/api/claude/v2`

---

## 1. list - 获取会话列表

获取用户会话列表。

**端点:** `POST /api/claude/v2/list`

**请求参数:**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| userId | String | 否 | 用户ID |
| appId | String | 否 | 应用ID |

**响应示例:**
```json
{
  "code": 200,
  "data": [
    {
      "appId": "app1",
      "userId": "user1",
      "sessionId": "xxx-xxx",
      "sessionTitle": "会话标题",
      "sessionStatus": "active",
      "createTime": "2026-05-06T10:00:00Z"
    }
  ],
  "message": "success"
}
```

---

## 2. listMysql - MySQL会话列表查询

查询MySQL数据库中的会话记录，支持多条件筛选。

**端点:** `POST /api/claude/v2/listMysql`

**请求参数:**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| appId | String | 否 | 应用ID（精确匹配） |
| userId | String | 否 | 用户ID（精确匹配） |
| userCwd | String | 否 | 用户工作目录（精确匹配） |
| sessionId | String | 否 | 会话ID（精确匹配） |
| sessionTitle | String | 否 | 会话标题（精确匹配） |
| sessionStatus | String | 否 | 会话状态（精确匹配，如active/created） |
| createTimeStart | String | 否 | 创建时间范围起点（ISO格式，如2026-01-01T00:00:00） |
| createTimeEnd | String | 否 | 创建时间范围终点（ISO格式） |

**响应示例:**
```json
{
  "code": 200,
  "data": [
    {
      "appId": "app1",
      "userId": "user1",
      "userCwd": "/home/ubuntu/user1",
      "sessionId": "xxx-xxx",
      "sessionTitle": "会话标题",
      "sessionStatus": "active",
      "createTime": "2026-05-06T10:00:00"
    }
  ],
  "message": "success"
}
```

---

## 3. getLatestSession - 获取用户最新会话

根据用户ID获取该用户最新创建的一条会话记录。

**端点:** `POST /api/claude/v2/getLatestSession`

**请求参数:**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| userId | String | 是 | 用户ID |

**响应示例:**
```json
{
  "code": 200,
  "data": {
    "appId": "app1",
    "userId": "user1",
    "userCwd": "/home/ubuntu/user1",
    "sessionId": "xxx-xxx",
    "sessionTitle": "最新会话",
    "sessionStatus": "active",
    "createTime": "2026-05-06T10:00:00"
  },
  "message": "success"
}
```

**错误响应:**
```json
{
  "code": 404,
  "data": null,
  "message": "No session found for user"
}
```

---

## 4. get - 获取会话详情

获取指定会话的详细信息和消息记录。

**端点:** `POST /api/claude/v2/get`

**请求参数:**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| sessionId | String | 是 | 会话ID |
| userId | String | 否 | 用户ID |

**响应示例:**
```json
{
  "code": 200,
  "data": {
    "thread": {
      "id": "xxx-xxx",
      "status": { "type": "idle" },
      "turns": [
        {
          "id": "turn-1",
          "type": "userMessage",
          "content": [{ "text": "你好", "type": "text" }]
        },
        {
          "id": "turn-2",
          "type": "agentMessage",
          "content": [{ "text": "你好！很高兴见到你。", "type": "text" }]
        }
      ]
    }
  },
  "message": "success"
}
```

---

## 5. create - 创建会话

创建新的会话。

**端点:** `POST /api/claude/v2/create`

**请求参数:**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| userId | String | 是 | 用户ID |
| appId | String | 是 | 应用ID |
| sessionTitle | String | 否 | 会话标题 |

**响应示例:**
```json
{
  "code": 200,
  "data": {
    "codexId": "新生成的UUID"
  },
  "message": "session created"
}
```

**说明:**
- 生成新的UUID作为sessionId
- userCwd自动设置为 `/home/ubuntu/{userId}`
- sessionStatus默认为 "created"
- sessionTitle默认为空字符串

---

## 6. sendMessage - 发送消息

发送消息并等待响应（非流式）。

**端点:** `POST /api/claude/v2/sendMessage`

**请求参数:**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| sessionId | String | 是 | 会话ID |
| userId | String | 否 | 用户ID |
| text | String | 是 | 消息内容 |
| permissionMode | String | 否 | 权限模式（acceptAll/denyAll/default） |
| maxTurns | Integer | 否 | 最大轮次 |
| allowedTools | String | 否 | 允许的工具列表（逗号分隔） |
| disallowedTools | String | 否 | 禁止的工具列表（逗号分隔） |

**响应示例:**
```json
{
  "code": 200,
  "data": {
    "ok": true,
    "requestId": 1714972800000
  },
  "message": "message sent"
}
```

**副作用:**
- 发送成功后更新数据库中对应session的sessionStatus为 "active"
- 同时将sessionTitle设置为消息内容的前12个字符

---

## 7. sendMessageStream - 发送消息（流式/SSE）

发送消息并通过Server-Sent Events流式接收响应。

**端点:** `POST /api/claude/v2/sendMessageStream`

**请求参数:**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| sessionId | String | 是 | 会话ID |
| userId | String | 否 | 用户ID |
| text | String | 是 | 消息内容 |

**SSE事件格式:**

| 事件类型 | 说明 |
|----------|------|
| ready | 连接就绪，包含sessionId |
| turn/started | 回合开始 |
| stream_delta | 流式内容输出 |
| complete | 完成 |
| error | 错误 |
| ping | 心跳 |

**示例:**
```
data: {"method":"ready","params":{"sessionId":"xxx-xxx"}}
data: {"method":"stream_delta","params":{"text":"你好"}}
data: {"method":"complete","params":{"sessionId":"xxx-xxx"}}
```

**副作用:**
- 发送成功后更新数据库中对应session的sessionStatus为 "active"
- 同时将sessionTitle设置为消息内容的前12个字符

---

## 8. cancel - 取消会话

取消正在进行的会话。

**端点:** `POST /api/claude/v2/cancel`

**请求参数:**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| sessionId | String | 是 | 会话ID |

**响应示例:**
```json
{
  "code": 200,
  "data": { "sessionId": "xxx-xxx", "status": "cancelled" },
  "message": "cancelled"
}
```

---

## 9. approval - 发送审批

对工具调用权限请求进行审批。

**端点:** `POST /api/claude/v2/approval`

**请求参数:**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| sessionId | String | 是 | 会话ID |
| approvalId | String | 否 | 审批ID（来自权限请求） |
| action | String | 是 | 操作（approve/deny） |

**响应示例:**
```json
{
  "code": 200,
  "data": { "approved": true },
  "message": "approved"
}
```

---

## 10. delete - 删除会话

删除（归档）指定会话。

**端点:** `POST /api/claude/v2/delete`

**请求参数:**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| sessionId | String | 是 | 会话ID |
| userId | String | 否 | 用户ID |

**响应示例:**
```json
{
  "code": 200,
  "data": {
    "sessionId": "xxx-xxx",
    "status": "archived"
  },
  "message": "archived"
}
```

---

## 11. searchFiles - 搜索文件

在会话中搜索文件（当前实现返回空列表）。

**端点:** `POST /api/claude/v2/searchFiles`

**请求参数:**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| sessionId | String | 是 | 会话ID |
| q | String | 否 | 搜索关键词 |
| limit | Integer | 否 | 结果数量限制（默认10） |

**响应示例:**
```json
{
  "code": 200,
  "data": [],
  "message": "success"
}
```

---

## 12. updateTitle - 更新会话标题

更新指定会话的标题。

**端点:** `POST /api/claude/v2/updateTitle`

**请求参数:**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| sessionId | String | 是 | 会话ID |
| title | String | 是 | 新标题 |

**响应示例:**
```json
{
  "code": 200,
  "data": {
    "sessionId": "xxx-xxx",
    "title": "新标题"
  },
  "message": "title updated"
}
```

**副作用:**
- 同时更新MySQL数据库中对应session的sessionTitle

---

# 二、AgentController 接口

**基础路径:** `/api/agent`

运行时管理 Claude Agent 连接，支持查询状态和热切换目标 Agent。

---

## 1. status - 查询连接状态

获取当前连接的 Claude Agent 地址及连接状态。

**端点:** `GET /api/agent/status`

**请求参数:** 无

**响应示例:**
```json
{
  "host": "127.0.0.1",
  "port": 3000,
  "connected": true,
  "apiKeySet": true
}
```

| 字段 | 类型 | 说明 |
|------|------|------|
| host | String | 当前连接的 Agent 主机地址 |
| port | Integer | 当前连接的 Agent 端口 |
| connected | Boolean | 是否已成功连接 |
| apiKeySet | Boolean | 是否已配置 API Key |

---

## 2. switch-default - 切换回默认 Agent

将连接切换回配置文件中定义的默认 Claude Agent。

**端点:** `POST /api/agent/switch-default`

**请求参数:** 无

**响应示例（成功）:**
```json
{
  "ok": true,
  "agent": "default"
}
```

**响应示例（失败，HTTP 503）:**
```json
{
  "error": "未能连接到默认Agent: ..."
}
```

---

## 3. switch - 切换到指定 Agent

切换到指定地址的 Claude Agent，连接失败时立即返回错误（不重试）。

**端点:** `POST /api/agent/switch`

**请求参数（JSON Body）:**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| host | String | 是 | 目标 Agent 主机地址（不能为空） |
| port | Integer | 是 | 目标 Agent 端口（1–65535） |
| apiKey | String | 否 | 目标 Agent 的 API Key |

**响应示例（成功）:**
```json
{
  "ok": true,
  "host": "192.168.1.100",
  "port": 3000
}
```

**响应示例（参数校验失败，HTTP 400）:**
```json
{
  "error": "host 不能为空"
}
```

**响应示例（连接失败，HTTP 503）:**
```json
{
  "error": "连接失败: ..."
}
```

**说明:**
- 与 `switch-default` 不同，此接口使用一次性连接（`connectOnce`），连接失败立即抛出异常并返回 503，不会进行重连重试。

---

## 公共响应格式

**成功:**
```json
{
  "code": 200,
  "data": { ... },
  "message": "success"
}
```

**错误:**
```json
{
  "code": 400,
  "data": null,
  "message": "错误描述"
}
```

---

## 数据库表结构

### t_ai_sessions

| 字段 | 类型 | 说明 |
|------|------|------|
| app_id | VARCHAR(20) | 应用ID |
| user_id | VARCHAR(20) | 用户ID |
| user_cwd | VARCHAR(255) | 用户工作目录 |
| session_id | VARCHAR(100) | 会话ID（主键） |
| session_title | VARCHAR(255) | 会话标题 |
| session_status | VARCHAR(20) | 会话状态 |
| create_time | DATETIME | 创建时间 |

### t_ai_apps

| 字段 | 类型 | 说明 |
|------|------|------|
| app_id | VARCHAR(50) | 应用ID（主键） |
| access_taken | VARCHAR(100) | 认证密钥 |
| serve_type | VARCHAR(20) | 智能体类型（codex/claude） |
| serve_ip | VARCHAR(100) | 智能体服务地址 |
| serve_port | VARCHAR(10) | 智能体服务端口 |