package cz.cuni.amis.pogamut.sposh.engine;

import cz.cuni.amis.pogamut.sposh.elements.LapPath;
import cz.cuni.amis.pogamut.sposh.executor.IWorkExecutor;


/**
 * Common executor for AP, C and primitive
 *
 * @author Honza
 */
interface ElementExecutor {

    FireResult fire(IWorkExecutor workExecuter);

    /**
     * @return Path of the element executor is processing.
     */
    LapPath getPath();
}
