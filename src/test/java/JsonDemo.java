import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.Map;

/**
 * @author LiYu
 * @ClassName JsonDeomo.java
 * @Description 测试
 * @createTime 2024年05月30日 14:27:00
 */
public class JsonDemo {

    public static void main(String[] args) {
        //List<JSONObject> jsonObjects = new ArrayList<>();
        testType();
    }

    // test TypeReference
    public static void testType() {
        String jsonString = "{\"name\":\"tengyy\",\"age\":18}";
        Map<String, Object> map = JSON.parseObject(jsonString, new TypeReference<Map<String, Object>>() {});
        System.out.println(map);
    }
}
