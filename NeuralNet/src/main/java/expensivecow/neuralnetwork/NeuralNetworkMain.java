package expensivecow.neuralnetwork;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.jfree.ui.RefineryUtilities;

public class NeuralNetworkMain {
	public static void main(String[] args) {
        NeuralNetApp chart = new NeuralNetApp("EECE 592 - Junbin ZHANG - 96889167");
        chart.pack();
        RefineryUtilities.centerFrameOnScreen(chart);
        chart.setVisible(true);
	}
}
