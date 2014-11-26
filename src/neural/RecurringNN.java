package neural;

import helpers.DelayHelper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class RecurringNN extends NeuralNetwork<double[][], double[]> {

	// fields that are actually important to the network's functioning
	Neuron[] hiddenLayer;
	public Neuron[] outputNeurons;
	private int numAbstractNeurons;
	private double[][] refinedInputs;

	public RecurringNN(int numInputs, int numOutputs, int numAbstractNeurons) {
		super(numInputs, numOutputs);
		this.numAbstractNeurons = numAbstractNeurons;
		hiddenLayer = new Neuron[numAbstractNeurons];
		for (int i = 0; i < hiddenLayer.length; i++) {
			hiddenLayer[i] = new Neuron(getLayerSize());
		}
		outputNeurons = new Neuron[getNumOutputs()];
		for (int i = 0; i < outputNeurons.length; i++) {
			outputNeurons[i] = new Neuron(getLayerSize());
		}
	}

	public static RecurringNN parseFromFile(String file) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File(file)));
			int numInputs = Integer.parseInt(br.readLine());
			int numOutputs = Integer.parseInt(br.readLine());
			int numAbstractNeurons = Integer.parseInt(br.readLine());
			RecurringNN ret = new RecurringNN(numInputs, numOutputs, numAbstractNeurons);
			for (int i = 0; i < numAbstractNeurons; i++) {
				ret.hiddenLayer[i] = Neuron.parse(br.readLine());
			}
			for (int i = 0; i < ret.getNumOutputs(); i++) {
				ret.hiddenLayer[i] = Neuron.parse(br.readLine());
			}
			br.close();
			return ret;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String getCompactNotation() {
		StringBuffer ret = new StringBuffer();
		ret.append(getNumInputs() + "\n");
		ret.append(getNumOutputs() + "\n");
		ret.append(numAbstractNeurons + "\n");
		for (int i = 0; i < numAbstractNeurons; i++) {
			ret.append(hiddenLayer[i].getCompactRepresentation() + "\n");
		}
		for (int i = 0; i < getNumOutputs(); i++) {
			ret.append(outputNeurons[i].getCompactRepresentation() + "\n");
		}
		return ret.toString();
	}

	public void writeToFile(String fileString) {
		try {
			File file = new File(fileString);
			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter fw = new FileWriter(file);
			fw.write(getCompactNotation());
			fw.flush();
			fw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void calculateOutput(double[][] input) {
		refinedInputs = new double[input.length][getLayerSize()];
		for (int i = 0; i < input.length; i++) {
			System.arraycopy(input[i], 0, refinedInputs[i], 0, getNumInputs());
		}
		double[] outputs = new double[getNumOutputs()];
		for (int i = 0; i < input.length - 1; i++) {
			for (int q = 0; q < getNumOutputs(); q++) {
				outputNeurons[q].calculateOutput(refinedInputs[i]);
				outputs[q] += outputNeurons[q].error;
			}
			for (int j = 0; j < numAbstractNeurons; j++) {
				hiddenLayer[j].calculateOutput(refinedInputs[i]);
				if (i < input.length - 1) {
					refinedInputs[i + 1][getNumInputs() + j] = hiddenLayer[j].gamma;
				}
			}
		}
		for (int i = 0; i < getNumOutputs(); i++) {
			outputNeurons[i].calculateOutput(refinedInputs[input.length - 1]);
			outputs[i] += outputNeurons[i].error;
			outputs[i] /= getNumOutputs();
			outputNeurons[i].gamma = outputs[i];
		}
		// for (int i = 0; i < getNumOutputs(); i++) {
		// outputNeurons[i].calculateOutput(refinedInputs[input.length - 1]);
		// }
	}

	public int getLayerSize() {
		return getNumInputs() + numAbstractNeurons;
	}

	@Override
	public void adjustForAnswer(double[][] input, double[] answers) {
		if (answers.length < getNumOutputs()) {
			System.err.println("Desired output length is shorter than real output length.");
		}
		calculateOutput(input);
		for (int i = 0; i < getNumOutputs(); i++) {
			outputNeurons[i].error = answers[i] - outputNeurons[i].gamma;
		}
		for (int h = 0; h < numAbstractNeurons; h++) {
			hiddenLayer[h].error = 0;
		}
		for (int o = 0; o < getNumOutputs(); o++) {
			for (int h = 0; h < numAbstractNeurons; h++) {
				hiddenLayer[h].error += NeuralNetwork.LEARNING_RATE * outputNeurons[o].delta() * outputNeurons[o].weights[h] / input.length;
			}
			double oerr = outputNeurons[o].error;
			outputNeurons[o].adjustWeights(refinedInputs[input.length - 1]);
			outputNeurons[o].error = oerr;
		}
		double[] errors = new double[numAbstractNeurons];
		for (int i = 0; i < input.length - 1; i++) {
			for (int j = 0; j < numAbstractNeurons; j++) {
				hiddenLayer[j].gamma = refinedInputs[input.length - 1 - i][j];
				for (int k = 0; k < numAbstractNeurons; k++) {
					errors[k] += NeuralNetwork.LEARNING_RATE * hiddenLayer[j].delta() * hiddenLayer[j].weights[k];
				}
				hiddenLayer[j].adjustWeights(refinedInputs[input.length - 2 - i]);
			}
			for (int o = 0; o < getNumOutputs(); o++) {
				for (int h = 0; h < numAbstractNeurons; h++) {
					errors[h] += NeuralNetwork.LEARNING_RATE * outputNeurons[o].delta() * outputNeurons[o].weights[h] / input.length;
				}
				double oerr = outputNeurons[o].error;
				outputNeurons[o].adjustWeights(refinedInputs[input.length - 1]);
				outputNeurons[o].error = oerr;
			}
			for (int j = 0; j < numAbstractNeurons; j++) {
				hiddenLayer[j].error = errors[j];
				errors[j] = 0;
			}
		}
		if (getVerbosity() >= 4) {
			boolean anyWrong = false;
			for (int i = 0; i < answers.length; i++) {
				double gamma = outputNeurons[i].getAdjustedGamma(getGammaTruncation());
				if (answers[i] != gamma) {
					anyWrong = true;
					break;
				}
			}
			if (anyWrong) {
				for (int i = 0; i < answers.length; i++) {
					double gamma = outputNeurons[i].getAdjustedGamma(getGammaTruncation());
					if (answers[i] != gamma) {
						System.out.print("*****");
					}
					System.out.println(i + ":::" + answers[i] + "|" + gamma);
				}
			} else {
				System.out.println("Correct.");
			}
		}
	}

	@Override
	public int trainTillPerfection(double[][][] problems, double[][] solutions, boolean randomizeOrder) {
		boolean done = false;
		int iteration = 0;
		// XXX
		// FFNNDisplay visual = null;
		if (isDisplayingVisually()) {
			// visual = new FFNNDisplay(this, getDisplayWidth(),
			// getDisplayHeight(), getDisplayDelay());
		}
		while (!done) {
			if (isDisplayingVisually()) {
				long st = System.currentTimeMillis();
				// XXX
				// visual.repaint();
				DelayHelper.delayFor(getDisplayDelay() - (System.currentTimeMillis() - st));
			}
			done = true;
			if (getVerbosity() >= 2) {
				System.out.println("----------Iteration # " + iteration + "----------");
			}
			ArrayList<Integer> order = new ArrayList<Integer>();
			for (int i = 0; i < problems.length; i++) {
				order.add(i);
			}
			for (int k = 0; k < problems.length; k++) {
				int orderIndex = k;
				if (randomizeOrder) {
					orderIndex = (int) (NeuralUtils.random() * order.size());
				}
				int j = order.get(orderIndex);
				if (randomizeOrder) {
					order.remove(orderIndex);
				}
				if (getVerbosity() >= 3) {
					System.out.println("Problem " + j + " : ");
					for (int q = 0; q < problems[j].length; q++) {
						for (int i = 0; i < problems[j][q].length; i++) {
							System.out.print((int) problems[j][q][i]);
						}
						System.out.println();
					}
				}
				adjustForAnswer(problems[j], solutions[j]);
				for (int i = 0; i < getNumOutputs(); i++) {
					if (Math.abs(solutions[j][i] - outputNeurons[i].gamma) > getGammaTruncation()) {
						done = false;
					}
				}
			}
			iteration++;
			// XXX
			// visual.setVisible(true);

			if (done) {
				if (getVerbosity() >= 1) {
					System.out.println("Learned input in " + iteration + " iterations of " + problems.length + " problems (" + (iteration * problems.length) + " total iterations).");
					System.out.println("Learning rate was " + LEARNING_RATE);
					System.out.println("Truncation used was " + this.getGammaTruncation() + " (" + (1 - getGammaTruncation()) + "+ for 1, " + getGammaTruncation() + "- for 0)");
					if (randomizeOrder) {
						System.out.println("Problems were randomly ordered.");
					}
				}
				// XXX
				// if (isDisplayingVisually()) {
				// visual.continueRepainting();
				// }
				break;
			}
		}
		return iteration;
	}
}
