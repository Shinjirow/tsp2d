import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Random;
import java.util.Map;

public class Gene implements Comparable<Gene> {
    private int[] route;
    private int score;

    private Set<Gene> did;

    public Gene(int size) {
        this.route = new int[size];
        did = new HashSet<>();
    }

    public Gene(int[] route, int score) {
        this.route = route;
        this.score = score;
        did = new HashSet<>();
    }

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

    private class PMXConverter {
        Map<Integer, Integer> router = new HashMap<>();

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

    public int[] getRoute() {
        return route;
    }

    @Override
    public int compareTo(Gene o) {
        if (this.score == o.score) return 0;
        return this.score > o.score ? 1 : -1;
    }

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
