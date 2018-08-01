package jadx.core.statistics;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import jadx.core.codegen.MethodGen;
import jadx.core.codegen.RegionGen;
import jadx.core.codegen.TypeGen;
import jadx.core.dex.instructions.args.FieldArg;
import jadx.core.dex.instructions.args.InsnArg;
import jadx.core.dex.instructions.args.InsnWrapArg;
import jadx.core.dex.instructions.args.LiteralArg;
import jadx.core.dex.instructions.args.Named;
import jadx.core.dex.instructions.args.RegisterArg;
import jadx.core.dex.nodes.IBlock;
import jadx.core.dex.nodes.IContainer;
import jadx.core.dex.nodes.IRegion;
import jadx.core.dex.nodes.InsnNode;
import jadx.core.dex.regions.conditions.Compare;
import jadx.core.dex.regions.conditions.IfCondition;
import jadx.core.dex.regions.conditions.IfRegion;
import jadx.core.dex.regions.loops.LoopRegion;
import jadx.core.dex.regions.conditions.IfCondition.Mode;
import jadx.core.utils.exceptions.CodegenException;

/**
 * Encapsulates the statistics of an if, else if, else (held each as
 */
public class IfStatistic {
	MethodGen mth;
	RegionGen rGen;
	IfRegion ifRegion;
	IContainer els;
	List<Compare> conditionCompares;
	List<Statistic> statistics;

	
	public IfStatistic(IfRegion ifRegion, MethodGen mth, RegionGen rGen) {
		this.mth = mth;
		this.rGen = rGen;
		this.ifRegion = ifRegion;
		this.conditionCompares = new ArrayList<Compare>();
		this.statistics = new ArrayList<Statistic>();
		
		createStatistics();
		
	}
	
	private void createStatistics() {
		// Populate else block and statistics
		try {
			// Then
			System.out.println("Found If: " + ifRegion.getCondition());
			addStatistic(ifRegion.getThenRegion(), ifRegion.getCondition(), false);
			
			// Handle the conditions else if and else cases
			for (IfRegionInfo info : rGen.getIfRegionInfo(ifRegion)) {
				// If/Else If Case
				if (!info.isElse()) {
					System.out.println("Found Else If: " + info.getCondition());
					IfRegion ir = (IfRegion) info.getRegion();
					addStatistic(ir.getThenRegion(), info.getCondition(), info.isElse());
				}
				// Else Case
				else {
					System.out.println("Found Else");
					addStatistic(info.getRegion(), info.getCondition(), info.isElse());
				}
			}
		} catch (CodegenException e) {
			e.printStackTrace();
		}
	}
	
	private void addStatistic(IContainer container, IfCondition condition, boolean isElse) throws CodegenException {
		Statistic stat = new Statistic(container, isElse);
		
		stat.setNumStatements(getNumStatements(container));
		stat.setNumNestedIfStatements(rGen.getNumNestedIfConditions(container));
		stat.setInLoop(inLoop((IRegion) container));
		
		List<Compare> comparisons = getCompareList(condition);
		stat.setComparisonList(comparisons);

		for (Compare compare : comparisons) {
			InsnArg arg = compare.getA();
			InsnArg arg2 = compare.getB();

			//getVariables(arg);
			//getVariables(arg2);
		}
		
		statistics.add(stat);
	}
	
	/*
	private void getVariables(InsnArg arg) throws CodegenException {
		if (arg.isRegister()) {
			RegisterArg rArg = (RegisterArg) arg;
			String name = rArg.getName();
			if (name == null) {
				name = "r" + rArg.getRegNum();
			}
		} else if (arg.isLiteral()) {
			LiteralArg lArg = (LiteralArg) arg;
			System.out.println(TypeGen.literalToString(lArg.getLiteral(), arg.getType(), mth.getMethodNode()));
		} else if (arg.isInsnWrap()) {
			System.out.println("Instruction Wrap");
			InsnWrapArg iwArg = (InsnWrapArg) arg;
			
			Iterator it = iwArg.getWrapInsn().getArguments().iterator();
			getVariables(iwArg.getWrapInsn().getResult());
			while(it.hasNext()) {
				InsnArg a = (InsnArg) it.next();
				getVariables(a);
			}
			//Flags flag = wrap ? Flags.BODY_ONLY : Flags.BODY_ONLY_NOWRAP;
			//makeInsn(((InsnWrapArg) arg).getWrapInsn(), code, flag);
		} else if (arg.isNamed()) {
			//code.add(((Named) arg).getName());
		} else if (arg.isField()) {
			//FieldArg f = (FieldArg) arg;
			//if (f.isStatic()) {
			//	staticField(code, f.getField());
			//} else {
			//	instanceField(code, f.getField(), f.getInstanceArg());
			//}
		} else {
			throw new CodegenException("Unknown arg type " + arg);
		}
	}
	*/
	
	private int getNumStatements(IContainer container) throws CodegenException {
		int numStatements = 0;
		for (IBlock block : rGen.getBlocksForRegion(container)) {
			for (InsnNode node : block.getInstructions()) {
				numStatements++;
			}
		}
		return numStatements;
	}
	
	// Returns a new list if condition specified is null
	public List<Compare> getCompareList(IfCondition condition) {
		if (condition == null)
			return new ArrayList<Compare>();
		
		conditionCompares.clear();
		traverseCondition(condition);
		return conditionCompares;
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
			return getNestingLevel(region.getParent()) + 1;
		}
	}
	
	private void traverseCondition(IfCondition condition) {
		switch (condition.getMode()) {
		case COMPARE:
			conditionCompares.add(condition.getCompare());
			break;
		case TERNARY:
			traverseCondition(condition.first());
			traverseCondition(condition.second());
			traverseCondition(condition.third());
			break;
		case NOT:
			break;
		case AND:
		case OR:
			String mode = condition.getMode() == Mode.AND ? " && " : " || ";
			for (IfCondition c : condition.getArgs()) {
				traverseCondition(c);
			}
			break;

		default:
		}
	}
}
