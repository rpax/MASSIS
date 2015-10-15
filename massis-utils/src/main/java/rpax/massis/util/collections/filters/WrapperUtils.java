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

    public <T> Iterable<T> unwrap(Iterable<GenericWrapper<T>> items)
    {
        final Iterator<GenericWrapper<T>> it = items.iterator();
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
}
