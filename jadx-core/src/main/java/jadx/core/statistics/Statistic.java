package jadx.core.statistics;

import java.util.List;
import java.util.Set;

import jadx.core.dex.nodes.IContainer;
import jadx.core.dex.regions.conditions.Compare;

public class Statistic {
	private IContainer thenRegion;
	private IContainer elseRegion;
	private int numStatements;
	private int numNestedIfStatements;
	private int nestingLevel;
	private int numIfContainers;

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
	
	private boolean isElse;
	private boolean inLoop;
	
	public Statistic(IContainer thenRegion, IContainer elseRegion, boolean isElse) {
		this.thenRegion = thenRegion;
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

	public void setReadVarInCond(List<VariableUsage> variables) {
		readVarInCond= variables;
	}

	public void setWriteVarInCond(List<VariableUsage> variables) {
		writeVarInCond = variables;
	}

	public void setReadLitInCond(List<VariableUsage> literal) {
		readLitInCond= literal;
	}

	public void setWriteLitInCond(List<VariableUsage> literal) {
		writeLitInCond = literal;
	}

	public void setMethodCallsInCond(List<MethodCall> calls) {
		methodCallsInCond = calls;
	}
	
	public void setUniqueReadVarInCond(Set<String> variableNames) {
		uniqueReadVarInCond = variableNames;
	}
	
	public Set<String> getUniqueReadVarInCond() {
		return uniqueReadVarInCond;
	}

	public void setUniqueWriteVarInCond(Set<String> variableNames) {
		uniqueWriteVarInCond = variableNames;
	}
	
	public Set<String> getUniqueWriteVarInCond() {
		return uniqueWriteVarInCond;
	}
	
	public void setUniqueReadLitInCond(Set<String> literalNames) {
		uniqueReadLitInCond = literalNames;
	}

	public Set<String> getUniqueReadLitInCond() {
		return uniqueReadLitInCond;
	}
	
	public void setUniqueWriteLitInCond(Set<String> literalNames) {
		uniqueWriteLitInCond = literalNames;
	}

	public Set<String> getUniqueWriteLitInCond() {
		return uniqueWriteLitInCond;
	}
	
	public void setUniqueMethodCallsInCond(Set<String> methodCalls) {
		uniqueMethodCallsInCond = methodCalls;
	}
	
	public Set<String> getUniqueMethodCallsInCond() {
		return uniqueMethodCallsInCond;
	}
	
	
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
		return result;
	}

	public void setNumIfContainers(int numIfContainers) {
		this.numIfContainers = numIfContainers;
	}
}