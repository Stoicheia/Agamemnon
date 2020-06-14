package comp1140.ass2;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

import java.util.Arrays;

import static org.junit.Assert.assertTrue;

public class GetTotalScoreTest {
	@Rule
	public Timeout globalTimeout = Timeout.millis(1000);

	private void test(String[] in, int[] expected) {
		int[] out = Agamemnon.getTotalScore(in);
		assertTrue("For input state: '" + Arrays.toString(in)+ "', expected " + Arrays.toString(expected) + " but got " + Arrays.toString(out),
				out[0] == expected[0] && out[1] == expected[1]);
	}

	@Test
	public void testShort() {
		String[] tiles = {
				"",
				"Oa00",
				"Oi00",
				"Oj00",
				"Oa00Bf09",
				"Oa00Bf04",
				"Oa00Bg04",
				"Oa00Bg09Bj04",
				"Oa00Bg09Bi04",
				"Oa00Bg09Bj04Oe02",
				"Oa00Bg09Bj04Oe02Oj11",
		};
		int[][] points = {
				{0, 0},
				{2, 0},
				{0, 0},
				{0, 0},
				{2, 3},
				{0, 0},
				{0, 3},
				{0, 5},
				{2, 3},
				{4, 5},
				{6, 5},
		};
		for (int i = 0; i < tiles.length; i++) {
			String[] state = {
					tiles[i],
					TestData.SAMPLE_EDGE_MAP
			};

			test(state, points[i]);
		}
	}

	@Test
	public void testFull() {
		for (int i = 0; i < TestData.SAMPLE_FINISH_STATE.length; i++) {
			String[] state = {
					TestData.SAMPLE_FINISH_STATE[i],
					TestData.SAMPLE_EDGE_MAP
			};

			test(state, TestData.SAMPLE_TOTAL_POINTS[i]);
		}
	}
}
