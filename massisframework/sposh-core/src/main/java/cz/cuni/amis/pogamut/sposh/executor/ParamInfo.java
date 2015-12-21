package cz.cuni.amis.pogamut.sposh.executor;

import cz.cuni.amis.pogamut.sposh.elements.EnumValue;
import cz.cuni.amis.pogamut.sposh.elements.Result;
import cz.cuni.amis.pogamut.sposh.exceptions.FubarException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Object for storing information about one parameter of a parametrized
 * primitive.
 *
 * The class is immutable and can be sorted according to the name of the
 * parameter.
 *
 * @see ParamsAction Parametrized action
 * @see ParamsSense Parametrized sense
 * @author Honza
 */
public class ParamInfo implements Comparable<ParamInfo> {

    /**
     * Enum of all possible types allowed as the parameters of methods in a
     * parametrized primitive.
     */
    public enum Type {

    	BOOLEAN(true, boolean.class, java.lang.Boolean.class),
        INT(true, int.class, java.lang.Integer.class),
        DOUBLE(true, double.class, java.lang.Double.class),
        STRING(true, java.lang.String.class),
        ENUM(false, java.lang.Enum.class);
        private final boolean exactType;
        private final Class[] javaTypes;

        private Type(boolean exactType, Class... javaTypes) {
            this.exactType = exactType;
            this.javaTypes = javaTypes;
        }

        /**
         * Find the {@link Type} for passed java type.
         *
         * @param javaType FQN of a java type for which we want to find param
         * type.
         * @return found type
         * @throws IllegalArgumentException If passed typeName doesn't match any
         * type.
         */
        public static Type findType(String javaType) {
            for (Type type : Type.values()) {
                if (type.representsType(javaType)) {
                    return type;
                }
            }
            throw new IllegalArgumentException("Unable to find " + Type.class.getSimpleName() + " for " + javaType);
        }

        /**
         * Does the passed javaType matches the type?
         *
         * @param testedJavaType Type used for maching (e.g. "int" or
         * "java.lang.String")
         * @return True if the {@link Type} matches the javaType, false
         * otherwise.
         */
        boolean representsType(String testedJavaType) {
            if (exactType) {
                for (Class javaType : javaTypes) {
                    if (javaType.getName().equals(testedJavaType)) {
                        return true;
                    }
                }
            } else {
                try {
                    Class testedType = Class.forName(testedJavaType);
                    for (Class javaType : javaTypes) {
                        if (javaType.isAssignableFrom(testedType)) {
                            return true;
                        }
                    }
                } catch (ClassNotFoundException ex) {
                    return false;
                }
            }

            return false;
        }
    }
    /**
     * Name of the parameter, i.e. the string in the {@link Param} annotation.
     */
    public final String name;
    /**
     * Type of parameter.
     */
    public final Type type;
    /**
     * The class of the parameter.
     */
    public final String clsName;

    /**
     * Create new info object about parameter of a parametrized method.
     *
     * @param name Name of a parameter (from {@link Param) annotation.
     * @param type Type of parameter that is being accepted.
     * @param clsName Class of the parameter, but as string, not the actual
     * class, e.g. "java.lang.String"
     */
    public ParamInfo(String name, Type type, String clsName) {
        assert name != null;
        this.name = name;
        this.type = type;
        this.clsName = clsName;
    }

    @Override
    public int compareTo(ParamInfo o) {
        return name.compareTo(o.name);
    }

    /**
     * Is passed value assignable to the parameter? This method is not called
     * during runtime.
     *
     * @param value See {@link Type} for list, e.g. instance of String,
     * EnumValue, int, Integer, Double,...
     * @return True if value can be assigned to specified {@link #type}.
     */
    public boolean isValueAssignable(Object value) {
        if (value == null) {
            return true;
        }

        if (value instanceof EnumValue) {
            EnumValue enumValue = (EnumValue) value;
            return (this.clsName.equals(enumValue.getEnumFQN()));
        } else {
            String javaClass = value.getClass().getName();
            return type.representsType(javaClass);
        }
    }
}
