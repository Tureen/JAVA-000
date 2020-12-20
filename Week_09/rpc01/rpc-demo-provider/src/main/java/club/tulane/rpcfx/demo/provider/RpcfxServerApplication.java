package club.tulane.rpcfx.demo.provider;

import club.tulane.rpcfx.api.RpcRequest;
import club.tulane.rpcfx.api.RpcResolver;
import club.tulane.rpcfx.api.RpcResponse;
import club.tulane.rpcfx.demo.api.OrderService;
import club.tulane.rpcfx.demo.api.UserService;
import club.tulane.rpcfx.netty.NettyHttpServer;
import club.tulane.rpcfx.netty.ServerHandler;
import club.tulane.rpcfx.server.RpcfxInvoker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class RpcfxServerApplication implements ApplicationRunner {

    public static void main(String[] args) {
        SpringApplication.run(RpcfxServerApplication.class, args);
    }

    @Autowired
    RpcfxInvoker invoker;

    /**
     * spring web方式接收RPC调用
     * @param request
     * @return
     */
    @PostMapping("/")
    public RpcResponse invoke(@RequestBody RpcRequest request){
        return invoker.invoke(request);
    }

    @Bean
    public RpcfxInvoker createInvoker(@Autowired RpcResolver resolver){
        return new RpcfxInvoker(resolver);
    }

    @Bean
    public RpcResolver createResolver(){
        return new SpringResolver();
    }

    @Bean(name = "club.tulane.rpcfx.demo.api.UserService")
    public UserService createUserService(){
        return new UserServiceImpl();
    }

    @Bean(name = "club.tulane.rpcfx.demo.api.OrderService")
    public OrderService createOrderService(){
        return new OrderServiceImpl();
    }

    /**
     * Netty Server 方式接收RPC调用
     * @param args
     * @throws Exception
     */
    @Override
    public void run(ApplicationArguments args) throws Exception {
        new NettyHttpServer() {
            @Override
            protected ServerHandler createServerHandler() {
                return new ServerHandler() {
                    @Override
                    protected RpcResponse invoke(RpcRequest rpcRequest) {
                        return invoker.invoke(rpcRequest);
                    }
                };
            }
        }.listen();
    }
}
