# 高校教材管理系统（Java Swing + JDBC）

本项目基于 **JDK 21** 与 Java Swing 构建桌面客户端，使用 JDBC 访问本地 **MySQL(TextBookManager)** 数据库。代码按分层架构组织：`ui` 负责界面交互，`dao` 负责数据访问，`service` 实现业务校验，`entity` 封装实体，`util` 管理数据库连接与初始化。

## 主要特性
- 默认使用本地 MySQL 数据库（库名 `TextBookManager`），每次启动都会检查并创建缺失的表/触发器/存储过程并写入演示数据。
- Swing 界面提供表单与表格联动，支持教材、出版社、教材类型、订购、入库/领用的录入与查询，并在教材列表中展示教材类型。
- 可独立打包成可执行 JAR，便于分发运行。
- 内置 ISBN 规则校验（必须以 `ISBN` 开头并跟随 10 位数字），触发器自动维护库存，存储过程输出订购/到货/发放统计。

## 环境准备
- JDK 21（建议安装 OpenJDK 21+ 并将 `JAVA_HOME` 指向该版本）。
- Maven 3.9+（保证 `mvn -v` 输出的 Java 版本为 21）。

> 变更说明：项目已从 JDK 17 升级到 **JDK 21**，请在本地与 CI 环境中同步更新所用 JDK 版本后再进行构建与运行。

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

## 功能模块速览
- **教材维护**：录入/修改教材信息，ISBN 需满足 `ISBNXXXXXXXXXX` 格式；库存非负；支持一键刷新同步数据库中的最新数据。
  新增教材类型列，方便在表格中直接查看分类。
- **出版社管理**：维护出版社名称与联系方式，可在教材表单中直接关联。
- **教材类型管理**：维护教材分类并可在教材表单中选择绑定。
- **教材订购**：搜索选择教材（名称+ISBN），填写订购/到货数量与状态，日期通过 `yyyy-MM-dd` 选择器录入。
- **入库与领用**：搜索选择教材（名称+ISBN），通过 “IN/OUT” 方向记录入库或领用，录入日期，触发器会自动同步教材库存。

## 库表、规则与触发器
- 表结构：`publishers`、`textbook_types`、`textbooks`、`textbook_orders`、`inventory_transactions`，均由 `DatabaseUtil` 启动时自动创建并填充示例数据。
- 约束：`textbooks.isbn` 启用格式检查，同时在业务层二次校验必须以 `ISBN` 开头且后续 10 位为数字。
- 触发器：`trg_inventory_insert` 在每次新增入库/领用流水时自动增加或扣减 `textbooks.stock`。
- 存储过程：`sp_textbook_stats` 汇总各教材订购数量、到货数量（IN）与发放数量（OUT）。

### 在 MySQL 中查看触发器/存储过程
```sql
SHOW TRIGGERS LIKE 'inventory_transactions';
CALL sp_textbook_stats();
```

## 数据库切换与配置说明
### 本地 MySQL（库名 TextBookManager）
1. 安装并启动 MySQL 8+。应用会在首次连接时自动创建 **TextBookManager** 数据库并建表/触发器/存储过程。
2. 配置连接参数（任选其一）：
   - **环境变量**：
     ```bash
     export DB_URL="jdbc:mysql://localhost:3306/TextBookManager?useSSL=false&serverTimezone=UTC"
     export DB_USER="你的用户名"
     export DB_PASSWORD="你的密码"
     ```
   - **IDEA VM Options**（运行配置的 VM options 一栏）：
     ```
     -DDB_URL=jdbc:mysql://localhost:3306/TextBookManager?useSSL=false&serverTimezone=UTC -DDB_USER=你的用户名 -DDB_PASSWORD=你的密码
     ```
   未显式指定时，程序默认使用 `root` 用户、空密码及上述默认 URL。
3. 在 IntelliJ IDEA 中连接数据库（可选）：添加 MySQL 数据源，URL 使用 `jdbc:mysql://localhost:3306/TextBookManager`，用户名/密码与上方保持一致，即可在 IDE 内浏览表结构与数据。

> 所有数据库初始化均通过 `util/DatabaseUtil` 执行，开箱即用 MySQL，无需 H2。
