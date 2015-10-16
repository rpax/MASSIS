/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpax.massis.util.collections.filters;

import java.util.Iterator;
import org.apache.commons.collections15.iterators.IteratorChain;

/**
 *
 * @author Rafael Pax
 */
public final class WrapperUtils {

    public static <T> Iterable<T> unwrap(Iterable<? extends GenericWrapper<T>> items)
    {
        final Iterator<? extends GenericWrapper<T>> it = items.iterator();
        return new Iterable<T>() {
            @Override
            public Iterator<T> iterator()
            {

                return new Iterator<T>() {
                    @Override
                    public boolean hasNext()
                    {
                        return it.hasNext();
                    }

                    @Override
                    public T next()
                    {
                        return it.next().getElement();
                    }

                    @Override
                    public void remove()
                    {
                        throw new UnsupportedOperationException("Not supported.");
                    }
                };
            }
        };

    }

    public static <T> Iterable<GenericWrapper<T>> wrap(Iterable<T> items)
    {
        final Iterator<T> it = items.iterator();
        return new Iterable<GenericWrapper<T>>() {
            @Override
            public Iterator<GenericWrapper<T>> iterator()
            {

                return new Iterator<GenericWrapper<T>>() {
                    @Override
                    public boolean hasNext()
                    {
                        return it.hasNext();
                    }

                    @Override
                    public GenericWrapper<T> next()
                    {
                        final T next = it.next();
                        return new GenericWrapper<T>() {
                            @Override
                            public T getElement()
                            {
                                return next;
                            }
                        };
                    }

                    @Override
                    public void remove()
                    {
                        throw new UnsupportedOperationException("Not supported.");
                    }
                };
            }
        };
    }
}
