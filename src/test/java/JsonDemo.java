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
        //消耗时间
        long start = System.currentTimeMillis();
        for (int i = 0;i<10;i++) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.add("name", "LiYu")
                    .add("age", 18);
            ;
            jsonObjects.add(jsonObject);
        }
        long end = System.currentTimeMillis();
        System.out.println("消耗时间："+(end-start));

    }
}
