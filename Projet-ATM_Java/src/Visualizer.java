import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.util.ArrayList;
import javax.imageio.ImageIO;

import framework.launcher;
import framework.ScanClusters;
import framework.cluster;

public class Visualizer extends ScanClusters {


	
	public static void main(String[] args) throws IOException {
		
		ArrayList clusters = getClusters();
		//		Loop on clusters
		for (int i = 0; i < clusters.size(); i++) {
			
			cluster C = (cluster) clusters.get(i);
			
			//	Loop on aircraft (radioOff)
			for (int k = 0; k < C.nbAircraft; k++) {
				
				launcher Robustness = new launcher(C.nbAircraft, C.uncertainty, C.clusterId);
				Robustness.setSolution("cp");
				Robustness.setOutputs(true, false, false, true);
				Robustness.setSimulatedAnnealingAlgorithm(2000, 1, 0.001);
				Robustness.setRadioOff(k, true);
				if (!Robustness.isInConflict()) continue;
				Robustness.execSimulatedAnnealing();
				
				int[] conf1 = Robustness.defaultConfiguration.getAircraft();
				int[] conf2 = Robustness.testedConfiguration.getAircraft();
				int[] conf3 = Robustness.AlgorithmConfiguration.getAircraft();
					
				//	On trace coco !
				String chatter = Robustness.console();
				System.out.println(chatter);
				image Background = new image("data\\RAW\\man_"+C.nbAircraft+"ac_"+C.uncertainty+"err_"+C.clusterId);
				Background.tag(k);
				//Background.tryptique(conf1, conf2, conf3);
				Background.duo(conf1, conf2, conf3);
				Background.write("data\\results\\gif\\"+C.name+"_RdOff_"+k);
			}
			
		}
		
	}

	



}

class image {
	
	static private int largeur = 800;
	static private int hauteur = 800;
	
	private BufferedImage img;
	private Graphics2D g;
	private String format = "gif";
	private Double x_min = -100d;
	private Double y_min = -100d;
	private Double amplitude_x = 200d;
	private Double amplitude_y = 200d;
	
	
	private String ficManeuvers = "";
	
	private ArrayList tagged = new ArrayList();
	
	public image(String filename) {
		
		this.ficManeuvers = filename;
		
	}
	

	public void tag(int k) {
		// TODO Auto-generated method stub
		this.tagged.add(k);
	}


	public void tryptique(int [] conf1, int [] conf2, int [] conf3) {
		BufferedImage img1 = drawConfiguration(conf1,null);
		BufferedImage img2 = drawConfiguration(conf2,conf1);
		BufferedImage img3 = drawConfiguration(conf3,conf1);
		
		this.img = new BufferedImage(largeur*3, hauteur,    BufferedImage.TYPE_INT_RGB);
		Graphics2D g = this.img.createGraphics();
		g.drawImage(img1, 0, 0, null);
		g.drawImage(img2, largeur, 0, null);
		g.drawImage(img3, 2*largeur, 0, null);

	}
	
	public void duo(int [] conf1, int [] conf2, int [] conf3) {
		BufferedImage img1 = drawConfiguration(conf2,conf1);
		BufferedImage img2 = drawConfiguration(conf3,conf1);
		
		this.img = new BufferedImage(largeur*2, hauteur,    BufferedImage.TYPE_INT_RGB);
		Graphics2D g = this.img.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.drawImage(img1, 0, 0, null);
		g.drawImage(img2, largeur, 0, null);
	}
	
	public BufferedImage drawConfiguration(int[] conf,int[] def) {
		
		BufferedImage rtrnImg = new BufferedImage(largeur, hauteur,    BufferedImage.TYPE_INT_RGB);
		Graphics2D g = rtrnImg.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, largeur, hauteur);
		
		g.setColor(Color.DARK_GRAY);
		g.fillRect(2, 2, largeur-4, hauteur-4);
        		
		//ConvexHulls[] CvxHulls = new ConvexHulls[conf.length];
		//this.drawCvxHs(CvxHulls);
		
