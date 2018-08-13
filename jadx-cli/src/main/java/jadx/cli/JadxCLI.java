package jadx.cli;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jadx.api.JadxDecompiler;
import jadx.core.statistics.FeatureAnalysis;
import jadx.core.statistics.IfStatistic;
import jadx.core.statistics.IfStatistics;
import jadx.core.statistics.Statistic;
import jadx.core.utils.exceptions.JadxArgsValidateException;

public class JadxCLI {
	private static final Logger LOG = LoggerFactory.getLogger(JadxCLI.class);
	private static int numApplications = 0;
	private static final String delimiter = "`";
	
	public static void main(String[] args) {
		try {
			List<File> apkDirectories = new ArrayList<File>();
			List<File> outputDirectories = new ArrayList<File>();
			
			Map<File, File> output = new HashMap<File, File>();
			
			output.put(
					new File("/home/mike/Dev/School/Research/LogicBombs/benign"), 
					new File("/home/mike/Dev/School/Research/LogicBombs/logs/benignStats.csv")
			);
			output.put(
					new File("/home/mike/Dev/School/Research/LogicBombs/benign"), 
					new File("/home/mike/Dev/School/Research/LogicBombs/logs/benignStats.csv")
			);
			
			Iterator it = output.entrySet().iterator();
		    while (it.hasNext()) {
		        Map.Entry pair = (Map.Entry) it.next();

		        File outputFile = (File) pair.getValue();
				PrintWriter printer = new PrintWriter(outputFile);
				printer.write(FeatureAnalysis.getInstance().getTitles(delimiter));
				
				File apkDirectory = (File) pair.getKey();

				exploreDirectoryRecursively(apkDirectory, printer, false);
				printer.close();
		        it.remove(); // avoids a ConcurrentModificationException
		    }
			
		} catch (Exception e) {
			LOG.error("jadx error: {}", e.getMessage(), e);
			System.exit(1);
		}
	}
	
	private static void exploreDirectoryRecursively(File dir, PrintWriter printer, boolean writeOut) {
		if (dir.exists()) {
		    for (final File file : dir.listFiles()) {
		        if (file.isDirectory()) {
		        	exploreDirectoryRecursively(file, printer, writeOut);
		        } else {
		        	String fileName = file.getName();
		        	
		        	if (FilenameUtils.getExtension(fileName).equals("apk") && numApplications < 50) {
		        		System.out.print("Starting analysis on " + file.getAbsolutePath() + "...");
		    			String[] arguments = new String[4];
		    			arguments[0] = file.getAbsolutePath();
		    			arguments[1] = "--no-replace-consts";
		    			arguments[2] = "--deobf";
		    			arguments[3] = "--deobf-use-sourcename";
		    			JadxCLIArgs jadxArgs = new JadxCLIArgs();
		    			if (jadxArgs.processArgs(arguments)) {
		    				processAndSave(jadxArgs, fileName, printer, writeOut);
		    			}
		        		System.out.print(" done.\n");
		        		numApplications++;
		        	}
		        }
		    }
		}
		else {
			
		}
	}

	static void processAndSave(JadxCLIArgs inputArgs, String fileName, PrintWriter printer, boolean isPrint) {
		JadxDecompiler jadx = new JadxDecompiler(inputArgs.toJadxArgs());
		try {
			jadx.load();
		} catch (JadxArgsValidateException e) {
			LOG.error("Incorrect arguments: {}", e.getMessage());
			System.exit(1);
		}
		jadx.save();
		
		if (jadx.getErrorsCount() != 0) {
			jadx.printErrorsReport();
			LOG.error("finished with errors");
		} else {
			LOG.info("done");
		}

		if (isPrint) {
			FeatureAnalysis.getInstance();
			for (IfStatistics statistics : FeatureAnalysis.getStatistics().values()) {
				for (IfStatistic stats : statistics.getIfStatisticForMethod().values()) {
					for (Statistic s : stats.getStatistics()) {
						printer.write(fileName + delimiter + s.getStats(delimiter));
					}
				}
			}
			
			FeatureAnalysis.getInstance().clear();	
		}
	}
}
