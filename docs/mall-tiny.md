# mall-tiny

## 新旧技术对应关系

| 原项目                       | 我们的新项目                           |
| ---------------------------- | -------------------------------------- |
| Java 8                       | Java 17                                |
| Spring Boot 2.7.5            | Spring Boot 4.1.0                      |
| `javax.*`                    | `jakarta.*`                            |
| Springfox Swagger            | Springdoc OpenAPI                      |
| 旧版 Spring Security 配置    | `SecurityFilterChain` 配置             |
| `antMatchers`                | `requestMatchers`                      |
| `mysql:mysql-connector-java` | `com.mysql:mysql-connector-j`          |
| JJWT 0.9.1                   | 新版 JJWT                              |
| `spring.redis.*`             | `spring.data.redis.*`                  |
| `spring-boot-starter-web`    | Boot 4 的 `spring-boot-starter-webmvc` |

## 哪些东西保持不变

以下核心设计仍然可以完整复现：

- MySQL 的 9 张权限管理表；
- 用户、角色、菜单和接口资源；
- 用户与角色多对多关系；
- 角色与菜单多对多关系；
- 角色与接口资源多对多关系；
- MyBatis-Plus 数据访问；
- JWT 登录认证；
- Spring Security 接口授权；
- Redis 缓存用户权限；
- 统一响应结果；
- 全局异常处理；
- 参数校验；
- Swagger/OpenAPI 接口文档。

## 当前还需加入的三个基础依赖

在 `<dependencies>` 中加入：

```
<!-- Jakarta Bean Validation 参数校验 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>

<!-- Spring AOP -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-aop</artifactId>
</dependency>

<!-- 健康检查和应用监控 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

它们分别用于：

- `validation`：校验用户名、密码等请求参数；
- `aop`：支持切面和横切逻辑；
- `actuator`：提供健康检查和运行状态接口。

MyBatis-Plus、OpenAPI、JWT 等依赖先不要随便指定版本。Boot 4 比较新，我们在真正整合每个组件时，分别选与 Boot 4 兼容的版本并验证。

## 统一响应接口

这里声明的是枚举：

```
public enum ResultCode
```

`enum` 叫做枚举，适合表示一组固定值。

例如星期：

```
public enum Week {
    MONDAY,
    TUESDAY,
    WEDNESDAY
}
```

而你的 `ResultCode` 表示一组固定的业务结果：

```
SUCCESS
FAILED
VALIDATE_FAILED
UNAUTHORIZED
FORBIDDEN
```

这些结果通常不会随便增加或修改，所以很适合用枚举。

```
public enum ResultCode implements IErrorCode
```

表示这个枚举实现了 `IErrorCode` 接口。

这个接口大概是：

```java
public interface IErrorCode {

    long getCode();

    String getMessage();
}
```

也就是说，只要实现 `IErrorCode`，就必须提供：

```java
getCode()
getMessage()
```

你的枚举下面正好实现了这两个方法：

```java
@Override
public long getCode() {
    return code;
}

@Override
public String getMessage() {
    return message;
}
```

## 5. 构造方法

```
ResultCode(long code, String message) {
    this.code = code;
    this.message = message;
}
```

这是枚举的构造方法。

当写：

```
SUCCESS(200, "操作成功")
```

就会自动调用这个构造方法，把：

```
200
```

赋给：

```
this.code
```

把：

```
"操作成功"
```

赋给：

```
this.message
```

枚举的构造方法默认就是私有的，不能在其他类里直接：

```
new ResultCode(...)
```

## 5.4 暂时不加入 Druid

原项目使用 Druid 连接池，但 Spring Boot 默认提供 HikariCP。

当前先使用 HikariCP，原因是：

- Spring Boot 原生管理；
- 与 Boot 4 兼容稳定；
- 不影响 MyBatis-Plus 功能；
- 可以先把数据库访问跑通。

这属于“实现相同功能时采用现代默认组件”。以后确实需要 Druid 监控页面时，再单独整合。

##  创建 Mapper

在 `mapper` 中创建 `UmsAdminMapper.java`：

```
package com.macro.mall.tiny.modules.ums.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.macro.mall.tiny.modules.ums.model.UmsAdmin;

