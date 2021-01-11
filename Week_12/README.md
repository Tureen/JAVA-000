**1.（必做）**配置 redis 的主从复制，sentinel 高可用，Cluster 集群



## Redis 主从配置

**redis.conf 重要配置**

```shell
# 绑定当前IP
bind 127.0.0.1 ::1
# 端口
port 6379
# pid文件路径
pidfile "/var/run/redis_6379.pid"
# 默认多少数据库
databases 16
# save快照操作触发时机 (900秒1次key修改 300秒10次key修改 60秒 10000次key修改)
save 900 1
save 300 10
save 60 10000
# rdb文件内容是否压缩
rdbcompression yes
# 数据存储位置
dir "/Users/kimmking/logs/redis0"

# 如果当前库是从库, 指定主库 (注释掉, 会通过命令开)
# replicaof <masterip> <masterport>

# 连接数最大配置 (生产环境要配高, 默认10000过小)
# maxclients 10000

# 最大内存
# maxmemory <bytes>

# 淘汰策略
# maxmemory-policy noeviction
```

 

**redis 从库配置**

```shell
6380> slaveof 127.0.0.1 6379
```

1. 从库请求主库
2. 主库bgsave, 并发送给从库
3. 从库全量加载数据

*从库宕机后重启会丢失主从状态, 所以需要在redis.conf中指定, 保证重启仍然有状态*



**redis 从库查看主库信息**

```shell
6380> info Replication
```



**redis 从库断开主从关系**

```shell
6380> slaveof no one
```





## Redis Sentinel 主从切换

**两种启动方式**

* redis-sentinel sentinel.conf
* redis-server redis.conf --sentinel



**sentinel.conf配置**

```shell
# 监视<mymaster> 每次变化需要2个sentinel确认
sentinel monitor mymaster 127.0.0.1 6379 2
# 60秒心跳状态不对则认为宕掉 (超过半数sentinel, 更换主节点)
sentinel down-after-milliseconds mymaster 60000
# 主库节点变更流程的超时时间 180秒
sentinel failover-timeout mymaster 180000
# 选出主节点后同步从节点的数量, 每次只允许1个从节点变更, 让其他从节点仍可使用
sentinel parallel-syncs mymaster 1
```

redis sentinel原理介绍: http://www.redis.cn/topics/sentinel.html

redis复制与高可用配置: https://www.cnblogs.com/itzhouq/p/redis5.html



## Redis Cluster 全自动分库分表

**Redis Cluster 全自动分库分表**

```shell
port 7000                                 #端口号
loglevel notice                           #日志的记录级别，notice是适合生产环境的
logfile "Logs/redis7000_log.txt"          #指定log的保持路径,默认是创建在Redis安装目录下，如果有子目录需要手动创建，如此处的Logs目录
syslog-enabled yes                        #是否使用系统日志
syslog-ident redis7000                    #在系统日志的标识名
appendonly yes                            #数据的保存为aof格式
appendfilename "appendonly.7000.aof"      #数据保存文件
cluster-enabled yes                       #是否开启集群
cluster-config-file nodes.7000.conf
cluster-node-timeout 15000
cluster-slave-validity-factor 10
cluster-migration-barrier 1
cluster-require-full-coverage yes
```

分别使用命令启动至少6个redis节点

```shell
redis-server /redis.conf
...
```

创建 cluster 集群

```shell
# replicas 1 表示集群中的每个主节点创建一个从节点
redis-cli --cluster create 127.0.0.1:7000 127.0.0.1:7001 127.0.0.1:7002 127.0.0.1:7003 127.0.0.1:7004 127.0.0.1:7005 --cluster-replicas 1
```

进入某个节点, 使用 cluster info 命令查看集群信息