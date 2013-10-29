/**
 * 
 */
package edu.cmu.cs.lti.zhengzhl.algorithm.decode;

/**
 * A decoder with a Lagrangian multiplier, you could implement it as HMM or CRF
 * 
 * Current only 1st order
 * 
 * @author Zhengzhong Liu, Hector
 * 
 */
public abstract class ViterbiDecoder {
	public String[] decode(String[] observations, String[] stateSet, double[] langrangian) {
		double[][] lattice = new double[observations.length][stateSet.length];
		int[][] backPointers = new int[observations.length][stateSet.length];

		viterbi(observations, stateSet, lattice, backPointers, langrangian);

		return recover(lattice, backPointers, stateSet);
	}

	private String[] recover(double[][] lattice, int[][] backPointers, String[] stateSet) {

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

	private void viterbi(String[] observations, String[] stateSet, double[][] lattice, int[][] backPointers, double[] langrangian) {
		// decode from left to right
		for (int col = 0; col < observations.length; col++) {
			// consider each possible state for this column
			for (int stateIndex = 0; stateIndex < stateSet.length; stateIndex++) {
				String currentState = stateSet[stateIndex];
				if (col == 0) {
					// special case for the first column
					lattice[col][stateIndex] = getScore(observations, col, null, currentState, langrangian);
				} else {
					// aggregate each possible previous state using MAX
					double maxScore = 0;

					backPointers[col][stateIndex] = 0;

					for (int previousStateIndex = 0; previousStateIndex < stateSet.length; previousStateIndex++) {
						double previousMax = lattice[col - 1][previousStateIndex];
						double currentScore = getScore(observations, col, stateSet[previousStateIndex], currentState, langrangian);

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
			}
		}
	}

	private double getScore(String[] observations, int index, String previousState, String currentState, double[] langrangian) {
		return getScore(observations, index, previousState, currentState) - langrangian[index];
	}

	protected abstract double getScore(String[] observations, int index, String previousState, String currentState);
}
