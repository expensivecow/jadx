package jadx.core.statistics;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jadx.core.codegen.CodeWriter;
import jadx.core.codegen.ConditionGen;
import jadx.core.codegen.InsnGen;
import jadx.core.codegen.MethodGen;
import jadx.core.codegen.RegionGen;
import jadx.core.dex.nodes.IBlock;
import jadx.core.dex.nodes.IContainer;
import jadx.core.dex.nodes.IRegion;
import jadx.core.dex.nodes.InsnNode;
import jadx.core.dex.regions.Region;
import jadx.core.dex.regions.conditions.Compare;
import jadx.core.dex.regions.conditions.IfCondition;
import jadx.core.dex.regions.conditions.IfRegion;
import jadx.core.dex.regions.loops.LoopRegion;
import jadx.core.utils.exceptions.CodegenException;

/**
 * Encapsulates the statistics of an if, else if, else (held each as
 */
public class IfStatistic {
	MethodGen mth;
	RegionGen rGen;
	IfRegion ifRegion;
	IContainer els;
	List<Statistic> statistics;
	InsnGen iGen;
	ConditionGen cGen;

	
	public IfStatistic(IfRegion ifRegion, MethodGen mth, RegionGen rGen) {
		this.mth = mth;
		this.rGen = rGen;
		this.ifRegion = ifRegion;
		this.statistics = new ArrayList<Statistic>();
		this.iGen = new InsnGen(mth, false);
		cGen = new ConditionGen(iGen);
		createStatistics();
	}
	
	public List<Statistic> getStatistics() {
		return statistics;
	}
	
	private void createStatistics() {
		// Populate else block and statistics
		try {
			CodeWriter writer = new CodeWriter();
			// Then
			cGen.add(writer, ifRegion.getCondition());
			//System.out.println("Found If: " + writer.toString());
			
			// IfRegion, abstractly an IRegion
			int ifNestingLevel = getNestingLevel((IRegion) ifRegion) - 1;
			int numIfContainers = rGen.getIfRegionInfo(ifRegion).size() + 1;
			
			addStatistic(ifRegion.getThenRegion(), ifRegion.getElseRegion(), ifRegion.getCondition(), false, ifNestingLevel, numIfContainers);
			
			// Handle the conditions else if and else cases
			for (IfRegionInfo info : rGen.getIfRegionInfo(ifRegion)) {
				// If/Else If Case
				if (!info.isElse()) {
					writer = new CodeWriter();
					cGen.add(writer, info.getCondition());
					//System.out.println("Found Else If: " + info.getCondition());
					IfRegion ir = (IfRegion) info.getRegion();
					addStatistic(ir.getThenRegion(), ir.getElseRegion(), info.getCondition(), info.isElse(), ifNestingLevel, numIfContainers);
				}
				// Else Case
				else {
					//System.out.println("Found Else");
					//addStatistic(info.getRegion(), info.getCondition(), info.isElse(), ifNestingLevel);
				}
			}
		} catch (CodegenException e) {
			e.printStackTrace();
		}
	}
	
	private void addStatistic(IContainer thenRegion, IContainer elseRegion, IfCondition condition, boolean isElse, int nestingLevel, int numIfContainers) throws CodegenException {
		CodeWriter writer = new CodeWriter();
		ConditionGen cGen = new ConditionGen(iGen);
		cGen.add(writer, condition);
		Statistic stat = new Statistic(thenRegion, elseRegion, condition, writer.toString(), mth);
		
		int numTotalStatements = getNumStatements(thenRegion) + ((elseRegion != null) ? getNumStatements(elseRegion) : 0);
		//System.out.println("Total Statements: " + numTotalStatements);
		stat.setNumStatements(numTotalStatements);
		stat.setNumNestedIfStatements(rGen.getNumNestedIfConditions(thenRegion));
		stat.setInLoop(inLoop((IRegion) thenRegion));
		stat.setNestingLevel(nestingLevel);
		stat.setNumIfContainers(numIfContainers);

		List<Compare> comparisons = iGen.getCompareList(condition);
		stat.setComparisonList(comparisons);

		InsnVariableContainer vc = new InsnVariableContainer();
		
		for (Compare compare : comparisons) {
			iGen.handleCompare(vc, compare);
		}
		
		vc.separateLiteralsAndVariables();
		updateVariablesForStat(stat, condition, vc);
		updateLiteralsForStat(stat, condition, vc);
		updateMethodCallsForStat(stat, condition, vc);
		
		//System.out.println(vc.toString());
		
		getThenVariableStatistics(stat, vc, thenRegion);
		getElseVariableStatistics(stat, vc, elseRegion);
		
		//System.out.println("On Method: " + mth.getMethodNode().getMethodInfo().getFullName());
		//System.out.println("==================");
		
		statistics.add(stat);
	}
	
