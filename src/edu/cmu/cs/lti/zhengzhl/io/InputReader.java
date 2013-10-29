/**
 * 
 */
package edu.cmu.cs.lti.zhengzhl.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Zhengzhong Liu, Hector
 * 
 */
public class InputReader {
	public static List<String> getLines(File inputFile) throws IOException {
		List<String> lines = new ArrayList<String>();

		FileInputStream fstream = new FileInputStream(inputFile);
		BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

		String strLine;
		while ((strLine = br.readLine()) != null) {
			lines.add(strLine);
		}
		br.close();

		return lines;
	}
}
