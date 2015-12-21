package cz.cuni.amis.pogamut.sposh.elements;

import cz.cuni.amis.pogamut.sposh.exceptions.CycleException;
import cz.cuni.amis.pogamut.sposh.exceptions.DuplicateNameException;
import cz.cuni.amis.pogamut.sposh.exceptions.InvalidNameException;

/**
 * Interface for lap elements with name. Most of the elements have a name, but
 * some (e.g. {@link Goal)) don't.
 *
 * @author HonzaH
 */
public interface INamedElement {

    /**
     * Get name of the element. The actual name, not display name (e.g. action
     * can have FQN of class, but display name would be derived fromk
     * annotations of the class) or representation (e.g. sense name "health" is
     * different from actual condition of sense "health > 90").
     *
     * @return Name of the element
     */
    public String getName();

    /**
     * Change name of the element to newName. Renaming doesn't just change name,
     * it also changes all references in the plan from the old name to a new
     * one.
     *
     * @param newName New name of the element.
     * @throws InvalidNameException Passed name is not valid Yaposh name.
     * @throws CycleException Renaming causes cycle.
     * @throws DuplicateNameException Such name is already used in the plan.
     */
    public void rename(String newName) throws InvalidNameException, CycleException, DuplicateNameException;
}
