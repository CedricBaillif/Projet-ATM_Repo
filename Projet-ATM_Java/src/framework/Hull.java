package framework;

public class Hull {
	
	public double [] x_array;
	public double [] y_array;
	public int time;
	public int n_points;
	
	public double xm;
	public double ym;
	
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
			}
			else {
				instance = (i-3)/2;
				x_array[instance] = Double.valueOf(str[i]);
			}
		}
		
		for (int i = 0; i < x_array.length; i++) {
			xm+=x_array[i];
			ym+=y_array[i];
		}
		xm = xm/x_array.length;
		ym = ym/y_array.length;
		
	}
}
