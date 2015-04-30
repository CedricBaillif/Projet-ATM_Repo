

public class SimulatedAnnealing {
	
	//	Ingredients
	private Configuration InitialConfiguration;
	public AllowedManeuversMatrix amm; //A Matrix to store restrictions to the maneuvers each aircraft can use
		
	//	Cuisson
	final static double T_0 = 10;
	final static double decreaseRate = 0.001;
	final static double T_f = 0.1;
	
	//	Paramètres de sortie
	private Configuration bestConfiguration;
	private int bestConfigCost;
	private boolean remainingConflicts;
	
	public SimulatedAnnealing(Configuration InitConf)//, AllowedManeuversMatrix Mat)
	{
		this.InitialConfiguration = InitConf;
		//this.amm = Mat;
	}

	public void setConfiguration(Configuration Conf)
	{
		this.InitialConfiguration = Conf;
	}
	
	public void solve(boolean chatterBox)
	{
		double temperature = T_0;
		
		//	On initialise avec la configuration de départ
		Configuration currentConfig = InitialConfiguration.duplicate();
		int cost = currentConfig.getSyntheticCost();
		
		//	Boucle de recherche d'une solution
		while (temperature > T_f) {
			//	on genere une config aléatoire proche
			Configuration randomConfig = currentConfig.NewAllowedConfiguration();
			int randomConf_cost = randomConfig.getSyntheticCost();
			
			double random = Math.random();
			double proba = Math.exp(-Math.abs(randomConf_cost - cost) / temperature);
			
			//System.out.println("T \t| cost |  newcost | deltacost | random | proba ");
			/*
			System.out.print(Math.round(temperature*100)/100f + " | ");
			System.out.print(cost + " \t | ");
			System.out.print(randomConf_cost + " | ");
			System.out.print(cost - randomConf_cost  + " | ");
			System.out.print(Math.round(random*100)/100f + " | ");
			System.out.print(Math.round(proba*100)/100f + " | ");
			*/
			//	Soit on améliore soit on peut garder en fonction d'une proba en exp(delta_cost/tempe)
			if ((randomConf_cost < cost) || (random < proba)) {

				currentConfig = randomConfig.duplicate();
				cost = randomConf_cost;
			}
			
			temperature *= 1-decreaseRate;
			
		}
		
		this.bestConfiguration = currentConfig.duplicate();
		this.bestConfigCost = bestConfiguration.getSyntheticCost();
		
		//	Impression des résultats
		if (chatterBox) {
			System.out.println("C'est cuit ! ");
			this.bestConfiguration.printConfiguration();
			this.bestConfiguration.printManeuvers();
		}
	}
	
	public Configuration getNewConfiguration()
	{
		return bestConfiguration;
	}
	
	public double getNewCost()
	{
		return bestConfigCost;
	}

	/**
	 * SimulatedAnnealing method to accept or not a configuration according to its cost
	 * @param newCost
	 * Cost of the configuration we are testing 
	 * @param cost
	 * Cost of the reference configuration
	 * @param T
	 * @return 1 if newCost < cost, an int between ]0,1[ otherwise
	 *
	private boolean acceptanceProbability(int newCost,int cost, double T){
		boolean acceptance;
		
		//	Always accept a configuration that improves the cost function
		if (newCost < cost) {
			acceptance = true;
		} else {
			//	If a configuration doesn't improve the cost function,
				//acceptance depends on random AND exp(1/T) function 
			acceptance = (	Math.random() < Math.exp((cost - newCost) / T)	); 
		}
		return acceptance;
	}*/
	private static double acceptanceProbability(int newCost,int refCost, double T){
		if (newCost < refCost) {
			return 1.0;
		}
		return Math.exp(- Math.abs(newCost - refCost) / T);
	}
		

}

/**
// TODO Auto-generated method stub
double TEMP = 15000;
double RATE = 0.0001; 
int      it = 0;
int best_it = 0;

int cost = this.getSyntheticCost();
int best_cost = cost;
aircraft_best = Arrays.copyOf(aircraft, aircraft.length);

while (TEMP > 0.1) {
	
	aircraft_old = Arrays.copyOf(aircraft, aircraft.length);
	this.generateConfiguration();
	
	if (Math.random() < acceptanceProbability(cost, getSyntheticCost(), TEMP)) {
		aircraft_old = Arrays.copyOf(aircraft, aircraft.length);
		cost = this.getSyntheticCost();
		
		if (cost < best_cost) {
			aircraft_best = Arrays.copyOf(aircraft, aircraft.length);
			best_cost = cost;
			best_it   =   it;
		}
		
	} else {
		aircraft = Arrays.copyOf(aircraft_old, aircraft.length);
	}
	
	TEMP *= (1 - RATE);
	it++;
}
System.out.println(it);
System.out.println(best_it + " : A table !");
System.out.println("Best Cost : " + best_cost);
**/