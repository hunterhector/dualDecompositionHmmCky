/**
 * 
 */
package edu.cmu.cs.lti.zhengzhl.io;

import java.io.PrintWriter;

/**
 * @author Zhengzhong Liu, Hector
 * 
 */
public class ResultWriter {

	public static void writeTags(String[] tags, PrintWriter writer) {
		for (int i = 0; i < tags.length; i++) {
			writer.print(tags[i]);
			if (i < tags.length - 1) {
				writer.print(" ");
			}
		}
		writer.println();
	}

}
