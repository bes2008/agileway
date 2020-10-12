## Codec

目前市面上支持很多的序列化框架
1）需要IDL的有：Thrift, Avro, Protobuf
2）不必需 IDL的：Fst, Hessian, Jdk 内置，Kryo, Protostuff

需要IDL的不必说了，性能好，兼容多种语言，是特色，但是开发效率低下。

下面来对比一下这些不需要IDL的序列化框架：


|   框架  |   无参构造函数 |    循环引用 | 对象为 null |是否需要预先知道对象类型|大对象（4M）| 跨语言 |
|--------|---------------|------------|-------------|--------------------|-----------|-------|
| Kryo   |  支持  | 需要将 reference 选项打开 | 支持 | 不需要，关闭 register | 支持       | 支持，较复杂 |
| Java   |  支持          | 支持       | 支持        | 不需要              | 支持       | 支持 |
| Protostuff   |  支持  | 支持       | 支持        | 需要              | 支持       | 支持 |
| Jackson   |  支持       | 不支持       | 支持        | 不需要              | 支持       | 支持|
| Hessian   |  支持      | 支持       | 支持        | 不需要              | 支持       | 支持 |
| FST   |  支持      | 支持       | 支持        | 不需要              | 支持       | 支持 |



虽然有这么多的框架可以先选择，但是他们在使用上毕竟有着不小的区别。
每一种类库肯定有一下自身的特性在里面，给使用也带来了不小的学习成本。

所以开发Codec模块来将它们统一。

## 核心APIs：

```java

public interface Codec<T> {
    @Nullable
    byte[] encode(@Nullable T obj) throws CodecException;

    @Nullable
    T decode(@Nullable byte[] bytes) throws CodecException;


    @Nullable
    T decode(@Nullable byte[] bytes, Class<T> targetType) throws CodecException;

    boolean canSerialize(Class<?> type);

    Class<T> getTargetType();

    void setTargetType(Class<T> targetType);
}

```





