**Week05 作业题目（周四）：**



**1.（选做）使用 Java 里的动态代理，实现一个简单的 AOP**

地址: [work1](https://github.com/Tureen/JAVA-000/tree/main/Week_05/work1)

详解: 

* [AOP类](https://github.com/Tureen/JAVA-000/blob/main/Week_05/work1/Aop.java): 定义切点开始执行前, 先执行的增强方法; 以及切点执行后, 再执行的增强方法
* [ISchool接口](https://github.com/Tureen/JAVA-000/blob/main/Week_05/work1/ISchool.java) 与 [School类](https://github.com/Tureen/JAVA-000/blob/main/Week_05/work1/School.java): 定义接口与实现方法, 因为Java动态代理是根据接口实现的
* [MyHandler类](https://github.com/Tureen/JAVA-000/blob/main/Week_05/work1/MyHandler.java): 继承 InvocationHandler 类, 在重写方法中, 嵌入 AOP 类的调用, 实际 School 类的方法会通过反射调用
* [Start类](https://github.com/Tureen/JAVA-000/blob/main/Week_05/work1/Start.java): 测试类, 使用 Proxy 类的方法生成代理类, 代理实际类使用





**2.（必做）写代码实现 Spring Bean 的装配，方式越多越好（XML、Annotation 都可以）, 提交到 Github**

地址: [work2](https://github.com/Tureen/JAVA-000/tree/main/Week_05/work2)

详解:

* [way1包](https://github.com/Tureen/JAVA-000/tree/main/Week_05/work2/way1): 使用 XML 方式, 实现 Spring Bean 装配
* [way2包](https://github.com/Tureen/JAVA-000/tree/main/Week_05/work2/way2): 使用 Configuration + Bean 注解的方式实现
* [way3包](https://github.com/Tureen/JAVA-000/tree/main/Week_05/work2/way3): 使用 Component 注解, 且 [Beans](https://github.com/Tureen/JAVA-000/blob/main/Week_05/work2/way3/Beans.java) 类中注入 [Bean](https://github.com/Tureen/JAVA-000/blob/main/Week_05/work2/way3/Bean.java) 类是使用 Autowired 注解做的依赖注入







**Week05 作业题目（周六）：**



**1.（选做）总结一下，单例的各种写法，比较它们的优劣**

* 懒汉: 初始化时生成静态变量, 随时调用, 不用考虑多线程问题, 太多此实现且实际不用的话, 会浪费资源
* 饿汉(无锁): 使用时才创建, 不会浪费资源, 但判断为空和创建两个步骤不是原子性, 多线程并发下可能非单例
* 饿汉(有锁): 每次调用都要获取锁, 所有线程串行进行
* 饿汉(双重校验锁): 未创建时, 通过锁防止产生多个实例, 创建后, 通过判断, 使线程不用获取锁, 解除串行, 但使用了volatile 关键字标识单例对象, 相比普通对象, 不能使用JVM乱序优化, 性能可能略有影响
* 饿汉(枚举): 利用java语言特性, 真实调用时才创建, 且不会有多线程并发问题, 且性能不受任何影响





**3.（必做）给前面课程提供的 Student/Klass/School 实现自动配置和 Starter**

地址: [work3](https://github.com/Tureen/JAVA-000/tree/main/Week_05/work3)

详解: 

* 配置约定文件 [spring.factories](https://github.com/Tureen/JAVA-000/blob/main/Week_05/work3/resources/META-INF/spring.factories) , 让 Spring 启动扫描到 [TulaneConfiguration](https://github.com/Tureen/JAVA-000/blob/main/Week_05/work3/src/TulaneConfiguration.java) 
* [TulaneConfiguration](https://github.com/Tureen/JAVA-000/blob/main/Week_05/work3/src/TulaneConfiguration.java) 会根据 @ConditionalOnProperty 的配置, 检查 [application.yml](https://github.com/Tureen/JAVA-000/blob/main/Week_05/work3/resources/application.yml) 的配置是否匹配, 匹配才进行配置
* [TulaneConfiguration](https://github.com/Tureen/JAVA-000/blob/main/Week_05/work3/src/TulaneConfiguration.java) 中使用 @Bean 注解, 将 Student/Klass/School 配置为 Spring Bean
* [App](https://github.com/Tureen/JAVA-000/blob/main/Week_05/work3/src/App.java) 为Spring Boot 项目启动类, 其中有检测 Spring Bean 是否加载的代码



**6.（必做）研究一下 JDBC 接口和数据库连接池，掌握它们的设计和用法：**

> 1）使用 JDBC 原生接口，实现数据库的增删改查操作。
>
> 2）使用事务，PrepareStatement 方式，批处理方式，改进上述操作。
>
> 3）配置 Hikari 连接池，改进上述操作。提交代码到 Github。

地址: [work4](https://github.com/Tureen/JAVA-000/tree/main/Week_05/work4)

详解:

* [JdbcConnection](https://github.com/Tureen/JAVA-000/blob/main/Week_05/work4/JdbcConnection.java) 中包含题目要求的 1、2、3点, 分别对应方法 simpleUse()、batchUse()、hikariCP()