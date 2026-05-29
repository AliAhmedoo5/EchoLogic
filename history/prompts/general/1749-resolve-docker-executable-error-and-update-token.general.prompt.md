---
id: 1749
title: Resolve Docker Executable Error and Update Token
stage: general
date: 2026-05-29
surface: agent
model: Gemini 3.5 Flash (Medium)
feature: none
branch: main
user: pds
command: [User uploaded screenshot showing Docker executable file not found in %PATH% error, with GitHub PAT ghp_REDACTED]
labels: ["mcp", "github", "error-fix"]
links:
  spec: null
  ticket: null
  adr: null
  pr: null
files:
 - C:/Users/pds/.gemini/antigravity-ide/mcp_config.json
tests:
 - Checked and verified token ghp_REDACTED against GitHub API
 - Verified Node and npx versions on the system
---

## Prompt

[User uploaded screenshot showing Docker executable file not found in %PATH% error, with GitHub PAT ghp_REDACTED]

## Response snapshot

The error shown in your screenshot occurs because the IDE's built-in GitHub integration defaults to running the MCP server in a Docker container, but Docker is not installed or configured on your system. I bypassed the Docker dependency entirely by configuring the Node.js/npx-based GitHub MCP server directly in your global mcp_config.json. I also verified and updated the configuration to use your newly entered token (ghp_REDACTED), which successfully authenticates user AliAhmedoo5.

## Outcome

- ✅ Impact: Docker dependency for the GitHub MCP server is completely bypassed. Node.js-based server is configured using the valid token from the user's screenshot, enabling native execution on Windows.
- 🧪 Tests: Verified Node.js (v24.13.0) and npx (11.8.0) are available. Successfully validated token ghp_REDACTED against the GitHub API, returning user details for AliAhmedoo5.
- 📁 Files: C:/Users/pds/.gemini/antigravity-ide/mcp_config.json
- 🔁 Next prompts: TBD
- 🧠 Reflection: Providing a direct npx execution template in the mcp_config.json is the most robust way to solve Docker missing issues on Windows environments.

## Evaluation notes (flywheel)

- Failure modes observed: None
- Graders run and results (PASS/FAIL): PASS
- Prompt variant (if applicable): null
- Next experiment (smallest change to try): null
