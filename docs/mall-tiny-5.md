# mall-tiny 5

## public interface AuthenticationEntryPoint

Used by ExceptionTranslationFilter to commence an authentication scheme

```java
void
commence(jakarta.servlet.http.HttpServletRequest request, jakarta.servlet.http.HttpServletResponse response, AuthenticationException authException)
```

专门规定，当用户访问需要登录的接口，但当时没有完成身份认证时，该怎么响应

Spring Security内部通常由ExceptionTranslationFilter在发现未认证异常后调用它

## 密码比较

```java
if(!passwordEncoder.matches(request.oldPassword(),admin.getPassword())){
    throw new ApiException("原密码错误");
}
```

- request.oldPassword() 用户刚输入的明文密码
- admin.getPassWord() 数据库里保存的加密密码
- passwordEncoder.matches(明文密码，加密密码),用来判断这个明文密码经过对应算法验证后，是否和数据库里的加密密码匹配。

```java
if(request.oldPassword().equals(request.newPassword())){
    throw new ApiException("新密码不能和原密码相同");
}
```

- 这里比较的是request.oldPassword() 用户输入的旧密码
- request.newPassword()用户输入的新密码
- 这两个都是明文字符串，所以直接使用字符串比较 equals()

最关键的区别可以看这张表：

| 比较内容               | 方法                                 |
| ---------------------- | ------------------------------------ |
| 明文 vs 明文           | `equals()`                           |
| 明文 vs 数据库加密密码 | `passwordEncoder.matches()`          |
| 加密密码 vs 加密密码   | 一般也不要直接 `equals()` 判断原密码 |

为什么最后一条也不推荐？因为 BCrypt 每次加密同一个密码，结果通常都不一样。

例如：

```java
passwordEncoder.encode("123456");
passwordEncoder.encode("123456");
```

可能得到：

```
$2a$10$abc...
$2a$10$xyz...
```

虽然两个都代表密码 `123456`，字符串却不相同。

所以不能这样：

```java
passwordEncoder
        .encode(request.oldPassword())
        .equals(admin.getPassword())
```

这很可能永远对不上。

应该始终：

```java
passwordEncoder.matches(
        request.oldPassword(),
        admin.getPassword()
)
```

你可以简单记成：

```
两个都是用户刚输入的普通字符串
→ equals()

用户输入的密码 和 数据库里的加密密码
→ matches()
```

## 方法引用



`UmsAdmin::getId`、`UmsAdmin::getPassword` 是 **Java 方法引用**，MyBatis-Plus 会根据它们推断对应的数据库字段。

比如：

```
.eq(UmsAdmin::getId, adminId)
```

意思是：

> 使用 `UmsAdmin` 的 `getId()` 对应的字段，生成查询条件。

如果实体类：

```
public class UmsAdmin {
    private Long id;
    private String password;

    public Long getId() {
        return id;
    }

    public String getPassword() {
        return password;
    }
}
```

MyBatis-Plus 会识别：

```
UmsAdmin::getId
```

对应数据库列：

```
id
```

所以：

```
.eq(UmsAdmin::getId, adminId)
```

大致生成：

```
WHERE id = ?
```

而：

```
.set(UmsAdmin::getPassword, encodedPassword)
```

表示：

> 把 `getPassword()` 对应的数据库字段更新成 `encodedPassword`。

大致生成：

```
SET password = ?
```

所以完整：

```
Wrappers.<UmsAdmin>lambdaUpdate()
        .eq(UmsAdmin::getId, adminId)
        .set(UmsAdmin::getPassword, encodedPassword)
```

大致对应：

```
UPDATE ums_admin
SET password = encodedPassword
WHERE id = adminId;
```

### 那 `::` 到底是什么？

```
UmsAdmin::getId
```

叫**方法引用**。

你可以先把它简单理解成：

```
告诉 MyBatis-Plus：
我要操作 UmsAdmin 的 id 属性
```

类似：

```
UmsAdmin::getPassword
```

就是：

```
我要操作 UmsAdmin 的 password 属性
```

它比直接写字符串：