	private void getThenVariableStatistics(Statistic stat, InsnVariableContainer vc, IContainer thenRegion) throws CodegenException {
		//System.out.println("Get Then Variable Usage");
		int totalMethodCalls = 0;
		int totalReadThenRead = 0;
		int totalWriteThenWrite = 0;
		int totalReadThenWrite = 0;
		int totalWriteThenRead = 0;
		int totalWrites = 0;
		int totalUniqueReads = 0;
		int totalReads = 0;
		int totalUniqueMethodCalls = 0;
		
		Set<String> methodCalls = new HashSet<String>();
		
		for (IBlock block : rGen.getBlocksForRegion(thenRegion)) {
			for (InsnNode insn : block.getInstructions()) {
				InsnVariableContainer insnVC = new InsnVariableContainer();
				iGen.getInsnVariables(insnVC, insn);
				
				insnVC.separateLiteralsAndVariables();
				
				if (insnVC.getMethodCalls().size() > 0) {
					for (MethodCall call : insnVC.getMethodCalls()) {
						methodCalls.add(call.toString());
					}
					totalMethodCalls++;
				}
				
				for (VariableUsage usage : insnVC.getReadVariables()) {
					if (stat.getUniqueReadVarInCond().contains(usage.toString())) {
						totalReadThenRead++;
					}
					
					if (stat.getUniqueWriteVarInCond().contains(usage.toString())) {
						totalReadThenWrite++;
					}
				}
				
				for (VariableUsage usage : insnVC.getWriteVariables()) {
					if (stat.getUniqueReadVarInCond().contains(usage.toString())) {
						totalWriteThenRead++;
					}
					
					if (stat.getUniqueReadVarInCond().contains(usage.toString())) {
						totalWriteThenWrite++;
					}
				}
				
				if (insnVC.getWriteVariables().size() > 0) {
					totalWrites++;
				}
				
				if (countVariables(insnVC.getReadVariables()) > 0) {
					totalUniqueReads++;
					totalReads += (countVariables(insnVC.getReadVariables()));
				}
			}
		}
		
		totalUniqueMethodCalls = methodCalls.size();

		/*
		System.out.println("Total Variable Read Then Read: " + totalReadThenRead);
		System.out.println("Total Variable Write Then Write: " + totalWriteThenWrite);
		System.out.println("Total Variable Read Then Write: " + totalReadThenWrite);
		System.out.println("Total Variable Write Then Read: " + totalWriteThenRead);
		System.out.println("Total Variable Reads (Multiple variables per statement): " + totalReads);
		System.out.println("Total Variable Unique Reads (One per statement): " + totalUniqueReads);
		System.out.println("Total Variable Writes: " + totalWrites);
		System.out.println("Total Method Calls: " + totalMethodCalls);
		System.out.println("Total Unique Method Calls: " + totalUniqueMethodCalls);
		*/
		
		stat.setTotalReadsInThen(totalReads);
		stat.setTotalUniqueReadsInThen(totalUniqueReads);
		stat.setTotalWritesInThen(totalWrites);
		stat.setTotalReadReadInThen(totalReadThenRead);
		stat.setTotalReadWriteInThen(totalReadThenWrite);
		stat.setTotalWriteReadInThen(totalWriteThenRead);
		stat.setTotalWriteWriteInThen(totalWriteThenWrite);
		stat.setTotalMethodCallsInThen(totalMethodCalls);
		stat.setTotalUniqueMethodCallsInThen(totalUniqueMethodCalls);
	}
	
