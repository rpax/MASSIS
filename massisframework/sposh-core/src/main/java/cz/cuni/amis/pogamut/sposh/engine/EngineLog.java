package cz.cuni.amis.pogamut.sposh.engine;

import cz.cuni.amis.pogamut.sposh.elements.LapPath;
import cz.cuni.amis.pogamut.sposh.elements.PoshElement;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The logger of engine activity. It logs which paths were visited, this class
 * is used by Dash to determine which paths are about to be executed.
 *
 * Dash puts Java breakpoint at entry of {@link #pathReachedExit() } and each
 * time it is reached, it retrieves the reached path and displayes it in the
 * scene.
 *
 * @author Honza
 */
public class EngineLog {

    private final List<LapPath> paths = new LinkedList<LapPath>();
    private final List<LapPath> pathsUm = Collections.unmodifiableList(paths);
    private final Logger log;

    EngineLog(Logger log) {
        this.log = log;
    }

    /**
     * Every time some executor of {@link PoshElement} is about to execute it,
     * it calls this method.
     *
     * This method will write to SPOSH {@link Logger} with level {@link Level#FINEST}
     * that path has been reached.
     *
     * @param path Path of element that is about to be executed.
     */
    public void pathReached(LapPath path) {
        finest("Reached path: " + path.toString());
        paths.add(path);
        // DO NOT REMOVE, USE BY DASH. For details see javadoc of the method.
        pathReachedExit();
    }

    /**
     * <em>DO NOT REMOVE!!!</em> This method is here as a workaround of slow
     * Netbeans breakpoint API. The API is quite fast when adding a breakpoint
     * of {@link MethodBreakpoint#TYPE_METHOD_ENTRY} type, but <em>very
     * slow</em> (about 1.2 seconds penalty) when using the
     * {@link MethodBreakpoint#TYPE_METHOD_EXIT}. As a workaround, I am adding
     * an {@link MethodBreakpoint#TYPE_METHOD_ENTRY entry type} breakpoint to
     * this method that is used directly before returning from
     * {@link #pathReached(cz.cuni.amis.pogamut.sposh.elements.LapPath)
     * }.
     *
     * Dash is adding an entry breakpoint at this method and calls {@link #getLastReachedPath()
     * } in order to get the firing path.
     */
    public void pathReachedExit() {
        // intentionally left empty
    }

    /**
     * This method is used by the Dash to retrieve the last firing path. Dash
     * has a breakpoint at entry of method {@link #pathReachedExit() }.
     *
     * @return String serialization ({@link LapPath}) of last firing path.
     */
    public String getLastReachedPath() {
        return paths.get(paths.size() - 1).toString();
    }

    List<LapPath> getPaths() {
        return pathsUm;
    }

    void clear() {
        paths.clear();
    }

    void finest(String msg) {
        if (log != null) {
            log.finest(msg);
        }
    }

    void fine(String msg) {
        if (log != null) {
            log.fine(msg);
        }
    }

    void info(String msg) {
        if (log != null) {
            log.info(msg);
        }
    }

    void warning(String msg) {
        if (log != null) {
            log.warning(msg);
        }
    }

    Logger getLogger() {
        return this.log;
    }
}