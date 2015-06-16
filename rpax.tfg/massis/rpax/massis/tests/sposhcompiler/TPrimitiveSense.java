package rpax.massis.tests.sposhcompiler;

import java.util.Map;

public class TPrimitiveSense extends TPrimitive{

	public TPrimitiveSense(String name, Map<String, Object> map2) {
		super(name, map2);
	}

	@Override
	public String getBaseMethodName() {
		return "query";
	}

	@Override
	public Class<?> getReturnType() {
		return Boolean.class;
	}

	

	
	
}
