package rpax.massis.util.io;

/**
 * Represents the state of an element in a concrete step of the simulation
 * @author rpax
 *
 */
public interface JsonState<T> {
	/**
	 * Constant for representing the class name of the elements
	 */
	public static final String KIND_KEY_NAME = "type";
	/**
	 * Restores the state of an element 
	 * @param simulationBuilding the simulation building
	 * @return the agent's state.
	 */
	public Object restore(T simulationBuilding);
	

}
