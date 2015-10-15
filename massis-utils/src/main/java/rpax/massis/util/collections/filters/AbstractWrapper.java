/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpax.massis.util.collections.filters;

/**
 * Generic Wrapper
 *
 * @author Rafael Pax
 */
public abstract class AbstractWrapper<T> {

    protected T element;

    public AbstractWrapper(T element)
    {
        this.element = element;
    }

    public T getElement()
    {
        return element;
    }

    public void setElement(T element)
    {
        this.element = element;
    }
}
