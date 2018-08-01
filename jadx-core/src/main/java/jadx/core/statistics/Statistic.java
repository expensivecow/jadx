package jadx.core.statistics;

import java.util.ArrayList;
import java.util.List;

import jadx.core.codegen.MethodGen;
import jadx.core.codegen.RegionGen;
import jadx.core.dex.nodes.IContainer;
import jadx.core.dex.regions.conditions.Compare;
import jadx.core.dex.regions.conditions.IfRegion;

public class Statistic {
	private IContainer region;
	private int numStatements;
	private int numNestedIfStatements;
	private int nestingLevel;
	private List<Compare> comparisons;
	private boolean isElse;
	private boolean inLoop;
	
	public Statistic(IContainer region, boolean isElse) {
		this.region = region;
		this.isElse = isElse;
	}
	
	public void setNumStatements(int numStatements) {
		this.numStatements = numStatements;
	}
	
	public void setInLoop(boolean inLoop) {
		this.inLoop = inLoop;
	}
	
	public void setNumNestedIfStatements(int numNestedIfStatements) {
		this.numNestedIfStatements = numNestedIfStatements;
	}
	
	public void setNestingLevel(int nestingLevel) {
		this.nestingLevel = nestingLevel;
	}
	
	public void setComparisonList(List<Compare> comparisons) {
		this.comparisons = comparisons;
	}
}