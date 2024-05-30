import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LiYu
 * @ClassName JsonDeomo.java
 * @Description 测试
 * @createTime 2024年05月30日 14:27:00
 */
public class JsonDemo {

    public static void main(String[] args) {
        List<JSONObject> jsonObjects = new ArrayList<>();
        //单引号的json字符串
        String jsonStr = "{'name':'LiYu'}";
        System.out.println(JSONObject.parseObject(jsonStr));

    }
}
