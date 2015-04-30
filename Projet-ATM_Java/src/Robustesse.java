
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
	static int acNumber = 20;
	static int uncertaintyLevel = 1;
	static int idScenario = 4;
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
		//printRunTime();
		System.out.println("Loading files ...");
		
		//	Initiation of mat and man with values from the cluster file
		mat = new NogoodMatrix();
		man = new Maneuvers();
		readCluster(clusterFile); 
		
		//	Initiation of a configuration with a pre-existing solution file
		Configuration testedConfiguration = new Configuration(mat, man, readSolution(solutionFile));

		//	Statistics object...
		Statistics STAT = new Statistics();
		STAT.init_Data(testedConfiguration.getConflictNumber(), testedConfiguration.getSyntheticCost());
		
		//	Alterations loop of the solution clearances to test robustness
		System.out.println("\rStarting alterations loop ...");
		//printRunTime();
		for (int i = 0; i < acNumber; i++) {

			//	Perturbation
			System.out.println("\r\t Radio failure aircraft "+i);
			Configuration PerturbatedConfig = testedConfiguration.duplicate();
			PerturbatedConfig.setRadioOff(i,true);

			//	Recherche d'une nouvelle solution
			SimulatedAnnealing RobustnessAlgorithm = new SimulatedAnnealing(PerturbatedConfig);
			RobustnessAlgorithm.solve(false);
			
			//	Ajout aux statistiques
			STAT.aircraftOff_Data(
					i, 
					RobustnessAlgorithm.getNewConfiguration().getConflictNumber(), 
					RobustnessAlgorithm.getNewConfiguration().getSyntheticCost(), 
					testedConfiguration.distance(RobustnessAlgorithm.getNewConfiguration())
					);
			
			//	Affichage
			testedConfiguration.compareManeuvers(RobustnessAlgorithm.getNewConfiguration());
		}
		STAT.printData();
		//printRunTime();
		
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

class Statistics extends Robustesse
{
	private int initNbConflicts;
	private double initCost;
	
	private int[] finalNbConflicts = new int[acNumber];
	private double avg_finalNbConflicts;
	private double StdDvt_finalNbConflicts;
	
	private double[] finalCost = new double[acNumber];
	private double avg_finalCost;
	private double StdDvt_finalCost;
	
	private double[] distances = new double[acNumber];
	private double avg_distance;
	private double StdDvt_distance;
	
	public void init_Data(int nbConf, int Cost)
	{
		initNbConflicts = nbConf;
		initCost = Cost;
	}
	public void aircraftOff_Data(int aircraft_off, int NbConflicts, double Cost, double distance)
	{
		finalNbConflicts[aircraft_off] = NbConflicts;
		finalCost[aircraft_off] = Cost;
		distances[aircraft_off] = distance;
	}
	
	private void calculate()
	{
		//	Means
		for (int i = 0; i < acNumber; i++) {
			avg_finalNbConflicts+= finalNbConflicts[i];
			avg_finalCost += finalCost[i];
			avg_distance += distances[i];
		}
		avg_finalNbConflicts = avg_finalNbConflicts/acNumber;
		avg_finalCost = avg_finalCost/acNumber;
		avg_distance = avg_distance/acNumber;
		
		//	Standard deviations
		for (int i = 0; i < acNumber; i++) {
			StdDvt_finalNbConflicts+= Math.pow(finalNbConflicts[i] - avg_finalNbConflicts,2);
			StdDvt_finalCost += Math.pow(finalCost[i] - avg_finalCost,2);
			StdDvt_distance += Math.pow(distances[i] - avg_distance,2);
		}
		StdDvt_finalNbConflicts = Math.pow(StdDvt_finalNbConflicts,0.5);
		StdDvt_finalCost = Math.pow(StdDvt_finalCost,0.5);
		StdDvt_distance = Math.pow(StdDvt_distance,0.5);
	}
	
	public void printData()
	{
		this.calculate();
		
		System.out.println("\ri  initNbConflicts  finalNbConflicts initCost   finalCost  distances");
		for (int i = 0; i < acNumber; i++) {
			
			System.out.print(i);
			System.out.print(" \t ");
			System.out.print(initNbConflicts);
			System.out.print(" \t  \t");
			System.out.print(finalNbConflicts[i]);
			System.out.print(" \t  \t");
			System.out.print(initCost);
			System.out.print("  \t");
			System.out.print(finalCost[i]);
			System.out.print("\t \t");
			System.out.print(distances[i]);
			System.out.print(" \t \r");
			
		}
		System.out.println("Conflits:");
		System.out.println("\t Moyenne : "+ avg_finalNbConflicts);
		System.out.println("\t Ecart type : "+ StdDvt_finalNbConflicts);
		System.out.println("Couts:");
		System.out.println("\t Moyenne : "+ avg_finalCost);
		System.out.println("\t Ecart type : "+ StdDvt_finalCost);
		System.out.println("Distances:");
		System.out.println("\t Moyenne : "+ avg_distance);
		System.out.println("\t Ecart type : "+ StdDvt_distance);
	}
	
}