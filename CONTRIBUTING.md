# Contributing

Bugs, ideas, and pull requests are welcome.

## Reporting a bug

Use the [bug report template](https://github.com/Chalwk/JCBudgetBuddy/issues/new?template=bug-report.yaml) and include:

- Your Windows version
- Which build you're using (installer or from source)
- The exact steps to reproduce
- The full error output or crash log
- Your Qt version if you built from source

Redact any personal financial data before posting. The app stores data in
`%USERPROFILE%\.JCBudgetBuddy\userdata.json`, so sanitise any snippets you share.

## Reporting a security issue

**Do not open a public issue for security problems.**

Use the private [Report a vulnerability](https://github.com/Chalwk/JCBudgetBuddy/security/advisories/new)
flow on the Security tab, or see [SECURITY.md](https://github.com/Chalwk/JCBudgetBuddy/blob/main/SECURITY.md)
for the full policy.

## Suggesting a feature

Open an issue describing the problem you're trying to solve, not the solution
you have in mind. That gives more room to suggest something simpler.

## Pull requests

Before opening a PR, make sure:

- The project builds cleanly with CMake and Qt 6.x
- No new compiler warnings on MSVC
- The app runs and persists data correctly on Windows
- No hardcoded paths, secrets, or personal data
- You've tested the cases described in the related issue

## Code style

There's no enforced linter, but the house style is:

- 4-space indentation
- Qt naming conventions (camelCase for methods, PascalCase for classes)
- Prefer Qt types (`QString`, `QList`, `QJsonObject`) where the rest of the
  codebase does
- Keep UI logic in widgets and persistence logic in dedicated classes

## Questions

Open a [GitHub Discussion](https://github.com/Chalwk/JCBudgetBuddy/discussions)
or find me on [Discord](https://discord.gg/VAEb4FXU5).
