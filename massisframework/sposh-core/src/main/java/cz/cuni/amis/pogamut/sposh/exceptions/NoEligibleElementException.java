package cz.cuni.amis.pogamut.sposh.exceptions;

/**
 * Exception that is used when I am trying to execute shade plan, but according
 * to priority and triggers of its elements, no element is eligible to be
 * selected.
 * @author Honza
 */
public class NoEligibleElementException extends Exception {

    public NoEligibleElementException(String message) {
        super(message);
    }
}
