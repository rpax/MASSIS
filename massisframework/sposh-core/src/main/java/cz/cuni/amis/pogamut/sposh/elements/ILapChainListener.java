package cz.cuni.amis.pogamut.sposh.elements;

/**
 * Listener for changes on {@link LapChain}. Every time there is a change in the
 * chain (links have new {@link FormalParameters} or {@link Arguments}),
 * listener will be notified.
 *
 * @author Honza
 */
public interface ILapChainListener {

    /**
     * This method is called each time the link of chain is changed.
     */
    void notifyLinkChanged();
}
