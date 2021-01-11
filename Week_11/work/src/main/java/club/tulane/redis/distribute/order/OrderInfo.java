package club.tulane.redis.distribute.order;

import lombok.Data;

@Data
public class OrderInfo {

    private String orderId;
    private String orderDesc;
    private String orderTime;
}
