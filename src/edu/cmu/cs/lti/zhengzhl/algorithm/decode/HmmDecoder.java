/**
 * 
 */
package edu.cmu.cs.lti.zhengzhl.algorithm.decode;

import java.util.Set;

import com.google.common.collect.Table;

import edu.cmu.cs.lti.zhengzhl.algorithm.utils.Lagrangian;

/**
 * @author Zhengzhong Liu, Hector
 * 
 */
public class HmmDecoder extends ViterbiDecoder {
	double VERY_SMALL_LOG_PROB = -10000;

	Table<String, String, Double> logpEmit;
	Table<String, String, Double> logpTrans;

	public HmmDecoder(Table<String, String, Double> logpEmit, Table<String, String, Double> logpTrans) {
		super(logpEmit.rowKeySet().toArray(new String[logpEmit.rowKeySet().size()]));
		this.logpEmit = logpEmit;
		this.logpTrans = logpTrans;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.cmu.cs.lti.zhengzhl.algorithm.decode.ViterbiDecoder#getScore()
	 */
	@Override
	protected double getScore(String[] observations, int index, String previousState, String currentState) {
		String emitSymbol = observations[index];

		double emitLogp = VERY_SMALL_LOG_PROB;
		if (logpEmit.contains(currentState, emitSymbol)) {
			emitLogp = logpEmit.get(currentState, emitSymbol);
		}

		if (previousState == null) {
			return emitLogp;
		}

		double transLogp = VERY_SMALL_LOG_PROB;

		if (logpTrans.contains(previousState, currentState)) {
			transLogp = logpTrans.get(previousState, currentState);
		}

		return emitLogp + transLogp - Lagrangian.getLangrangian(index, currentState);
	}
}