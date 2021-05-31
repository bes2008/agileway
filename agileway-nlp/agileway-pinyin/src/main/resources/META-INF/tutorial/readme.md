## 拼音

虽然我们可能天天都在用汉字，但中文的发音，拼音的读写，其实没有我们想象中那么单一。

关于中文的拼音的读写，有多种形式。官网：http://www.pinyin.info/



# 1. 罗马化系统 与 其他系统
    中文在进行现代化时，为了更方便的让西方人理解，西方人以及部分中国人，搞起了罗马化的系统。

+ 罗马化系统
    + Hanyu Pinyin
    + MPS2
    + Gwoyeu Romatzyh (Guoyu Luomazi)
    + Sin Wenz (新文字)
    + Tongyong Pinyin
    + Wade-Giles
    + Yale
+ 其他
    + zhuyin fuhao (注音符号)
    
这些拼音系统的比较：http://www.pinyin.info/romanization/compare/tongyong.html


有这么多的拼音系统，我们通常采用的是 Hanyu Pinyin 系统。


# 2. 声母 (initials )和韵母(finals)

http://www.pinyin.info/rules/initials_finals.html


# 3. Java版本的Pinyin库实现方案

## Pinyin4j (com.belerweb:pinyin4j)
    1) 支持6种罗马化系统：Hanyu Pinyin, MPS2, Gwoyeu Romatzyh, Tongyong, Wade-Giles, Yale
    2) License: BSD
    3) 实现方案：
        + 定义了每一个汉字的 Hanyu pinyin, 带声调的，声调不是在元音字母上面，而是放在整个拼音的后面 （支持 20903 个汉字）
        + 维护了Hanyu pinyin 的每一个拼音与其他的罗马化系统拼写的映射表
                     
    
## Houbb pinyin (com.github.houbb:pinyin)
    1) 只支持Hanyu Pinyin系统
    2) License： Apache 2.0
    3) 实现方案：
        + 定义了每一个汉字的 Hanyu pinyin, 带声调的 （支持 41451 个汉字）
        + 定义了一些常用词汇、成语 的发音 （可以避免多音字时的转换错误，支持 42987个）
        + 定义了 5个元音字母的声调

## duguying-pinyin (net.duguying.pinyin:pinyin)
    1) 只支持Hanyu Pinyin系统
    2) License： MIT
    3) 实现方案：
        + 定义了每一个汉字的 Hanyu pinyin, 带声调的 （支持 6763 个汉字）
        + 定义了一些常用词汇、成语 的发音 （可以避免多音字时的转换错误，支持 41119个）
        
## mynlp-pinyin (com.mayabot.mynlp:mynlp-pinyin)
    1) 只支持Hanyu Pinyin系统
    2) License： Apache 2.0
    3) 实现方案：
        + 定义了每一个汉字的 Hanyu pinyin, 带声调的 （支持 6763 个汉字）
        + 支持有声调，无声调结果输出
        + 定义了一些常用词汇、成语 的发音 （可以避免多音字时的转换错误，支持 41119个）      
    4) 该公司还提供了分词支持
    5) 支持 Kotlin   

## nlp-lang (org.nlpcn:nlp-lang)
    1) 只支持Hanyu Pinyin系统
    2) License: Apache 2.0
    3) 实现方案：
        + 定义了每一个汉字的 Hanyu pinyin, 带声调的 （支持 6763 个汉字）
        + 定义了一些常用词汇、成语 的发音 （可以避免多音字时的转换错误，支持 41119个）
    4) 支持简繁互转
    5) elasticsearch 的中文分词是基于它来做的        

## TinyPinyin (io.github.biezhi:TinyPinyin)
    1) 只支持 Hanyu Pinyin系统
    2) License： Apache2.0
    3) 实现方案：
        + 定义了常用词汇发音等 （支持 2594个）
        + 不支持声调
    4) 优点：
        + 快，内存占用少
        + 手机端应用可以用一下

## JPinyin (com.github.stuxuhai:jpinyin)
    1) 只支持 Hanyu Pinyin 系统
    2) License：GPL
    3) 实现方案：
        + 定义了每一个汉字的 Hanyu pinyin, 带声调的（支持 20903 个汉字）
        + 定义了一些多音字发音 （可以避免多音字时的转换错误，支持 828个）
    4) 支持简繁互转

## bopomofo4j (com.rnkrsoft.bopomofo4j:bopomofo4j)
    1) 只支持 Hanyu Pinyin 系统
    2) License：Apache 2.0
    3) 实现方案：
        + 定义了每一个汉字的 Hanyu pinyin, 带声调的（支持 20903 个汉字）
        + 定义了一些多音字发音 （可以避免多音字时的转换错误，支持 856个）
    4) 支持简繁互转 (2534 个字)
    
    
## 选择

Houbb pinyin, duguying-pinyin 他们的功能基本一样，duguying-pinyin支持的汉字、词汇等都比 houbbpinyin少，所以这两个中，可以优先选择 houbb 。


    