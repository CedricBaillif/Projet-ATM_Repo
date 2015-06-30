import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JFrame;

import visu.TheWindow;
import framework.launcher;


public class clusterVisualizer extends JFrame{
	
	public static void main(String[] args) throws IOException {
		
		int acNumber = 20;
		int err = 2;
		int idCluster = 3;
		
		ArrayList rdOffs = new ArrayList();
		int rdOff = 4;
		rdOffs.add(rdOff);
		
		//*
		launcher Robustness = new launcher(acNumber, err, idCluster);

		Robustness.setOutputs(true, false, false,true);
		
		
		Robustness.setSimulatedAnnealingAlgorithm(50, 1, 0.0001);
		Robustness.setSolution("cp");
		Robustness.setRadioOff(rdOff, true);
		
		
		
		Robustness.execSimulatedAnnealing();
		
		String chatter = Robustness.console();
		System.out.println(chatter);
		//*/
		
		//	Visualizer
		String Name = "cluster_"+acNumber+"ac_"+err+"err_"+idCluster;
		System.out.println(" >> Création de la fenêtre de visualisation");
		int[] left = Robustness.testedConfiguration.getAircraft();//new int[]{150,150,150,150,132};
		int[] right = Robustness.AlgorithmConfiguration.getAircraft();//new int[]{29,150,150,150,150};
		//*
		TheWindow w = new TheWindow(acNumber, err, idCluster, left, right, rdOffs);
		w.setName(Name);
		w.setLeftTitle("Algorithme CP (Coût : "+Robustness.getTestedConfigurationCost()+")");
		w.setRightTitle("Réparation Radio Off N°"+rdOff+" par SA (Coût : "+Robustness.getAlgorithmConfigurationCost()+")");
		w.setPanel(acNumber, err, idCluster, left, right, rdOffs);
		System.out.println("Fenêtre \""+Name+"\" prête");
		w.setVisible(true);
		//*/
		
	}

}
