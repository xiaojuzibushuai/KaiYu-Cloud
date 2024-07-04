
鼠鼠设计的公司教育平台后端应该为：
内容管理微服务： 进行后台相关数据管理
媒资管理微服务： 进行平台各种资源上传下载管理
学习中心微服务： 进行用户课程学习记录、学习资格等校验
平台订单微服务： 进行平台支付相关功能
平台消息微服务： 进行平台各种消息发送接收功能
平台第三方微服务： 进行平台第三方服务集成功能 如 验证码 短信等


~~~
com.ruoyi     
├── ruoyi-ui              // 前端框架 [80]
├── ruoyi-gateway         // 网关模块 [8080]
├── ruoyi-auth            // 认证中心 [9200]
├── ruoyi-api             // 接口模块
│       └── ruoyi-api-system                          // 系统接口
├── ruoyi-common          // 通用模块
│       └── ruoyi-common-core                         // 核心模块
│       └── ruoyi-common-datascope                    // 权限范围
│       └── ruoyi-common-datasource                   // 多数据源
│       └── ruoyi-common-log                          // 日志记录
│       └── ruoyi-common-redis                        // 缓存服务
│       └── ruoyi-common-seata                        // 分布式事务
│       └── ruoyi-common-security                     // 安全模块
│       └── ruoyi-common-swagger                      // 系统接口
├── ruoyi-modules         // 业务模块
│       └── kaiyu-content                             // 内容管理 [9206]
│       └── kaiyu-media                               // 媒资管理 [9207]
│       └── kaiyu-learning                            // 学习中心 [9208]
│       └── kaiyu-order                               // 订单中心 [9209]
│       └── ruoyi-system                              // 系统模块 [9201]
│       └── ruoyi-gen                                 // 代码生成 [9202]
│       └── ruoyi-job                                 // 定时任务 [9203]
│       └── ruoyi-file                                // 文件服务 [9300]
├── ruoyi-visual          // 图形化管理模块
│       └── ruoyi-visual-monitor                      // 监控中心 [9100]
├──pom.xml                // 公共依赖
~~~

