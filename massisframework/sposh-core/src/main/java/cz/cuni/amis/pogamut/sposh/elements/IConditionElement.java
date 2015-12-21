package cz.cuni.amis.pogamut.sposh.elements;

/**
 * Interface for elements that use condition: either goal or trigger. Both of
 * them are list of {@link Sense}s connected by logical operation <em>AND</em>.
 *
 * @param <CONDITION_OWNER> Class implementing the interface (e.g. {@link DriveElement}
 * has a {@link Trigger} so when it implements this interface, it will use {@link DriveElement}).
 * @author Honza H
 */
public interface IConditionElement<CONDITION_OWNER extends PoshElement> {

    /**
     * Get condition of the element.
     *
     * @return Condition of the element, never null, but condition can be empty
     * (e.g. it contains no senses).
     */
    Trigger<CONDITION_OWNER> getCondition();
}
