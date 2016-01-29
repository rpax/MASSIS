package cz.cuni.amis.pogamut.sposh.executor;

import cz.cuni.amis.pogamut.sposh.context.Context;
import cz.cuni.amis.pogamut.sposh.engine.VariableContext;
import cz.cuni.amis.pogamut.sposh.exceptions.MethodException;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;

/**
 * Create new parametrized {@link ISense sense} using reflection. The senses
 * created by subclassing this class will have access to the desired parameters
 * directly from method parameters.
 *
 * In order to create new parametrized sense, subclass this class and create
 * method <tt>public RETURN query</tt>. RETURN is same type as passed to the
 * generics. All parameters of the <tt>query</tt> method must be annotated by
 * the {@link Param} and must be one types supported by POSH (currently {@link String}, {@link Double}, {@link Boolean}
 * and {@link Integer}).
 *
 * If you so desire, no parameters are necessary, thus to create parameterless
 * sense simply use <tt>public RETURN query() {...}</tt> method, but it is not
 * very useful, it is better to directly subclass {@link StateSense} that forces
 * you to implement {@link ISense#query(cz.cuni.amis.pogamut.sposh.engine.VariableContext)
 * } method.
 *
 * <b>NOTE:</b> interfaces and other mechanisms of Java are not capable to
 * ensure the presence of parametrized <tt>query</tt> method at compile time.
 * The presence of method will be checked at the moment of instantiation.
 * 
 * Example:
 * <pre>
 * public class FlagIsOnGround extends FlagSense&lt;AttackbotContext, Boolean&gt; {

    public FlagIsOnGround(AttackbotContext ctx) {
        super(ctx, Boolean.class);
    }

    public Boolean query(&064;Param("$teamname") String teamName) {

 * </pre>
 *
 * @see ParamsAction
 *
 * @param <RETURN> Return type of query.
 * @author Honza Havlicek
 */
public abstract class ParamsSense<CONTEXT extends Context, RETURN> extends StateSense<CONTEXT, RETURN> {

    private static final String QUERY_METHOD_NAME = "query";
    private final ParamsMethod<RETURN> queryMethod;

    /**
     * Create new parametrized sense.
     *
     * @param ctx Shared info of bot + tools for manipulating the world.
     * @param returnCls Class of RETURN type. Required by reflection for finding
     * the correct query method (generic types are erased during compile time).
     * Currently only <tt>String.class</tt>, <tt>Double.TYPE</tt> and
     * <tt>Integer.TYPE</tt> return values are supported.
     */
    protected ParamsSense(CONTEXT ctx, Class<RETURN> returnCls) {
        super(ctx);

        queryMethod = new ParamsMethod<RETURN>(getClass(), QUERY_METHOD_NAME, returnCls);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
	protected ParamsSense(CONTEXT ctx) {
        super(ctx);

        queryMethod = new ParamsMethod(getClass(), QUERY_METHOD_NAME, Object.class);
    }    
    
    @Override
    public final RETURN query(VariableContext params) {
        try {
            return queryMethod.invoke(this, params);
        } catch (MethodException ex) {
            String msg = MessageFormat.format("Method {1} of sense {0} has thrown an exception. ", this.getClass().getName(), QUERY_METHOD_NAME);
            throw new MethodException(msg, ex);
        } catch (InvocationTargetException ex) {
            String thrownExceptionName = ex.getTargetException().getClass().getSimpleName();
            String msg = MessageFormat.format("Method {1} of sense {0} has thrown an exception {2}", this.getClass().getName(), QUERY_METHOD_NAME, thrownExceptionName);

            throw new MethodException(msg, ex.getTargetException());
        }
    }
}
