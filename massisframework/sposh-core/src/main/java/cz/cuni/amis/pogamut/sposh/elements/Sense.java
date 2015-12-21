package cz.cuni.amis.pogamut.sposh.elements;

import cz.cuni.amis.pogamut.sposh.engine.PoshEngine;
import cz.cuni.amis.pogamut.sposh.exceptions.CycleException;
import cz.cuni.amis.pogamut.sposh.exceptions.DuplicateNameException;
import cz.cuni.amis.pogamut.sposh.exceptions.InvalidFormatException;
import cz.cuni.amis.pogamut.sposh.exceptions.InvalidNameException;
import cz.cuni.amis.pogamut.sposh.executor.IWorkExecutor;
import java.awt.datatransfer.DataFlavor;
import java.io.StringReader;
import java.util.*;

/**
 * Sense is a condition consisting from primitive call, predicate and value.
 *
 * When {@link PoshEngine} askes if sense is true, the sense will call the
 * primitive through {@link IWorkExecutor} and compares returned value by
 * predicate. If no argument was passed, use true, if no predicate was passed,
 * use ==, e.g.
 * <code>ammo</code> is equivalent of
 * <code>ammo true ==</code>,
 * <code>VisiblePlayerCount 4</code> is equivalent of
 * <code>VisiblePlayerCount 4 ==</code>.
 *
 * @author Honza
 */
public class Sense extends PoshDummyElement implements Comparable<Sense>, IReferenceElement, INamedElement {

    /**
     * Various predicates used in <tt>Sense</tt>
     *
     * @author Honza
     */
    public enum Predicate {

        EQUAL(new String[]{"==", "="}),
        NOT_EQUAL("!="),
        LOWER("<"),
        GREATER(">"),
        LOWER_OR_EQUAL("<="),
        GREATER_OR_EQUAL(">="),
        DEFAULT("==");				// default is "=="
        private String[] stringForm = null;

        private Predicate(String form) {
            stringForm = new String[]{form};
        }

        private Predicate(String[] form) {
            stringForm = form;
        }

        /**
         * Get Predicate enum from passed string
         *
         * @param str passed predicate in string form
         * @return Predicate
         */
        public static Predicate getPredicate(String str) {
            if (str == null) {
                return Predicate.DEFAULT;
            }

            str = str.trim();

            for (Predicate p : Predicate.values()) {
                for (String form : p.stringForm) {
                    if (form.equals(str)) {
                        return p;
                    }
                }
            }
            throw new IllegalArgumentException("String \"" + str + "\" is not a predicate.");
        }

        /**
         * Get integer id of predicate. Used because of stupid combo box
         * property pattern in NB.
         *
         * @return
         */
        public int getId() {
            for (int i = 0; i < Predicate.values().length; i++) {
                if (values()[i] == this) {
                    return i;
                }
            }
            throw new RuntimeException("Predicate \"" + this.toString() + "\" wasn't found in list of predicates.");
        }

        @Override
        public String toString() {
            return stringForm[0];
        }

        /**
         * Get all predicates, without {@link #DEFAULT}.
         */
        public static Predicate[] getPredicates() {
            List<Predicate> predicates = new LinkedList<Predicate>();
            for (Predicate predicate : values()) {
                if (predicate != Predicate.DEFAULT) {
                    predicates.add(predicate);
                }
            }
            return predicates.toArray(new Predicate[predicates.size()]);
        }
    }
    /**
     * Name of primitive that will be called along with list of parameters
     */
    private PrimitiveCall senseCall;
    /**
     * When evaluating result of sense, should I compare value returned by the
     * primitive or should I just evaluate primtive and return?
     */
    private boolean compare = true;
    /**
     * Comparator used for comparison of value returned by primitive and operand
     */
    private Predicate _predicate = Predicate.DEFAULT;
    /**
     * Value can be number, string or nil This can be null.
     */
    private Object operand = true;

    /**
     * Create sense that will evaluate true based on result of called primitive.
     * The primitive has no parameters.
     *
     * @param senseName name of primitive
     */
    public Sense(String senseName) {
        this(new PrimitiveCall(senseName));
    }

    /**
     * Create sense that will evaluate based on result of called primitive some
     * parameters
     *
     * @param senseCall details of call primitive
     */
    public Sense(PrimitiveCall senseCall) {
        this.senseCall = senseCall;
        this.compare = false;
    }

    /**
     *
     * @param senseCall
     * @param operand operand that will be parsed and changed into proper object
     * @param predicate
     */
    public Sense(PrimitiveCall senseCall, Object operand, Predicate predicate) {
        assert predicate != null;

        this.senseCall = senseCall;
        this.compare = true;
        this.operand = operand;
        this._predicate = predicate;
    }
    public static final String psSenseName = "paSenseName";
    public static final String psArgs = "paSenseArgs";
    public static final String psPredicateIndex = "paPredicate";
    public static final String psValue = "paValue";
    public static final String psType = "paType";

