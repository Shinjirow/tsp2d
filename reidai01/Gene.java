import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Random;
import java.util.Map;

/**
 * Gene
 * 遺伝的アルゴリズムを行う上での遺伝子情報のクラス
 */
public class Gene implements Comparable<Gene> {

    /**
     * route
     * 自身の経路情報
     */
    private int[] route;

    /**
     * score
     * 自身のスコア
     */
    private int score;

    /**
     * did
     * 自身とヤった相手
     */
    private Set<Gene> did;

    /**
     * コンストラクタ
     * 経路長は初期化用途に必要
     *
     * @param size 経路の長さ
     */
    public Gene(int size) {
        this.route = new int[size];
        did = new HashSet<>();
    }

    /**
     * コンストラクタ
     * 経路もあらかじめ決めてもらうやつ
     * 本当はスコアはこっちで決めるべきなんだけど、時間的な問題でちょっと
     *
     * @param route 経路
     * @param score その経路のスコア
     */
    public Gene(int[] route, int score) {
        this.route = route;
        this.score = score;
        did = new HashSet<>();
    }

    /**
     * crossOver
     * 交叉を行う
     * 自分自身や、すでに一度ヤった相手に対してはnullを返す
     * <p>
     * 二点交叉法で交叉するので、ここでその2点を定める
     *
     * @param pair お相手
     * @return 子のペア もしくはnull
     */
    public Gene[] crossOver(Gene pair) {

        if (did.contains(pair) || pair.equals(this)) return null;

        Random rand = new Random();
        int firstHalf = rand.nextInt(route.length - 2) + 2;
        int secondHalf = firstHalf;
        while (secondHalf == firstHalf) secondHalf = rand.nextInt(route.length - 2) + 2;

        if (secondHalf < firstHalf) {
            int tmp = firstHalf;
            firstHalf = secondHalf;
            secondHalf = tmp;
        }
        Gene[] children = new Gene[2];
        children[0] = this.crossOver(firstHalf, secondHalf, pair);
        children[1] = pair.crossOver(firstHalf, secondHalf, this);

        return children;
    }

    /**
     * PMXConverter
     * PMX法による交叉を実現するための変換テーブル
     */
    private class PMXConverter {
        /**
         * 入力に対応する出力を持つルータ
         * intからIntegerになるのでオブジェクト特有の事故が発生しそうでこわい(未検証)
         */
        private Map<Integer, Integer> router = new HashMap<>();

        void addRoute(int from, int to) {
            if (from == to) return;
            router.put(from, to);
        }

        int convert(int value) {
            while (true) {
                if (router.get(value) == null) {
                    return value;
                }
                value = router.get(value);
            }
        }
    }

    /**
     * crossOver
     * 内部用交叉メソッド
     * 前述の通り、二点交叉法のPMX法で子供を作る
     *
     * @param firstHalf  二点交叉の一点目
     * @param secondHalf 二点交叉の二点目
     * @param pair       お相手
     * @return 子供
     */
    private Gene crossOver(int firstHalf, int secondHalf, Gene pair) {
        PMXConverter converter = new PMXConverter();

        this.did.add(pair);
        Gene aChild = new Gene(route.length);
        for (int i = firstHalf; i < secondHalf; i++) {
            converter.addRoute(this.route[i], pair.route[i]);
            aChild.route[i] = this.route[i];
        }

        for (int i = 1; i < route.length; i++) {
            if (firstHalf <= i && i < secondHalf) continue;

            aChild.route[i] = converter.convert(pair.route[i]);
        }

        //突然変異
        Random rand = new Random();
        if (rand.nextInt(100) == 0) {
            int i = rand.nextInt(route.length - 1) + 1;
            int j = rand.nextInt(route.length - 1) + 1;
            int tmp = aChild.route[i];
            aChild.route[i] = aChild.route[j];
            aChild.route[j] = tmp;
        }
        if (rand.nextInt(50) == 0) {
            int i = rand.nextInt(route.length - 2) + 1;
            int j = 0;
            while (j < i) j = rand.nextInt(route.length - 2) + 1;

            int tmp = aChild.route[j];
            for (int k = j - 1; k >= i; k--) aChild.route[k + 1] = aChild.route[k];
            aChild.route[i] = tmp;
        }

        aChild.score = Solver.getCurrentDistance(aChild.route);

        return aChild;
    }

    /**
     * 自身の経路を返す
     *
     * @return 経路
     */
    public int[] getRoute() {
        return route;
    }

    /**
     * 自身と相手で比較するためのメソッド
     * コストで比較する
     *
     * @param o 相手
     * @return 自身のコストが大きければ1, 小さければ-1, 同じなら0
     */
    @Override
    public int compareTo(Gene o) {
        if (this.score == o.score) return 0;
        return this.score > o.score ? 1 : -1;
    }

    /**
     * 自身を文字列にして応答する
     *
     * @return 自身の文字列
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("route = ");
        for (int route : route) {
            sb.append(route);
            sb.append(", ");
        }
        sb.append("score = ");
        sb.append(score);
        return sb.toString();
    }
}
