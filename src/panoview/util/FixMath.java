package panoview.util;


public class FixMath {
	
	public static final int FIXED_BITS = 16;
	public static final int ONE = 1 << FIXED_BITS;
	public static final int PI = (int) (Math.PI * ONE);
	
	private static int[] atanTable = new int[ONE + 1];
	private static int[] sqrtTable = new int[ONE + 1];

	static {
		for (int i = 0; i < atanTable.length; i++) {
			atanTable[i] = toFixed(Math.atan((double) i / ONE) / Math.PI);
			sqrtTable[i] = toFixed(Math.sqrt((double) i / ONE));
		}
	}
	
	public static int toFixed(double d) {
		long x = (long) (d * 2 * ONE);
		return (int) ((x + 1 - (x >>> 63) >> 1));
	}
	
	public static int toFixed(int i) {
		return i << FIXED_BITS;
	}
	
	public static double toDouble(int f) {
		return (double) f / ONE;
	}
	
	public static int toInt(int f) {
		return f >> FIXED_BITS;
	}
	
	public static int mul(int f, int g) {
		return (int) (((long) f * g) >> FIXED_BITS);
	}

	public static int div(int f, int g) {
		return (int) (((long) f << FIXED_BITS) / g);
	}

	/**
	 * Calculate the polar angle from x/y coordinates.
	 * @param y the y coordinate in fixed point.
	 * @param x the x coordinate in fixed point.
	 * @return the fixed point angle in units of PI radians (-1.0 .. 1.0)
	 */
	public static int atan2(int y, int x) {
		if (y <= 0) {
			y = -y;
			if (x < 0) {
				x = -x;
				if (x < y) {
					int i = (int) ((long) ONE * x / y);
					return -ONE + ONE/2 - atanTable[i];
				} else {
					int i = (int) ((long) ONE  * y / x);
					return -ONE + atanTable[i];
				}
			} else {
				if (x < y) {
					int i = (int) ((long) ONE  * x / y);
					return -ONE/2 + atanTable[i];
				} else if (x != 0) {
					int i = (int) ((long) ONE  * y / x);
					return -atanTable[i];
				} else {	// x == 0 && y == 0
					return 0;
				}
			}
		} else {
			if (x < 0) {
				x = -x;
				if (x < y) {
					int i = (int) ((long) ONE  * x / y);
					return ONE - ONE/2 + atanTable[i];
				} else {
					int i = (int) ((long) ONE  * y / x);
					return ONE - atanTable[i];
				}
			} else {
				if (x < y) {
					int i = (int) ((long) ONE  * x / y);
					return ONE/2 - atanTable[i];
				} else {
					int i = (int) ((long) ONE  * y / x);
					return atanTable[i];
				}
			}
		}
	}
	
	public static int sqrt(int f) {
		if (f <= ONE) {
			return sqrtTable[f];
		}
		if (f <= ONE * 4) {
			return sqrtTable[f >> 2] << 1;
		}
		return sqrt(f >> 4) << 2;
	}
	
	public static void main(String[] args) {
		for (int i = -180; i < 180; i++) {
			double d = i * Math.PI / 180;
			double x = Math.cos(d);
			double y = Math.sin(d);
			double atan2 = Math.atan2(y, x);
			double err = atan2 - d;
			if (Math.abs(err) > 3E-16) {
				System.out.println("i: " + i + " d: " + d + " atan2:" + atan2 + " err:" + err);
			}
			int fx = toFixed(x);
			int fy = toFixed(y);
			double fatan2 = Math.PI * toDouble(atan2(fy, fx));
			err = fatan2 - d;
			if (Math.abs(err) > 6.5E-5) {
				System.out.println("i: " + i + " d: " + d + " fatan2:" + fatan2 + " err:" + err);
			}
		}
		
		int steps = 10000000;
		
		long time0 = System.nanoTime();
		double sum = 0;
		for (int i = -steps; i < steps; i++) {
			double d = i * Math.PI / steps;
			double x = Math.cos(d);
			double y = Math.sin(d);
			sum += y + x;	
		}
		time0 = System.nanoTime() - time0;
		System.out.println("sum: " + sum + " time: " + (time0 / 1000000000.0));

		long time = System.nanoTime();
		sum = 0;
		for (int i = -steps; i < steps; i++) {
			double d = i * Math.PI / steps;
			double x = Math.cos(d);
			double y = Math.sin(d);
			sum += Math.atan2(y, x);	
		}
		time = System.nanoTime() - time;
		System.out.println("sum: " + sum + " time: " + (time / 1000000000.0) + " adjusted: " + (time - time0) / 1000000000.0);

		time = System.nanoTime();
		sum = 0;
		for (int i = -steps; i < steps; i++) {
			double d = i * Math.PI / steps;
			double x = Math.cos(d);
			double y = Math.sin(d);
			int fx = toFixed(x);
			int fy = toFixed(y);
			sum += toDouble(atan2(fy, fx));
		}
		time = System.nanoTime() - time;
		System.out.println("sum: " + sum + " time: " + (time / 1000000000.0) + " adjusted: " + (time - time0) / 1000000000.0);
	}

}
