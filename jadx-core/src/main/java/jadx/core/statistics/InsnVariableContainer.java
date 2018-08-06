package jadx.core.statistics;

import java.util.ArrayList;
import java.util.List;

public class InsnVariableContainer {
	List<VariableUsage> readVariables;
	List<VariableUsage> writeVariables;
	List<MethodCall> methodCalls;
	
	public InsnVariableContainer() {
		readVariables = new ArrayList<VariableUsage>();
		writeVariables = new ArrayList<VariableUsage>();
		methodCalls = new ArrayList<MethodCall>();
	}
	
	public void cleanUpLiterals() {
		List<VariableUsage> newReadVariableList = new ArrayList<VariableUsage>();
		List<VariableUsage> newWriteVariableList = new ArrayList<VariableUsage>();
		for (VariableUsage usage : readVariables) {
			if (!usage.isLiteral()) {
				newReadVariableList.add(usage);
			}
		}
		
		for (VariableUsage usage : writeVariables) {
			if (!usage.isLiteral()) {
				newWriteVariableList.add(usage);
			}
		}

		readVariables = newReadVariableList;
		writeVariables = newWriteVariableList;
	}
	
	public void addReadUsage(VariableUsage varUsage) {
		readVariables.add(varUsage);
	}
	
	public void addWriteUsage(VariableUsage varUsage) {
		writeVariables.add(varUsage);
	}

	public List<VariableUsage> getReadVariables() {
		return readVariables;
	}
	
	public List<VariableUsage> getWriteVariables() {
		return writeVariables;
	}
	
	public void addMethodCall(MethodCall mCall) {
		methodCalls.add(mCall);
	}
	
	public List<MethodCall> getMethodCalls() {
		return methodCalls;
	}
	
	@Override
	public String toString() {
		String result = "";
		if (readVariables.size() > 0) {
			result += "Read: " + readVariables + "\n";
		}
		
		if (writeVariables.size() > 0) {
			result += "Write: " + writeVariables + "\n";
		}
		
		if (methodCalls.size() > 0) {
			result += "Method Calls: " + methodCalls + "\n";
		}
		
		return result;
	}
}
