package cz.cuni.amis.pogamut.sposh.elements;

import cz.cuni.amis.pogamut.sposh.elements.LapPath.Link;
import cz.cuni.amis.pogamut.sposh.engine.VariableContext;
import java.beans.PropertyChangeEvent;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

interface IChainLink {

    LapType getType();

    Arguments getArgs();

    FormalParameters getParams();

    PoshElement getReference();

    PoshElement getReferencedNode();

    void register(LapChain chain);

    void unregister(LapChain chain);
}

/**
 * Chain link representing call of primitive (action or sense).
 */
class ReferenceChainLink<REFERENCE_ELEMENT extends PoshElement & IReferenceElement> extends ChainLink {

    private final REFERENCE_ELEMENT reference;
    private final FormalParameters EMPTY_PARAMETERS = new FormalParameters();

    public ReferenceChainLink(REFERENCE_ELEMENT reference) {
        this.reference = reference;
    }

    @Override
    public void register(LapChain chain) {
        reference.addElementListener(chain);
    }

    @Override
    public void unregister(LapChain chain) {
        reference.removeElementListener(chain);
    }

    @Override
    public LapType getType() {
        return reference.getType();
    }

    @Override
    public FormalParameters getParams() {
        return EMPTY_PARAMETERS;
    }

    @Override
    public Arguments getArgs() {
        return reference.getArguments();
    }

    @Override
    public REFERENCE_ELEMENT getReference() {
        return reference;
    }

    @Override
    public PoshElement getReferencedNode() {
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ReferenceChainLink<REFERENCE_ELEMENT> other = (ReferenceChainLink<REFERENCE_ELEMENT>) obj;
        if (this.reference != other.reference) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        return hash;
    }
}

/**
 * Link representing referencing of {@link ReferencedNode} from some {@link TriggeredAction}.
 */
class ReferenceNodeChainLink<REFERENCED_NODE extends PoshElement & IParametrizedElement> extends ChainLink {

    private final TriggeredAction action;
    private final REFERENCED_NODE referencedNode;

    public ReferenceNodeChainLink(REFERENCED_NODE referencedNode, TriggeredAction action) {
        this.referencedNode = referencedNode;
        this.action = action;
    }

    @Override
    public LapType getType() {
        return referencedNode.getType();
    }

    @Override
    public final Arguments getArgs() {
        return action.getArguments();
    }

    @Override
    public final FormalParameters getParams() {
        return referencedNode.getParameters();
    }

    @Override
    public final TriggeredAction getReference() {
        return action;
    }

    @Override
    public REFERENCED_NODE getReferencedNode() {
        return referencedNode;
    }

    @Override
    public void register(LapChain chain) {
        referencedNode.addElementListener(chain);
        action.addElementListener(chain);
    }

    @Override
    public void unregister(LapChain chain) {
        action.removeElementListener(chain);
        referencedNode.removeElementListener(chain);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ReferenceNodeChainLink<REFERENCED_NODE> other = (ReferenceNodeChainLink<REFERENCED_NODE>) obj;
        if (this.action != other.action) {
            return false;
        }
        if (this.referencedNode != other.referencedNode) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        return hash;
    }
}

/**
 * One link of {@link ParametersChain} chain. Each link is basically a reference
 * from {@link TriggeredAction action} to C/Ap/primitive, therefore we need
 * arguments of the reference and parameters of the refereced.
 */
abstract class ChainLink implements IChainLink {

    @Override
    public String toString() {
        return "{" + "params=" + getParams() + ", args=" + getArgs() + '}';
    }
}

/**
 * Chain of links that represents how are parameters passed in the chain of lap
 * elements. Each time action is referenced, it adds new chain of link.
 *
 * The chain itself is immutable. You can listen for changes of {@link FormalParameters parameters}
 * and {@link Arguments} of {@link PoshElement elements} referenced by the
 * individual links.
 *
 * Example1: There is only drive that calls some action. In such case, there is
 * only one link of the chain - the call from the chain to the action.
 *
 * Example2: There is drive action that references AP1 and that AP1 references
 * some action. In such case chain has two links - first link represents
 * reference from drive action to AP1 and second link represents reference from
 * AP1 to the action..
 */
public final class LapChain implements PoshElementListener {

    /**
     * All links of the chain. The last index is the last link of the chain.
     */
    private List<IChainLink> links;
    /**
     * Listeners for this chain.
     */
    private final Set<ILapChainListener> listeners = new HashSet<ILapChainListener>();

    /**
     * Create new chain, It has no {@link ChainLink}.
     */
    public LapChain() {
        links = new LinkedList<IChainLink>();
    }

    /**
     * Create chain from the @path.
     *
     * @param path Path that will be used as source of info.
     * @return New chain.
     */
    public static LapChain fromPath(PoshPlan plan, LapPath path) {
        LapChain chain = new LapChain();
        Iterator<Link> iterator = path.iterator();
        while (iterator.hasNext()) {
            Link nodeLink = iterator.next();
            int index = path.getLinkIndex(nodeLink) + 1;
            LapPath nodePath = path.subpath(0, index);
            PoshElement node = nodePath.traversePath(plan);
            if (node instanceof TriggeredAction) {
                TriggeredAction reference = (TriggeredAction) node;
                chain = processReference(plan, path, chain, iterator, reference);
            } else if (node instanceof Sense) {
                chain = chain.derive((Sense) node);
            } else {
                // Ignore other nodes
            }
        }
        return chain;
    }

