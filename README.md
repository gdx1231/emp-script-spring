# emp-script-spring

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://github.com/gdx1231/emp-script-spring/blob/main/LICENSE)
[![Maven Central](https://img.shields.io/badge/maven-1.1.10-blue)](https://mvnrepository.com/artifact/com.gdxsoft.easyweb/emp-script-spring)
[![Java](https://img.shields.io/badge/java-1.8-orange)](https://openjdk.java.net/)
[![Spring Boot](https://img.shields.io/badge/spring%20boot-2.5.12-brightgreen)](https://spring.io/projects/spring-boot)

**Easy Web Application Builder Spring** - 基于 Spring Boot 的 Web 系统快速成型框架

## 📖 简介

emp-script-spring 是 EWA (Easy Web Application) 框架的 Spring Boot 集成版本，提供了一套完整的 Web 应用快速开发解决方案。支持多语言、新闻内容管理、文件上传、RESTful API 等企业级功能。

## ✨ 特性

- 🌐 **多语言支持** - 内置中英文切换，支持国际化
- 📰 **内容管理** - 完整的新闻/文章目录管理和展示
- 📁 **文件上传** - 支持多文件上传、缩略图生成、格式转换
- 🔌 **RESTful API** - 标准化的 REST 接口，支持 CORS 跨域
- 🎨 **静态资源** - 内置静态资源控制器
- 💾 **缓存机制** - 智能缓存站点配置和导航信息

## 🚀 快速开始

### 1. 添加 Maven 依赖

```xml
<dependency>
  <groupId>com.gdxsoft.easyweb</groupId>
  <artifactId>emp-script-spring</artifactId>
  <version>1.1.10</version>
</dependency>
```

### 2. Spring Boot 应用配置

```java
@SpringBootApplication
@ComponentScan(basePackages = {
    "com.gdxsoft.easyweb.spring.controllers",           // EWA 核心控制器
    "com.gdxsoft.easyweb.spring.staticResources.controllers", // 静态资源 (CSS/JS/图片)
    "com.gdxsoft.easyweb.spring.restful.controllers",   // RESTful API
    "com.gdxsoft.easyweb.spring.restful.cloud.controllers", // RESTful Cloud
    "com.gdxsoft.yourApp.controllers"                   // 你的自定义控制器
})
public class YourAppApplication {
    public static void main(String[] args) {
        SpringApplication.run(YourAppApplication.class, args);
    }
}
```

### 3. 配置文件

在 `ewa_conf.xml` 中配置站点信息：

```xml
<initparas>
    <para name="site_unid">YOUR_SITE_UNID</para>
</initparas>
```

## 📦 核心模块

### Controller 详细说明

#### 1. EWA 定义控制器 (`EwaDefineController`)
**包路径**: `com.gdxsoft.easyweb.spring.controllers`

| URL 路径 | 功能说明 |
|----------|----------|
| `/EWA_DEFINE` | EWA 定义管理首页 |
| `/EWA_DEFINE/cgi-bin/xml/` | 配置管理操作 |
| `/EWA_DEFINE/remoteSync` | 本地和远程资源同步 |
| `/EWA_DEFINE/cgi-bin/group/` | 组文件操作 |

#### 2. EWA 样式控制器 (`EwaStyleController`)
**包路径**: `com.gdxsoft.easyweb.spring.controllers`

| URL 路径 | 功能说明 |
|----------|----------|
| `/EWA_STYLE/cgi-bin/`, `/ewa` | EWA 脚本主入口 |
| `/EWA_STYLE/cgi-bin/_re_/`, `/r.ewa` | 资源加载 |
| `/EWA_STYLE/cgi-bin/_st_/` | 树形状态查询 |
| `/EWA_STYLE/cgi-bin/_up_/` | 文件上传处理 |
| `/EWA_STYLE/cgi-bin/_er_/` | 错误处理 |
| `/EWA_STYLE/cgi-bin/_wf_/` | 工作流处理 |

#### 3. 静态资源控制器 (`EwaStaticController`)
**包路径**: `com.gdxsoft.easyweb.spring.staticResources.controllers`

| URL 路径 | 功能说明 |
|----------|----------|
| `/EmpScriptV2/**` | EWA 静态文件映射（CSS/JS/图片） |

#### 4. RESTful 控制器 (`EwaRestfulController`)
**包路径**: `com.gdxsoft.easyweb.spring.restful.controllers`

| URL 路径 | 功能说明 |
|----------|----------|
| `/ewa-api/**` | RESTful API 接口（配置在 ewa_conf.xml） |

**特性**:
- 自动配置 Jackson 解决 Long 类型精度丢失问题
- 支持文件上传处理
- 支持 CORS 跨域（通过 `CorsConfig`）

#### 5. RESTful Cloud 控制器 (`EwaRestfulCloudController`)
**包路径**: `com.gdxsoft.easyweb.spring.restful.cloud.controllers`

**功能**: 基于 Spring Cloud 的服务间调用
- 从服务发现中心获取实例列表
- 负载均衡调用远程服务
- 转发请求头和请求体

#### 6. 首页控制器 (`IndexController`)
**包路径**: `com.gdxsoft.easyweb.spring.web`

| URL 路径 | 功能说明 |
|----------|----------|
| `/` | 应用首页，自动语言检测 |
| `/{ewa_lang}` | 指定语言的首页 |
| `/{ewa_lang}/header` | 页面头部 |
| `/{ewa_lang}/footer` | 页面底部 |

#### 7. 新闻控制器 (`NwsController`)
**包路径**: `com.gdxsoft.easyweb.spring.web`

| URL 路径 | 功能说明 |
|----------|----------|
| `/news-cover-pic` | 新闻封面图片（支持缓存和缩略图） |
| `/{ewa_lang}/news` | 新闻列表（Grid 格式） |
| `/{ewa_lang}/news/{NWS_CAT_UNID}` | 指定目录的新闻 |
| `/{ewa_lang}/news/{NWS_CAT_UNID}/{NWS_GUID}` | 具体文章详情 |
| `/news-info` | 新闻详情（支持跳转到第三方 URL） |
| `/news_att_download/{nws_att_guid}` | 新闻附件下载 |

#### 8. 后台管理控制器 (`EwaBackAdminController`)
**包路径**: `com.gdxsoft.easyweb.spring.backAdmin.controllers`

| URL 路径 | 功能说明 |
|----------|----------|
| `/back_admin/` | 后台管理首页 |
| `/back_admin/login` | 登录页面 |
| `/back_admin/login_exit` | 退出登录 |

#### 9. 业务控制器 (`BusinessController`)
**包路径**: `com.gdxsoft.easyweb.spring.business.controllers`

| URL 路径 | 功能说明 |
|----------|----------|
| `/business` | 商户系统首页 |
| `/business/login` | 商户登录（支持找回密码） |
| `/business/login_exit` | 退出登录 |
| `/business/qrcode` | 生成二维码 |
| `/business/oa-file-view` | OA 文件在线查看 |
| `/business/sys-att` | 系统附件查看 |

#### 10. OA 控制器 (`OAController`)
**包路径**: `com.gdxsoft.easyweb.spring.business.controllers`

| URL 路径 | 功能说明 |
|----------|----------|
| `/ups/**` | 上传目录资源读取（30 天有效期） |
| `/s`, `/short-url-verify` | 短地址跳转 |
| `/sys-att` | 系统附件查看 |

## 🔧 技术栈

- **Spring Framework** 5.3.27
- **Spring Boot** 2.5.12
- **Spring Cloud** 3.0.3
- **Jackson** 2.12.7.1
- **Servlet API** 4.0.1
- **Java** 1.8+

## 📚 使用示例

### 新闻内容展示

```java
@Controller
@RequestMapping("/{lang}/news")
public class NewsController extends BaseController {
    
    @GetMapping("/{catTag}/{guid}")
    public String showNews(@PathVariable String lang,
                          @PathVariable String catTag,
                          @PathVariable String guid,
                          Model model,
                          HttpServletRequest request,
                          HttpServletResponse response) {
        return news(lang, catTag, guid, "news", request, response, model);
    }
}
```

### 文件上传

```java
@PostMapping("/upload")
@ResponseBody
public RestfulResult<Object> upload(HttpServletRequest request) {
    RestfulResult<Object> result = new RestfulResult<>();
    EwaSpingUpload.handleUpload(rv, request, result);
    return result;
}
```

## 🛠️ 构建

```bash
# 编译打包
mvn clean package

# 生成源码包和文档
mvn clean package

# 发布到 Maven 中央仓库（需要 GPG 签名）
mvn clean deploy -P release
```

## 📄 许可证

本项目采用 [MIT 许可证](https://github.com/gdx1231/emp-script-spring/blob/main/LICENSE)

## 👥 开发者

| 姓名 | ID | 邮箱 |
|------|-----|------|
| 郭磊 | guolei | guolei@gdxsoft.com |
| 靳朝鹏 | jinzhappeng | jinzhaopeng@126.com |

## 🔗 相关链接

- **官方网站**: https://www.gdxsoft.com
- **GitHub**: https://github.com/gdx1231/emp-script-spring
- **Maven Central**: https://mvnrepository.com/artifact/com.gdxsoft.easyweb/emp-script-spring

## 📝 更新日志

### v1.1.10
- 优化文件上传处理
- 改进多语言支持
- 修复已知问题

---

© 2021 gdxsoft. All rights reserved.
