**Week07-Thursday**



**2.（必做）**按自己设计的表结构，插入 100 万订单模拟数据，测试不同方式的插入效率



**第一种: 存储过程**

```sql
-- 使用数据库
use tulane;
-- 清空表
truncate table t_order_info; 
-- 清掉存储过程
DROP PROCEDURE IF EXISTS InsertMoreData;

-- 创建存储过程
DELIMITER $$
use tulane$$
CREATE PROCEDURE InsertMoreData(IN num INT)
BEGIN
DECLARE i INT(2);
SET i=0;
WHILE i<num DO
	insert into t_order_info(order_number,user_id,total_price,order_status,create_time,update_time) value(i,1,'10.00',0,'1606789737370','1606789737370');
	SET i=i+1;
END WHILE;
END$$
DELIMITER ;

-- 调用存储过程
call InsertMoreData(10000);
```

![image-20201130165505071](images/image-20201130165505071.png)

仅仅调用1万次, 耗时也达到12秒



**第二种: 存储过程 (优化)**

```sql
-- 使用数据库
use tulane;
-- 清空表
truncate table t_order_info; 
-- 清掉存储过程
DROP PROCEDURE IF EXISTS InsertMoreData;

-- 创建存储过程
DELIMITER $$
use tulane$$
CREATE PROCEDURE InsertMoreData(IN num INT)
BEGIN
DECLARE i,loopNum,limitNum INT(2);
SET i=0;
SET loopNum=1;
SET limitNum=1000;

WHILE i<num DO	
	-- 分段拆分sql执行, 避免超长
	SET @sql_insert_data = "insert into t_order_info(order_number,user_id,total_price,order_status,create_time,update_time) values";
	WHILE i<num and i<loopNum*limitNum DO
		SET @sql_insert_data = CONCAT(@sql_insert_data,"(",i,",1,'10.00',0,'1606789737370','1606789737370'),");
		SET i=i+1;
	END WHILE;
	SET @sql_insert_data = LEFT((@sql_insert_data),LENGTH(@sql_insert_data)-1);

	-- select @sql_insert_data;

	PREPARE stmt FROM @sql_insert_data;
	EXECUTE stmt;
	SET loopNum=loopNum+1;
	
END WHILE;

END$$
DELIMITER ;

-- 调用存储过程
call InsertMoreData(1000000);
```

![image-20201201103842727](images/image-20201201103842727.png)

调用1万次, 仅需320毫秒

![image-20201201103749061](images/image-20201201103749061.png)

调用100万次, 需23秒左右





**第三种: 存储过程 (测试调用次数与sql长度相关度, 将sql长度作为入参)**

```sql
-- 使用数据库
use tulane;
-- 清空表
truncate table t_order_info; 
-- 清掉存储过程
DROP PROCEDURE IF EXISTS InsertMoreData;

-- 创建存储过程
DELIMITER $$
use tulane$$
CREATE PROCEDURE InsertMoreData(IN num INT, IN limitNum INT)
BEGIN
DECLARE i,loopNum INT(2);
SET i=0;
SET loopNum=1;
SET limitNum=1000;

WHILE i<num DO	
	-- 分段拆分sql执行, 避免超长
	SET @sql_insert_data = "insert into t_order_info(order_number,user_id,total_price,order_status,create_time,update_time) values";
	WHILE i<num and i<loopNum*limitNum DO
		SET @sql_insert_data = CONCAT(@sql_insert_data,"(",i,",1,'10.00',0,'1606789737370','1606789737370'),");
		SET i=i+1;
	END WHILE;
	SET @sql_insert_data = LEFT((@sql_insert_data),LENGTH(@sql_insert_data)-1);

	-- select @sql_insert_data;

	PREPARE stmt FROM @sql_insert_data;
	EXECUTE stmt;
	SET loopNum=loopNum+1;
	
END WHILE;

END$$
DELIMITER ;

-- 调用存储过程
call InsertMoreData(1000000, 1000);
```

