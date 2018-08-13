package expensivecow.neuralnetwork;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.SortedSet;
import java.util.TreeSet;

public class NeuralNetTest {
	private static int numFeatures = 36;
	private static File inputMalwareSamples;
	private static File inputBenignSamples;
	private static int numBenignSamples;
	private static int numMalwareSamples;
	
    public static double[] xor(double momentum, boolean useBinary, int epoch) {
        double[][] X;
        double[][] argValues;
        if (useBinary) {
            X = new double[][]{
                    {0.0, 0.0},
                    {1.0, 0.0},
                    {0.0, 1.0},
                    {1.0, 1.0}
            };
            argValues = new double[][]{{0.0}, {1.0}, {1.0}, {0.0}};
        } else {
            X = new double[][]{
                    {-1.0, -1.0},
                    {1.0, -1.0},
                    {-1.0, 1.0},
                    {1.0, 1.0}
            };
            argValues = new double[][]{{-1.0}, {1.0}, {1.0}, {-1.0}};
        }

        NeuralNet neuralNet = new NeuralNet(new int[]{2, 4, 1}, 0.2, momentum, new double[]{-0.5, 0.5}, useBinary, false, 0, 0, false);

        if (epoch > 0) {
            return neuralNet.trainForEpochs(X, argValues, epoch);
        } else {
            return neuralNet.trainForStable(X, argValues);
        }
    }
    
    public static double[] logicBombRecognition(double momentum, boolean useBinary, int epoch) {
    	double[][] input;
    	double[][] groundTruth;
    	
    	List<double[]> samples = getSamples();
    	groundTruth = new double[samples.size()][];
    	input = new double[samples.size()][];
    	
    	int index = 0;
    	for (int j = 0; j < samples.size(); j++) {
    		double[] inRow = new double[numFeatures];
    		for (int i = 0; i < numFeatures; i++) {
    			inRow[i] = samples.get(j)[i];
    		}
    		
    		input[j] = inRow;
    		
    		double[] outRow = new double[1];
    		outRow[0] = samples.get(j)[numFeatures];
    		groundTruth[j] = outRow;
    		index++;
    	}


        NeuralNet neuralNet = new NeuralNet(new int[]{36, 50, 50, 1}, 0.0001, momentum, new double[]{-0.5, 0.5}, useBinary, false, 0, 0, false);
    	return neuralNet.trainForEpochs(input, groundTruth, epoch);
    }
    

	private static List<double[]> getSamples() {
		Random rand = new Random();
		numMalwareSamples = -1;
		numBenignSamples = -1;
		
        try {
            inputMalwareSamples = new File("/home/mike/Dev/School/Research/LogicBombs/logs/MalwareStats.csv");
            inputBenignSamples = new File("/home/mike/Dev/School/Research/LogicBombs/logs/BenignStats.csv");
            BufferedReader malwareBR = new BufferedReader(new FileReader(inputMalwareSamples));
            BufferedReader benignBR = new BufferedReader(new FileReader(inputBenignSamples));

            Integer[] minValues = new Integer[numFeatures];
            Integer[] maxValues = new Integer[numFeatures];
            
            // set min/max values for columns
            for (int i = 0; i < numFeatures; i++) {
            	minValues[i] = Integer.MAX_VALUE;
            	maxValues[i] = Integer.MIN_VALUE;
            }
            
            String readLine = "";
            //System.out.print("Start reading malware samples... ");
            while ((readLine = malwareBR.readLine()) != null) {
                String[] lineVariables = readLine.split("`");

                // Ignore titles
                if (numMalwareSamples >= 0) {
                	for (int i = 0; i < numFeatures; i++) {                		
                		Integer currentValue = Integer.parseInt(lineVariables[i+3]);
                		if (currentValue > maxValues[i]) {
                			maxValues[i] = currentValue;
                		}
                		
                		if (currentValue < minValues[i]) {
                			minValues[i] = currentValue;
                		}
                	}
                }
                
                numMalwareSamples++;
            }
            //System.out.print("done." + "\n");
            
            readLine = "";
            //System.out.print("Start reading benign samples... ");
            while ((readLine = benignBR.readLine()) != null) {
                String[] lineVariables = readLine.split("`");
                
                // Ignore titles
                if (numBenignSamples >= 0) {
                	for (int i = 3; i < numFeatures; i++) {
                		Integer currentValue = Integer.parseInt(lineVariables[i+3]);
                		if (currentValue > maxValues[i]) {
                			maxValues[i] = currentValue;
                		}
                		
                		if (currentValue < minValues[i]) {
                			minValues[i] = currentValue;
                		}
                	}
                }
            	
            	numBenignSamples++;
            }
            //System.out.print("done." + "\n");

            SortedSet<Integer> benignRandomNumbers = new TreeSet<Integer>(); 

            while (benignRandomNumbers.size() != numMalwareSamples) {
            	benignRandomNumbers.add((rand.nextInt(numBenignSamples) + 1));
            }
                        
            List<Integer> min = Arrays.asList(minValues);
            List<Integer> max = Arrays.asList(maxValues);

            //System.out.println("Min Values: " + min);
            //System.out.println("Max Values: " + max);
            
            
            int index = -1;
            readLine = "";
            benignBR = new BufferedReader(new FileReader(inputBenignSamples));
            
            List<double[]> samples = new ArrayList<double[]>();
            
            //System.out.print("Get random benign samples...");
            while ((readLine = benignBR.readLine()) != null) {
                String[] lineVariables = readLine.split("`");
                
                if (index >= 0) {
                    if (benignRandomNumbers.contains(index)) {
                		Integer[] features = new Integer[numFeatures];
                    	for (int i = 0; i < numFeatures; i++) {
                    		features[i] = Integer.parseInt(lineVariables[i+3]);
                    	}
                    	
                    	samples.add(translateSample(features, minValues, maxValues, false));
                    }
                }
            	
            	index++;
            }
            //System.out.print("done." + "\n");
            
            readLine = "";
            malwareBR = new BufferedReader(new FileReader(inputMalwareSamples));
            
            index = -1;
            //System.out.print("Get random malware samples...");
            while ((readLine = malwareBR.readLine()) != null) {
                String[] lineVariables = readLine.split("`");

                if (index >= 0) {
	        		Integer[] features = new Integer[numFeatures+1];
	            	for (int i = 0; i < numFeatures; i++) {
	            		features[i] = Integer.parseInt(lineVariables[i+3]);
	            	}

                	samples.add(translateSample(features, minValues, maxValues, true));
                }
            	
            	index++;
            }
            //System.out.print("done." + "\n");

            //System.out.print("Shuffling samples...");
            Collections.shuffle(samples);
            //System.out.print("done." + "\n");
            
            return samples;
            
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return null;
	}
	
	private static double[] translateSample(Integer[] beforeNormalization, Integer[] minValues, Integer[] maxValues, boolean isMalware) {
		double[] afterNormalization = new double[numFeatures+1];
		int min = -1;
		int max = 1;
		
		for (int i = 0; i < numFeatures; i++) {
			double minVal = (double) minValues[i];
			double maxVal = (double) maxValues[i];
			double val = (double) beforeNormalization[i];
			
			if (maxVal == minVal) {
				afterNormalization[i] = 0.0;
			}
			else {
				afterNormalization[i] = min + max*((val - minVal)/maxVal);	
			}
		}
		
		if (isMalware) {
			afterNormalization[numFeatures] = 1.0;
		}
		else {
			afterNormalization[numFeatures] = 0.0;
		}
		
		return afterNormalization;
	}
}
