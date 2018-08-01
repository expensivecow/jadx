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
    
    public int getNumMethodsAnalyzed() {
    	return statistics.size();
    }
}
