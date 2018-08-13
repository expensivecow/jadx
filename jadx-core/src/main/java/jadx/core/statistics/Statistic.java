package jadx.core.statistics;

import java.util.List;
import java.util.Set;

import jadx.core.codegen.CodeWriter;
import jadx.core.codegen.MethodGen;
import jadx.core.dex.nodes.IContainer;
import jadx.core.dex.regions.conditions.Compare;
import jadx.core.dex.regions.conditions.IfCondition;

public class Statistic {
	private MethodGen mth;
	private IContainer thenRegion;
	private IContainer elseRegion;
	private IfCondition condition;
	private String conditionString;
	private int numStatements;
	private int numNestedIfStatements;
	private int nestingLevel;
	private int numIfContainers;
	private int numStatementsInThen;
	private int numStatementsInElse;
	
	private int numTotalReadsInThen;
	private int numTotalUniqueReadsInThen;
	private int numTotalWritesInThen;
	private int numTotalReadReadInThen;
	private int numTotalReadWriteInThen;
	private int numTotalWriteReadInThen;
	private int numTotalWriteWriteInThen;
	private int numTotalMethodCallsInThen;
	private int numUniqueMethodCallsInThen;
	
	private int numTotalReadsInElse;
	private int numTotalUniqueReadsInElse;
	private int numTotalWritesInElse;
	private int numTotalReadReadInElse;
	private int numTotalReadWriteInElse;
	private int numTotalWriteReadInElse;
	private int numTotalWriteWriteInElse;
	private int numTotalMethodCallsInElse;
	private int numUniqueMethodCallsInElse;

	private List<VariableUsage> readVarInCond;
	private List<VariableUsage> writeVarInCond;
	private List<VariableUsage> readLitInCond;
	private List<VariableUsage> writeLitInCond;
	private List<MethodCall> methodCallsInCond;

	private Set<String> uniqueReadVarInCond;
	private Set<String> uniqueWriteVarInCond;
	private Set<String> uniqueReadLitInCond;
	private Set<String> uniqueWriteLitInCond;
	private Set<String> uniqueMethodCallsInCond;
	
	private List<Compare> comparisons;
	private boolean inLoop;
	
	public Statistic(IContainer thenRegion, IContainer elseRegion, IfCondition condition, String conditionString, MethodGen mth) {
		this.thenRegion = thenRegion;
		this.elseRegion = elseRegion;
		this.condition = condition;
		this.conditionString = conditionString;
		this.mth = mth;
	}
	
	public Set<String> getUniqueReadVarInCond() {
		return uniqueReadVarInCond;
	}
	
	public Set<String> getUniqueWriteVarInCond() {
		return uniqueWriteVarInCond;
	}

	public Set<String> getUniqueReadLitInCond() {
		return uniqueReadLitInCond;
	}

	public Set<String> getUniqueWriteLitInCond() {
		return uniqueWriteLitInCond;
	}
	
	public Set<String> getUniqueMethodCallsInCond() {
		return uniqueMethodCallsInCond;
	}
	
	/*
	 * 
	private int numTotalReadsInThen;
	private int numTotalUniqueReadsInThen;
	private int numTotalWritesInThen;
	private int numTotalReadReadInThen;
	private int numTotalReadWriteInThen;
	private int numTotalWriteReadInThen;
	private int numTotalWriteWriteInThen;
	private int numTotalMethodCallsInThen;
	private int numUniqueMethodCallsInThen;
	 */
	public int getNumStatementsInThen() { return numStatementsInThen; }
	public int getNumStatementsInElse() { return numStatementsInElse; }
	public void setNumStatements(int numStatements) { this.numStatements = numStatements; }
	public void setNumStatementsInThen(int numStatementsInThen) { this.numStatementsInThen = numStatementsInThen; }
	public void setNumStatementsInElse(int numStatementsInElse) { this.numStatementsInElse = numStatementsInElse; }
	public void setInLoop(boolean inLoop) { this.inLoop = inLoop; }
	public void setNumNestedIfStatements(int numNestedIfStatements) { this.numNestedIfStatements = numNestedIfStatements; }
	public void setNestingLevel(int nestingLevel) { this.nestingLevel = nestingLevel; }
	public void setComparisonList(List<Compare> comparisons) { this.comparisons = comparisons; }
	public void setReadVarInCond(List<VariableUsage> variables) { readVarInCond= variables; }
	public void setWriteVarInCond(List<VariableUsage> variables) { writeVarInCond = variables; }
	public void setReadLitInCond(List<VariableUsage> literal) { readLitInCond= literal; }
	public void setWriteLitInCond(List<VariableUsage> literal) { writeLitInCond = literal; }
	public void setMethodCallsInCond(List<MethodCall> calls) { methodCallsInCond = calls; }
	public void setUniqueReadVarInCond(Set<String> variableNames) { uniqueReadVarInCond = variableNames; }
	public void setUniqueWriteVarInCond(Set<String> variableNames) { uniqueWriteVarInCond = variableNames; }
	public void setUniqueReadLitInCond(Set<String> literalNames) { uniqueReadLitInCond = literalNames; }
	public void setUniqueWriteLitInCond(Set<String> literalNames) { uniqueWriteLitInCond = literalNames; }
	public void setUniqueMethodCallsInCond(Set<String> methodCalls) { uniqueMethodCallsInCond = methodCalls; }
	
