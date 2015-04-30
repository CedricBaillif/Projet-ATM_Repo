import java.util.Arrays;


/**
 * Handles a matrix representing the allowed maneuvers for each aircraft
 * Methods are defined to disable maneuvers for specific aircrafts in a defined pattern (RADIO_OFF, FORBIDDEN_BLOCK, REVERSE_HEADING)
 * 
 * @author Romain
 *
 */


public class AllowedManeuversMatrix {
	
	private boolean[][] elements;
	
	AllowedManeuversMatrix(int ac, int mans) {
		elements = new boolean[ac][mans];
		reset();
	}
	
	/**
	 * Reset all elements to true (all maneuvers are possible for all aircraft
	 */
	public void reset() {
		for (int i = 0; i < elements.length; i++) {
			for (int j = 0; j < elements[0].length; j++) {
				set(i,j,true);
			}
		}
	}

	void set(int i, int j, boolean v) {
		elements[i][j] = v;
	}
	
	boolean get(int i, int j){
		return elements[i][j];
	}

	public void printLine(int i) {
		for (int j = 0; j < elements[0].length; j++) {
			System.out.print((elements[i][j]) ? "." : "X");
		}
		System.out.print("\r");
	}
	
	public void print() {
		for (int i = 0; i < elements.length; i++) {
			printLine(i);
		}
	}
	
	/**
	 * Sets m as the only possible maneuver for aircraft i
	 * @param i : aircraft index
	 * @param m : maneuver index
	 */
	public void setImposedManeuver(int i,int m) {
		for (int j = 0; j < elements[i].length; j++) {
			if (j != m) set(i,j,false);
		}
	}
	
	
	public AllowedManeuversMatrix duplicate()
	{
		AllowedManeuversMatrix cloneAMM = new AllowedManeuversMatrix(this.elements.length, this.elements[0].length);
		cloneAMM.elements = Arrays.copyOf(this.elements, this.elements.length);
		return cloneAMM;
	}
	
	
	
	
}
