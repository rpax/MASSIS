package cz.cuni.amis.pogamut.sposh.context;

import cz.cuni.amis.pogamut.sposh.executor.IAction;
import cz.cuni.amis.pogamut.sposh.executor.ISense;

/**
 * This is the "original" context from which all others should be derived.
 * Context is used in state primitives (i.e. {@link IAction} and{@link ISense})
 * to access "shared" info. This is the "basic version," there are other versions
 * tailored with modules for other environments, such as {@link UT2004Context}.
 * @author Honza
 */
public class Context<AGENT> implements IContext<AGENT> {
    protected final String name;
    protected final AGENT bot;

    /**
     * Create new context.
     * @param name Name of context, can be used in logs and such.
     * @param bot Class of the bot that this behaviour is serving.
     *            Used by sense and actions for gathering info and manipulation of the bot.
     */
    protected Context(String name, AGENT bot) {
        this.name = name;
        this.bot = bot;
    }

    @Override
    public AGENT getBot() {
        return bot;
    }
}
