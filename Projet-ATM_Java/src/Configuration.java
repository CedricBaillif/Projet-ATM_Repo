import java.util.Arrays;


/**
 * Stores a set of aircraft performing a specific maneuver
 */
public class Configuration {
	
	public static final int CONFLICT_COST = 100;

	private int[] aircraft; //An aircraft is defined by its index and maneuver
	private int[] aircraft_new; //Array to store a new configuration (used by the simulated annealing method)
	private int[] aircraft_back; //The non-perturbated configuration
	AllowedManeuversMatrix amm; //A Matrix to store restrictions to the maneuvers each aircraft can use
	private NogoodMatrix ngm;
	private Maneuvers mans;
	
	private int acNumber;
	private int manNumber;
	
	Configuration(NogoodMatrix n, Maneuvers m, int[] ac){
		ngm = n;
		mans = m;
		aircraft = ac;
		aircraft_new = Arrays.copyOf(aircraft, aircraft.length);
		aircraft_back = Arrays.copyOf(aircraft, aircraft.length);
		
		acNumber = aircraft.length;
		manNumber = mans.size();
		amm = new AllowedManeuversMatrix(acNumber, manNumber);
	}
	
	
	
	/**
	 * Scans the aircraft pairwise to detect conflicts and returns the number of pair of aircraft in conflict
	 * @return
	 * The number of pairs of aircraft in conflict 
	 */
	int getConflictNumber(){
		int c = 0;
		for (int i = 0; i < aircraft.length; i++) {
			for (int j = i+1; j < aircraft.length; j++) {
				c = (ngm.get(i, j, aircraft[i], aircraft[j])) ? c + 1 : c;
			}
		}
		return c;
	}
	
	/**
	 * 
	 * @param i
	 * @return
	 */
	private boolean isInConflict(int i) {
		boolean c = false;
		for (int j = 0; j < aircraft.length; j++) {
				c = c || ngm.get(i, j, aircraft[i], aircraft[j]);
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
	 * Returns the total cost of this configuration, taking into account the aircraft maneuvers, and a penalty for each conflict
	 * @return
	 */
	int getSyntheticCost(){
		
		return (getCost() + getConflictNumber() * CONFLICT_COST);
	}
	
	/**
	 * Sets the configuration back to its original settings
	 * 
	 * @return true if the configuration has changed
	 */
	boolean reset(){
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
	 * Prints to the terminal : the list of maneuvers followed by the aircraft, the original maneuvers set at class instanciation, and the allowed maneuvers 
	 * matrix
	 */
	void printConfiguration() {
		
		System.out.println("Current configuration " + ((getConflictNumber() != 0) ? "is" : "is not") + " in conflict");
		System.out.println("Number of conflicts " + getConflictNumber());
		printConflicts();
		System.out.println("Configuration cost :" + getSyntheticCost());
		
		
		System.out.print("| CURR\t- ORIG\t|");
		for (int i = 0; i < 15; i++) {
			System.out.print("         V");
		}
		System.out.println();
		
		for (int i = 0; i < aircraft.length; i++) {
			System.out.print("| " + aircraft[i] + "\t| " + aircraft_back[i] + "\t|");
			amm.printLine(i);
			System.out.println("|");
		}
		System.out.println("-----------------");
	}
	
	void printConflicts(){
		for (int i = 0; i < aircraft.length; i++) {
			for (int j = i+1; j < aircraft.length; j++) {
				if (ngm.get(i, j, aircraft[i], aircraft[j])) {
					System.out.println(i + "-" + j);
				}
			}
		}
	}


	/**
	 * Returns true if the configuration is a 1-0 SuperSolution. 1-0 Super solutions are solutions that can always be made free of conflicts in the event of
	 * one aircraft not being able to perform it's original trajectory. This has to be achieved without modifying any other aircraft's trajectory.
	 * This is the same as checking that every aircraft has a possible backup trajectory that lefts the configuration non-conflictual.
	 * @return true if the current configuration is a 1-0 SuperSolution
	 */
	public boolean isSuperSolution() {
		
		int[] ss = new int[acNumber];
		boolean r = true;

		for (int i = 0; i < acNumber; i++) {
			int linecount = 0;
			for (int j = 0; j < manNumber; j++) {
				setManeuver(i, j);
				if (getConflictNumber() != 0) {
					linecount++;
				}
				reset();
			}
			ss[i] = linecount;
			r = r & (linecount >= 2);
		}
		return r;
	}



	public void setRadioOff(int i) {
		
		//TODO pour être plus réaliste, il faudrait figer les manoeuvres d'avions ayant déjà débuté leur manoeuvre au moment du "Radio off", et interdire les
		//maneuvres "anterieures" au radio off pour les avions n'ayant pas manoeuvré. 
		
		amm.setImposedManeuver(i,mans.getRadioOff());
		aircraft[i] = mans.getRadioOff();
		
	}



	public void simulatedAnnealingRepair() {
		// TODO Auto-generated method stub
		this.generateConfiguration();
		
	}
	
	private static double acceptanceProbability(int cost,int newCost, double T){
		if (newCost < cost) {
			return 1.0;
		}
		return Math.exp((cost - newCost) / T);
	}
	
	
	//TODO idée d'heuristique --> tourner les avions conflictuels dans le même sens, faire varier d0 et d1 en random
	private void generateConfiguration(){
		int turnBias = (Math.random() < 0.5) ? 1 : -1; //We choose a turning side which will be the same for all conflictual aircraft
		for (int i = 0; i < aircraft.length; i++) {
			if (isInConflict(i)) {
				
				int newMan = mans.turn(aircraft[i],turnBias);
				
				if (amm.get(i, newMan)) aircraft[i] = newMan ;
				
				
			}
		}
	}



	
}
