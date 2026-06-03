package com.liyu.fastjson;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.liyu.fastjson.annotation.JSONField;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 原生 Jackson 与 {@link JSON} 兼容层性能对比测试。
 * <p>
 * 运行方式：
 * <pre>{@code
 * mvn test -Dtest=JsonPerformanceTest -Dperf.excludedGroups=
 * mvn test -Dtest=JsonPerformanceTest -Dperf.excludedGroups= -Dperf.iterations=20000
 * }</pre>
 * </p>
 * <p>
 * 注意：结果受 CPU 负载、JIT 预热、GC 等影响，请在尽量空闲的环境下多次运行取参考值。
 * </p>
 */
@Tag("performance")
class JsonPerformanceTest {

    private static final int WARMUP_ROUNDS = 5;
    private static final int MEASURE_ROUNDS = 7;

    private static int iterations;
    private static ObjectMapper jacksonMapper;
    private static String complexJson;
    private static String arrayJson;
    private static Order order;

    @BeforeAll
    static void setUp() {
        iterations = Integer.getInteger("perf.iterations", 10_000);
        jacksonMapper = createJacksonMapper();
        order = buildOrder(100);
        complexJson = JSON.toJSONString(order);
        arrayJson = buildArrayJson(200);
    }

    @Test
    void correctnessCheckBeforeBenchmark() throws Exception {
        String jacksonSerialized = jacksonMapper.writeValueAsString(order);
        String compatSerialized = JSON.toJSONString(order);
        assertNotNull(jacksonSerialized);
        assertNotNull(compatSerialized);

        Order jacksonOrder = jacksonMapper.readValue(complexJson, Order.class);
        Order compatOrder = JSON.parseObject(complexJson, Order.class);
        assertEquals(jacksonOrder.getId(), compatOrder.getId());
        assertEquals(jacksonOrder.getItems().size(), compatOrder.getItems().size());

        JSONObject jsonObject = JSON.parseObject(complexJson);
        assertEquals(order.getId(), jsonObject.getLong("id"));

        List<OrderItem> jacksonList = jacksonMapper.readValue(
                arrayJson, new TypeReference<List<OrderItem>>() {});
        List<OrderItem> compatList = JSON.parseObject(
                arrayJson, new com.liyu.fastjson.TypeReference<List<OrderItem>>() {});
        assertEquals(jacksonList.size(), compatList.size());
    }

