import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class ScanClusters {

	//	Directories and file paths
	final static String RootPath = System.getProperty("user.dir");
	final static String dataPath = RootPath + "\\data\\RAW\\";

	public static ArrayList getClusters() throws IOException {
		ArrayList clusters = new ArrayList();
		
		String SolFilesDirectory = dataPath;
		String SolFilesPattern = ".cp";
		
		
		File f = new File (SolFilesDirectory);
		File[] filesList = f.listFiles();
		
		for (int i = 0; i < filesList.length; i++) {
			String filename = filesList[i].getName();
			
			//	On selectionne les fichiers solutions
			if (filename.endsWith(SolFilesPattern)) {
				
				String clusterName = filename.split(SolFilesPattern)[0];
				File f_cluster = new File (SolFilesDirectory + "\\" + clusterName);
				
				String maneuversName = clusterName.replace("cluster", "man");
				File f_man = new File (SolFilesDirectory + "\\" + maneuversName);
				
				if (!f_cluster.isFile()) {
					System.out.println(clusterName+" n'existe pas...");
					continue;					
				}
				/*
				if (!f_man.isFile()) {
					System.out.println(maneuversName+" n'existe pas...");
					continue;
				}
				*/

				clusterName = clusterName.replaceAll("[a-z].", "_");
				String[] tabnum = clusterName.split("_+");
				cluster c = new cluster(Integer.parseInt(tabnum[1]),Integer.parseInt(tabnum[2]),Integer.parseInt(tabnum[3]));
				clusters.add(c);
			}
			
			
		}
		return clusters;
	}

}

class cluster {
	public int nbAircraft;
	public int uncertainty;
	public int clusterId;
	
	public cluster(int nb,int uncert,int id) {
		this.nbAircraft = nb;
		this.uncertainty = uncert;
		this.clusterId = id;
	}
	
	public void print() {
		System.out.println("\rCluster:");
		System.out.println("nbAircraft:" + nbAircraft);
		System.out.println("uncertainty:" + uncertainty);
		System.out.println("clusterId:" + clusterId);
	}
}
