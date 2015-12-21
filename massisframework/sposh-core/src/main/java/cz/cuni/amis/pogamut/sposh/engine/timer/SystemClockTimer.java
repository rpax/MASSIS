package cz.cuni.amis.pogamut.sposh.engine.timer;

import java.util.Calendar;

/**
 * Simple timer that has same time as Calendar. Can't be suspended or resumed.
 * @author Honza
 */
public class SystemClockTimer implements ITimer {

    @Override
    public void init() {
    }

    @Override
    public void suspend() {
        throw new UnsupportedOperationException("System clock can't be suspended.");
    }

    @Override
    public boolean isSuspended() {
        return false;
    }

    @Override
    public void resume() {
        throw new UnsupportedOperationException("System clock can't be suspended.");
    }

    @Override
    public long getTime() {
        return Calendar.getInstance().getTimeInMillis();
    }

}
