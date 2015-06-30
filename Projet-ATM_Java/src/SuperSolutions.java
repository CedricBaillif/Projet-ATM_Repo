
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import framework.launcher;
import framework.ScanClusters;
import framework.cluster;

/**
 * Main interface class to test if the algorithm Solution is a 1,0 super solution
 * @author Cedric
 */
public class SuperSolutions extends ScanClusters {
	
	static int AlgorithmIterations = 5;
	static String dumpFilePath = RootPath + "\\data\\results\\overview\\1_0SuperSolutions.csv";
	
	public static void main(String[] args) throws IOException {
		
		ArrayList clusters = getClusters();
		FileWriter fw = new FileWriter (dumpFilePath);
		fw.write("cluster;nbAircraft;uncertainty;clusterId;isSuperSolution" + "\r");
		
		//	Loop on clusters
		for (int i = 0; i < clusters.size(); i++) {
			
			cluster C = (cluster) clusters.get(i);
						
			launcher Robustness = new launcher(C.nbAircraft, C.uncertainty, C.clusterId);
			Robustness.setSolution("cp");
			
			int bool = ( Robustness.is_1_0_SuperSolution() )? 1:0;
			
			fw.write( "cluster_"+C.nbAircraft+"ac_"+C.uncertainty+"err_"+C.clusterId+";"+
					C.nbAircraft +";"+ C.uncertainty +";"+ C.clusterId +";"+ bool+"\r");
		}
		fw.close();
	}

}
