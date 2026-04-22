# Claude Web

Claude Agent 的 Web 界面 Spring Boot 实现，通过 WebSocket 连接 Claude Agent 后端，提供 Web UI 访问。

## 概览

本项目是原 Node.js 项目的 Java/Spring Boot 移植版本，提供以下功能：

- 支持可选密码保护的 HTTP 服务器
- 通过 WebSocket 连接 Claude Agent 后端
- 服务器推送事件（SSE）实时通知
- 静态文件服务，支持 SPA 回退
- **自动重连** - WebSocket 连接断开时自动重新连接

## 环境要求

- Java 17 或更高版本
- Maven 3.6+
- 已运行的 Claude Agent 后端（例如 `claude-agent` Node.js 服务）

## 项目结构

```
code/
├── claude-web/                      # 本项目
│   ├── src/
│   ├── target/
│   ├── start.sh
│   ├── stop.sh
│   ├── restart.sh
│   └── pom.xml
├── .jdk/                            # JDK 17（相对路径）
│   └── jdk-17/
└── .maven/                          # Maven 3.9.14（相对路径）
    └── apache-maven-3.9.14/
```

## 构建

```bash
cd claude-web
../.maven/apache-maven-3.9.14/bin/mvn clean package
```

或者如果 Maven 已在 PATH 中：

```bash
cd claude-web
mvn clean package
```

## 运行

### 快速启动

服务器配置已预设于 `start.sh` 中，直接运行：

```bash
./start.sh
```

服务将启动在 http://localhost:3000

### 停止服务

```bash
./stop.sh
```

### 重启服务

```bash
./restart.sh
```

### 自定义服务器配置

编辑 `start.sh` 并修改配置段落：

```bash
# ============================================
# Claude Agent 配置
# ============================================
export CLAUDE_AGENT_HOST="127.0.0.1"
export CLAUDE_AGENT_PORT=8011
# API 密钥默认从 /home/sunsw/.codex/jwt_secret.key 读取
export CLAUDE_AGENT_API_KEY=""

# Web UI 配置
export CLAUDE_PASSWORD_ENABLED=false
export PORT=3000
# ============================================
```

**密钥文件说明：**

`start.sh` 默认从 Claude Code 的 JWT 密钥文件读取 API Key：

```bash
export CLAUDE_AGENT_API_KEY="${CLAUDE_AGENT_API_KEY:-$(cat /home/sunsw/.codex/jwt_secret.key)}"
```

密钥文件路径：`/home/sunsw/.codex/jwt_secret.key`

该文件由 Claude Code 生成，claude-agent 和 claude-web 共享此密钥进行 WebSocket 握手认证。如果文件不存在，需手动设置 `CLAUDE_AGENT_API_KEY` 环境变量。

### 手动启动（不使用脚本）

**使用环境变量：**

```bash
# 设置 Java 和 Maven 路径（相对路径）
export JAVA_HOME="../.jdk/jdk-17"
export PATH="$JAVA_HOME/bin:../.maven/apache-maven-3.9.14/bin:$PATH"

# 设置 Claude Agent 配置
export CLAUDE_AGENT_HOST=127.0.0.1
export CLAUDE_AGENT_PORT=8011
export CLAUDE_AGENT_API_KEY=""
export CLAUDE_PASSWORD_ENABLED=false
export PORT=3000

java -jar target/claude-web-0.1.0.jar
```

**使用命令行参数：**

```bash
java -jar target/claude-web-0.1.0.jar \
  --claude.web.claude-agent.host=127.0.0.1 \
  --claude.web.claude-agent.port=8011 \
  --claude.web.password-enabled=false \
  --server.port=3000
```

### 自定义端口

```bash
# 编辑 start.sh 并修改：
export PORT=8080

# 或使用命令行参数：
java -jar target/claude-web-0.1.0.jar --server.port=8080
```

## 配置

### 通用设置

| 属性 | 环境变量 | 默认值 | 说明 |
|------|---------|--------|------|
| `server.port` | `PORT` | `3000` | HTTP 服务器端口 |
| `claude.web.password` | `CLAUDE_PASSWORD` | (自动生成) | 认证密码 |
| `claude.web.password-enabled` | `CLAUDE_PASSWORD_ENABLED` | `false` | 启用密码保护 |
| `claude.web.auto-approve` | `CLAUDE_AUTO_APPROVE` | `false` | 自动批准权限请求 |

