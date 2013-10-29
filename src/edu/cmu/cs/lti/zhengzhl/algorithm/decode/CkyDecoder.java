package edu.cmu.cs.lti.zhengzhl.algorithm.decode;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.Table;

import edu.cmu.cs.lti.zhengzhl.model.CkyCell;

/**
 * Implements a CKY decoder with a Lagrangian multiplier. The weight is
 * considered to be general so this is can be used for PCFG or normal WCFG, but
 * restricted to Chomsky Normal Form
 * 
 * @author Zhengzhong Liu, Hector
 * 
 */
public class CkyDecoder {
	private Table<String, String, Double> ruleLogps;

	private Table<String, String, Double> lexicalRules;

	private Table<String, String, Double> cnfRules;

	Map<String, CkyCell>[][] chart;

	double smallLogProb = -1000;

	public CkyDecoder(Table<String, String, Double> ruleLogps) {
		this.ruleLogps = ruleLogps;
	}

	public String[] decode(String[] observations, String[] stateSet,
			double[] langrangian) {
		// initialize the chart
		int sentLength = observations.length;
		initializeChart(sentLength);

		if (fillLexicalCells(observations)) {

		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private void initializeChart(int sentLength) {
		chart = new Map[sentLength][sentLength + 1];
		for (int i = 0; i < sentLength; i++) {
			for (int j = i + 1; j <= sentLength; j++) {
				chart[i][j] = new HashMap<String, CkyCell>();
			}
		}
	}

	private boolean fillLexicalCells(String[] tokens) {
		for (int i = 0; i < tokens.length; i++) {
			int j = i + 1;
			if (lexicalRules.containsColumn(tokens[i])) {
				for (Entry<String, Double> lhs : lexicalRules.column(tokens[i])
						.entrySet()) {
					chart[i][j].put(lhs.getKey(), new CkyCell(i, j, tokens[i],
							lhs.getValue()));

				}

			} else {// deal with oov later
				return false;
			}
		}
		return true; // no oov cases
	}

	private String[] recover(double[][] lattice, int[][] backPointers,
			String[] stateSet) {

		return null;
	}

	private double getScore(String[] observations, int index,
			String previousState, String currentState, double[] langrangian) {

		return getScore(observations, index, previousState, currentState)
				+ langrangian[index];
	}

	protected double getScore(String[] observations, int index,
			String previousState, String currentState) {
		return 0;
	}
}
