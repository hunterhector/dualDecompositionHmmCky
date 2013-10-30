/**
 * 
 */
package edu.cmu.cs.lti.zhengzhl.runner;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Table;

import edu.cmu.cs.lti.zhengzhl.algorithm.decode.HmmDecoder;
import edu.cmu.cs.lti.zhengzhl.algorithm.utils.Lagrangian;
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

		//input output
		File inputFile = new File("data/hw3-v1.2/test_sents");
		PrintWriter writer = new PrintWriter("data/hw3-v1.2/test-plain-postags-zhengzhl.txt");

		Table<String, String, Double> logpEmit = ModelReader.fromFile(emitFile);
		Table<String, String, Double> logpTrans = ModelReader.fromFile(transFile);

		System.out.println(String.format("%d hmm emission entries read", logpEmit.size()));
		System.out.println(String.format("%d hmm transition entries read", logpTrans.size()));

		HmmDecoder hmmDecoder = new HmmDecoder(logpEmit, logpTrans, "sentence_boundary");

		for (String line : InputReader.getLines(inputFile)) {
			String[] tokens = line.split(" ");
			Lagrangian.initialize(tokens.length);
			String[] tagSeq = hmmDecoder.decode(tokens);

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
