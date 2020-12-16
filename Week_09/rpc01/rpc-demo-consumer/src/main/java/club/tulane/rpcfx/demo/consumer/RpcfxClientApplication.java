package club.tulane.rpcfx.demo.consumer;

import club.tulane.rpcfx.client.RpcfxFactory;
import club.tulane.rpcfx.demo.api.Order;
import club.tulane.rpcfx.demo.api.OrderService;
import club.tulane.rpcfx.demo.api.User;
import club.tulane.rpcfx.demo.api.UserService;

public class RpcfxClientApplication {

    public static void main(String[] args) {
        UserService userService = RpcfxFactory.getInstance().create(UserService.class, "http://localhost:8080/");
        final User user = userService.findById(1);
        System.out.println("find user id=1 from server: " + user.getName());

        OrderService orderService = RpcfxFactory.getInstance().create(OrderService.class, "http://localhost:8080/");
        final Order order = orderService.findOrderById(1992129);
        System.out.println(String.format("find order name=%s, amount=%f", order.getName(), order.getAmount()));
    }
}
