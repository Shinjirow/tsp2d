import java.util.List;
import java.util.Arrays;
import java.util.Collections;

public class Solver {

	static int num = Map.p.length;
	static int[] x = new int[num];
	static Integer[] order = new Integer[num-1];

	public static void answer() {
		for (int i=1; i<num; i++) {
			order[i-1] = i;
		}
        List<Integer> list = Arrays.asList(order);
        Collections.shuffle(list);
		order = (Integer[])list.toArray(new Integer[list.size()]);

		x[0] = 0;
		for (int i=1; i<num; i++) {
			x[i] = order[i-1];
		}

		yakusoku();
		TSP2D.submit(x);
	}

	private static void yakusoku() {
		int tmp;
		for(int i=0; i<Map.yakusoku.length; i++){
			if(x[Map.yakusoku[i][1]] != Map.yakusoku[i][0]){
				tmp = x[Map.yakusoku[i][1]];
				for(int j=0; j<num; j++){
					if(x[j]==Map.yakusoku[i][0]){
						x[j]=tmp;
					}
				}
				x[Map.yakusoku[i][1]]=Map.yakusoku[i][0];
			}
		}
	}

}


