package expensivecow.neuralnetwork;

import java.io.*;
import java.util.ArrayList;
import java.util.Random;

import sarb.NeuralNetInterface;

public class NeuralNet implements NeuralNetInterface {

    static final int STABLEERROREPOCHES = 1;
    static final double ERRORTHRESHOLD = 0.05;

    double argLearningRate;
    double argMomentumTerm;
    double[] randomValueRange;
    boolean useBinary;

    boolean useCustomSigmoid;
    double argA;
    double argB;

    boolean useZeroWeights;

    double[][][] weights;
    double[][][] lastWeights;

    public NeuralNet (
            int[] argStructure, // Indicate the number of neurons in different layers. Specifically, argStructure[0] is for the number of inputs,
                                // and argStructure[argStructure.length - 1] is for the outputs.
            double argLearningRate,
            double argMomentumTerm,
            double[] randomValueRange, // The length of this array must be two. [0] for the minimum value and [1] for the maximum value.
            boolean useBinary,

            boolean useCustomSigmoid,
            double argA,
            double argB,

            boolean useZeroWeights) {

        this.argLearningRate = argLearningRate;
        this.argMomentumTerm = argMomentumTerm;

        if (randomValueRange.length == 2) {
            this.randomValueRange = randomValueRange;
        } else {
            // Force useZeroWeights to true is range for random weights is invalid
            System.out.println("Range for initializing random weights is invalid, set the weights to zero.");
            useZeroWeights = true;
        }

        this.useBinary = useBinary;

        this.useCustomSigmoid = useCustomSigmoid;
        this.argA = argA;
        this.argB = argB;

        this.useZeroWeights = useZeroWeights;

        // Initialize the dimensions of weights and lastWeights (they have the identical dimensions)
        // Firstly set the number of layer, exclude the inputs
        weights = new double[argStructure.length - 1][][];
        lastWeights = new double[argStructure.length - 1][][];
        // Secondly, set the neuron number for every layer
        for (int layer = 0; layer < argStructure.length - 1; layer++) {
            weights[layer] = new double[argStructure[layer + 1]][];
            lastWeights[layer] = new double[argStructure[layer + 1]][];
        }
        // Finally, every neuron should connected to the number of neurons from its previous layer plus the bias
        for (int layer = 0; layer < argStructure.length - 1; layer++) {
            for (int node = 0; node < argStructure[layer + 1]; node++) {
                weights[layer][node] = new double[argStructure[layer] + 1];
                lastWeights[layer][node] = new double[argStructure[layer] + 1];
            }
        }

        // Initialize the weights
        if (!this.useZeroWeights) {
            initializeWeights();
        } else {
            zeroWeights();
        }
    }

    @Override
    public double sigmoid(double x) {
        if (this.useBinary) {
            return 1.0 / (1.0 + Math.exp(-x));
        } else {
            return 2.0 / (1.0 + Math.exp(-x)) - 1.0 ;
        }
    }

    @Override
    public double customSigmoid(double x) {
        return (argB - argA) / (1 + Math.exp(-x)) - (-argA);
    }

    @Override
    public void initializeWeights() {
        Random rand = new Random();

        for (int layer = 0; layer < weights.length; layer++) {
            for (int node = 0; node < weights[layer].length; node++) {
                for (int w = 0; w < weights[layer][node].length; w++) {
                    weights[layer][node][w] = rand.nextDouble() * (randomValueRange[1] - randomValueRange[0]) + randomValueRange[0];
                    lastWeights[layer][node][w] = weights[layer][node][w];
                }
            }
        }
    }

    @Override
    public void zeroWeights() {
        for (int layer = 0; layer < weights.length; layer++) {
            for (int node = 0; node < weights[layer].length; node++) {
                for (int w = 0; w < weights[layer][node].length; w++) {
                    weights[layer][node][w] = 0.0;
                    lastWeights[layer][node][w] = 0.0;
                }
            }
        }
    }

    @Override
    public double[] outputFor(double[] X) {
        double[][] output = forwardPropagation(X);
        return output[output.length - 1];
    }

    public double[][] forwardPropagation(double[] X) {
        double[][] output;
        output = new double[weights.length][];

        for (int layer = 0; layer < weights.length; layer++) {
            output[layer] = new double[weights[layer].length];
            for (int node = 0; node < weights[layer].length; node++) {
                double sum = 0.0;
                for (int input = 0; input < (layer == 0? X.length: output[layer - 1].length); input++) {
                    sum += (layer == 0? X[input]: output[layer - 1][input]) * weights[layer][node][input];
                }
                sum += bias * weights[layer][node][weights[layer][node].length - 1];

                if (!useCustomSigmoid) {
                    output[layer][node] = sigmoid(sum);
                } else {
                    output[layer][node] = customSigmoid(sum);
                }
            }
        }

        return output;
    }

    public double getFDerivative(double output) {
        if (useBinary) {
            return output * (1.0 - output);
        } else {
            return 0.5 * (1.0 - Math.pow(output, 2));
        }
    }

