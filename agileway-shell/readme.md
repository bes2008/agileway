# agileway-shell 使用说明

## 1. 目标
替换 spring-shell 

### 1.1 功能说明
1. 支持 `POSIX`/`GNU` 命令行模式
2. 支持 `<command>` `<Options>` `<Args>` 风格的命令
3. 支持众多内置命令
4. 支持多值，自动生成相应集合
5. 支持与`Spring`应用整合使用
6. 可以很方便从spring-shell迁移到agileway-shell
7. 可配置多个命令扫描目录
8. 支持3种运行模式：ad-hoc （一次性命令执行）, interactive （交互式）, script （脚本模式）。 
9. 支持 ANSI 输出

## 2. 使用说明

### 2.1 基于注解的命令定义
1. `@CommandComponent` 用在类上，用于标识命令类。当命令扫描时，会基于这个注解。
2. `@CommandGroup`用在类上、或者package上，用于标识命令所属的组
3. `@Command`用在`public`方法上，用于标识会被作为命令来解析的方法。被使用@Command标注的方法有如下要求：
   1. 方法是`public`且不能是`static`的
   2. 方法的参数必须使用 `@CommandOption` 或者 `@CommandArgument`标注
4. `@CommandOption` 用的方法的参数上，用于标识命令的 `option`
5. `@CommandArugment` 用的方法的参数上，用于标识命令的 `arg`

### 2.2 内置命令
1. `help`: 提供命令帮助信息。也可以使用 `<COMMAND> --help` 格式
2. `stacktrace`： 提供最近10个异常stack信息
3. `commands`： 查看命令清单
4. `history`：查看历史执行的命令，最大可查看最近1000条
5. `env-variables`： 查看环境变量
6. `system-props`：查看系统属性
7. `clear`：清理屏幕
8. `quit`：退出命令行

## 3. 应用集成

### 3.1 非 spring的应用
1. 引入依赖
```xml
<dependency>
    <groupId>io.github.bes2008.solution.agileway</groupId>
    <artifactId>agileway-shell</artifactId>
    <version>${agileway.version}</version>
</dependency>
```
2. ad-hoc模式
```java
new ShellBuilder()
    .defaultRunMode(RunMode.ADHOC)
    .build()
    .start(new String[]{"system-props" "-iv" "Jav"});
```
3. 交互模式
```java
new ShellBuilder()
    .defaultRunMode(RunMode.INTERACTIVE)
    .build()
    .start(new String[]{"system-props" "-iv" "Jav"});
```


### 3.2 Spring应用
1. 引入依赖
```xml
<dependency>
    <groupId>io.github.bes2008.solution.agileway</groupId>
    <artifactId>agileway-shell</artifactId>
    <version>${agileway.version}</version>
</dependency>
<dependency>
   <groupId>io.github.bes2008.solution.agileway</groupId>
   <artifactId>agileway-spring</artifactId>
   <version>${agileway.version}</version>
</dependency>
```
2. 交互模式：
```java

ApplicationContext appCtx = SpringApplication.run(YourApplication.class, args);

new ShellBuilder()
    .ansiConsoleEnabled(true) // 启用 ANSI 输出
    .name("reposcan") // 指定shell命令
    .propertySet(new SpringEnvironmentPropertySet("spring", appCtx.getEnvironment())) // 集成 spring environment
    .defaultExecutor(SpringContextHolder.getBean(SpringComponentFactory.class)) // 引入 spring 的bean factory
    .defaultRunMode(RunMode.INTERACTIVE) // 启用 交互模式
    .build().start(args); // 启用shell，如果 args 是一个命令，可直接执行

```