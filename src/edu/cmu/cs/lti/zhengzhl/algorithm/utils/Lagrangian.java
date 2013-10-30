/**
 * 
 */
package edu.cmu.cs.lti.zhengzhl.algorithm.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Zhengzhong Liu, Hector
 * 
 */
public class Lagrangian {
	private static List<Map<String, Double>> lagrangian;

	public static void initialize(int length) {
		lagrangian = new ArrayList<Map<String, Double>>(length);
		for (int i = 0; i < length; i++) {
			lagrangian.add(new HashMap<String, Double>());
		}
	}

	public static double getLagrangian(int i, String t) {
		if (lagrangian.get(i).containsKey(t)) {
			return lagrangian.get(i).get(t);
		} else {
			return 0;
		}
	}

	public static void setLagrangian(int i, String t, double score) {
		lagrangian.get(i).put(t, score);
	}

}
