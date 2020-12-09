package club.tulane.shardinghor.horizontal;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;

@Controller
@RestController
@RequestMapping("/web")
public class WebController {

    @Resource
    private DataSource dataSource;

    @GetMapping("/save")
    public String save(){

        try (
                Connection conn = dataSource.getConnection();
                final Statement stmt = conn.createStatement();
        ) {
            // 新增一个
            stmt.execute("insert into `tulane`.`t_order_info` ( `id`, `update_time`, `order_status`, `user_id`, `total_price`, `create_time`, `order_number`) values ( 2, null, '1', 2, '10.00', null, '0001');");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "save";
    }
}
