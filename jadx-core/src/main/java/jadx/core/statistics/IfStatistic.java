package jadx.core.statistics;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jadx.core.codegen.ArgTypes;
import jadx.core.codegen.CodeWriter;
import jadx.core.codegen.ConditionGen;
import jadx.core.codegen.InsnGen;
import jadx.core.codegen.MethodGen;
import jadx.core.codegen.RegionGen;
import jadx.core.dex.attributes.AFlag;
import jadx.core.dex.instructions.SwitchNode;
import jadx.core.dex.instructions.args.InsnArg;
import jadx.core.dex.nodes.IBlock;
import jadx.core.dex.nodes.IContainer;
import jadx.core.dex.nodes.IRegion;
import jadx.core.dex.nodes.InsnNode;
import jadx.core.dex.regions.Region;
import jadx.core.dex.regions.SwitchRegion;
import jadx.core.dex.regions.conditions.Compare;
import jadx.core.dex.regions.conditions.IfCondition;
import jadx.core.dex.regions.conditions.IfRegion;
import jadx.core.dex.regions.loops.ForEachLoop;
import jadx.core.dex.regions.loops.ForLoop;
import jadx.core.dex.regions.loops.LoopRegion;
import jadx.core.dex.regions.loops.LoopType;
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
			System.out.println("Found If: " + writer.toString());
			
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
					System.out.println("Found Else If: " + info.getCondition());
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
		
		stat.setNumStatements(stat.getNumStatementsInThen() + stat.getNumStatementsInElse());
		System.out.println("On Method: " + mth.getMethodNode().getMethodInfo().getFullName());
		System.out.println("==================");
		
		statistics.add(stat);
	}
	
	private void getThenVariableStatistics(Statistic stat, InsnVariableContainer vc, IContainer thenRegion) throws CodegenException {
		System.out.println("Get Then Variable Usage");
		int totalMethodCalls = 0;
		int totalReadThenRead = 0;
		int totalWriteThenWrite = 0;
		int totalReadThenWrite = 0;
		int totalWriteThenRead = 0;
		int totalWrites = 0;
		int totalUniqueReads = 0;
		int totalReads = 0;
		int totalUniqueMethodCalls = 0;
		int numStatementsInThen = 0;
		
		numStatementsInThen = getNumStatements(thenRegion);
		
		Set<String> methodCalls = new HashSet<String>();

		// Handle Simple Statements
		for (IBlock block : rGen.getBlocksForRegion(thenRegion)) {
			int i = 1;
			for (InsnNode insn : block.getInstructions()) {

				if (!insn.contains(AFlag.SKIP)) {
					InsnVariableContainer insnVC = new InsnVariableContainer();
					iGen.getInsnVariables(insnVC, insn);
					
					System.out.println((i++) + ": " + insn);
				
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
		}
		
		// Handle Switch enum/int/boolean read
		for (SwitchRegion region : rGen.getSwitchRegion(thenRegion)) {
			numStatementsInThen++;
			SwitchNode switchInsn = (SwitchNode) region.getHeader().getInstructions().get(0);
			InsnArg arg = switchInsn.getArg(0);
			
			InsnVariableContainer switchVC = new InsnVariableContainer();
			iGen.getVariableUsageFromArg(false, switchVC, arg, false);
			switchVC.separateLiteralsAndVariables();

			if (switchVC.getMethodCalls().size() > 0) {
				for (MethodCall call : switchVC.getMethodCalls()) {
					methodCalls.add(call.toString());
				}
				totalMethodCalls++;
			}
			
			for (VariableUsage usage : switchVC.getReadVariables()) {
				if (stat.getUniqueReadVarInCond().contains(usage.toString())) {
					totalReadThenRead++;
				}
				
				if (stat.getUniqueWriteVarInCond().contains(usage.toString())) {
					totalReadThenWrite++;
				}
			}
			
			for (VariableUsage usage : switchVC.getWriteVariables()) {
				if (stat.getUniqueReadVarInCond().contains(usage.toString())) {
					totalWriteThenRead++;
				}
				
				if (stat.getUniqueReadVarInCond().contains(usage.toString())) {
					totalWriteThenWrite++;
				}
			}
			
			if (switchVC.getWriteVariables().size() > 0) {
				totalWrites++;
			}
			
			if (countVariables(switchVC.getReadVariables()) > 0) {
				totalUniqueReads++;
				totalReads += (countVariables(switchVC.getReadVariables()));
			}
		}
		
		// Handle If Conditions
		for (IfCondition condition : rGen.getConditionsForRegion(thenRegion)) {
			numStatementsInThen++;
			
			InsnVariableContainer conditionVC = new InsnVariableContainer();
			
			List<Compare> conditionComparisons = iGen.getCompareList(condition);

			for (Compare compare : conditionComparisons) {
				iGen.handleCompare(conditionVC, compare);
			}
			
			conditionVC.separateLiteralsAndVariables();
			
			if (conditionVC.getMethodCalls().size() > 0) {
				for (MethodCall call : conditionVC.getMethodCalls()) {
					methodCalls.add(call.toString());
				}
				totalMethodCalls++;
			}
			
			for (VariableUsage usage : conditionVC.getReadVariables()) {
				if (stat.getUniqueReadVarInCond().contains(usage.toString())) {
					totalReadThenRead++;
				}
				
				if (stat.getUniqueWriteVarInCond().contains(usage.toString())) {
					totalReadThenWrite++;
				}
			}
			
			for (VariableUsage usage : conditionVC.getWriteVariables()) {
				if (stat.getUniqueReadVarInCond().contains(usage.toString())) {
					totalWriteThenRead++;
				}
				
				if (stat.getUniqueReadVarInCond().contains(usage.toString())) {
					totalWriteThenWrite++;
				}
			}
			
			if (conditionVC.getWriteVariables().size() > 0) {
				totalWrites++;
			}
			
			if (countVariables(conditionVC.getReadVariables()) > 0) {
				totalUniqueReads++;
				totalReads += (countVariables(conditionVC.getReadVariables()));
			}
		}
		
		// Handle Loop Condition
		for (LoopRegion loop : rGen.getLoopsForRegion(thenRegion)) {
			numStatementsInThen++;

			if (loop.getCondition() != null) {
				LoopType type = loop.getType();
				InsnVariableContainer loopVC = new InsnVariableContainer();

				if (type != null) {
					// Exclude ForLoop, no condition is used for this case.
					if (!(type instanceof ForEachLoop)) {
						List<Compare> loopComparisons = iGen.getCompareList(loop.getCondition());

						for (Compare compare : loopComparisons) {
							iGen.handleCompare(loopVC, compare);
						}
					}
					
					if (type instanceof ForLoop) {
						ForLoop forLoop = (ForLoop) type;
						
						iGen.getInsnVariables(loopVC, forLoop.getInitInsn());
						iGen.getInsnVariables(loopVC, forLoop.getIncrInsn());
						
					}
					if (type instanceof ForEachLoop) {
						System.out.println("Hello");
						ForEachLoop forEachLoop = (ForEachLoop) type;

						System.out.println(forEachLoop.getVarArg());
						iGen.getVariableUsageFromArg(true, loopVC, forEachLoop.getVarArg(), false);
						iGen.getVariableUsageFromArg(false, loopVC, forEachLoop.getIterableArg(), false);
					}
				}

				loopVC.separateLiteralsAndVariables();

				if (loopVC.getMethodCalls().size() > 0) {
					for (MethodCall call : loopVC.getMethodCalls()) {
						methodCalls.add(call.toString());
					}
					totalMethodCalls++;
				}
				
				for (VariableUsage usage : loopVC.getReadVariables()) {
					if (stat.getUniqueReadVarInCond().contains(usage.toString())) {
						totalReadThenRead++;
					}
					
					if (stat.getUniqueWriteVarInCond().contains(usage.toString())) {
						totalReadThenWrite++;
					}
				}
				
				for (VariableUsage usage : loopVC.getWriteVariables()) {
					if (stat.getUniqueReadVarInCond().contains(usage.toString())) {
						totalWriteThenRead++;
					}
					
					if (stat.getUniqueReadVarInCond().contains(usage.toString())) {
						totalWriteThenWrite++;
					}
				}
				
				if (loopVC.getWriteVariables().size() > 0) {
					totalWrites++;
				}
				
				if (countVariables(loopVC.getReadVariables()) > 0) {
					totalUniqueReads++;
					totalReads += (countVariables(loopVC.getReadVariables()));
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
		
		stat.setNumStatementsInThen(numStatementsInThen);
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
		System.out.println("Get Else Variable Usage");
		System.out.println("");
		int totalMethodCalls = 0;
		int totalReadThenRead = 0;
		int totalWriteThenWrite = 0;
		int totalReadThenWrite = 0;
		int totalWriteThenRead = 0;
		int totalWrites = 0;
		int totalUniqueReads = 0;
		int totalReads = 0;
		int totalUniqueMethodCalls = 0;
		int numStatementsInElse = 0;
		
		if (elseRegion != null) {
			numStatementsInElse = getNumStatements(elseRegion);
			Set<String> methodCalls = new HashSet<String>();

			// Handle Simple Statements
			for (IBlock block : rGen.getBlocksForRegion(elseRegion)) {
				int i = 0;
				for (InsnNode insn : block.getInstructions()) {
					if (!insn.contains(AFlag.SKIP)) {
						InsnVariableContainer insnVC = new InsnVariableContainer();
						iGen.getInsnVariables(insnVC, insn);
						insnVC.separateLiteralsAndVariables();
						
						System.out.println((i++) + ": " + insn);
						
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
			}

			// Handle Switch enum/int/boolean read
			for (SwitchRegion region : rGen.getSwitchRegion(elseRegion)) {
				numStatementsInElse++;
				
				SwitchNode switchInsn = (SwitchNode) region.getHeader().getInstructions().get(0);
				InsnArg arg = switchInsn.getArg(0);
				
				InsnVariableContainer switchVC = new InsnVariableContainer();
				iGen.getVariableUsageFromArg(false, switchVC, arg, false);
				switchVC.separateLiteralsAndVariables();

				if (switchVC.getMethodCalls().size() > 0) {
					for (MethodCall call : switchVC.getMethodCalls()) {
						methodCalls.add(call.toString());
					}
					totalMethodCalls++;
				}
				
				for (VariableUsage usage : switchVC.getReadVariables()) {
					if (stat.getUniqueReadVarInCond().contains(usage.toString())) {
						totalReadThenRead++;
					}
					
					if (stat.getUniqueWriteVarInCond().contains(usage.toString())) {
						totalReadThenWrite++;
					}
				}
				
				for (VariableUsage usage : switchVC.getWriteVariables()) {
					if (stat.getUniqueReadVarInCond().contains(usage.toString())) {
						totalWriteThenRead++;
					}
					
					if (stat.getUniqueReadVarInCond().contains(usage.toString())) {
						totalWriteThenWrite++;
					}
				}
				
				if (switchVC.getWriteVariables().size() > 0) {
					totalWrites++;
				}
				
				if (countVariables(switchVC.getReadVariables()) > 0) {
					totalUniqueReads++;
					totalReads += (countVariables(switchVC.getReadVariables()));
				}
			}
			
			// Handle If Conditions
			for (IfCondition condition : rGen.getConditionsForRegion(elseRegion)) {
				numStatementsInElse++;
				
				InsnVariableContainer conditionVC = new InsnVariableContainer();
				
				List<Compare> conditionComparisons = iGen.getCompareList(condition);

				for (Compare compare : conditionComparisons) {
					iGen.handleCompare(conditionVC, compare);
				}
				
				conditionVC.separateLiteralsAndVariables();
				
				if (conditionVC.getMethodCalls().size() > 0) {
					for (MethodCall call : conditionVC.getMethodCalls()) {
						methodCalls.add(call.toString());
					}
					totalMethodCalls++;
				}
				
				for (VariableUsage usage : conditionVC.getReadVariables()) {
					if (stat.getUniqueReadVarInCond().contains(usage.toString())) {
						totalReadThenRead++;
					}
					
					if (stat.getUniqueWriteVarInCond().contains(usage.toString())) {
						totalReadThenWrite++;
					}
				}
				
				for (VariableUsage usage : conditionVC.getWriteVariables()) {
					if (stat.getUniqueReadVarInCond().contains(usage.toString())) {
						totalWriteThenRead++;
					}
					
					if (stat.getUniqueReadVarInCond().contains(usage.toString())) {
						totalWriteThenWrite++;
					}
				}
				
				if (conditionVC.getWriteVariables().size() > 0) {
					totalWrites++;
				}
				
				if (countVariables(conditionVC.getReadVariables()) > 0) {
					totalUniqueReads++;
					totalReads += (countVariables(conditionVC.getReadVariables()));
				}
			}
			
			// Handle Loop Condition
			for (LoopRegion loop : rGen.getLoopsForRegion(elseRegion)) {
				numStatementsInElse++;

				if (loop.getCondition() != null) {
					LoopType type = loop.getType();
					InsnVariableContainer loopVC = new InsnVariableContainer();

					if (type != null) {
						// Exclude ForLoop, no condition is used for this case.
						if (!(type instanceof ForEachLoop)) {
							List<Compare> loopComparisons = iGen.getCompareList(loop.getCondition());

							for (Compare compare : loopComparisons) {
								iGen.handleCompare(loopVC, compare);
							}
						}
						
						if (type instanceof ForLoop) {
							ForLoop forLoop = (ForLoop) type;
							
							iGen.getInsnVariables(loopVC, forLoop.getInitInsn());
							iGen.getInsnVariables(loopVC, forLoop.getIncrInsn());
							
						}
						if (type instanceof ForEachLoop) {
							System.out.println("Hello");
							ForEachLoop forEachLoop = (ForEachLoop) type;

							System.out.println(forEachLoop.getVarArg());
							iGen.getVariableUsageFromArg(true, loopVC, forEachLoop.getVarArg(), false);
							iGen.getVariableUsageFromArg(false, loopVC, forEachLoop.getIterableArg(), false);
						}
					}

					loopVC.separateLiteralsAndVariables();

					if (loopVC.getMethodCalls().size() > 0) {
						for (MethodCall call : loopVC.getMethodCalls()) {
							methodCalls.add(call.toString());
						}
						totalMethodCalls++;
					}
					
					for (VariableUsage usage : loopVC.getReadVariables()) {
						if (stat.getUniqueReadVarInCond().contains(usage.toString())) {
							totalReadThenRead++;
						}
						
						if (stat.getUniqueWriteVarInCond().contains(usage.toString())) {
							totalReadThenWrite++;
						}
					}
					
					for (VariableUsage usage : loopVC.getWriteVariables()) {
						if (stat.getUniqueReadVarInCond().contains(usage.toString())) {
							totalWriteThenRead++;
						}
						
						if (stat.getUniqueReadVarInCond().contains(usage.toString())) {
							totalWriteThenWrite++;
						}
					}
					
					if (loopVC.getWriteVariables().size() > 0) {
						totalWrites++;
					}
					
					if (countVariables(loopVC.getReadVariables()) > 0) {
						totalUniqueReads++;
						totalReads += (countVariables(loopVC.getReadVariables()));
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
		
		stat.setNumStatementsInElse(numStatementsInElse);
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
				if (!node.contains(AFlag.SKIP_ARG)) {
					numStatements++;
				}
			}
		}

		for (IfCondition condition : rGen.getConditionsForRegion(container)) {
			numStatements++;
		}
		
		for (SwitchRegion switches : rGen.getSwitchRegion(container)) {
			numStatements++;
		}
		
		for (LoopRegion loop : rGen.getLoopsForRegion(container)) {
			numStatements++;
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
