package cz.cuni.amis.pogamut.sposh.executor;

import cz.cuni.amis.pogamut.sposh.context.Context;
import cz.cuni.amis.pogamut.sposh.engine.VariableContext;
import cz.cuni.amis.pogamut.sposh.exceptions.MethodException;
import java.lang.reflect.InvocationTargetException;

/**
 * Base class for {@link IAction actions} that have parameters passed to them
 * using the reflection API. The actions derived directly from {@link StateAction}
 * must check that passed {@link VariableContext} contains expected parameters.
 *
 * To correctly implement action that can utilize the reflection for parameters,
 * you must derive from this class and implement three methods: <tt>public void
 * init</tt>, <tt>public ActionResult run</tt> and <tt>public void done</tt>.
 * All parameters of these methods must be annotated by the {@link Param} with
 * specified name of the variable contained in the {@link VariableContext}. Note
 * that each action can have different parameters, as long as all parameters
 * have annotations with {@link Param} and are all in the context.
 *
 * Only allowed parameters are of type {@link String}, {@link Integer} and {@link Double}.
 *
 * Example:
 * <pre>
 * class StayAction extends ParamsAction&lt;PreyContext&gt; {
 *   public StayAction(Context ctx) {
 *     super(ctx);
 *   }
 *
 *   public void init(&064;Param("$enemy") String enemy, &064;Param("$distance") Double distance) {
 *     ...
 *   }
 *
 *   public ActionResult run(&064;Param("$teamname") String teamName) {
 *     ...
 *   }
 *
 *   public void done() {
 *     ...
 *   }
 * }
 *
 * </pre>
 *
 * @see ParamsSense
 * @see Param Annotation for parameters of the methods.
 * @author Honza Havlicek
 */
public abstract class ParamsAction<CONTEXT extends Context> extends StateAction<CONTEXT> {

    private static final String INIT_METHOD_NAME = "init";
    private static final String RUN_METHOD_NAME = "run";
    private static final String DONE_METHOD_NAME = "done";
    private final ParamsMethod<Void> initMethod;
    private final ParamsMethod<ActionResult> runMethod;
    private final ParamsMethod<Void> doneMethod;

    public ParamsAction(CONTEXT ctx) {
        super(ctx);

        initMethod = new ParamsMethod<Void>(getClass(), INIT_METHOD_NAME, Void.TYPE);
        runMethod = new ParamsMethod<ActionResult>(getClass(), RUN_METHOD_NAME, ActionResult.class);
        doneMethod = new ParamsMethod<Void>(getClass(), DONE_METHOD_NAME, Void.TYPE);
    }

    @Override
    public final void init(VariableContext params) {
        try {
            initMethod.invoke(this, params);
        } catch (InvocationTargetException ex) {
            throw new MethodException("",ex.getTargetException());
        }
    }

    @Override
    public final ActionResult run(VariableContext params) {
        try {
            return runMethod.invoke(this, params);
        } catch (InvocationTargetException ex) {
            throw new MethodException("",ex.getTargetException());
        }
    }

    @Override
    public final void done(VariableContext params) {
        try {
            doneMethod.invoke(this, params);
        } catch (InvocationTargetException ex) {
            throw new MethodException("",ex.getTargetException());
        }
    }
}
