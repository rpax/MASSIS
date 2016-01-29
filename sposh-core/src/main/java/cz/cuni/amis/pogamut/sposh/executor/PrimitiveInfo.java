package cz.cuni.amis.pogamut.sposh.executor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Optional, use this annotation for the some implementation of {@link ISense} and
 * {@link IAction}, this annotation specifies the name and description of primitive.
 * 
 * If not specified, nothing horrible will happen, but it is not user friendly,
 * because the name of the implementing class will be used. Try to keep it unique, not
 * required, only better for humans working with editor.
 * @author Honza
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface PrimitiveInfo {
    /**
     * Name of the primitive displayed in the editor and debugger.
     * @return Human readable name of the primitive
     */
    String name();
    /**
     * More precise description of the primitive, should describe what exactly
     * does it do and what are its parameters. Displayed in the Shed palette.
     */
    String description();
    /**
     * Tags of the primitive (e.g. "movement" for action Turn). Should be used
     * during searchng, but not currently used.
     *
     * @return List of tags of this primitive
     */
    String[] tags() default {};
}