package jadx.core.statistics;

import java.util.ArrayList;
import java.util.List;

public class InsnVariableContainer {
	private List<VariableUsage> readVariablesAndLiterals;
	private List<VariableUsage> writeVariablesAndLiterals;
	private List<MethodCall> methodCalls;
	private List<VariableUsage> readLiterals;
	private List<VariableUsage> writeLiterals;
	private List<VariableUsage> readVariables;
	private List<VariableUsage> writeVariables;
	
	public InsnVariableContainer() {
		readVariablesAndLiterals = new ArrayList<VariableUsage>();
		writeVariablesAndLiterals = new ArrayList<VariableUsage>();
		methodCalls = new ArrayList<MethodCall>();
		readLiterals = new ArrayList<VariableUsage>();
		writeLiterals = new ArrayList<VariableUsage>();
		readVariables = new ArrayList<VariableUsage>();
		writeVariables = new ArrayList<VariableUsage>();
	}
	
	public void separateLiteralsAndVariables() {
		readLiterals.clear();
		writeLiterals.clear();
		readVariables.clear();
		writeVariables.clear();
		for (VariableUsage usage : readVariablesAndLiterals) {
			if (!usage.isLiteral()) {
				readVariables.add(usage);
			}
			else {
				readLiterals.add(usage);
			}
		}
		
		for (VariableUsage usage : writeVariablesAndLiterals) {
			if (!usage.isLiteral()) {
				writeVariables.add(usage);
			}
			else {
				writeLiterals.add(usage);
			}
		}
	}
	
	public void addReadUsage(VariableUsage varUsage) {
		readVariablesAndLiterals.add(varUsage);
	}
	
	public void addWriteUsage(VariableUsage varUsage) {
		writeVariablesAndLiterals.add(varUsage);
	}

	public List<VariableUsage> getReadUsages() {
		return readVariablesAndLiterals;
	}
	
	public List<VariableUsage> getWriteUsages() {
		return writeVariablesAndLiterals;
	}

	public List<VariableUsage> getReadVariables() {
		return readVariables;
	}
	
	public List<VariableUsage> getWriteVariables() {
		return writeVariables;
	}
	
	public List<VariableUsage> getReadLiterals() {
		return readLiterals;
	}
	
	public List<VariableUsage> getWriteLiterals() {
		return writeLiterals;
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
			result += "Read Variables: " + getReadVariables() + "\n";
		}

		if (readLiterals.size() > 0) {
			result += "Read Literals: " + getReadLiterals() + "\n";
		}
		
		if (writeVariables.size() > 0) {
			result += "Write Variables: " + getWriteVariables() + "\n";
		}
		
		if (writeLiterals.size() > 0) {
			result += "Write Literals: " + getWriteLiterals() + "\n";
		}
		
		if (methodCalls.size() > 0) {
			result += "Method Calls: " + getMethodCalls() + "\n";
		}
		
		return result;
	}
}
