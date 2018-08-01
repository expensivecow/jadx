package jadx.core.statistics;

import java.util.List;

import jadx.core.dex.nodes.IBlock;
import jadx.core.dex.nodes.IContainer;
import jadx.core.dex.regions.conditions.IfCondition;
import jadx.core.dex.regions.conditions.IfRegion;

public class IfRegionInfo {
	private IfCondition condition;
	private boolean isElse;
	private IContainer cont;
	
	public IfRegionInfo(IfCondition condition, IContainer cont, boolean isElse) {
		this.condition = condition;
		this.cont = cont;
		this.isElse = isElse;
	}
	
	public boolean isElse() {
		return isElse;
	}
	
	public IContainer getRegion() {
		return cont;
	}
	
	public IfCondition getCondition() {
		return condition;
	}
}
