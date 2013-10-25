/**
 * 
 */
package edu.cmu.cs.lti.zhengzhl.runner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Table;

import edu.cmu.cs.lti.zhengzhl.algorithm.decode.HmmDecoder;
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
		File emitFile = new File("data/hw3-v1.1/hmm_emits");
		File transFile = new File("data/hw3-v1.1/hmm_trans");
		File inputFile = new File("data/hw3-v1.1/dev_sents");

		Table<String, String, Double> logpEmit = ModelReader.fromFile(emitFile);
		Table<String, String, Double> logpTrans = ModelReader
				.fromFile(transFile);

		System.out.println(String.format("%d hmm emission entries read",
				logpEmit.size()));
		System.out.println(String.format("%d hmm transition entries read",
				logpTrans.size()));

		HmmDecoder hmmDecoder = new HmmDecoder(logpEmit, logpTrans);

		Set<String> allStateSymbols = logpEmit.rowKeySet();

		// get all possible states from the model file
		String[] states = allStateSymbols.toArray(new String[allStateSymbols
				.size()]);

		System.out.println(String.format("Number of tags in total : %d",
				states.length));

		for (String line : getLines(inputFile)) {
			System.out.println(line);
			String[] tagSeq = hmmDecoder.decode(line.split(" "), states);
			for (int i = 0; i < tagSeq.length; i++) {
				System.out.print(tagSeq[i] + " ");
			}
			System.out.println();
		}

	}

	private static List<String> getLines(File inputFile) throws IOException {
		List<String> lines = new ArrayList<String>();

		FileInputStream fstream = new FileInputStream(inputFile);
		BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

		String strLine;
		while ((strLine = br.readLine()) != null) {
			String[] parts = strLine.split("\t");
			lines.add(strLine);
		}
		br.close();

		return lines;
	}

}
