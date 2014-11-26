package nlp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map.Entry;

import neural.RecurringNNSave;

public class RecurrentNNUsage {
	private static final String UNKNOWN = "UNKNOWN";
	private static Hashtable<String, Integer> indices;
	private static RecurringNNSave network;

	public static HashSet<String> getTokens(String[][] inputs) {
		HashSet<String> ret = new HashSet<String>();
		for (String[] input : inputs) {
			for (String token : input) {
				ret.add(token);
			}
		}
		return ret;
	}

	public static Hashtable<String, Integer> getIndices(HashSet<String> tokens) {
		Hashtable<String, Integer> ret = new Hashtable<String, Integer>();
		int index = 0;
		for (String token : tokens) {
			ret.put(token, index);
			index++;
		}
		return ret;
	}

	public static double[][] convertToProblem(String[] input, Hashtable<String, Integer> indices) {
		double[][] problem = new double[input.length][indices.size()];
		for (int j = 0; j < input.length; j++) {
			if (!indices.containsKey(input[j])) {
				problem[j][indices.get(UNKNOWN)] = 1;
			} else {
				problem[j][indices.get(input[j])] = 1;
			}
		}
		return problem;
	}

	public static double[][][] getProblems(String[][] inputs, Hashtable<String, Integer> indices) {
		double[][][] problems = new double[inputs.length][][];
		for (int i = 0; i < inputs.length; i++) {
			problems[i] = convertToProblem(inputs[i], indices);
		}
		return problems;
	}

	public static double[][] getSolutions(int numOutputs, int[] answers) {
		double[][] solutions = new double[answers.length][numOutputs];
		for (int i = 0; i < answers.length; i++) {
			solutions[i][answers[i]] = 1;
		}
		return solutions;
	}

	private static String clean(String input) {
		input = input.toLowerCase();
		String[] rep = { ".", "?", "'", "`" };
		for (int i = 0; i < rep.length; i++) {
			input = input.replace(rep[i], "");
		}
		return input;
	}

	@SuppressWarnings("unchecked")
	private static ArrayList<Object>[] parseProblems(String file) {
		ArrayList<Integer> answers = new ArrayList<Integer>();
		ArrayList<String[]> problems = new ArrayList<String[]>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File(file)));
			String line;
			while ((line = br.readLine()) != null) {
				if (line.length() > 0) {
					answers.add(Integer.parseInt(line.substring(1, line.indexOf(":"))));
					problems.add(clean(line.substring(line.indexOf(':') + 1, line.length())).split(" "));
				}
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new ArrayList[] { problems, answers };
	}

	static void parseProblemsFrom(String file) {
		ArrayList<Object>[] parse = parseProblems(file);

		String[][] inputs = new String[parse[0].size()][];
		parse[0].toArray(inputs);
		int[] answers = new int[parse[1].size()];
		int numOutputs = -1;
		for (int i = 0; i < parse[1].size(); i++) {
			answers[i] = (Integer) parse[1].get(i);
			numOutputs = Math.max(numOutputs, answers[i] + 1);
		}
		indices = getIndices(getTokens(inputs));
		indices.put(UNKNOWN, numOutputs - 1);
		writeIndicesToFile("indices");

		int numInputs = getTokens(inputs).size();

		double[][][] problems = getProblems(inputs, indices);
		double[][] solutions = getSolutions(numOutputs, answers);

		network = new RecurringNNSave(numInputs, numOutputs, 10);
		network.setGammaTruncation(0.3);
		network.setVerbosity(2);

		network.trainTillPerfection(problems, solutions);
	}

	public static void readIndicesFromFile(String fileString) {
		indices = new Hashtable<String, Integer>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File(fileString)));
			String line;
			while ((line = br.readLine()) != null) {
				indices.put(line.substring(0, line.lastIndexOf(':')), Integer.parseInt(line.substring(line.lastIndexOf(':') + 1)));
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void writeIndicesToFile(String fileString) {
		File file = new File(fileString);
		try {
			FileWriter writer = new FileWriter(file);
			for (Entry<String, Integer> entry : indices.entrySet()) {
				writer.append(entry.getKey() + ":" + entry.getValue() + "\n");
			}
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		// String file = "traindata.txt";
		// parseProblemsFrom(file);
		//
		// network.writeToFile("network");

		network = RecurringNNSave.parseFromFile(args[0]);
		readIndicesFromFile(args[1]);

		String input = args[2];
		double[][] test = convertToProblem(input.split(" "), indices);
		network.calculateOutput(test);
		int maxIndex = 0;
		double val = 0;
		for (int i = 0; i < network.getNumOutputs(); i++) {
			if (val < network.outputNeurons[i].getAdjustedGamma(0)) {
				maxIndex = i;
				val = network.outputNeurons[i].getAdjustedGamma(0);
			}
		}
		System.out.println(maxIndex);

		// Scanner s = new Scanner(System.in);
		// while (s.hasNext()) {
		// String input = s.nextLine();
		// double[][] test = convertToProblem(input.split(" "), indices);
		// network.calculateOutput(test);
		// for (int i = 0; i < network.getNumOutputs(); i++) {
		// System.out.println(i + ":" +
		// network.outputNeurons[i].getAdjustedGamma(0));
		// }
		// }
		// s.close();
	}
}