    /**
     * Get call to the sense primitive.
     */
    public PrimitiveCall getCall() {
        return senseCall;
    }

    /**
     * Set new sense name if new name matches <tt>IDENT_PATTERN</tt>, name will
     * be trimmed.
     *
     * @param newSenseName
     */
    public void setSenseName(String newSenseName) throws InvalidNameException {
        newSenseName = newSenseName.trim();

        if (!newSenseName.matches(IDENT_PATTERN)) {
            throw InvalidNameException.create(newSenseName);
        }

        String oldSenseName = this.senseCall.getName();

        this.senseCall = new PrimitiveCall(newSenseName, senseCall.getParameters());
        this.firePropertyChange(Sense.psSenseName, oldSenseName, newSenseName);
    }

    /**
     * Used in Node.Properties XXX: Do more properly with custom editor
     *
     * @return
     */
    public String getValueString() {
        if (operand == null) {
            return "nil";
        }
        if (operand instanceof String) {
            return '"' + operand.toString() + '"';
        }
        return operand.toString();
    }

    /**
     * Get operand that should be used for evaluating this sense. Default value
     * of operand is true.
     *
     * @return Object representing operand, true, false, string, int, double,
     * even null is valid operand.
     */
    public Object getOperand() {
        return operand;
    }

    /**
     * Set value of argument (as class, e.g. Boolean, String, Integer...).
     *
     * @param newValue number, string or nil
     */
    public void setOperand(Object newValue) {
        firePropertyChange(Sense.psValue, operand, operand = newValue);
    }

    public Integer getPredicateIndex() {
        return this._predicate.getId();
    }

    public Predicate getPredicate() {
        return this._predicate;
    }

    /**
     * Set the predicate in the sense. If set to DEFAULT predicate, it won't be
     * shown.
     *
     * @param newPredicate non null
     */
    public void setPredicate(Predicate newPredicate) {
        compare = true;
        this._predicate = newPredicate;
        this.firePropertyChange(Sense.psPredicateIndex, null, newPredicate.getId());
    }

    /**
     * Set predicate, use index to array Predicate.values()[index]
     *
     * @param newPredicateIndex index to new predicate
     */
    public void setPredicateIndex(Integer newPredicateIndex) {
        if (newPredicateIndex != null) {
            this._predicate = Predicate.values()[newPredicateIndex];
            this.firePropertyChange(Sense.psPredicateIndex, null, newPredicateIndex);
        } else {
            this._predicate = Predicate.DEFAULT;
            this.firePropertyChange(Sense.psPredicateIndex, null, Predicate.DEFAULT.getId());
        }
    }

    @Override
    public Arguments getArguments() {
        return senseCall.getParameters();
    }

    public void setArguments(Arguments newArguments) {
        String senseName = senseCall.getName();
        Arguments oldArguments = senseCall.getParameters();
        this.senseCall = new PrimitiveCall(senseName, newArguments);

        this.firePropertyChange(Sense.psArgs, oldArguments, newArguments);
    }

    /**
     * Get parameters of the node this sense is part of, basically only C
     * returns something non-empty. Parameters are specified after "vars"
     * keyword, e.g in (C vars($a=1, $b=2) (elements ( ((choice (trigger
     * ((sense($b)))) action($a) )) ))), the $a and $b are parameters that can
     * be used in senses or actions.
     *
     * @return Parameters or null if this sense doesn't have parents.
     */
    private FormalParameters getParentParameters() {
        PoshElement parent = getParent();

        if (parent == null) {
            return null;
        }

        if (parent instanceof DriveElement) {
            return new FormalParameters();
        }
        if (parent instanceof DriveCollection) {
            return new FormalParameters();
        }
        if (parent instanceof CompetenceElement) {
            CompetenceElement cel = (CompetenceElement) parent;
            Competence competence = cel.getParent();
            return competence.getParameters();
        }
        throw new IllegalStateException("Unexpected parent " + parent.getClass().getCanonicalName());
    }

    /**
     * TODO: Correctly document, changed in hurry. This is not correct,. we just
     * call PoshParser Take the input that represents the sense in one way or
     * another and set this sense to the values found in the input.
     *
     * Possible inputs: senseName (e.g. 'cz.cuni.Health') | senseName value
     * (e.g. 'cz.cuni.BadlyHurt True') | senseName predicate value (e.g.
     * 'cz.cuni.Health &lt; 90'). In the second case, the default predicate is
     * equality. Value can be bool, number or string in double quotes(it will be
     * properly unescaped).
     *
     * @param input
     * @throws InvalidNameException When name of the sense is not valid.
     * @throws InvalidFormatException When wrong number of tokens in the input.
     */
    public void parseSense(String input) throws ParseException {
        // XXX: quick and dirty hack for full sense
        String adjustedInput = '(' + input + ')';
        PoshParser parser = new PoshParser(new StringReader(adjustedInput));
        FormalParameters parameters = getParentParameters();
        Sense parsedSense = parser.fullSense(parameters);
        changeTo(parsedSense);
    }