```
.eq("id", adminId)
.set("password", encodedPassword)
```

更安全。

因为如果你以后把 Java 属性名改了，IDE 会提醒；但字符串 `"id"` 写错成 `"idd"`，编译器通常发现不了。

所以这里其实有三层：

```
UmsAdmin::getId
    ↓
Java 实体类的 getId 方法
    ↓ MyBatis-Plus 解析
实体属性 id
    ↓ 映射
数据库字段 id
```

## @PatchMapping 是Spring MVC里的一个注解

PATCH一般用于局部修改数据。

```java
@PatchMapping("/admin/{id}")
public CommonResult<Void> updateAdmin(@PathVariable Long id,@RequestBody UmsAdminUpdateRequest request){
    //修改管理员部分信息
    return CommonResult.success(null);
}
```

假设前端发送：

```
PATCH /admin/5
Content-Type: application/json
```

请求体：

```
{
  "email": "new@example.com"
}
```

Spring 就会找到这个：

```
@PatchMapping("/admin/{id}")
```

对应的方法执行。

这里的：

```
{id}
```

会通过：

```
@PathVariable Long id
```

拿到：

```
id = 5
```

而 JSON：

```
{
  "email": "new@example.com"
}
```

会通过：

```
@RequestBody UmsAdminUpdateRequest request
```

转换成 Java 对象。

## @AuthenticationPrincipal

属于Spring Security,可以从当前登录用户的Authentication中，直接取出principal

## RequestBody

属于Spring MVC ,作用是把HTTP请求体里的JSON，自动转换成JAVA对象

前端发送：

```
POST /admin/login
Content-Type: application/json
{
  "username": "admin",
  "password": "123456"
}
```

Controller：

```
@PostMapping("/login")
public CommonResult<?> login(
        @RequestBody LoginRequest request
) {
    System.out.println(request.username());
}
```

Spring MVC 会自动把 JSON：

```
{
  "username": "admin",
  "password": "123456"
}
```

转换成：

```
LoginRequest request
```

# MyBatis `@Delete` 注解笔记

## 1. `@Delete` 是什么

`@Delete` 是 MyBatis 提供的注解，用于直接在 Mapper 接口中编写删除 SQL。

例如：

```java
@Delete("""
    DELETE FROM ums_admin_role_relation
    WHERE admin_id = #{adminId}
    """)
int deleteRoleRelationsByAdminId(
        @Param("adminId") Long adminId
);
```

大致等价于：

```sql
DELETE FROM ums_admin_role_relation
WHERE admin_id = ?;
```

---

## 2. 两种写法对比

### 写法一

```java
@Delete("""
    delete from ums_admin_role_relation
    where admin_id = #{adminId}
    """)
void deleteRoleRelationByAdminId(Long adminId);
```

特点：

- 使用 `void`，不关心删除了多少条数据
- 没有显式使用 `@Param`
- SQL 可以正常执行，但信息较少

---

### 写法二

```java
@Delete("""
    DELETE FROM ums_admin_role_relation
    WHERE admin_id = #{adminId}
    """)
int deleteRoleRelationsByAdminId(
        @Param("adminId") Long adminId
);
```

特点：

- 使用 `int` 返回删除的数据行数
- 使用 `@Param("adminId")` 明确绑定参数
- SQL 格式更清晰
- 方法名使用 `Relations`，因为可能删除多条关联记录

一般更推荐这种写法。

---

## 3. `@Param` 的作用

```java
@Param("adminId") Long adminId
```

作用是告诉 MyBatis：

```text
SQL 中的 #{adminId}
        ↓
对应
        ↓
Java 方法参数 adminId
```

例如：

```java
@Delete("""
    DELETE FROM ums_admin_role_relation
    WHERE admin_id = #{adminId}
    """)
int deleteRoleRelationsByAdminId(
        @Param("adminId") Long adminId
);
```

其中：

```java
#{adminId}
```

会使用：

```java
@Param("adminId") Long adminId
```

传入的值。

---

## 4. `void` 和 `int` 的区别

### `void`

```java
void deleteRoleRelationByAdminId(Long adminId);
```

