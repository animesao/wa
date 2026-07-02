# Contributing to Wasteland Artifacts

Спасибо, что хочешь помочь проекту! Мы рады любому вкладу — баг-репортам, фича-реквестам, пулл-реквестам и улучшению документации.

---

## 🌍 Language

- **Issues/comments** — English or Russian
- **Code** — English (classes, methods, variables, commits)
- **YAML/Config** — English keys, Russian descriptions where appropriate

## 🐛 Reporting Bugs

Before reporting, check [existing issues](https://github.com/animesao/WA/issues).

When filing a bug report, include:

- **Server version** (`/version`)
- **Plugin version** (`/version WastelandArtifacts`)
- **Steps to reproduce** (minimal, clear)
- **Expected behavior** vs **actual behavior**
- **Full error log** (use [mclo.gs](https://mclo.gs) or [pastebin](https://pastebin.com))
- **Config files** if relevant (remove sensitive data)

Use the **Bug Report** template when creating an issue.

## 💡 Suggesting Features

Use the **Feature Request** template. Describe:

- What you want to achieve
- Why it fits the wasteland/post-apocalyptic theme
- How it could be configured (YAML schema idea)
- Any references (other plugins, games)

## 🧑‍💻 Pull Requests

### Getting Started

1. Fork the repo
2. Create a branch: `git checkout -b feat/my-feature` or `fix/my-bug`
3. Make your changes
4. Run `./gradlew fatJar` to verify it compiles
5. Test on a local Paper 1.21.1 server
6. Push and open a PR

### Code Style

- **Java 21** — use var, records, text blocks, switch expressions where appropriate
- **Indentation:** 4 spaces (no tabs)
- **Braces:** K&R style (opening brace on same line)
- **Imports:** no wildcard imports (`import me.darkcube.wa.*`)
- **Naming:**
  - Classes: `PascalCase`
  - Methods/variables: `camelCase`
  - Constants: `UPPER_SNAKE_CASE`
  - Config IDs: `snake_case`
- **Null safety:** use `@Nullable`/`@NotNull` annotations, avoid returning null
- **Minimal comments** — let code speak; doc comments only for public API

### Architecture Guidelines

- New features go under `me.darkcube.wa.feature.<feature_name>/`
- Wire through `FeatureBootstrapper` — don't call directly from `onEnable()`
- Add a toggle in `FeatureConfig` (`config.yml -> features`)
- Use `ComponentLogger` for logging (not `getLogger()`)
- Use Kyori Adventure (MiniMessage) for all text — never `§` or `ChatColor`
- YAML configs use Jackson (`ObjectMapper` with `YAMLFactory`)

### Commit Messages

```
<type>: <short summary>

Optional body with details.
```

Types: `feat`, `fix`, `refactor`, `config`, `docs`, `test`, `chore`

Examples:
- `feat: add frost damage component`
- `fix: altar hologram not updating after craft`
- `docs: add example for custom item section`

### PR Checklist

- [ ] Compiles: `./gradlew fatJar`
- [ ] Tested on Paper 1.21.1
- [ ] No new warnings in logs
- [ ] Config changes have defaults in `config.yml` or feature file
- [ ] New features have a toggle (enabled/disabled)
- [ ] Language strings added to `lang/*.yml` (at least `en_US.yml`)
- [ ] No hardcoded magic values — use config or constants

---

## 🧪 Testing

The project currently has **no automated tests**. If you add tests, you're a hero. Contribution guidelines will be updated when a test framework is introduced.

---

## 📖 Documentation

- Update `README.md` if you add commands, permissions, or config keys
- Update `docs/WIKI_EN.md` and `docs/WIKI_RU.md` for major features
- Add YAML examples if introducing new config formats

---

## 🤝 Need Help?

Open a [Discussion](https://github.com/animesao/WA/discussions) or ping in the issue/PR. We're friendly!
