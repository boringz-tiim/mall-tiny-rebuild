# mall-tiny-rebuild 项目状态

最后更新：2026-08-13

## 项目目标

使用 Spring Boot 4、Java 17 和现代依赖，从零复现
[`macrozheng/mall-tiny`](https://github.com/macrozheng/mall-tiny)
的后台权限管理功能。

复现范围包括：

- 后台用户、角色、菜单和接口资源管理
- RBAC 权限模型
- JWT 登录认证
- Spring Security 动态授权
- Redis 用户与权限缓存
- OpenAPI 接口文档
- 测试、打包和运行说明

## 技术栈

| 组件 | 当前选择 |
|---|---|
| Java | Java 17，使用 JDK 21 编译 |
| Spring Boot | 4.1.0 |
| 构建工具 | Maven |
| 数据库 | MySQL，数据库名 `mall_tiny` |
| 缓存 | Redis |
| ORM | MyBatis-Plus 3.5.17 |
| 安全框架 | Spring Security 7 |
| 接口文档 | 计划使用 Springdoc OpenAPI |
| JWT | JJWT 0.13.0 |

## 已完成

### 环境与工程

- [x] 克隆原项目作为参考。
- [x] 从零创建 `mall-tiny-rebuild`。
- [x] 使用 Spring Boot 4.1.0 和 Java 17。
- [x] Maven 使用 JDK 21 编译。
- [x] 配置 MySQL 和 Redis。
- [x] 创建并导入 `mall_tiny` 数据库。
- [x] 使用环境变量 `MYSQL_PASSWORD`，不提交真实密码。
- [x] 项目已推送到 GitHub。

### 公共基础

- [x] `IErrorCode`
- [x] `ResultCode`
- [x] `CommonResult<T>`
- [x] `CommonPage<T>`
- [x] `ApiException`
- [x] `GlobalExceptionHandler`
- [x] 请求参数校验异常处理
- [x] 未知异常兜底处理

### MyBatis-Plus

- [x] 集成 `mybatis-plus-spring-boot4-starter` 3.5.17。
- [x] 集成分页所需的 `mybatis-plus-jsqlparser`。
- [x] 配置 MySQL 分页拦截器。
- [x] 配置下划线转驼峰和自增主键。
- [x] 配置 Mapper XML 扫描路径。

### 后台用户模块

- [x] 创建 `UmsAdmin` 实体。
- [x] 创建 `UmsAdminMapper`。
- [x] 创建 `UmsAdminService` 和实现类。
- [x] 创建 `UmsAdminController`。
- [x] 实现用户分页查询。
- [x] 支持用户名和昵称关键字搜索。
- [x] 校验 `pageNum` 和 `pageSize`。
- [x] 使用 `UmsAdminSummary`，避免返回密码哈希。
- [x] 根据 ID 获取用户详情，并处理用户不存在的情况。
- [x] 创建新增用户请求 DTO，并配置请求参数校验。
- [x] 使用 BCrypt 加密新增用户密码。
- [x] 创建后台用户，并校验用户名不能重复。

### 文档与 Git

- [x] 创建详细学习笔记 `docs/mall-tiny.md`。
- [x] 补充 Git、GitHub 和全局异常处理学习笔记 `docs/mall-tiny-2.md`。
- [x] 补充用户注册与 MyBatis-Plus 学习笔记 `docs/mall-tiny-rebuild-3.md`。
- [x] 补充 Base64、JWT 与认证过滤器学习笔记 `docs/mall-tiny-4.md`。
- [x] 建立 GitHub 仓库。
- [x] 提交用户分页功能。
- [x] 提交全局异常处理。
- [x] 创建项目协作说明 `AGENTS.md`。
- [x] 创建当前状态文档。

## 当前阶段

后台用户新增、登录、JWT 生成解析、基本请求认证、统一安全异常响应和
基础退出接口以及后台用户新增、查询、基本信息、状态、密码和删除功能已经
完成。自动化测试按当前学习安排暂时延期，当前正在实现角色模块，角色
基础分层、分页查询、新增、详情、修改、状态修改和删除已经完成；用户角色
关系实体、查询用户角色和重新分配用户角色也已经完成。
当前已进入菜单模块，菜单基础分层、按父级查询、CRUD 和菜单树已经完成。
角色菜单关系、查询角色菜单和重新分配角色菜单也已经完成。
当前已进入接口资源模块，`UmsResourceCategory` CRUD 已经完成，删除分类时会
检查关联资源并禁止删除非空分类。下一步实现 `UmsResource` CRUD 和分页。

推荐顺序：

1. 已禁用默认表单登录和 Basic 登录。
2. 已统一处理未登录响应，固定返回 HTTP 401 和 `CommonResult`。
3. 已统一处理无权限响应，固定返回 HTTP 403 和 `CommonResult`。
4. 登录、Token 解析和受保护接口自动化测试暂时延期，后续集中补充。
5. 已实现基础退出接口，并说明无状态 JWT 的退出语义。
6. 继续后台用户修改、状态、密码和删除功能。

## 后续路线

### 1. 后台用户模块

- [x] 根据 ID 获取用户详情
- [x] 新增用户
- [x] BCrypt 密码加密
- [x] 用户名唯一性校验
- [x] 修改用户基本信息
- [x] 修改用户状态
- [x] 修改密码
- [x] 删除用户（物理删除并事务清理用户角色关系）
- [ ] 用户接口测试

### 2. 角色模块

- [x] `UmsRole` 实体、Mapper、Service、Controller
- [x] 角色分页查询
- [x] 新增角色
- [x] 角色详情与修改
- [x] 角色 CRUD（删除时事务清理用户、菜单和资源关系）
- [x] `UmsAdminRoleRelation`
- [x] 给用户分配角色（事务执行旧关系删除与新关系写入）
- [x] 查询用户角色

### 3. 菜单模块

- [x] `UmsMenu` 实体、Mapper、Service、Controller
- [x] 菜单 CRUD（有子菜单时禁止删除，并事务清理角色菜单关系）
- [x] 构造菜单树
- [x] `UmsRoleMenuRelation`
- [x] 给角色分配菜单（事务执行旧关系删除与新关系写入）

### 4. 接口资源模块

- [x] `UmsResourceCategory` CRUD（删除非空分类时进行关联资源检查）
- [ ] `UmsResource` CRUD 和分页
- [ ] `UmsRoleResourceRelation`
- [ ] 给角色分配接口资源
- [ ] 根据用户查询接口权限

### 5. 登录与 JWT

- [x] 登录请求 DTO
- [x] 根据用户名加载用户
- [x] BCrypt 密码校验
- [x] 使用 JJWT 0.13.0 生成 Token
- [x] 解析和验证 Token
- [x] JWT 请求过滤器
- [x] 登录接口
- [x] 当前用户信息接口
- [x] 基础退出接口（客户端删除 Token，暂不支持服务端立即失效）

### 6. Spring Security 动态授权

- [x] 定义开发期公开接口白名单
- [x] 无状态 Session 配置
- [x] 禁用表单登录和默认 Basic 登录
- [x] 统一处理未登录响应
- [x] 统一处理无权限响应
- [ ] 根据 URL 动态判断资源权限
- [ ] 移除当前为调试而放行的受保护接口

### 7. Redis 缓存

- [ ] Redis 序列化配置
- [ ] 通用 Redis Service
- [ ] 缓存后台用户
- [ ] 缓存用户接口资源列表
- [ ] 用户、角色和资源变化时清理缓存
- [ ] 验证缓存命中和失效

### 8. OpenAPI、测试和交付

- [ ] 集成与 Boot 4 兼容的 Springdoc OpenAPI
- [ ] 配置 Bearer JWT 认证
- [ ] 补充 Controller、Service 和安全测试
- [ ] 完善 README
- [ ] 完成 Maven 打包
- [ ] 验证可执行 JAR
- [ ] 编写部署说明

## 已知问题与待清理事项

- 测试类目前仍位于：

```text
src/test/java/com/macro/malltiny/MallTinyRebuildApplicationTests.java
```

应移动到：

```text
src/test/java/com/macro/mall/tiny/MallTinyRebuildApplicationTests.java
```

并将包名改成：

```java
package com.macro.mall.tiny;
```

- `UmsAdminController` 中 `Page.convert(...)` 的结果存在不必要的显式强制转换，后续可清理。
- `application.properties` 可能为空；确认无用途后可以删除。
- 当前 `SecurityConfig` 仍为开发期临时配置，不能视为最终安全方案。
- 当前尚未建立完整自动化测试，完成用户 CRUD 后优先补充。
- 当前修改用户状态会阻止后续登录，但已签发的无状态 JWT 不会立即失效；
  后续结合 Redis、Token 版本或黑名单机制完善立即失效能力。
- 当前用户修改密码后，修改前已签发的 JWT 仍可使用至过期；后续与 Token
  版本或 Redis 失效机制一起完善。
- 当前用户删除采用物理删除，并在同一事务中清理 `ums_admin_role_relation`；
  登录日志作为审计数据保留。被删除用户已签发的 JWT 仍可使用至过期。
- 当前角色名称仅在应用层检查重复，数据库没有唯一索引，并发创建时仍可能
  产生重名角色；后续数据库迁移时应增加唯一约束。
- 当前角色删除采用物理删除，并在同一事务中清理
  `ums_admin_role_relation`、`ums_role_menu_relation` 和
  `ums_role_resource_relation` 中对应角色的关联记录。
- 当前用户角色分配以 `ums_admin_role_relation` 为真实数据来源，尚未同步维护
  `ums_role.admin_count` 冗余计数字段；后续统一决定动态统计或写入时同步。
- 当前修改菜单父级时会校验祖先链以防止形成环，并更新当前菜单的 `level`；
  已有后代菜单的 `level` 尚未递归同步，但菜单树仍以 `parent_id` 为准构造。

## 本地启动

PowerShell：

```powershell
cd D:\mall-tiny-rebuild\mall-tiny-rebuild
$env:MYSQL_PASSWORD = "本机MySQL密码"
mvn spring-boot:run
```

编译：

```powershell
mvn clean compile
```

检查 Redis：

```powershell
redis-cli -h 127.0.0.1 -p 6379 PING
```

预期：

```text
PONG
```

## 当前可验证接口

健康测试：

```text
GET http://localhost:8080/hello
```

用户分页：

```text
GET http://localhost:8080/admin/list?pageNum=1&pageSize=5
```

用户搜索：

```text
GET http://localhost:8080/admin/list?keyword=admin&pageNum=1&pageSize=5
```

用户详情：

```text
GET http://localhost:8080/admin/1
```

新增用户：

```text
POST http://localhost:8080/admin
Content-Type: application/json
```

用户登录：

```text
POST http://localhost:8080/admin/login
Content-Type: application/json
```

当前登录用户：

```text
GET http://localhost:8080/admin/me
Authorization: Bearer <token>
```

修改用户基本信息：

```text
PUT http://localhost:8080/admin/{id}
Authorization: Bearer <token>
Content-Type: application/json
```

修改用户状态：

```text
PATCH http://localhost:8080/admin/{id}/status
Authorization: Bearer <token>
Content-Type: application/json
```

当前用户修改密码：

```text
PATCH http://localhost:8080/admin/me/password
Authorization: Bearer <token>
Content-Type: application/json
```

删除用户：

```text
DELETE http://localhost:8080/admin/{id}
Authorization: Bearer <token>
```

查询用户角色：

```text
GET http://localhost:8080/admin/{id}/roles
Authorization: Bearer <token>
```

重新分配用户角色：

```text
PUT http://localhost:8080/admin/{id}/roles
Authorization: Bearer <token>
Content-Type: application/json
```

角色分页：

```text
GET http://localhost:8080/role/list?pageNum=1&pageSize=5
Authorization: Bearer <token>
```

新增角色：

```text
POST http://localhost:8080/role
Authorization: Bearer <token>
Content-Type: application/json
```

角色详情：

```text
GET http://localhost:8080/role/{id}
Authorization: Bearer <token>
```

修改角色：

```text
PUT http://localhost:8080/role/{id}
Authorization: Bearer <token>
Content-Type: application/json
```

修改角色状态：

```text
PATCH http://localhost:8080/role/{id}/status
Authorization: Bearer <token>
Content-Type: application/json
```

删除角色：

```text
DELETE http://localhost:8080/role/{id}
Authorization: Bearer <token>
```

按父级查询菜单：

```text
GET http://localhost:8080/menu/list/{parentId}
Authorization: Bearer <token>
```

菜单详情：

```text
GET http://localhost:8080/menu/{id}
Authorization: Bearer <token>
```

新增菜单：

```text
POST http://localhost:8080/menu
Authorization: Bearer <token>
Content-Type: application/json
```

修改菜单：

```text
PUT http://localhost:8080/menu/{id}
Authorization: Bearer <token>
Content-Type: application/json
```

删除菜单：

```text
DELETE http://localhost:8080/menu/{id}
Authorization: Bearer <token>
```

完整菜单树：

```text
GET http://localhost:8080/menu/tree
Authorization: Bearer <token>
```

查询角色菜单：

```text
GET http://localhost:8080/role/{id}/menus
Authorization: Bearer <token>
```

重新分配角色菜单：

```text
PUT http://localhost:8080/role/{id}/menus
Authorization: Bearer <token>
Content-Type: application/json
```

资源分类列表：

```text
GET http://localhost:8080/resource-category/list
Authorization: Bearer <token>
```

资源分类详情：

```text
GET http://localhost:8080/resource-category/{id}
Authorization: Bearer <token>
```

新增资源分类：

```text
POST http://localhost:8080/resource-category
Authorization: Bearer <token>
Content-Type: application/json
```

修改资源分类：

```text
PUT http://localhost:8080/resource-category/{id}
Authorization: Bearer <token>
Content-Type: application/json
```

删除资源分类：

```text
DELETE http://localhost:8080/resource-category/{id}
Authorization: Bearer <token>
```

参数校验：

```text
GET http://localhost:8080/admin/list?pageNum=0&pageSize=5
```

## 新对话交接提示

当前对话已完成用户、角色、菜单、资源分类及用户角色、角色菜单关系功能。
开启新对话继续开发时，应从接口资源模块的 `UmsResource` CRUD 和分页开始。工作区中
`UmsMenuService.java` 可能仅有未提交的文件末尾空行变化，处理前先检查差异，
不要把它误认为未完成的业务代码。

在新的 Codex 对话中使用：

```text
我正在从零复现 mall-tiny。

项目目录：
D:\mall-tiny-rebuild\mall-tiny-rebuild

请先阅读：
1. AGENTS.md
2. docs/PROJECT_STATUS.md
3. docs/mall-tiny.md
4. pom.xml
5. 最近的 Git 提交记录

请先检查当前源码和 git status，不要重做已经完成的功能。
按照 AGENTS.md 的协作方式，每一步解释目的、原理、代码职责和验证方法。

本次从 docs/PROJECT_STATUS.md 的“当前阶段”继续，下一步实现
UmsResource CRUD 和分页。项目业务代码由我亲手编写，请按步骤指导，
不要直接修改业务代码；项目状态文档、Git 提交和远程推送由你负责。
```

## GitHub

仓库地址：

<https://github.com/boringz-tiim/mall-tiny-rebuild>

