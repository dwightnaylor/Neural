package helpers;

public class MathHelper {
	public static double sigmoid(double x) {
		return (1.0 / (1 + Math.pow(Math.E, (-1) * x)));
	}

	public static double round(double x, int decimalPoints) {
		double pow = Math.pow(10, decimalPoints);
		return (int) (pow * x) / pow;
	}
	
	public static void main(String[] args) {
		System.out.println(round(1.23456,4));
	}
}
