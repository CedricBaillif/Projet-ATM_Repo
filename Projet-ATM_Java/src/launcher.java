
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * "Mother" class that should be instanced to create a fully customizable scenario
 * @author Cedric
 *
 */
public class launcher {
	
	//	Cluster & solution details
	static int acNumber;
	static int uncertaintyLevel;
	static int idScenario;
	static String AlgoType;
	
	//	Algorithm parameters
	static double T_0; 
	static double decreaseRate;
	static double T_f;
	
	//	Directories and file paths
	static String filePattern;
	static String RootPath;
	static String dataPath;
	static String clusterFile;
	static String maneuversFile;
	static String solutionFile;
	
	//outputs
	private boolean console_O = true;
	private String console = "";
	private boolean log_O = false;
	private static String logFicPath;
	private boolean resul_O = true;
	private boolean metadata_O = true;
	private static String resultsFicPath;
	public SimulatedAnnealing Algorithm;
	
	static NogoodMatrix mat;
	static Maneuvers    man;
	private Configuration defaultConfiguration;
	private Configuration testedConfiguration;
	private Configuration AlgorithmConfiguration;

	private ArrayList<Integer> rdoffs = new ArrayList<Integer>();
	
	public launcher(int ac, int err, int id) {
		
		acNumber = ac;
		uncertaintyLevel = err;
		idScenario = id;
		
		
		this.setFilenames();
		
		//	Initiation of mat and man with values from the cluster file
		mat = new NogoodMatrix();
		man = new Maneuvers();
		readCluster(clusterFile); 
		
		//	Initiation of a configuration with a pre-existing solution file
		int[] aircraft = new int[ac];
		for (int i = 0; i < aircraft.length; i++) aircraft[i] = man.size()-1;
		this.defaultConfiguration = new Configuration(mat, man, aircraft);
		//this.testedConfiguration = new Configuration(mat, man, readSolution(solutionFile));
		this.testedConfiguration = this.defaultConfiguration.duplicate();
		
	}
	
	public void setSolution(String extension) {
		AlgoType = extension;
		launcher.solutionFile = clusterFile + "." + AlgoType;
		this.testedConfiguration = new Configuration(mat, man, readSolution(solutionFile));
	}
	
	private void setFilenames() {
		
		String filePattern = acNumber + "ac_" + uncertaintyLevel + "err_" + idScenario;
		this.add2Print("\r Cluster \""+ filePattern + "\"\r");
		
		launcher.RootPath = System.getProperty("user.dir");
		launcher.dataPath = RootPath + "\\data\\RAW\\";
		
		launcher.clusterFile = dataPath + "cluster_" + filePattern;
		launcher.maneuversFile = dataPath + "man_" + filePattern;
		
		launcher.logFicPath 	= RootPath + "\\data\\log\\benchmark_" + filePattern;
		launcher.resultsFicPath = RootPath + "\\data\\results\\robustness_" + filePattern;
		
	}
	
	/**
	 * "Temperature-like" algorithm parameters
	 * @param T_0
	 * @param T_f
	 * @param decreaseRate
	 */
	public void setSimulatedAnnealingAlgorithm(double T_0,double T_f, double decreaseRate) {
		launcher.T_0 = T_0;
		launcher.T_f = T_f;
		launcher.decreaseRate = decreaseRate;
		
		String suffix = "id_"+ T_0 + "Ti_"+ decreaseRate + "dR_"+ T_f + "Tf";
		launcher.logFicPath = launcher.logFicPath + suffix;
		launcher.resultsFicPath = launcher.resultsFicPath + suffix;
	}
	
	/**
	 * Getting details of what is happening
	 * @param hasConsole = true
	 * Whether or not printing details in console...
	 * @param hasLog = false
	 * Whether or not writing algorithm iterations details in a log file...
	 * @param hasResul = true
	 * Whether or not writing algorithm results in a result file...
	 * @param hasMeta = true
	 * Whether or not printing condensed algorithm informations in console...
	 */
	public void setOutputs(boolean hasConsole, boolean hasLog, boolean hasResul,boolean hasMeta) {
		this.console_O = hasConsole;
		this.log_O = hasLog;
		this.resul_O = hasResul;
		this.metadata_O = hasMeta;
		//this.printOutputs();
	}
	
	/**
	 * Add an aircraft to the RadioOffs list
	 * @param i
	 * The aircraft to add
	 * @param reset
	 * Replacing or adding ?
	 */
	public void setRadioOff(int i, boolean reset) {
		if (reset) this.rdoffs.clear();
		this.rdoffs.add(i);
	}
	
	/**
	 * Determines if the tested Configuration, with its Radio Off settings, is conflicting
	 * @return
	 */
	public boolean isInConflict() {
		Configuration PerturbatedConfig = this.testedConfiguration.duplicate();
		for (int i = 0; i < this.rdoffs.size(); i++) {
			this.add2Print("\rRadio failure aircraft "+ this.rdoffs.get(i) + "\r");
			PerturbatedConfig.setRadioOff(this.rdoffs.get(i), false);
		}
		return (PerturbatedConfig.getConflictNumber() != 0);
	}
	
