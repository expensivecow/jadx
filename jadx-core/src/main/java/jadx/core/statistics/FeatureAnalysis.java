package jadx.core.statistics;

import java.util.HashMap;

import jadx.core.codegen.MethodGen;
import jadx.core.codegen.RegionGen;
import jadx.core.dex.nodes.MethodNode;
import jadx.core.dex.regions.conditions.IfRegion;

public class FeatureAnalysis {
    // static variable single_instance of type Singleton
    private static FeatureAnalysis instance = null;
    private static HashMap<MethodGen, IfStatistics> statistics; 
    
    private FeatureAnalysis() {
    	statistics = new HashMap<MethodGen, IfStatistics>();
    }
    
    public static FeatureAnalysis getInstance() {
    	if (instance == null) {
    		instance = new FeatureAnalysis();
    	}
    	
    	return instance;
    }
    
    public static HashMap<MethodGen, IfStatistics> getStatistics() {
    	return statistics;
    }
    
    public void addStatistic(MethodGen mth, IfRegion region, RegionGen rGen) {
    	IfStatistics found = statistics.get(mth);
    	
    	if (found == null) {
    		IfStatistics ifStatistics = new IfStatistics(mth, rGen);
    		ifStatistics.addStatistic(region);
    		statistics.put(mth, ifStatistics);
    	}
    	else {
    		found.addStatistic(region);
    	}
    }
    
    public void clear() {
    	statistics.clear();
    }
   
    public String getTitles(String delimiter) {
		String result = ""; 
		result += "Filename" + delimiter;
		result += "Method"+ delimiter;
		result += "Condition" + delimiter;
		result += "Num Statements" + delimiter;
		result += "Num Statements In Then" + delimiter;
		result += "Num Statements In Else" + delimiter;
		result += "Num Nested If Statements" + delimiter;
		result += "Nesting Level" + delimiter;
		result += "In Loop" + delimiter;
		result += "Num Comparisons" + delimiter;
		result += "Num If Containers" + delimiter;
		result += "Variables Read In Condition" + delimiter;
		result += "Variables Written In Condition" + delimiter;
		result += "Literals Read In Condition" + delimiter;
		result += "Literals Written In Condition" + delimiter;
		result += "Variables Method Calls In Condition" + delimiter;
		result += "Unique Variables Read In Condition" + delimiter;
		result += "Unique Variables Written In Condition" + delimiter;
		result += "Unique Literals Read In Condition" + delimiter;
		result += "Unique Literals Written In Condition" + delimiter;
		result += "Unique Variables Method Calls In Condition" + delimiter;
		result += "Total Reads In Then: " + delimiter;
		result += "Statements Containing a Read In Then" + delimiter;
		result += "Total Writes In Then" + delimiter;
		result += "Read after Cond Read In Then" + delimiter;
		result += "Write after Cond Read In Then" + delimiter;
		result += "Read after Cond Write In Then" + delimiter;
		result += "Write after Cond Write In Then" + delimiter;
		result += "Total Method Calls In Then" + delimiter;
		result += "Statements Containing a Method Call In Then" + delimiter;
		result += "Total Reads In Else" + delimiter;
		result += "Statements Containing a Read In Else" + delimiter;
		result += "Total Writes In Else" + delimiter;
		result += "Read after Cond Read In Else" + delimiter;
		result += "Write after Cond Read In Else" + delimiter;
		result += "Read after Cond Write In Else" + delimiter;
		result += "Write after Cond Write In Else" + delimiter;
		result += "Total Method Calls In Else" + delimiter;
		result += "Statements Containing a Method Call In Else" + "\n";
		return result;
    }
    
    public int getNumMethodsAnalyzed() {
    	return statistics.size();
    }
}
