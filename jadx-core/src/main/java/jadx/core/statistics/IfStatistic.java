package jadx.core.statistics;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jadx.core.codegen.InsnGen;
import jadx.core.codegen.MethodGen;
import jadx.core.codegen.RegionGen;
import jadx.core.dex.instructions.IfOp;
import jadx.core.dex.instructions.args.ArgType;
import jadx.core.dex.instructions.args.InsnArg;
import jadx.core.dex.instructions.args.LiteralArg;
import jadx.core.dex.nodes.IBlock;
import jadx.core.dex.nodes.IContainer;
import jadx.core.dex.nodes.IRegion;
import jadx.core.dex.nodes.InsnNode;
import jadx.core.dex.regions.Region;
import jadx.core.dex.regions.conditions.Compare;
import jadx.core.dex.regions.conditions.IfCondition;
import jadx.core.dex.regions.conditions.IfRegion;
import jadx.core.dex.regions.loops.LoopRegion;
import jadx.core.utils.ErrorsCounter;
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

	
	public IfStatistic(IfRegion ifRegion, MethodGen mth, RegionGen rGen) {
		this.mth = mth;
		this.rGen = rGen;
		this.ifRegion = ifRegion;
		this.statistics = new ArrayList<Statistic>();
		
		createStatistics();
	}
	
	private void createStatistics() {
		// Populate else block and statistics
		try {
			// Then
			System.out.println("Found If: " + ifRegion.getCondition());
			
			// IfRegion, abstractly an IRegion
			int ifNestingLevel = getNestingLevel((IRegion) ifRegion) - 1;
			
			addStatistic(ifRegion.getThenRegion(), ifRegion.getCondition(), false, ifNestingLevel);
			
			// Handle the conditions else if and else cases
			for (IfRegionInfo info : rGen.getIfRegionInfo(ifRegion)) {
				// If/Else If Case
				if (!info.isElse()) {
					System.out.println("Found Else If: " + info.getCondition());
					IfRegion ir = (IfRegion) info.getRegion();
					addStatistic(ir.getThenRegion(), info.getCondition(), info.isElse(), ifNestingLevel);
				}
				// Else Case
				else {
					System.out.println("Found Else");
					addStatistic(info.getRegion(), info.getCondition(), info.isElse(), ifNestingLevel);
				}
			}
		} catch (CodegenException e) {
			e.printStackTrace();
		}
	}
	
	private void addStatistic(IContainer container, IfCondition condition, boolean isElse, int nestingLevel) throws CodegenException {
		Statistic stat = new Statistic(container, isElse);
		InsnGen iGen = new InsnGen(mth, false);
		
		stat.setNumStatements(getNumStatements(container));
		stat.setNumNestedIfStatements(rGen.getNumNestedIfConditions(container));
		stat.setInLoop(inLoop((IRegion) container));
		stat.setNestingLevel(nestingLevel);
		
		List<Compare> comparisons = iGen.getCompareList(condition);
		stat.setComparisonList(comparisons);

		InsnVariableContainer vc = new InsnVariableContainer();
		
		for (Compare compare : comparisons) {
			iGen.getVariableUsageFromArg(false, vc, compare.getA(), false);
			iGen.getVariableUsageFromArg(false, vc, compare.getB(), false);
		}
		
		vc.cleanUpLiterals();

		// Read, Write, and Method Calls from If Condition
		Set<String> readVariables = new HashSet<String>();
		Set<String> writeVariables = new HashSet<String>();
		Set<String> methodCalls = new HashSet<String>();

		for (VariableUsage usage : vc.getReadVariables()) { readVariables.add(usage.toString()); }
		for (VariableUsage usage : vc.getWriteVariables()) { writeVariables.add(usage.toString()); }
		for (MethodCall call : vc.getMethodCalls()) { methodCalls.add(call.toString());}

		stat.setReadVarInCond(vc.getReadVariables());
		stat.setWriteVarInCond(vc.getReadVariables());
		stat.setMethodCallsInCond(vc.getMethodCalls());
		
		stat.setUniqueReadVarInCond(readVariables);
		stat.setUniqueWriteVarInCond(writeVariables);
		stat.setUniqueMethodCallsInCond(methodCalls);
		
		System.out.println("Num Overall Read Variables: " + vc.getReadVariables().toString());
		System.out.println("Num Overall Written Variables: " + vc.getWriteVariables().toString());
		System.out.println("Num Overall Method Calls" + vc.getMethodCalls().toString());
		System.out.println("");
		System.out.println("Unique Read Variables: " + readVariables.toString());
		System.out.println("Unique Written Variables: " + writeVariables.toString());
		System.out.println("Unique Method Calls" + methodCalls.toString());
		
		System.out.println("Variables read/written and Method calls for If Condition:");
		System.out.println(vc.toString());

		System.out.println("On Method: " + mth.getMethodNode().getMethodInfo().getFullName());
		System.out.println("==================");
		
		statistics.add(stat);
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
	
	private void handleCompare(InsnVariableContainer vc, Compare compare) {
		IfOp op = compare.getOp();
		InsnArg firstArg = compare.getA();
		InsnArg secondArg = compare.getB();
		
		if (firstArg.getType().equals(ArgType.BOOLEAN)
				&& secondArg.isLiteral()
				&& secondArg.getType().equals(ArgType.BOOLEAN)) {
			LiteralArg lit = (LiteralArg) secondArg;
			if (lit.getLiteral() == 0) {
				op = op.invert();
			}
			if (op == IfOp.EQ) {
				// == true
				//if (stack.getStack().size() == 1) {
				//	addArg(code, firstArg, false);
				//}
				return;
			} else if (op == IfOp.NE) {
				return;
			}
		}
	}
}
