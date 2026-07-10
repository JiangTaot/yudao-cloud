---
name: project-claude-path
description: 当前项目的 .claude 配置目录路径及路径映射规则
metadata:
  type: reference
---

# 项目 .claude 路径

- **Windows 路径**: `D:\workspace\YunDao\yudao-cloud\.claude\`
- **Git Bash 路径**: `/d/workspace/YunDao/yudao-cloud/.claude/`
- **记忆目录**: `.claude/memory/`（内存文件存放在此子目录中）

## 路径映射规则
- Windows `D:\` 在 Git Bash 中对应 `/d/`
- **不要**使用 `/mnt/d/` 前缀（那是 WSL 路径，本环境不适用）
- Write/Read/Edit 等工具使用 Git Bash 路径格式（`/d/...`）
