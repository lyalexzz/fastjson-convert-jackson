# fastjson-convert-jackson

基于 **Jackson** 实现的 Fastjson API 兼容库。业务项目可从阿里巴巴 Fastjson 平滑迁移：删除原 fastjson 依赖、引入本包，全局替换 import 包名即可，**`JSON` / `JSONObject` / `JSONArray` 等写法保持不变**。

---

## 目录

- [为什么需要这个库](#为什么需要这个库)
- [正式项目接入指南](#正式项目接入指南)
  - [接入前检查](#接入前检查)
  - [Maven 项目](#maven-项目)
  - [Gradle 项目](#gradle-项目)
  - [本地 / 私服安装](#本地--私服安装)
  - [迁移步骤（推荐顺序）](#迁移步骤推荐顺序)
  - [Spring Boot 项目注意事项](#spring-boot-项目注意事项)
  - [多模块项目](#多模块项目)
  - [上线前验证清单](#上线前验证清单)
  - [灰度与回滚建议](#灰度与回滚建议)
- [用法示例](#用法示例)
- [已支持的 API](#已支持的-api)
- [与原版 Fastjson 的差异](#与原版-fastjson-的差异)
- [性能说明](#性能说明)
- [常见问题](#常见问题)
- [本地构建](#本地构建)

---

## 为什么需要这个库

- Fastjson 1.x 存在较多安全漏洞，维护成本高
- 直接换 Jackson/Gson 需要改动大量 `JSONObject`、`JSONArray` 相关代码
- 本库在底层使用 Jackson，对外暴露与 Fastjson 一致的 API

---

## 正式项目接入指南

### 接入前检查

在动手改代码前，建议先确认以下几点：

| 检查项 | 说明 |
|--------|------|
| Java 版本 | 需要 **Java 8+** |
| 是否使用 AutoType | 若业务依赖 `@type` / `AutoType`，**本库不支持**，需先改造相关业务 |
| 是否使用 fastjson2 | 本库兼容 **fastjson 1.x** API，不是 fastjson2 |
| Jackson 版本 | 本库自带 Jackson 依赖；若项目已有 Jackson，注意版本冲突（见下文） |
| 第三方传递依赖 | 用 `mvn dependency:tree \| grep fastjson` 排查间接引入的 fastjson |

**快速排查命令（Maven）：**

```bash
# 查找项目中所有 fastjson 引用
mvn dependency:tree -Dincludes=com.alibaba:fastjson

# 查找代码中的 import
rg "import com\.alibaba\.fastjson" --type java
```

---

### Maven 项目

**1. 在父 POM 或 BOM 中统一管理版本（推荐）：**

```xml
<properties>
    <fastjson-convert-jackson.version>1.0.0</fastjson-convert-jackson.version>
</properties>

<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>com.liyu</groupId>
            <artifactId>fastjson-convert-jackson</artifactId>
            <version>${fastjson-convert-jackson.version}</version>
        </dependency>
    </dependencies>
</dependencyManagement>
```

**2. 各模块引入依赖：**

```xml
<dependency>
    <groupId>com.liyu</groupId>
    <artifactId>fastjson-convert-jackson</artifactId>
</dependency>
```

**3. 删除原有 fastjson 依赖：**

```xml
<!-- 删除以下依赖 -->
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>fastjson</artifactId>
</dependency>
```

**4. 若父 POM 通过 `<exclusions>` 传递引入了 fastjson，一并清理。**

---

### Gradle 项目

**Groovy DSL：**

```groovy
dependencies {
    implementation 'com.liyu:fastjson-convert-jackson:1.0.0'
    // 删除: implementation 'com.alibaba:fastjson:...'
}
```

**Kotlin DSL：**

```kotlin
dependencies {
    implementation("com.liyu:fastjson-convert-jackson:1.0.0")
}
```

---

### 本地 / 私服安装

若尚未发布到 Maven Central / 公司私服，可先在本地安装：

```bash
git clone <本仓库地址>
cd fastjson-convert-jackson
mvn clean install -DskipTests
```

安装后，业务项目 `pom.xml` 中正常引用 `com.liyu:fastjson-convert-jackson:1.0.0` 即可。

**发布到公司 Nexus / Artifactory 私服：**

```bash
mvn clean deploy -DskipTests
```

> 私服地址、账号在项目的 `settings.xml` 或 CI 中配置，按公司规范操作即可。

---

### 迁移步骤（推荐顺序）

按以下顺序操作，可最大程度降低风险：

#### 第一步：新建分支，替换依赖

```text
feature/replace-fastjson
```

- 删除 `com.alibaba:fastjson`
- 添加 `com.liyu:fastjson-convert-jackson`
- 执行 `mvn dependency:tree`，确认无 fastjson 残留

#### 第二步：全局替换 import

IDE 使用 **Replace in Path**（建议分四条规则依次执行）：

| 查找 | 替换为 |
|------|--------|
| `com.alibaba.fastjson.annotation` | `com.liyu.fastjson.annotation` |
| `com.alibaba.fastjson.parser` | `com.liyu.fastjson.parser` |
| `com.alibaba.fastjson.serializer` | `com.liyu.fastjson.serializer` |
| `com.alibaba.fastjson` | `com.liyu.fastjson` |

> **注意替换顺序**：先替换子包（`annotation` / `parser` / `serializer`），最后替换 `com.alibaba.fastjson`，避免重复替换。

#### 第三步：编译修复

```bash
mvn clean compile -DskipTests
```

常见编译问题：

- 引用了本库未实现的类（如 `SerializeConfig`、`ParserConfig`、`JSONPath`）→ 需单独改造或提 Issue
- 使用了 `AutoType` 相关 API → 必须移除，改为显式类型

#### 第四步：跑测试

```bash
mvn test
```

重点回归：

- 接口入参 / 出参 JSON 序列化
- 数据库 JSON 字段读写
- 消息队列（Kafka / RabbitMQ 等）消息体解析
- 配置文件 / 缓存中的 JSON 字符串处理

#### 第五步：联调 & 上线

见 [上线前验证清单](#上线前验证清单) 和 [灰度与回滚建议](#灰度与回滚建议)。

---

### Spring Boot 项目注意事项

Spring Boot 默认使用 Jackson 作为 HTTP 消息转换器。**本库与 Spring MVC 的 Jackson 相互独立**，接入后通常无需改动 `@RestController` 的 `@RequestBody` / `@ResponseBody` 逻辑。

| 场景 | 建议 |
|------|------|
| Controller 用 `@RequestBody User` | **不用改**，继续走 Spring 内置 Jackson |
| Service 层手动调用 `JSON.parseObject` | **替换 import** 即可，写法不变 |
| 混用 `JSONObject` 和 `@RequestBody` | 可以共存；`JSONObject` 走本库，Bean 走 Spring Jackson |
| 统一 Jackson 版本 | 在 Spring Boot 父 POM 中管理 `jackson-bom`，与本库版本尽量接近，避免 classpath 冲突 |
| `@JSONField` 与 `@JsonProperty` | 本库识别 `@JSONField`；Spring MVC 默认识别 Jackson 注解。若同一字段两处都用，建议统一为 `@JSONField` 或同时保留两者 |

**Spring Boot 依赖示例：**

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
    <groupId>com.liyu</groupId>
    <artifactId>fastjson-convert-jackson</artifactId>
    <version>1.0.0</version>
</dependency>
```

**Jackson 版本冲突排查：**

```bash
mvn dependency:tree -Dincludes=com.fasterxml.jackson.core
```

若出现多个 Jackson 版本，在 Spring Boot 的 `dependencyManagement` 中统一版本即可，一般不影响本库运行。

---

### 多模块项目

典型分层结构示例：

```text
parent/
├── api/          # 对外 DTO，可能有 @JSONField
├── service/      # 业务逻辑，大量使用 JSON.parseObject
├── web/          # Controller
└── common/       # 工具类，JSON 工具集中在这里
```

**推荐做法：**

1. 在 **parent POM** 的 `dependencyManagement` 中声明本库版本
2. 只在 **实际用到 fastjson API 的模块** 中引入依赖（不必所有模块都引）
3. 若 `common` 模块封装了 JSON 工具类，优先改这一处，其他模块间接受益

**common 模块封装示例：**

```java
package com.example.common.json;

import com.liyu.fastjson.JSON;
import com.liyu.fastjson.JSONObject;
import com.liyu.fastjson.TypeReference;

public final class JsonUtils {

    private JsonUtils() {}

    public static String toJson(Object obj) {
        return JSON.toJSONString(obj);
    }

    public static <T> T parse(String json, Class<T> clazz) {
        return JSON.parseObject(json, clazz);
    }

    public static <T> T parse(String json, TypeReference<T> typeRef) {
        return JSON.parseObject(json, typeRef);
    }

    public static JSONObject parseObject(String json) {
        return JSON.parseObject(json);
    }
}
```

业务代码逐步从直接 `import com.liyu.fastjson.JSON` 改为 `JsonUtils`，后续更换 JSON 实现只需改 common 模块。

---

### 上线前验证清单

上线前建议逐项确认：

- [ ] 全项目无 `com.alibaba.fastjson` 的 import 和依赖
- [ ] `mvn dependency:tree` 无 fastjson 传递依赖
- [ ] 单元测试 / 集成测试全部通过
- [ ] 核心接口 JSON 字段名、字段顺序（若依赖顺序）与迁移前一致
- [ ] 日期格式符合预期（默认 `yyyy-MM-dd HH:mm:ss`）
- [ ] `null` 值字段：默认**不输出**（与 fastjson 默认行为略有差异，若业务依赖输出 null 需加 `SerializerFeature.WriteMapNullValue`）
- [ ] 未使用 AutoType / `@type`（本库不支持）
- [ ] 日志 / 监控中无新增 `JSONException` 异常尖刺
- [ ] 在测试环境完成至少一轮全链路回归

---

### 灰度与回滚建议

**灰度策略：**

1. 先在 **测试环境** 完整回归
2. 再发 **预发环境**，对比核心接口响应 JSON 是否与旧版一致
3. 生产环境 **单实例灰度** → 扩大至全量

**回滚方案：**

迁移仅涉及依赖和 import 替换，回滚成本低：

1. Git revert 迁移提交
2. 恢复 `com.alibaba:fastjson` 依赖
3. 重新部署

建议在迁移分支保留至少一个版本周期，确认稳定后再删除 fastjson 相关代码。

---

## 用法示例

与 Fastjson 1.x 写法完全一致，仅 import 包名不同：

```java
import com.liyu.fastjson.JSON;
import com.liyu.fastjson.JSONObject;
import com.liyu.fastjson.JSONArray;
import com.liyu.fastjson.TypeReference;
import com.liyu.fastjson.annotation.JSONField;
import com.liyu.fastjson.serializer.SerializerFeature;

// 字符串 -> JSONObject
JSONObject obj = JSON.parseObject("{\"name\":\"张三\",\"age\":18}");
String name = obj.getString("name");
int age = obj.getIntValue("age");

// 字符串 -> JavaBean
User user = JSON.parseObject(json, User.class);

// 泛型 List
List<User> users = JSON.parseObject(json, new TypeReference<List<User>>() {});

// JSONArray
JSONArray array = JSON.parseArray("[{\"id\":1},{\"id\":2}]");
JSONObject first = array.getJSONObject(0);

// 序列化
String json = JSON.toJSONString(user);

// 格式化输出
String pretty = JSON.toJSONString(user, SerializerFeature.PrettyFormat);

// 输出 null 字段
String withNull = JSON.toJSONString(obj, SerializerFeature.WriteMapNullValue);

// 链式构建
JSONObject body = new JSONObject()
        .fluentPut("code", 0)
        .fluentPut("message", "success")
        .fluentPut("data", user);

// JSONObject 转 JavaBean
User user2 = obj.toJavaObject(User.class);
```

**JavaBean 字段重命名：**

```java
public class User {
    @JSONField(name = "user_name")
    private String userName;
}
```

---

## 已支持的 API

| 类别 | 类 / 接口 |
|------|-----------|
| 核心 | `JSON`, `JSONObject`, `JSONArray`, `TypeReference`, `JSONException` |
| 注解 | `@JSONField`（name / serialize / deserialize 等常用属性） |
| 解析特性 | `parser.Feature`（AllowComment、AllowSingleQuotes 等） |
| 序列化特性 | `serializer.SerializerFeature`（WriteMapNullValue、PrettyFormat 等） |

### JSON 常用方法

- `parse` / `parseObject` / `parseArray`
- `toJSONString` / `toJSONBytes` / `toJSON`
- `isValid` / `isValidObject` / `isValidArray`
- 支持 `Class`、`Type`、`TypeReference`、`byte[]`、`InputStream`、`Reader`

### JSONObject / JSONArray 常用方法

- `getString` / `getInteger` / `getIntValue` / `getLong` / `getBoolean` ...
- `getJSONObject` / `getJSONArray` / `getObject`
- `toJavaObject` / `toJavaList`
- `fluentPut` / `fluentAdd` 等链式写法
- 实现 `Map` / `List` 标准接口

---

## 与原版 Fastjson 的差异

本库目标是覆盖 **90%+ 常见业务场景**，以下高级特性暂未完整实现或行为可能略有差异：

| 特性 | 说明 | 正式项目建议 |
|------|------|-------------|
| `AutoType` / `@type` | **不支持** | 移除所有 AutoType 用法，改为显式 Class |
| `SerializeConfig` / `ParserConfig` | 未单独暴露 | 使用默认配置，或提 Issue 说明需求 |
| 自定义 `ObjectSerializer` | 未实现 | 使用 Jackson 标准扩展 |
| `WriteNullStringAsEmpty` 等 | 行为可能略有差异 | 上线前针对 null 字段做对比测试 |
| 默认不输出 null 字段 | Jackson 默认 `NON_NULL` | 需要输出 null 时加 `WriteMapNullValue` |
| `JSONObject` 动态代理 | 未实现 | 改用显式 `getXxx` 或 `toJavaObject` |
| `JSONPath` | 未实现 | 使用其他 JSONPath 库或手动解析 |

如遇到未覆盖的 API，欢迎提 Issue。

---

## 性能说明

本库底层是 Jackson，性能与原生 Jackson 的关系：

| 场景 | 预期 |
|------|------|
| JavaBean 序列化 / 反序列化 | 接近原生 Jackson（约 1.1~1.3 倍耗时） |
| `JSONObject` / `JSONArray` 解析 | 有额外包装开销，通常比原生 Jackson 慢 1.3~2 倍 |
| 日常业务接口 | 一般可忽略（JSON 通常不是瓶颈） |

项目内提供了性能对比测试，可在本地运行：

```bash
mvn test -Dtest=JsonPerformanceTest -Dperf.excludedGroups=
```

---

## 常见问题

**Q：替换后编译报错「找不到 SerializeConfig」？**

A：本库未实现 `SerializeConfig`，需删除相关代码，或改用 `SerializerFeature` 枚举传参。

**Q：JSON 字段 null 值消失了？**

A：默认不序列化 null 字段。需要保留 null 时：

```java
JSON.toJSONString(obj, SerializerFeature.WriteMapNullValue);
```

**Q：和 Spring Boot 自带的 Jackson 会冲突吗？**

A：不会冲突。两者可共存：HTTP 层走 Spring Jackson，工具类走本库。注意 classpath 中 Jackson 版本尽量统一。

**Q：能否不改 import，只换依赖？**

A：本库包名是 `com.liyu.fastjson`，必须改 import。若希望零 import 改动，需要将包名改为 `com.alibaba.fastjson`（可自行 fork 调整）。

**Q：fastjson 1.x 和 fastjson2 都能替吗？**

A：本库针对 **fastjson 1.x** API。fastjson2 包名和 API 不同，不适用。

**Q：生产环境推荐哪个版本？**

A：当前稳定版 **1.0.0**。建议在 `dependencyManagement` 中锁定版本，避免 SNAPSHOT。

---

## 本地构建

```bash
# 编译 + 单元测试
mvn clean test

# 安装到本地 Maven 仓库
mvn clean install

# 运行性能对比测试
mvn test -Dtest=JsonPerformanceTest -Dperf.excludedGroups=
```

## 技术栈

- Java 8+
- Jackson 2.17.x
- JUnit 5

## License

MIT
