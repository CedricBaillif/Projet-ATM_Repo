package framework;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class ConvexHulls {
	
	int n_aircraft;
	public int maneuver;
	public double x0;
	public double xf;
	public double y0;
	public double yf;

	public ArrayList Hulls = new ArrayList();
	
	public ConvexHulls(int aircraft, int man, String filename) {
		this.n_aircraft = aircraft;
		this.maneuver = man;
		
		createHulls(System.getProperty("user.dir")+"\\data\\RAW\\"+filename);
		
		Hull H0 = (Hull) Hulls.get(0);
		Hull Hf = (Hull) Hulls.get(Hulls.size()-1);
		this.x0 = H0.xm;
		this.xf = Hf.xm;
		this.y0 = H0.ym;
		this.yf = Hf.ym;
	}
	
	private void createHulls(String filename) {

		InputStream ips;
		String resul = "";
		try {
			ips = new FileInputStream(filename);
			InputStreamReader ipsr= new InputStreamReader(ips);
			BufferedReader br = new BufferedReader(ipsr);
			String line;			
			try {
				while((line=br.readLine())!=null)
				{
					String[] str =line.split(" ");
					if (Integer.valueOf(str[0]) != n_aircraft || Integer.valueOf(str[1]) != maneuver) continue;
					this.Hulls.add(new Hull(line));	  
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
			System.out.println("Cluster file \""+ filename+"\" does not exist...");
			System.exit(0);
		}
		
	}
}
