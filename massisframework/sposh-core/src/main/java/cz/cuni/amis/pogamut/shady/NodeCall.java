package cz.cuni.amis.pogamut.shady;

import java.util.Collections;
import java.util.List;

/**
 * During the execution of the tree, we have to go from one node to another and
 * finally execute one primitive. Shady is using this class for representing
 * the call. The target is identified only by name, it can be another node,
 * action, or it doesn't have to exists at all (that will cause an exception).
 *
 * Name of the call can be same as FQN name of java class(for the most part).
 *
 * TODO: How is target determined. Probably search in order: node names, names
 *       on classpath, fqn names of classes.
 *
 * @author Honza
 */
public class NodeCall {
    private String name;
    private final List<IArgument> argsUm;

    /**
     * Create new node call.
     * @param name name of the target. 
     * @param args
     */
    public NodeCall(String name, List<IArgument> args) {
        this.name = name;
        this.argsUm = Collections.unmodifiableList(args);
    }

    /**
     * Get name of the target
     * @return target of call
     */
    public String getName() {
        return name;
    }

    /**
     * Get unmodifiable list of arguments.
     * @return list of arguments
     */
    public List<IArgument> getArgs() {
        return argsUm;
    }
}

