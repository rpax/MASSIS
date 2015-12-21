package cz.cuni.amis.pogamut.sposh.exceptions;

import cz.cuni.amis.pogamut.sposh.elements.ParseException;
import java.text.MessageFormat;

/**
 * Thrown when trying to add element into the posh tree, but the tree already
 * contains element with the same name.
 * @author Honza
 */
public class DuplicateNameException extends ParseException {

    public static DuplicateNameException create(String name) {
        String msg = MessageFormat.format("Another element is already using ''{0}''.", name);
        return new DuplicateNameException(msg);
    }

    public DuplicateNameException(String message) {
        super(message);
    }
}
