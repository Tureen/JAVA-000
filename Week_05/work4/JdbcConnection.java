package com.tulane.hatch.javacourse.week5.work4;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.*;

public class JdbcConnection {

    private static final String url = "jdbc:mysql://5948fbcc4d08d.gz.cdb.myqcloud.com:10633/a_tulane_test";
    private static final String user = "pinuser";
    private static final String passwd = "xdp170620";

    static {
        try {
            // 反射, 该类加载时会在静态块中, 向 DriverManager 注册 Driver
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void simpleUse() {
        try (
                final Connection conn = DriverManager.getConnection(url, user, passwd);
                final Statement stmt = conn.createStatement();
                final ResultSet rs = stmt.executeQuery("select count(1) from test")
        ) {
            while (rs.next()) {
                int count = rs.getInt("count(1)");
                System.out.println(count);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void batchUse() {
        try (
                final Connection conn = DriverManager.getConnection(url, user, passwd);
                final PreparedStatement pstmt = conn.prepareStatement(
                        "insert into test(id, name) value (?, ?)");
        ) {
            final boolean b = conn.getMetaData().supportsBatchUpdates();
            if (!b) {
                return;
            }

            conn.setAutoCommit(false);

            pstmt.setInt(1, 1);
            pstmt.setString(2, "小明");
            pstmt.addBatch();

            pstmt.setInt(1, 2);
            pstmt.setString(2, "小红");
            pstmt.addBatch();

            pstmt.setInt(1, 3);
            pstmt.setString(2, "小王");
            pstmt.addBatch();

            final int[] ints = pstmt.executeBatch();
            conn.commit();
            System.out.println(ints.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void hikariCP() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setUsername(user);
        config.setPassword(passwd);
        config.setAutoCommit(false);
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", 300);

        final HikariDataSource dataSource = new HikariDataSource(config);

        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(
                        "insert into test(id, name) value (?, ?)")
        ) {
            pstmt.setInt(1, 1);
            pstmt.setString(2, "小明");
            pstmt.addBatch();

            pstmt.setInt(1, 2);
            pstmt.setString(2, "小红");
            pstmt.addBatch();

            pstmt.setInt(1, 3);
            pstmt.setString(2, "小王");
            pstmt.addBatch();

            final int[] ints = pstmt.executeBatch();
            conn.commit();
            System.out.println(ints.length);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
