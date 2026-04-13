# ACR v2.1.2-b cleanup script notes

This version adds the last safety fixes:

- pre-cleanup workspace snapshot written to `cleanup_logs/`
- existing `.gitignore` is preserved and only missing lines are appended
- optional confirmation before archive moves unless `AUTO_APPROVE=true`
- explicit diagnosis of reasoner layout:
  - `microservices/openllet-reasoner`
  - `services/acr-reasoner-service`
- helper commands printed for Spring Boot parent and `javax.annotation` / `jakarta.annotation` inspection

Recommended first run:

```zsh
DRY_RUN=true zsh cleanup_acr_v2.1.2-b_safe.zsh
git status
```

Then review:

- `cleanup_logs/pre_cleanup_*.log`
- any proposed archive moves
- current reasoner location state
- `.gitignore` diff

This script is intentionally conservative. The actual move from:

- `microservices/openllet-reasoner`

to:

- `services/acr-reasoner-service`

should still be a separate manual Git operation.
