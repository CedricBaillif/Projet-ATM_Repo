import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * >TEST
 * Interface class to evaluate robustness with TWO aircraft in radio-Off instead of one
 * @author Cedric
 */
public class Overview_RdOffBis extends ScanClusters {
	
	static int AlgorithmIterations = 5;
	static String dumpFilePath = RootPath + "\\data\\results\\overview\\clustersresults_RdOffBIS.csv";
	public static void main(String[] args) throws IOException {
		
		System.out.println("c'est parti....");
		ArrayList clusters = getClusters();
		FileWriter fw = new FileWriter (dumpFilePath);
		fw.write("cluster;nbAircraft;uncertainty;clusterId;iterations;rdOff1;rdOff2;InitialConfigurationCost;AlgorithmConfigurationCost;ConfigurationsDistance" + "\r");
		
		//	Loop on clusters
		for (int i = 0; i < clusters.size(); i++) {
			
			cluster C = (cluster) clusters.get(i);
						
			//	Loop on aircraft on radioOff
			for (int k = 0; k < C.nbAircraft; k++) {

				//	Iterations
				for (int j = 0; j < AlgorithmIterations; j++) {
					
					//	Second aircraft in radio off
					int second = k;
					while (second ==k) {
						second = (int) Math.floor( Math.random() * C.nbAircraft ); 
					}
					
					launcher Robustness = new launcher(C.nbAircraft, C.uncertainty, C.clusterId);
					Robustness.setSolution("cp");
					Robustness.setOutputs(true, false, false, true);
					Robustness.setSimulatedAnnealingAlgorithm(2000, 1, 0.001);
					Robustness.setRadioOff(k, true);
					Robustness.setRadioOff(second, false);
					
					if (!Robustness.isInConflict()) continue;
					Robustness.execSimulatedAnnealing();
					
					//	PRINT !!!
					fw.write
					(
						"cluster_"+C.nbAircraft+"ac_"+C.uncertainty+"err_"+C.clusterId+";"+
						C.nbAircraft +";"+ C.uncertainty +";"+ C.clusterId +";"+ j +";"+ k +";"+second+";"+ 
						Robustness.getTestedConfigurationCost() +";"+
						Robustness.getAlgorithmConfigurationCost() +";"+
						Math.round(Robustness.getConfigurationsDistance()) +";"
						+"\r"
					);
					
					String chatter = Robustness.console();
					System.out.println(chatter);
					
				}
			}
		}		
		fw.close();
	}

}
