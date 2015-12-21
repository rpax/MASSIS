package cz.cuni.amis.pogamut.sposh.executor;

import cz.cuni.amis.pogamut.sposh.executor.IAction;
import cz.cuni.amis.pogamut.sposh.executor.ISense;
import cz.cuni.amis.pogamut.sposh.executor.ParamInfo;
import cz.cuni.amis.pogamut.sposh.executor.PrimitiveInfo;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Exchange container used for passing info between crawler and explorer.
 * Contains information about primitive {@link IAction} and {@link ISense}, i.e.
 * what is stored in {@link PrimitiveInfo} and the parameters of the primitive.
 *
 * @author Honza
 */
public final class PrimitiveData implements Comparable<PrimitiveData> {

    /**
     * Fully qualified name of the primitive class
     */
    public final String classFQN;
    /**
     * Name of the primitive from {@link PrimitiveInfo}, not necessary unique.
     * Is null, if no {@link PrimitiveInfo} annotation used.
     */
    public final String name;
    /**
     * Description of the primitive from {@link PrimitiveInfo}, can be null if
     * no annotation.
     */
    public final String description;
    /**
     * Tags for primitive
     */
    public final String[] tags;
    /**
     * Parameters used by the primitive
     */
    public final Set<ParamInfo> params;

    /**
     * Create {@link PrimitiveData} about some primitive that does't have {@link PrimitiveInfo}
     * annotation.
     *
     * @param classFQN fully qualified name of the primitive class.
     */
    public PrimitiveData(String classFQN) {
        this(classFQN, null, null, new String[0], Collections.<ParamInfo>emptySet());
    }

    /**
     * Create new instance of PrimitiveData
     *
     * @param classFQN fully qualified name of the primitive class.
     * @param name name of primitive from {@link PrimitiveInfo}, can be null.
     * @param description description of primitive from {@link PrimitiveInfo},
     * can be null.
     * @param tags tags of the primitive
     * @param parameters set of all parameters. Deep copy made.
     */
    public PrimitiveData(String classFQN, String name, String description, String[] tags, Set<ParamInfo> parameters) {
        this.classFQN = classFQN;
        this.name = name;
        this.description = description;
        this.tags = tags;
        this.params = Collections.unmodifiableSet(new HashSet<ParamInfo>(parameters));
    }

    /**
     * Get simple class name (not FQN)
     *
     * @return simple name of classFQN
     */
    public String getClassName() {
        return classFQN.replaceFirst("^.*\\.", "");
    }

    /**
     * Compare this data to another. First ignorcase-compare of names, if same,
     * compare FQN of primitives.
     *
     * @param o The other comparison object
     * @return
     */
    @Override
    public int compareTo(PrimitiveData o) {
        if (this == o) {
            return 0;
        }

        String myName = name != null ? name : getClassName();
        String oName = o.name != null ? o.name : o.getClassName();

        int nameComparison = myName.toLowerCase().compareTo(oName.toLowerCase());
        if (nameComparison != 0) {
            return nameComparison;
        }

        return classFQN.compareTo(o.classFQN);
    }

    @Override
    public String toString() {
        return (name != null ? name : getClassName()) + "(" + classFQN + ")";
    }

    /**
     * Get HTML description of the metadata of this object.
     * @return HTML description of the object.
     */
    public String getHtmlDescription() {
        StringBuilder info = new StringBuilder();
        info.append("<html><b>Class:</b> ");
        info.append(classFQN);
        if (name != null) {
            info.append("<br/><b>Name:</b> ");
            info.append(name);
        }
        if (description != null) {
            info.append("<br/><b>Description:</b> ");
            info.append(description);
        }
        if (tags.length > 0) {
            info.append("<br/><b>Tags:</b> ");
            for (int tagIndex = 0; tagIndex < tags.length; ++tagIndex) {
                info.append(tags[tagIndex]);
                if (tagIndex != tags.length - 1) {
                    info.append(',');
                    info.append(' ');
                }
            }
        }
        info.append("</html>");
        return info.toString();
    }
}
