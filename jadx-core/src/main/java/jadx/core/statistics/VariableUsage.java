package jadx.core.statistics;

import jadx.core.dex.instructions.args.InsnArg;

public class VariableUsage {
	InsnArg reg;
	String name;
	
	public VariableUsage(InsnArg reg, String name) {
		this.reg = reg;
		this.name = name;
	}
	
	public boolean isLiteral() {
		if (reg == null) {
			return false;
		}
		
		return reg.isLiteral();
	}
	
	@Override
	public String toString() {
		return name;
	}
}
