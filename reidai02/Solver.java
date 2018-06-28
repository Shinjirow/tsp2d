import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;


/**
 * Solver
 * 課題を解くメインクラス
 */
public class Solver {
    /**
     * size
     * 与えられる問題の大きさ
     */
    private static int size = Map.p.length;

    /**
     * result
     * 提出する経路
     */
    private static int[] result = new int[size];

    /**
     * minDistance
     * 経路のコスト
     */
    private static int minDistance;

    /**
     * TLE
     * これ以上の時間動いちゃうとアになるやつ 単位はms
     */
    private static long TLE = 800000;

    /**
     * MARGIN
     * TLEに対する余裕を持ったやつ 単位はms
     */
    private static long MARGIN = 100;

    /**
     * start
     * スタートの時間を記録しておく
     */
    private static long start;

    /**
     * DEBUG
     * デバッグ用変数
     */
    private static boolean DEBUG = true;

    /**
     * 回答するメソッド
     */
    public static void answer() {
        start = System.currentTimeMillis();

        result = geneticAlgorithmAll();
        twoOptimization();

        if (DEBUG) {
            System.out.println(getDistance(0, result.length, result));
        }

        submission();
    }

    /**
     * geneticAlgorithmAll
     * 遺伝的アルゴリズムにて解を改善していく方法
     * 親の選別方法はトーナメント方式、交叉方法はPMX法(のつもり)
     * 現時点では収束時点で950 - 1100くらいの経路ができあがる
     * 30秒が結構激痛で、親を増やして解をよくしたいものの収束が追いつかなくなる
     */
    private static int[] geneticAlgorithmAll() {
        int numberOfKeepingParents = 100000;
        int numberOfCrossOver = 100000;
        List<Gene> parents = initGenes(numberOfKeepingParents);
        PriorityQueue<Gene> children = new PriorityQueue<>();

        Gene best = parents.get(0);

        int gen = 1;
        while (System.currentTimeMillis() - start < TLE - MARGIN) {
            /*
            データ取り用の分岐
            if (gen % 50 == 1) {
                result = best.getRoute();
                System.err.println("gen " + gen + ", cost " + getCurrentDistance(result));
                print();
            }
            */

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
            //parents.add(best);  エリート保存は早期収束の原因となったので削除
            for (int i = 0; i < numberOfKeepingParents; i++) {
                parents.set(i, children.poll());
            }

            children.clear();


            if (best.compareTo(parents.get(0)) > 0) {
                best = parents.get(0);
            }


            if (DEBUG) System.out.println("current best, gen[" + gen + "] = " + best);
            gen++;
        }

        return best.getRoute();
    }

    /**
     * initGenes
     * 遺伝的アルゴリズムで使用する、遺伝子を初期化(生成)する
     * 与えられたサイズの長さで遺伝子を乱択で作る
     *
     * @param size 親の数
     * @return 遺伝子が入っているリスト
     */
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

    /**
     * decideParent
     * 遺伝的アルゴリズムで使用する、親のペアを決める
     * 現在はトーナメント方式で選定する
     * トーナメントの大きさは5である(引数にした方がよかったのでは？)
     *
     * @param range   親の大きさ リストのサイズ取得すれば不要な引数やんけ
     * @param parents 親のリスト
     * @return 遺伝子
     */
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
        for (int i = 0; i < 5; i++) pq.add(parents.get(rand.nextInt(range)));