public interface UmsAdminMapper extends BaseMapper<UmsAdmin> {
}
```

虽然接口中没有写方法，但继承 `BaseMapper<UmsAdmin>` 后，自动获得：

```
selectById()
selectList()
insert()
updateById()
deleteById()
```

MyBatis-Plus 会在运行时为这个接口创建代理实现，不需要手写实现类。

这段继承关系表示：

```
ServiceImpl<使用哪个Mapper, 操作哪个实体>
```

也就是：

```
ServiceImpl<UmsAdminMapper, UmsAdmin>
```





```java
//Java17 的record写法用来定义一个只负责承载数据的DTO
//相当于一个不可变数据类，java会自动生成全参数构造函数，所有字段的访问方法，
//一个不包含密码的摘要DTO UmsAdminSummary
public record UmsAdminSummary (
        Long id,
        String username,
        String icon,
        String email,
        String nickName,
        String note,
        LocalDateTime createTime,
        LocalDateTime loginTime,
        Integer status
){
    public static UmsAdminSummary from(UmsAdmin admin){
        return new UmsAdminSummary(
                admin.getId(),
                admin.getUsername(),
                admin.getIcon(),
                admin.getEmail(),
                admin.getNickName(),
                admin.getNote(),
                admin.getCreateTime(),
                admin.getLoginTime(),
                admin.getStatus()
        );
    }


}

