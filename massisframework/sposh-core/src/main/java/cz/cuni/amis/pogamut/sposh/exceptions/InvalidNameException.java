package cz.cuni.amis.pogamut.sposh.exceptions;

import cz.cuni.amis.pogamut.sposh.elements.ParseException;
import cz.cuni.amis.pogamut.sposh.elements.PoshElement;
import java.text.MessageFormat;

/**
 * Thrown when name in the lap plan is not valid (example: it includes
 * whitespaces). Names of {@link PoshElement data nodes} in the lap plan must be
 * parsable (since we are putting them into a text file), so when user tries to
 * pass incorrect name (e.g. starts with number, has whitespaces, braces...)
 *
 * XXX: How exactly are names defined? Certainly not java identifier (thx2 '-'),
 * just regexp in the parser?
 *
 * @author Honza
 */
public class InvalidNameException extends ParseException{

    /**
     * Create an exception about invalid name
     * @param name invalid name
     * @return created exception
     */
    public static InvalidNameException create(String name) {
        String msg = MessageFormat.format("Name ''{0}'' is not valid.", name);
        return new InvalidNameException(msg);
    }

    public InvalidNameException(String message) {
        super(message);
    }

}
