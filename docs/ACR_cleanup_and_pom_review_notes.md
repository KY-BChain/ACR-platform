# ACR cleanup script and pom review

## Script review

The uploaded cleanup script is useful, but it should be revised before running on the active workspace. The main issues are:

- it is written as `bash`, not `zsh`
- it still uses the older `microservices/` and v2.1.1 naming
- it still references **Claude 3.5 Sonnet** in the OpenClaw README, but your current architecture requires **DeepSeek**
- it archives `ACR-Ontology-Interface` automatically, which is risky if that folder is still active
- it rewrites `.gitignore` from scratch without preserving repo-specific choices
- it treats blockchain structure as more immediate than your phased plan requires
- it writes `$(date)` inside a single-quoted heredoc in the original script, so that date would not expand

## pom.xml review

The uploaded XML snippet adds:

```xml
<dependency>
    <groupId>javax.annotation</groupId>
    <artifactId>javax.annotation-api</artifactId>
    <version>1.3.2</version>
</dependency>
```

That can be a valid short-term fix for missing `@PostConstruct` and `@PreDestroy` if your code still imports `javax.annotation.*`.

### Recommended decision rule
- If your reasoner service is on **Spring Boot 2.x**, `javax.annotation-api` can be acceptable.
- If your reasoner service is on **Spring Boot 3.x / Java 21**, the cleaner long-term fix is to migrate imports to `jakarta.annotation.*`.

### Before editing pom.xml, check
1. Spring Boot parent version
2. Java version
3. whether the Java files import `javax.annotation.*` or `jakarta.annotation.*`

Do not add both `javax.annotation-api` and `jakarta.annotation-api` blindly.

## Suggested run order

1. Review the revised zsh script
2. Run:
   `DRY_RUN=true zsh cleanup_acr_v2.1.2_safe.zsh`
3. Review `git status`
4. Run the script for real only if the proposed moves look correct
5. Then fix `pom.xml`
6. Then build the reasoner module