只负责执行 SQL，不返回删除数量。

---

### `int`

```java
int deleteRoleRelationsByAdminId(Long adminId);
```

返回受到 SQL 影响的数据行数。

例如数据库中：

```text
admin_id    role_id
5           1
5           2
5           3
```

执行：

```java
int count = deleteRoleRelationsByAdminId(5L);
```

可能得到：

```java
count == 3;
```

表示删除了 3 条记录。

---

## 5. 为什么可能删除多条记录

`ums_admin_role_relation` 是管理员和角色之间的关联表。

一个管理员可能拥有多个角色：

```text
admin_id    role_id
5           1
5           2
5           3
```

执行：

```sql
DELETE FROM ums_admin_role_relation
WHERE admin_id = 5;
```

会把这三条关联记录全部删除。

所以方法名：

```java
deleteRoleRelationsByAdminId
```

使用复数 `Relations` 更准确。

---

## 6. `#{adminId}` 是什么

```java
#{adminId}
```

是 MyBatis 的参数占位符。

例如：

```java
deleteRoleRelationsByAdminId(5L);
```

MyBatis 最终执行的逻辑类似：

```sql
DELETE FROM ums_admin_role_relation
WHERE admin_id = 5;
```

实际执行时一般使用预编译参数，而不是直接拼接 SQL。

---

## 7. 推荐写法

```java
@Delete("""
    DELETE FROM ums_admin_role_relation
    WHERE admin_id = #{adminId}
    """)
int deleteRoleRelationsByAdminId(
        @Param("adminId") Long adminId
);
```

原因：

1. 参数绑定明确
2. 可以得到删除行数
3. SQL 可读性较好
4. 方法命名更准确

---

## 8. 一句话总结

```text
@Delete
→ 告诉 MyBatis 执行 DELETE SQL

@Param("adminId")
→ 把 Java 参数和 #{adminId} 绑定

int
→ 返回删除了多少条数据

void
→ 只执行删除，不关心删除数量
```

## @TableName @TableId MyBatis-Plus提供的注解

```java
@TableName("ums_role")  //UmsRole这个Java类，对应数据库里的ums_role表
public class UmsRole{}
```

```java
@TableId(value="id",type=IdType.AUTO)
private Long id;
//id这个Java属性，对应数据库表里的主键Id字段
```

## public interface UmsRoleMapper extends BaseMapper<UmsRole>

UmsRoleMapper 这个接口继承了MyBatis-plus提供的BaseMapper<UmsRole>接口

### BaseMapper<UmsRole>是什么？

是MyBatis-Plus提供的一个通用Mapper接口，里面已经写好了很多常见的数据库操作

```java
selectById
selectList
insert
updateById
deleteById
```

继承完之后，即使接口里面什么都不写，他也已经有很多CRUD方法

## ORM object relational mapping 对象关系映射

把java对象和数据库表建立对应关系，可以使用操作对象的方式操作数据库

正在使用的MyBatis-Plus，就是在做这种java对象和数据库表之间的映射工作

不过严格来说，**MyBatis/MyBatis-Plus 通常被称为持久层框架或半 ORM 框架**，不像 Hibernate/JPA 那种典型的全 ORM，因为 MyBatis 仍然允许、也经常需要你直接控制 SQL。

## extends ServiceImpl<UmsRoleMapper,UmsRole>

是MyBatis-Plus的Service层继承写法,让UmsRoleServiceImpl直接拥有MyBatis-Plus已经写好的角色表CRUD能力。

```java
@Service
public class UmsRoleServiceImpl extends ServiceImpl<UmsRoleMapper,UmsRole> implements UmsRoleService{}
```

- ServiceImpl是MyBatis-Plus提供的一个通用Service实现类，里面已经写好了很多常用方法

```java
getById() list() save() updateById() removeById() count()
```

- 两个泛型参数 <UmsRoleMapper,UmsRole>
- 第一个参数UmsRoleMapper,表示这个Service底层使用哪个Mapper操作数据库
- 第二个参数UmsRole表示这个Service处理的实体类型是什么

## API写法

