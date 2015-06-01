
import java.io.IOException;

public class Test {
	
	public static void main(String[] args) throws IOException {
		
		
		launcher Robustness = new launcher(15, 3, 0);

		Robustness.setOutputs(true, false, false,true);
		
		
		Robustness.setSimulatedAnnealingAlgorithm(50, 1, 0.0001);

		//Robustness.setRadioOff(4, true);
		Robustness.execSimulatedAnnealing();
		
		String chatter = Robustness.console();
		System.out.println(chatter);
		
		Robustness.setSolution("cp");
		
		System.out.println(Robustness.getDefaultConfigurationCost());
		System.out.println(Robustness.getTestedConfigurationCost());
		System.out.println(Robustness.getAlgorithmConfigurationCost());
		
	}
		
}
