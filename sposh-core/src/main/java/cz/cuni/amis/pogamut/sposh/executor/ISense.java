package cz.cuni.amis.pogamut.sposh.executor;

import cz.cuni.amis.pogamut.sposh.engine.VariableContext;

/**
 * Interface for sense in state paradigma.
 * <p/>
 * Sense should <b>NEVER EVER</b> do any action. It is different from {@link IAction}.
 * Think of it as constant method of C++ class.
 * <p/>
 * Examples:
 * <ul>
 *  <li>Can I see my base: true/false</li>
 *  <li>Number of rabbits in my bag: number</li>
 * </ul>
 * @see IPrimitive
 * @see StateWorkExecutor
 * @author Honza
 * @param <RETURN> Class of object that this sense returns every time it is queried.
 */
public interface ISense<RETURN> {
    /**
     * Query current value of sense.
     * @param params Variable context passed from posh plan to sense. Could be used for things like threshold and so on.
     * @return what is sense currently sensing, e.g. number of friends in FOV
     */
    RETURN query(VariableContext params);
}