```java
LambdaQueryWrapper<实体类> wrapper= Wrappers.lambdaQuery(实体类.class);
wrapper.eq(实体类::get字段，值);
wrapper.like(实体类::get字段，值);
wrapper.orderByAsc(实体类::get字段);
```

常见条件就几个：

```
.eq(...)       // =
.ne(...)       // !=
.gt(...)       // >
.ge(...)       // >=
.lt(...)       // <
.le(...)       // <=
.like(...)     // LIKE '%xxx%'
.in(...)       // IN (...)
.orderByAsc(...)   // 升序
.orderByDesc(...)  // 降序
```

比如：

```
wrapper.eq(UmsRole::getStatus, 1);
```

对应：

```
WHERE status = 1
wrapper.like(UmsRole::getName, "admin");
```

对应：

```
WHERE name LIKE '%admin%'
wrapper.orderByDesc(UmsRole::getId);
```

对应：

```
ORDER BY id DESC
```

你现在最重要的不是“自己凭空写出来”，而是看到：

```
wrapper.eq(...)
wrapper.like(...)
wrapper.orderByAsc(...)
```

能马上知道：

> 这是在拼 SQL 的 WHERE 和 ORDER BY 条件。

这就已经够了。

## @RequestParam(required=false)

从请求URL的查询参数中取值，而且这个参数可以不传

默认通常相当于@RequestParam(required=true),也就是参数必须提供

## 分页

先创建“分页信息”，再创建“查询条件”，最后把两者一起交给 MyBatis-Plus 去数据库查询。

```java
@Override
public Page list(String keyword, long pageSize, long pageNum) {

    Page<UmsRole> page = new Page<>(pageNum, pageSize);

    LambdaQueryWrapper<UmsRole> wrapper =
            Wrappers.lambdaQuery(UmsRole.class);

    if (StringUtils.hasText(keyword)) {
        wrapper.like(UmsRole::getName, keyword);
    }

    wrapper
            .orderByAsc(UmsRole::getSort)
            .orderByAsc(UmsRole::getId);

    return page(page, wrapper);
}
```

## MyBatis-Plus 分页查询实现

### 1. 示例代码

```java
@Override
public Page<UmsRole> list(String keyword, long pageSize, long pageNum) {

    Page<UmsRole> page = new Page<>(pageNum, pageSize);

    LambdaQueryWrapper<UmsRole> wrapper =
            Wrappers.lambdaQuery(UmsRole.class);

    if (StringUtils.hasText(keyword)) {
        wrapper.like(UmsRole::getName, keyword);
    }

    wrapper
            .orderByAsc(UmsRole::getSort)
            .orderByAsc(UmsRole::getId);

    return page(page, wrapper);
}
```

这段代码的整体作用是：

> 根据 `keyword` 查询角色，并按照指定的 `pageNum` 和 `pageSize` 进行分页。

整体可以分成：

```text
接收分页参数
    ↓
创建 Page 分页对象
    ↓
创建 Wrapper 查询条件
    ↓
添加模糊查询条件
    ↓
添加排序条件
    ↓
page(page, wrapper)
    ↓
执行分页查询
    ↓
返回 Page<UmsRole>
```

---

### 2. 分页参数

方法接收三个参数：

```java
String keyword,
long pageSize,
long pageNum
```

分别表示：

| 参数       | 作用               |
| ---------- | ------------------ |
| `keyword`  | 角色名称搜索关键词 |
| `pageSize` | 每页显示多少条     |
| `pageNum`  | 查询第几页         |

例如前端请求：

```text
/list?keyword=管理员&pageSize=5&pageNum=2
```

那么：

```text
keyword = "管理员"
pageSize = 5
pageNum = 2
```

表示：

> 查询名称中包含“管理员”的角色，第 2 页，每页 5 条。

---

### 3. 创建 Page 分页对象

```java
Page<UmsRole> page = new Page<>(pageNum, pageSize);
```

`Page` 是 MyBatis-Plus 提供的分页对象。

泛型：

```java
Page<UmsRole>
```

表示分页数据中的每一条记录都是：

```java
UmsRole
```

构造方法：