```





## record 的访问方法

传统类通常使用：

```
summary.getId();
summary.getUsername();
```

record 自动生成的方法不带 `get`：

```
summary.id();
summary.username();
summary.nickName();
```

但 `from()` 方法中的参数 `admin` 是传统的 `UmsAdmin` 类，所以使用：

```
admin.getId();
admin.getUsername();
```

## `from()` 是什么

```
public static UmsAdminSummary from(UmsAdmin admin)
```

这是一个静态工厂方法，负责把 `UmsAdmin` 转换为 `UmsAdminSummary`。



# static

# Java `static` 与 `record` 学习笔记

## 一、`static` 是什么

`static` 是 Java 的关键字，表示成员属于“类本身”，而不是某个具体对象。

`static` 可以修饰：

- 成员变量
- 方法
- 代码块
- 内部类

## 二、普通方法与静态方法

### 2.1 普通实例方法

~~~java
public class Calculator {

    public int add(int a, int b) {
        return a + b;
    }
}
~~~

普通方法属于对象，调用前必须创建对象：

~~~java
Calculator calculator = new Calculator();
int result = calculator.add(1, 2);
~~~

### 2.2 静态方法

~~~java
public class Calculator {

    public static int add(int a, int b) {
        return a + b;
    }
}
~~~

静态方法属于类，可以直接使用类名调用：

~~~java
int result = Calculator.add(1, 2);
~~~

不需要提前创建 `Calculator` 对象。

## 三、静态变量

普通成员变量由每个对象分别保存：

~~~java
public class User {

    private String username;
}
~~~

静态变量由整个类共享：

~~~java
public class User {

    public static int count = 0;

    public User() {
        count++;
    }
}
~~~

使用：

~~~java
new User();
new User();

System.out.println(User.count); // 2
~~~

无论创建多少个 `User` 对象，静态变量 `count` 都只有一份。

## 四、静态方法的限制

静态方法没有对应的具体对象，因此不能直接访问实例字段，也不能使用 `this`。

错误示例：

~~~java
public class User {

    private String username;

    public static void printUsername() {
        System.out.println(username); // 编译错误
    }
}
~~~

Java 不知道要访问哪个 `User` 对象的 `username`。

可以通过参数接收对象：

~~~java
public static void printUsername(User user) {
    System.out.println(user.username);
}
~~~

## 五、为什么 `from()` 使用 `static`

~~~java
public static UmsAdminSummary from(UmsAdmin admin) {
    return new UmsAdminSummary(
            admin.getId(),
            admin.getUsername(),
            admin.getIcon(),
            admin.getEmail(),
            admin.getNickName(),
            admin.getNote(),
            admin.getCreateTime(),
            admin.getLoginTime(),
            admin.getStatus()
    );
}
~~~

调用方式：

~~~java
UmsAdminSummary summary = UmsAdminSummary.from(admin);
~~~

可以理解为：

> 使用 `UmsAdminSummary` 类提供的 `from()` 方法，根据 `admin` 创建一个摘要对象。

这种方法称为静态工厂方法。

如果 `from()` 不是静态方法，就必须先创建对象才能调用：

~~~java
UmsAdminSummary oldSummary = ...;
UmsAdminSummary newSummary = oldSummary.from(admin);
~~~

但 `from()` 的目的就是创建对象，因此设计成静态方法更加合理。

---

# 六、`record` 是什么

`record` 是 Java 提供的一种特殊数据类，适合表示一组固定的数据。

~~~java
public record User(
        Long id,
        String username
) {
}
~~~

它特别适合：

- DTO
- API 响应对象
- 查询结果
- 配置数据
- 方法之间传递的数据

## 七、record 自动生成的内容

定义以下 record：

~~~java
public record User(
        Long id,
        String username
) {
}
~~~

Java 会自动生成以下内容。

### 7.1 私有 final 字段

概念上相当于：

~~~java
private final Long id;
private final String username;
~~~

### 7.2 全参数构造方法

~~~java
User user = new User(1L, "admin");
~~~

### 7.3 字段访问方法

record 的访问方法与字段同名：

~~~java
Long id = user.id();
String username = user.username();
~~~

不是传统 JavaBean 的写法：

~~~java
user.getId();
user.getUsername();
~~~

### 7.4 `equals()` 和 `hashCode()`

两个相同类型的 record，如果所有数据相等，那么两个对象相等：

~~~java
User user1 = new User(1L, "admin");
User user2 = new User(1L, "admin");

System.out.println(user1.equals(user2)); // true
~~~

### 7.5 `toString()`

~~~java
System.out.println(user);
~~~

输出类似：

~~~text
User[id=1, username=admin]
~~~

## 八、record 的基本使用

### 8.1 创建对象

~~~java
UmsAdminSummary summary = new UmsAdminSummary(
        3L,
        "admin",
        "https://example.com/icon.png",
        "admin@example.com",
        "系统管理员",
        "管理员账号",
        LocalDateTime.now(),
        LocalDateTime.now(),
        1
);
~~~

### 8.2 读取数据

~~~java
Long id = summary.id();
String username = summary.username();
String nickName = summary.nickName();
~~~

### 8.3 作为接口返回值

~~~java
@GetMapping("/{id}")
public CommonResult<UmsAdminSummary> getAdmin(@PathVariable Long id) {
    UmsAdmin admin = adminMapper.selectById(id);
    UmsAdminSummary summary = UmsAdminSummary.from(admin);
    return CommonResult.success(summary);
}
~~~

返回的 JSON 类似：

~~~json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 3,
    "username": "admin",
    "email": "admin@example.com",
    "nickName": "系统管理员",
    "status": 1
  }
}
~~~

## 九、record 创建后不能修改

record 不会生成 setter：

~~~java
summary.setUsername("newName"); // 不存在
~~~

如果需要修改数据，应创建一个新对象：

~~~java
UmsAdminSummary newSummary = new UmsAdminSummary(
        summary.id(),
        "newName",
        summary.icon(),
        summary.email(),
        summary.nickName(),
        summary.note(),
        summary.createTime(),
        summary.loginTime(),
        summary.status()
);
~~~

因此 record 适合“创建后只读取”的数据。

record 是浅不可变的。如果字段引用的是可变集合，集合内部的数据仍然可能被修改。

## 十、record 中添加方法

### 10.1 静态工厂方法

~~~java
public record User(
        Long id,
        String username
) {

    public static User from(UmsAdmin admin) {
        return new User(
                admin.getId(),
                admin.getUsername()
        );
    }
}
~~~

调用：

~~~java
User user = User.from(admin);
~~~

### 10.2 普通实例方法

~~~java
public record User(
        Long id,
        String username
) {

    public String displayName() {
        return id + " - " + username;
    }
}
~~~

调用：

~~~java
User user = new User(1L, "admin");
String result = user.displayName();
~~~

### 10.3 紧凑构造器

record 可以在创建对象时校验参数：

~~~java
public record User(
        Long id,
        String username
) {

    public User {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("用户名不能为空");
        }
    }
}
~~~

创建非法对象：

~~~java
new User(1L, "");
~~~

会抛出异常。

## 十一、record 与传统类的对比

record：

~~~java
public record User(
        Long id,
        String username
) {
}
~~~

大致相当于：

~~~java
public final class User {

    private final Long id;
    private final String username;

    public User(Long id, String username) {
        this.id = id;
        this.username = username;
    }

    public Long id() {
        return id;
    }

    public String username() {
        return username;
    }

    @Override
    public boolean equals(Object object) {
        // Java 自动生成
    }

    @Override
    public int hashCode() {
        // Java 自动生成
    }

    @Override
    public String toString() {
        // Java 自动生成
    }
}
~~~

record 可以减少大量只用于承载数据的样板代码。

## 十二、record 与 Lombok 的对比

### 12.1 record

~~~java
public record User(
        Long id,
        String username
) {
}
~~~

特点：

- Java 原生功能
- 不需要额外依赖
- 字段不可重新赋值
- 自动生成构造方法和常用方法
- 访问方法为 `id()`、`username()`

### 12.2 Lombok `@Value`

~~~java
@Value
public class User {

    Long id;
    String username;
}
~~~

特点：

- 依赖 Lombok
- 表现接近不可变对象
- 通常生成 `getId()`、`getUsername()`

### 12.3 Lombok `@Data`

~~~java
@Data
public class User {

    private Long id;
    private String username;
}
~~~

特点：

- 对象可以修改
- 自动生成 getter 和 setter
- 适合需要赋值的 MyBatis 实体

## 十三、record 的版本历史

| Java 版本      | 状态                   |
| -------------- | ---------------------- |
| Java 14        | 第一次作为预览功能引入 |
| Java 15        | 第二次预览             |
| Java 16        | 成为正式语言功能       |
| Java 17 及以上 | 可以直接稳定使用       |

相关规范：

- [JEP 359：Java 14 Records Preview](https://openjdk.org/jeps/359)
- [JEP 384：Java 15 Records Second Preview](https://openjdk.org/jeps/384)
- [JEP 395：Java 16 Records](https://openjdk.org/jeps/395)

当前项目使用 Java 17，因此可以直接使用 record，不需要开启预览功能。

## 十四、在项目中的实际含义

~~~java
public record UmsAdminSummary(...) {

    public static UmsAdminSummary from(UmsAdmin admin) {
        return new UmsAdminSummary(...);
    }
}
~~~

其中：

~~~text
record
→ UmsAdminSummary 是一个以数据为核心的不可变 DTO

static
→ from() 属于 UmsAdminSummary 类本身

from(admin)
→ 将数据库实体 UmsAdmin 转换成接口响应 DTO
~~~

使用：

~~~java
UmsAdmin admin = adminMapper.selectById(3L);
UmsAdminSummary summary = UmsAdminSummary.from(admin);
~~~

采用 DTO 的重要目的之一，是避免直接返回数据库实体中的敏感字段，例如用户密码。



# 构造器注入以及Autowired

你之前学的 `@Autowired` 写法没有错。只是 Spring 后来简化了构造器注入。

## 你之前学的可能是字段注入

```
@RestController
public class UmsAdminController {