| 限制value个数 | 执行sql数 | 耗时(sec) |
| ------------- | --------- | --------- |
| 100           | 10000     | 25.93     |
| 200           | 5000      | 22.93     |
| 500           | 2000      | 23.25     |
| 1000          | 1000      | 22.84     |
| 1500          | 667       | 22.92     |
| 2000          | 500       | 23.50     |
| 3000          | 333       | 22.77     |

实验结果为多次采样得到, 解决不稳定, 但可以通过此数据, 得知并不是压缩执行sql数, 就能线性降低耗时时间.

耗时时间也受到sql长度的影响, sql越长, 单条sql的解析、运行耗时就越慢. 所以**要找到性能表现最好的点, 优化批量插入速度**.





**第四种: JDBC的batch方式**

```java
package com.tulane.hatch.javacourse.week7;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class JdbcConnection {

    private static final String url = "jdbc:mysql://127.0.0.1:3306/db";
    private static final String user = "root";
    private static final String passwd = "123456";

    static {
        try {
            // 反射, 该类加载时会在静态块中, 向 DriverManager 注册 Driver
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void batchUse(int num) {
        try (
                final Connection conn = DriverManager.getConnection(url, user, passwd);
                final PreparedStatement pstmt = conn.prepareStatement(
                        "insert into test(val) value (?)");
        ) {
            final boolean b = conn.getMetaData().supportsBatchUpdates();
            if (!b) {
                return;
            }

            final long startTime = System.currentTimeMillis();
            conn.setAutoCommit(false);

            for (int i = 0; i < num; i++) {
                pstmt.setInt(1, i);
                pstmt.addBatch();
            }

            pstmt.executeBatch();
            conn.commit();
            final long endTime = System.currentTimeMillis();
            System.out.println(String.format("数量: %s, 耗时: %s ms", num, endTime - startTime));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        batchUse(10000);
    }
}
```

![image-20201130185256691](images/image-20201130185256691.png)

也不能达到快速插入百万数据的级别, 仅能测得1万数据, 21秒耗时. 还是SQL执行语句过多问题, 若要优化, 也和存储过程的优化方法类似.





**3.（选做）**按自己设计的表结构，插入 1000 万订单模拟数据，测试不同方式的插入效果

![image-20201201110137996](images/image-20201201110137996.png)

千万数据插入, 200个value聚合一条sql, 需要 4分10秒

![image-20201201105424748](images/image-20201201105424748.png)

千万数据插入, 1000个value聚合一条sql, 需要 4分5秒



**4.（选做）**使用不同的索引或组合，测试不同方式查询效率

如第二题的第三种方案所示





**6.（选做）**尝试自己做一个 ID 生成器（可以模拟 Seq 或 Snowflake）



实现了一个类 Leaf 的高性能分布式ID发号器, 借助 Redis 做原子性自增. 其中 Redis 不能开启异步复制



*正常发号器*

![组件时序图(发号)](images/组件时序图(发号).jpg)



*临界状态: 首次发号*

![组件时序图(首次)](images/组件时序图(首次).jpg)



*临界状态: 跨号段*

![组件时序图 (跨号段)](images/组件时序图 (跨号段).jpg)



*临界状态: 阈值预热号段*

![组件时序图 (超过阈值热备)](images/组件时序图 (超过阈值热备).jpg)



*临界状态: 宕机*

![组件时序图(Redis重启)](images/组件时序图(Redis重启).jpg)





总结: 

1. DB数据库分发号段到 Redis 中, 减少同步修改数据库的频次 (减少量级的倍数, 为号段大小)
2. Redis incr 发号, 原子性发号, 无锁方式
3. 号段到达阈值, 异步通知数据库发号段, 将同步阻塞更新号段, 改为异步发号, 让用户无需数据库同步IO的阻塞.