package cz.cuni.amis.pogamut.sposh.engine;

import cz.cuni.amis.pogamut.sposh.elements.Arguments;
import cz.cuni.amis.pogamut.sposh.elements.LapPath;
import cz.cuni.amis.pogamut.sposh.elements.PrimitiveCall;
import cz.cuni.amis.pogamut.sposh.elements.Result;
import cz.cuni.amis.pogamut.sposh.elements.Sense;
import cz.cuni.amis.pogamut.sposh.elements.Sense.Predicate;
import cz.cuni.amis.pogamut.sposh.executor.IWorkExecutor;

/**
 * Class for storing result of one sense evaluation. This stores value returned 
 * by a sense, not the result of sense comparison with a value.
 * 
 * E.g.: we have sense "health" and {@link PrimitiveCall} "(health &lt; 25)"; the sense
 * will be evaluated to 56, but the sensecall is false. The sense result is 
 * therefore 56.
 */
class SenseResult {

    /** Name of sense that was evaluated */
    public final String name;
    /** Was this sense successful? */
    private final boolean success;

    /**
     * Create new result of sense with some value
     * @param name name of sense that was fired
     * @param success was the sense successful?
     */
    SenseResult(String name, boolean success) {
        this.name = name;
        this.success = success;
    }

    /**
     * Check is this sense was successful. This is different from value of 
     * sensecall.
     * @return if this sense was evaluated to be true
     */
    public boolean wasSuccessful() {
        return success;
    }
}

/**
 * Evaluate the primitive (either sesnee or action).
 * <p>
 * Possible format of the primitive (in the plan):
 * <ul>
 *  <li>primitive</li>
 *  <li>(primitive($variable,...))</li>
 *  <li>(primitive($variable,...) value)</li>
 *  <li>(primitive($variable,...) value predicate)</li>
 *  <li>(primitive)</li>
 *  <li>(primitive value)</li>
 *  <li>(primitive value predicate)</li>
 * </ul>
 * @author Honza
 */
final class SenseExecutor extends AbstractExecutor {

    private PrimitiveCall senseCall;
    private Predicate predicate;
    /**
     * When evaluating the sense result, should I compare it with the operand?
     * XXX: if operand is true and predicate ==, don't compare
     */
    private final boolean compare = true;
    private Object operand;

    /**
     * Create new sense executor.
     * @param sense sense that is going to be executed
     * @param ctx variable context for the sense
     * @param log logger to record actions of this executor
     */
    SenseExecutor(Sense sense, LapPath sensePath, VariableContext ctx, EngineLog log) {
        super(sensePath, ctx, log);

        assert sensePath.traversePath(sense.getRootNode()) == sense;
        
        senseCall = sense.getCall();
        predicate = sense.getPredicate();
        operand = sense.getOperand();
    }

    /**
     * Evaluate the primitive
     * @return Result object with information about sense evaluation
     */
    public SenseResult fire(IWorkExecutor workExecuter) {
        engineLog.fine("Fire: " + toString());
        engineLog.pathReached(path);
        
        String senseName = senseCall.getName();

        Object primitiveResult = workExecuter.executeSense(senseName, new VariableContext(ctx, senseCall.getParameters()));

        boolean res;
        // If there is nothing to compare with, use only result.
        if (!compare) {
            res = Result.isTrue(primitiveResult);
        } else {
            res = evaluateComparison(primitiveResult, predicate, operand);
        }
        return new SenseResult(senseName, res);
    }

    /**
     * Compare two values.
     * <p>
     * This thing deserves a lot of TLC. I try to abide to python convention:
     *
     * http://docs.python.org/reference/expressions.html#notin
     * http://docs.python.org/library/stdtypes.html
     *
     * <p>
     * numbers: numint, numfloat
     *
     * @param value1 nonempty value
     * @param predicate predicate that will be used for comparison
     * @param value2 nonempty value
     * @return
     */
    static boolean evaluateComparison(Object operand1, Predicate predicate, Object operand2) {
        String comparison = operand1 + " " + predicate + " " + operand2;
        switch (predicate) {
            case DEFAULT:  // default predicate is equal
            case EQUAL:
                return Result.equal(operand1, operand2);
            case NOT_EQUAL:
                return !Result.equal(operand1, operand2);
            case LOWER:
                return Result.compare(operand1, operand2) < 0;
            case GREATER:
                return Result.compare(operand1, operand2) > 0;
            case LOWER_OR_EQUAL:
                return Result.equal(operand1, operand2) || Result.compare(operand1, operand2) < 0;
            case GREATER_OR_EQUAL:
                return Result.equal(operand1, operand2) || Result.compare(operand1, operand2) > 0;
            default:
                throw new IllegalArgumentException("Predicate operation \"" + predicate + "\" is implemented.");
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("SenseExecutor[(");

        sb.append(senseCall.getName());
        sb.append('(');
        boolean first = true;
        for (Arguments.Argument parameter : senseCall.getParameters()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append(parameter.getParameterName());
            first = false;
        }
        sb.append(')');

        if (compare) {
            sb.append(' ');
            sb.append(predicate);

            sb.append(' ');
            sb.append(operand);
        }

        sb.append(')');
        sb.append(']');
        return sb.toString();
    }
}
