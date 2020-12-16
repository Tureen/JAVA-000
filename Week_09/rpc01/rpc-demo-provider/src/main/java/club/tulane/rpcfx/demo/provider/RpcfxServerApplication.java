package club.tulane.rpcfx.demo.provider;

import club.tulane.rpcfx.api.RpcRequest;
import club.tulane.rpcfx.api.RpcResolver;
import club.tulane.rpcfx.api.RpcResponse;
import club.tulane.rpcfx.demo.api.OrderService;
import club.tulane.rpcfx.demo.api.UserService;
import club.tulane.rpcfx.server.RpcfxInvoker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class RpcfxServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(RpcfxServerApplication.class, args);
    }

    @Autowired
    RpcfxInvoker invoker;

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
}
