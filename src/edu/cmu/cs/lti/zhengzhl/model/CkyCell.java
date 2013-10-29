/**
 * 
 */
package edu.cmu.cs.lti.zhengzhl.model;

/**
 * @author Zhengzhong Liu, Hector
 * 
 */
public class CkyCell {
	private int from;
	private int to;
	private int splitAt;

	private String leftChild;
	private String rightChild;

	private double marginalLogp;

	public CkyCell(int i, int k, int j, String leftChild, String rightChild) {
		this(i, k, j, leftChild, rightChild, 0.0);
	}

	public CkyCell(int i, int k, int j, String leftChild, String rightChild,
			double logProb) {

		this.from = i;
		this.to = j;
		this.splitAt = k;

		this.leftChild = leftChild;
		this.rightChild = rightChild;

		this.marginalLogp = logProb;
	}

	public CkyCell(int i, int j, String singleChild) {
		this(i, -1, j, singleChild, null, 0.0);
	}

	public CkyCell(int i, int j, String singleChild, double logProb) {
		this(i, -1, j, singleChild, null, logProb);
	}

	public int getFrom() {
		return from;
	}

	public int getTo() {
		return to;
	}

	public int getSplitAt() {
		return splitAt;
	}

	public String getSingleChild() {
		return leftChild;
	}

	public String getLeftChild() {
		return leftChild;
	}

	public String getRightChild() {
		return rightChild;
	}

	public double getMarginalLogp() {
		return marginalLogp;
	}
}
