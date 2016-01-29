package cz.cuni.amis.pogamut.sposh.elements;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * {@link LapPath} is used to describe path from the root of {@link PoshPlan} to
 * some subnode of the plan. The path consists from several links, each link
 * describes which cild of current node should be used to change into new
 * current node.
 *
 * There are three major features we require:
 *
 * 1. Parse path from string
 *
 * 2. traverse plan according to the path and return the node.
 *
 * 3. serialize path to the string
 *
 * Examples of serialized path:
 *
 * /P:0/DC:0 - default plan, drive collection 0
 *
 * /P:0/DC:0/S:1 - Drive collection 0, goal sense 1
 *
 * /P:0/DC:0/DE:4 - fifth drive in the DC
 *
 * /P:0/DC:0/DE:4/S:0 - first trigger sense of fifth drive in the DC
 *
 * /P:0/DC:0/DE:4/A:0/AP:4 - reference to AP node.
 *
 * @author Honza
 */
public final class LapPath implements Iterable<LapPath.Link> {

    /**
     * Empty path with no links.
     */
    public static final LapPath EMPTY = new LapPath();
    /**
     * Path to the root of the plan, equivalent of <tt>/P:0</tt>
     */
    public static LapPath PLAN_PATH = new LapPath().concat(LapType.PLAN, 0);
    /**
     * Path to the {@link LapType#DRIVE_COLLECTION}. Equivalent of
     * <tt>/P:0/DC:0</tt>
     */
    public static LapPath DRIVE_COLLECTION_PATH = new LapPath().concat(LapType.PLAN, 0).concat(LapType.DRIVE_COLLECTION, 0);
    /**
     * Character used to separate links.
     */
    private static char LINK_SEPARATOR = '/';
    /**
     * Character used to separate type from id in the {@link Link}.
     */
    private static char TYPE_SEPARATOR = ':';
    /**
     * All links of the path. Each link determines next current node.
     */
    private final Link[] links;

    public LapPath() {
        this.links = new Link[0];
    }

    LapPath(List<Link> links) {
        this.links = links.toArray(new Link[links.size()]);
    }

    /**
     * Make sure that link from {@link Iterator#next() } is {@link LapType#PLAN}
     * with id 0.
     */
    private void traverseRoot(Iterator<Link> iter) {
        if (!iter.hasNext()) {
            throw new IllegalStateException("No plan specified in the path.");
        }
        Link rootLink = iter.next();
        if (rootLink.type != LapType.PLAN) {
            throw new IllegalStateException("First link always has type " + LapType.PLAN + ", but is " + rootLink.type);
        }
        if (rootLink.id != 0) {
            throw new IndexOutOfBoundsException("Id of plan must be 0, but is " + rootLink.id);
        }
    }

    /**
     * Make sure that link from {@link Iterator#next() } is {@link LapType#DRIVE_COLLECTION}
     * with id 0.
     */
    private void traversePlan(Iterator<Link> iter) {
        Link planLink = iter.next();
        if (planLink.type != LapType.DRIVE_COLLECTION) {
            throw new IllegalStateException("Second link always has type " + LapType.DRIVE_COLLECTION + ", but is " + planLink.type);
        }
        if (planLink.id != 0) {
            throw new IndexOutOfBoundsException("Id of drive collection must be 0, but is " + planLink.id);
        }
    }

    /**
     * Traverse path from {@link DriveCollection} downwards. Use link from {@link Iterator#next()
     * }.
     */
    private static PoshElement traverseDriveCollection(Iterator<Link> iter, PoshPlan plan) {
        Link driveCollectionLink = iter.next();
        DriveCollection driveCollection = plan.getDriveCollection();

        if (driveCollectionLink.type == LapType.DRIVE_ELEMENT) {
            DriveElement drive = driveCollection.getDrives().get(driveCollectionLink.id);
            if (!iter.hasNext()) {
                return drive;
            }
            return traverseDrive(iter, plan, drive);
        } else if (driveCollectionLink.type == LapType.SENSE) {
            Sense goalSense = driveCollection.getGoal().get(driveCollectionLink.id);
            if (iter.hasNext()) {
                throw new IllegalStateException("Goal sense must be last link, but isn't.");
            }
            return goalSense;
        } else {
            throw new IllegalStateException("Link was expected to be DRIVE or SENSE, but is " + driveCollectionLink.type);
        }
    }

