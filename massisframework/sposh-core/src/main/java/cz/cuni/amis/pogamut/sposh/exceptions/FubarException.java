package cz.cuni.amis.pogamut.sposh.exceptions;

/**
 * Wrapper os {@link RuntimeException} for situations that shouldn;t ever happen
 * (e.g. I check that I have unique name, but get {@link DuplicateNameException}
 * anyway). Basically something that shouldn't EVER happen happend. This
 * exception makes it easier to find such places.
 *
 * @author HonzaH
 */
public class FubarException extends RuntimeException {

    public FubarException(Throwable cause) {
        super(cause);
    }

    public FubarException(String message, Throwable cause) {
        super(message, cause);
    }

    public FubarException(String message) {
        super(message);
    }
}