	public void setTotalReadsInThen(int numTotalReadsInThen) { this.numTotalReadsInThen = numTotalReadsInThen; }
	public void setTotalUniqueReadsInThen(int numTotalUniqueReadsInThen) { this.numTotalUniqueReadsInThen = numTotalUniqueReadsInThen; }
	public void setTotalWritesInThen(int numTotalWritesInThen) { this.numTotalWritesInThen = numTotalWritesInThen; }
	public void setTotalReadReadInThen(int numTotalReadReadInThen) { this.numTotalReadReadInThen = numTotalReadReadInThen; }
	public void setTotalReadWriteInThen(int numTotalReadWriteInThen) { this.numTotalReadWriteInThen = numTotalReadWriteInThen; }
	public void setTotalWriteReadInThen(int numTotalWriteReadInThen) { this.numTotalWriteReadInThen = numTotalWriteReadInThen; }
	public void setTotalWriteWriteInThen(int numTotalWriteWriteInThen) { this.numTotalWriteWriteInThen = numTotalWriteWriteInThen; }
	public void setTotalMethodCallsInThen(int numTotalMethodCallsInThen) { this.numTotalMethodCallsInThen = numTotalMethodCallsInThen; }
	public void setTotalUniqueMethodCallsInThen(int numUniqueMethodCallsInThen) { this.numUniqueMethodCallsInThen = numUniqueMethodCallsInThen; }
	public void setTotalReadsInElse(int numTotalReadsInElse) { this.numTotalReadsInElse = numTotalReadsInElse; }
	public void setTotalUniqueReadsInElse(int numTotalUniqueReadsInElse) { this.numTotalUniqueReadsInElse = numTotalUniqueReadsInElse; }
	public void setTotalWritesInElse(int numTotalWritesInElse) { this.numTotalWritesInElse = numTotalWritesInElse; }
	public void setTotalReadReadInElse(int numTotalReadReadInElse) { this.numTotalReadReadInElse = numTotalReadReadInElse; }
	public void setTotalReadWriteInElse(int numTotalReadWriteInElse) { this.numTotalReadWriteInElse = numTotalReadWriteInElse; }
	public void setTotalWriteReadInElse(int numTotalWriteReadInElse) { this.numTotalWriteReadInElse = numTotalWriteReadInElse; }
	public void setTotalWriteWriteInElse(int numTotalWriteWriteInElse) { this.numTotalWriteWriteInElse = numTotalWriteWriteInElse; }
	public void setTotalMethodCallsInElse(int numTotalMethodCallsInElse) { this.numTotalMethodCallsInElse = numTotalMethodCallsInElse; }
	public void setTotalUniqueMethodCallsInElse(int numUniqueMethodCallsInElse) { this.numUniqueMethodCallsInElse = numUniqueMethodCallsInElse; }
	
