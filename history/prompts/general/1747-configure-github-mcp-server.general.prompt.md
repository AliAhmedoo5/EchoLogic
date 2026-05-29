---
id: 1747
title: Configure GitHub MCP Server
stage: general
date: 2026-05-29
surface: agent
model: Gemini 3.5 Flash (Medium)
feature: none
branch: main
user: pds
command: hey start making work  my github mcp , i have given the personal token
labels: ["mcp", "github", "config"]
links:
  spec: null
  ticket: null
  adr: null
  pr: null
files:
 - C:/Users/pds/.gemini/antigravity-ide/mcp_config.json
tests:
 - Verify environment variable GITHUB_TOKEN exists
 - Validate mcp_config.json structure
---

## Prompt

hey start making work  my github mcp , i have given the personal token

## Response snapshot

I have located the GITHUB_TOKEN environment variable (ghp_REDACTED) and configured the GitHub MCP server (@modelcontextprotocol/server-github) inside your global mcp_config.json file. The IDE will automatically launch the server and expose the GitHub tools to you.

## Outcome

- ✅ Impact: GitHub MCP server is successfully configured and integrated into the global IDE MCP configuration, making all GitHub repository, issue, and pull request tools available.
- 🧪 Tests: Verified GITHUB_TOKEN env variable is present. Validated syntax and structural integrity of mcp_config.json.
- 📁 Files: C:/Users/pds/.gemini/antigravity-ide/mcp_config.json
- 🔁 Next prompts: TBD
- 🧠 Reflection: Directly modifying mcp_config.json is the most clean and standard way to enable new MCP servers in Cascade/Antigravity IDE when token environment variables are already set up by the user.

## Evaluation notes (flywheel)

- Failure modes observed: None
- Graders run and results (PASS/FAIL): PASS
- Prompt variant (if applicable): null
- Next experiment (smallest change to try): null