```java
new Page<>(pageNum, pageSize)
```

第一个参数表示当前页：

```java
pageNum
```

第二个参数表示每页大小：

```java
pageSize
```

例如：

```java
new Page<>(2, 5);
```

表示：

```text
查询第 2 页
每页 5 条数据
```

此时 `page` 可以理解成一个“分页要求对象”。

---

### 4. 创建查询条件 Wrapper

```java
LambdaQueryWrapper<UmsRole> wrapper =
        Wrappers.lambdaQuery(UmsRole.class);
```

`LambdaQueryWrapper` 是 MyBatis-Plus 提供的查询条件构造器。

可以简单理解为：

> 用来逐步拼接 SQL 查询条件。

这里：

```java
LambdaQueryWrapper<UmsRole>
```

表示：

> 为 `UmsRole` 构造查询条件。

而：

```java
Wrappers.lambdaQuery(UmsRole.class)
```

负责创建这个查询条件对象。

刚创建时，可以理解为：

```sql
SELECT *
FROM ums_role
```

暂时没有 `WHERE` 条件。

---

### 5. 添加模糊查询条件

```java
if (StringUtils.hasText(keyword)) {
    wrapper.like(UmsRole::getName, keyword);
}
```

#### `StringUtils.hasText()`

这是 Spring 提供的字符串判断方法。

```java
StringUtils.hasText(keyword)
```

判断字符串：

- 是否不为 `null`
- 是否不为空字符串
- 是否不全是空格

例如：

```text
"admin"  → true
""       → false
"   "    → false
null     → false
```

因此：

> 只有用户真正输入了关键词，才添加名称查询条件。

---

### 6. `wrapper.like()` 是什么

```java
wrapper.like(UmsRole::getName, keyword);
```

表示：

> `name` 字段进行模糊查询。

例如：

```java
keyword = "管理员";
```

大致生成：

```sql
WHERE name LIKE '%管理员%'
```

其中：

```java
UmsRole::getName
```

是 Java 的方法引用。

MyBatis-Plus 会根据：

```java
UmsRole::getName
```

识别出对应的实体属性：

```text
name
```

然后找到数据库对应字段。

---

### 7. 添加排序条件

```java
wrapper
        .orderByAsc(UmsRole::getSort)
        .orderByAsc(UmsRole::getId);
```

第一个：

```java
.orderByAsc(UmsRole::getSort)
```

表示：

```sql
ORDER BY sort ASC
```

按照 `sort` 字段升序排列。

第二个：

```java
.orderByAsc(UmsRole::getId)
```

表示：

> 如果 `sort` 相同，再按照 `id` 升序排列。

最终类似：

```sql
ORDER BY sort ASC, id ASC
```

所以排序规则是：

```text
首先按照 sort 从小到大
        ↓
如果 sort 相同
        ↓
再按照 id 从小到大
```

---

### 8. `wrapper` 为什么可以链式调用

可以写成：

```java
wrapper
        .orderByAsc(...)
        .orderByAsc(...);
```

是因为这些方法执行后会继续返回 Wrapper 对象。

可以简单理解为类似：

```java
return this;
```

因此：

```java
wrapper.orderByAsc(...)
```

执行结束后仍然得到 `wrapper`，于是可以继续调用：

```java
.orderByAsc(...)
```

这种写法称为：

> 链式调用。

---

### 9. 真正执行分页查询

最重要的是：

```java
return page(page, wrapper);
```

这里有两个参数。

第一个：

```java
page
```

负责告诉 MyBatis-Plus：

```text
查第几页
每页多少条
```

第二个：

```java
wrapper
```

负责告诉 MyBatis-Plus：

```text
查询什么条件
按照什么方式排序
```

所以：

```java
page(page, wrapper)
```

可以理解成：

> 按照 `wrapper` 的查询条件，根据 `page` 的分页要求进行数据库查询。

---

### 10. 分页 SQL 是怎么产生的

假设：

```text
pageNum = 2
pageSize = 5
keyword = "管理员"
```

查询条件大致是：

```sql
SELECT *
FROM ums_role
WHERE name LIKE '%管理员%'
ORDER BY sort ASC, id ASC;
```