    @Autowired
    private UmsAdminService adminService;
}
```

Spring 会在创建 Controller 后，把匹配的 Service 对象写入字段。

这种写法现在仍然能运行。

## 当前代码是构造器注入

```
@RestController
public class UmsAdminController {

    private final UmsAdminService adminService;

    public UmsAdminController(UmsAdminService adminService) {
        this.adminService = adminService;
    }
}
```

Spring 创建 Controller 时，会自动调用这个构造器：

```
UmsAdminService service = 找到的Service对象;
new UmsAdminController(service);
```

## 构造器也可以写 `@Autowired`

下面这样完全正确：

```
@RestController
public class UmsAdminController {

    private final UmsAdminService adminService;

    @Autowired
    public UmsAdminController(UmsAdminService adminService) {
        this.adminService = adminService;
    }
}
```

但是，因为这个类只有一个构造器，Spring 能确定应该调用哪个构造器，所以 `@Autowired` 可以省略。

以下两种写法效果相同。

### 显式写 `@Autowired`

```
@Autowired
public UmsAdminController(UmsAdminService adminService) {
    this.adminService = adminService;
}
```

### 省略 `@Autowired`

```
public UmsAdminController(UmsAdminService adminService) {
    this.adminService = adminService;
}
```

Spring 从 4.3 开始支持单构造器自动注入。你当前的 Spring Boot 4 项目使用的是更新的 Spring Framework，自然支持这种写法。

## 什么时候还需要 `@Autowired`

如果类有多个构造器，Spring可能无法判断应该使用哪一个：

```
public class UmsAdminController {