	/**
	 * Perturbates a solution and performs a Simulated Annealing algorithm
	 * IF THE CONFIGURATION IS CONFLICTING
	 * @throws IOException
	 */
	public void execSimulatedAnnealing() throws IOException {

		//	Radio Off
		Configuration PerturbatedConfig = this.testedConfiguration.duplicate();
		for (int i = 0; i < this.rdoffs.size(); i++) {
			this.add2Print("\rRadio failure aircraft "+ this.rdoffs.get(i) + "\r");
			PerturbatedConfig.setRadioOff(this.rdoffs.get(i), false);
		}

		if (PerturbatedConfig.getConflictNumber() == 0) {
			this.add2Print("\r\t ==> Non Conflicting configuration,\r No log/results files have been edited.\r\r");
			this.AlgorithmConfiguration = this.testedConfiguration;
			return;
		}
		
		//	Looking for a solution...
		SimulatedAnnealing RobustnessAlgorithm = new SimulatedAnnealing(PerturbatedConfig);
		RobustnessAlgorithm.setAlgorithmParameters(launcher.T_0, launcher.T_f, launcher.decreaseRate);
		String details;
		details = RobustnessAlgorithm.solve();
		this.add2Print(this.testedConfiguration.compareManeuvers(RobustnessAlgorithm.getNewConfiguration()));
		if (this.metadata_O) this.add2Print(details);
		
		this.AlgorithmConfiguration = RobustnessAlgorithm.getNewConfiguration();
		
		//	Dumping to files
		this.writeLogFile(RobustnessAlgorithm);
		this.writeResultFile(RobustnessAlgorithm);

	}

	/**
	 * Dumps algorithm results in a result file (resultsFicPath)
	 * @param robustnessAlgorithm
	 * @throws IOException
	 */
	private void writeResultFile(SimulatedAnnealing robustnessAlgorithm) throws IOException {
		
		if (!resul_O) return;
		
		String filename = launcher.resultsFicPath + "_"+ Math.round(Math.random()*10000000);
		this.add2Print("\rEcriture du fichier results " + filename + "\r");
		
		String m_line = "m," + acNumber + "," + uncertaintyLevel + "," + idScenario + "," ;
		m_line = m_line + this.rdoffs.toString().replace(",", ";");
		m_line = m_line.replaceAll("[\\[\\]\\s]", "");
		m_line = m_line + "," + T_0 + "," + T_f + "," + decreaseRate;
		
		String r_line = "r, " + testedConfiguration.getSyntheticCost() + "," + AlgorithmConfiguration.getSyntheticCost() + "," + this.testedConfiguration.distance(AlgorithmConfiguration);

		/**
		 * m,	nAircraft,	err, 	id, 	rdOff,	T0, 	Tf, 	dR
		 * i, 	mans
		 * p,	mans
		 * s,	mans
		 * r,	i_cost, 	s_cost, 	distance
		 * 
		*/
		FileWriter fw = new FileWriter (filename);
		fw.write(m_line + "\r");
		fw.write("i," + testedConfiguration.aircraft2csv()  + "\r" );
		fw.write("p," + robustnessAlgorithm.InitialConfiguration.aircraft2csv()  + "\r" );
		fw.write("s," + AlgorithmConfiguration.aircraft2csv()  + "\r" );
		fw.write(r_line  + "\r");
		fw.close();
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
				e.printStackTrace();
			}
		} catch (FileNotFoundException e1) {
			//e1.printStackTrace();
			System.out.println("Cluster file \""+ filename+"\" does not exist...");
			System.exit(0);
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
			System.out.println("Solution file \""+ filename+"\" does not exist...");
			System.exit(0);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return ac;
	}

	/**
	 * Loop on the aircraft list of the configuration and alternately
	 * sets a aircraft in radioOff
	 * @throws IOException
	 */
	public void radioOffPerturbations() throws IOException {
		
		// Loop on aircraft array
		for (int i = 0; i < acNumber; i++) {
			
			this.setRadioOff(i, true);
			this.execSimulatedAnnealing();
	
		}
	}
	
	/**
	 * Dumps algorithm iterations details in a log file (logFicPath)
	 * @param robustnessAlgorithm
	 * @throws IOException
	 */
	private void writeLogFile(SimulatedAnnealing robustnessAlgorithm) throws IOException {
		
		if (!log_O) return;

		String filename = logFicPath + "_"+ Math.round(Math.random()*10000000);
		this.add2Print("\rEcriture du fichier log " + filename);
		
		FileWriter fw = new FileWriter (filename);
		fw.write("//");
		String[] metaDataParameters = robustnessAlgorithm.getMetaLogArray();
		for (int h = 0; h < metaDataParameters.length; h++) {fw.write (String.valueOf (metaDataParameters[h])+",");	}
		fw.write ("\r//"+ acNumber + "\r");
		
		ArrayList metaData = robustnessAlgorithm.getLogArray();
		for (int i = 0; i < metaData.size(); i++) {
			String line = metaData.get(i).toString();
			line = line.replace(",,", ",");
			line = line.replaceAll("[\\[\\]\\s]", "");
			/*
			for (int j = 0; j < ligne.size(); j++) {
				fw.write (String.valueOf (ligne.get(j)));
		        fw.write (",");
			}
			*/
			fw.write( line	);
			fw.write ("\r");
		}
	
		fw.close();
	}
	
	private void add2Print(String str) {
		if (console_O) 	this.console = this.console.concat(str);
			
	}

	public void printOutputs() {
		this.add2Print("\rSortie console :" + console_O);
		this.add2Print("\rFichier(s) log :" + log_O);
		this.add2Print("\rFichier(s) results :" + resul_O + "\r");
	}
	
	/**
	 * Returns the whole chatter of the class in a string
	 * @return
	 */
	public String console() {
		return this.console;
	}
	
	public int getDefaultConfigurationCost() {
		return defaultConfiguration.getSyntheticCost();
	}
	
	public int getTestedConfigurationCost() {
		return testedConfiguration.getSyntheticCost();
	}
	
	public int getAlgorithmConfigurationCost() {
		return this.AlgorithmConfiguration.getSyntheticCost();
	}
	
	public double getConfigurationsDistance() {
		return this.testedConfiguration.distance(this.AlgorithmConfiguration);
	}
}

