package cz.cuni.amis.pogamut.sposh.engine.timer;

import java.util.Calendar;

/**
 * Timer what allows me to skip forward in time or suspend time for a while.
 * Very useful for tests or debugging.
 * @author Honza
 */
public class DebugTimer implements ITimer {
    long subtract = 0;

    boolean suspended = false;
    long suspendedTime = 0;

    @Override
    public void init() {
    }

    @Override
    public void suspend() {
        suspendedTime = Calendar.getInstance().getTimeInMillis();
    }

    @Override
    public boolean isSuspended() {
        return suspended;
    }

    @Override
    public void resume() {
        if (suspended) {
            subtract += Calendar.getInstance().getTimeInMillis() - suspendedTime;
            suspendedTime = 0;
            suspended = false;
        }
    }

    public void addTime(long time2add) {
        subtract -= time2add;
    }

    @Override
    public long getTime() {
        if (suspended)
            return suspendedTime - subtract;

        return Calendar.getInstance().getTimeInMillis() - subtract;
    }

}
