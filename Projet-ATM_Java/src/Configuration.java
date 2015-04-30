
import java.util.Arrays;


/**
 * Stores a set of aircraft performing a specific maneuver
 */
public class Configuration {
	
	public static final int CONFLICT_COST = 2500;

	private int[] aircraft; //An aircraft is defined by its index and maneuver
	AllowedManeuversMatrix amm; //A Matrix to store restrictions to the maneuvers each aircraft can use
	private NogoodMatrix ngm;
	private Maneuvers mans;
	
	private int acNumber;
	private int manNumber;
	
	//	La touche du chef
	private int TURN_AMPLITUDE = 1;
	private int DELAY_AMPLITUDE = 2;
	private int EXTEND_AMPLITUDE = 2;
	
	
	Configuration(NogoodMatrix n, Maneuvers m, int[] ac){
		ngm = n;
		mans = m;
		aircraft = ac;
		
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
	boolean resetAllowedManeuvers(){
		this.amm.reset();
		return true;
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
		int NbConflicts = getConflictNumber();
		if (NbConflicts != 0) {
			System.out.println("Current configuration has" + NbConflicts + " conflicts remaining");
			printConflicts();
		} else {
			System.out.println("Current configuration is not in conflict");
		}

		System.out.println("Configuration cost :" + getSyntheticCost());
		System.out.println("--------  Allowed Maneuvers Matrix---------");
		this.amm.print();
		System.out.println("-------------------------------------------");
		
	}
	
	void printManeuvers() {
		for (int i = 0; i < aircraft.length; i++) {
			System.out.print("| " + aircraft[i] + "\t| ");
			amm.printLine(i);
		}
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
	 *
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
	}*/



	public void setRadioOff(int i, boolean reset) {
		
		//TODO pour être plus réaliste, il faudrait figer les manoeuvres d'avions ayant déjà débuté leur manoeuvre au moment du "Radio off", et interdire les
		//maneuvres "anterieures" au radio off pour les avions n'ayant pas manoeuvré. 
		
		if (reset) resetAllowedManeuvers();
		amm.setImposedManeuver(i,mans.getRadioOff());
		aircraft[i] = mans.getRadioOff();
		
	}

	/**
	 * Generate a new Configuration close to the initial one,
	 * according to the allowed maneuvers
	 * @return
	 * The new random Configuration object
	 */
	public Configuration NewAllowedConfiguration(){
		Configuration NewConf = this.duplicate();
	
		int turnBias = (Math.random() < 0.5) ? TURN_AMPLITUDE : -TURN_AMPLITUDE; //We choose a turning side which will be the same for all conflictual aircraft
		for (int i = 0; i < aircraft.length; i++) {
			if (isInConflict(i)) {
				
				int newMan = mans.turn(aircraft[i],turnBias);
				
				newMan = mans.delay(newMan, (int) Math.floor(Math.random()*(2*DELAY_AMPLITUDE+1)) - DELAY_AMPLITUDE);
				newMan = mans.extend(newMan, (int) Math.floor(Math.random()*(2*EXTEND_AMPLITUDE+1)) - EXTEND_AMPLITUDE);
				
				
				if (amm.get(i, newMan)) NewConf.aircraft[i] = newMan ;				
			}
		}
		return NewConf;
	}
	
	public double distance(Configuration conf2)
	{
		double distance = 0;
		for (int i = 0; i < this.aircraft.length; i++) {
			distance+= this.mans.getManeuver(this.aircraft[i]).getDistance(this.mans.getManeuver(conf2.aircraft[i]));
		}
		
		return distance;
	}
	
	/**
	 * Duplicate a configuration object
	 * @return
	 */
	public Configuration duplicate()
	{
		Configuration clone = new Configuration(ngm, mans, Arrays.copyOf(aircraft, aircraft.length));
		clone.amm = this.amm.duplicate();
		return clone;
	}

	public void compareManeuvers(Configuration Conf2) {
		for (int i = 0; i < aircraft.length; i++) {
			System.out.print("| " + this.aircraft[i] + "\t| "+ Conf2.aircraft[i] + "\t|");
			if (this.aircraft[i] != Conf2.aircraft[i]) System.out.print("   <");
			System.out.println("");
		}
		
	}



	
}
