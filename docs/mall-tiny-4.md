# mall-tiny 4

# Base64编码

**Base64怎么进行转换**

首先将字符串（图片）转换成二进制序列，然后按每6个二进制为1组，分成若干组，如果不足6位，则低位补0。每刘伟组成一个新的字节，高位补00，构成一个新的二进制序列，最后依据base64索引表中的值找到对应的字符。

## Base64 有什么作用？

很多数据本身是二进制的，比如：

- 图片
- 密钥字节
- 文件内容
- 加密结果

这些二进制数据直接放进配置文件、JSON 或网络请求中可能不方便，所以可以先转成 Base64 字符串。

它相当于：

```
二进制数据 → Base64编码 → 普通字符串
```

使用时再反过来：

```
Base64字符串 → Base64解码 → 原始字节数据
```

## Base64 不是加密

这一点很重要：

```
Base64 是编码，不是加密。
```

任何人拿到 Base64 字符串，都可以轻松解码。

例如：

```
MTIzNDU2
```

解码后就是：

```
123456
```

所以不能依靠 Base64 来保护密码或敏感信息。

```java
byte[] keyBytes=Decoders.BASE64.decode(properties.secret());
//这里的secret是一个Base64字符串
//执行代码Decoders.BASE64.decode()后会把他解码成真正的字节数组byte[] keyBytes
```

然后

```java
Keys.hmacShaKeyFor(keyBytes)
//把这些字节创建成JWT签名所使用的米亚
```



## JWT

jwt里面存的本质上就是一组用户登录信息，JWT由三个部分组成，中间通过.分隔

```
Header.Payload.Signature
头部.数据部分.签名
```

1. Header：使用什么签名算法

解码后可能是：

```
{
  "alg": "HS256",
  "typ": "JWT"
}
```

意思是：

- `typ`：这是一个 JWT
- `alg`：使用 HS256 算法进行签名

这一部分不存用户的重要业务信息。

2. Payload：真正存用户信息的地方

例如：

```
{
  "userId": 1,
  "username": "zhangsan",
  "role": "ADMIN",
  "iat": 1785850000,
  "exp": 1785936400
}
```

这些字段可能表示：

| 字段       | 含义           |
| ---------- | -------------- |
| `userId`   | 用户 ID        |
| `username` | 用户名         |
| `role`     | 用户角色       |
| `iat`      | JWT 的签发时间 |
| `exp`      | JWT 的过期时间 |

所以，JWT 里面具体存什么，是程序员生成 JWT 时决定的。

例如下面的代码：

```
String token = Jwts.builder()
        .subject(username)
        .claim("userId", userId)
        .claim("role", "ADMIN")
        .issuedAt(new Date())
        .expiration(expirationDate)
        .signWith(signingKey)
        .compact();
```

生成的 JWT 中就会包含：

```
{
  "sub": "zhangsan",
  "userId": 1,
  "role": "ADMIN",
  "iat": 1785850000,
  "exp": 1785936400
}
```

这里：

```
.subject(username)
```

会生成标准字段：

```
{
  "sub": "zhangsan"
}
```

`sub` 是 subject 的缩写，可以理解为“这个 JWT 属于谁”。

而：

```
.claim("userId", userId)
```

则是自己添加一个 `userId` 字段。

3. Signature:防止JWT被修改

签名部分可以简单理解为：

```
签名 = Header + Payload + 密钥
```

例如：

```
.signWith(signingKey)
```

就是使用之前创建的：

```
this.signingKey
```

给 JWT 签名。

服务器收到 JWT 后，会重新使用同一个密钥进行校验。

假设原来的 Payload 是：

```
{
  "userId": 1,
  "role": "USER"
}
```

用户偷偷把它修改为：

```
{
  "userId": 1,
  "role": "ADMIN"
}
```

虽然内容改成功了，但是用户不知道服务器的密钥，因此无法生成正确的新签名。

