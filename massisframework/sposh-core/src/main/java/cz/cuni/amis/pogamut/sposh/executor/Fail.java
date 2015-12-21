package cz.cuni.amis.pogamut.sposh.executor;

import cz.cuni.amis.pogamut.sposh.context.Context;
import cz.cuni.amis.pogamut.sposh.engine.VariableContext;

/**
 * Primitive sense that always fails (returns false) and does nothing else.
 *
 * @author Honza
 */
@PrimitiveInfo(name = "Fail", description = "Return false")
public class Fail extends StateSense<Context, Boolean> {

    public Fail(Context ctx) {
        super(ctx);
    }

    @Override
    public Boolean query(VariableContext params) {
        return false;
    }
}
