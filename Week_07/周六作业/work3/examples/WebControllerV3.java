package club.tulane.shardingdatabase.examples;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

@Controller
@RestController
@RequestMapping("/web")
public class WebControllerV3 {

    @Resource(name = "dataSource")
    private DataSource dataSource;

    @RequestMapping("/save")
    public String save(){
        try (
                Connection conn = dataSource.getConnection();
                final Statement stmt = conn.createStatement();

        ) {
            // 新增一个
            stmt.execute("insert into t1(id) value(99)");

            final ResultSet rs = stmt.executeQuery("select id from t1");
            // 查看是否有新增值, 有则说明自动路由到master
            System.out.println("--------------------------");
            while (rs.next()) {
                int id = rs.getInt("id");
                System.out.println("使用master数据库, 查到t1表的id: " + id);
            }
            System.out.println("--------------------------");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "save";
    }

    @RequestMapping("/query")
    public String query(){
        try (
                Connection conn = dataSource.getConnection();
                final Statement stmt = conn.createStatement();
                final ResultSet rs = stmt.executeQuery("select id from t1")
        ) {
            System.out.println("--------------------------");
            while (rs.next()) {
                int id = rs.getInt("id");
                System.out.println("使用slave数据库, 查到t1表的id: " + id);
            }
            System.out.println("--------------------------");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "query";
    }
}