    private static LapChain processReference(PoshPlan plan, LapPath path, LapChain chain, Iterator<Link> iterator, TriggeredAction reference) {
        if (iterator.hasNext()) {
            Link referencedNodeLink = iterator.next();
            LapPath processedPath = path.subpath(0, path.getLinkIndex(referencedNodeLink) + 1);
            PoshElement referencedNode = processedPath.traversePath(plan);
            if (referencedNode instanceof Competence) {
                chain = chain.derive(reference, (Competence) referencedNode);
            } else if (referencedNode instanceof ActionPattern) {
                chain = chain.derive(reference, (ActionPattern) referencedNode);
            } else if (referencedNode instanceof Adopt) {
                chain = chain.derive(reference, (Adopt) referencedNode);
            } else {
                throw new IllegalStateException("Node " + referencedNodeLink + " is not a referenced node.");
            }
        } else {
            chain = chain.derive(reference);
        }
        return chain;
    }

    /**
     * Create {@link LapPath} from the chain. Since not every {@link LapPath}
     * can be represented by the chain (e.g. {@link LapType#COMPETENCE_ELEMENT}),
     * it is not 1-on-1 mapping. Resulting {@link LapPath} always starts with
     * /P:0 and ends with type of referenced node of last link. If there is not
     * referenced node at last link (e.g. {@link Sense} or {@link TriggeredAction}),
     * the last link of path is {@link LapType} of {@link IChainLink#getReference()
     * }.
     *
     * If chain is empty, return {@link LapPath#DRIVE_COLLECTION_PATH}.
     *
     * @return Created path.
     */
    public LapPath toPath() {
        if (links.isEmpty()) {
            return LapPath.DRIVE_COLLECTION_PATH;
        }
        LapPath path = LapPath.PLAN_PATH;
        Iterator<IChainLink> it = links.iterator();
        IChainLink link;
        do {
            link = it.next();
            LapPath linkPath = LapPath.getLinkPath(link.getReference());
            path = path.concat(linkPath);
        } while (it.hasNext());

        PoshElement referencedNode = link.getReferencedNode();
        if (referencedNode != null) {
            path = path.concat(referencedNode.getType(), referencedNode.getId());
        }
        return path;
    }

    /**
     * Create new chain by copying the original chain and appending one link at
     * the end.
     *
     * @param chain Chain that will be copied.
     * @param link last link of new chain.
     */
    private LapChain(LapChain chain, IChainLink link) {
        this.links = new LinkedList<IChainLink>(chain.links);
        this.links.add(link);
    }

    /**
     * Add the chain as listener for changes of elements of links. Chain can't
     * notify its {@link ILapChainListener} unless it is registered.
     */
    public void register() {
        for (IChainLink link : links) {
            link.register(this);
        }
    }

    /**
     * Remove the chain as listener of all links elements.
     */
    public void unregister() {
        for (IChainLink link : links) {
            link.unregister(this);
        }
    }

    /**
     * Return new chain using this one as the base and create new link from the
     * reference of action to AP.
     *
     * @param action action referencing the AP. Basis of the link.
     * @param actionPattern referenced AP.
     * @return Newly created chain.
     */
    public <REFERENCED_NODE extends PoshElement & IParametrizedElement> LapChain derive(TriggeredAction action, REFERENCED_NODE referencedNode) {
        assert action.getName().equals(referencedNode.getName());

        return new LapChain(this, new ReferenceNodeChainLink(referencedNode, action));
    }

    /**
     * Create and return new chain using this one as the base. The last link is
     * a leaf, either action or sense. Since we are calling primitive action,
     * there is no referenced node.
     *
     * @param reference Reference to action or sense in work executor
     * @return Newly created chain.
     */
    public <REFERENCE extends PoshElement & IReferenceElement> LapChain derive(REFERENCE reference) {
        return new LapChain(this, new ReferenceChainLink(reference));
    }

    /**
     * Create subchain created from links of this chain.
     * @param beginIndex Begin index of subchain, inclusive
     * @param endIndex End index of subchain, exclusive
     * @return 
     */
    public LapChain subchain(int beginIndex, int endIndex) {
        LapChain subchain = new LapChain();
        for (IChainLink link : links.subList(beginIndex, endIndex)) {
            subchain = new LapChain(subchain, link);
        }
        return subchain;
    }
    
    /**
     * How many links of chain are there.
     */
    public int size() {
        return links.size();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Chain{");
        boolean first = true;
        for (IChainLink link : links) {
            if (!first) {
                sb.append(',');
            }
            sb.append(link.toString());
        }
        sb.append('}');

        return sb.toString();
    }

    /**
     * @return Newly created {@link VariableContext} for current chain.
     */
    public VariableContext createContext() {
        VariableContext ctx = new VariableContext();

        for (IChainLink link : links) {
            ctx = new VariableContext(ctx, link.getArgs(), link.getParams());
        }
        return ctx;
    }

    @Override
    public void childElementAdded(PoshElement parent, PoshElement child) {
    }

    @Override
    public void childElementMoved(PoshElement parent, PoshElement child, int oldIndex, int newIndex) {
    }

    @Override
    public void childElementRemoved(PoshElement parent, PoshElement child, int removedChildPosition) {
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        emitLinkChanged();
    }

    public boolean addChainListener(ILapChainListener listener) {
        return listeners.add(listener);
    }

    public boolean removeChainListener(ILapChainListener listener) {
        return listeners.remove(listener);
    }

    private void emitLinkChanged() {
        ILapChainListener[] listenersArray = listeners.toArray(new ILapChainListener[listeners.size()]);

        for (ILapChainListener listener : listenersArray) {
            listener.notifyLinkChanged();
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final LapChain other = (LapChain) obj;
        if (this.links != other.links && (this.links == null || !this.links.equals(other.links))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 31 * hash + (this.links != null ? this.links.hashCode() : 0);
        return hash;
    }
}