### Claude Agent 设置

| 属性 | 环境变量 | 默认值 | 说明 |
|------|---------|--------|------|
| `claude.web.claude-agent.host` | `CLAUDE_AGENT_HOST` | `127.0.0.1` | Claude Agent 主机 |
| `claude.web.claude-agent.port` | `CLAUDE_AGENT_PORT` | `8011` | Claude Agent 端口 |
| `claude.web.claude-agent.api-key` | `CLAUDE_AGENT_API_KEY` | - | 认证 API 密钥 |
| `claude.web.claude-agent.connection-timeout` | `CLAUDE_AGENT_CONNECTION_TIMEOUT` | `30000` | 连接超时（毫秒） |

### 连接设置

| 设置 | 值 | 说明 |
|------|-----|------|
| 心跳间隔 | 30 秒 | WebSocket 保活间隔 |
| 最大重连次数 | 5 | 最大重连尝试次数 |
| 重连延迟 | 3 秒 | 每次重连之间的等待时间 |

## API 接口

### 会话管理（前端 API）

- `GET /api/claude` - 列出所有会话
- `POST /api/claude` - 创建新会话
- `GET /api/claude/{id}` - 获取会话详情
- `POST /api/claude/{id}/message` - 向会话发送消息
- `POST /api/claude/{id}/cancel` - 取消当前操作
- `POST /api/claude/{id}/approval` - 发送批准/拒绝
- `DELETE /api/claude/{id}` - 停止/关闭会话
- `GET /api/claude/{id}/files?q={query}` - 搜索文件

### 内部 API

- `POST /claude-api/rpc` - JSON-RPC 代理（不可用，返回 403）
- `GET /claude-api/events` - SSE 通知流
- `GET /claude-api/server-requests/pending` - 列出待处理的服务器请求
- `POST /claude-api/server-requests/respond` - 响应服务器请求
- `GET /claude-api/meta/methods` - 列出可用方法
- `GET /claude-api/meta/notifications` - 列出可用通知方法

### 认证

- `POST /auth/login` - 使用密码登录（返回 Cookie）

### 静态文件

- `/*` - 静态文件，支持 SPA 回退

## 连接事件

服务会在连接状态变化时发送 SSE 通知：

- `connection/lost` - WebSocket 连接断开，正在尝试重连
- `connection/restored` - WebSocket 连接成功恢复
- `connection/failed` - 达到最大重连次数后仍失败

## 前端集成

后端运行时期望在 `dist/` 目录中存在构建好的前端。如需配合原始 Vue.js 前端使用：

1. 从原项目构建前端
2. 将 `dist/` 文件夹复制到 `claude-web/dist/`
3. 运行 Spring Boot 应用

## 架构

### 组件

1. **AuthFilter** - 处理基于 Cookie 会话的密码认证
2. **AppServerProcess** - 管理 WebSocket 连接，支持自动重连
3. **SessionApiController** - 提供前端兼容的会话管理 API
4. **SseEventService** - 管理服务器推送事件流
5. **ClaudeApiController** - 内部 REST API 端点
6. **SseController** - SSE 端点
7. **SpaController** - 静态文件服务，支持 SPA 回退

### 数据流

```
浏览器 -> AuthFilter -> SessionApiController -> AppServerProcess -> WebSocket -> Claude Agent
                |                                    |
         SPA Controller <- SSE <- 通知
```

### 连接管理

```
WebSocket 连接
     |
保活心跳（每 30 秒）
     |
[连接断开？] -> 是 -> 自动重连（最多 5 次）
     | 否
继续处理
```

## 故障排查

### WebSocket 连接失败

查看日志文件 `/tmp/claude-web.log` 获取详细错误信息：

```bash
tail -f /tmp/claude-web.log
```

常见问题：
- **连接被拒绝**：检查 Claude Agent 是否在配置的地址/端口上运行
- **认证失败**：检查 API 密钥配置

### 左侧边栏无会话列表

1. 检查 WebSocket 连接是否成功（查看日志）
2. 确认 Claude Agent 正在运行且存在会话
3. 直接测试 API：`curl http://localhost:3000/api/claude`

### 连接反复断开

1. 检查客户端与服务器之间的网络稳定性
2. 确认 Claude Agent 运行稳定
3. 检查自动重连设置

## 许可证

MIT
