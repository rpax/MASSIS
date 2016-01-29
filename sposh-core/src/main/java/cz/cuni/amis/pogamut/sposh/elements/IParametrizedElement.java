package cz.cuni.amis.pogamut.sposh.elements;

import cz.cuni.amis.pogamut.sposh.engine.VariableContext;

/**
 * Interface for elements that have parameters. {@link FormalParameters parameters}
 * are immutable.
 *
 * @author Honza
 */
public interface IParametrizedElement extends INamedElement {

    public FormalParameters getParameters();

    /**
     * Set new parameters.
     *
     * @param newParams new parameters
     * @throws IllegalArgumentException Implementing methods fire changes and
     * also to {@link LapChain}. If it propagates to some node that is showing
     * the node, it can cause missing variable in {@link VariableContext}.
     */
    public void setParameters(FormalParameters newParams);
}