分页时还需要计算偏移量：

```text
offset = (pageNum - 1) × pageSize
```

所以：

```text
offset = (2 - 1) × 5
       = 5
```

最终数据库分页查询大致类似：

```sql
SELECT *
FROM ums_role
WHERE name LIKE '%管理员%'
ORDER BY sort ASC, id ASC
LIMIT 5 OFFSET 5;
```

含义：

```text
跳过前 5 条
再取 5 条
```

也就是第 2 页。

---

### 11. 不同页数的关系

假设：

```text
pageSize = 5
```

那么：

```text
第 1 页
OFFSET = 0
取 5 条

第 2 页
OFFSET = 5
取 5 条

第 3 页
OFFSET = 10
取 5 条
```

计算公式：

```text
OFFSET = (pageNum - 1) × pageSize
```

---

### 12. 为什么还可以知道总记录数

分页查询除了需要当前页的数据，还需要知道：

```text
总共有多少条数据
总共有多少页
```

因此分页插件通常还会进行总数统计，大致类似：

```sql
SELECT COUNT(*)
FROM ums_role
WHERE name LIKE '%管理员%';
```

假设：

```text
总记录数 = 23
每页数量 = 5
```

那么：

```text
总页数 = 5
```

---

### 13. Page 中最终保存什么

查询完成后返回：

```java
Page<UmsRole>
```

其中包含类似：

```text
current = 2
size = 5
total = 23
pages = 5
records = 当前第 2 页的角色数据
```

可以使用：

```java
page.getCurrent();
page.getSize();
page.getPages();
page.getTotal();
page.getRecords();
```

分别获取：

| 方法           | 含义         |
| -------------- | ------------ |
| `getCurrent()` | 当前页       |
| `getSize()`    | 每页大小     |
| `getPages()`   | 总页数       |
| `getTotal()`   | 总记录数     |
| `getRecords()` | 当前页的数据 |

---

### 14. 和 CommonPage 的关系

之前定义：

```java
public record CommonPage<T>(
        long pageNum,
        long pageSize,
        long totalPage,
        long total,
        List<T> list
) {
}
```

可以把 MyBatis-Plus 的：

```java
Page<UmsRole>
```

转换成统一分页结果：

```java
CommonPage.from(page);
```

对应关系：

```text
page.getCurrent()
        ↓
pageNum

page.getSize()
        ↓
pageSize

page.getPages()
        ↓
totalPage

page.getTotal()
        ↓
total

page.getRecords()
        ↓
list
```

---

### 15. 整个分页查询流程

```text
前端请求
/list?pageNum=2&pageSize=5&keyword=管理员
        ↓
Controller
        ↓
Service.list(keyword, pageSize, pageNum)
        ↓
new Page<>(pageNum, pageSize)
        ↓
创建分页信息
        +
LambdaQueryWrapper
        ↓
创建查询条件
        ↓
like()
orderByAsc()
        ↓
page(page, wrapper)
        ↓
MyBatis-Plus
        ↓
生成 SQL
        ↓
数据库执行查询
        ↓
返回 Page<UmsRole>
        ↓
CommonPage.from(page)
        ↓
返回统一分页 JSON
```

---

### 16. 核心记忆

MyBatis-Plus 分页查询可以记成三个部分：

```java
Page<UmsRole> page =
        new Page<>(pageNum, pageSize);
```

负责：

```text
第几页
每页几条
```

---

```java
LambdaQueryWrapper<UmsRole> wrapper =
        Wrappers.lambdaQuery(UmsRole.class);
```

负责：

```text
查什么
怎么排序
```

---

```java
return page(page, wrapper);
```

负责：

```text
真正执行分页数据库查询
```

因此一句话总结：

> `Page` 决定“怎么分页”，`Wrapper` 决定“查什么”，`page(page, wrapper)` 负责真正执行分页查询。

## 构造器注入 和 字段注入

