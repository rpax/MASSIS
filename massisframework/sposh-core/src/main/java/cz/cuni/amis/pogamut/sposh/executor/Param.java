package cz.cuni.amis.pogamut.sposh.executor;

import cz.cuni.amis.pogamut.sposh.engine.VariableContext;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specified the name of a variable from the {@link VariableContext} that will
 * be mapped the parameter with the annotation. This annotation is used in the
 * implementations of {@link IAction actions} and {@link ISense senses}, where
 * the engine uses reflection API to map the {@link VariableContext} variables
 * to the specified parameters of the primitives.
 * 
 * Example: 
 * <pre>
 *   public void init()
 * </pre>
 *
 * @author Honza Havlicek
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Param {

    /**
     *
     * @return
     */
    String value();
}