    private final UmsAdminService adminService;

    public UmsAdminController() {
        this.adminService = null;
    }

    public UmsAdminController(UmsAdminService adminService) {
        this.adminService = adminService;
    }
}
```

这时可以用 `@Autowired` 明确指定：

```
@Autowired
public UmsAdminController(UmsAdminService adminService) {
    this.adminService = adminService;
}
```

不过一般不建议无缘无故给 Spring Bean 写多个构造器。

## 为什么现在更推荐构造器注入

### 1. 可以使用 `final`

```
private final UmsAdminService adminService;
```

创建后不能把它替换成其他对象：

```
this.adminService = anotherService; // 编译错误
```

### 2. 保证依赖不缺失

要创建 Controller，就必须传入 Service：

```
new UmsAdminController(service);
```

不能创建一个缺少 Service 的不完整 Controller。

### 3. 更方便测试

```
UmsAdminService mockService = mock(UmsAdminService.class);

UmsAdminController controller =
        new UmsAdminController(mockService);
```

不启动 Spring 也能创建 Controller 进行测试。

### 4. 依赖更明确

看到构造器就知道这个类需要什么：

```
public UmsAdminController(
        UmsAdminService adminService,
        UmsRoleService roleService
) {
}
```

字段注入则要进入类内部寻找所有 `@Autowired` 字段。

## Lombok 还能进一步简写

原本：

```
@RestController
public class UmsAdminController {

    private final UmsAdminService adminService;

    public UmsAdminController(UmsAdminService adminService) {
        this.adminService = adminService;
    }
}
```

使用 Lombok：

```
@RestController
@RequiredArgsConstructor
public class UmsAdminController {

    private final UmsAdminService adminService;
}
```

`@RequiredArgsConstructor` 会为所有需要初始化的 `final` 字段生成构造器，效果相同。

目前建议你先保留手写构造器，这样更容易理解依赖注入过程。等熟悉后再使用 Lombok 简写。

结论：

> `@Autowired` 没有失效。单构造器场景下 Spring 能自动识别，所以可以省略；当前写法是更推荐的构造器注入。



# 流式代码和传统for循环

整段流式代码：

```java
List<UmsAdminSummary> result=adminService.listAll()
    .stream()
    .map(UmsAdminSummary::from)//map()表示把流中的每个元素转换成另一种元素，
    //当前转换： UmsAdmin->UmsAdminSummary
    .toList();//把数据库实体列表逐个转换成安全的DTO列表
return CommonResult.success(result);
```

对流中的每个UmsAdmin,调用UmsAdminSummary.from()将其转换成UmsAdminSummary

```java
public static UmsAdminSummary from(UmsAdmin admin){
    return new UmsAdminSummary(
            admin.getId(),
            admin.getUsername(),
            admin.getIcon(),
            admin.getEmail(),
            admin.getNickName(),
            admin.getNote(),
            admin.getCreateTime(),
            admin.getLoginTime(),
            admin.getStatus()
    );
}
```

等价于传统for循环

```java
List<UmsAdmin> adminList = adminService.listAll();
List<UmsAdminSummary> result= new ArrayList<>();
for(UmsAdmin admin:adminList){
    UmsAdminSummary summary=UmsAdminSummary.from(admin);
    result.add(summary);
}
```

```java
@GetMapping("/list")
public CommonResult<List<UmsAdminSummary>> list() {
    List<UmsAdmin> adminList = adminService.listAll();
    List<UmsAdminSummary> result = new ArrayList<>();

    for (UmsAdmin admin : adminList) {
        UmsAdminSummary summary = UmsAdminSummary.from(admin);
        result.add(summary);
    }

    return CommonResult.success(result);
}
```

# 分页

```text
PageHelper
→ 独立的 MyBatis 分页插件

PaginationInnerInterceptor
→ MyBatis-Plus 自己的分页插件

