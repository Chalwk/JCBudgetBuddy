# Security Policy

JCBudgetBuddy is a local Windows desktop application. It stores user data on
disk and does not transmit anything to a remote server. This document explains
which versions receive security updates, how to report a vulnerability, and
what to expect.

## Supported Versions

This repository does not publish long-term support branches. Fixes are applied
to the latest release on `main`.

| Version          | Supported |
| ---------------- | --------- |
| Latest release   | Yes       |
| Older releases   | No        |
| Forks            | No        |

If you have an older copy, re-download from the Releases page before reporting
anything. It may already be fixed.

## Reporting a Vulnerability

**Please do not open a public issue for security problems.**

Two private channels are available:

1. **GitHub Private Vulnerability Reporting** (preferred). Use the
   [Report a vulnerability](https://github.com/Chalwk/JCBudgetBuddy/security/advisories/new)
   button on the repository's Security tab.
2. **Email**. If you'd rather not use GitHub, email
   [chalwk.dev@gmail.com](mailto:chalwk.dev@gmail.com) with "SECURITY" in the
   subject line.

### What to include

- The version and build you're running
- A clear description of the issue
- Steps to reproduce, or a minimal proof of concept
- The impact you believe it has
- Whether you've disclosed it anywhere else

Redact any real financial data, file paths containing your username, or
personal information from what you send.

## Scope

### In scope

- Hardcoded secrets or credentials in the source or build scripts
- Unsafe deserialisation of `userdata.json`
- Path traversal or arbitrary file write via the data directory
- DLL hijacking or unsafe library loading in the Windows build
- Crashes or memory corruption triggered by crafted JSON input
- Vulnerabilities in the NSIS installer that could lead to code execution

### Out of scope

- Issues that require an attacker to already be running untrusted code on
  your machine
- Local privilege escalation that requires admin rights to begin with
- Cosmetic bugs, typos, or feature requests
- Findings from automated scanners with no demonstrated impact

If you're not sure whether something is in scope, report it anyway and I'll
tell you.

## What to expect

This is a personal project maintained by one person. Realistic timelines:

- **Acknowledgement:** within 7 days
- **Initial assessment:** within 14 days
- **Fix or workaround:** depends on severity, usually within 30 days for anything confirmed
- **Public disclosure:** coordinated with you. I'll credit you unless you'd
  prefer to stay anonymous.

If a report is declined, I'll explain why.

## Using JCBudgetBuddy safely

- Keep your copy current. Pull the latest release from the Releases page.
- Back up `%USERPROFILE%\.JCBudgetBuddy\userdata.json` before upgrading.
- Treat the data file as sensitive. It contains your financial records.
- Only download installers from the official Releases page.

## Automated security

This repository runs the following GitHub security features on every push:

- Dependabot alerts and security updates
- Secret scanning with push protection

Findings from these tools are triaged by the maintainer. If you've spotted
something the automated tools missed, use the private reporting channels above.
