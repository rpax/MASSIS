package cz.cuni.amis.pogamut.sposh.exceptions;

import cz.cuni.amis.pogamut.sposh.elements.ParseException;
import java.text.MessageFormat;

/**
 * When there is a cycle in the posh tree, this exception should be thrown.
 * Use in methods adding elements to the tree. Cycle in the tree is
 * a sequence of elements, that link to each other and last one links to the first one
 * (e.g. (AP ap1 ap2) (AP ap2 ap1))
 * @author Honza
 */
public class CycleException extends ParseException {
    
    /**
     * Create a new exception that specifies which name caused the cycle.
     */
    public static CycleException createFromName(String name) {
        String msg = MessageFormat.format("Adding an element with name '{0}' would cause a cycle", name);
        return new CycleException(msg);
    }
    
    public CycleException(String message) {
        super(message);
    }
}
