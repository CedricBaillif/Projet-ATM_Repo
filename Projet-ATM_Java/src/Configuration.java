import java.util.Arrays;


/**
 * Stores a set of aircraft performing a specific maneuver
 */
public class Configuration {

	private int[] aircraft; //An aircraft is defined by its index and maneuver
	private int[] aircraft_pert; //The perturbated configuration
	private int[] aircraft_back; //The non-perturbated configuration
	private NogoodMatrix ngm;
	private Maneuvers mans;
	
	private int acNumber;
	private int manNumber;
	
	Configuration(NogoodMatrix n, Maneuvers m, int[] ac){
		ngm = n;
		mans = m;
		aircraft = ac;
		aircraft_pert = Arrays.copyOf(aircraft, aircraft.length);
		aircraft_back = Arrays.copyOf(aircraft, aircraft.length);
		
		acNumber = aircraft.length;
		manNumber = mans.size();
	}
	
	
	
	/**
	 * Scans the aircraft pairwise to detect conflicts and returns true if at least one pair of aircraft is in conflict
	 * @return
	 * True if at least one pair of aircraft is in conflict 
	 */
	boolean isInConflict(){
		boolean c = false;
		for (int i = 0; i < aircraft.length; i++) {
			for (int j = i+1; j < aircraft.length; j++) {
				c = c || ngm.get(i, j, aircraft[i], aircraft[j]);
			}
		}
		return c;
	}
	
	/**
	 * Returns the total cost of this configuration, as a sum of each individual aircraft's maneuver
	 * @return the total cost of this configuration
	 */
	int getCost(){
		int c = 0;
		for (int i = 0; i < aircraft.length; i++) {
			c += mans.getCost(aircraft[i]); 
		}
		return c;
	}
	

	/**
	 * Sets the perturbated setting. An aircraft's trajectory is modified in a predefined way as set by the type parameter
	 * @param ac The aircraft whose trajectory is modified
	 * @param type The type of modification to the trajectory (see Maneuvers class)
	 */
	void setPerturbation(int ac, int type){
		aircraft = Arrays.copyOf(aircraft_back, aircraft.length); //Reset the configuration to the backed up configuration
		int newman = mans.getAlteredManeuver(aircraft[ac],type);
		aircraft_pert[ac] = newman;
	}
	
	/**
	 * Sets the configuration into the perturbated setting
	 * @return true if the configuration has changed.
	 */
	boolean perturbate(){
		boolean r = !Arrays.equals(aircraft, aircraft_pert);
		aircraft = Arrays.copyOf(aircraft_pert, aircraft.length);
		return r;
	}
	
	/**
	 * Sets the configuration back to its original settings
	 * 
	 * @return true if the configuration has changed
	 */
	boolean resetConfig(){
		boolean r = !Arrays.equals(aircraft, aircraft_back);
		aircraft = Arrays.copyOf(aircraft_back, aircraft.length);
		return r;
	}
	
	/**
	 * Sets the maneuver for an aircraft
	 * @param ac the aircraft index
	 * @param m the maneuver index
	 * @return true if the configuration is changed
	 */
	boolean setManeuver(int ac, int m){
		boolean r = (aircraft[ac] != m);
		aircraft[ac] = m;
		return r;
	}
	
	/**
	 * Prints the maneuvers followed by the aircraft to the terminal
	 */
	void printConfiguration() {
		
		System.out.println("| CURR - PERT - ORIG|");
		
		for (int i = 0; i < aircraft.length; i++) {
			System.out.println("| " + aircraft[i] + "  | " + aircraft_pert[i] + "  | " + aircraft_back[i] + " |");
		}
		System.out.println("------------------");
	}


	/**
	 * Returns true if the configuration is a 1-0 SuperSolution. 1-0 Super solutions are solutions that can always be made free of conflicts in the event of
	 * one aircraft not being able to perform it's original trajectory. This has to be achieved without modifying any other aircraft's trajectory.
	 * This is the same as checking that every aircraft has a possible backup trajectory that lefts the configuration non-conflictual.
	 * @return true if the current configuration is a 1-0 SuperSolution
	 */
	public boolean isSuperSolution() {
		IntMatrix m = new IntMatrix(acNumber, manNumber);
		
		int[] ss = new int[acNumber];
		boolean r = true;

		for (int i = 0; i < acNumber; i++) {
			int linecount = 0;
			for (int j = 0; j < manNumber; j++) {
				setManeuver(i, j);
				if (!isInConflict()) {
					m.set(i, j, 1);
					linecount++;
					//this.printConfiguration();
				}
				resetConfig();
			}
			ss[i] = linecount;
			r = r & (linecount >= 2);
		}
		/*
		for (int i = 0; i < ss.length; i++) {
			System.out.print(ss[i] + "|");
		}
		System.out.println();*/
		return r;
	}
	
	
}
