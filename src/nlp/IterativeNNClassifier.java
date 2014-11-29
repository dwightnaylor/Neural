/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nlp;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 *
 * @author daniel
 */
public class IterativeNNClassifier {

	public static String cleanAndSplit(String str) {
		return str.replaceAll("[^a-zA-Z ]", "").toLowerCase();
	}

	public static List<String> convertSentenceToTokens(String str) {
		List<String> tokens = new ArrayList<>();
		StringTokenizer st = new StringTokenizer(cleanAndSplit(str));
		while (st.hasMoreElements()) {
			tokens.add(st.nextElement().toString());
		}
		return tokens;
	}

}
