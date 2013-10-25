/**
 * 
 */
package edu.cmu.cs.lti.zhengzhl.algorithm.decode;

import com.google.common.collect.Table;

/**
 * @author Zhengzhong Liu, Hector
 * 
 */
public class HmmDecoder extends ViterbiDecoder {
	Table<String, String, Double> logpEmit;
	Table<String, String, Double> logpTrans;

	public HmmDecoder(Table<String, String, Double> logpEmit,
			Table<String, String, Double> logpTrans) {
		this.logpEmit = logpEmit;
		this.logpTrans = logpTrans;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.cmu.cs.lti.zhengzhl.algorithm.decode.ViterbiDecoder#getScore()
	 */
	@Override
	protected double getScore(String[] observations, int index,
			String previousState, String currentState) {
		String emitSymbol = observations[index];

		// no logp are negative 0 is not a good starting points
		double emitLogp = 0;
		if (logpEmit.contains(currentState, emitSymbol)) {
			emitLogp = logpEmit.get(currentState, emitSymbol);
		}

		// no logp are negative 0 is not a good starting points
		double transLogp = 0;
		if (logpTrans.contains(previousState, currentState)) {
			transLogp = logpTrans.get(previousState, currentState);
		}

		if (previousState == null) {
			return transLogp;
		}

		return emitLogp + transLogp;
	}
}
