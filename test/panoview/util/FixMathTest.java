package panoview.util;

import junit.framework.TestCase;


public class FixMathTest extends TestCase {

	private static final double DELTA = 1.0 / (1 << FixMath.FIXED_BITS);

	public void testToFixedDouble() {
		assertEquals(0, FixMath.toFixed(0.0));
		assertEquals(FixMath.ONE, FixMath.toFixed(1.0));
		assertEquals(-FixMath.ONE, FixMath.toFixed(-1.0));
		assertEquals(1, FixMath.toFixed(DELTA));
		assertEquals(-1, FixMath.toFixed(-DELTA));
		assertEquals(FixMath.PI, FixMath.toFixed(Math.PI));
		// Rounding to nearest
		assertEquals(1, FixMath.toFixed(1.1 * DELTA));
		assertEquals(1, FixMath.toFixed(0.9 * DELTA));
		assertEquals(-1, FixMath.toFixed(-1.1 * DELTA));
		assertEquals(-1, FixMath.toFixed(-0.9 * DELTA));
		// Rounding 0.5 up
		assertEquals(2, FixMath.toFixed(1.5 * DELTA));
		assertEquals(1, FixMath.toFixed(0.5 * DELTA));
		assertEquals(-2, FixMath.toFixed(-1.5 * DELTA));
		assertEquals(-1, FixMath.toFixed(-0.5 * DELTA));
	}

	public void testToFixedInt() {
		assertEquals(0, FixMath.toFixed(0));
		assertEquals(FixMath.ONE, FixMath.toFixed(1));
		assertEquals(-FixMath.ONE, FixMath.toFixed(-1));
	}

	public void testToDouble() {
		assertEquals(0.0, FixMath.toDouble(0), 0.0);
		assertEquals(1.0, FixMath.toDouble(FixMath.ONE), 0.0);
		assertEquals(-1.0, FixMath.toDouble(-FixMath.ONE), 0.0);
		assertEquals(DELTA, FixMath.toDouble(1), 0);
		assertEquals(-DELTA, FixMath.toDouble(-1), 0);
		assertEquals(Math.PI, FixMath.toDouble(FixMath.PI), 0.5 * DELTA);
	}

	public void testToInt() {
		assertEquals(0, FixMath.toInt(0));
		assertEquals(1, FixMath.toInt(FixMath.ONE));
		assertEquals(-1, FixMath.toInt(-FixMath.ONE));
		// Floor
		assertEquals(1, FixMath.toInt(FixMath.ONE+1));
		assertEquals(0, FixMath.toInt(FixMath.ONE-1));
		assertEquals(0, FixMath.toInt(1));
		assertEquals(-1, FixMath.toInt(-1));
		assertEquals(-1, FixMath.toInt(-FixMath.ONE+1));
		assertEquals(-2, FixMath.toInt(-FixMath.ONE-1));
	}

	public void testMul() {
		assertEquals(0, FixMath.mul(0, 0));
		assertEquals(FixMath.ONE, FixMath.mul(FixMath.ONE, FixMath.ONE));
		assertEquals(FixMath.ONE, FixMath.mul(-FixMath.ONE, -FixMath.ONE));
		assertEquals(-FixMath.ONE, FixMath.mul(FixMath.ONE, -FixMath.ONE));
		assertEquals(-FixMath.ONE, FixMath.mul(-FixMath.ONE, FixMath.ONE));
		assertEquals(1, FixMath.mul(FixMath.ONE, 1));
		assertEquals(-1, FixMath.mul(-FixMath.ONE, 1));
		assertEquals(-1, FixMath.mul(FixMath.ONE, -1));
		assertEquals(FixMath.ONE * 32000, FixMath.mul(FixMath.ONE * 100, FixMath.ONE * 320));
	}