    /**
     * Take other sense and change all attributes in the sense to be same as in
     * other sense.
     *
     * @param other
     */
    public void changeTo(Sense other) throws InvalidNameException {
        this.compare = other.compare;
        this.setSenseName(other.getName());
        this.setArguments(other.senseCall.getParameters());
        this.setOperand(other.getOperand());
        this.setPredicateIndex(other.getPredicateIndex());
    }

    /**
     * Return text representation in posh friendly manner. Examples: (senseName)
     * (senseName 2 !=) (senseName "Ahoj")
     *
     * @return
     */
    @Override
    public String toString() {
        String res = "(" + senseCall;

        if (compare) {
            if (operand instanceof String) {
                res += " \"" + operand + '"';
            } else {
                res += " " + operand;
            }
            if (_predicate != Predicate.DEFAULT) {
                res += " " + _predicate;
            }
        }

        return res + ")";
    }

    @Override
    public String getName() {
        return senseCall.getName();
    }

    /**
     * XXX: Not implemented, because I don't have a usecase, but interface has
     * it.
     *
     * Finds all senses in the plan with the same name as this sense and changes
     * their name to newName. } + fire property.
     *
     * @param newName What will be new name of all senses with old name.
     */
    @Override
    public void rename(String newName) throws InvalidNameException, CycleException, DuplicateNameException {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public List<PoshElement> getChildDataNodes() {
        return Collections.<PoshElement>emptyList();
    }

    @Override
    public boolean moveChild(int newIndex, PoshElement child) {
        throw new UnsupportedOperationException();
    }
    public static final DataFlavor dataFlavor = new DataFlavor(Sense.class, "sense");

    @Override
    public DataFlavor getDataFlavor() {
        return dataFlavor;
    }

    @Override
    public LapType getType() {
        return LapType.SENSE;
    }

    /**
     * Get human readable representation of this sense.
     *
     * Examples: 'working', 'cz.Health &gt; 10', 'cz.Distance("ArmyBot",
     * $cutoff=12) &lt; 10'
     *
     * @return Human readable representation of the sense.
     */
    public String getRepresentation() {
        return getRepresentation(getName());
    }

    /**
     * Get string representation of the sense, i.e. sensecall predicate value,
     * but instead of sense name, use the passed name.
     *
     * Examples: 'working', 'cz.Health &gt; 10', 'cz.Distance("ArmyBot",
     * $cutoff=12) &lt; 10'
     *
     *
     * @param name Name that is used instead of name in the {@link PrimitiveCall}
     * @return Human readable representation of the sense.
     */
    public String getRepresentation(String name) {
        StringBuilder representation = new StringBuilder(name);
        Arguments args = senseCall.getParameters();
        if (!args.isEmpty()) {
            representation.append('(').append(args.toString()).append(')');
        }

        boolean predicateIsEqual = _predicate == Predicate.EQUAL || _predicate == Predicate.DEFAULT;
        boolean valueIsTrue = Boolean.TRUE.equals(operand);

        if (!(predicateIsEqual && valueIsTrue)) {
            representation.append(_predicate);
            representation.append(operand);
        }
        return representation.toString();
    }

    @Override
    public int compareTo(Sense o) {
        return this.getRepresentation().compareTo(o.getRepresentation());
    }

    /**
     * Get {@link Trigger} this sense is part of. {@link Trigger} by itself is
     * not a {@link PoshElement}, but it is part of some other element (e.g. {@link DriveCollection}
     * or {@link CompetenceElement})
     *
     * @return The trigger this sense belongs
     * @throws IllegalArgumentException If parent is null or I forgot some
     * possible parent.
     */
    public Trigger<?> getTrigger() {
        PoshElement senseParent = this.getParent();
        Trigger<?> senseTrigger;
        if (senseParent instanceof DriveCollection) {
            DriveCollection dc = (DriveCollection) senseParent;
            senseTrigger = dc.getGoal();
        } else if (senseParent instanceof CompetenceElement) {
            CompetenceElement celParent = (CompetenceElement) senseParent;
            senseTrigger = celParent.getTrigger();
        } else if (senseParent instanceof DriveElement) {
            DriveElement driveParent = (DriveElement) senseParent;
            senseTrigger = driveParent.getTrigger();
        } else {
            throw new IllegalArgumentException("Unexpected parent of sense " + this.getRepresentation() + ": " + senseParent);
        }
        return senseTrigger;
    }

    /**
     * This is a common method for removing the sense from its parent. There are
     * multiple possible parents (DC, CE, DE or no parent) and this method will
     * handle them all. Once the method is finished, this sense is no longer
     * child of its original parent and its parent is null.
     *
     * When calling this method, parent must not be null.
     */
    public void removeFromParent() {
        assert getParent() != null;
        Trigger<?> senseTrigger = getTrigger();
        senseTrigger.remove(this);
    }
}
