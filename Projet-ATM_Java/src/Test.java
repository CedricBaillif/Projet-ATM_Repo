
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Test {
	
	//	Project directory
	final static String RootPath = System.getProperty("user.dir");
	
	static NogoodMatrix mat;
	static Maneuvers    man;

	public static void main(String[] args) {
		mat = new NogoodMatrix();
		man = new Maneuvers();
		
		String ClusterPath = RootPath + "\\data\\cluster";
        System.out.println("current dir = " + ClusterPath);

		readCluster(ClusterPath);
		
		System.out.println(mat.get(1, 0, 150, 59));
		System.out.println(mat.get(1, 0, 150, 60));

	}

	
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
					case "d":
						mat.readMeta(str);
						man.readMeta(str);
						break;
					case "c":
						mat.readNoGoods(str);
						break;
					case "m":
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
