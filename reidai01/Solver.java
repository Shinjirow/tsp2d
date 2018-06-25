import java.util.*;


public class Solver {
    private static int size = Map.p.length;
    private static int[] result = new int[size];
    private static int minDistance;

    private static long TLE = 30000;
    private static long MARGIN = 500;

    private static long start;
    private static long end;

    private static boolean DEBUG = true;

    public static void answer() {
        start = System.currentTimeMillis();

        //for (int i = 0; i < size; i++) result[i] = i;
        result = geneticAlgorithmAll();
        //twoOptimization();

        submission();
    }

    /**
     *
     */
    private static int[] geneticAlgorithmAll() {
        int numberOfKeepingParents = 10000;
        int numberOfCrossOver = 10000;
        List<Gene> parents = initGenes(numberOfKeepingParents);
        PriorityQueue<Gene> children = new PriorityQueue<>();

        Gene best = parents.get(0);

        int gen = 1;
        while (System.currentTimeMillis() - start < TLE - MARGIN) {
            //交叉する
            for (int i = 0; i < numberOfCrossOver; i++) {
                Gene aParent = decideParent(numberOfKeepingParents, parents);
                Gene anotherParent = decideParent(numberOfKeepingParents, parents);
                Gene[] child = aParent.crossOver(anotherParent);

                if (child == null) {
                    i--;
                    continue;
                }
                children.addAll(Arrays.asList(child));
            }

            //子を厳選する, 親に移す
            for (int i = 0; i < numberOfKeepingParents; i++) {
                parents.set(i, children.poll());
            }

            children.clear();

            if (best.compareTo(parents.get(0)) > 0) {
                best = parents.get(0);
            }
            if (DEBUG) System.out.println("gen " + gen + " , " + best);
            gen++;
        }

        return best.getRoute();
    }

    private static List<Gene> initGenes(int size) {
        List<Gene> parents = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            result = randomSelect();
            //twoOptimization();
            int sc = getCurrentDistance(result);
            Gene g = new Gene(result, sc);
            parents.add(g);
        }
        Collections.sort(parents);
        return parents;
    }

    private static Gene decideParent(int range, List<Gene> parents) {

        Random rand = new Random();
        /*
        int bound = (int) Math.sqrt(range);
        int value = rand.nextInt(bound) + 1;
        value *= (rand.nextInt(bound) + 1);
        value--;
        return value;
        */
        PriorityQueue<Gene> pq = new PriorityQueue<>();
        for (int i = 0; i < 4; i++) pq.add(parents.get(rand.nextInt(range)));

        return pq.peek();
    }

    /**
     * NN-algorithm + 2-Opt
     */
    private static void nearestNeighbourAll() {
        nearestNeighbour();

        twoOptimization();

        submission();
    }

    private static void nearestNeighbour() {
        boolean[] used = new boolean[size];
        for (int i = 0; i < size; i++) used[i] = false;
        used[0] = true;
        int from = 0;
        for (int i = 1; i < size; i++) {
            int dist = 1145141919;
            int distPos = 0;
            for (int to = 1; to < size; to++) {
                if (from == to) continue;
                if (used[to]) continue;

                if (dist > TSP2D.distance(from, to)) {
                    dist = TSP2D.distance(from, to);
                    distPos = to;
                }
            }

            result[i] = distPos;
            from = distPos;
            used[distPos] = true;
        }
    }

    private static int[] randomSelect() {
        Integer[] order = new Integer[size - 1];
        for (int i = 1; i < size; i++) {
            order[i - 1] = i;
        }
        List<Integer> list = Arrays.asList(order);
        Collections.shuffle(list);
        order = (Integer[]) list.toArray(new Integer[list.size()]);
        int[] res = new int[size];
        res[0] = 0;
        for (int i = 1; i < size; i++) {
            res[i] = order[i - 1];
        }
        return res;
    }

    private static void routeOptimization() {
        /*

        while(true){
            int s = Solver.minDistance;
            for (int i = 1; i < size; i++) {
                for (int j = i + 1; j < size; j++) {
                    int tmp = result[j];
                    for (int k = j - 1; k >= i; k--) result[k + 1] = result[k];
                    result[i] = tmp;

                    int cur = Solver.getCurrentDistance();
                    if (Solver.minDistance > cur) {
                        Solver.minDistance = cur;
                    } else {
                        tmp = result[i];
                        for (int k = i; k < j; k++) result[k] = result[k + 1];
                        result[j] = tmp;
                    }
                }
            }
            if(s == Solver.minDistance) break;
            end = System.currentTimeMillis();
            if (end - start > TLE - MARGIN) break;
        }*/

        /*

        Random rand = new Random();

        while (true) {
            int i = rand.nextInt(size - 2) + 1;
            int j = 0;
            while (j < i) j = rand.nextInt(size - 2) + 1;

            int tmp = result[j];
            for (int k = j - 1; k >= i; k--) result[k + 1] = result[k];
            result[i] = tmp;

            int cur = Solver.getCurrentDistance();
            if (Solver.minDistance > cur) {
                Solver.minDistance = cur;
            } else {
                tmp = result[i];
                for (int k = i; k < j; k++) result[k] = result[k + 1];
                result[j] = tmp;
            }
            end = System.currentTimeMillis();
            if (end - start > TLE - MARGIN) break;
        }*/
    }

    private static void twoOptimization() {
        Solver.minDistance = Solver.getCurrentDistance(result);

        for (int yaju = 810; yaju < 114514; yaju++) {
            int s = Solver.minDistance;

            for (int i = size - 1; i >= 1; i--) {
                for (int j = i + 1; j < size; j++) {
                    reverse(i, j);
                    int cur = Solver.getCurrentDistance(result);
                    if (Solver.minDistance > cur) {
                        Solver.minDistance = cur;
                    } else {
                        reverse(i, j);
                    }
                }
            }
            if (s == Solver.minDistance) break;
            if (DEBUG){
                System.err.println(yaju + "th try");
                print();
            }
        }
    }

    public static int getCurrentDistance(int[] route) {
        int currentCost = 0;
        for (int i = 1; i < size; i++) currentCost += TSP2D.distance(route[i - 1], route[i]);
        currentCost += TSP2D.distance(route[size - 1], route[0]);
        return currentCost;
    }

    private static void reverse(int i, int j) {
        assert (j > i);
        while (j - i > 0) {
            int tmp = result[i];
            result[i] = result[j];
            result[j] = tmp;
            j--;
            i++;
        }
    }

    private static void print() {
        for (int i = 0; i < size; i++) System.err.println(Map.p[result[i]][0] + "," + Map.p[result[i]][1]);
    }

    private static void submission() {
        if(DEBUG){
            System.err.println("final try");
            print();
        }

        TSP2D.submit(result);
    }
}


