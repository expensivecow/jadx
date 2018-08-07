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
    
    public String getTitles() {
		String result = ""; 
		result += "Filename" + ",";
		result += "Method"+ ",";
		result += "Condition" + ",";
		result += "Num Statements" + ",";
		result += "Num Nested If Statements" + ",";
		result += "Nesting Level" + ",";
		result += "In Loop" + ",";
		result += "Num Comparisons" + ",";
		result += "Num If Containers" + ",";
		result += "Num Overall Variables Read In Condition" + ",";
		result += "Num Overall Variables Written In Condition" + ",";
		result += "Num Overall Literals Read In Condition" + ",";
		result += "Num Overall Literals Written In Condition" + ",";
		result += "Num Overall Variables Method Calls In Condition" + ",";
		result += "Num Unique Variables Read In Condition" + ",";
		result += "Num Unique Variables Written In Condition" + ",";
		result += "Num Unique Literals Read In Condition" + ",";
		result += "Num Unique Literals Written In Condition" + ",";
		result += "Num Unique Variables Method Calls In Condition" + ",";
		result += "Num Total Reads In Then: " + ",";
		result += "Num Unique Reads In Then (1 per statement)" + ",";
		result += "Num Total Writes In Then" + ",";
		result += "Num Read after Cond Read In Then" + ",";
		result += "Num Write after Cond Read In Then" + ",";
		result += "Num Read after Cond Write In Then" + ",";
		result += "Num Write after Cond Write In Then" + ",";
		result += "Num Total Method Calls In Then" + ",";
		result += "Num Unique Method Calls In Then" + ",";
		result += "Num Total Reads In Else" + ",";
		result += "Num Unique Reads In Else (1 per statement): " + ",";
		result += "Num Total Writes In Else" + ",";
		result += "Num Read after Cond Read In Else" + ",";
		result += "Num Write after Cond Read In Else" + ",";
		result += "Num Read after Cond Write In Else" + ",";
		result += "Num Write after Cond Write In Else" + ",";
		result += "Num Total Method Calls In Else" + ",";
		result += "Num Unique Method Calls In Else";
		return result;
    }
    
    public int getNumMethodsAnalyzed() {
    	return statistics.size();
    }
}
