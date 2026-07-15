---
id: 1757
title: Learn Android CI and YAML Block Scalar Guardrails
stage: general
date: 2026-07-15
surface: agent
model: Gemini 3.1 Pro (High)
feature: none
branch: 001-fix-app-startup
user: pds
command: /learn/learn
labels: ["learn", "github-actions", "gradle", "yaml", "guardrails", "ci-cd"]
links:
  spec: null
  ticket: null
  adr: null
  pr: null
files:
 - e:/Projects/dribile/sp_dribbile/HURDLES.md
 - e:/Projects/dribile/sp_dribbile/learntbymistakes.md
tests:
 - Verified HURDLES.md Items 22 & 23 appended cleanly
 - Verified learntbymistakes.md Sections 7 & 8 plus Critical Checkpoints appended cleanly
---

## Prompt

/learn/learn

## Response snapshot

Executed `/learn` protocol following user approval of `learning_proposal.md`. Persisted lessons learned from recent GitHub Actions debugging sessions into project architectural knowledge documents (`HURDLES.md` Items 22 & 23 and `learntbymistakes.md` Sections 7 & 8 plus checkpoints 4-6):
1. **Gradle Wrapper Guardrail**: Explicitly allow and commit `gradle-wrapper.jar` (`!gradle/wrapper/gradle-wrapper.jar`) when creating CI/CD workflows.
2. **Self-Healing Credentials**: Always add self-healing checks for `google-services.json` and release `keystore.jks` in CI workflows so builds run out of the box on clean runners without failing due to missing local untracked files.
3. **YAML vs Bash Heredoc Indentation**: Never use multi-line `cat << 'EOF'` heredocs inside YAML `run: |` block scalars due to indentation syntax conflicts; always use single-line `echo` commands.

## Outcome

- ✅ Impact: Permanently integrated critical remote CI/CD, Gradle wrapper, and YAML formatting guardrails into project knowledge base to prevent regressions across all future AI/developer interactions.
- 🧪 Tests: Confirmed markdown formatting and structure of HURDLES.md and learntbymistakes.md.
- 📁 Files: HURDLES.md, learntbymistakes.md
- 🔁 Next prompts: TBD
- 🧠 Reflection: Persisting architectural and CI/CD learnings directly inside project repositories ensures institutional knowledge is preserved and applied automatically by future developer and AI agents across every feature branch.

## Evaluation notes (flywheel)

- Failure modes observed: None
- Graders run and results (PASS/FAIL): PASS
- Prompt variant (if applicable): null
- Next experiment (smallest change to try): null
