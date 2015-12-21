package cz.cuni.amis.pogamut.shady;

import cz.cuni.amis.pogamut.sposh.executor.IWorkExecutor;
import java.math.BigDecimal;

/**
 * Interface for getting integer value from some part of the plan, e.g. getting
 * priority.
 *
 * @see QueryCall
 * @see QueryCmp
 * @see QueryInt
 * @see QueryFloat
 * @see QueryGt
 * @see QueryGe
 * @see QueryEq
 * @see QueryNe
 * @see QueryLe
 * @see QueryLt
 * @see QueryAnd
 * @see QueryOr
 * @see QueryNot
 *
 * @author Honza
 */
public interface IQuery {

    /**
     * Execute query and return number.
     *
     * @return number as result of query. 0 is failure, otherwise is success
     */
    BigDecimal execute(IWorkExecutor executor);
}
