
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Robustesse {
	
	/********************************************* 
	** 		SCRIPT CONFIGURATION 				**
	*********************************************/
	static int acNumber = 10;
	static int uncertaintyLevel = 3;
	static int idScenario = 8;
	static String AlgoType = "cp";
	/********************************************/
	
	//	Directories and file paths
	final static String filePattern = acNumber + "ac_" + uncertaintyLevel + "err_" + idScenario;
	final static String RootPath = System.getProperty("user.dir");
	final static String dataPath = RootPath + "\\data\\RAW\\";
	final static String clusterFile = dataPath + "cluster_" + filePattern;
	final static String maneuversFile = dataPath + "man_" + filePattern;
	final static String solutionFile = clusterFile + "." + AlgoType;
	
	static NogoodMatrix mat;
	static Maneuvers    man;
	
	static int[][] super_solutions;
	
	static long chrono = 0;

	public static void main(String[] args) {
		printRunTime();
		System.out.println("Loading files ...");
		
		//	Initiation of mat and man with values from the cluster file
		mat = new NogoodMatrix();
		man = new Maneuvers();
		readCluster(clusterFile); 
		
		//	Initiation of a configuration with a pre-existing solution file
		Configuration testedConfiguration = new Configuration(mat, man, readSolution(solutionFile));
		System.out.println("Initial solution configuration : ");
		testedConfiguration.printManeuvers();
		System.out.println("Initial solution cost : "+ testedConfiguration.getSyntheticCost());
		System.out.println("Conflicts : " + testedConfiguration.getConflictNumber());
		
		//	Alterations loop of the solution clearances to test robustness
		System.out.println("\rStarting alterations loop ...");
		//printRunTime();
		for (int i = 0; i < acNumber; i++) {
			/**
			 * La boucle est volontairement CASSEE sur CE cas que l'algo n'arrive même pas à résoudre... :/
			 */
			i=2;
			//	Perturbation
			System.out.println("\r\t Radio failure aircraft "+i);
			Configuration PerturbatedConfig = testedConfiguration.duplicate();
			PerturbatedConfig.setRadioOff(i,false);
			System.out.println("Perturbated configuration : ");
			PerturbatedConfig.printManeuvers();
			System.out.println("Conflicts : " + PerturbatedConfig.getConflictNumber());
			System.out.println("Perturbated configuration cost : "+ PerturbatedConfig.getSyntheticCost());
			
			//	Recherche d'une nouvelle solution
			SimulatedAnnealing RobustnessAlgorithm = new SimulatedAnnealing(PerturbatedConfig);
			RobustnessAlgorithm.solve(false);
			
			//	Affichage		
			System.out.println("\r---------  BILAN  ---------\r");
			
			System.out.println("testedSolution VS NewSolution :");
			testedConfiguration.compareManeuvers(RobustnessAlgorithm.getNewConfiguration());
			
			System.out.println("Algorithm solution cost : "+ RobustnessAlgorithm.getNewConfiguration().getSyntheticCost());
			System.out.println("Algorithm solution remaining Conflicts : " + RobustnessAlgorithm.getNewConfiguration().getConflictNumber());
			
			System.out.println("Distance between solutions : "+ testedConfiguration.distance(RobustnessAlgorithm.getNewConfiguration()));
			System.exit(0);
		}
		//printRunTime();
		
		
		/**
		chrono = System.currentTimeMillis();
		
		System.out.println("Loading files ...");
		
		//Initiation of mat and man with values from the cluster file
		mat = new NogoodMatrix();
		man = new Maneuvers();
		readCluster(clusterFile); 
		
		//The new configuration is loaded with a pre-existing solution
		Configuration conf = new Configuration(mat, man, readSolution(solutionFile));
		
		printRunTime();
		//		man.printMans();		System.exit(0);
		
		System.out.println("Original configuration");
		conf.printConfiguration();
		
		System.out.println("Loaded solution " + ((conf.isSuperSolution()) ? "is" : "is not") + " a 1-0 supersolution");

		//	Radio is Off...
		int radio_off = 0;
		
		conf.setRadioOff(radio_off);
		
		conf.printConfiguration();
		System.exit(0);
		conf.simulatedAnnealingRepair();

		conf.printConfiguration();
		
		
		printRunTime();
		**/
	}
	
	private static void printRunTime() {
		System.out.println("Time elapsed : " + (System.currentTimeMillis() - chrono) + " ms");
		
	}

	/**
	 * Reading an entire cluster file in order to set up
	 * the 4D Conflict Matrix and all the maneuvers
	 * 
	 * @param filename
	 * Absolute path of the cluster file
	 */
	static void readCluster(String filename) {
		
		InputStream ips;
		try {
			ips = new FileInputStream(filename);
			InputStreamReader ipsr= new InputStreamReader(ips);
			BufferedReader br = new BufferedReader(ipsr);
			String line;			
			try {
				while((line=br.readLine())!=null)
				{
					String[] str =line.split(" ");
					switch (str[0]) {
					case "d":	//	file header
						mat.readMeta(str);
						man.readMeta(str);
						break;
					case "c":	//	conflict description cluster line
						mat.readNoGoods(str);
						break;
					case "m":	//	maneuver description cluster line
						man.readManeuver(str);
						break;
					default:
						break;
					}				  
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
	}
	
	/**
	 * 
	 * Reads a solution file. Solutions are outputs of the mathematical methods we are going to evaluate in terms of robustness.
	 * 
	 * @param filename
	 * The file to extract the data from
	 * @return
	 * An integer table formatted such as ac[i] contains the maneuver index for aircraft i
	 */
	
	static int[] readSolution(String filename){
		InputStream ips;
		int[] ac = new int[acNumber];
		try {
			ips = new FileInputStream(filename);
			InputStreamReader ipsr= new InputStreamReader(ips);
			BufferedReader br = new BufferedReader(ipsr);
			String line;			
			try {
				while((line=br.readLine())!=null)
				{
					String[] str =line.split(" ");
					ac[Integer.parseInt(str[0])] = Integer.parseInt(str[1]);				  
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			ips.close();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return ac;
	}
}
