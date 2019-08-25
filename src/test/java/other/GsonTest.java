package other;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import org.junit.Test;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class GsonTest {

    @Test
    public void listToJsonTest() {
        List<String> list = new ArrayList<>();
        list.add(".,mxdbn,n");
        list.add("-d-.,");
        list.add("`hjkl~");
        String multiValueHeaderJson = new Gson().toJson(list);
        System.out.println(multiValueHeaderJson);
    }

    @Test
    public void JsonToListTest() {
        String jsonString;
        //jsonString = "gdhgdhgdhgdh";
        jsonString = "[\"gdhgdhgdhgdh\",\"fghjkhghjkj\"]";

        List<String> list = new ArrayList<>();
        if (jsonString.startsWith("[") && jsonString.endsWith("]")) {
            try {
                Type typeOfList = new TypeToken<List<String>>() {
                }.getType();
                list = new Gson().fromJson(jsonString, typeOfList);
            } catch (JsonSyntaxException e) {
                list.add(jsonString);
            }
        } else {
            list.add(jsonString);
        }
        System.out.println(list);
    }
}
