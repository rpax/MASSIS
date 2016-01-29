package cz.cuni.amis.pogamut.sposh.executor;

import cz.cuni.amis.pogamut.sposh.engine.VariableContext;
import java.io.PrintStream;
import java.util.HashMap;

/**
 * Very simple executor for plans that will print name of primitive, that is supposed
 * to execute, to specified stream.
 * <p/>
 * There are two lists of primitives, one will always return true, other will always return false.
 * Both will be printed. Primitives that are not in either of lists will cause exception
 * {@link IllegalArgumentException}.
 * @author Honza
 */
public class PrintWorkExecutor implements IWorkExecutor {

    /**
     * Stream into which we will write called primitives.
     */
    private PrintStream stream;
    /**
     * Map that stores return value of primitives.
     */
    private HashMap<String, Boolean> map = new HashMap<String, Boolean>();

    /**
     * Create worker that will print name of primitives into specified stream.
     * @param succeed list of non-zero length strings with names of primitives returning true
     * @param fail list of non-zero length strings with names of primitives returning false
     * @param stream stream to write into
     * @throws IllegalArgumentException If some primitive is specified twice,
     */
    public PrintWorkExecutor(String[] succeed, String[] fail, PrintStream stream) {
        this.stream = stream;

        addPrimitives(succeed, Boolean.TRUE);
        addPrimitives(fail, Boolean.FALSE);
    }

    /**
     * Add primitives to the map with specified return value. 
     * @param primitives list of primitives we want to add.
     * @param value what value should all primitives return
     * @throws IllegalArgumentException If some primitive is specified twice,
     */
    public synchronized void addPrimitives(String[] primitives, Boolean value) {
        for (String primtive : primitives) {
            if (map.put(primtive, value) != null)
                throw new IllegalArgumentException("Primitive \"" + primtive + "\" has already been specified.");
        }
    }

    /**
     * Create worker that will print name of primitives into {@link System#out}.
     */
    public PrintWorkExecutor(String[] succeed, String[] fail) {
        this(succeed, fail, System.out);
    }
    
    private Object executePrimitive(String primitive, VariableContext ctx) {
        Boolean value = map.get(primitive);
        // no mapping for specified primitive
        if (value == null) {
            throw new IllegalArgumentException("Primitive \"" + primitive + "\" is not specified in the worker.");
        }
        stream.println(getClass().getSimpleName() + ": execute \"" + primitive + "\"" + ctx.toString() + " -> " + value);
        return value;
    }
    
    @Override
    public synchronized Object executeSense(String primitive, VariableContext ctx) {
        return executePrimitive(primitive, ctx);
    }
    
    @Override
    public synchronized ActionResult executeAction(String primitive, VariableContext ctx) {
    	Object result = executePrimitive(primitive, ctx);
    	if (result instanceof ActionResult) return (ActionResult)result;
        return ActionResult.FINISHED;
    }
    
}
