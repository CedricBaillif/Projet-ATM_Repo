package visu;

import java.awt.*;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.event.*;

public class TheWindow extends JFrame
{
	private static String WindowName;
	
	private JSlider slider;
	
	private DrawPanel myPanel; 
	
	private String leftTitle;
	private String rightTitle;
	
	
	public TheWindow(int ac, int err, int id,int[] left, int[] right, ArrayList tag)
	{
		super("");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(1240,660);
		
		//setPanel(ac, err, id, left, right, tag);
	}

	public void setName(String name) {
		this.WindowName = name;
		this.setTitle(this.WindowName);
		//this.setLeftTitle();
	}
	
	public void setLeftTitle(String title) {
		this.leftTitle = title;
	}
	
	public void setRightTitle(String title) {
		this.rightTitle = title;
	}
	
	public void setPanel(int ac, int err, int id,int[] left, int[] right, ArrayList tag) {
		myPanel=new DrawPanel(ac, err, id, left, right,tag);
		myPanel.leftTitle = this.leftTitle;
		myPanel.rightTitle = this.rightTitle;
		myPanel.setBackground(Color.BLACK);
		
		
		slider=new JSlider(SwingConstants.HORIZONTAL,0,40,5);
		slider.setMajorTickSpacing(10);
		slider.setPaintTicks(true); 
   
        // ADD LISTERNER TO SLIDER
		slider.addChangeListener
		(
				new ChangeListener()
				{
				    public void stateChanged(ChangeEvent e)
				    {
				     myPanel.setD(slider.getValue()); //CALL SETTER METHOD
				    }
				}
		);
		add(slider,BorderLayout.SOUTH); // ADD SLIDER + LAYOUT
		add(myPanel,BorderLayout.CENTER); // ADD PANEL TO FRAME
		//this.setVisible(true);
	}
	
}
