package club.tulane.v2;

import club.tulane.v1.SpringContextUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

@Controller
@RestController
@RequestMapping("/web/v2")
public class WebControllerV2 {

    @RequestMapping("/master")
    public String testMaster(){
        final DataSource dataSource = (DataSource) SpringContextUtil.ctx.getBean(DynamicDataSourceConfig.DYNAMIC_DATA_SOURCE);
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
    @RealOnly
    public String testSlave(){
        final DataSource dataSource = (DataSource) SpringContextUtil.ctx.getBean(DynamicDataSourceConfig.DYNAMIC_DATA_SOURCE);
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
