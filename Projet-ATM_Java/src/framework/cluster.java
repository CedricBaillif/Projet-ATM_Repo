package framework;

public class cluster {
	
	public int nbAircraft;
	public int uncertainty;
	public int clusterId;
	public String name = "";
	
	public cluster(int nb,int uncert,int id) {
		this.nbAircraft = nb;
		this.uncertainty = uncert;
		this.clusterId = id;
		this.name = "cluster_"+nb+"ac_"+uncert+"err_"+id;
	}
	
	public void print() {
		System.out.println("\rCluster:");
		System.out.println("nbAircraft:" + nbAircraft);
		System.out.println("uncertainty:" + uncertainty);
		System.out.println("clusterId:" + clusterId);
	}

}
