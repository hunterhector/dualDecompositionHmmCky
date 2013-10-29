/**
 * 
 */
package edu.cmu.cs.lti.zhengzhl.runner;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;

import com.google.common.collect.Table;

import edu.cmu.cs.lti.zhengzhl.algorithm.decode.HmmDecoder;
import edu.cmu.cs.lti.zhengzhl.io.InputReader;
import edu.cmu.cs.lti.zhengzhl.io.ModelReader;

/**
 * @author Zhengzhong Liu, Hector
 * 
 */
public class HmmDecoderRunner {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		File emitFile = new File("data/hw3-v1.2/hmm_emits");
		File transFile = new File("data/hw3-v1.2/hmm_trans");
		File inputFile = new File("data/hw3-v1.2/dev_sents");

		PrintWriter writer = new PrintWriter("data/hw3-v1.2/hmm_tags.txt");

		Table<String, String, Double> logpEmit = ModelReader.fromFile(emitFile);
		Table<String, String, Double> logpTrans = ModelReader.fromFile(transFile);

		System.out.println(String.format("%d hmm emission entries read", logpEmit.size()));
		System.out.println(String.format("%d hmm transition entries read", logpTrans.size()));

		HmmDecoder hmmDecoder = new HmmDecoder(logpEmit, logpTrans);

		Set<String> allStateSymbols = logpEmit.rowKeySet();

		// get all possible states from the model file
		String[] states = allStateSymbols.toArray(new String[allStateSymbols.size()]);

		System.out.println(String.format("Number of tags in total : %d", states.length));

		for (String line : InputReader.getLines(inputFile)) {
			String[] tokens = line.split(" ");
			double[] langrangian = new double[tokens.length];

			String[] tagSeq = hmmDecoder.decode(tokens, states, langrangian);

			writeTags(tagSeq, writer);
		}

		writer.close();
	}

	private static void writeTags(String[] tags, PrintWriter writer) {
		for (int i = 0; i < tags.length; i++) {
			writer.print(tags[i]);
			if (i < tags.length - 1) {
				writer.print(" ");
			}
		}
		writer.println();
	}

}
