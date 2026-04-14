package lektion8;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class ValutaKurser {
    public static void main(String[] args) throws Exception {
        URL url = new URL("https://www.valutakurser.dk/");
        BufferedReader br = new BufferedReader(
                new InputStreamReader(url.openStream()));
        String line;
        while ((line = br.readLine())!=null) {
            //if (line.contains("USD") || line.contains("DKK")){
            //    System.out.println("Line: " + line);
            //}

            if (line.contains("   ") && line.contains("USD") || line.contains("DKK")){
                System.out.println(line);
            }
        }
    }
}
