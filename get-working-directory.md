---
name: get-working-directory
description: 在 agent 需要为用户生成文档或文件时调用。通过当前会话 sessionId 向本地服务查询目标工作目录（cwd），并将该目录作为所有文件写入操作的根路径。
---

# Get Working Directory

当 agent 收到生成文档或文件的请求时，**必须首先执行本 skill**，以确定文件应写入的正确工作目录，而非默认使用当前进程的工作目录。

## 触发时机

满足以下任意条件时调用本 skill：

- 用户要求生成、创建、导出或保存文档（Markdown、PDF、Word、TXT 等）
- 用户要求创建或写入代码文件、配置文件、脚本等
- 用户要求将输出内容保存到文件系统
- 任何涉及 `Write`、`Edit` 工具写入文件的场景

## 执行步骤

### Step 1：获取当前会话的 sessionId

通过进程树向上查找 Claude Code 主进程，读取其 session 文件获得 sessionId。

```bash
# 向上遍历进程树，找到在 ~/.claude/sessions/ 中存在对应记录的 PID
_find_session_id() {
  local pid=$$
  while [ "$pid" -gt 1 ]; do
    local session_file="$HOME/.claude/sessions/${pid}.json"
    if [ -f "$session_file" ]; then
      python3 -c "import json,sys; d=json.load(open('$session_file')); print(d['sessionId'])"
      return 0
    fi
    pid=$(ps -o ppid= -p "$pid" 2>/dev/null | tr -d ' ')
    [ -z "$pid" ] && break
  done
  return 1
}

SESSION_ID=$(_find_session_id)
echo "sessionId: $SESSION_ID"
```

> **注意**：如果上述方法未找到 sessionId，可直接列出 `~/.claude/sessions/` 下所有文件，选取 `status` 为 `"busy"` 的记录：
> ```bash
> python3 -c "
> import json, os, glob
> files = glob.glob(os.path.expanduser('~/.claude/sessions/*.json'))
> for f in files:
>     try:
>         d = json.load(open(f))
>         if d.get('status') == 'busy':
>             print(d['sessionId'])
>     except: pass
> "
> ```

### Step 2：获取宿主机 IP 地址

agent 运行于服务器的 Docker 容器中，使用以下命令获取公网ip并存储于变量HOST_IP：

```bash
curl -s --max-time 5 ip.sb 2>/dev/null || curl -s --max-time 5 icanhazip.com 2>/dev/null
```

### Step 3：向本地服务查询工作目录

使用 `curl` 向 `http://${HOST_IP}:8080/api/claude/v2/get` 发送 POST 请求，携带 sessionId。

```bash
RESPONSE=$(curl -s -X POST "http://${HOST_IP}:8080/api/claude/v2/get" \
  -H "Content-Type: application/json" \
  -d "{\"sessionId\": \"${SESSION_ID}\"}")

echo "Response: $RESPONSE"
```

### Step 4：提取 cwd 字段

从响应 JSON 中取出 `cwd` 字段的值。

```bash
CWD=$(echo "$RESPONSE" | python3 -c "import json,sys; print(json.load(sys.stdin)['cwd'])")
echo "Working directory: $CWD"
```

### Step 5：使用 cwd 作为文件写入根路径

将 `$CWD` 作为所有后续文件操作的根目录：

- 使用 `Write` 工具时，路径前缀使用 `$CWD`，例如 `$CWD/output.md`
- 使用 `Edit` 工具时，确认目标文件路径在 `$CWD` 下
- 使用 `Bash` 工具执行文件操作时，以 `$CWD` 为基准路径

## 错误处理

| 情形 | 处理方式 |
|------|----------|
| 未找到 sessionId | 告知用户无法确认会话信息，询问是否手动指定目标目录 |
| 无法获取宿主机 IP | 告知用户无法从默认网关推断宿主机地址，询问是否手动指定 IP |
| curl 请求失败（连接拒绝、超时） | 告知用户本地服务（{HOST_IP}:8080）不可达，询问是否使用当前默认目录 |
| 响应中无 `cwd` 字段 | 输出原始响应内容，告知用户并询问是否手动指定目标目录 |
| `cwd` 路径不存在 | 告知用户该路径不存在，询问是否自动创建或指定其他路径 |

## 完整执行示例

```bash
# ---- Step 1: 获取 sessionId ----
_find_session_id() {
  local pid=$$
  while [ "$pid" -gt 1 ]; do
    local session_file="$HOME/.claude/sessions/${pid}.json"
    if [ -f "$session_file" ]; then
      python3 -c "import json; print(json.load(open('$session_file'))['sessionId'])"
      return 0
    fi
    pid=$(ps -o ppid= -p "$pid" 2>/dev/null | tr -d ' ')
    [ -z "$pid" ] && break
  done
  return 1
}

SESSION_ID=$(_find_session_id)

if [ -z "$SESSION_ID" ]; then
  echo "ERROR: Could not determine sessionId"
  exit 1
fi

# ---- Step 2: 获取宿主机 IP ----
HOST_IP=$(ip route show default | awk '/default/ {print $3}' | head -1)

if [ -z "$HOST_IP" ]; then
  echo "ERROR: Could not determine host IP from default gateway"
  exit 1
fi

# ---- Step 3: 查询工作目录 ----
RESPONSE=$(curl -s -X POST "http://${HOST_IP}:8080/api/claude/v2/get" \
  -H "Content-Type: application/json" \
  -d "{\"sessionId\": \"${SESSION_ID}\"}")

# ---- Step 4: 提取 cwd ----
CWD=$(echo "$RESPONSE" | python3 -c "import json,sys; print(json.load(sys.stdin)['cwd'])" 2>/dev/null)

if [ -z "$CWD" ]; then
  echo "ERROR: Could not extract cwd from response: $RESPONSE"
  exit 1
fi

echo "Target working directory: $CWD"

# ---- Step 5: 后续文件操作以 $CWD 为根路径 ----
# 示例：写入文件
# Write tool path → "${CWD}/document.md"
```

## 重要约束

- **本 skill 不替代用户的路径指定**：若用户在请求中已明确指定了绝对路径，优先使用用户指定路径，无需调用本 skill。
- **只读查询**：本 skill 仅查询工作目录，不做任何文件写入操作。
- **幂等性**：在同一次用户请求中，仅需执行一次本 skill，结果可复用于该请求内的所有文件操作。
