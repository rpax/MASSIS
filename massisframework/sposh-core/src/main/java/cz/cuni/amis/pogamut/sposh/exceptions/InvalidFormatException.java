package cz.cuni.amis.pogamut.sposh.exceptions;

/**
 * This exception is thrown when string passed to the method is not in the
 * expected format.
 *
 * @author Honza Havlicek
 */
public class InvalidFormatException extends Exception {

    public InvalidFormatException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidFormatException(String message) {
        super(message);
    }
}
