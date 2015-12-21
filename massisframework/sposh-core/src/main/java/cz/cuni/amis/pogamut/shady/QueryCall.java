package cz.cuni.amis.pogamut.shady;

import cz.cuni.amis.pogamut.sposh.executor.IWorkExecutor;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

/**
 * Query call is used to call some function in outside of engine with some
 * parameters and return the value. {@link NodeCall} just descend further into
 * a tree.
 *
 * TODO: explain, how target is determined
 * @see NodeCall
 * @author Honza
 */
public class QueryCall implements IQuery {

    private final String name;
    private final List<IArgument> argsUm;

    public QueryCall(String name, List<IArgument> args) {
        this.name = name;
        this.argsUm = Collections.unmodifiableList(args);
    }

    /**
     * Get name of called entity
     * @return name of called entity, whatever {@link IWorkExecutor} will make
     * out of it.
     */
    public String getName() {
        return name;
    }

    /**
     * Get arguments passed to the called entity
     * @return unmodifiable list of arguments
     */
    public List<IArgument> getArgs() {
        return argsUm;
    }

    /**
     * Execute the primitive with name and proper arguments and return value.
     * @return the value that query returned
     */
    @Override
    public BigDecimal execute(IWorkExecutor executor) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }
}

/**
 * Dummy query that always returns number passed in constructor.
 * @author Honza
 */
class QueryInt implements IQuery {

    private final BigDecimal number;

    public QueryInt(int number) {
        this.number = new BigDecimal(number);
    }

    @Override
    public BigDecimal execute(IWorkExecutor executor) {
        return number;
    }
}

/**
 * Dummy query that always returns number passed in the constructor.
 * @author Honza
 */
class QueryFloat implements IQuery {

    private final BigDecimal number;

    QueryFloat(double number) {
        this.number = new BigDecimal(number);
    }

    @Override
    public BigDecimal execute(IWorkExecutor executor) {
        return number;
    }
}

/**
 * Query operator not, when queried for value, execute passed query and return
 * negative of that answer.
 * @author Honza
 */
class QueryNot implements IQuery {

    private final IQuery query;

    QueryNot(IQuery query) {
        this.query = query;
    }

    @Override
    public BigDecimal execute(IWorkExecutor executor) {
        BigDecimal queryResult = query.execute(executor);
        if (queryResult.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ONE;
        } else {
            return BigDecimal.ZERO;
        }
    }
}

/**
 * Make an "and" operation for many {@link IQuery} arguments.
 * @author Honza
 */
class QueryAnd implements IQuery {

    private final List<IQuery> args;

    QueryAnd(List<IQuery> args) {
        this.args = args;
    }

    @Override
    public BigDecimal execute(IWorkExecutor executor) {
        boolean result = true;
        for (IQuery arg : args) {
            result &= arg.execute(executor).compareTo(BigDecimal.ZERO) != 0;
        }
        return result ? BigDecimal.ONE : BigDecimal.ZERO;
    }
}

/**
 * Make an "or" operation for many {@link IQuery} arguments.
 * @author Honza
 */
class QueryOr implements IQuery {

    private final List<IQuery> args;

    QueryOr(List<IQuery> args) {
        this.args = args;
    }

    @Override
    public BigDecimal execute(IWorkExecutor executor) {
        boolean result = false;
        for (IQuery arg : args) {
            result |= arg.execute(executor).compareTo(BigDecimal.ZERO) != 0;
        }
        return result ? BigDecimal.ONE : BigDecimal.ZERO;
    }
}

abstract class QueryCmp implements IQuery {

    private final IQuery op1;
    private final IQuery op2;

    protected QueryCmp(IQuery op1, IQuery op2) {
        this.op1 = op1;
        this.op2 = op2;
    }

    /**
     * Specify comparison operation.
     * @param cmp -1 if op1 < op2, 0 if op1 == op2 and 1 if op1 > op2
     * @return result of your comparison operation
     */
    abstract boolean cmp(int cmp);

    @Override
    final public BigDecimal execute(IWorkExecutor executor) {
        BigDecimal res1 = op1.execute(executor);
        BigDecimal res2 = op2.execute(executor);
        return cmp(res1.compareTo(res2)) ? BigDecimal.ONE : BigDecimal.ZERO;
    }
}

class QueryGt extends QueryCmp {

    QueryGt(IQuery op1, IQuery op2) {
        super(op1, op2);
    }

    @Override
    protected boolean cmp(int cmp) {
        return cmp > 0;
    }
}

class QueryGe extends QueryCmp {

    QueryGe(IQuery op1, IQuery op2) {
        super(op1, op2);
    }

    @Override
    protected boolean cmp(int cmp) {
        return cmp >= 0;
    }
}

class QueryEq extends QueryCmp {

    QueryEq(IQuery op1, IQuery op2) {
        super(op1, op2);
    }

    @Override
    protected boolean cmp(int cmp) {
        return cmp == 0;
    }
}

class QueryNe extends QueryCmp {

    QueryNe(IQuery op1, IQuery op2) {
        super(op1, op2);
    }

    @Override
    protected boolean cmp(int cmp) {
        return cmp != 0;
    }
}

class QueryLe extends QueryCmp {

    QueryLe(IQuery op1, IQuery op2) {
        super(op1, op2);
    }

    @Override
    protected boolean cmp(int cmp) {
        return cmp <= 0;
    }
}

class QueryLt extends QueryCmp {

    QueryLt(IQuery op1, IQuery op2) {
        super(op1, op2);
    }

    @Override
    protected boolean cmp(int cmp) {
        return cmp < 0;
    }
}
