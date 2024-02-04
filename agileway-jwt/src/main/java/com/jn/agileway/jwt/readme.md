## 数据模型

+ JWT 代表token, 它有三个子类
  + JWTPlainToken
  + JWSToken
  + JWEToken
+ JWTBuilder：用于构建 JWT 对象，它有三个子类：
  + JWTPlainTokenBuilder
  + JWSTokenBuilder
  + JWETokenBuilder
+ JWTSerializer：用于生成base64Url格式的token串，它有三个子类：
  + JWTPlainTokenSerializer
  + JWSTokenSerializer
  + JWETokenSerializer
+ JWTParser: 用于解析 base64url 格式的 token，生成 JWT对象
    