    /**
     * Traverse path from drive downwards. Use link from {@link Iterator#next()
     * }.
     */
    private static PoshElement traverseDrive(Iterator<Link> iter, PoshPlan plan, DriveElement drive) {
        Link driveLink = iter.next();
        // now we are in drive, either action or 
        if (driveLink.type == LapType.SENSE) {
            Sense driveTriggerSense = drive.getTrigger().get(driveLink.id);
            if (iter.hasNext()) {
                throw new IllegalStateException("Drive trigger sense must be last link, but isn't.");
            }
            return driveTriggerSense;
        } else if (driveLink.type == LapType.ACTION) {
            if (driveLink.id != 0) {
                throw new IndexOutOfBoundsException("Id of action in drive should be 0, but is " + driveLink.id);
            }
            TriggeredAction action = drive.getAction();
            if (!iter.hasNext()) {
                return action;
            }
            return traverseReference(iter, plan, action);
        } else {
            throw new IllegalStateException("Link was expected to be SENSE or ACTION, but is " + driveLink.type);
        }
    }

    /**
     * Traverse reference {@link TriggeredAction}. Use link from {@link Iterator#next()
     * }. This can be used only if {@link TriggeredAction} actually references
     * something.
     */
    private static PoshElement traverseReference(Iterator<Link> iter, PoshPlan plan, TriggeredAction reference) {
        Link referenceLink = iter.next();
        if (referenceLink.type == LapType.ADOPT) {
            Adopt adopt = plan.getAdopts().get(referenceLink.id);
            if (!reference.getName().equals(adopt.getName())) {
                throw new IllegalStateException("Reference link with name " + reference.getName() + " does not match referencing Adopt " + referenceLink.id + " with name " + adopt.getName());
            }
            if (!iter.hasNext()) {
                return adopt;
            }
            return traverseAdopt(iter, plan, adopt);
        } else if (referenceLink.type == LapType.ACTION_PATTERN) {
            ActionPattern actionPattern = plan.getActionPatterns().get(referenceLink.id);
            if (!reference.getName().equals(actionPattern.getName())) {
                throw new IllegalStateException("Reference link with name " + reference.getName() + " does not match referencing AP " + referenceLink.id + " with name " + actionPattern.getName());
            }
            if (!iter.hasNext()) {
                return actionPattern;
            }
            return traverseActionPattern(iter, plan, actionPattern);
        } else if (referenceLink.type == LapType.COMPETENCE) {
            Competence competence = plan.getCompetences().get(referenceLink.id);
            if (!reference.getName().equals(competence.getName())) {
                throw new IllegalStateException("Reference link with name " + reference.getName() + " does not match referencing C " + referenceLink.id + " with name " + competence.getName());
            }
            if (!iter.hasNext()) {
                return competence;
            }
            return traverseCompetence(iter, plan, competence);
        } else {
            throw new IllegalStateException("Link was expected to be ADOPT, ACTION_PATTERN or COMPETENCE, but is " + referenceLink.type);
        }
    }