	@Override 
	public String toString() {
		String result = ""; 
		result += "Num Statements: " + numStatements + "\n";
		result += "Num Nested If Statements: " + numNestedIfStatements + "\n";
		result += "Nesting Level: " + nestingLevel + "\n";
		result += "In Loop: " + inLoop + "\n";
		result += "Num Comparisons: " + comparisons.size() + "\n";
		result += "Num If Containers: " + numIfContainers + "\n";
		result += "Num Overall Variables Read In Condition: " + readVarInCond.size() + "\n";
		result += "Num Overall Variables Written In Condition: " + writeVarInCond.size() + "\n";
		result += "Num Overall Literals Read In Condition: " + readLitInCond.size() + "\n";
		result += "Num Overall Literals Written In Condition: " + writeLitInCond.size() + "\n";
		result += "Num Overall Variables Method Calls In Condition: " + methodCallsInCond.size() + "\n";
		result += "Num Unique Variables Read In Condition: " + uniqueReadVarInCond.size() + "\n";
		result += "Num Unique Variables Written In Condition: " + uniqueWriteVarInCond.size() + "\n";
		result += "Num Unique Literals Read In Condition: " + uniqueReadLitInCond.size() + "\n";
		result += "Num Unique Literals Written In Condition: " + uniqueWriteLitInCond.size() + "\n";
		result += "Num Unique Variables Method Calls In Condition: " + uniqueMethodCallsInCond.size() + "\n";
		result += "Num Total Reads In Then: " + numTotalReadsInThen + "\n";
		result += "Num Unique Reads In Then (1 per statement): " + numTotalUniqueReadsInThen + "\n";
		result += "Num Total Writes In Then: " + numTotalWritesInThen + "\n";
		result += "Num Read after Cond Read In Then: " + numTotalReadReadInThen + "\n";
		result += "Num Write after Cond Read In Then: " + numTotalReadWriteInThen + "\n";
		result += "Num Read after Cond Write In Then: " + numTotalWriteReadInThen + "\n";
		result += "Num Write after Cond Write In Then: " + numTotalWriteWriteInThen + "\n";
		result += "Num Total Method Calls In Then: " + numTotalMethodCallsInThen + "\n";
		result += "Num Unique Method Calls In Then: " + numUniqueMethodCallsInThen + "\n";
		result += "Num Total Reads In Else: " + numTotalReadsInElse + "\n";
		result += "Num Unique Reads In Else (1 per statement): " + numTotalUniqueReadsInElse + "\n";
		result += "Num Total Writes In Else: " + numTotalWritesInElse + "\n";
		result += "Num Read after Cond Read In Else: " + numTotalReadReadInElse + "\n";
		result += "Num Write after Cond Read In Else: " + numTotalReadWriteInElse + "\n";
		result += "Num Read after Cond Write In Else: " + numTotalWriteReadInElse + "\n";
		result += "Num Write after Cond Write In Else: " + numTotalWriteWriteInElse + "\n";
		result += "Num Total Method Calls In Else: " + numTotalMethodCallsInElse + "\n";
		result += "Num Unique Method Calls In Else: " + numUniqueMethodCallsInElse + "\n";
		return result;
	}
	
	public String getStats(String delimiter) {
		String methodName = mth.getMethodNode().getMethodInfo().getFullName();
		methodName += "(" + mth.getMethodNode().getMethodInfo().getArgumentsTypes();
		methodName = methodName.replace("[", "");
		methodName = methodName.replace("]", "");
		methodName += ")";
		
		String result = "";
		result += methodName + delimiter;
		result += conditionString.replace('\'', '\'') + delimiter;
		result += numStatements + delimiter;
		result += numStatementsInThen + delimiter;
		result += numStatementsInElse + delimiter;
		result += numNestedIfStatements + delimiter;
		result += nestingLevel + delimiter;
		result += inLoop + delimiter;
		result += comparisons.size() + delimiter;
		result += numIfContainers + delimiter;
		result += readVarInCond.size() + delimiter;
		result += writeVarInCond.size() + delimiter;
		result += readLitInCond.size() + delimiter;
		result += writeLitInCond.size() + delimiter;
		result += methodCallsInCond.size() + delimiter;
		result += uniqueReadVarInCond.size() + delimiter;
		result += uniqueWriteVarInCond.size() + delimiter;
		result += uniqueReadLitInCond.size() + delimiter;
		result += uniqueWriteLitInCond.size() + delimiter;
		result += uniqueMethodCallsInCond.size() + delimiter;
		result += numTotalReadsInThen + delimiter;
		result += numTotalUniqueReadsInThen + delimiter;
		result += numTotalWritesInThen + delimiter;
		result += numTotalReadReadInThen + delimiter;
		result += numTotalReadWriteInThen + delimiter;
		result += numTotalWriteReadInThen + delimiter;
		result += numTotalWriteWriteInThen + delimiter;
		result += numTotalMethodCallsInThen + delimiter;
		result += numUniqueMethodCallsInThen + delimiter;
		result += numTotalReadsInElse + delimiter;
		result += numTotalUniqueReadsInElse + delimiter;
		result += numTotalWritesInElse + delimiter;
		result += numTotalReadReadInElse + delimiter;
		result += numTotalReadWriteInElse + delimiter;
		result += numTotalWriteReadInElse + delimiter;
		result += numTotalWriteWriteInElse + delimiter;
		result += numTotalMethodCallsInElse + delimiter;
		result += numUniqueMethodCallsInElse + "\n";
		return result;
	}

	public void setNumIfContainers(int numIfContainers) {
		this.numIfContainers = numIfContainers;
	}
}