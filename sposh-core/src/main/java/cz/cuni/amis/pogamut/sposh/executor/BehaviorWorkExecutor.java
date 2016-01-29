package cz.cuni.amis.pogamut.sposh.executor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cz.cuni.amis.pogamut.sposh.JavaBehaviour;
import cz.cuni.amis.pogamut.sposh.SPOSHAction;
import cz.cuni.amis.pogamut.sposh.SPOSHSense;
import cz.cuni.amis.pogamut.sposh.engine.PoshEngine;
import cz.cuni.amis.pogamut.sposh.engine.VariableContext;

/**
 * Executor for posh plans that will execute annotated methods on the object.
 * <p/>
 * You put behavior objects into this {@link IWorkExecutor} that have some methods
 * annotated with {@link SPOSHAction} or {@link SPOSHSense}. Names of these methods
 * are used stored as primitives. There can't be two behavior methods with same
 * names, (e.g. testMethod() from behavior object A and testMethod() from object B or
 * testMethod(String) from object A). If such sitation occurs,
 * {@link BehaviorWorkExecutor#addBehavior(java.lang.Object) } will throw
 * {@link IllegalArgumentException}.
 * <p/>
 * When {@link PoshEngine} asks to execute some primitive, this executor will look 
 * in list of primitives it is capable to execute (names of annotated methods 
 * from behavior objects) and if it has some behavior method with same name, it executed it
 * and returns value.
 *
 * @author Honza
 */
public class BehaviorWorkExecutor implements ILogicWorkExecutor {

    /**
     * Class for storing methods that can be executed as primitives.
     */
    protected class BehaviorMethod {
        /**
         * Create new behavior method. 
         */
        public BehaviorMethod(Object behavior, Method method) {
            this.behavior = behavior;
            this.method = method;
        }
        public final Object behavior;
        public final Method method;
    }

    /**
     * Map that maps name of primitives into behavior methods.
     */
    protected final HashMap<String, BehaviorMethod> primitives = new HashMap<String, BehaviorMethod>();
    
    protected List<JavaBehaviour> behaviors = new ArrayList<JavaBehaviour>();

    /**
     * Create BehaviorWorkExecutor with no methods.
     */
    public BehaviorWorkExecutor() {
    }
    
    /**
     * Create BehaviorWorkExecutor with primitives from behavior.
     * @param  behavior object from which we will add primitives into this worker.
     */
    public BehaviorWorkExecutor(JavaBehaviour behavior) {
        addBehavior(behavior);
    }

    /**
     * Take the behavior object, find its methods annotated with either
     * {@link SPOSHAction} or {@link SPOSHSense} and add them as primitives this
     * work executor can process.
     * @param behavior
     * @throws IllegalArgumentException if behavior contains primitive method with same name, that is
     * already contained in primitives of this worker.
     */
    public synchronized void addBehavior(JavaBehaviour behavior) {
    	behaviors.add(behavior);
        Method[] methods = behavior.getClass().getMethods();

        // filter methods to only
        for (Method method : methods) {
            boolean isAnnotated =
                method.isAnnotationPresent(SPOSHAction.class) ||
                method.isAnnotationPresent(SPOSHSense.class);

            String name = method.getName();
            if (isAnnotated) {
                if (primitives.containsKey(name)) {
                    throw new IllegalArgumentException("primitive name clash (there are at least 2 primitives with name \"" + name + "\")");
                }
                primitives.put(name, new BehaviorMethod(behavior, method));
            }
        }
    }


    /**
     * Execute the primitive. Take behavior that contains method with same name
     * as primitive and execute it, if method has as first parameter {@link VariableContext},
     * pass it, the otherwise all parameters passed to method will be null.
     *
     * @param primitive
     * @param ctx
     * @return
     * @throws IllegalArgumentException if there is no behavior method with same name as primitive
     */
    private Object executePrimitive(String primitive, VariableContext ctx) {
        BehaviorMethod behaviorMethod = primitives.get(primitive);
        if (behaviorMethod == null) {
            throw new IllegalArgumentException("Primitive \"" + primitive + "\" has no behavior method.");
        }

        Method method = behaviorMethod.method;

        // create and fill arguments for behavior method
        Object[] args = new Object[method.getParameterTypes().length];
        if (method.getParameterTypes().length > 0) {
            Class firstParameterClass = method.getParameterTypes()[0];

            if (firstParameterClass.equals(VariableContext.class)) {
                args[0] = ctx;
            }
        }
        // Try to invoke behavior method, so many things can go wrong...
        try {
            return method.invoke(behaviorMethod.behavior, args);
        } catch (IllegalAccessException ex) {
            // Wrong access for method specified.
            throw new RuntimeException(ex);
        } catch (IllegalArgumentException ex) {
            // Some things in arguments were wrong
            throw new RuntimeException(ex);
        } catch (InvocationTargetException ex) {
            // if underlaying method invoked exception
            throw new RuntimeException(ex.getCause());
        }
    }
    
    /**
     * Uses {@link BehaviorWorkExecutor#executePrimitive(String, VariableContext)}.
     */
    @Override
    public Object executeSense(String primitive, VariableContext ctx) {
        return executePrimitive(primitive, ctx);
    }
    
    /**
     * Uses {@link BehaviorWorkExecutor#executePrimitive(String, VariableContext)}.
     */
    @Override
    public ActionResult executeAction(String primitive, VariableContext ctx) {
    	return (ActionResult) executePrimitive(primitive, ctx);
    }

	@Override
	public void logicAfterPlan() {
		for (JavaBehaviour behavior : behaviors) {
			behavior.logicAfterPlan();
		}
	}

	@Override
	public void logicBeforePlan() {
		for (JavaBehaviour behavior : behaviors) {
			behavior.logicBeforePlan();
		}
	}

}
