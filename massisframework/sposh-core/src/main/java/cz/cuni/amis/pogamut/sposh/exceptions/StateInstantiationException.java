package cz.cuni.amis.pogamut.sposh.exceptions;

/**
 * During automatic instantiation of state primitives, there can happen multiple
 * exceptions that I can't bubble up nor ignore (e.g. state class can't be instantiated).
 * This is a wrapper exception class for them.
 * @author Honza
 */
public class StateInstantiationException extends RuntimeException {
    public StateInstantiationException(String msg, Exception ex) {
        super(msg, ex);
    }

    public StateInstantiationException(String msg) {
        super(msg);
    }
}
