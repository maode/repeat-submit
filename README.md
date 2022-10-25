# repeat-submit

并发提交、重复提交处理组件

## 实现原理

该组件通过redis的分布式锁的形式，限制请求重复提交，频繁提交、并发提交。

## 分支说明
该组件创建了两个分支，其中 `master` 分支为常规分支。可引入任何配置并开启了redis的springboot、springcloud项目使用。
`repeat-submit-smes` 分支，是为了方便与smes项目集成而创建的。

### 区别
- master 在拦截到重复的请求时，会抛出 RuntimeException ，需要自己在全局异常处理部分进行捕获处理。
- repeat-submit-smes 在拦截到重复的请求时，会抛出 BizException ，能够自动被smes项目的全局异常处理程序捕获，无需额外处理，即可返回前端友好的提示信息。

## 使用方法

pom.xml 中引入该组件。
```xml
<!--普通项目引入依赖-->
<dependency>
    <groupId>com.yhwt</groupId>
    <artifactId>repeat-submit</artifactId>
    <version>1.0.0</version>
</dependency>
<!--smes项目引入依赖-->
<dependency>
    <groupId>com.yhwt</groupId>
    <artifactId>repeat-submit-smes</artifactId>
    <version>1.0.0</version>
</dependency>
```
在需要防止重复提交的方法上添加 `@ReSubmit` 注解，在防止重复的参数或实体类属性上添加 `@ReSubmitParam` 注解，即可。
使用demo详见 smes-demo 示例项目的 `RepeatSubmitController`

