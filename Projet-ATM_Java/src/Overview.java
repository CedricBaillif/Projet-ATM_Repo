import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Main interface class to perturbate and repare an algorithm solution
 * @author Cedric
 */
public class Overview extends ScanClusters {
	
	static int AlgorithmIterations = 5;
	static String dumpFilePath = RootPath + "\\data\\results\\overview\\clustersresults.csv";
	public static void main(String[] args) throws IOException {
		
		ArrayList clusters = getClusters();
		FileWriter fw = new FileWriter (dumpFilePath);
		fw.write("cluster;nbAircraft;uncertainty;clusterId;iterations;rdOff;InitialConfigurationCost;AlgorithmConfigurationCost;ConfigurationsDistance" + "\r");
		
		//	Loop on clusters
		for (int i = 0; i < clusters.size(); i++) {
			
			cluster C = (cluster) clusters.get(i);
						
			//	Loop on aircraft (radioOff)
			for (int k = 0; k < C.nbAircraft; k++) {
				
				//	Iterations
				for (int j = 0; j < AlgorithmIterations; j++) {
					
					launcher Robustness = new launcher(C.nbAircraft, C.uncertainty, C.clusterId);
					Robustness.setSolution("cp");
					Robustness.setOutputs(false, false, false, false);
					Robustness.setSimulatedAnnealingAlgorithm(2000, 1, 0.001);
					Robustness.setRadioOff(k, true);
					if (!Robustness.isInConflict()) continue;
					Robustness.execSimulatedAnnealing();
					
					//	PRINT !!!
					fw.write
					(
						"cluster_"+C.nbAircraft+"ac_"+C.uncertainty+"err_"+C.clusterId+";"+
						C.nbAircraft +";"+ C.uncertainty +";"+ C.clusterId +";"+ j +";"+ k +";"+ 
						Robustness.getTestedConfigurationCost() +";"+
						Robustness.getAlgorithmConfigurationCost() +";"+
						Math.round(Robustness.getConfigurationsDistance()) +";"
						+"\r"
					);
					
				}
			}
		}		
		fw.close();
	}

}
