package jadx.core.statistics;

import jadx.core.codegen.ArgTypes;
import jadx.core.dex.instructions.args.InsnArg;

public class VariableUsage {
	String name;
	ArgTypes type;
	
	public VariableUsage(ArgTypes type, String name) {
		this.type = type;
		this.name = name;
	}
	
	public boolean isLiteral() {
		return (type == ArgTypes.LITERAL);
	}
	
	public boolean isVariable() {
		return (type == ArgTypes.VARIABLE);
	}
	
	@Override
	public String toString() {
		return name;
	}
}
