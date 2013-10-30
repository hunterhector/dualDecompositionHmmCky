/**
 * 
 */
package edu.cmu.cs.lti.zhengzhl.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Zhengzhong Liu, Hector
 * 
 */
public class Parse {

	private String root;
	private List<Parse> children;

	public static Parse EMPTY_PARSE() {
		return new Parse(null);
	}

	public boolean isEmptyParse() {
		return root == null;
	}

	public Parse(String root, Parse... children) {
		this.root = root;
		this.children = new ArrayList<Parse>(children.length);

		for (Parse child : children) {
			this.children.add(child);
		}
	}

	public String getRoot() {
		return root;
	}

	public List<Parse> getChildren() {
		return children;
	}

	public String toString() {
		if (isEmptyParse()) {
			return "";
		} else if (children.size() == 0) {
			return root.toString();
		} else {
			String result = "(" + root;
			for (Parse child : children) {
				if (child.isEmptyParse()) {
					result += " EMPTY";
				} else {
					result += " " + child;
				}
			}
			result += ")";
			return result;
		}
	}

	public List<String> getParseTags() {
		if (isEmptyParse()) {
			return new ArrayList<String>(0);
		} else if (children.size() == 1) {
			List<String> tags = Arrays.asList(root.toString());
			return tags;
		} else {
			List<String> tags = new ArrayList<String>();

			for (Parse child : children) {
				if (!child.isEmptyParse()) {
					tags.addAll(child.getParseTags());
				}
			}
			return tags;
		}
	}

}
