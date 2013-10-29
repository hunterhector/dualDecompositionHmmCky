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
	double smallProb = -1000;

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

		double emitLogp = smallProb;
		if (logpEmit.contains(currentState, emitSymbol)) {
			emitLogp = logpEmit.get(currentState, emitSymbol);

		}

		if (previousState == null) {
			return emitLogp;
		}

		double transLogp = smallProb;

		if (logpTrans.contains(previousState, currentState)) {
			transLogp = logpTrans.get(previousState, currentState);
		}

		return emitLogp + transLogp;
	}
}
