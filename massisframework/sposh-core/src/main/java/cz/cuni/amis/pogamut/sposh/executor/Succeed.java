package cz.cuni.amis.pogamut.sposh.executor;

import cz.cuni.amis.pogamut.sposh.context.Context;
import cz.cuni.amis.pogamut.sposh.engine.VariableContext;

/**
 * Primitive sense, always succeed (return true) and do nothing else.
 *
 * @author Honza
 */
@PrimitiveInfo(name = "Succeed", description = "Return true")
public class Succeed extends StateSense<Context, Boolean> {

    public Succeed(Context ctx) {
        super("succeed", ctx);
    }

    @Override
    public Boolean query(VariableContext ctx) {
        return true;
    }
}
