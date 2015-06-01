import java.io.IOException;
import java.util.ArrayList;

/**
 *  Generic probabilistic metaheuristic for the global optimization problem 
 *  of locating a good approximation to the global optimum of a given function in a large search space
 *  (wikipedia)
 *
 */
class SimulatedAnnealing {
	
	//	Input
	Configuration InitialConfiguration;
	
	//	Algorithm parameters
	static double T_0 = 50000;
	static double decreaseRate = 0.001;
	static double T_f = 0.1;
	private boolean settings = false;
	
	//	Outputs
	private Configuration bestConfiguration;
	private int bestConfigCost;
	private ArrayList metaData  = new ArrayList();
	static private String metaDataParameters[] = {
		"T","rdmCost","rdmConflictsNb","accept","curCost","curConflictsNb","isBest","currentConf","randomConf"
	};
		
	public SimulatedAnnealing(Configuration InitConf)
	{
		this.setConfiguration(InitConf);
	}
	
	/**
	 * Compulsory before using the algorithm :
	 * sets "temperature - like" parameters
	 * @param T0
	 * Initial temperature
	 * @param Tf
	 * Final temperature
	 * @param dR
	 * decrease Rate : T(t) = (1- dR) * T(t-1) 
	 */
	public void setAlgorithmParameters(double T0, double Tf, double dR) {
		
		SimulatedAnnealing.T_0 = T0;
		SimulatedAnnealing.T_f = Tf;
		SimulatedAnnealing.decreaseRate = dR;
		
		this.settings = true;
	}

	/**
	 * Sets the Initial Configuration
	 * @param Conf
	 */
	public void setConfiguration(Configuration Conf)
	{
		this.InitialConfiguration = Conf;
	}
	
	/**
	 * Search for an optimized solution
	 * @throws IOException
	 */
	public String solve() throws IOException
	{
		if (!settings) {
			System.out.println("Use \"setAlgorithmParameters()\" before \"solve()\" method...");
			System.exit(0);
		}
		
		
		double temperature = T_0;
		
		//	Initialisation
		Configuration currentConfig = InitialConfiguration.duplicate();
		int cost = currentConfig.getSyntheticCost();
		
		//	Algorithm "Memory" : the best solution so far is kept here...
		Configuration bestConfig = InitialConfiguration.duplicate();
		int bestCost = cost;
		
		// log output
		String console = "";
		ArrayList dumpList = new ArrayList();
		int nbIter = 0;
		int nbAccept = 0;
		int nbBests = 0;
		int BestIter = 0;
		double tempBest = temperature;
		
		//	Loop according to the temperature decrease law
		while (temperature > T_f) {
			ArrayList metas = new ArrayList();
			boolean isBest = false;
			metas.add(temperature);
						
			//	Randomly generating a nearby configuration
			Configuration randomConfig = currentConfig.NewAllowedConfiguration();
			int randomConf_cost = randomConfig.getSyntheticCost();
			metas.add(randomConf_cost);
			metas.add(randomConfig.getConflictNumber());

			//	Whether or not to accept this solution
			double random = Math.random();
			double proba = Math.exp(-Math.abs(randomConf_cost - cost) / temperature);
			boolean accept = (randomConf_cost < cost) || (random < proba); 
			if (accept) {

				currentConfig = randomConfig.duplicate();
				cost = randomConf_cost;
				nbAccept++;
				
				//	Is this the best config so far ?
				if ( randomConf_cost < bestCost) {
					bestCost = randomConf_cost;
					bestConfig = randomConfig.duplicate();
					isBest = true;
					nbBests++;
					BestIter = nbIter;
					tempBest = temperature;
				}
			}
			
			//	log file...
			metas.add((accept) ? 1 : 0);
			metas.add(cost);
			metas.add(currentConfig.getConflictNumber());
			metas.add((isBest) ? 1 : 0);
			metas.add(currentConfig.aircraft2csv());
			metas.add(randomConfig.aircraft2csv());
			dumpList.add(metas);
			
			temperature *= 1-decreaseRate;
			
			nbIter++;	
		}

		this.metaData = dumpList;
		
		this.bestConfiguration = bestConfig.duplicate();
		this.bestConfigCost = this.bestConfiguration.getSyntheticCost();

		//	Metadata output...
		console = console + "\rNb iterations de l'algo : " + nbIter;
		//nbIter = 0;
		console = console + "\rNb de fois ou on a accepté une solution: " + nbAccept;
		console = console + "\rNb de fois ou on a amélioré la best solution: " + nbBests;
		console = console + "\rL'iteration pour laquelle on a la best sol: " + BestIter;
		console = console + "\rLa tempé pour laquelle on a la best sol: " + tempBest;
		console = console + "\rCout initial: " + this.InitialConfiguration.getSyntheticCost();
		console = console + "\rCout de la best sol: " + this.bestConfigCost;
		
		return console + "\r";	
	}
	
	/**
	 * @return
	 * The algorithm configuration solution
	 */
	public Configuration getNewConfiguration()
	{
		return bestConfiguration.duplicate();
	}
	
	public double getDistance2Initial(Configuration conf) {
		return this.InitialConfiguration.distance(conf);
	}
	
	/**
	 * @return
	 * The algorithm configuration solution synthetic cost
	 */
	public double getNewCost()
	{
		return bestConfigCost;
	}
	
	public ArrayList getLogArray()
	{
		return this.metaData;
	}
	
	public String[] getMetaLogArray()
	{
		return this.metaDataParameters;
	}
		
	@Deprecated
	private static double acceptanceProbability(int newCost,int refCost, double T){
		if (newCost < refCost) {
			return 1.0;
		}
		return Math.exp(- Math.abs(newCost - refCost) / T);
	}
		

}

