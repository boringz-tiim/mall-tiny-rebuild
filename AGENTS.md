# mall-tiny-rebuild 项目协作说明

## 项目目标

- 使用 Spring Boot 4、Java 17 和现代依赖，从零复现 `mall-tiny` 后端权限管理功能。
- 原项目 `macrozheng/mall-tiny` 只作为业务功能、数据库结构和分层设计参考。
- 不逐行复制 Spring Boot 2 代码；遇到不兼容时采用 Spring Boot 4 的当前写法。

## 技术基线

- Spring Boot 4.1.0
- Java 17；本机使用 JDK 21 编译和运行 Maven
- Maven
- MySQL，数据库名 `mall_tiny`
- Redis，开发环境使用 `localhost:6379`、逻辑数据库 0、无密码
- MyBatis-Plus 3.5.17
- Spring Security 7
- 后续使用新版 JJWT 和 Springdoc OpenAPI

## 协作方式

- 用户通过本项目学习 Java 后端开发。
- 项目代码由用户亲手编写。除非用户明确要求代为修改，否则 Codex 不直接修改业务代码。
- Codex 应按照实际开发顺序，依次给出每一步需要创建或修改的文件、代码位置和代码内容；一次可以指导完成一个完整的小功能，但不能跳过关键步骤。
- 每一步都应说明：目的、原理、代码职责、关键语法、验证方法和常见错误。
- 每段代码都应解释它在当前调用链中的作用，以及它与 Controller、Service、ServiceImpl、Mapper、DTO 和数据库之间的关系。
- 遇到常见 Java 后端面试知识点时，应单独标明并重点解释，包括概念、原理、常见追问、易错点和结合本项目的回答方式。
- 按小功能推进，不要一次性生成整个项目。
- 开始新功能前先读取 `docs/PROJECT_STATUS.md`、相关源码和最近的 Git 提交。
- 不要重做 `docs/PROJECT_STATUS.md` 中已经完成的功能。
- 用户说“继续”时，从 `docs/PROJECT_STATUS.md` 的“当前阶段”继续。
- 第三方库、框架、SDK、API 或 CLI 的用法必须先查询当前官方文档。

## 代码约定

- 根包名为 `com.macro.mall.tiny`。
- UMS 模块按以下结构组织：

```text
modules/ums
├── controller
├── dto
├── mapper
├── model
└── service
    └── impl
```

- Controller 只负责参数接收、调用 Service 和组织响应。
- Service 负责业务逻辑，Mapper 负责数据库访问。
- 新建或修改 Controller、Service 接口和 ServiceImpl 实现类时，应在类或接口声明上方编写清晰的 JavaDoc，说明其职责；重要的公开方法也应补充 JavaDoc，说明参数、返回值和业务作用。
- 使用构造器注入，不使用字段注入。
- 对外响应使用 `CommonResult<T>`。
- 分页响应使用 `CommonPage<T>`。
- 不直接向 API 返回包含 `password` 的 `UmsAdmin`，使用 DTO。
- 数据库 `datetime` 字段优先映射为 `LocalDateTime`。
- MyBatis-Plus 查询优先使用 Lambda Wrapper，避免硬编码字段名。
- 当前项目使用 Java 17，可使用 `record`、Stream `toList()` 等语言特性。

## 安全要求

- 不得把数据库密码、JWT 密钥、Token、私钥或其他凭据提交到 Git。
- MySQL 密码通过环境变量 `MYSQL_PASSWORD` 传入。
- MySQL 用户名可通过 `MYSQL_USERNAME` 传入，默认值为 `root`。
- 日志和 API 响应不得输出密码明文或密码哈希。
- 新增注册和修改密码功能时必须使用 BCrypt。

## 配置与启动

PowerShell：

```powershell
$env:MYSQL_PASSWORD = "本机MySQL密码"
mvn spring-boot:run
```

编译：

```powershell
mvn clean compile
```

测试：

```powershell
mvn test
```

启动前确保：

- MySQL 服务已启动，`mall_tiny` 数据库已导入。
- Redis 服务已启动。
- 8080 端口未被其他程序占用。
- Maven 实际运行在 JDK 21，而不是 JDK 8。

## 验证要求

- 修改代码后至少运行与改动相称的编译或测试。
- 接口变更后给出可执行的 `curl.exe` 或 PowerShell 验证命令。
- 验证分页、参数边界、未找到数据和异常响应。
- 发现已有代码问题时先说明证据，再决定是否修改。

## Git 约定

- 一个完整功能模块完成并验证通过后，由 Codex 负责创建本地 Git 提交并推送到远程仓库；功能尚未完成时不提前提交半成品。
- 提交和推送前必须先获得或确认用户对该功能的完成认可，并确保只包含本功能及约定的文档变更。
- 提交前执行 `git status`、`git diff` 和 `git diff --cached`。
- 常用提交前缀：
  - `feat:` 新功能
  - `fix:` 修复
  - `docs:` 文档
  - `refactor:` 重构
  - `test:` 测试
  - `chore:` 配置或依赖
- 推送前确认没有敏感信息。
- 每完成一个阶段，同步更新 `docs/PROJECT_STATUS.md`。

## 重要文档

- `docs/PROJECT_STATUS.md`：当前进度、后续路线和已知问题。
- `docs/mall-tiny.md`：详细学习笔记。
- GitHub：<https://github.com/boringz-tiim/mall-tiny-rebuild>
