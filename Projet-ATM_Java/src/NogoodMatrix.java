
/**
 * 4-D Conflict Matrix Object : 
 * 		For each pair of aircraft involved (i,j) and the concerns maneuvers (m_i,m_j)
 * 		Matrix[i][j][m_i][m_j] is true if situation is conflicting
 */

//TODO Renommer la classe en "ConflictMatrix" ?
public class NogoodMatrix {
	
	private boolean[][][][] nogoods;
	
	private int nAC, nMan; //Number of aircraft and maneuvers

	/**
	 * Getting a ConflictMatrix value
	 * @param i 	First aircraft index
	 * @param j		Second aircraft index
	 * @param mi	Aircraft i maneuver index
	 * @param mj	Aircraft j maneuver index
	 * @return		true if situation is conflicting
	 */
	boolean get(int i, int j, int mi, int mj) {
		return nogoods[i][j][mi][mj];
	}

	/**
	 * Initializing conflicting situations from a "cluster file"
	 * @param str
	 * String Array : c i j mi mj1 mj2 … mjk
	 */
	void readNoGoods(String[] str) {
		for (int i = 4; i < str.length; i++) {
			nogoods[Integer.parseInt(str[1])][Integer.parseInt(str[2])][Integer.parseInt(str[3])][Integer.parseInt(str[i])] = true;
		}
		
	}

	/**
	 * Initializing matrix from a "cluster file" header
	 * 
	 * @param str
	 * String Array : d n m n0 n1 na
	 * n is the number of aircraft, m the number of maneuvers allowed per aircraft 
	 * and n0 n1 na are the maximum indexes for parameters d_0, d_1 and alpha for the maneuvers
	 */
	void readMeta(String[] str) {
		nAC = Integer.parseInt(str[1]);
		nMan = Integer.parseInt(str[2]);
		nogoods = new boolean[nAC][nAC][nMan][nMan];
	}
	
	void print() {
		// TODO OUTPUT of nogood matrix in the cluster format
		
	}
	
	
	
}
