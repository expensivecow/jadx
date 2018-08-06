package jadx.core.statistics;

import java.util.ArrayList;
import java.util.List;

import jadx.core.dex.info.ClassInfo;
import jadx.core.dex.info.MethodInfo;
import jadx.core.dex.instructions.args.ArgType;

public class MethodCall {
	private MethodInfo mth;
	private ClassInfo cls;
	private String fullMethodName;
	
	List<ArgType> arguments;
	
	public MethodCall(MethodInfo mth, ClassInfo cls) {
		this.mth = mth;
		this.cls = cls;
		this.fullMethodName = mth.getFullName();
		
		arguments = mth.getArgumentsTypes();
	}
	
	public String getMethodName() {
		return fullMethodName;
	}
	
	public int getReturnArgumentCount() {
		return arguments.size();
	}
	
	public MethodInfo getMethodInfo() {
		return mth;
	}
	
	public ClassInfo getClassInfo() {
		return cls;
	}
	
	@Override
	public String toString() {
		String argumentString = arguments.toString();
		argumentString = argumentString.replace("[", "");
		argumentString = argumentString.replace("]", "");
		return fullMethodName + "(" + argumentString + ")";
	}
}
