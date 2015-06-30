import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import framework.launcher;
import framework.ScanClusters;
import framework.cluster;

/**
 * A Interface class file to test our Simulated Annealing algorithm.
 * Compares Constraint Programming solutions with SA solutions for all clusters
 * @author Cedric
 *
 */
public class CompareAlgorithms extends ScanClusters {

	static int AlgorithmIterations = 5;
	static String dumpFilePath = RootPath + "\\data\\results\\overview\\compareAlgorithms.csv";

	public static void main(String[] args) throws IOException {


		System.out.println("c'est parti....");
		ArrayList clusters = getClusters();
		FileWriter fw = new FileWriter (dumpFilePath);
		fw.write("cluster;nbAircraft;uncertainty;clusterId;iterations;default;CP;SA" + "\r");
		
		//	Loop on clusters
		for (int i = 0; i < clusters.size(); i++) {
			
			cluster C = (cluster) clusters.get(i);
						
			//	Several iterations
			for (int j = 0; j < AlgorithmIterations; j++) {
				
				launcher Robustness = new launcher(C.nbAircraft, C.uncertainty, C.clusterId);
				//Robustness.setSolution("cp");
				Robustness.setOutputs(true, false, false, true);
				Robustness.setSimulatedAnnealingAlgorithm(2000, 1, 0.001);
				
				if (!Robustness.isInConflict()) continue;
				Robustness.execSimulatedAnnealing();
				
				double def = Robustness.getDefaultConfigurationCost();
				double SA = Robustness.getAlgorithmConfigurationCost();
				Robustness.setSolution("cp");
				double CP = Robustness.getTestedConfigurationCost();
				
				//	PRINT !!!
				fw.write
				(
					"cluster_"+C.nbAircraft+"ac_"+C.uncertainty+"err_"+C.clusterId+";"+
					C.nbAircraft +";"+ C.uncertainty +";"+ C.clusterId +";"+ j + ";" +
					def + ";" + CP + ";" + SA + ";"
					+"\r"
				);
				
				String chatter = Robustness.console();
				System.out.println(chatter);
				System.out.println("=====\r" + def + ";" + CP + ";" + SA + ";" + "\r=======");
			}
		
		}		
		fw.close();
	}

}
