
/**
 * Object listing and initializing all the Maneuver(s) objects, 
 * conflicting or non-conflicting 
 */
public class Maneuvers {
	
	// Trajectory alteration types
	public static final int ALT_RADIO_OFF = 0;
	public static final int ALT_REVERSE_HEADING = 1;
	
	private int n0,n1,na;
	
	private Maneuver[] maneuverSet;
	
	/**
	 * TODO Constructeur vide, il faut le supprimer ou y mettre readMeta
	 */
	Maneuvers(){

	}
	
	/**
	 * Initializing maneuverSet Array from a "cluster file" header
	 * @param str
	 * String Array : d n m n0 n1 na
	 * n is the number of aircraft, m the number of maneuvers allowed per aircraft 
	 * and n0 n1 na are the maximum indexes for parameters d_0, d_1 and alpha for the maneuvers
	 */
	void readMeta(String[] str){
		maneuverSet = new Maneuver[Integer.parseInt(str[2])];
		n0 = Integer.parseInt(str[3]);
		n1 = Integer.parseInt(str[4]);
		na = Integer.parseInt(str[5]);
	}
	
	/**
	 * Adding a Maneuver Object to maneuverSet Array
	 * @param str
	 * String Array : m k c k0 k1 ka
	 */
	void readManeuver(String[] str) {
		Maneuver m = new Maneuver(str);
		maneuverSet[Integer.parseInt(str[1])] = m;
	}

	/**
	 * Get the cost of a specific maneuver
	 * @param i the index of the maneuver
	 * @return the cost of the maneuver
	 */
	public int getCost(int i) {
		return maneuverSet[i].getCost();
	}
	
	/**
	 * Returns the maneuver corresponding to the alteration of an other maneuver using a specific alteration method.
	 * @param i the original maneuver's index
	 * @param type the alteration type
	 * @return the altered maneuver's index
	 */
	public int getAlteredManeuver(int i, int alt_type) {
		switch (alt_type) {
		case Maneuvers.ALT_RADIO_OFF:
			//TODO get a maneuver from indices
			return 0;
			
		case Maneuvers.ALT_REVERSE_HEADING:
			//TODO get a maneuver from indices
			return 0;

		default:
			return i;
		}
	}

}
