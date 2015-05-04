
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;


public class Test {
	
	//	Project directory
	final static String RootPath = System.getProperty("user.dir");
	
	public static void main(String[] args) throws IOException {
		
		int nAircraft = 10;
		
		Serialized test = new Serialized();
		test.addParam("tempe", Math.random());
		test.addParam("NbConflicts", nAircraft);
		
		int[] tab = new int[nAircraft];
		
		for (int i = 0; i < nAircraft; i++) {
			
			double tempe = Math.random();
			
			tab[i] = (int) Math.round(i*Math.random()*15);
		//	test.newLine();
		}
		
		test.addArray("maneuvers", tab);
	
		test.print();
	}
		
}

class Serialized {
	public String ser;
	
	
	Serialized(){
		ser = "{";
	}
	public void addParam(String nomParam, Object value) {
		ser = ser + ""+ nomParam + ":"+String.valueOf(value) + ";";
	}
	
	public void addArray(String nomArray, int[] tab) {
		Serialized stringTab = new Serialized();
		for (int i = 0; i < tab.length; i++) {
			stringTab.addParam(""+i, tab[i]);
		}
		this.addParam(nomArray, stringTab.get());
	}
	public void newLine() {
		ser = ser +  "}\r{";
	}
	public void print() {
		System.out.println(get());
	}
	
	public String get() 
	{
		ser = ser + "}";
		return ser;
	}
}