		for (int i = 0; i < conf.length; i++) {
			//CvxHulls[i] = new ConvexHulls(i, conf[i], this.ficManeuvers);
			ConvexHulls CH =  new ConvexHulls(i, conf[i], this.ficManeuvers);
			ConvexHulls CH_def;
			if (def == null) CH_def = (ConvexHulls) CH;
			else CH_def =  new ConvexHulls(i, def[i], this.ficManeuvers);
			
			for (int j = 0; j < CH.Hulls.size(); j++) {
				
				Hull h = (Hull) CH.Hulls.get(j);
				Hull hbis = (Hull) CH_def.Hulls.get(j);
				if (j==0) {
					g.setColor(Color.CYAN);
					g.drawString(String.valueOf(conf[i]), x2pixel(h.x0), y2pixel(h.y0));
					//g.fillOval( x2pixel(h.x0),y2pixel(h.y0), 5, 5);
					
				}
				
				g.setColor(Color.BLACK);
				
				g.drawPolyline(xtab2pixel(hbis.x_array), ytab2pixel(hbis.y_array), hbis.n_points);
				
				if ( this.tagged.contains(CH.n_aircraft) ) g.setColor(Color.ORANGE);
				else g.setColor(Color.WHITE);				
				
				g.setStroke( new BasicStroke( 2 ) );
				g.drawPolyline(xtab2pixel(h.x_array), ytab2pixel(h.y_array), h.n_points);
			}
			
		}
				
		return rtrnImg;
	}

	public void drawCvxHs(ConvexHulls[] cvxHulls) {

		g = this.img.createGraphics();
		
		for (int i = 0; i < cvxHulls.length; i++) {
			ConvexHulls CH = cvxHulls[i];
			
			for (int j = 0; j < CH.Hulls.size(); j++) {
								
				Hull h = (Hull) CH.Hulls.get(j);
				
				if (j==0) {
					g.setColor(Color.CYAN);
					//this.g.drawString(String.valueOf(i), x2pixel(h.x0), y2pixel(h.y0));
					g.fillOval( x2pixel(h.x0),y2pixel(h.y0), 5, 5);
					
				}
				
				g.setColor(Color.WHITE);
				g.drawPolyline(xtab2pixel(h.x_array), ytab2pixel(h.y_array), h.n_points);
				
			}
			
		}

		//this.img.
	}

	public void write(String filename) {
		try {
            // types possibles : jpeg, png, gif
            //ImageIO.write(this.img, this.format, new File(this.filename));
			ImageIO.write(this.img, this.format, new File(filename+"."+this.format));
        } catch(IOException e) {
            System.err.println("Erreur lors de l'écriture de l'image.");
        }
	}
	
	public int x2pixel(double coordinate) {
		return (int) Math.round((coordinate-this.x_min)/this.amplitude_x*this.largeur);
	}
	
	public int[] xtab2pixel(double[] coordinates) {
		int pixels[] = new int[coordinates.length];
		for (int i = 0; i < pixels.length; i++) {
			pixels[i] = x2pixel(coordinates[i]);
		}
		return pixels;
		
	}
	
	public int y2pixel(double coordinate) {
		return (int) Math.round((coordinate-this.y_min)/this.amplitude_y*this.hauteur);
	}
	
	public int[] ytab2pixel(double[] coordinates) {
		int pixels[] = new int[coordinates.length];
		for (int i = 0; i < pixels.length; i++) {
			pixels[i] = y2pixel(coordinates[i]);
		}
		return pixels;
		
	}
	
	
}

class ConvexHulls {
	
	int n_aircraft;
	private int maneuver;

	public ArrayList Hulls = new ArrayList();
	
	public ConvexHulls(int aircraft, int man, String filename) {
		this.n_aircraft = aircraft;
		this.maneuver = man;
		createHulls(filename);
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
					//System.out.println("Je bosse sur avion "+n_aircraft+" et man "+maneuver);
					this.Hulls.add(new Hull(line));	  
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
}

class Hull {
	
	public double [] x_array;
	public double [] y_array;
	public int time;
	public int n_points;
	
	public double x0;
	public double y0;
	
	public Hull(String line) {
		String[] str =line.split(" ");
		this.time = Integer.valueOf(str[2]);
		
		n_points = (str.length -3)/2;
		x_array = new double[n_points];
		y_array = new double[n_points];
		
		for (int i = 3; i < str.length; i++) {
			
			int reste = Integer.divideUnsigned(i, 2);
			int instance =0;
			if (i %2 == 0) {
				instance = (i-4)/2;
				y_array[instance] = Double.valueOf(str[i]);
				//System.out.println("J'ai remplis y="+i+"("+instance+")");
			}
			else {
				instance = (i-3)/2;
				x_array[instance] = Double.valueOf(str[i]);
				//System.out.println("J'ai remplis x="+i+"("+instance+")");
			}
		}
		x0 = x_array[0];
		y0 = y_array[0];
		//System.out.println("x:"+x0+" - y: "+y0);
	}
}