package rpax.massis.tests.sposhcompiler;

import java.util.Map;

import cz.cuni.amis.pogamut.sposh.executor.ActionResult;
 
public class TPrimitiveAction extends TPrimitive {
	

	public TPrimitiveAction(String name, Map<String, Object> map2) {
		super(name, map2);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(name).append("(").append(params).append(");");
		return builder.toString();
	}

	
	@Override
	public String getBaseMethodName() {
		//private static final String RUN_METHOD_NAME = "run";
		return "run";
	}

	@Override
	public Class<?> getReturnType() {
		return ActionResult.class;
	}
	
	

	

}
