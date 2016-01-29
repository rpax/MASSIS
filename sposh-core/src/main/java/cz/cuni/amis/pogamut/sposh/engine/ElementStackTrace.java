package cz.cuni.amis.pogamut.sposh.engine;

import java.util.ArrayList;

/**
 * One level in callstack.
 */
class StackElement<T extends ElementExecutor> {

    public final String name;
    public final Class clazz;
    public final T executor;

    StackElement(Class clazz, String name, T executor) {
        this.clazz = clazz;
        this.name = name;
        this.executor = executor;
    }

    @Override
    public String toString() {
        return "StackElement[" + clazz.getSimpleName() + ":" + name + ":" + executor + "]";
    }

    T getExecutor() {
        return executor;
    }
}

/**
 * Callstack used for storing info what was call hiearchy of elements.
 *
 * @author Honza
 */
final class ElementStackTrace extends ArrayList<StackElement> {

    public void push(StackElement element) {
    	add(element);
    }
    
    public StackElement pop() {
    	if (size() == 0) return null;
    	return remove(size()-1);
    }
    
    public StackElement peek() {
    	if (size() == 0) return null;
    	return get(size()-1);
    }
    
    public void removeAllElements() {
    	clear();
    }
    
    /**
     * Pops all elements until 'executor' is encountered. Pops 'executor' out of the stack as well.
     * 
     * If 'null' is passed as 'executor', removes whole stack.
     * 
     * @param executor
     */
    public void cutDownToIncluding(ElementExecutor executor) {
    	if (executor == null) {
    		removeAllElements();
    		return;
    	}
		while (size() != 0 && peek().getExecutor() != executor) {
			pop();
		}
		if (size() > 0) pop(); // pops 'executor' as well
	}
    
    /**
     * Pops all elements until 'executor' is encountered. Leaves 'executor' on the stack.
     * 
     * If 'null' is passed as 'executor', removes whole stack.
     * 
     * @param executor
     */
    public void cutDownToExcluding(ElementExecutor executor) {
    	if (executor == null) {
    		removeAllElements();
    		return;
    	}
		while (size() != 0 && peek().getExecutor() != executor) {
			pop();
		}
		// leave executor on the stack
	}
}
