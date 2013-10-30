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

import edu.cmu.cs.lti.zhengzhl.algorithm.decode.DualDecompositionHmmCky;
import edu.cmu.cs.lti.zhengzhl.algorithm.decode.HmmDecoder;
import edu.cmu.cs.lti.zhengzhl.io.InputReader;
import edu.cmu.cs.lti.zhengzhl.io.ModelReader;
import edu.cmu.cs.lti.zhengzhl.model.Parse;

/**
 * @author Zhengzhong Liu, Hector
 * 
 */
public class DualDecoderRunner {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		File emitFile = new File("data/hw3-v1.2/hmm_emits");
		File transFile = new File("data/hw3-v1.2/hmm_trans");
		File inputFile = new File("data/hw3-v1.2/test_sents");
		File pcfg = new File("data/hw3-v1.2/pcfg");

		PrintWriter tagWriter = new PrintWriter("data/hw3-v1.2/hmm_tags_dual_test.txt");
		PrintWriter parseWriter = new PrintWriter("data/hw3-v1.2/parses_dual_test.txt");

		Table<String, String, Double> logpEmit = ModelReader.fromFile(emitFile);
		Table<String, String, Double> logpTrans = ModelReader.fromFile(transFile);

		Table<String, String, Double> pcfgRules = ModelReader.fromFile(pcfg);

		System.out.println(String.format("%d hmm emission entries read", logpEmit.size()));
		System.out.println(String.format("%d hmm transition entries read", logpTrans.size()));
		System.out.println(String.format("%d pcfg entries read", pcfgRules.size()));

		DualDecompositionHmmCky decoder = new DualDecompositionHmmCky(logpEmit, logpTrans, pcfgRules);

		for (String line : InputReader.getLines(inputFile)) {
			String[] tokens = line.split(" ");
			decoder.decode(tokens);

			String[] tagSeq = decoder.getTags();
			Parse parse = decoder.getParse();

			writeTags(tagSeq, tagWriter);
			parseWriter.println(parse);
		}

		tagWriter.close();
		parseWriter.close();
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
