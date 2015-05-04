
import java.io.IOException;

public class SimulatedAnnealingBenchmark extends Robustesse {
	
	private static String logFicPath = System.getProperty("user.dir") + "\\data\\log\\benchmark_" + filePattern + "_rdOff_";
	
	public static void main(String[] args) throws IOException {
		
		//		Initiation of mat and man with values from the cluster file
		mat = new NogoodMatrix();
		man = new Maneuvers();
		readCluster(clusterFile); 
			
		//	Initiation of a configuration with a pre-existing solution file
		Configuration testedConfiguration = new Configuration(mat, man, readSolution(solutionFile));
		
		//		Alterations loop of the solution clearances to test robustness
		printRunTime();
		System.out.println("\rStarting alterations loop ...");
		
		for (int i = 0; i < acNumber; i++) {

			//	Perturbation
			System.out.println("\r\t Radio failure aircraft "+i);
			Configuration PerturbatedConfig = testedConfiguration.duplicate();
			PerturbatedConfig.setRadioOff(i,true);
			if (PerturbatedConfig.getConflictNumber() == 0) {
				System.out.println("\r\t Non Conflicting configuration...");
				continue;
			}

			//	Recherche d'une nouvelle solution et dump dans un fichier de log
			SimulatedAnnealing RobustnessAlgorithm = new SimulatedAnnealing(PerturbatedConfig);
			RobustnessAlgorithm.solve(false);
			RobustnessAlgorithm.writeMetaData(logFicPath + i + ".csv");
		
			printRunTime();
		}

	}

}
