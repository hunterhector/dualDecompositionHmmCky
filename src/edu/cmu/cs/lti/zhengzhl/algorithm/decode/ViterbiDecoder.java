/**
 * 
 */
package edu.cmu.cs.lti.zhengzhl.algorithm.decode;

import java.util.List;
import java.util.Map;

import edu.cmu.cs.lti.zhengzhl.algorithm.utils.Lagrangian;

/**
 * A decoder with a Lagrangian multiplier, you could implement it as HMM or CRF
 * 
 * Current only 1st order
 * 
 * @author Zhengzhong Liu, Hector
 * 
 */
public abstract class ViterbiDecoder {

	private String[] stateSet;
	private String sentenceBoundary;

	public ViterbiDecoder(String[] stateSet, String sentenceBoundary) {
		this.stateSet = stateSet;

		this.sentenceBoundary = sentenceBoundary;
	}

	public String[] decode(String[] observations) {
		double[][] lattice = new double[observations.length][stateSet.length];
		int[][] backPointers = new int[observations.length][stateSet.length];

		viterbi(observations, lattice, backPointers);

		return recover(lattice, backPointers);
	}

	private String[] recover(double[][] lattice, int[][] backPointers) {
		int sentLength = lattice.length;

		// initialize the seq of tags
		String[] seq = new String[sentLength];

		int maxIndex = 0;
		double maxScore = 0;

		for (int row = 0; row < lattice[0].length; row++) {
			double thisScore = lattice[sentLength - 1][row];
			if (row == 0) {
				maxScore = thisScore;
			} else {
				if (thisScore > maxScore) {
					maxScore = thisScore;
					maxIndex = row;
				}
			}
		}

		seq[sentLength - 1] = stateSet[maxIndex];

		// fill the sequence from right to left
		for (int col = sentLength - 1; col >= 0; col--) {
			if (col > 0) {
				maxIndex = backPointers[col][maxIndex];
				seq[col - 1] = stateSet[maxIndex];
			}
		}

		return seq;
	}

	private void viterbi(String[] observations, double[][] lattice, int[][] backPointers) {
		// decode from left to right
		for (int col = 0; col < observations.length; col++) {
			// System.out.println("Col " + col);
			// consider each possible state for this column
			for (int stateIndex = 0; stateIndex < stateSet.length; stateIndex++) {
				String currentState = stateSet[stateIndex];
				if (col == 0) {
					// special case for the first column
					lattice[col][stateIndex] = getScore(observations, col, sentenceBoundary, currentState);
				} else if (col < observations.length) {
					// aggregate each possible previous state using MAX
					double maxScore = 0;

					backPointers[col][stateIndex] = 0;

					for (int previousStateIndex = 0; previousStateIndex < stateSet.length; previousStateIndex++) {
						double previousMax = lattice[col - 1][previousStateIndex];
						double currentScore = getScore(observations, col, stateSet[previousStateIndex], currentState);

						double sequenceScore = currentScore + previousMax;

						if (previousStateIndex == 0) {
							backPointers[col][stateIndex] = previousStateIndex;
							maxScore = sequenceScore;
						} else if (sequenceScore > maxScore) {
							backPointers[col][stateIndex] = previousStateIndex;
							maxScore = sequenceScore;
						}
					}
					lattice[col][stateIndex] = maxScore;
				}

				// System.out.print(lattice[col][stateIndex] + "->");
				// System.out.print(backPointers[col][stateIndex] + ",");
			}
			// handle sentence end

			// System.out.println();
		}
	}

	protected abstract double getScore(String[] observations, int index, String previousState, String currentState);
}
