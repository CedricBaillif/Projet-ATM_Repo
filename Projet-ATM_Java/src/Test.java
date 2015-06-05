
import java.io.IOException;

public class Test {
	
	public static void main(String[] args) throws IOException {
		
		
		launcher Robustness = new launcher(15, 3, 3);

		Robustness.setOutputs(true, false, false,true);
		
		
		Robustness.setSimulatedAnnealingAlgorithm(50, 1, 0.0001);
		Robustness.setSolution("cp");
		Robustness.setRadioOff(11, true);
		Robustness.execSimulatedAnnealing();
		
		//Robustness.exportSolution("data\tst.rs");
		String chatter = Robustness.console();
		System.out.println(chatter);
		
		
		
		System.out.println(Robustness.getDefaultConfigurationCost());
		System.out.println(Robustness.getTestedConfigurationCost());
		System.out.println(Robustness.getAlgorithmConfigurationCost());
		
	}
		
}
