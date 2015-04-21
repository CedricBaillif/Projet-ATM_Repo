
public class IntMatrix {
	
	private int[][] elements;
	
	IntMatrix(int lines, int columns) {
		
		elements = new int[lines][columns];
		
	}
	
	void set(int i, int j, int v) {
		elements[i][j] = v;
	}
	
	int get(int i, int j){
		return elements[i][j];
	}

}