	private void getElseVariableStatistics(Statistic stat, InsnVariableContainer vc, IContainer elseRegion) throws CodegenException {
		//System.out.println("Get Else Variable Usage");
		int totalMethodCalls = 0;
		int totalReadThenRead = 0;
		int totalWriteThenWrite = 0;
		int totalReadThenWrite = 0;
		int totalWriteThenRead = 0;
		int totalWrites = 0;
		int totalUniqueReads = 0;
		int totalReads = 0;
		int totalUniqueMethodCalls = 0;
		
		if (elseRegion != null) {
			Set<String> methodCalls = new HashSet<String>();
			
			for (IBlock block : rGen.getBlocksForRegion(elseRegion)) {
				for (InsnNode insn : block.getInstructions()) {
					InsnVariableContainer insnVC = new InsnVariableContainer();
					iGen.getInsnVariables(insnVC, insn);
					insnVC.separateLiteralsAndVariables();
					
					if (insnVC.getMethodCalls().size() > 0) {
						for (MethodCall call : insnVC.getMethodCalls()) {
							methodCalls.add(call.toString());
						}
						totalMethodCalls++;
					}
					
					for (VariableUsage usage : insnVC.getReadVariables()) {
						if (stat.getUniqueReadVarInCond().contains(usage.toString())) {
							totalReadThenRead++;
						}
						
						if (stat.getUniqueWriteVarInCond().contains(usage.toString())) {
							totalReadThenWrite++;
						}
					}
					
					for (VariableUsage usage : insnVC.getWriteVariables()) {
						if (stat.getUniqueReadVarInCond().contains(usage.toString())) {
							totalWriteThenRead++;
						}
						
						if (stat.getUniqueReadVarInCond().contains(usage.toString())) {
							totalWriteThenWrite++;
						}
					}
					
					if (insnVC.getWriteVariables().size() > 0) {
						totalWrites++;
					}
					
					if (countVariables(insnVC.getReadVariables()) > 0) {
						totalUniqueReads++;
						totalReads += (countVariables(insnVC.getReadVariables()));
					}
				}
			}
			
			totalUniqueMethodCalls = methodCalls.size();
			/*
			System.out.println("Total Variable Read Then Read: " + totalReadThenRead);
			System.out.println("Total Variable Write Then Write: " + totalWriteThenWrite);
			System.out.println("Total Variable Read Then Write: " + totalReadThenWrite);
			System.out.println("Total Variable Write Then Read: " + totalWriteThenRead);
			System.out.println("Total Variable Reads (Multiple variables per statement): " + totalReads);
			System.out.println("Total Variable Unique Reads (One per statement): " + totalUniqueReads);
			System.out.println("Total Variable Writes: " + totalWrites);
			System.out.println("Total Method Calls: " + totalMethodCalls);
			System.out.println("Total Unique Method Calls: " + totalUniqueMethodCalls);
			*/	
		}
		
		
		stat.setTotalReadsInElse(totalReads);
		stat.setTotalUniqueReadsInElse(totalUniqueReads);
		stat.setTotalWritesInElse(totalWrites);
		stat.setTotalReadReadInElse(totalReadThenRead);
		stat.setTotalReadWriteInElse(totalReadThenWrite);
		stat.setTotalWriteReadInElse(totalWriteThenRead);
		stat.setTotalWriteWriteInElse(totalWriteThenWrite);
		stat.setTotalMethodCallsInElse(totalMethodCalls);
		stat.setTotalUniqueMethodCallsInElse(totalUniqueMethodCalls);
	}
	
	private int countVariables(List<VariableUsage> usages) {
		int variableReads = 0;
		for (VariableUsage usage : usages) {
			if (usage.isVariable())
				variableReads++;
		}
		
		return variableReads;
	}
	
	private void updateVariablesForStat(Statistic stat, IfCondition condition, InsnVariableContainer vc) throws CodegenException {
		// Read, Write, and Method Calls from If Condition
		Set<String> readVariables = new HashSet<String>();
		Set<String> writeVariables = new HashSet<String>();

		for (VariableUsage usage : vc.getReadVariables()) { readVariables.add(usage.toString()); }
		for (VariableUsage usage : vc.getWriteVariables()) { writeVariables.add(usage.toString()); }

		stat.setReadVarInCond(vc.getReadVariables());
		stat.setWriteVarInCond(vc.getWriteVariables());
		stat.setUniqueReadVarInCond(readVariables);
		stat.setUniqueWriteVarInCond(writeVariables);
	}
	
	private void updateLiteralsForStat(Statistic stat, IfCondition condition, InsnVariableContainer vc) {
		Set<String> readLiterals = new HashSet<String>();
		Set<String> writeLiterals = new HashSet<String>();
		
		for (VariableUsage usage : vc.getReadLiterals()) { readLiterals.add(usage.toString()); }
		for (VariableUsage usage : vc.getWriteLiterals()) { writeLiterals.add(usage.toString()); }
		
		stat.setReadLitInCond(vc.getReadLiterals());
		stat.setWriteLitInCond(vc.getWriteLiterals());
		stat.setUniqueReadLitInCond(readLiterals);
		stat.setUniqueWriteLitInCond(writeLiterals);
	}
	
	private void updateMethodCallsForStat(Statistic stat, IfCondition condition, InsnVariableContainer vc) {
		Set<String> methodCalls = new HashSet<String>();
		for (MethodCall call : vc.getMethodCalls()) { methodCalls.add(call.toString());}

		stat.setMethodCallsInCond(vc.getMethodCalls());	
		stat.setUniqueMethodCallsInCond(methodCalls);	
	}
	
	private int getNumStatements(IContainer container) throws CodegenException {
		int numStatements = 0;
		for (IBlock block : rGen.getBlocksForRegion(container)) {
			for (InsnNode node : block.getInstructions()) {
				numStatements++;
			}
		}
		return numStatements;
	}
	
	private boolean inLoop(IRegion region) {
		if (region.getParent() == null) {
			return false;
		}
		else if (region instanceof LoopRegion) {
			return true;
		}
		else {
			return inLoop(region.getParent());
		}
	}

	private int getNestingLevel(IRegion region) {
		if (region.getParent() == null) {
			return 0;
		}
		else {
			if (region instanceof Region) {
				return getNestingLevel(region.getParent()) + 0;
			}
			else {
				return getNestingLevel(region.getParent()) + 1;
			}
		}
	}
}
