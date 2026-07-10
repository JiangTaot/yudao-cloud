# Claude Code 常用命令速查表

> 适用版本：Claude Code CLI（2026年7月）

---

## 一、会话管理

| 命令 | 说明 |
|---|---|
| `/clear` | 清空当前会话上下文（开启新对话） |
| `/compact` | 压缩上下文，将对话摘要后释放 token 空间 |
| `/context` | 查看当前上下文占用情况 |
| `/cost` | 查看当前会话的 token 消耗和费用 |
| `/doctor` | 诊断 Claude Code 环境问题 |
| `/status` | 查看 Claude Code 当前状态 |
| `/logout` | 退出登录 |
| `/login` | 重新登录 |

---

## 二、项目与工作区

| 命令 | 说明 |
|---|---|
| `/init` | 初始化项目，自动生成 `CLAUDE.md` 项目指导文件 |
| `/init memory` | 初始化项目级记忆文件（`.claude/memory/`） |
| `/add-dir <路径>` | 添加额外的工作目录 |
| `/worktree` | 创建/进入隔离的 git worktree 分支工作区 |
| `/ide` | 管理 IDE 集成 |

---

## 三、编码与实现

| 命令 | 说明 |
|---|---|
| `/plan` | 进入计划模式：先设计实现方案，用户审批后再写代码 |
| `/review` | 对当前分支的变更进行代码审查（Review） |
| `/security-review` | 对当前变更进行安全审查 |
| `/simplify` | 审查并简化代码（消除重复、提升可读性） |
| `/test` | 运行测试 |
| `/run` | 启动并运行应用，验证改动效果 |
| `/fix` | 修复编译错误或测试失败 |
| `/verify` | 验证某个改动是否按预期工作（启动应用实际测试） |

---

## 四、Git 相关

| 命令 | 说明 |
|---|---|
| `/commit` | 自动生成 commit message 并提交 |
| `/pr` | 创建 Pull Request |
| `/branch <分支名>` | 创建新分支 |
| `/switch <分支名>` | 切换到已有分支 |
| `/diff` | 查看当前改动差异 |
| `/log` | 查看 git 提交历史 |

---

## 五、配置与自定义

| 命令 | 说明 |
|---|---|
| `/config` | 打开/修改 Claude Code 配置文件（`settings.json`） |
| `/config theme` | 切换终端主题 |
| `/config model` | 切换默认 AI 模型 |
| `/permissions` | 管理工具权限（允许/禁止特定命令） |
| `/hooks` | 管理钩子（Hook），在特定事件前后自动执行脚本 |
| `/keybindings` | 自定义快捷键 |
| `/add-mcp` | 添加 MCP 服务器 |

---

## 六、记忆与知识

| 命令 | 说明 |
|---|---|
| `/remember` | 让 Claude 记住某个信息，写入记忆文件持久保存 |
| `/memory` | 查看已保存的记忆列表 |
| `/forget <名称>` | 删除某条记忆 |

---

## 七、高级功能

| 命令 | 说明 |
|---|---|
| `/workflows` | 查看多 Agent 协作工作流执行进度 |
| `/tasks` | 查看和管理后台任务 |
| `/loop [间隔] <指令>` | 定时循环执行某个指令（如 `/loop 5m /review`） |
| `/resume` | 恢复之前中断的会话 |
| `/export` | 导出当前会话记录 |
| `/bashes` | 查看正在运行的 shell 后台任务 |
| `/agents` | 查看正在运行的子 Agent 任务 |
| `/terminal-setup` | 配置终端集成（Shift+Enter 换行等） |
| `/setup` | 设置 Claude Code 运行环境 |
| `/mcp` | 管理 MCP 服务器 |
| `/mcp list` | 列出已连接的 MCP 服务器 |

---

## 八、常用交互技巧

### 快捷操作

| 操作 | 快捷键 / 方式 |
|---|---|
| 提交消息 | `Ctrl+S` 或 `Enter` |
| 换行 | `Shift+Enter`（可配置） |
| 中断执行 | `Ctrl+C` |
| 退出 Claude Code | `/exit` 或 `Ctrl+D` |

### 使用感叹号前缀

在聊天中输入 `! <shell命令>` 可以让该命令在宿主的 shell 中直接运行，结果回显到对话中。例如：

```
! git log --oneline -5
! npm run build
! cat /etc/hosts
```

### 文件引用

在对话中直接粘贴文件路径，Claude 会自动读取。支持：
- 绝对路径：`D:\workspace\project\src\main.java`
- 相对路径：`src/main/java/App.java`
- 带行号：`src/main.java:42`

### 模型切换

| 模型 | 说明 |
|---|---|
| `Opus` (4.8) | 最强推理能力，适合复杂架构和代码审查 |
| `Sonnet` (4.6) | 平衡性能与速度，日常编码首选 |
| `Haiku` (4.5) | 极速响应，适合简单查询和文件搜索 |
| `Fable 5` | 最新旗舰模型 |

使用 `/config model` 切换，或在对话中临时指定。

---

## 九、自定义 Skill（技能）

在项目下创建 `.claude/skills/<名称>.md` 文件，定义自定义技能后，通过 `/<名称>` 调用。

### Skill 文件模板

```markdown
---
description: 技能的一句话描述
---

# 技能名称

## 触发条件
描述什么情况下应该使用这个技能。

## 执行步骤
1. 步骤一
2. 步骤二
3. 步骤三
```

---

## 十、CLAUDE.md 项目指导文件

`CLAUDE.md` 是项目的根指导文件，放在项目根目录。Claude 每次启动时自动加载。

### 常见内容

- 构建命令（`mvn`、`npm`、`make` 等）
- 项目架构说明
- 编码规范
- 测试策略
- 分支策略
- 部署模式

### 子目录 CLAUDE.md

可以在子目录放置更细粒度的 `CLAUDE.md`，当 Claude 操作该目录下的文件时会自动加载对应的指导。

---

## 十一、Memory 记忆系统

| 路径 | 作用范围 |
|---|---|
| `~/.claude/memory/` | 用户级记忆，所有项目共享 |
| `<项目>/.claude/memory/` | 项目级记忆，仅当前项目可见 |

每条记忆一个 `.md` 文件，包含 frontmatter 元数据。使用 `/remember` 快速创建，`/memory` 查看列表，`/forget` 删除。

---

## 十二、Settings 配置速查

配置文件位置：

| 级别 | 路径 |
|---|---|
| 用户全局 | `~/.claude/settings.json` |
| 项目级 | `<项目>/.claude/settings.json` |
| 项目本地 | `<项目>/.claude/settings.local.json`（不提交 git） |

### 常用配置项

```json
{
  "model": "claude-sonnet-4-6",          // 默认模型
  "theme": "dark",                        // 终端主题
  "autoCompact": true,                    // 自动压缩上下文
  "autoRun": false,                       // 是否自动运行命令
  "permissions": {
    "allow": ["Bash(npm run build)"],     // 允许的命令
    "deny": ["Bash(rm -rf *)"]            // 禁止的命令
  }
}
```

---

> 📌 **提示**：在对话中输入 `/help` 可查看最新最全的内置命令列表。
