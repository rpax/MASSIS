package cz.cuni.amis.pogamut.sposh.context;

/**
 * Inteface for getting the agent for the context.
 *
 * @author Honza
 * @param <AGENT> Access to the agent itself that.
 */
public interface IContext<AGENT> {

    /**
     * Get bot for this context.
     *
     * @return bot
     */
    public AGENT getBot();
}
