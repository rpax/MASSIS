package cz.cuni.amis.pogamut.sposh.exceptions;

import cz.cuni.amis.pogamut.sposh.executor.ParamsAction;
import cz.cuni.amis.pogamut.sposh.executor.ParamsSense;
import java.lang.reflect.InvocationTargetException;

/**
 * Unchecked exception that is used to wrap other exceptions thrown in some
 * method. Used instead of checked {@link InvocationTargetException} in the
 * parametrized primitives ({@link ParamsAction} and {@link ParamsSense}).
 *
 * @author Honza Havlicek
 */
public class MethodException extends RuntimeException {

    public MethodException(String message, Throwable cause) {
        super(message, cause);
    }

    public MethodException(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        if (getCause() != null) {
            return super.getMessage() + ' ' + getCause().getMessage();
        } else {
            return super.getMessage();
        }
    }
    
    
}
