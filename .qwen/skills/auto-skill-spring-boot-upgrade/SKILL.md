---
name: spring-boot-upgrade
description: Spring Boot 大版本升级流程：从旧版（2.x）升级到 4.x，包含级联依赖版本查询、javax→jakarta 命名空间迁移、Spring Boot 4.x API 断裂修复
source: auto-skill
extracted_at: '2026-06-27T09:01:26.222Z'
---

# Spring Boot 大版本升级

将 Spring Boot 项目从一个主版本升级到另一个主版本（如 2.x → 4.x）的系统化流程。

## 前置条件

- 知道目标 Spring Boot 版本号（让用户确认，不要假设版本不存在）
- 项目使用 Maven
- 能访问 Maven Central（或用户本地仓库有对应版本）

## 步骤

### 1. 确认目标版本

让用户指定具体版本。如果用户给的版本号看似不存在（如 "4+"），用事实询问而非断言不存在——用户的截止日期可能比 AI 训练数据更新。

### 2. 查询 Spring Boot BOM 获取级联依赖版本

Spring Boot 的 `spring-boot-dependencies` POM 管理了所有兼容依赖版本的 property，直接查询避免猜测：

```bash
# 获取 Spring Boot 自身的依赖版本
curl -sL "https://repo1.maven.org/maven2/org/springframework/boot/spring-boot/<version>/spring-boot-<version>.pom"

# 获取 BOM 管理的属性版本
curl -sL "https://repo1.maven.org/maven2/org/springframework/boot/spring-boot-dependencies/<version>/spring-boot-dependencies-<version>.pom" \
  | grep -E '<spring-framework.version>|<jakarta-servlet.version>|<jackson-bom.version>|<jackson-2-bom.version>'
```

关键属性名规律：
- Spring Framework: `<spring-framework.version>`
- Jakarta Servlet: `<jakarta-servlet.version>`
- Jakarta WebSocket: `<jakarta-websocket.version>`
- Jackson 2: `<jackson-2-bom.version>`（groupId `com.fasterxml.jackson`）
- Jackson 3: `<jackson-bom.version>`（groupId `tools.jackson`，注意 Boot 4.x 默认仍用 Jackson 2）

**注意**：Jackson 3 坐标变更为 `tools.jackson:jackson-bom`，若 BOM 引用 3.x 但 Maven Central 只有 2.x，说明 Boot 默认仍用 Jackson 2——直接用 `<jackson-2-bom.version>` 对应的版本。

### 3. 查询其他依赖的最新兼容版本

Spring Cloud 不受 Boot BOM 管理，需单独查：

```bash
# 查 artifact 最新版本
curl -sL "https://repo1.maven.org/maven2/org/springframework/cloud/spring-cloud-commons/maven-metadata.xml" \
  | grep -o '<version>[^<]*</version>' | grep -v 'rc\|M\|alpha\|beta' | tail -10
```

### 3b. 检查 jdk17 变体依赖的坐标变化

如果依赖库有 jdk17 专版，坐标可能发生变化——不仅是 version 后缀，也可能是 artifactId 变化：
- `emp-script:1.1.10-jdk17`（version 后缀）→ 本地实际是 `emp-script-jdk17:1.1.10`（artifactId 后缀）
- 通过 `find ~/.m2/repository/<group-path> -name "*jdk17*" -type f` 检查本地实际 jar 坐标

### 4. 批量更新 pom.xml 版本号

同版本号出现多次的依赖用 `edit` + `replace_all` 一次更新（如 Spring Framework 版本出现在 5 个 dependency 中）。

**升级顺序**：先改框架依赖（Spring → Boot → Cloud），再改辅助库（Jackson），最后改 Servlet 命名空间。

Spring Boot 2→4 典型版本映射：

| 组件 | 2.x | 4.x |
|------|-----|-----|
| Spring Framework | 5.3.x | 7.0.x |
| Spring Boot | 2.5.x | 4.1.x |
| Spring Cloud | 3.0.x | 5.0.x |
| Jackson | 2.12.x | 2.21.x |
| Servlet | javax:4.x | jakarta:6.x |
| WebSocket | javax:1.x | jakarta:2.x |
| Compiler | 1.8 | 17 |

**Compiler 配置**：用 `<release>17</release>` 替代 `<source>/<target>`，JDK 9+ 推荐 `release` 以自动设置系统模块路径。

**Build 目录**：如果改了 `<directory>`（如 `target` → `target17`），搜索所有硬编码的 `target/` 路径替换为 `${project.build.directory}`：

```xml
<!-- 错误 -->
<sourceFile>target/${project.build.finalName}.jar</sourceFile>
<!-- 正确 -->
<sourceFile>${project.build.directory}/${project.build.finalName}.jar</sourceFile>
```

### 5. javax → jakarta 命名空间迁移

源码级别迁移分为两步：

**5a. 发现所有引用**

```bash
grep -r "javax\.\(servlet\|websocket\)" src/
```

可能出现在：
- `import javax.servlet.*` — 最常见
- 全限定引用如 `javax.servlet.http.Cookie` — 少量但必须改

**5b. 批量替换**

用 `edit` + `replace_all` 逐文件替换：
- `old_string`: `import javax.servlet` → `new_string`: `import jakarta.servlet`
- `old_string`: `javax.servlet` → `new_string`: `jakarta.servlet`（处理全限定引用）

最后再次 grep 确认零残留。

### 6. Spring Boot 4.x 特定 API 断裂修复

以下 API 在 Spring Boot 4.x / Spring Framework 7.x 中被移除或变更，升级后必然遇到：

| 断裂点 | 旧 API | 新 API / 修复 |
|--------|--------|--------------|
| Jackson 定制 | `Jackson2ObjectMapperBuilderCustomizer` (spring-boot-autoconfigure) | 直接返回 `Jackson2ObjectMapperBuilder` Bean |
| RestTemplate 构建 | `RestTemplateBuilder` (spring-boot) | `new RestTemplate()` 直接构造 |
| HTTP 方法解析 | `HttpMethod.resolve(str)` (Spring 6) | `HttpMethod.valueOf(str)` (Spring 7) |
| 错误处理 | `DefaultResponseErrorHandler.handleError(ClientHttpResponse)` | `handleError(URI, HttpMethod, ClientHttpResponse)` 多两个参数 |
| emp 库类路径 | `com.gdxsoft.easyweb.script.servlets.RestfulResult` | `com.gdxsoft.easyweb.script.restful.RestfulResult`（用 `javap` 检查 jar 确认） |

**通用修复方法**：编译报错后，用 `javap` 检查 jar 中类的实际 API：
```bash
javap -public -classpath <jar-path> <fully-qualified-class-name> | grep <method-name>
```

### 7. 编译与验证

**首次编译必须在线**（去掉 `-o`），新版本依赖可能不在本地仓库。如果被缓存的失败下载阻塞，加 `-U` 强制更新：

```bash
mvn clean compile -U
```

编译成功后打包：
```bash
mvn clean package -DskipTests
```

验证点：
- 确认输出 jar 在正确的 build directory 下（如 `target17/`）
- `copy-rename` 等后处理插件路径正确
- 所有 Java 文件编译通过，无 `javax` 残留

### 8. 编译警告处理

- `未与 -source 17 一起设置系统模块的位置` → 用 `<release>17</release>` 替代（已在上文修复）
- `Jackson2ObjectMapperBuilder 已过时` → Spring 7 中标记为 deprecated-for-removal，但功能正常；可后续迁移到 Jackson 3.x 时一并处理
