//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import java.awt.Point;
import java.util.Arrays;

public class TSP2D {
    private static Point[] map;
    private static long start;
    private static long end;

    public TSP2D() {
    }

    public static void main(String[] var0) {
        makemap();
        start = System.currentTimeMillis();
        Solver.answer();
        System.out.println("*** No answer submitted. ***");
    }

    public static void makemap() {
        for(int var0 = 0; var0 < Map.p.length; ++var0) {
            map[var0] = new Point();
            map[var0].x = Map.p[var0][0];
            map[var0].y = Map.p[var0][1];
        }

    }

    public static void submit(int[] var0) {
        end = System.currentTimeMillis();
        System.out.print("Order, \t");

        for(int var1 = 0; var1 < var0.length; ++var1) {
            System.out.print(var0[var1] + ",");
        }

        System.out.println("");
        if (var0.length != map.length) {
            System.out.println("*** Invalid array length. ***");
            System.exit(0);
        }

        if (var0[0] != 0) {
            System.out.println("*** Invalid start position. ***");
            System.exit(0);
        }

        int[] var3 = new int[map.length];

        int var2;
        for(var2 = 0; var2 < map.length; ++var2) {
            var3[var2] = var0[var2];
        }

        Arrays.sort(var3);

        for(var2 = 0; var2 < map.length; ++var2) {
            if (var3[var2] != var2) {
                System.out.println("*** Invalid travel. ***");
                System.exit(0);
            }
        }

        System.out.println("Dist, \t" + evaluate(var0));
        System.out.println("Time, \t" + (end - start) + " msecs");
        System.exit(0);
    }

    private static int evaluate(int[] var0) {
        int var1 = 0;

        for(int var2 = 0; var2 < map.length - 1; ++var2) {
            var1 += distance(var0[var2], var0[var2 + 1]);
        }

        var1 += distance(var0[map.length - 1], 0);
        return var1;
    }

    private static void sort(int[] var0) {
        System.out.println("***************");

        for(int var1 = 0; var1 < var0.length; ++var1) {
            System.out.println(map[var0[var1]].x + "," + map[var0[var1]].y);
        }

        System.out.println(map[0].x + "," + map[0].y);
        System.out.println("***************");
    }

    public static int distance(int var0, int var1) {
        double var2 = map[var0].distance(map[var1]);
        int var4 = (int)Math.round(var2);
        return var4;
    }

    static {
        map = new Point[Map.p.length];
    }
}
