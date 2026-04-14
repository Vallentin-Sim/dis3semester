package lektion8;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class URLTest {
    public static void main(String[] args) throws Exception {
        URL url = new URL("https://www.yr.no/nb");
        BufferedReader br = new BufferedReader(
                new InputStreamReader(url.openStream()));
        String line;
        while ((line = br.readLine())!=null) {
            System.out.println("Line: "+line);
        }
    }
}
