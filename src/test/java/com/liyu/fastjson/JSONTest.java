package com.liyu.fastjson;

import com.liyu.fastjson.annotation.JSONField;
import com.liyu.fastjson.serializer.SerializerFeature;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JSONTest {

    @Test
    void parseObjectAndGetValues() {
        String json = "{\"name\":\"张三\",\"age\":25,\"active\":true,\"score\":98.5}";
        JSONObject obj = JSON.parseObject(json);

        assertEquals("张三", obj.getString("name"));
        assertEquals(25, obj.getIntValue("age"));
        assertTrue(obj.getBooleanValue("active"));
        assertEquals(98.5, obj.getDoubleValue("score"), 0.001);
    }

    @Test
    void parseArrayAndTraverse() {
        String json = "[{\"name\":\"a\"},{\"name\":\"b\"}]";
        JSONArray array = JSON.parseArray(json);

        assertEquals(2, array.size());
        assertEquals("a", array.getJSONObject(0).getString("name"));
        assertEquals("b", array.getJSONObject(1).getString("name"));
    }

    @Test
    void toJSONStringFromJavaBean() {
        User user = new User();
        user.setId(1L);
        user.setUserName("jack");

        String json = JSON.toJSONString(user);
        assertTrue(json.contains("\"user_name\":\"jack\""));
        assertTrue(json.contains("\"id\":1"));
    }

    @Test
    void parseObjectToJavaBean() {
        String json = "{\"id\":2,\"user_name\":\"tom\"}";
        User user = JSON.parseObject(json, User.class);

        assertEquals(2L, user.getId());
        assertEquals("tom", user.getUserName());
    }

    @Test
    void typeReferenceForGenericList() {
        String json = "[{\"id\":1,\"user_name\":\"u1\"},{\"id\":2,\"user_name\":\"u2\"}]";
        List<User> users = JSON.parseObject(json, new TypeReference<List<User>>() {
        });

        assertEquals(2, users.size());
        assertEquals("u1", users.get(0).getUserName());
        assertEquals("u2", users.get(1).getUserName());
    }

    @Test
    void parseArrayToList() {
        String json = "[{\"id\":1,\"user_name\":\"u1\"}]";
        List<User> users = JSON.parseArray(json, User.class);

        assertEquals(1, users.size());
        assertEquals("u1", users.get(0).getUserName());
    }

    @Test
    void nestedJSONObject() {
        String json = "{\"teacher\":{\"name\":\"李老师\",\"age\":40},\"students\":[{\"name\":\"小明\",\"age\":10}]}";
        JSONObject root = JSONObject.parseObject(json);

        assertEquals("李老师", root.getJSONObject("teacher").getString("name"));
        assertEquals(10, root.getJSONArray("students").getJSONObject(0).getIntValue("age"));
    }

    @Test
    void fluentPutAndToJavaObject() {
        JSONObject obj = new JSONObject();
        obj.fluentPut("id", 1).fluentPut("user_name", "neo");

        User user = obj.toJavaObject(User.class);
        assertEquals(1L, user.getId());
        assertEquals("neo", user.getUserName());
    }

    @Test
    void toJavaList() {
        JSONArray array = new JSONArray();
        array.add(new JSONObject().fluentPut("id", 1).fluentPut("user_name", "a"));
        array.add(new JSONObject().fluentPut("id", 2).fluentPut("user_name", "b"));

        List<User> users = array.toJavaList(User.class);
        assertEquals(Arrays.asList("a", "b"), Arrays.asList(users.get(0).getUserName(), users.get(1).getUserName()));
    }

    @Test
    void writeMapNullValue() {
        JSONObject obj = new JSONObject();
        obj.put("name", null);

        String json = JSON.toJSONString(obj, SerializerFeature.WriteMapNullValue);
        assertTrue(json.contains("\"name\":null"));
    }

    @Test
    void isValidChecks() {
        assertTrue(JSON.isValid("{\"a\":1}"));
        assertTrue(JSON.isValidObject("{\"a\":1}"));
        assertTrue(JSON.isValidArray("[1,2]"));
        assertFalse(JSON.isValidObject("[1,2]"));
        assertFalse(JSON.isValid("{invalid}"));
    }

    @Test
    void toJSONConvertsMapAndCollection() {
        JSONObject obj = (JSONObject) JSON.toJSON(new java.util.HashMap<String, Object>() {{
            put("k", "v");
        }});
        assertEquals("v", obj.getString("k"));

        JSONArray array = (JSONArray) JSON.toJSON(Arrays.asList(1, 2));
        assertEquals(2, array.size());
    }

    public static class User {
        private Long id;

        @JSONField(name = "user_name")
        private String userName;

        private Date createTime;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public Date getCreateTime() {
            return createTime;
        }

        public void setCreateTime(Date createTime) {
            this.createTime = createTime;
        }
    }
}
