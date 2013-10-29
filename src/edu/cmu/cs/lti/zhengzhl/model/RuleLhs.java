/**
 * 
 */
package edu.cmu.cs.lti.zhengzhl.model;

/**
 * @author Zhengzhong Liu, Hector
 * 
 */
public class RuleLhs {
	private String nonTerminal;
	private double logProb;

	public RuleLhs(String nonTerminal, double logProb) {
		this.nonTerminal = nonTerminal;
		this.logProb = logProb;
	}

	public String getNonTerminal() {
		return nonTerminal;
	}

	public double getLogProb() {
		return logProb;
	}

	public String toString() {
		return nonTerminal + " :[" + logProb + "]";

	}
}
