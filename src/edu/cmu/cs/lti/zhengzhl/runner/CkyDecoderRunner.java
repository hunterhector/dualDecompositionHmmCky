/**
 * 
 */
package edu.cmu.cs.lti.zhengzhl.runner;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import com.google.common.collect.Table;

import edu.cmu.cs.lti.zhengzhl.algorithm.decode.CkyDecoder;
import edu.cmu.cs.lti.zhengzhl.algorithm.utils.Lagrangian;
import edu.cmu.cs.lti.zhengzhl.io.InputReader;
import edu.cmu.cs.lti.zhengzhl.io.ModelReader;
import edu.cmu.cs.lti.zhengzhl.model.Parse;

/**
 * @author Zhengzhong Liu, Hector
 * 
 */
public class CkyDecoderRunner {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		final long startTime = System.currentTimeMillis();

		File pcfg = new File("data/hw3-v1.2/pcfg");

		File inputFile = new File("data/hw3-v1.2/dev_sents");

		PrintWriter writer = new PrintWriter("data/hw3-v1.2/parses.txt");

		Table<String, String, Double> pcfgRules = ModelReader.fromFile(pcfg);

		System.out.println(String.format("%d pcfg entries read", pcfgRules.size()));

		CkyDecoder decoder = new CkyDecoder(pcfgRules);

		for (String line : InputReader.getLines(inputFile)) {
			String[] tokens = line.split(" ");
			Lagrangian.initialize(tokens.length);
			Parse tree = decoder.decode(tokens);

			writer.println(tree);
		}

		writer.close();

		final long endTime = System.currentTimeMillis();
		System.out.println("Total execution time: " + (endTime - startTime) / 1000.0);
	}
}