	public void testDiv() {
		assertEquals(0, FixMath.div(0, FixMath.ONE));
		assertEquals(FixMath.ONE, FixMath.div(FixMath.ONE, FixMath.ONE));
		assertEquals(FixMath.ONE, FixMath.div(-FixMath.ONE, -FixMath.ONE));
		assertEquals(-FixMath.ONE, FixMath.div(FixMath.ONE, -FixMath.ONE));
		assertEquals(-FixMath.ONE, FixMath.div(-FixMath.ONE, FixMath.ONE));
		assertEquals(1, FixMath.div(1, FixMath.ONE));
		assertEquals(-1, FixMath.div(1, -FixMath.ONE));
		assertEquals(-1, FixMath.div(-1, FixMath.ONE));
		assertEquals((FixMath.ONE / 4) << FixMath.FIXED_BITS, FixMath.div(FixMath.ONE / 4, 1));
		assertEquals(FixMath.ONE, FixMath.div(FixMath.ONE * 32767, FixMath.ONE * 32767));
		assertEquals(FixMath.ONE * 32767, FixMath.div(FixMath.ONE * 32767, FixMath.ONE));
	}

	public void testAtan2() {
		assertEquals(FixMath.ONE/2, FixMath.atan2(FixMath.ONE, 0));
		assertEquals(0, FixMath.atan2(0, FixMath.ONE));
		assertEquals(-FixMath.ONE/2, FixMath.atan2(-FixMath.ONE, 0));
		assertEquals(-FixMath.ONE, FixMath.atan2(0, -FixMath.ONE));
		assertEquals(0, FixMath.atan2(0, 1));
		assertEquals(0, FixMath.atan2(0, FixMath.ONE * 32767));
		// Special case: atan2(0,0) = 0
		assertEquals(0, FixMath.atan2(0, 0));
		// Bound for the error on the unit circle
		for (int i = -180; i < 180; i++) {
			double d = i * Math.PI / 180;
			double x = Math.cos(d);
			double y = Math.sin(d);
			int fx = FixMath.toFixed(x);
			int fy = FixMath.toFixed(y);
			double fatan2 = Math.PI * FixMath.toDouble(FixMath.atan2(fy, fx));
			assertEquals(d, fatan2, 2.2 * DELTA);
		}
	}

	public void testSqrt() {
		assertEquals(0, FixMath.sqrt(0));
		assertEquals(FixMath.ONE, FixMath.sqrt(FixMath.ONE));
		// Here we put some bounds on the error
		// The boundaries are related to the cases in the implementation
		for (int i = 1; i < FixMath.ONE; i++) {
			double x = FixMath.toDouble(i);
			double sqrt = Math.sqrt(x);
			double fsqrt = FixMath.toDouble(FixMath.sqrt(i));
			assertEquals("sqrt("+x+")", sqrt, fsqrt, 0.5 * DELTA);
		}
		for (int i = FixMath.ONE; i < FixMath.ONE * 4; i++) {
			double x = FixMath.toDouble(i);
			double sqrt = Math.sqrt(x);
			double fsqrt = FixMath.toDouble(FixMath.sqrt(i));
			assertEquals("sqrt("+x+")", sqrt, fsqrt, 2.5 * DELTA);
		}
		for (int i = FixMath.ONE * 4; i < FixMath.ONE * 16; i++) {
			double x = FixMath.toDouble(i);
			double sqrt = Math.sqrt(x);
			double fsqrt = FixMath.toDouble(FixMath.sqrt(i));
			assertEquals("sqrt("+x+")", sqrt, fsqrt, 5.75 * DELTA);
		}
		// In general the error is linear in the result
		// but the above bounds are tighter for those ranges
		for (int i = FixMath.ONE; i < FixMath.ONE * 32767; i += FixMath.ONE/4) {
			double x = FixMath.toDouble(i);
			double sqrt = Math.sqrt(x);
			double fsqrt = FixMath.toDouble(FixMath.sqrt(i));
			assertEquals("sqrt("+x+")", sqrt, fsqrt, sqrt * 2.5 * DELTA);
		}
	}

}
