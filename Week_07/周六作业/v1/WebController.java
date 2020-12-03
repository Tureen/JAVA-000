package club.tulane.v1;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.*;

@Controller
@RestController
@RequestMapping("/web")
public class WebController {

    @RequestMapping("/master")
    public String testMaster(){
        final DataSource dataSource = (DataSource) SpringContextUtil.ctx.getBean(DataSourceConfig.MASTER);
        try (
                Connection conn = dataSource.getConnection();
                final Statement stmt = conn.createStatement();
                final ResultSet rs = stmt.executeQuery("select id from t1")
        ) {
            while (rs.next()) {
                int id = rs.getInt("id");
                System.out.println("使用master数据库, 查到t1表的id: " + id);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "master";
    }

    @RequestMapping("/slave")
    public String testSlave(){
        final DataSource dataSource = (DataSource) SpringContextUtil.ctx.getBean(DataSourceConfig.SLAVE);
        try (
                Connection conn = dataSource.getConnection();
                final Statement stmt = conn.createStatement();
                final ResultSet rs = stmt.executeQuery("select id from t1")
        ) {
            while (rs.next()) {
                int id = rs.getInt("id");
                System.out.println("使用slave数据库, 查到t1表的id: " + id);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "slave";
    }
}
