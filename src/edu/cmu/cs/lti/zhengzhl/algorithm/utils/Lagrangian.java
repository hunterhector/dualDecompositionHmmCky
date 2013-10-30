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
	private static List<Map<String, Double>> langrangian;

	public static void initialize(int length) {
		langrangian = new ArrayList<Map<String, Double>>(length);
		for (int i = 0; i < length; i++) {
			langrangian.add(new HashMap<String, Double>());
		}
	}

	public static double getLangrangian(int i, String t) {
		if (langrangian.get(i).containsKey(t)) {
			return langrangian.get(i).get(t);
		} else {
			return 0;
		}
	}

	public static void setLangrangian(int i, String t, double score) {
		langrangian.get(i).put(t, score);
	}

}