    public void backwardErrorPropagation(double[] X, double[][] output, double[] argValue, boolean layerByLayer) {
        // Create temporary space for newWeights
        double[][][] newWeights = new double[weights.length][][];
        for (int layer = 0; layer < weights.length; layer++) {
            newWeights[layer] = new double[weights[layer].length][];
            for (int node = 0; node < weights[layer].length; node++) {
                newWeights[layer][node] = new double[weights[layer][node].length];
            }
        }

        double[][] delta = new double[weights.length][];

        if (layerByLayer) {
            for (int layer = weights.length - 1; layer >= 0; layer--) {
                // First calculate the deltas
                culculateDelta(delta, layer, argValue, output, newWeights, layerByLayer);
                // Then update the new weights
                culculateNewWeights(layer, X, output, newWeights, delta);
            }
        } else {
            // Calculate all deltas
            for (int layer = weights.length - 1; layer >= 0; layer--) {
                culculateDelta(delta, layer, argValue, output, newWeights, layerByLayer);
            }
            // Calculate new weights
            for (int layer = weights.length - 1; layer >= 0; layer--) {
                culculateNewWeights(layer, X, output, newWeights, delta);
            }
        }

        // Update stored weights with the new weights
        for (int layer = 0; layer < weights.length; layer++) {
            for (int node = 0; node < weights[layer].length; node++) {
                for (int w = 0; w < weights[layer][node].length; w++) {
                    lastWeights[layer][node][w] = weights[layer][node][w];
                    weights[layer][node][w] = newWeights[layer][node][w];
                }
            }
        }
    }

    public void culculateDelta(double[][] delta, int layer, double[] argValue, double[][] output, double[][][] newWeights, boolean layerByLayer) {
        delta[layer] = new double[weights[layer].length];
        for (int node = 0; node < weights[layer].length; node++) {
            if (layer == weights.length - 1) {
                delta[layer][node] = (argValue[node] - output[output.length - 1][node]) * getFDerivative(output[output.length - 1][node]);
            } else {
                double sum = 0.0;
                for (int nextLayerNode = 0; nextLayerNode < weights[layer + 1].length; nextLayerNode++) {
                    // Use newWeights if update weights layer by layer
                    sum += delta[layer + 1][nextLayerNode] * (layerByLayer? newWeights[layer + 1][nextLayerNode][node]: weights[layer + 1][nextLayerNode][node]);
                }
                delta[layer][node] = sum * getFDerivative(output[layer][node]);
            }
        }
    }

    public void culculateNewWeights(int layer, double[] X, double[][] output, double[][][] newWeights, double[][] delta) {
        for (int node = 0; node < weights[layer].length; node++) {
            for (int input = 0; input < weights[layer][node].length; input++) {
                double x;
                if (input == weights[layer][node].length - 1) {
                    x = bias;
                } else if (layer == 0) {
                    x = X[input];
                } else {
                    x = output[layer - 1][input];
                }
                newWeights[layer][node][input] = weights[layer][node][input] +
                        argMomentumTerm * (weights[layer][node][input] - lastWeights[layer][node][input]) +
                        argLearningRate * delta[layer][node] * x;
            }
        }
    }

    public double calError(double[] output, double[] argValue) {
        double sum = 0.0;
        for (int index = 0; index < argValue.length; index++) {
            sum += 1.0 / 2.0 * Math.pow(output[index] - argValue[index], 2);
        }
        return sum;
    }

    public double calTotalError(double[][] X, double[][] argValue) {
        double sum = 0.0;
        for (int i = 0; i < X.length; i++) {
            sum += calError(outputFor(X[i]), argValue[i]);
        }
        return sum;
    }

    @Override
    public double train(double[] X, double[] argValue) {
        double[][] output = forwardPropagation(X);
        backwardErrorPropagation(X, output, argValue, true);
        return calError(output[output.length - 1], argValue);
    }

    public double[] trainForEpochs(double[][] X, double[][] argValues, int epoch) {
        double[] ret = new double[epoch];
        for (int e = 0; e < epoch; e++) {
            for (int i = 0; i < X.length; i++) {
                train(X[i], argValues[i]);
            }
            ret[e] = calTotalError(X, argValues);
        }

        return ret;
    }

    public double[] trainForStable(double[][] X, double[][] argValues) {
        ArrayList<Double> retArrayList = new ArrayList<Double>();

        int totalEpochs = 0;
        int stableEpochs = 0;
        while (stableEpochs < STABLEERROREPOCHES) {
            double error = 0.0;
            for (int i = 0; i < X.length; i++) {
                train(X[i], argValues[i]);
            }
            error = calTotalError(X, argValues);
            retArrayList.add(error);
            totalEpochs++;

            if (error <= ERRORTHRESHOLD) {
                stableEpochs++;
            } else if (error > ERRORTHRESHOLD && stableEpochs > 0) {
                stableEpochs = 0;
            }
        }

        //System.out.println("Number of epochs before stable: " + totalEpochs);

        // The last element of the return array represents the number of epochs
        retArrayList.add((double)totalEpochs);

        double[] ret = new double[retArrayList.size()];
        for (int i = 0; i < retArrayList.size(); i++) {
            ret[i] = (double)retArrayList.get(i);
        }
        return ret;
    }

    @Override
    public void save(File argFile) {
        try {
            PrintWriter writer = new PrintWriter(argFile);
            for (int layer = 0; layer < weights.length; layer++) {
                for (int node = 0; node < weights[layer].length; node++) {
                    for (int w = 0; w < weights[layer][node].length; w++) {
                        writer.println(weights[layer][node][w]);
                    }
                }
            }
            writer.close();
        } catch (FileNotFoundException e) {
            System.out.println(argFile + " not found.\n");
        }
    }

    @Override
    public void load(String argFileName) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(argFileName));
        for (int layer = 0; layer < weights.length; layer++) {
            for (int node = 0; node < weights[layer].length; node++) {
                for (int w = 0; w < weights[layer][node].length; w++) {
                    weights[layer][node][w] = Double.parseDouble(reader.readLine());
                }
            }
        }
        reader.close();
    }
}