    /**
     * Traverse from adopt node to next item. Use link from {@link Iterator#next()
     * }, it is guaranteed to exist.
     */
    private static PoshElement traverseAdopt(Iterator<Link> iter, PoshPlan plan, Adopt adopt) {
        Link adoptLink = iter.next();
        if (adoptLink.type == LapType.SENSE) {
            Sense exitConditionSense = adopt.getExitCondition().get(adoptLink.id);
            if (iter.hasNext()) {
                throw new IllegalStateException("Exit condition sense of adopt is not last link in path.");
            }
            return exitConditionSense;
        } else if (adoptLink.type == LapType.ACTION) {
            if (adoptLink.id != 0) {
                throw new IllegalStateException("Id of action in adopt should be 0, but is " + adoptLink.id);
            }
            TriggeredAction action = adopt.getAdoptedElement();
            if (!iter.hasNext()) {
                return action;
            }
            return traverseReference(iter, plan, action);
        } else {
            throw new IllegalStateException("Expecting sense or action in adopt, got " + adoptLink.type);
        }
    }

    /**
     * Traverse from action pattern to next item. Use link from {@link Iterator#next()
     * }.
     */
    private static PoshElement traverseActionPattern(Iterator<Link> iter, PoshPlan plan, ActionPattern actionPattern) {
        Link actionPatternLink = iter.next();
        if (actionPatternLink.type != LapType.ACTION) {
            throw new IllegalStateException("Action pattern can have only action subnodes.");
        }

        TriggeredAction action = actionPattern.getActions().get(actionPatternLink.id);
        if (!iter.hasNext()) {
            return action;
        }
        return traverseReference(iter, plan, action);
    }

    /**
     * Traverse from choice to next node. Use link from {@link Iterator#next()
     * }.
     */
    private static PoshElement traverseCompetence(Iterator<Link> iter, PoshPlan plan, Competence competence) {
        Link competenceLink = iter.next();
        if (competenceLink.type != LapType.COMPETENCE_ELEMENT) {
            throw new IllegalStateException("Link from competence can be only choice, but is " + competenceLink.type);
        }
        CompetenceElement choice = competence.getChildDataNodes().get(competenceLink.id);
        if (!iter.hasNext()) {
            return choice;
        }
        return traverseChoice(iter, plan, choice);
    }

    /**
     * Traverse from choice to next item. Use link from {@link Iterator#next()
     * }.
     */
    private static PoshElement traverseChoice(Iterator<Link> iter, PoshPlan plan, CompetenceElement choice) {
        Link choiceLink = iter.next();
        if (choiceLink.type == LapType.SENSE) {
            Sense choiceTriggerSense = choice.getTrigger().get(choiceLink.id);
            if (iter.hasNext()) {
                throw new IllegalStateException("Trigger sense of choice is not last link in path.");
            }
            return choiceTriggerSense;
        } else if (choiceLink.type == LapType.ACTION) {
            if (choiceLink.id != 0) {
                throw new IllegalStateException("Id of action in choice should be 0, but is " + choiceLink.id);
            }
            TriggeredAction action = choice.getAction();
            if (!iter.hasNext()) {
                return action;
            }
            return traverseReference(iter, plan, action);
        } else {
            throw new IllegalStateException("Expecting sense or action in choice, got " + choiceLink.type);
        }
    }

    /**
     * Methods with name traverseXYZ mean traverse from XYZ below xyzLink means
     * gor from xyz below according to type and id.
     *
     * @param plan
     * @return Found node
     * @throws IllegalStateException If path does not play nice with tree, e.g.
     * when paths wants sense subnode in AP, but there is none according to
     * syntax
     * @throws IndexOutOfBoundsException If some id on the path references to
     * nonexistent node.
     */
    public <T extends PoshElement> T traversePath(PoshPlan plan) {
        Iterator<Link> iterator = Arrays.asList(links).iterator();

        traverseRoot(iterator);
        if (!iterator.hasNext()) {
            return (T) plan;
        }

        traversePlan(iterator);
        if (!iterator.hasNext()) {
            return (T) plan.getDriveCollection();
        }

        return (T) traverseDriveCollection(iterator, plan);
    }

