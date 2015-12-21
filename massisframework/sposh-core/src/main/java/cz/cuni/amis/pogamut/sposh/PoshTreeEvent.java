package cz.cuni.amis.pogamut.sposh;

/**
 * Enum for events that can change the POSH plan.
 * @author Honza
 */
public enum PoshTreeEvent {
	/**
	 * PoshDataNode has a new child PoshDataNode
	 */
	NEW_CHILD_NODE,
	/**
	 * PoshDataNode is the deleted node, but probably not necessary
	 */
	NODE_DELETED,
	/**
	 * Child was moved
	 * TODO: Not enough info, where was it before?
	 */
	CHILD_NODE_MOVED
}
