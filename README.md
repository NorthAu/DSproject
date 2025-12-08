# 高校教材管理系统（Java Swing + JDBC）

本项目使用 Java Swing 构建桌面界面，利用 H2 内存数据库（JDBC）完成教材的增删改查示例，目录结构包括 `ui`、`dao`、`service`、`entity`、`util` 等模块。

## 主要特性
- H2 内存数据库自动建表，启动后即可使用。
- Swing 界面包含教材信息表格、输入表单与新增/修改/删除/刷新操作按钮。
- 分层设计：DAO 负责数据库交互，Service 处理业务逻辑，UI 负责交互展示。

## 运行
1. 确保已安装 JDK 21 与 Maven。
2. 编译打包：`mvn package`
3. 运行：`java -jar target/textbook-mgmt-1.0.0.jar`

> 如果 Maven 在下载依赖时受限，请检查网络或配置私有仓库。
