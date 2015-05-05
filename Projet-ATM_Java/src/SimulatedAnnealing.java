import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 *  Generic probabilistic metaheuristic for the global optimization problem 
 *  of locating a good approximation to the global optimum of a given function in a large search space
 *  (wikipedia)
 *
 */
public class SimulatedAnnealing {
	
	//	Input
	private Configuration InitialConfiguration;
	/*
	 * A Matrix to store restrictions to the maneuvers each aircraft can use
	 * public AllowedManeuversMatrix amm; 
	 */
		
	//	Algorithm parameters
	final static double T_0 = 50000;
	final static double decreaseRate = 0.001;
	final static double T_f = 0.1;
	
	//	Output
	private Configuration bestConfiguration;
	private int bestConfigCost;
	private ArrayList metaData;
	static public String metaDataParameters[] = {
		"T","rdmCost","rdmConflictsNb","accept","curCost","curConflictsNb","isBest"
	};
	
	public SimulatedAnnealing(Configuration InitConf)
	{
		this.InitialConfiguration = InitConf;
	}

	public void setConfiguration(Configuration Conf)
	{
		this.InitialConfiguration = Conf;
	}
	
	
	public void solve(boolean chatterBox) throws IOException
	{
		double temperature = T_0;
		
		//	On initialise avec la configuration de départ
		Configuration currentConfig = InitialConfiguration.duplicate();
		int cost = currentConfig.getSyntheticCost();
		
		//	"Mémoire" du recuit : On garde de coté la meilleure solution actuelle
		Configuration bestConfig = InitialConfiguration.duplicate();
		int bestCost = cost;
		
		// log
		ArrayList dumpList = new ArrayList();
		int nbIter = 0;
		int nbAccept = 0;
		int nbBests = 0;
		int BestIter = 0;
		double tempBest = temperature;
		
		//	Boucle de recherche d'une solution
		while (temperature > T_f) {
			ArrayList metas = new ArrayList();
			boolean isBest = false;
			metas.add(temperature);
			

			
			//	on genere une config aléatoire proche
			Configuration randomConfig = currentConfig.NewAllowedConfiguration();
			int randomConf_cost = randomConfig.getSyntheticCost();
			metas.add(randomConf_cost);
			metas.add(randomConfig.getConflictNumber());

			//	Soit on améliore soit on peut garder en fonction d'une proba en exp(delta_cost/tempe)
			double random = Math.random();
			double proba = Math.exp(-Math.abs(randomConf_cost - cost) / temperature);
			boolean accept = (randomConf_cost < cost) || (random < proba); 
			if (accept) {

				currentConfig = randomConfig.duplicate();
				cost = randomConf_cost;
				nbAccept++;
				
				//	A t'on amélioré la best config ?
				if ( randomConf_cost < bestCost) {
					bestCost = randomConf_cost;
					bestConfig = randomConfig.duplicate();
					isBest = true;
					nbBests++;
					BestIter = nbIter;
					tempBest = temperature;
				}
			}
			
			metas.add((accept) ? 1 : 0);
			metas.add(cost);
			metas.add(currentConfig.getConflictNumber());
			metas.add((isBest) ? 1 : 0);
			dumpList.add(metas);
			
			temperature *= 1-decreaseRate;
			
			nbIter++;
			
		}

		if (chatterBox) {
			System.out.println("nbIter: " + nbIter);
			nbIter = 0;
			System.out.println(" nbAccept: " + nbAccept);
			System.out.println(" nbBests: " + nbBests);
			System.out.println(" BestIter: " + BestIter);
			System.out.println(" tempBest: " + tempBest);
		}
		
		this.metaData = dumpList;
				
		this.bestConfiguration = bestConfig.duplicate();
		this.bestConfigCost = this.bestConfiguration.getSyntheticCost();
		
	}
	
	public void writeMetaData(String filename) throws IOException {

		FileWriter fw = new FileWriter (filename + T_0 + "Ti_"+ decreaseRate + "dR_"+ T_f + "Tf"+".csv");
		for (int h = 0; h < this.metaDataParameters.length; h++) {
			fw.write (String.valueOf (this.metaDataParameters[h])+",");
		}
		fw.write ("\r");
		
		for (int i = 0; i < this.metaData.size(); i++) {
			ArrayList ligne = (ArrayList) this.metaData.get(i);
			for (int j = 0; j < ligne.size(); j++) {
				fw.write (String.valueOf (ligne.get(j)));
		        fw.write (",");
			}
			fw.write ("\r");
		}
	
		fw.close();

	}
	
	public Configuration getNewConfiguration()
	{
		return bestConfiguration;
	}
	
	public double getNewCost()
	{
		return bestConfigCost;
	}
	
	public ArrayList getMetaData()
	{
		return this.metaData;
	}

	private static double acceptanceProbability(int newCost,int refCost, double T){
		if (newCost < refCost) {
			return 1.0;
		}
		return Math.exp(- Math.abs(newCost - refCost) / T);
	}
		

}

