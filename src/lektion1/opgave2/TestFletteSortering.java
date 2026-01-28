package lektion1.opgave2;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TestFletteSortering {
    public static void main(String[] args) {
        List<Integer> list = new ArrayList<>();
        Random r = new Random();
        for (int i = 0; i < 1000000; i++) {
            list.add(Math.abs(r.nextInt() % 10000));
        }
        System.out.println(list);
        FletteSortering sort = new FletteSortering();
        long l1, l2;
        l1 = System.nanoTime();
        // sort.mergesort(list, 0, list.size() - 1); -- 1055 nanosekunder
        sort.mergesortTwoThreads(list, 0, list.size() -1); // 824, 847 og 741 nanosekunder
        l2 = System.nanoTime();
        System.out.println();
        System.out.println("Koeretiden var " + (l2 - l1) / 1000000);
        System.out.println();
        System.out.println(list);
    }
}
