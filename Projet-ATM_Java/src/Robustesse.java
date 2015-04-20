
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;

public class Robustesse {
	
	//	Directories and paths
	final static String RootPath = System.getProperty("user.dir");
	final static String ClusterPath = "\\data\\cluster";
	final static String SolutionPath = "\\data\\solution.cp";
	
	static NogoodMatrix mat;
	static Maneuvers    man;
	
	static int acNumber = 5;
	
	static int[][] super_solutions;
	
	static long chrono = 0;

	public static void main(String[] args) {
		
		chrono = System.currentTimeMillis();
		
		System.out.println("Loading files ...");
		
		//Initiation of mat and man with values from the cluster file
		mat = new NogoodMatrix();
		man = new Maneuvers();
		readCluster(RootPath + ClusterPath); 
		
		//The new configuration is loaded with a pre-existing solution
		Configuration conf = new Configuration(mat, man, readSolution(RootPath + SolutionPath));
		
		printRunTime();
		
		System.out.println("Original configuration");
		conf.printConfiguration();
		

		
		String s = (conf.isSuperSolution()) ? "is" : "is not";
		
		System.out.println("Loaded solution " + s + " a 1-0 supersolution");
		
		printRunTime();
		
		
	}
	
	private static void printRunTime() {
		System.out.println("Time elapsed : " + (System.currentTimeMillis() - chrono) + " ms");
		
	}

	static int superSolution(Configuration c){
		int s = 0;

		for (int ac = 0; ac < acNumber; ac++) {
			c.perturbate();
			for (int i = 0; i < man.size(); i++) {
				c.setManeuver(ac, i);
				if (!c.isInConflict()) {
					s++;
					super_solutions[ac][i] = c.getCost();
					c.printConfiguration();
				}
			}
		}
		return s;
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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return ac;
	}
}
