## 数据模型

+ JWT 代表token, 它有2个子类
  + JWSToken
  + JWEToken
+ JWTBuilder：用于构建 JWT 对象，它有2个子类：
  + JWSTokenBuilder ，用于构建 plain token, jws token
  + JWETokenBuilder
+ JWTParser: 用于解析 base64url 格式的 token，生成 JWT对象
    