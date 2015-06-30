package visu;

import framework.ConvexHulls;
import framework.Hull;

import javax.swing.*;

import java.awt.*;
import java.util.ArrayList;


public class DrawPanel extends JPanel
{
	private int aircraftNumber;
	private String ficManeuvers;
	public String leftTitle;
	public String rightTitle;
	
	static ArrayList tagged = new ArrayList();
	private ConvexHulls[] left_Ch; 
	private ConvexHulls[] right_Ch;
	private ConvexHulls[] def_Ch;
	private int[] leftConf;
	private int[] rightConf;
	private int[] defConf;
	
	private Double x_min = -100d;
	private Double y_min = -100d;
	private Double amplitude_x = 200d;
	private Double amplitude_y = 200d;
	
	private int largeur = 600;//400;
	private int hauteur = 600;//400;
	private int gap = 2; 
	

	
	
	public DrawPanel(int ac, int err, int id,int[] left, int[] right, ArrayList tag) {
		
		this.aircraftNumber = ac;
		this.ficManeuvers = "man_"+ac+"ac_"+err+"err_"+id;
		
		this.left_Ch = new ConvexHulls[aircraftNumber];
		this.right_Ch = new ConvexHulls[aircraftNumber];
		this.def_Ch = new ConvexHulls[aircraftNumber];

		this.leftConf = left;
		this.rightConf = right;
		this.defConf = new int[aircraftNumber];
		this.tagged = tag;
		
		//	Création des enveloppes convexes
		for (int i = 0; i < this.leftConf.length; i++) {
			this.defConf[i] = 150;
			this.left_Ch[i] = new ConvexHulls(i, this.leftConf[i], this.ficManeuvers);
			this.right_Ch[i] = new ConvexHulls(i, rightConf[i], this.ficManeuvers);
			this.def_Ch[i] = new ConvexHulls(i, defConf[i], this.ficManeuvers);
		}
		
		
		this.setSize(largeur*2,hauteur);
		
	}
 private int d=0; //SET START SIZE OF CIRCLE
 
 public void paintComponent(Graphics g)
 {
	 
	 int limit = d;
	 super.paintComponent(g); //CALL JPANEL PAINT.COMPONENT METHOD
	 Graphics2D g2 = (Graphics2D) g;
	 g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
	 g.setColor(Color.BLACK);
	 g.fillRect(0, 0, largeur, hauteur);
	 g.setColor(Color.DARK_GRAY);
	 g.fillRect(2, 2, largeur-4, hauteur-4);
	 g.fillRect(largeur + gap +2, 2, largeur-4, hauteur-4);
	 
	 //	10NM
	 g.setColor(Color.WHITE);
	 g.drawString("10NM", 10, hauteur-40);
	 g.drawLine(10, hauteur-30, x2pixel(x_min+10,0)+10, hauteur-30);
	 g.drawString("10NM", 10+largeur+gap, hauteur-40);
	 g.drawLine(10+largeur+gap, hauteur-30, x2pixel(x_min+10,0)+10+largeur+gap, hauteur-30);
	 
	 //	Titres
	 g.drawString(leftTitle, 5, 15);
	 g.drawString(rightTitle, 5+largeur+gap, 15);
	 
	//	Boucle sur les avions
	for (int i = 0; i < this.left_Ch.length; i++) {
		
		boolean alteredManeuver = (left_Ch[i].maneuver != right_Ch[i].maneuver || this.tagged.contains(i));
		
		//	Numéro de manoeuvre et segments de trajectoires par défaut
		int x0 = x2pixel(this.def_Ch[i].x0,0);
		int y0 = y2pixel(this.def_Ch[i].y0);
		int xf = x2pixel(this.def_Ch[i].xf,0);
		int yf = y2pixel(this.def_Ch[i].yf);
		
		Color col = this.tagged.contains(i)?/*alteredManeuver?*/ Color.ORANGE : Color.CYAN;
		g.setColor(col);
		g.drawString(String.valueOf(leftConf[i]), x0, y0);
		g.drawString(String.valueOf(rightConf[i]), x0+largeur+gap, y0);
		
		g.setColor(Color.GRAY);
		g.drawLine(x0, y0, xf, yf);
		g.drawLine(x0+largeur+gap, y0, xf+largeur+gap, yf);
		
		//	Boucle sur les Hulls
		for (int j = 0; j < this.left_Ch[i].Hulls.size(); j++) {
					
			Hull leftH = (Hull) this.left_Ch[i].Hulls.get(j);
			Hull defH  = (Hull) this.def_Ch[i].Hulls.get(j);
			Hull rightH = (Hull) this.right_Ch[i].Hulls.get(j);

			//	Comète
			if (j < d-5 || j>d) continue;
			
			col = alteredManeuver? Color.ORANGE : Color.LIGHT_GRAY;
			g.setColor(col);
			g.drawPolyline(xtab2pixel(leftH.x_array,0), ytab2pixel(leftH.y_array), leftH.n_points);
			g.drawPolyline(xtab2pixel(rightH.x_array,largeur+gap), ytab2pixel(rightH.y_array), rightH.n_points);
			
			if (j ==d) {

				col = alteredManeuver? Color.ORANGE : Color.WHITE;
				g.setColor(col);
				g.fillPolygon(xtab2pixel(leftH.x_array,0), ytab2pixel(leftH.y_array), leftH.n_points);
				g.fillPolygon(xtab2pixel(rightH.x_array,largeur+gap), ytab2pixel(rightH.y_array), rightH.n_points);
				g.drawOval(x2pixel(leftH.xm, 0), y2pixel(leftH.ym), 3, 3);
				g.drawOval(x2pixel(rightH.xm, largeur+gap), y2pixel(rightH.ym), 3, 3);
			}
		}
		
	}
  
				  
  		  
		  
 }
 
	public int x2pixel(double coordinate, int offset) {
		return (int) Math.round((coordinate-this.x_min)/this.amplitude_x*this.largeur) + offset;
	}
	
	public int[] xtab2pixel(double[] coordinates, int offset) {
		int pixels[] = new int[coordinates.length];
		for (int i = 0; i < pixels.length; i++) {
			pixels[i] = x2pixel(coordinates[i],offset);
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
	
	
    /**
     * CREATE SETTER METHOD TO CHANGE VARIABLE AND REPAINT
     * @param newD
     */
	public void setD(int newD)
	{
		d=(newD>=0 ? newD : 10);
		repaint();
	}
 
	public Dimension getPreferredSize()
	{
		return new Dimension(800,500);
	}
 
	public Dimension getMinimumSize()
	{
		return getPreferredSize();
	}
}