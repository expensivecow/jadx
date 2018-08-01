package jadx.core.statistics;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.LinkedHashMap;

import jadx.core.codegen.MethodGen;
import jadx.core.codegen.RegionGen;
import jadx.core.dex.regions.conditions.IfRegion;

/**
 * List of statistics for a given method
 * @author mike
 */
public class IfStatistics {
	HashMap<IfRegion, IfStatistic> statistics;
	MethodGen mth;
	RegionGen rGen;
	
	public IfStatistics (MethodGen mth, RegionGen rGen) {
		this.mth = mth;
		this.rGen = rGen;
		statistics = new HashMap<IfRegion, IfStatistic>();
	}
	
	public void addStatistic (IfRegion region) {
		IfStatistic found = statistics.get(region);
		
		if (found == null) {
			IfStatistic statistic = new IfStatistic(region, mth, rGen);
			statistics.put(region, statistic);
		}
	}
}
