import java.io.IOException;
import java.util.ArrayList;


import framework.launcher;
import framework.ScanClusters;
import framework.cluster;

/**
 * Testing our SA algorithm on default configurations to check if it is
 * able to find decent solutions.
 * @author Cedric
 *
 */
public class SA_Solutions extends ScanClusters {

	static String dumpFilePath = RootPath + "\\data\\RAW\\";
	
	public static void main(String[] args) throws IOException {
		
		ArrayList clusters = getClusters();
		//		Loop on clusters
		for (int i = 0; i < clusters.size(); i++) {
			
			cluster C = (cluster) clusters.get(i);
			String filename = dumpFilePath + C.name+".sa";
			launcher Robustness = new launcher(C.nbAircraft, C.uncertainty, C.clusterId);
			Robustness.setOutputs(true, false, false, true);
			Robustness.setSimulatedAnnealingAlgorithm(2000, 1, 0.001);
			if (!Robustness.isInConflict()) continue;
			Robustness.execSimulatedAnnealing();
			Robustness.exportSolution(filename);
			
			String chatter = Robustness.console();
			System.out.println(chatter);
		}

	}

}
