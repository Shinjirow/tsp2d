import java.util.Random;


public class Solver {
    private static int size = Map.p.length;
    private static int[] x = new int[size];
    private static int minDistance;

    private static long TLE = 30000;
    private static long MARGIN = 500;

    public static void answer() {

        long start = System.currentTimeMillis();
        long end;

        for (int i = 0; i < size; i++) x[i] = i;

        Solver.minDistance = Solver.getCurrentDistance();

        for (int yaju = 810; yaju < 114514; yaju++) {
            int s = Solver.minDistance;

            for (int i = size - 1; i >= 1; i--) {
                for (int j = i + 1; j < size; j++) {
                    reverse(i, j);
                    int cur = Solver.getCurrentDistance();
                    if (Solver.minDistance > cur) {
                        Solver.minDistance = cur;
                    } else {
                        reverse(i, j);
                    }
                }
            }
            if (s == Solver.minDistance) break;
            System.err.println(yaju + "th try");
            print();
        }
        Random rand = new Random();

        while (true) {
            int i = rand.nextInt(size - 2) + 1;
            int j = 0;
            while (j < i) j = rand.nextInt(size - 2) + 1;

            int tmp = x[j];
            for (int k = j - 1; k >= i; k--) x[k + 1] = x[k];
            x[i] = tmp;

            int cur = Solver.getCurrentDistance();
            if (Solver.minDistance > cur) {
                Solver.minDistance = cur;
            } else {
                tmp = x[i];
                for (int k = i; k < j; k++) x[k] = x[k + 1];
                x[j] = tmp;
            }
            end = System.currentTimeMillis();
            if (end - start > TLE - MARGIN) break;
        }

        System.err.println("final try");
        print();

        TSP2D.submit(x);
    }

    private static int getCurrentDistance() {
        int currentCost = 0;
        for (int i = 1; i < size; i++) currentCost += TSP2D.distance(x[i - 1], x[i]);
        currentCost += TSP2D.distance(x[size - 1], x[0]);
        return currentCost;
    }

    private static void reverse(int i, int j) {
        assert (j > i);
        while (j - i > 0) {
            int tmp = x[i];
            x[i] = x[j];
            x[j] = tmp;
            j--;
            i++;
        }
    }

    private static void print() {
        for (int i = 0; i < size; i++) System.err.println(Map.p[x[i]][0] + "," + Map.p[x[i]][1]);
    }
}


