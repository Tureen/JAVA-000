**最新作业导航: [点击跳转](https://github.com/Tureen/grow)**



<br>

<br><br>



*以下为旧信息*

---





Netty作业- 实现网关



作业要求:

1. 实现 Http 客户端, 接收到响应数据并原样返回
2. (选做) 用Netty 实现 Http 客户端
3. 实现 Filter 过滤器功能, 给 Http 客户端请求先, 加上请求头 ("nio", "xxx")
4. (选做) 实现 router 路由器功能



完成情况

1. 使用 httpClient 实现网关的 http客户端功能, 实现代码类为 club.tulane.customgateway.outbound.MyHttpOutboundHandler
2. 未实现
3. 用流水线的方式, 实现过滤器链, 可自由添加任意过滤器
4. 实现了轮询路由, 在http客户端请求前, 通过路由获得实际访问地址