mybatis-plus-jsqlparser
→ MyBatis-Plus 分页插件需要的 SQL 解析模块
```

## PageHelper使用

```java
PageHelper.startPage(pageNum.pageSize);
List<UmsAdmin> adminList=umsAdminMapper.selectList();
PageInfo<UmsAdmin> pageInfo=new PageInfo<>(adminList);
```

PageHelper 通常要求分页调用紧挨着查询：

```java
PageHelper.startPage(pageNum, pageSize);
List<UmsAdmin> list = mapper.selectList();
```

如果中间执行了其他查询，可能分页到错误的 SQL。

## MyBatis-Plus怎么进行分页

MyBatis-Plus使用Page对象传递分页参数

这个Page是MyBatis-Plus提供的

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

```java
//创建一个分页对象page传递给Mapper,MyBatis-Plus根据这个对象自动生成分页SQL，然后返回一个带分页信息的Page对象
Page<UmsAdmin> page=new Page<>(pageNum,pageSize);
//selectPage（）是MyBatis-Plus提供的方法，不是自己写的SQL
Page<UmsAdmin>result=umsAdminMapper.selectPage(page,null);
```

自己的Mapper

```java
public interface UmsAdminMapper extends BaseMapper<UmsAdmin>{
    //继承BaseMapper，自动拥有
    //selectById()
    //insert()
    //updateById()
    //deleteById()
    //selectPage()
    
}
```



比如：

```java
Page<UmsAdmin> page = new Page<>(2,10);
Page<UmsAdmin> result=umsAdminMapper.selectPage(page,null);
```

## 3. `selectPage(page,null)`是什么意思？

方法定义类似：

```
Page<T> selectPage(
    Page<T> page,
    Wrapper<T> queryWrapper
);
```

两个参数：

### 第一个参数

```
page
```

分页信息：

```
第几页
每页多少条
```

------

### 第二个参数

```
null
```

查询条件。

比如：

查询所有管理员：

```
selectPage(page,null)
```

相当于：

```
select *
from ums_admin
limit 10,10;
```

------

如果想查询用户名包含 admin：

```
QueryWrapper<UmsAdmin> wrapper =
        new QueryWrapper<>();

wrapper.like("username","admin");


Page<UmsAdmin> result =
        umsAdminMapper.selectPage(page,wrapper);
```

对应 SQL：

```
select *
from ums_admin
where username like '%admin%'
limit 10,10;
```

结果中可以获得：

```
result.getRecords(); // 当前页数据
result.getTotal();   // 总记录数
result.getPages();   // 总页数
result.getCurrent(); // 当前页
result.getSize();    // 每页数量
```

MyBatis-Plus 通常执行两条 SQL。

第一条查询总数量：

```
SELECT COUNT(*)
FROM ums_admin;
```

第二条查询当前页：

```
SELECT *
FROM ums_admin
LIMIT 10, 10;
```

## `mybatis-plus-jsqlparser` 是什么

这个依赖：

```
<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-jsqlparser</artifactId>
    <version>3.5.17</version>
