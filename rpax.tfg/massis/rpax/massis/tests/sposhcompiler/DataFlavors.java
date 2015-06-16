package rpax.massis.tests.sposhcompiler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rpax.massis.ia.sposh.SPOSHLogicController;
import cz.cuni.amis.pogamut.sposh.elements.ActionPattern;
import cz.cuni.amis.pogamut.sposh.elements.Arguments;
import cz.cuni.amis.pogamut.sposh.elements.Arguments.Argument;
import cz.cuni.amis.pogamut.sposh.elements.Competence;
import cz.cuni.amis.pogamut.sposh.elements.CompetenceElement;
import cz.cuni.amis.pogamut.sposh.elements.DriveElement;
import cz.cuni.amis.pogamut.sposh.elements.FormalParameters;
import cz.cuni.amis.pogamut.sposh.elements.FormalParameters.Parameter;
import cz.cuni.amis.pogamut.sposh.elements.PoshElement;
import cz.cuni.amis.pogamut.sposh.elements.PoshPlan;
import cz.cuni.amis.pogamut.sposh.elements.PrimitiveCall;
import cz.cuni.amis.pogamut.sposh.elements.Sense;
import cz.cuni.amis.pogamut.sposh.elements.Trigger;
import cz.cuni.amis.pogamut.sposh.elements.TriggeredAction;
 
public class DataFlavors {

	public static void main(String[] args) {
		try
		{
			final String POSH_PLAN_PATH = "src/main/resources/sposh/plan/CaseStudy1.lap";

			PoshPlan plan = SPOSHLogicController
					.loadPoshPlanFromFile(POSH_PLAN_PATH);
			
			for (DriveElement drive : plan.getDriveCollection().getDrives())
			{
				
				System.out.println("void execute_"+drive.getName().replace("-", "_")+"()");

				System.out.println(processAction(plan, drive.getAction()).transform(1));

			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}

	public static TComplexAction processAction(PoshPlan plan,
			TriggeredAction ta, int level, Map<String, Object> paramMap) {
		PrimitiveCall actionCall = ta.getActionCall();
		String name = actionCall.getName();
		Map<String, Object> map2 = mergeAndCopy(actionCall.getParameters(),
				paramMap);

		if (plan.isAD(name))
		{
			throw new UnsupportedOperationException("AD not supported");
		}
		else if (plan.isAP(name))
		{
			return processAP(plan, plan.getAP(name), level, map2);
		}
		else if (plan.isC(name))
		{
			return processC(plan, plan.getC(name), level, map2);
		}
		else
		{
			TComplexAction cAction = new TComplexAction();
			cAction.addAction(new TPrimitiveAction(name, map2));
			return cAction;
		}

	}

	public static HashMap<String, Object> mergeAndCopy(Arguments args,
			Map<String, Object> map) {
		HashMap<String, Object> map2 = new HashMap<>(map);

		for (Argument argument : args)
		{
			if (argument.getParameterVariable() != null)
			{
				map2.put(argument.getName(),map.get(argument.getParameterVariable()));
			}
			else
			{
				map2.put(argument.getName(), argument.getValue());
			}
		}
		return map2;
	}

	public static HashMap<String, Object> mergeAndCopy(
			FormalParameters formalParams, Map<String, Object> map) {
		HashMap<String, Object> map2 = new HashMap<>(map);

		for (Parameter param : formalParams)
		{
			
			if (map.get(param.getName())==null)
			{
				
				if (param.getDefaultValue() != null)
				{
					//System.err.println(param.getName()+","+map.get(param.getDefaultValue()));
					map2.put(param.getName(),param.getDefaultValue());
				}
			}
		}
		return map2;
	}

	public static TComplexAction processAction(PoshPlan plan,
			TriggeredAction ta) {
		return processAction(plan, ta, 0, new HashMap<String, Object>());
	}

	public static TComplexAction processC(PoshPlan plan, Competence c,
			int level, Map<String, Object> paramMap) {
		// printlvl("CE " + c.getName() + "(" + paramMap + ")", level);
		TComplexAction cAction = new TComplexAction();

		for (CompetenceElement ce : c.getChoices())
		{
			TComplexAction ceAction = new TComplexAction();

			ceAction.addSenses(processTrigger(plan, ce.getTrigger(), level + 1,
					mergeAndCopy(c.getParameters(), paramMap)));
			ceAction.addAction(processAction(plan, ce.getAction(), level + 1,
					mergeAndCopy(c.getParameters(), paramMap)));
			cAction.addAction(ceAction);

		}
		return cAction;
	}

	public static List<TPrimitiveSense> processTrigger(PoshPlan plan,
			@SuppressWarnings("rawtypes") Trigger<? extends PoshElement> trigger, int level,
			Map<String, Object> paramMap) {

		List<TPrimitiveSense> cSenses = new ArrayList<TPrimitiveSense>();
		for (Sense sense : trigger)
		{
			PrimitiveCall actionCall = sense.getCall();
			
			String name = actionCall.getName();
			cSenses.add(new TPrimitiveSense(name, mergeAndCopy(actionCall.getParameters(),paramMap)));
		}
		return cSenses;

	}

	public static TComplexAction processAP(PoshPlan plan, ActionPattern ap,
			int level, Map<String, Object> paramMap) {

		TComplexAction cAction = new TComplexAction();
		for (TriggeredAction ta : ap.getActions())
		{
			
			cAction.addAction(processAction(plan, ta, level + 1, mergeAndCopy(ta.getActionCall().getParameters(), paramMap)));
		}
		return cAction;
	}

}