        return pq.peek();
    }

    /**
     * NN-algorithm + 2-Opt
     * 現在地からもっとも近い点に移動し続ける
     */
    private static void nearestNeighbourAll() {
        nearestNeighbour();

        twoOptimization();

        submission();
    }

    /**
     * nearestNeighbour
     * NN法の実装
     */
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

    /**
     * randomSelect
     * 乱択で経路を生やすメソッド
     *
     * @return 経路
     */
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

    /**
     * routeOptimization
     * 経路改善メソッド
     * 初期解の名残
     * 2-optの後にこれをぐるぐる回して経路をよくしていこうみたいな
     */
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

    /**
     * twoOptimization
     * 2-opt法を行うプログラム
     * 結び目を解くようなイメージ
     */
    private static void twoOptimization() {
        Solver.minDistance = Solver.getCurrentDistance(result);

        for (int yaju = 810; yaju < 114514; yaju++) {
            int s = Solver.minDistance;

            for (int i = 1; i < size; i++) {
                for (int j = size - 1; j >= i + 1; j--) {
                    int before = getDistance(i - 1, j + 1, result);
                    result = reverse(i, j, result);
                    int after = getDistance(i - 1, j + 1, result);

                    //効果なしなら戻す
                    if (before <= after) result = reverse(i, j, result);

                }
            }
            Solver.minDistance = Solver.getCurrentDistance(result);
            if (s == Solver.minDistance) break;
            if (DEBUG) {
                System.err.println(yaju + "th try");
                print();
            }
        }
    }

    /**
     * 与えられた経路における、iからj番までの部分的な経路コストを測定する
     * 経路の一部のみ変更するなら、全体の経路コストで測る必要がないので高速化の一環
     *
     * @param i     ここから
     * @param j     ここまで
     * @param route 現在の経路
     * @return コスト
     */
    public static int getDistance(int i, int j, int[] route) {
        assert (j <= route.length);
        int cost = 0;
        if (j == route.length) {
            cost += TSP2D.distance(route[j - 1], route[0]);
            j--;
        }
        for (; i < j; i++) cost += TSP2D.distance(route[i], route[i + 1]);

        return cost;
    }

    /**
     * 与えられた経路のコストを測定する
     *
     * @param route 経路
     * @return コスト
     */
    public static int getCurrentDistance(int[] route) {
        int currentCost = 0;
        for (int i = 1; i < size; i++) currentCost += TSP2D.distance(route[i - 1], route[i]);
        currentCost += TSP2D.distance(route[size - 1], route[0]);
        return currentCost;
    }

    /**
     * 与えられた経路の一部を反転させる
     *
     * @param i     ここから
     * @param j     ここまで
     * @param array 経路
     * @return 反転された経路
     */
    private static int[] reverse(int i, int j, int[] array) {
        assert (j > i);
        while (j - i > 0) {
            int tmp = array[i];
            array[i] = array[j];
            array[j] = tmp;
            j--;
            i++;
        }

        return array;
    }

    /**
     * 経路を出力する
     */
    private static void print() {
        for (int i = 0; i < size; i++) System.err.println(Map.p[result[i]][0] + "," + Map.p[result[i]][1]);
    }

    /**
     * 提出を行う
     * 経路出力するかをデバッグ変数によって分けたかったのでラップした
     */
    private static void submission() {
        if (DEBUG) {
            System.err.println("final try");
            print();
        }

        TSP2D.submit(result);
    }

    private int[] dist1245 = {0,83,123,71,74,6,160,16,96,117,118,184,89,62,143,27,25,55,78,141,149,43,11,180,5,22,76,162,144,84,67,148,174,75,126,64,36,100,188,178,65,177,163,116,172,15,66,14,194,58,114,13,173,101,99,176,168,79,166,51,136,154,1,159,103,137,69,147,9,189,86,21,161,18,152,128,17,12,56,197,186,158,171,3,35,10,111,196,190,91,175,140,113,97,115,169,32,47,72,37,2,8,70,59,23,20,52,33,112,199,19,7,150,24,146,167,90,30,138,182,93,142,183,125,29,157,45,187,119,110,28,38,102,42,34,53,193,85,170,151,61,92,181,106,109,108,134,155,195,49,135,131,124,120,145,127,80,104,48,40,81,87,179,191,44,121,82,88,107,164,165,31,130,98,132,39,192,73,63,26,129,4,156,105,185,50,41,46,68,153,122,133,60,54,94,77,198,95,139,57};
}


