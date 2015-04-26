
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
	 * Returns the number of maneuver 
	 * @return the number of maneuver 
	 */
	int size(){
		return maneuverSet.length;
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
		n0 = Integer.parseInt(str[3])-1;
		n1 = Integer.parseInt(str[4])-1;
		na = Integer.parseInt(str[5])-1;
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
	/*
	public int getAlteredManeuver(int i, int alt_type) {
		switch (alt_type) {
		case Maneuvers.ALT_RADIO_OFF:
			//TODO get a maneuver from indices 
			
			return getRadioOff(); // TEMPORARY !!
			
		case Maneuvers.ALT_REVERSE_HEADING:
			//TODO get a maneuver from indices
			return 0;

		default:
			return i;
		}
	}
	*/
	
	public int maneuverFromTriplet(int d0, int d1, int alpha){		
		//int N = (n0+1)*(n1)*(na)+1;

		if(d1 == 0 || alpha == (na+1)/2) {
			return maneuverSet.length-1;			
		}else{
			int a = (alpha <= na/2) ? alpha : alpha - 1;
			d1--;
			return a + d1*na + d0*(na)*n1;
		}		
	}
	
	public int paramFromManeuver(int manIndex, int paramIndex){
		
		//int N = (n0+1)*(n1)*(na)+1;
		int A = (n1)*(na);
		
		int resultat[]= new int[3];
		
		if( manIndex == maneuverSet.length-1){
			resultat[0] = n0;
			resultat[1] = 0;
			resultat[2] = (na+1)/2;
		}else{
			resultat[0] = manIndex/A;
			resultat[1] = (manIndex-resultat[0]*A)/(na)+1;
			resultat[2] = (manIndex-resultat[0]*A-(resultat[1]-1)*na);
			
			if (resultat[2]>=na/2) resultat[2]++;
		}	
		return resultat[paramIndex];
	}
	
	
	/**
	 * Returns the maneuver for which the aircraft is not moved. It is the last index of the maneuver set.
	 * @return
	 */
	public int getRadioOff() {
		// TODO to be modified to cover other cases than the 150 maneuvers one
		return (maneuverSet.length - 1);
	}

	/**
	 * Returns the index of a maneuver corresponding to maneuver index i turned by an angular amount of a
	 * @param i
	 * @param a
	 * @return the turned maneuver index
	 */
	public int turn(int i, int a) {

		int newa = paramFromManeuver(i,2);
		
		newa += a;
		
		if (newa >= na) newa = na;
		if (newa <=  0) newa =  0;
		
		if (i == maneuverSet.length - 1) return maneuverFromTriplet(n0,1,newa);
		
		return maneuverFromTriplet(paramFromManeuver(i,0),paramFromManeuver(i,1),newa);
	}

	public void printMans() {
		System.out.println((n0+1)*(n1)*(na)+1);
		System.out.println(n0 + " "+n1 + " "+na);
		for (int i = 0; i < maneuverSet.length; i++) {
			System.out.print(i+"-"+paramFromManeuver(i,0)+"-"+paramFromManeuver(i,1)+"-"+paramFromManeuver(i,2)+"=>");
			System.out.println(maneuverFromTriplet(paramFromManeuver(i,0),paramFromManeuver(i,1),paramFromManeuver(i,2)));
		}
	}

}
