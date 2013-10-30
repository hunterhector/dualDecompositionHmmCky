package edu.cmu.cs.lti.zhengzhl.algorithm.decode;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.common.collect.Table.Cell;

import edu.cmu.cs.lti.zhengzhl.algorithm.utils.Lagrangian;
import edu.cmu.cs.lti.zhengzhl.model.CkyCell;
import edu.cmu.cs.lti.zhengzhl.model.Parse;
import edu.cmu.cs.lti.zhengzhl.model.RuleLhs;

/**
 * Implements a CKY decoder with a Lagrangian multiplier. The weight is
 * considered to be general so this is can be used for PCFG or normal WCFG, but
 * restricted to Chomsky Normal Form
 * 
 * @author Zhengzhong Liu, Hector
 * 
 */
public class CkyDecoder {

	// inverted tables for CNF rules
	private ArrayListMultimap<String, RuleLhs> originalLexicalRules = ArrayListMultimap.create();

	private Table<String, String, Set<RuleLhs>> binaryRules = HashBasedTable.create();

	private Set<String> allPreTerminals = new HashSet<String>();

	Map<String, CkyCell>[][] chart;

	private final String ROOT_NON_TERMINAL_SYMBOL = "S";

	private final double VERY_SMALL_LOG_PROB = -10000;

	public CkyDecoder(Table<String, String, Double> ruleLogps) {
		parseRules(ruleLogps);
	}

	private void parseRules(Table<String, String, Double> ruleLogps) {
		int bCounter = 0;
		for (Cell<String, String, Double> rule : ruleLogps.cellSet()) {
			String lhs = rule.getRowKey();
			String rhs = rule.getColumnKey();
			double logp = rule.getValue();

			String[] rhsParts = rhs.split(" ");

			RuleLhs ruleLhs = new RuleLhs(lhs, logp);

			if (rhsParts.length == 2) {
				if (binaryRules.contains(rhsParts[0], rhsParts[1]))
					binaryRules.get(rhsParts[0], rhsParts[1]).add(ruleLhs);
				else {
					Set<RuleLhs> newSet = new HashSet<RuleLhs>();
					newSet.add(ruleLhs);
					binaryRules.put(rhsParts[0], rhsParts[1], newSet);
				}
				bCounter++;
			} else if (rhsParts.length == 1) {
				originalLexicalRules.put(rhs, ruleLhs);
				allPreTerminals.add(lhs);
			} else {
				System.err.println(String.format("Invalid rule : %s -> %s", lhs, rhs));
				throw new IllegalArgumentException("Right hand side rule more than one, not a CNF grammar");
			}
		}
		System.out.println(String.format("Stats: %d lexical rules, %d binary rules, %d pre-terminals", originalLexicalRules.size(), bCounter,
				allPreTerminals.size()));

	}

	public Parse decode(String[] tokens) {
		// initialize the chart
		int sentLength = tokens.length;
		initializeChart(sentLength);

		// System.err.println("Sentence length is " + sentLength);

		if (fillLexicalCells(tokens)) {
			// System.err.println("Filled with lexicals");
			for (int width = 2; width <= sentLength; width++) {
				for (int i = 0; i <= sentLength - width; i++) {
					int j = i + width;
					for (int k = i + 1; k < j; k++) {
						if (chart[i][k].isEmpty() || chart[k][j].isEmpty()) {
							// a little speed up
							continue;
						} else {
							for (String leftChild : chart[i][k].keySet()) {
								if (binaryRules.containsRow(leftChild)) {
									for (String rightChild : chart[k][j].keySet()) {
										if (binaryRules.contains(leftChild, rightChild)) {
											for (RuleLhs lhs : binaryRules.get(leftChild, rightChild)) {
												double leftLogProb = chart[i][k].get(leftChild).getMarginalLogp();
												double rightLogProb = chart[k][j].get(rightChild).getMarginalLogp();
												double newLogProb = lhs.getLogProb() + leftLogProb + rightLogProb;

												if (!chart[i][j].containsKey(lhs.getNonTerminal())
														|| (newLogProb > chart[i][j].get(lhs.getNonTerminal()).getMarginalLogp())) {
													chart[i][j].put(lhs.getNonTerminal(), new CkyCell(i, k, j, leftChild, rightChild, newLogProb));
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		} else {
			System.err.println("OOV not handle, cannot parse this sentence");
			return Parse.EMPTY_PARSE();
		}

		Map<String, CkyCell> rootCell = chart[0][sentLength];

		if (rootCell.isEmpty()) {
			System.err.println("Root is empty");
			printChart(chart);

			return Parse.EMPTY_PARSE();
		} else if (rootCell.containsKey(ROOT_NON_TERMINAL_SYMBOL)) {
			// double maxScore = Double.NEGATIVE_INFINITY;
			// String bestRoot = null;
			//
			// CkyCell ckyCell = rootCell.get(ROOT_NON_TERMINAL_SYMBOL);
			// for (Entry<String, CkyCell> cell : rootCell.entrySet())
			// if (cell.getKey().equals(ROOT_NON_TERMINAL_SYMBOL)) {
			// if (cell.getValue().getMarginalLogp() > maxScore) {
			// maxScore = cell.getValue().getMarginalLogp();
			// bestRoot = cell.getKey();
			// }
			// }

			Parse parse = recover(chart, ROOT_NON_TERMINAL_SYMBOL, 0, sentLength);
			return parse;
		} else {
			System.err.println("Cannot prove root node");
			return Parse.EMPTY_PARSE();
		}
	}

	private void printChart(Map<String, CkyCell>[][] chart) {
		for (int i = 0; i < chart.length; i++) {
			for (int j = 0; j < chart[i].length; j++) {

				String keySetStr = chart[i][j] != null ? "[" + chart[i][j].keySet().isEmpty() + "]" : "[-]";
				System.err.print(String.format("%d,%d : %s\t", i, j, keySetStr));
			}
			System.err.println();
		}
	}

	private Parse recover(Map<String, CkyCell>[][] chart, String root, int i, int j) {
		if (j == i + 1) {
			return new Parse(root, new Parse(chart[i][j].get(root).getSingleChild()));
		} else {
			CkyCell parent = chart[i][j].get(root);
			if (parent == null) {
				System.out.println(i + " " + j);
				System.out.println(root);
				// System.err.println(chart[i][j]);
				System.err.println("Hey!");
			}
			Parse leftTree = recover(chart, parent.getLeftChild(), parent.getFrom(), parent.getSplitAt());
			Parse rightTree = recover(chart, parent.getRightChild(), parent.getSplitAt(), parent.getTo());

			return new Parse(root, leftTree, rightTree);
		}
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
			if (originalLexicalRules.containsKey(tokens[i])) {
				for (RuleLhs lhs : originalLexicalRules.get(tokens[i])) {
					String nt = lhs.getNonTerminal();
					chart[i][j].put(nt, new CkyCell(i, j, tokens[i], lhs.getLogProb() + Lagrangian.getLangrangian(i, nt)));
					// System.out.println(lhs.getNonTerminal() + " " + tokens[i]
					// + " " + i + " " + j + lhs.getLogProb());
				}
			} else {// deal with oov
				// for all preterminals, we add a rule
				for (String nt : allPreTerminals) {
					chart[i][j].put(nt, new CkyCell(i, j, tokens[i], VERY_SMALL_LOG_PROB + Lagrangian.getLangrangian(i, nt)));
				}
			}
		}
		return true; // no oov cases
	}

}
