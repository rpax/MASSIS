package cz.cuni.amis.pogamut.sposh.executor;

import cz.cuni.amis.pogamut.sposh.engine.VariableContext;

/**
 * Primitive representation used in {@link StateWorkExecutor}. This primitive is
 * executed in phases: first is called init, then run is called, until
 * executor decides it is time to switch to another primtive. Before init of
 * new primtive is called, done of previous primitive is called.
 * @author Honza
 * @param <RETURN> Type of object that is returned by this action every time it is evaluated.
 */
public interface IAction {

    /**
     * Initialize action. Called ever time action is supposed to execute.
     * <p/>
     * After init, run is immediately called, so first run call has same state of world
     * as init did.
     * @param params Variable context that is passed from posh plan to the primitive
     */
    void init(VariableContext params);
    
    /**
     * Run is called every time evaluation of posh plan determines that this and
     * no other action is the one that is supposed to execute. 
     * @param params Variable context passed from posh plan to the primtive
     * @return result of action in this iteration.
     */
    ActionResult run(VariableContext params);
    
    /**
     * This action is done, according to posh plan, some other {@link IAction} is
     * supposed to execute, therefore use this to clean up the mess.
     * @param params
     */
    void done(VariableContext params);
    
}