    @Test
    void compareJacksonAndCompatLayerPerformance() throws Exception {
        System.out.println();
        System.out.println("======== JSON 性能对比：原生 Jackson vs com.liyu.fastjson.JSON ========");
        System.out.printf(Locale.ROOT, "迭代次数/场景: %d, 预热轮次: %d, 测量轮次: %d%n%n",
                iterations, WARMUP_ROUNDS, MEASURE_ROUNDS);

        printHeader();

        benchmark("JavaBean 序列化",
                () -> {
                    try {
                        jacksonMapper.writeValueAsString(order);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                },
                () -> JSON.toJSONString(order));

        benchmark("JavaBean 反序列化",
                () -> {
                    try {
                        jacksonMapper.readValue(complexJson, Order.class);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                },
                () -> JSON.parseObject(complexJson, Order.class));

        benchmark("JSONObject 解析",
                () -> {
                    try {
                        jacksonMapper.readValue(complexJson, new TypeReference<Map<String, Object>>() {});
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                },
                () -> JSON.parseObject(complexJson));

        benchmark("JSONArray 解析",
                () -> {
                    try {
                        jacksonMapper.readValue(arrayJson, new TypeReference<List<Map<String, Object>>>() {});
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                },
                () -> JSON.parseArray(arrayJson));

        benchmark("泛型 List 反序列化",
                () -> {
                    try {
                        jacksonMapper.readValue(arrayJson, new TypeReference<List<OrderItem>>() {});
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                },
                () -> JSON.parseObject(arrayJson, new com.liyu.fastjson.TypeReference<List<OrderItem>>() {}));

        benchmark("JSONObject.get 读取",
                () -> readWithJacksonMap(complexJson),
                () -> readWithCompatJSONObject(complexJson));

        System.out.println();
        System.out.println("说明：");
        System.out.println("  - 比值 > 1 表示兼容层比原生 Jackson 慢（倍数）");
        System.out.println("  - JSONObject/JSONArray 场景通常有额外包装开销，比值会偏高属正常");
        System.out.println("  - JavaBean 直序列化/反序列化两者底层均走 Jackson，差距应较小");
        System.out.println("====================================================================");
        System.out.println();

        assertTrue(iterations > 0);
    }

    private static void printHeader() {
        System.out.printf(Locale.ROOT, "%-26s %12s %12s %10s %10s%n",
                "场景", "Jackson", "Compat", "比值", "Ops/s");
        System.out.println("--------------------------------------------------------------------------------");
    }

    private static void benchmark(String scene, Runnable jacksonTask, Runnable compatTask) {
        long jacksonNanos = measure(jacksonTask);
        long compatNanos = measure(compatTask);

        double jacksonMs = jacksonNanos / 1_000_000.0;
        double compatMs = compatNanos / 1_000_000.0;
        double ratio = compatMs / jacksonMs;
        double compatOps = iterations * 1_000_000_000.0 / compatNanos;

        System.out.printf(Locale.ROOT, "%-26s %9.2f ms %9.2f ms %8.2fx %9.0f%n",
                scene, jacksonMs, compatMs, ratio, compatOps);
    }

    private static long measure(Runnable task) {
        for (int i = 0; i < WARMUP_ROUNDS; i++) {
            task.run();
        }

        long total = 0L;
        for (int i = 0; i < MEASURE_ROUNDS; i++) {
            long start = System.nanoTime();
            for (int j = 0; j < iterations; j++) {
                task.run();
            }
            total += System.nanoTime() - start;
        }
        return total / MEASURE_ROUNDS;
    }

    private static void readWithJacksonMap(String json) {
        try {
            Map<String, Object> map = jacksonMapper.readValue(json, new TypeReference<Map<String, Object>>() {});
            Object id = map.get("id");
            Object items = map.get("items");
            if (id == null || items == null) {
                throw new IllegalStateException("missing fields");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void readWithCompatJSONObject(String json) {
        JSONObject object = JSON.parseObject(json);
        long id = object.getLongValue("id");
        JSONArray items = object.getJSONArray("items");
        if (id <= 0 || items == null) {
            throw new IllegalStateException("missing fields");
        }
    }

    private static ObjectMapper createJacksonMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        mapper.setTimeZone(TimeZone.getDefault());
        return mapper;
    }

    private static Order buildOrder(int itemCount) {
        Order order = new Order();
        order.setId(9_876_543_210L);
        order.setOrderNo("ORD-2026-0603");
        order.setBuyerName("性能测试用户");
        order.setCreateTime(new Date());
        order.setPaid(true);
        order.setAmount(12_345.67D);

        List<OrderItem> items = new ArrayList<>(itemCount);
        for (int i = 0; i < itemCount; i++) {
            OrderItem item = new OrderItem();
            item.setSku("SKU-" + i);
            item.setProductName("商品-" + i);
            item.setQuantity(i % 5 + 1);
            item.setPrice(19.9D + i);
            items.add(item);
        }
        order.setItems(items);

        Map<String, String> tags = new LinkedHashMap<>();
        tags.put("channel", "web");
        tags.put("region", "cn-east");
        order.setTags(tags);
        return order;
    }

    private static String buildArrayJson(int size) {
        StringBuilder sb = new StringBuilder(size * 64 + 2);
        sb.append('[');
        for (int i = 0; i < size; i++) {
            if (i > 0) {
                sb.append(',');
            }
            sb.append("{\"sku\":\"SKU-").append(i)
                    .append("\",\"product_name\":\"商品-").append(i)
                    .append("\",\"quantity\":").append(i % 5 + 1)
                    .append(",\"price\":").append(19.9D + i)
                    .append('}');
        }
        sb.append(']');
        return sb.toString();
    }

    /** 测试用订单模型，Jackson 与 @JSONField 双注解保证两边字段一致。 */
    public static class Order {
        private Long id;
        private String orderNo;

        @JsonProperty("buyer_name")
        @JSONField(name = "buyer_name")
        private String buyerName;

        private Date createTime;
        private boolean paid;
        private double amount;
        private List<OrderItem> items;
        private Map<String, String> tags;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getOrderNo() {
            return orderNo;
        }

        public void setOrderNo(String orderNo) {
            this.orderNo = orderNo;
        }

        public String getBuyerName() {
            return buyerName;
        }

        public void setBuyerName(String buyerName) {
            this.buyerName = buyerName;
        }

        public Date getCreateTime() {
            return createTime;
        }

        public void setCreateTime(Date createTime) {
            this.createTime = createTime;
        }

        public boolean isPaid() {
            return paid;
        }

        public void setPaid(boolean paid) {
            this.paid = paid;
        }

        public double getAmount() {
            return amount;
        }

        public void setAmount(double amount) {
            this.amount = amount;
        }

        public List<OrderItem> getItems() {
            return items;
        }

        public void setItems(List<OrderItem> items) {
            this.items = items;
        }

        public Map<String, String> getTags() {
            return tags;
        }

        public void setTags(Map<String, String> tags) {
            this.tags = tags;
        }
    }

    /** 测试用订单明细。 */
    public static class OrderItem {
        private String sku;

        @JsonProperty("product_name")
        @JSONField(name = "product_name")
        private String productName;

        private int quantity;
        private double price;

        public String getSku() {
            return sku;
        }

        public void setSku(String sku) {
            this.sku = sku;
        }

        public String getProductName() {
            return productName;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
        }
    }
}
