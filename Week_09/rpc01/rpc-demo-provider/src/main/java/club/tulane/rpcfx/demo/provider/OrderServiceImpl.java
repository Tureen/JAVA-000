package club.tulane.rpcfx.demo.provider;

import club.tulane.rpcfx.demo.api.Order;
import club.tulane.rpcfx.demo.api.OrderService;

public class OrderServiceImpl implements OrderService {

    @Override
    public Order findOrderById(int id) {
        return new Order(id, "Cuijing" + System.currentTimeMillis(), 9.9f);
    }
}
