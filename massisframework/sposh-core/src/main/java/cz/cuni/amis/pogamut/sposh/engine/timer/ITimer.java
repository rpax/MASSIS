package cz.cuni.amis.pogamut.sposh.engine.timer;

/**
 * Timer used for things like frequency checking of sposh plans.
 * @author Honza
 */
public interface ITimer {
    /**
     * Initialize timer, e.g. synchronize it with system closk or anything.
     */
    void init();

    /**
     * Suspend timer, until resume() is called, stop the clock.
     */
    void suspend();

    /**
     * Is the timer suspended?
     */
    boolean isSuspended();

    /**
     * Resume suspended timer
     */
    void resume();

    /**
     * @return time 
     */
    long getTime();
}
