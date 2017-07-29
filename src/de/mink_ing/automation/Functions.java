package de.mink_ing.automation;


public class Functions {

	public static double gamma_correction(double in, double coeff){
		if(in >= 1.0) return 1.0;
		if(in <= 0.0) return 0.0;
		return(  Math.pow(in, coeff)  );
	}
	
	public class SensorCoeffNTC {
		private double Rref;
		double A1;
		double B1;
		double C1;
		double D1;
		
		public SensorCoeffNTC(double Rref, double A1, double B1, double C1,	double D1){
			this.Rref = Rref;
			this.A1 = A1;
			this.B1 = B1;
			this.C1 = C1;
			this.D1 = D1;
		}
		
	}

	
	public static double calcTempNTC(double R, SensorCoeffNTC coeff){
		//Temperature calculation
		double log_ntc = Math.log(R/coeff.Rref);
		double log_ntc2 = log_ntc*log_ntc;
		double log_ntc3 = log_ntc2*log_ntc;
		double tempDegC = -273.15 +  1.0/(coeff.A1 + coeff.B1*log_ntc + coeff.C1*log_ntc2 + coeff.D1*log_ntc3);
		
		return(tempDegC);
	}
}
