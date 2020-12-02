**Week07 作业题目（周六）：**



**1.（选做）**配置一遍异步复制，半同步复制、组复制

使用Docker进行主从数据库复制, 步骤如下

1. docker 启动 mysql 镜像的 master 实例 与 slave 实例

![image-20201202141736031](images/image-20201202141736031.png)

具体命令如下:

```shell
~ docker run -p 3316:3306 --name master -e MYSQL_ROOT_PASSWORD=123 -d mysql:5.6
~ docker run -p 3317:3306 --name slave -e MYSQL_ROOT_PASSWORD=123 -d mysql:5.6
```

启动两个mysql实例, 分别映射到主机的 3316 与 3317 端口



2. 配置 master/slave 的 my.cnf

使用命令进入 master 容器内部

```shell
~ docker exec -it master /bin/bash
~ docker exec -it slave /bin/bash
```

配置 my.cnf

```shell
~ cd etc/mysql
~ vim my.cnf

# master 下的 my.cnf 的配置文件, 新增如下信息
[mysqld]
server-id=100
sql_mode=NO_ENGINE_SUBSTITUTION,STRICT_TRANS_TABLES 
log_bin=mysql-bin
binlog-format=Row

# slave 下的 my.cnf 的配置文件, 新增如下信息
[mysqld]
server-id=101
sql_mode=NO_ENGINE_SUBSTITUTION,STRICT_TRANS_TABLES 
log_bin=mysql-bin
binlog-format=Row
```

附: 如果 vim 命令不可用, 需要 apt-get 安装, 或者 复制文件到容器

```shell
# 第一种 apt-get 安装
~ apt-get update
~ apt-get install vim

# 第二种 复制已编写好的文件
# 从容器中将文件复制到主机
~ docker cp master:/etc/mysql/my.cnf
# 从主机复制到容器内部
~ docker cp my.cnf master:/etc/mysql/my.cnf
```



3. 主节点用户创建与权限配置

```shell
~ mysql -uroot -h 127.0.0.1 -p -P 3316

mysql> CREATE USER 'repl'@'%' IDENTIFIED BY '123456';
Query OK, 0 rows affected (0.11 sec)

mysql> GRANT REPLICATION SLAVE ON *.* TO 'repl'@'%';
Query OK, 0 rows affected (0.12 sec)

mysql> flush privileges;
Query OK, 0 rows affected (0.10 sec)

mysql> show master status;
+------------------+----------+--------------+------------------+-------------------+
| File             | Position | Binlog_Do_DB | Binlog_Ignore_DB | Executed_Gtid_Set |
+------------------+----------+--------------+------------------+-------------------+
| mysql-bin.000003 |      905 |              |                  |                   |
+------------------+----------+--------------+------------------+-------------------+
1 row in set (0.00 sec)
```



4. 从节点的 slave 配置

```shell
~ mysql -uroot -h 127.0.0.1 -p -P 3317

# MASTER_HOST 填写 master 实例的ip地址, MASTER_PORT 则是 master 实例的 mysql 真实端口
# MASTER_LOG_FILE 为 master 的 binlog 的文件名, MASTER_LOG_POS 为偏移量
~ CHANGE MASTER TO MASTER_HOST='172.17.0.2', MASTER_PORT=3306,  MASTER_USER='repl', MASTER_PASSWORD='admin123', MASTER_LOG_FILE='mysql-bin.000003', MASTER_LOG_POS=905;

~ start slave;
~ show slave status\G;

mysql> show slave status\G;
*************************** 1. row ***************************
               Slave_IO_State: Waiting for master to send event
                  Master_Host: 172.17.0.2
                  Master_User: repl
                  Master_Port: 3306
                Connect_Retry: 60
              Master_Log_File: mysql-bin.000003
          Read_Master_Log_Pos: 905
               Relay_Log_File: mysqld-relay-bin.000002
                Relay_Log_Pos: 283
        Relay_Master_Log_File: mysql-bin.000003
             Slave_IO_Running: Yes
            Slave_SQL_Running: Yes
```

附: master 实例的ip , 也需要进入容器内, 用 ip addr 命令查看, 如果提示没装, 需要使用 apt-get 安装, 命令如下:

```shell
~ apt-get install iproute2 -y
```



