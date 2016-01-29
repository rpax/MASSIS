package cz.cuni.amis.pogamut.sposh.executor;

import cz.cuni.amis.pogamut.sposh.context.Context;
import cz.cuni.amis.pogamut.sposh.engine.VariableContext;

/**
 * Primitive action that doesn't do anything. It is used in empty plans and so on.
 * @author Honza
 */
@PrimitiveInfo(name = "Do nothing", description = "This action does nothing and lasts one iteration.")
public class DoNothing extends StateAction<Context> {

    public DoNothing(Context ctx) {
        super(ctx);
    }

    @Override
    public void init(VariableContext params) {
    }

    @Override
    public ActionResult run(VariableContext params) {
        return ActionResult.RUNNING_ONCE;
    }

    @Override
    public void done(VariableContext params) {
    }
}
