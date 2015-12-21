package cz.cuni.amis.pogamut.sposh.engine;

/**
 * Result of ElementExecutors, it specified how should be drive stack modified.
 * Only FOLLOW requires specification of nextElement.
 *
 * @author Honza
 */
public final class FireResult {

    /**
     * Value returned by the executor of an element on the stack, it determines
     * how should drive stack be modified.
     */
    public enum Type {

        /**
         * The element is finished and result is success.
         */
        FULFILLED,
        /**
         * The element is finished and it failed.
         */
        FAILED,
        /**
         * Element wasn't yet finished.
         */
        CONTINUE,
        /**
         * Some child element is supposed to be evaluated.
         */
        FOLLOW,
        /**
         * Well, not many will use this I suppose, but move up in the stack,
         * something like return from function. This is the only type that
         * causes the sposh logic controller to stop iterating. If {@link PoshEngine#evaluatePlan(cz.cuni.amis.pogamut.sposh.executor.IWorkExecutor)
         * } returns any other value, the sposh logic controller will call
         * another round of {@link PoshEngine#evaluatePlan(cz.cuni.amis.pogamut.sposh.executor.IWorkExecutor)}
         */
        SURFACE,
        /**
         * E.g. surface (pop element on the stack), but also indicates that
         * execution should continue.
         */
        SURFACE_CONTINUE,
    }
    private Type type;
    private StackElement nextElement;

    /**
     * Create result for modification of stack, the {@link #getNextElement() }
     * is null.
     *
     * @param type How should be stack modified, not {@link Type#FOLLOW}.
     */
    protected FireResult(Type type) {
        this(type, null);
    }

    /**
     * Create structure saying how the should be the drive stack modified.
     *
     * Example: Follow + some element of stack will result in stack of drive
     * adding the elemnet of stack on the top of the stack.
     *
     * @param type How should be stack of drive modified.
     * @param nextElement What should {@link #getNextElement() } be.
     */
    protected FireResult(Type type, StackElement nextElement) {
        this.type = type;
        this.nextElement = nextElement;
    }

    /**
     * @return How should stack be modified?
     */
    public Type getType() {
        return type;
    }

    /**
     * Get element that should be put at the top of the stack. This method can
     * be called only if {@link #getType() } is {@link Type#FOLLOW}.
     *
     * @return the nextElement that be added to the top of the stack
     */
    public StackElement getNextElement() {
        assert (nextElement != null) : "NextElement is null, type of FireResult should be such, that we never ask for it (is " + getType() + ")";
        return nextElement;
    }
}
