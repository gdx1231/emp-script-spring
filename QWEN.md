# emp-script-spring 项目上下文

## 项目概述

**emp-script-spring** 是一个基于 Spring Boot 的 Web 系统快速成型框架，用于快速构建企业级 Web 应用。该项目是 EWA (Easy Web Application) 框架的 Spring Boot 集成版本。

- **GroupId**: `com.gdxsoft.easyweb`
- **ArtifactId**: `emp-script-spring`
- **版本**: 1.1.10
- **许可证**: MIT License
- **官方网站**: https://www.gdxsoft.com
- **GitHub**: https://github.com/gdx1231/emp-script-spring

## 技术栈

| 组件 | 版本 |
|------|------|
| Spring Framework | 5.3.27 |
| Spring Boot | 2.5.12 |
| Spring Cloud | 3.0.3 |
| Jackson | 2.12.7.1 |
| Servlet API | 4.0.1 |
| WebSocket API | 1.1 |
| Java | 1.8 |

## 核心依赖

项目依赖以下 gdxsoft 自研库（均为 `provided` 作用域）：
- `emp-script` - 核心脚本引擎
- `emp-script-static` - 静态资源处理
- `emp-script-web` - Web 功能模块

## 项目结构

```
src/main/java/com/gdxsoft/easyweb/spring/
├── BaseController.java              # 基础控制器，处理通用数据和新闻逻辑
├── SiteUtils.java                   # 站点工具类，处理多语言、新闻目录等
├── EwaSpingUpload.java              # Spring Boot 文件上传处理
├── FakeEwa.java                     # 模拟 EWA 组件
├── SiteUtils.java                   # 站点上下文工具
├── backAdmin/controllers/           # 后台管理控制器
├── business/controllers/            # 业务控制器（含 OA）
├── controllers/                     # EWA 定义和样式控制器
├── restful/
│   ├── controllers/                 # RESTful API 控制器
│   └── cloud/controllers/           # RESTful Cloud 控制器
├── staticResources/controllers/     # 静态资源控制器
└── web/                             # Web 页面控制器（Index, Nws）
```

## 构建与运行

### 构建命令
```bash
# 编译打包
mvn clean package

# 发布到 Maven 中央仓库（需要 GPG 签名）
mvn clean deploy -P release
```

### 构建输出
- `target/emp-script-spring-<version>.jar` - 主 JAR 包
- `target/emp-script-spring-<version>-sources.jar` - 源码包
- `target/emp-script-spring-<version>-javadoc.jar` - 文档包
- `target/emp-script-spring-last.jar` - 副本（由 copy-rename-maven-plugin 生成）

### Maven 依赖配置
```xml
<dependency>
  <groupId>com.gdxsoft.easyweb</groupId>
  <artifactId>emp-script-spring</artifactId>
  <version>1.1.10</version>
</dependency>
```

## 使用方式

### Spring Boot 应用集成示例

```java
@SpringBootApplication
@ComponentScan(basePackages = {
    "com.gdxsoft.easyweb.spring.controllers",           // EWA 核心
    "com.gdxsoft.easyweb.spring.staticResources.controllers", // 静态资源
    "com.gdxsoft.easyweb.spring.restful.controllers",   // RESTful API
    "com.gdxsoft.yourApp.controllers"                   // 自定义控制器
})
public class YourAppApplication {
    public static void main(String[] args) {
        SpringApplication.run(YourAppApplication.class, args);
    }
}
```

## 核心功能模块

### 1. 新闻/内容管理
- 多语言支持（中文/英文）
- 新闻目录管理（NwsCat）
- 文章内容展示
- 关键词处理
- 封面图片处理（支持 WebP/HEIC 格式转换）

### 2. 文件上传
- 基于 `EwaSpingUpload` 的文件上传处理
- 支持多文件上传
- 自动生成缩略图
- 文件扩展名验证

### 3. RESTful API
- CORS 跨域支持（`CorsConfig`）
- 统一的 RESTful 结果格式
- Cloud 风格的 RESTful 接口

### 4. 站点管理
- 站点导航（头部/底部）
- 多语言切换
- 站点配置缓存

## 配置说明

### 关键配置项
- `SITE_UNID` - 站点唯一标识，在 `ewa_conf.xml` 的 `initparas->para` 中定义
- `NEWS_XMLNAME` - 新闻默认配置文件路径（`/front-web/news.xml`）

### 缓存机制
- 站点导航信息使用 `ConcurrentHashMap` 缓存
- 支持通过 `?new=true` 参数刷新缓存

## 开发约定

### 编码规范
- Java 8 语法
- UTF-8 编码
- 使用 SLF4J 日志框架

### 包命名约定
- 控制器包：`*.controllers`
- 工具类：直接位于 `spring` 包下
- 业务逻辑：`business.controllers`

### 多语言支持
- 语言参数：`enus`（英文）、其他（中文）
- 通过 `RequestValue.getLang()` 获取当前语言
- 站点内容根据语言动态切换

## 相关文件

| 文件 | 说明 |
|------|------|
| `pom.xml` | Maven 构建配置 |
| `README.md` | 项目说明文档 |
| `LICENSE` | MIT 许可证 |
| `.gitignore` | Git 忽略规则 |
| `src/main/resources/1.html` | 示例 HTML 模板 |

## 注意事项

1. 所有 Spring 相关依赖均设置为 `provided` 作用域，使用时需确保主应用已包含这些依赖
2. 项目需要配合 `emp-script` 系列库使用
3. 文件上传需要正确配置上传目录
4. 站点配置需要预先在数据库或配置文件中定义