    /**
     * Construct path from the @endElement to its very first branch node(DC, AD,
     * AP, C) upward in the tree. E.g. if @endElement is third trigger {@link LapType#SENSE}
     * in the second {@link CompetenceElement} of fourth {@link Competence}, the
     * path will be something like <tt>/C:4/CE:2/S:3</tt>, since the {@link Competence}
     * is a branch node.
     *
     * @param endElement Element whose ancestor we use to create path.
     * @return Path from closest ancestor branch node of element to @endElement
     */
    public static LapPath getLinkPath(PoshElement endElement) {
        LapPath path = LapPath.EMPTY;
        PoshElement element = endElement;
        PoshPlan plan = endElement.getRootNode();
        while (element != plan) {
            path = new LapPath().concat(element.getType(), element.getId()).concat(path);
            element = element.getParent();
        }
        return path;
    }

    /**
     * Get string from @iter until end of string or until terminator is
     * encountered.
     *
     * @param iter Character iterator.
     */
    private static String getStringUntil(CharacterIterator iter, char terminator) {
        StringBuilder sb = new StringBuilder();
        for (char currentChar = iter.current();
                currentChar != CharacterIterator.DONE && currentChar != terminator;
                currentChar = iter.next()) {
            sb.append(currentChar);
        }
        return sb.toString();
    }

    private static String getDecimals(CharacterIterator iter) {
        StringBuilder sb = new StringBuilder();
        for (char currentChar = iter.current();
                currentChar >= '0' && currentChar <= '9';
                currentChar = iter.next()) {
            sb.append(currentChar);
        }
        return sb.toString();
    }

    /**
     * Take the @iter and get the link from it. Link consists from {@link #LINK_SEPARATOR}, {@link LapType}, {@link #TYPE_SEPARATOR}
     * and integer id.
     *
     * @param iter Iterator from which to get characters.
     * @return
     */
    private static Link parseLink(CharacterIterator iter) throws ParseException {

        char linkSeparator = iter.current();
        if (linkSeparator == CharacterIterator.DONE || linkSeparator != LapPath.LINK_SEPARATOR) {
            throw new ParseException("Expected " + LapPath.LINK_SEPARATOR + " at " + iter.getIndex());
        }
        iter.next();
        String typeString = getStringUntil(iter, LapPath.TYPE_SEPARATOR);

        LapType linkType = null;
        for (LapType type : LapType.values()) {
            if (type.getName().equals(typeString)) {
                linkType = type;
            }
        }
        if (linkType == null) {
            throw new ParseException("No LapType '" + typeString + "' exists.");
        }


        char typeSeparatorChar = iter.current();
        if (typeSeparatorChar == CharacterIterator.DONE || typeSeparatorChar != LapPath.TYPE_SEPARATOR) {
            throw new ParseException("Expected " + LapPath.TYPE_SEPARATOR + " at " + iter.getIndex());
        }
        iter.next();

        try {
            String idString = getDecimals(iter);
            int linkId = Integer.parseInt(idString);

            return new Link(linkType, linkId);
        } catch (NumberFormatException ex) {
            throw new ParseException(ex.getMessage());
        }
    }

    /**
     * Parse @serializedPath to
     *
     * @param serializedPath {@link LapPath} in serialized form, e.g.
     * /P:0/DC:0/DE:1/S:1
     * @return Path object created according to @serializedPath
     */
    public static LapPath parse(String serializedPath) throws ParseException {
        CharacterIterator iter = new StringCharacterIterator(serializedPath);

        List<Link> parsedLinks = new LinkedList<Link>();
        do {
            parsedLinks.add(parseLink(iter));
        } while (iter.current() != CharacterIterator.DONE);
        return new LapPath(parsedLinks);
    }

