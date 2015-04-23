
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
	private void reset() {
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
	}

	public void setRadioOff(int i) {
		for (int j = 0; j < elements[i].length; j++) {
			if (j != Maneuvers.getRadioOff()) set(i,j,false);
		}
		
	}
	
	
	
	
	
}
