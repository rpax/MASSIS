package cz.cuni.amis.pogamut.sposh.exceptions;

import cz.cuni.amis.pogamut.sposh.elements.PoshElement;
import java.text.MessageFormat;

/**
 * The method was passed unexpected {@link PoshElement} as argument.
 * @author HonzaH
 */
public class UnexpectedElementException extends RuntimeException {

    /**
     * Create new unexpected element exception, caller got unexpected {@link PoshElement}.
     * @param element
     * @return created exception
     */
    public static UnexpectedElementException create(PoshElement element) {
        String elementName = element != null ? element.getClass().getCanonicalName() : "'null'";
        String msg = MessageFormat.format("Unexpected lap element ({0}).", elementName);
        return new UnexpectedElementException(msg);
    }

    public UnexpectedElementException(String message) {
        super(message);
    }
    
}
