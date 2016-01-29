package cz.cuni.amis.pogamut.sposh.elements;

import cz.cuni.amis.pogamut.shady.ArgString;
import cz.cuni.amis.pogamut.sposh.executor.ParamInfo;
import java.lang.reflect.Array;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class representing value and its properties in posh plan. Value in plan can
 * be e.g boolean or string and they have some properties. Methods of this class
 * take an object and perform some test on it (e.g. if you pass {@link Double}
 * with value 0.0 to {@link #isFalse(java.lang.Object) }, it will return true).
 *
 * Most important part of this class are methods {@link #compare(java.lang.Object, java.lang.Object)
 * } and {@link #equal(java.lang.Object, java.lang.Object) }.
 *
 * @author Honza
 */
public class Result {

    /**
     * Is the value false? null is false Boolean false is false Number 0 is
     * false Arrays of size 0 is false, otherwise value is true
     *
     * @see http://docs.python.org/library/stdtypes.html#truth-value-testing
     * @return true if value is null, value convertable to number and 0
     */
    public static boolean isFalse(Object value) {
        if (value == null) {
            return true;
        }
        if (isBoolean(value)) {
            return !getBoolean(value);
        }
        if (isNumber(value)) {
            return getNumber(value).doubleValue() == 0;
        }
        if (value.getClass().isArray()) {
            return Array.getLength(value) == 0;
        }
        return false;
    }

    /**
     * Is value true? Basically negate {@link Result#isFalse(java.lang.Object)
     * }.
     *
     * @param value value for which we want the information.
     */
    public static boolean isTrue(Object value) {
        return !isFalse(value);
    }

    public static boolean isNumber(Object value) {
        return value instanceof Number;
    }

    public static Number getNumber(Object value) {
        assert isNumber(value);
        return (Number) value;
    }

    /**
     * Is value instance of {@link Boolean} class?
     *
     * @param value value to be tested
     * @return true if it is instance of {@link Boolean}
     */
    public static boolean isBoolean(Object value) {
        return value instanceof Boolean;
    }

    /**
     * Get boolean value from {@link Boolean} class.
     *
     * @param value (value must be instance of {@link Boolean) class.
     * @return value of boolean.
     */
    public static boolean getBoolean(Object value) {
        assert isBoolean(value);
        return ((Boolean) value).booleanValue();
    }

    /**
     * Parse string from posh plan and convert it to object. Rules of parsing:
     * "nil" - null "true"/"false" - boolean int number - integer double number
     * - double otherwise string
     *
     * @param valueString string that will be parsed to object NOT NULL.
     * @return created object
     */
    public static Object parseValue(String valueString) throws ParseException {
        // TODO: Maytbe reuse the parser instead of duplicating the code
        assert valueString != null;

        if ("nil".equalsIgnoreCase(valueString)) {
            return null;
        }

        if (valueString.equalsIgnoreCase(Boolean.TRUE.toString())) {
            return Boolean.TRUE;
        }

        if (valueString.equalsIgnoreCase(Boolean.FALSE.toString())) {
            return Boolean.FALSE;
        }

        try {
            return Integer.parseInt(valueString);
        } catch (NumberFormatException ex) {
        }

        try {
            return Double.parseDouble(valueString);
        } catch (NumberFormatException ex) {
        }

        if (valueString.length() >= 2 && valueString.startsWith("\"") && valueString.endsWith("\"")) {
            String unquotedString = valueString.substring(1, valueString.length() - 1);
            try {
                return ArgString.unescape(unquotedString);
            } catch (cz.cuni.amis.pogamut.shady.ParseException ex) {
                throw new ParseException(ex.getMessage());
            }
        }
        if (valueString.length() >= 2 && valueString.startsWith("'")) {
            String enumString = valueString.substring(1);
            if (!enumString.matches("[a-zA-Z_]([a-zA-Z_0-9])*(.[a-zA-Z_]([a-zA-Z_0-9])*)*")) {
                throw new ParseException(enumString + " is not a valid enum name.");
            }
            return new EnumValue(enumString);
        }
        throw new ParseException("No good type from " + valueString);
    }

    /**
     * Get numerical representation for object derived from class {@link Number}
     * and from {@link Boolean} (true = 1, false = 0)
     *
     * @param value
     * @return numerical value of value.
     */
    public static double getNumerical(Object value) {
        if (isBoolean(value)) {
            return getBoolean(value) ? 1 : 0;
        }

        return getNumber(value).doubleValue();
    }

    /**
     * Is value numerical(either number or boolean)?
     *
     * @param value value to check if it is numerical
     */
    public static boolean isNumerical(Object value) {
        return isNumber(value) || isBoolean(value);
    }

    /**
     * Are two operands equal? Comparison is based on rules outlines in
     * http://docs.python.org/reference/expressions.html#notin
     *
     * @param operand1
     * @param operand2
     * @return
     */
    public static boolean equal(Object operand1, Object operand2) {
        // I can compare them numericly
        if (isNumerical(operand1) && isNumerical(operand2)) {
            double op1 = getNumerical(operand1);
            double op2 = getNumerical(operand2);
            return op1 == op2;
        }
        // otherwise use standard equals
        return operand1 == null ? operand2 == null : operand1.equals(operand2);
    }

    /**
     * What is the comparison of operand1 and operand2?
     *
     * @param operand1
     * @param operand2
     * @return a negative integer, zero, or a positive integer as this object is
     * less than, equal to, or greater than the specified object.
     */
    public static int compare(Object operand1, Object operand2) {
        // I can compare them numericly
        if (isNumerical(operand1) && isNumerical(operand2)) {
            double op1 = getNumerical(operand1);
            double op2 = getNumerical(operand2);
            int sig = (int) Math.signum(op1 - op2);
            return sig;
        }
        // otherwise use comparable
        if (operand1 == null) {
            if (operand2 == null) {
                return 0;
            }
            // two objects are not equal, but HOW? we have null and something.
            throw new IllegalArgumentException("I can't compare " + operand1 + " with " + operand2);
        }

        Comparable op1 = (Comparable) operand1;
        Comparable op2 = (Comparable) operand2;

        return op1.compareTo(op2);
    }

    /**
     * Get string representation of the value that can be used in the lap plan.
     * Basically opposite of {@link Result#parseValue(java.lang.String) }.
     *
     * @param value value to convret
     * @return string representation that can be used in the plan
     */
    public static String toLap(Object value) {
        if (value == null) {
            return "nil";
        }
        if (value instanceof Character) {
            return "\"" + value.toString() + "\"";
        }
        if (value instanceof String) {
            return "\"" + value.toString() + "\"";
        }
        if (value instanceof EnumValue) {
            return '\'' + ((EnumValue) value).getName();
        }

        return value.toString();
    }
    /**
     * Regexp for parameter name be same as <VARIABLE> PoshParser
     */
    public final static String variableNameRegexp = "\\$[a-zA-Z]([_\\-a-zA-Z0-9])*";

    /**
     * Check if passed string is variable name. Variable names start with $ and
     * are followed by characters. Should be same as parser <tt>VARIABLE<tt>
     * terminal.
     *
     * @param possibleVariableName Tested string.
     * @return true if passed string is varaible name. False otherwise.
     */
    public static boolean isVariableName(String possibleVariableName) {
        return possibleVariableName.matches(variableNameRegexp);
    }
}
