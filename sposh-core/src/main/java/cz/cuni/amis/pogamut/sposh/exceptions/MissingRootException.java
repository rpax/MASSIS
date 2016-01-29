package cz.cuni.amis.pogamut.sposh.exceptions;

/**
 * When plan is missing root node, throw this exception.
 * @author Honza
 */
public class MissingRootException extends RuntimeException {

    public MissingRootException(String rootName) {
        super("Plan is missing root node \"" + rootName + "\".");
    }

}
