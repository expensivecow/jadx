package expensivecow.neuralnetwork;

import java.util.Arrays;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class NeuralNetApp extends ApplicationFrame {
    static final boolean USEBINARY = false;
    static final double MOMENTUM = 0;
    static final int NUMOFTESTS = 10;
    static final int SHOWNTESTS = 10;

    public NeuralNetApp(final String title) {
        super(title);

        final XYDataset dataset = createDataset();
        final JFreeChart chart = createChart(dataset);
        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(800, 400));
        setContentPane(chartPanel);
    }

    private XYDataset createDataset() {
        final XYSeriesCollection dataset = new XYSeriesCollection();

        int sum = 0;
        int max = 0;
        int min = 0;

        for (int i = 0; i < NUMOFTESTS; i++) {
            double[] ret = NeuralNetTest.logicBombRecognition(MOMENTUM, USEBINARY, 100);

            System.out.println(Arrays.toString(ret));
            if (i < SHOWNTESTS) {
                final XYSeries series = new XYSeries("Test " + i);
                for (int j = 0; j < ret.length - 1; j++) {
                    series.add(j, ret[j]);
                }
                dataset.addSeries(series);
            }

            // Update sum
            sum += ret[ret.length - 1];

            // Update max
            if (i == 0) {
                max = (int)ret[ret.length - 1];
            } else if (ret[ret.length - 1] > max) {
                max = (int)ret[ret.length - 1];
            }

            // Update min
            if (i == 0) {
                min = (int)ret[ret.length - 1];
            } else if (ret[ret.length - 1] < min) {
                min = (int)ret[ret.length - 1];
            }
        }

        System.out.println("Average number of epochs before stable: " + (sum / NUMOFTESTS));
        System.out.println("Range of epochs before stable: [" + min + ", " + max + "]");

        return dataset;
    }

    private JFreeChart createChart(final XYDataset dataset) {
        final JFreeChart chart = ChartFactory.createXYLineChart(
                "Total squared error for " + (USEBINARY? "binary": "bipolar") +" representation of XOR problem",
                "Number of epochs",
                "Error",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        return chart;
    }

    public static void main(String[] args) {
        NeuralNetApp chart = new NeuralNetApp("EECE 592 - Junbin ZHANG - 96889167");
        chart.pack();
        RefineryUtilities.centerFrameOnScreen(chart);
        chart.setVisible(true);
    }
}
