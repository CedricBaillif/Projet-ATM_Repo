
import java.io.IOException;

public class Test {
	
	public static void main(String[] args) throws IOException {
		
		launcher Robustness = new launcher(10, 2, 9, "cp");
		Robustness.setOutputs(true, true, true,true);
		Robustness.printOutputs();
		
		Robustness.setSimulatedAnnealingAlgorithm(500, 10, 0.001);
		
		/*
		//	3 avions en radioOff
		Robustness.setRadioOff(1, false);
		Robustness.setRadioOff(3, false);
		Robustness.setRadioOff(2, false);
		Robustness.execSimulatedAnnealing();
		*/
		
		//	Boucle sur les radioOffs
		Robustness.radioOffPerturbations();

		
		
		String chatter = Robustness.console();
		System.out.println(chatter);
	}
		
}
