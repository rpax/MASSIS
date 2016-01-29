package cz.cuni.amis.pogamut.sposh.elements;

/**
 * Interface for elements that can reference other elements. Currently only {@link Sense}
 * which references primitive sense and {@link TriggeredAction} which can
 * reference either primitive action or AP/C/AD.
 *
 * @author Honza
 */
public interface IReferenceElement extends INamedElement {

    /**
     * @return arguments that are being passed to the referenced element.
     */
    Arguments getArguments();
}
