# Agent Instructions

## Environment Details
- **Operating System:** Windows 11
- **Shell:** PowerShell
- **Instruction:** Always use PowerShell-compatible commands when running shell scripts or commands. Avoid Linux-specific commands (like `ls -R` or `grep` without proper escaping) unless they are confirmed to work in the current PowerShell environment. Prefer built-in tools over shell commands where possible. Note that the `&&` operator is not supported in many PowerShell versions (like Windows PowerShell 5.1); use `;` or run commands separately instead.
