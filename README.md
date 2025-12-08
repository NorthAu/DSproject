# 高校教材管理系统（Java Swing + JDBC）

本项目基于 **JDK 17** 与 Java Swing 构建桌面客户端，使用 JDBC 访问数据库，默认启用 H2 内存库并支持切换至本地 **MySQL(TextBookManager)**。代码按分层架构组织：`ui` 负责界面交互，`dao` 负责数据访问，`service` 实现业务校验，`entity` 封装实体，`util` 管理数据库连接与初始化。

## 主要特性
- 默认使用 H2 内存数据库自动建表并写入演示数据，启动即可体验。
- 支持在本地切换至 MySQL 数据库（库名 `TextBookManager`），持久化保存数据。
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
3. IDE 运行：直接执行 `com.example.textbookmgmt.App` 的 `main` 方法即可。

> 如果 Maven 在下载依赖时受限，请配置可用的镜像或私有仓库，然后重新执行构建命令。

## 数据库切换与配置说明
### 默认的 H2 内存库
- 无需额外安装，应用启动时自动建表并写入一条演示教材记录。
- 适合快速体验或临时演示，进程退出后数据即被清空。

### 本地 MySQL（库名 TextBookManager）
1. 安装并启动 MySQL 8+，创建数据库：
   ```sql
   CREATE DATABASE IF NOT EXISTS TextBookManager CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```
   应用会在首次连接时自动建表并补充示例数据，无需手动建表。
2. 配置连接参数（任选其一）：
   - **环境变量**：
     ```bash
     export DB_PROFILE=mysql
     export DB_URL="jdbc:mysql://localhost:3306/TextBookManager?useSSL=false&serverTimezone=UTC"
     export DB_USER="你的用户名"
     export DB_PASSWORD="你的密码"
     ```
   - **IDEA VM Options**（运行配置的 VM options 一栏）：
     ```
     -Ddb.profile=mysql -DDB_URL=jdbc:mysql://localhost:3306/TextBookManager?useSSL=false&serverTimezone=UTC -DDB_USER=你的用户名 -DDB_PASSWORD=你的密码
     ```
   未显式指定时，程序默认使用 H2 内存库；若只指定 `db.profile=mysql`，其余参数将使用 `root` 用户、空密码及上述默认 URL。
3. 在 IntelliJ IDEA 中连接数据库（可选）：添加 MySQL 数据源，URL 使用 `jdbc:mysql://localhost:3306/TextBookManager`，用户名/密码与上方保持一致，即可在 IDE 内浏览表结构与数据。

> 所有数据库初始化均通过 `util/DatabaseUtil` 执行，H2 与 MySQL 采用相同的建表及示例数据脚本，保持两端功能一致。
