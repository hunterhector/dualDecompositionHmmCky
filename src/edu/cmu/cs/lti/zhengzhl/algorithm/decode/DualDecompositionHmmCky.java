/**
 * 
 */
package edu.cmu.cs.lti.zhengzhl.algorithm.decode;

import java.util.List;

import com.google.common.collect.Table;

import edu.cmu.cs.lti.zhengzhl.algorithm.utils.Lagrangian;
import edu.cmu.cs.lti.zhengzhl.model.Parse;

/**
 * @author Zhengzhong Liu, Hector
 * 
 */
public class DualDecompositionHmmCky {

	private HmmDecoder hmmDecoder;
	private CkyDecoder ckyDecoder;

	private Parse parse;

	public Parse getParse() {
		return parse;
	}

	public String[] getTags() {
		return tags;
	}

	private String[] tags;

	public DualDecompositionHmmCky(Table<String, String, Double> logpEmit, Table<String, String, Double> logpTrans,
			Table<String, String, Double> pcfgRules) {
		hmmDecoder = new HmmDecoder(logpEmit, logpTrans, "sentence_boundary");
		ckyDecoder = new CkyDecoder(pcfgRules);
	}

	public void decode(String[] tokens) {
		Lagrangian.initialize(tokens.length);

		for (int k = 0; k < 50; k++) {
			double stepSize = 0.005;
			// double stepSize = 1.0 / k;

			// System.out.println("Iter " + k);
			parse = ckyDecoder.decode(tokens);
			tags = hmmDecoder.decode(tokens);

			List<String> parseTags = parse.getParseTags();

			boolean optimal = true;

			for (int i = 0; i < tags.length; i++) {
				if (!parseTags.get(i).equals(tags[i])) {
					// y^k(i,t_tag) is 0, z^k(i,t_tag) is 1
					double updatedLangTagT = Lagrangian.getLagrangian(i, tags[i]) + stepSize;
					Lagrangian.setLagrangian(i, tags[i], updatedLangTagT);

					// y^k(i,t_parse) is 1, z^k(i,t_parse) is 0
					double updatedLangTagP = Lagrangian.getLagrangian(i, parseTags.get(i)) - stepSize;
					Lagrangian.setLagrangian(i, parseTags.get(i), updatedLangTagP);

					optimal = false;
					// System.out.println(String.format("Different at position %d : [%s - %s]",
					// i, parseTags.get(i), tags[i]));
				}
			}

			if (optimal) {
				System.out.println(String.format("Get optimial in %d iterations", k));
				return;
			}
		}

		System.out.println(String.format("Exit in max iterations"));
	}
}