</dependency>
```

不是让我们直接调用的分页工具。

它负责帮助 MyBatis-Plus理解和改写 SQL。

例如原始 SQL：

```
SELECT *
FROM ums_admin
WHERE status = 1
ORDER BY create_time DESC;
```

分页插件需要把它改造成：

```
SELECT *
FROM ums_admin
WHERE status = 1
ORDER BY create_time DESC
LIMIT 0, 10;
```

还可能需要生成数量查询：

```
SELECT COUNT(*)
FROM ums_admin
WHERE status = 1;
```

为了安全、正确地处理复杂 SQL，不能只在字符串末尾随便拼接 `LIMIT`，所以需要 SQL 解析器理解：

- `SELECT`
- `FROM`
- `WHERE`
- `JOIN`
- `ORDER BY`
- 子查询
- 表名和字段名

`mybatis-plus-jsqlparser` 就是提供这部分 SQL 解析能力。

从 MyBatis-Plus 3.5.9 开始，分页相关的 SQL 解析支持被拆成了独立依赖，需要使用分页插件的项目单独引入。[MyBatis-Plus 分页插件文档](https://github.com/baomidou/mybatis-plus-doc/blob/master/src/content/docs/en/plugins/pagination.mdx)

| 对比             | PageHelper               | MyBatis-Plus 分页       |
| ---------------- | ------------------------ | ----------------------- |
| 所属项目         | 独立第三方分页插件       | MyBatis-Plus 内置插件   |
| 分页入口         | `PageHelper.startPage()` | `new Page<>(...)`       |
| 返回包装         | `PageInfo`               | `Page` / `IPage`        |
| 参数保存方式     | 通常依赖紧接着执行的查询 | 分页对象显式传入 Mapper |
| 是否需要拦截 SQL | 是                       | 是                      |
| 是否查询总数     | 通常会                   | 默认会                  |
| 是否支持 MyBatis | 是                       | 是，基于 MyBatis-Plus   |
| 当前项目是否推荐 | 不需要                   | 推荐                    |

### 两种写法的对照

PageHelper

```java
PageHelper.startPage(pageNum,pageSize);
List<UmsAdmin> list=umsAdminMapper.selectList();
PageInfo<UmsAdmin> result=new PageInfo<>(list);
```

完整例子：

```java
@GetMapping("/admins")
public CommonResult<PageInfo<UmsAdmin>>list(@RequestParam Integer pageNum,@RequestParam Integer pageSize){
    PageHelper.startPage(pageNum,pageSize);
    List<UmsAdmin> list=umsAdminMapper.selectList();
    PageInfo<UmsAdmin>pageInfo=new PageInfo<>(list);
    return CommonResult.success(pageInfo);
}
```

访问：http://localhost:8080/admin?pageNum=1&pageSize=10

返回：

```java
{
 "code":200,
 "message":"操作成功",
 "data":{
    "pageNum":1,
    "pageSize":10,
    "total":56,
    "pages":6,
    "list":[
       {
          "id":1,
          "username":"admin"
       }
    ]
 }
}
```

### 开启分页

```
PageHelper.startPage(pageNum,pageSize);
```

### 查询

```
List<User> list = mapper.xxx();
```

### 包装结果

```
PageInfo<User> pageInfo = new PageInfo<>(list);
```

### 返回

```
return CommonResult.success(pageInfo);
```

- **`PageHelper`**：负责“分页拦截”

- **`PageInfo`**：负责“分页结果包装”

# 4. PageHelper 和 MyBatis-Plus区别

你前面那个：

### PageHelper

代码：

```
PageHelper.startPage(1,10);

List<UmsAdmin> list =
        mapper.selectList();

PageInfo<UmsAdmin> pageInfo =
        new PageInfo<>(list);
```

流程：

```
开始分页
 ↓
执行普通查询
 ↓
PageHelper拦截SQL
 ↓
自动加limit
 ↓
PageInfo包装结果
```

特点：

- 不改变 Mapper
- 适合 MyBatis 原生项目

------

### MyBatis-Plus

代码：

```
Page<UmsAdmin> page =
        new Page<>(1,10);

Page<UmsAdmin> result =
        mapper.selectPage(page,null);
```

流程：

```
创建Page对象
 ↓
调用selectPage
 ↓
MP生成分页SQL
 ↓
返回Page对象
```

特点：

- Mapper继承BaseMapper
- 自带CRUD
- 更符合快速开发

## 分页开发示例

# MyBatis-Plus完整例子

### Mapper

```java
@Mapper
public interface UmsAdminMapper 
        extends BaseMapper<UmsAdmin> {

}
```

------

### Service

```java
public Page<UmsAdmin> list(
        Integer pageNum,
        Integer pageSize
){

    Page<UmsAdmin> page =
            new Page<>(pageNum,pageSize);

    return umsAdminMapper.selectPage(
            page,
            null
    );
}
```

------

### Controller

```java
@GetMapping("/list")
public CommonResult<Page<UmsAdmin>> list(
        Integer pageNum,
        Integer pageSize
){

    Page<UmsAdmin> page =
            adminService.list(pageNum,pageSize);

    return CommonResult.success(page);
}
```

返回：

```json
{
 "code":200,
 "message":"操作成功",
 "data":{
    "current":1,
    "size":10,
    "total":100,
    "records":[
       {
          "id":1,
          "username":"admin"
       }
    ]
 }
}
```

------

简单总结：

|            | PageHelper               | MyBatis-Plus      |
| ---------- | ------------------------ | ----------------- |
| 分页对象   | `PageInfo`               | `Page`            |
| 分页开始   | `PageHelper.startPage()` | `new Page()`      |
| 查询方法   | 普通 Mapper 方法         | `selectPage()`    |
| SQL生成    | 拦截 SQL                 | MP 自动生成       |
| Mapper要求 | 普通 Mapper              | 继承 `BaseMapper` |