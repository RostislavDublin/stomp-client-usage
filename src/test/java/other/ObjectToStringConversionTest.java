package other;

import org.junit.Test;

import java.math.BigDecimal;

public class ObjectToStringConversionTest {
    @Test
    public void test(){
        byte o = new Byte("127");
        String str = String.valueOf(o);
        System.out.println(str);
    }
}
