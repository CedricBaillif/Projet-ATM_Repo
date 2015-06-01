import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Overview_RdOffBis extends ScanClusters {
	
	static int AlgorithmIterations = 5;
	static String dumpFilePath = RootPath + "\\data\\results\\overview\\clustersresults_RdOffBIS.csv";
	public static void main(String[] args) throws IOException {
		
		System.out.println("c'est parti....");
		ArrayList clusters = getClusters();
		FileWriter fw = new FileWriter (dumpFilePath);
		fw.write("cluster;nbAircraft;uncertainty;clusterId;iterations;rdOff1;rdOff2;InitialConfigurationCost;AlgorithmConfigurationCost;ConfigurationsDistance" + "\r");
		
		//	Boucle sur les clusters
		for (int i = 0; i < clusters.size(); i++) {
			
			cluster C = (cluster) clusters.get(i);
						
			//	Boucle sur les radioOff
			for (int k = 0; k < C.nbAircraft; k++) {

				//	Boucle sur les iterations
				for (int j = 0; j < AlgorithmIterations; j++) {
					
//					Generation d'un deuxième RdOff
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
