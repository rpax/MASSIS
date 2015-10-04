/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpax.massis.model.agents;

/**
 *
 * @author Rafael Pax
 */
public enum SimObjectProperty {

    IS_FURNITURE(Boolean.class),
    CLASSNAME(String.class),
    PLANFILE(String.class),
    TELEPORT(Boolean.class),
    WINDOW(Boolean.class),
    NAME(String.class),
    POINT_OF_INTEREST(Boolean.class),
    IS_OBSTACLE(Boolean.class),
    IS_DYNAMIC(Boolean.class),
    TYPE(String.class),
    START(String.class),
    ID(Integer.class){ public String toString(){return "id";}};
    public final Class<?> propertyType;

    private SimObjectProperty(Class<?> propertyType)
    {
        this.propertyType = propertyType;
    }
}
