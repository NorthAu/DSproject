# 高校教材管理系统（Java Swing + JDBC）

本项目基于 **JDK 17** 与 Java Swing 构建桌面客户端，使用 H2 内存数据库（JDBC）完成教材的增删改查示例。代码按分层架构组织：`ui` 负责界面交互，`dao` 负责数据访问，`service` 实现业务校验，`entity` 封装实体，`util` 管理数据库连接与初始化。

## 主要特性
- H2 内存数据库自动建表并写入演示数据，启动即可体验。
- Swing 界面提供表单与表格联动，支持新增、修改、删除、刷新操作。
- 可独立打包成可执行 JAR，便于分发运行。

## 环境准备
- JDK 17（建议安装 OpenJDK 17+ 并将 `JAVA_HOME` 指向该版本）。
- Maven 3.9+（保证 `mvn -v` 输出的 Java 版本为 17）。

## 编译与运行
1. 下载依赖并编译打包（跳过测试）：
   ```bash
   mvn -DskipTests package
   ```
2. 运行打包后的桌面应用：
   ```bash
   java -jar target/textbook-mgmt-1.0.0.jar
   ```
3. 若需在 IDE 中直接运行，打开项目后执行 `com.example.textbookmgmt.App` 的 `main` 方法即可。

> 如果 Maven 在下载依赖时受限，请配置可用的镜像或私有仓库，然后重新执行构建命令。
