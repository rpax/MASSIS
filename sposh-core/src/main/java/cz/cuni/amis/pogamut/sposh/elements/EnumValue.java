package cz.cuni.amis.pogamut.sposh.elements;

/**
 * This class is one of possible values of variable in the Yaposh plan. When you
 * specify the value
 */
public final class EnumValue {

    private final String name;

    public EnumValue(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final EnumValue other = (EnumValue) obj;
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + (this.name != null ? this.name.hashCode() : 0);
        return hash;
    }

    /**
     * Get fully qualified name of the enum.
     */
    public String getEnumFQN() {
        int lastSeparatorIndex = name.lastIndexOf('.');
        assert lastSeparatorIndex != -1;
        return name.substring(0, lastSeparatorIndex);
    }

    /**
     * Get name of the enum value, e.g. java.EnumTest.VALUE has simple name
     * VALUE.
     */
    public String getSimpleName() {
        return name.substring(name.lastIndexOf('.') + 1);
    }
}
