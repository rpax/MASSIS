package cz.cuni.amis.pogamut.sposh.elements;

/**
 * This class represents reference to some other element. The reference consists from
 * <ul>
 * <li>name of referenced element - action, sense, AP or C</li>
 * <li>list of argument</li>
 * <ul>
 */
public final class PrimitiveCall {

	private final String name;
	private final Arguments parameters;

	/**
	 * Create reference without arguments
	 * 
	 * @param name Name of the referenced element.
	 */
	protected PrimitiveCall(String name) {
		this.name = name;
		this.parameters = new Arguments();
	}

    /**
     * Copy constructor.
     * @param original Original primitive call from which we take the data.
     */
	protected PrimitiveCall(PrimitiveCall original) {
		this.name = original.getName();
		this.parameters = new Arguments(original.getParameters());
	}
    
    /**
     * Create reference to element.
     * @param name Name of referenced element.
     * @param arguments Arguments passed to the referenced element. 
     */
	protected PrimitiveCall(String name, Arguments arguments) {
		this.name = name;
		this.parameters = new Arguments(arguments);
	}

    /**
     * @return Name of referenced element
     */
	public String getName() {
		return name;
	}

	/**
	 * @return unmodifiable list of parameters in correct order.
	 */
	public Arguments getParameters() {
		return parameters;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(name);
		if (!parameters.isEmpty()) {
			sb.append('(');
			sb.append(parameters.toString());
			sb.append(')');
		}
		return sb.toString();
	}
}