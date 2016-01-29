package cz.cuni.amis.pogamut.sposh.elements;

/**
 * All elements that can exists in the plan and their type names that will be
 * used in the path.
 */
public enum LapType {

    ACTION("A", "Action", TriggeredAction.class),
    ACTION_PATTERN("AP", "Action pattern", ActionPattern.class),
    ADOPT("AD", "Adopt", Adopt.class),
    COMPETENCE("C", "Competence", Competence.class),
    COMPETENCE_ELEMENT("CE", "Choice", CompetenceElement.class),
    DRIVE_COLLECTION("DC", "Drive collection", DriveCollection.class),
    DRIVE_ELEMENT("DE", "drive", DriveElement.class),
    PLAN("P", "Plan", PoshPlan.class),
    SENSE("S", "Sense", Sense.class);

    private final String pathName;
    private final String displayName;
    private final Class<?> typeClass;

    private LapType(String name, String displayName, Class<?> typeClass) {
        this.pathName = name;
        this.displayName = displayName;
        this.typeClass = typeClass;
    }

    /**
     * @return Name of type, use in {@link LapPath}, e.g. "S" for {@link Sense}.
     */
    public String getName() {
        return pathName;
    }

    public String getDisplayName() {
        return displayName;
    }
    
    public Class<?> getTypeClass() {
        return typeClass;
    }
}
