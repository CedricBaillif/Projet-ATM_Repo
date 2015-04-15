
/**
 * Object listing and initializing all the Maneuver(s) objects, 
 * conflicting or non-conflicting 
 */
public class Maneuvers {
	
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

}