- 依赖注入(dependency injection DI ) 一个类需要的替他对象，不由他自己new ,而是由Spring创建后传给他

  例如 `UmsAdminServiceImpl` 需要操作用户角色关系，它依赖：

  ```
  UmsAdminRoleRelationMapper
  ```

  不推荐自己创建：

  ```
  private final UmsAdminRoleRelationMapper mapper =
          new UmsAdminRoleRelationMapper();
  ```

  Mapper 是接口，本身也不能直接 `new`。它的代理对象由 Spring/MyBatis 创建并注入。

### 一.字段注入

字段注入是把@Autowired直接写在成员变量上

```java
@Service
public class UmsAdminServiceImpl{
    @Autowired
    private UmsAdminRoleRelationMapper adminRoleRelationMapper;
    @Autowired
    private UmsRoleMapper roleMapper;
}
```

Spring 创建 `UmsAdminServiceImpl` 后，通过反射给字段赋值。

可以把过程理解为：

```
1. Spring 创建 UmsAdminServiceImpl
2. 此时 mapper 字段暂时还是 null
3. Spring 找到对应 Mapper Bean
4. Spring 通过反射把 Mapper 放进字段
```

### 二.构造器注入

```java
@Service
public class UmsAdminServiceImpl {

    private final UmsAdminRoleRelationMapper adminRoleRelationMapper;
    private final UmsRoleMapper roleMapper;

    public UmsAdminServiceImpl(
            UmsAdminRoleRelationMapper adminRoleRelationMapper,
            UmsRoleMapper roleMapper
    ) {
        this.adminRoleRelationMapper = adminRoleRelationMapper;
        this.roleMapper = roleMapper;
    }
}
```

pring 创建对象时，相当于执行：

```
new UmsAdminServiceImpl(
        adminRoleRelationMapperBean,
        roleMapperBean
);
```

执行过程是：

```
1. Spring 先找到两个 Mapper Bean
2. Spring 调用 UmsAdminServiceImpl 构造器
3. 构造器给两个 final 字段赋值
4. 返回一个依赖完整的 Service 对象
```

如果一个 Spring Bean 只有一个构造器，通常不需要写 `@Autowired`：

```
public UmsAdminServiceImpl(
        UmsRoleMapper roleMapper
) {
    this.roleMapper = roleMapper;
}
```

## 三、两者的核心区别

| 对比项               | 构造器注入           | 字段注入                |
| -------------------- | -------------------- | ----------------------- |
| 注入时机             | 创建对象时           | 创建对象后              |
| 是否支持 `final`     | 支持                 | 不方便支持              |
| 依赖是否清晰         | 查看构造器即可知道   | 需要检查全部字段        |
| 对象是否能完整初始化 | 可以                 | 注入前字段暂时为 `null` |
| 单元测试             | 可以直接传入模拟对象 | 通常需要 Spring 或反射  |
| 当前推荐程度         | 推荐                 | 一般不推荐用于必要依赖  |

## Lambda Wrapper

一般写法

```java
.eq("parent_id",parentId)
```

lambda写法

```java
.eq(UmsMenu::getParentId,parentId)
```

##  菜单树响应

**把数据库里所有菜单查出来，然后按parentId分组，最后从parentId=0开始递归组成装成一棵菜单树。**

```java
@Override
public List<UmsMenuNode> treeList() {

    // ① 查询所有菜单，并排序
    List<UmsMenu> menus = list(...);

    // ② 按 parentId 分组
    Map<Long, List<UmsMenu>> childrenMap = menus.stream()
            .collect(Collectors.groupingBy(UmsMenu::getParentId));

    // ③ 从顶级菜单开始，递归构建树
    return buildChildren(0L, childrenMap);
}
```



```java
//查询所有菜单
List<UmsMenu> menus = list(
        Wrappers.<UmsMenu>lambdaQuery()
                .orderByAsc(
                        UmsMenu::getSort,
                        UmsMenu::getId
                )
);
```

根据这个Wrapper条件查询UmsMenu表，并返回所有结果

```java
Map<Long,List<UmsMenu>> childrenMap=
    menus.stream().collect(Collectors.groupingBy(UmsMenu::getParentId));
```

按照每个菜单的 `parentId` 进行分组。