服务器验证时就会发现：

```
JWT 签名不正确
```

然后拒绝这个请求。

所以 JWT 的签名主要保证：

> JWT 中的内容没有被别人修改。

## JWT密钥字符串



```java
public JwtTokenService(JwtProperties properties){
    byte[] keyBytes=Decoders.BASE4.decode(properties.secret());//properties.secret()从配置类中获得密钥字符串，这里的字符串经过了Base64编码，不能直接当作真实密钥使用，会把Base64字符串还原成原始字节数组
    this.signingKey=Keys.hmacShaKeyFor(keyBytes);
    
}
```

## Keys.hmacShakeFor()是JJWT提供的方法，会把刚才得到的字节数组转换成一个适合HMAC算法使用的SecretKey对象

得到这个密钥（signingKey)后可以用来生成JWT，也可以用来校验JWT

## 为什么要先Base64解码

因为密钥本质上就是一串二进制字节，但是配置文件更适合存字符串，所以通常先把密钥解码成Base64,放入配置文件，程序运行时再反过来

### 异常捕获

```java
try{
    byte[] keyBytes= Decoders.BASE64.decode(
    	properties.secret()
    );
    this.signingKey=Keys.hmacShaKeyFor(keyBytes);
}catch(
	DecodingException|WeakKeyException exception
){throw new IllegalStateException(
	"JWT密钥必须是有效的Base64,并且解码后至少为32字节",exception
);
    
}

```

1. DecodingException 

说明配置中的字符串不是有效的Base64

2. WeakKeyException 

虽然Base64格式正确，但解码后的密钥太短

## JWT过滤器

> 每次请求进入后端的时候，先检查请求里有没有JWT，并判断这个JWT是否有效

前端登录成功后，拿到 JWT：

```
eyJhbGciOiJIUzI1NiJ9...
```

之后访问接口时，把 JWT 放在请求头里：

```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

请求进入后端：

```
前端请求
   ↓
JWT过滤器
   ↓
检查请求头中的Token
   ↓
验证Token是否合法
   ↓
把用户身份放入Spring Security
   ↓
Controller
```

所以 JWT 过滤器相当于门卫。

### JWT过滤器主要做什么

1.从请求头中取出JWT

```
Authorization: Bearer eyJhbGciOi...
```

过滤器先拿到：

```java
String authorization=request.getHeader("Authorization");
//然后判断是不是以Bearer开头
if(authorization==null || !authorization.startwith("Bearer ")){//如果没有JWT，就先不进行身份验证，继续执行后面的过滤器
    filterChain.doFilter(request,response);
    return ;
}
    
```

2.去掉Bearer

完整请求头

```
Bearer eyJhbGciOiJIUzI1NiJ9...
```

真正的JWT是后面的部分

```java
String token = authorization.substring(7);
//Brearer_ 一共七个字符，最后一个是空格，处理后得到token
```

3.解析和验证JWT

过滤器调用JWT服务，这一步会检查JWT格式是否正确，签名是否正确，JWT是否过期，JWT是否被修改

```java
Claims claims = jwtTokenService.parseToken(token)
```

如果JWT合法，就可以从Claims中拿到

```java
String username=claims.getSubject();
Long adminId=claims.get("adminId",Long.class);
```

4.告诉Spring Security当前用户是谁

验证JWT成功后，还不能只把用户名存在普通变量里。必须把用户放入SecurityContexHolder

```java
UsernamePasswordAuthenticationToken authentication=
    new UsernamePasswordAuthenticationToken(
		userDetails,
    	null,
    	userDetails.getAuthorities()
);
SecurityContexHolder
    .getContext()
    .setAuthentication(authentication);
```

这一步非常重要。

它相当于告诉 Spring Security：

> 这个请求已经认证成功，当前用户是张三，他拥有这些权限。

之后 Controller 或权限配置才能识别当前用户。


