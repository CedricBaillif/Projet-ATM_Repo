
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Robustesse {
	
	//	Directories and paths
	final static String RootPath = System.getProperty("user.dir");
	final static String ClusterPath = "\\data\\cluster";
	
	static NogoodMatrix mat;
	static Maneuvers    man;

	public static void main(String[] args) {
		mat = new NogoodMatrix();
		man = new Maneuvers();
		
		readCluster(RootPath + ClusterPath);

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
}
