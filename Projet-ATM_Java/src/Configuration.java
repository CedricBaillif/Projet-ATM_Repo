
/**
 * Stores a set of aircraft performing a specific maneuver
 */
public class Configuration {

	int[] aircraft; //An aircraft is defined by its index and maneuver
	private NogoodMatrix ngm;
	private Maneuvers mans;
	
	
	Configuration(NogoodMatrix n, Maneuvers m, int[] ac){
		ngm = n;
		mans = m;
		aircraft = ac;
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
	 * Generates a new configuration in which one of the aircraft's maneuver is altered.
	 * 
	 * @param ac The aircraft whose maneuver will be changed
	 * @param type The type of maneuver alteration (see Maneuver class)
	 * @return true if the configuration has changed.
	 */
	boolean perturbate(int ac, int type){
		
		int newman = mans.getAlteredManeuver(aircraft[ac],type);
		
		return false;
	}
	
	
	
}