    /**
     * Create and return new LapPath by appending new link to all links of
     * current path. This method does not change this path (LapPath is
     * immutable)/
     *
     */
    public LapPath concat(LapType type, int id) {
        List<Link> newPathLinks = new LinkedList<Link>(Arrays.asList(links));
        newPathLinks.add(new Link(type, id));

        return new LapPath(newPathLinks);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Link link : links) {
            sb.append(link.toString());
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final LapPath other = (LapPath) obj;
        if (!Arrays.deepEquals(this.links, other.links)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 59 * hash + Arrays.deepHashCode(this.links);
        return hash;
    }

    /**
     * Create new path consisting from this path and @appendPath
     *
     * @param appendPath Path that will be appended to this and returned.
     */
    public LapPath concat(LapPath appendPath) {
        List<Link> concatLinks = new LinkedList<Link>(Arrays.asList(this.links));
        concatLinks.addAll(Arrays.asList(appendPath.links));

        LapPath concatPath = new LapPath(concatLinks);
        return concatPath;
    }

    /**
     * @return new path from all links of this this path + appendedLink.
     */
    public LapPath concat(Link appendedLink) {
        List<Link> concatLinks = new LinkedList<Link>(Arrays.asList(this.links));
        concatLinks.add(appendedLink);

        LapPath concatPath = new LapPath(concatLinks);
        return concatPath;
    }

    /**
     * Return subpath of this path.
     *
     * @param beginIndex The beginning link index, inclusive.
     * @param endIndex The ending link index, exclusive.
     * @return subpath.
     * @throws IndexOutOfBoundsException If beginIndex &lt; 0 or endIndex &gt;
     * length of path or beginIndex &gt; endIndex.
     */
    public LapPath subpath(int beginIndex, int endIndex) {
        if (beginIndex < 0) {
            throw new IndexOutOfBoundsException("beginIndex(" + beginIndex + ") is negative.");
        }
        if (endIndex > links.length) {
            throw new IndexOutOfBoundsException("endIndex(" + endIndex + ") is greater than length of path(" + links.length + ").");
        }
        if (beginIndex > endIndex) {
            throw new IndexOutOfBoundsException("beginIndex(" + beginIndex + ") is greater than endIndex(" + endIndex + ").");
        }
        Link[] subarray = Arrays.copyOfRange(links, beginIndex, endIndex);
        return new LapPath(Arrays.asList(subarray));
    }

    /**
     * @return Number of links in the path.
     */
    public int length() {
        return links.length;
    }

    @Override
    public Iterator<Link> iterator() {
        return Arrays.asList(links).iterator();
    }

    public Link getLink(int linkId) {
        return links[linkId];
    }

    /**
     * Get index of first link equal to the @link.
     *
     * @param searchedLink link to search for
     * @return Index of first occurance of @link in links of the path. -1 If not found.
     */
    int getLinkIndex(Link searchedLink) {
        for (int index = 0; index < links.length; ++index) {
            if (links[index] == searchedLink) {
                return index;
            }
        }
        throw new IllegalArgumentException("Unable to find the link.");
    }

    /**
     * One link of the path, immutable.
     */
    public static class Link {

        /**
         * Type of this link
         */
        private final LapType type;
        /**
         * Id of this link. Id means that we take all children of current node
         * that have correct {@link #type} and pick the {@link #id}th one.
         */
        private final int id;

        public Link(LapType type, int id) {
            this.type = type;
            this.id = id;
        }

        /**
         * @return Type of path link
         */
        public LapType getType() {
            return type;
        }

        /**
         * @return Id of link. Id is index into list of all children of parent
         * with same {@link #getType() }.
         */
        public int getId() {
            return id;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();

            sb.append(LINK_SEPARATOR);
            sb.append(type.getName());
            sb.append(TYPE_SEPARATOR);
            sb.append(id);

            return sb.toString();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Link other = (Link) obj;
            if (this.type != other.type) {
                return false;
            }
            if (this.id != other.id) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 37 * hash + (this.type != null ? this.type.hashCode() : 0);
            hash = 37 * hash + this.id;
            return hash;
        }
    }
}