# mall-tiny 2

## Git 及 GitHub 提交

### 1. 移除配置中的真实密码

将 `application-dev.yml` 中的 MySQL 真实密码改成如下：

```yaml
spring:
  datasource:
    username: ${MYSQL_USERNAME:root}
    password: ${MYSQL_PASSWORD}
```

- `MYSQL_USERNAME`：从环境变量读取用户名。
- 如果没有用户名变量，默认使用 `root`。
- `MYSQL_PASSWORD`：必须从环境变量读取密码。
- GitHub 仓库中不保存真实密码。

本地启动前需要在 PowerShell 设置：

```powershell
$env:MYSQL_PASSWORD="真实密码"
mvn spring-boot:run
```

这个变量只在当前终端中生效。

如果使用 IDEA 绿色启动按钮，可在：

```text
Run
→ Edit Configurations
→ MallTinyRebuildApplication
→ Environment variables
```

添加：

```text
MYSQL_PASSWORD=你的真实密码
```

IDEA 运行配置通常保存在本地，不要主动共享包含密码的配置文件。

### 2. 检查 `.gitignore`

项目根目录应有 `.gitignore`。至少包含：

```text
target/
.idea/
*.iml
*.log
.env
application-local.yml
```

说明：

- `target/`：Maven 编译产物。
- `.idea/`、`*.iml`：IDEA 本地配置。
- `*.log`：运行日志。
- `.env`：可能保存环境变量和密码。
- `application-local.yml`：个人本地配置。

如果项目包含以下 Maven Wrapper 文件，应当提交，不要忽略：

```text
.mvn/
mvnw
mvnw.cmd
```

### 3. 初始化 Git 仓库

执行 `git status`，如果正常显示文件状态，说明创建项目时已经初始化 Git，不用重复操作。

如果出现 `fatal: not a git repository`，执行：

```text
git init
```

### 4. 暂存文件

执行：

```text
git add .
git status
```

`git add .` 不是提交，而是把当前项目快照放入暂存区。

仔细检查 `git status`，不应该出现：

```text
target/
.idea/
.env
```

然后检查即将提交的内容：

```text
git diff --cached
```

重点搜索确认没有：

- MySQL 真实密码。
- Token。
- 私钥。
- GitHub Personal Access Token。
- 其他账号凭据。

也可以单独检查配置文件：

```text
git diff --cached -- src/main/resources/application-dev.yml
```

如果发现密码，先修改文件，然后重新执行：

```text
git add src/main/resources/application-dev.yml
```

### 5. 创建本地提交

执行：

```text
git commit -m "feat: implement admin pagination"
```

提交信息含义：

- `feat`：新增功能。
- `implement admin pagination`：实现后台用户分页。

然后查看提交：

```text
git log --oneline -5
```

### 6. 将本地分支命名为 main

执行：

```text
git branch -M main
```

检查：

```text
git branch --show-current
```

预期：

```text
main
```

### 7. 在 GitHub 创建空仓库

登录 GitHub，点击：

```text
右上角 +
→ New repository
```

建议填写：

```text
Repository name: mall-tiny-rebuild
Description: Rebuilding mall-tiny with Spring Boot 4 and Java 17
Public / Private: 按你的需要选择
```

重要：不要勾选以下选项：

```text
Add a README file
Add .gitignore
Choose a license
```

### 8. 绑定 GitHub 远程仓库

GitHub 创建完成后会显示 HTTPS 地址，例如：

```text
https://github.com/你的用户名/mall-tiny-rebuild.git
```

先检查本地是否已有远程：

```text
git remote -v
```

如果没有任何输出，添加：

```text
git remote add origin https://github.com/你的用户名/mall-tiny-rebuild.git
```

如果提示：

```text
remote origin already exists
```

不要重复添加，改成：

```text
git remote set-url origin https://github.com/你的用户名/mall-tiny-rebuild.git
```

再次检查：

```text
git remote -v
```

### 9. 首次推送

执行：

```text
git push -u origin main
```

`-u` 表示将本地 `main` 与远程 `origin/main` 建立跟踪关系。以后只需要：

```text
git push
```

## 统一响应、错误码、自定义异常和全局异常处理

经典后端项目分层：

```text
com.xxx.project
|
├── common
│   |
│   ├── api
│   │    |
│   │    ├── IErrorCode.java
│   │    ├── ResultCode.java
│   │    └── CommonResult.java
│   │
│   └── exception
│        |
│        ├── ApiException.java
│        └── GlobalExceptionHandler.java
│
├── controller
├── service
├── mapper
├── entity
└── config
```

## 全局异常处理

### Java 原生异常以及 Spring Boot 异常处理机制

#### 1. 为什么需要异常处理

如果 Service 抛出异常（比如用户不存在），那么需要向上抛出：

```text
Service
   |
   ↓
Controller
   |
   ↓
Spring MVC
```

最后由 Spring 默认处理，但存在以下问题：

1. 前端看不懂。
2. 可能暴露内部信息。
3. 每个 Controller 都写 `try-catch` 很麻烦。

因此需要统一异常处理。

#### 2. `@ExceptionHandler`

`@ExceptionHandler` 用来指定一个方法处理某一种异常：

```java
@RestController
public class UserController {

    @ExceptionHandler(RuntimeException.class)
    public String handleException(RuntimeException exception) {
        return exception.getMessage();
    }
}
```

如果这个 Controller 中发生：

```java
throw new RuntimeException("用户不存在");
```

Spring 不会交给默认异常处理，而会进入 `handleException()`，返回：

```text
用户不存在
```

执行过程：

```text
请求
 |
Controller
 |
发生 RuntimeException
 |
Spring 寻找 @ExceptionHandler
 |
调用异常处理方法
 |
返回结果
```

但是项目通常有很多 Controller，每个都写 `@ExceptionHandler` 很麻烦，因此使用 `@RestControllerAdvice`。

#### 3. `@RestControllerAdvice`

`@RestControllerAdvice` 给所有 Controller 提供统一异常处理：

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public CommonResult<?> handle(RuntimeException exception) {
        return CommonResult.failed(exception.getMessage());
    }
}
```

现在任何 Controller 抛出的：

```java
throw new RuntimeException("失败");
```

都会进入统一的处理方法。

### 创建 `ApiException`

创建包：

```text
com.macro.mall.tiny.common.exception
```

创建 `ApiException`：

```java
package com.macro.mall.tiny.common.exception;

import com.macro.mall.tiny.common.api.IErrorCode;
import com.macro.mall.tiny.common.api.ResultCode;

public class ApiException extends RuntimeException {

    private final IErrorCode errorCode;

    public ApiException(IErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public ApiException(
            IErrorCode errorCode,
            String message
    ) {
        super(message);
        this.errorCode = errorCode;
    }

    public ApiException(String message) {
        super(message);
        this.errorCode = ResultCode.FAILED;
    }

    public IErrorCode getErrorCode() {
        return errorCode;
    }
}
```

业务代码以后可以直接抛出：

```java
throw new ApiException("用户不存在");
```

或者：

```java
throw new ApiException(ResultCode.FORBIDDEN);
```

业务层只负责说明“发生了什么”，由统一异常处理器负责转换成 HTTP 响应。

### 创建全局异常处理器 `GlobalExceptionHandler`

参数校验失败异常：

```java
@ExceptionHandler(MethodArgumentNotValidException.class)
```
