
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
	
	void getd0(){
	//TODO 	
	}
	
	void getd1(){
	//TODO 	
	}
	
	void getalpha(){
	//TODO 	
	}
	
	public int getCost() {
		return c;
	}
	
	
	
}
