# mall-tiny-rebuild 3

## IService<> 

IService<UmsAdmin>是MyBatis-Plus提供的通用Service接口

### 1.IService是什么

IService<T>内置了很多常用的数据库操作

```java
getById(id)  //根据主键查询
list() 		//查询列表
save(entity) //新增
updateById(entity)//根据主键修改
removeById(id)//根据主键删除
page(page)	//分页查询
```

其中 `T` 表示这些方法操作的实体类型。

项目中写成：

```
IService<UmsAdmin>
```

表示这些通用方法操作的是 `UmsAdmin` 实体。

因此可以调用：

```java
UmsAdmin admin=adminService.getById(1L);
//返回类型是UmsAdmin,
```

## Jakarta Validation参数校验注解，用来规定前端传入的数据必须满足的条件

### @NotBlank

```java
@NoteBlank(message="用户名不能为空")
String username
```

表示username:不能是null,不能是空字符串,不能全是空格

### @Size

```java
@Size(min=4,max=64,message="用户名长度必须在4到64个字符之间")
String username
```

### @Email

```java
@Email(message="邮箱格式不正确")
String email
```

```java
@NotBlank(message = "邮箱不能为空")
@Email(message = "邮箱格式不正确")
@Size(max = 100, message = "邮箱不能超过100个字符")
String email
```

### 这些注解什么时候会生效？

只在DTO上写注解还不够，一版还要在Controller参数前加上 @Valid

```java
@PostMapping("/register")
public CommonResult<?> register(
        @Valid @RequestBody UmsAdminCreateRequest request) {

    return CommonResult.success(null);
}
```

执行流程：

```java
前端提交 JSON
        ↓
@RequestBody 转换成 UmsAdminCreateRequest
        ↓
@Valid 启动参数校验
        ↓
检查 @NotBlank、@Size、@Email
        ↓
校验失败，抛出 MethodArgumentNotValidException
```

失败会产生MethodArgumentNoteValidException，然后就会被全局异常处理器捕获

```java
@ExceptionHandler(MethodArgumentNotValidException.class)
public CommonResult<?> handleValidException(
        MethodArgumentNotValidException e) {

    String message = e.getBindingResult()
            .getFieldError()
            .getDefaultMessage();

    return CommonResult.failed(message);
}
```

## 在Spring容器中注册一个密码加密器对象

```java
@Bean
public PasswordEncoder passwordEncoder(){
    return new BCryptPasswordEncoder();
    //Spring启动时会创建一个BCryptPasswordEncoder对象，并把它保存到Ioc容器中
}
```

以后其他类需要密码加密器时，不必自己写：

```
new BCryptPasswordEncoder()
```

而是直接让 Spring 注入：

```
@Service
public class UmsAdminServiceImpl {

    private final PasswordEncoder passwordEncoder;

    public UmsAdminServiceImpl(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }
}
```

## 2. `PasswordEncoder` 是什么？

`PasswordEncoder` 是 Spring Security 提供的密码处理接口。

它常用的方法有两个：

```
String encode(CharSequence rawPassword);
```

用于对原始密码进行加密。

```
boolean matches(
        CharSequence rawPassword,
        String encodedPassword
);
```

用于判断用户输入的密码和数据库中的加密密码是否匹配。

------

## 3. `BCryptPasswordEncoder` 是什么？

它是 `PasswordEncoder` 的一个实现类，使用 BCrypt 算法处理密码：

```
PasswordEncoder passwordEncoder =
        new BCryptPasswordEncoder();
```

关系大致是：

```
PasswordEncoder 接口
        ↑
BCryptPasswordEncoder 实现类
```

这里使用接口作为返回类型：

```
public PasswordEncoder passwordEncoder()
```

实际返回的是实现类：

```
return new BCryptPasswordEncoder();
```

这属于面向接口编程。

------

## 4. 注册用户时怎么使用？

假设用户输入密码：

```
123456
```

不能直接把明文密码存进数据库，应该先加密：

```
String encodedPassword =
        passwordEncoder.encode("123456");
```

得到的结果可能类似：

```
$2a$10$g0nX...一长串字符
```

然后把这串加密结果存进数据库。

## 用户名已存在判断

```java
long sameUsernameCount = count(
	Wrappers.<UmsAdmin>lambdaQuery()
    .eq(
    	UmsAdmin::getUsername,
        umsAdminCreateRequest.username()
    )
);
```

#### 获取用户名

```java
umsAdminCreateRequest.username()
```

假设前端传入

```json
{
    "username":"admin"
}
```

那么这里得到：admin

#### Lambda查询条件构造器

Wrappers.<UmsAdmin>lambdaQuery() 是MyBatis-Plus提供的Lambda条件查询构造器

创建一个针对UmsAdmin实体类的查询

生成：

```java
LambdaQueryWrapper<UmsAdmin>
```

```java
.eq(
 UmsAdmin::getUsername,
    umsAdminCreateRequest.username()
)
```

```java
long sameUsernameCount = count(
    Wrappers.<UmsAdmin>lambdaQuery()
        .eq(
            UmsAdmin::getUsername,
            umsAdminCreateRequest.username()
        )
);

if (sameUsernameCount > 0) {
    throw new ApiException("用户名已存在");
}
```

由 **Java 泛型 + Lambda 方法引用 + MyBatis-Plus 条件构造器 + Service 的 `count()` 方法**组合起来的。

## 链式调用

普通写法：

```java
LambdaQueryWrapper<UmsAdmin> wrapper=Wrappers.<UmsAdmin>lambdaQuery();
wrapper.eq(UmsAdmin::getUsername,umsAdminCreateRequest.username());
long sameUsernameCount=count(wrapper);
```

链式写法：

```java
long sameUsernameCount=count(
	Wrappers.<UmsAdmin>lambdaQuery()
    	.eq(
        	UmsAdmin::getUsername,
            umsAdminCreateRequest.username()
        )
);
```

```
Wrappers
  工具类
    |
    | 调用
    ↓
lambdaQuery()
  工具类里的静态方法
    |
    | 创建并返回
    ↓
LambdaQueryWrapper<UmsAdmin>
  返回对象的类型
    |
    | 赋值给
    ↓
wrapper
  保存这个对象的变量名
```

声明一个名叫wrapper的变量，它的类型是针对UmsAdmin的Lambda查询条件构造器
