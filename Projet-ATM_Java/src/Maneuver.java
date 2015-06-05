
/**
 * Lines of type “m k c k0 k1 ka” describe maneuver k,
 *  which has a cost c and correspond to indexes k0, k1 and ka 
 *  for parameters d_0, d_1 and alpha
 */
public class Maneuver {

	private int k, c, k0, k1, ka;
	
	Maneuver(String[] str){		
		k = Integer.parseInt(str[1]);
		c = Integer.parseInt(str[2]);
		k0 = Integer.parseInt(str[3]);
		k1 = Integer.parseInt(str[4]);
		ka = Integer.parseInt(str[5]);
	}
	
	public int getCost() {
		return c;
	}

	/**
	 * Calculate "distance" between tw0 maneuvers
	 * @param maneuver
	 * The maneuver to compare with
	 * @return distance between the two maneuvers
	 */
	public double getDistance(Maneuver maneuver) {
		
		double distance = 0;
		//	d0
		distance+=  Math.pow((this.k0 - maneuver.k0),2);
		//	d1
		distance+= Math.pow((this.k1 - maneuver.k1),2);
		//	alpha
		distance+= Math.pow((this.ka - maneuver.ka),2);
	
		return Math.sqrt(distance);
	}
	
	public void print()
	{
		System.out.println(k0 + " - "+ k1 +" - " + ka);
	}
	
}
