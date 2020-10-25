public class HttpUtilsTest {

    public static void main(String[] args) {
        final String s = HttpUtils.get("http://localhost:8808/test", null, null);
        System.out.println(s);
    }
}
