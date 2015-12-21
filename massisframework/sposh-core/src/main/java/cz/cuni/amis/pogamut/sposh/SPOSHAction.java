package cz.cuni.amis.pogamut.sposh;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marker that will annotate actions that should be made available for SPOSH engine
 * in the {@link JavaBehaviour behaviour} class.
 * @author Honza
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface SPOSHAction {
}
