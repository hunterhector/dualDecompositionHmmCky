/**
 * 
 */
package edu.cmu.cs.lti.zhengzhl.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

/**
 * A class to read in the model files, assmuming the following format per line:
 * 
 * the distribution p(decision|context) is written as:
 * 
 * context\tdecision\tlogp(decision|context)
 * 
 * Tabs are delimiters, additional inside splits should be done by single space
 * 
 * @author hector
 * 
 */
public class ModelReader {
	public static Table<String, String, Double> fromFile(File modelFile)
			throws IOException {
		Table<String, String, Double> distributions = HashBasedTable.create();

		FileInputStream fstream = new FileInputStream(modelFile);
		BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

		String strLine;

		while ((strLine = br.readLine()) != null) {
			String[] parts = strLine.split("\t");
			if (parts.length != 3) {
				continue;
			} else {
				distributions.put(parts[0], parts[1],
						Double.parseDouble(parts[2]));
			}
		}
		br.close();
		return distributions;
	}

	public static void main(String[] args) throws IOException {
		Table<String, String, Double> pcfgDist = ModelReader.fromFile(new File(
				"data/hw3/pcfg"));
		Table<String, String, Double> hmmEmi = ModelReader.fromFile(new File(
				"data/hw3/hmm_emits"));
		Table<String, String, Double> hmmTrans = ModelReader
				.fromFile(new File("data/hw3/hmm_trans"));
		System.out.println(String.format("%d pcfg distribution entires read",
				pcfgDist.size()));
		System.out.println(String.format("%d hmm emission entries read",
				hmmEmi.size()));
		System.out.println(String.format("%d hmm transition entries read",
				hmmTrans.size()));
	}
}