package cz.cuni.amis.pogamut.sposh.exceptions;

/**
 * This exception is thrown when some call has an argument with value of
 * parameter and the parameter is missing.
 *
 * E.g. (AP vars($a=1) ( do-something($b))), but $b is not one of parameters of
 * node AP.
 *
 * @author Honza
 */
public class MissingParameterException extends Exception {

    /**
     * Name of missing parameter
     */
    private final String parameterName;

    /**
     * Create new exception about missing parameter.
     *
     * @param parameterName Name of missing parameter.
     */
    public MissingParameterException(String parameterName) {
        super("Paramater \"" + parameterName + "\" is required, but is not present.");
        this.parameterName = parameterName;
    }

    public String getParameterName() {
        return parameterName;
    }
}
