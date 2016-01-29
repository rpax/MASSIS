package cz.cuni.amis.pogamut.sposh.elements;

import cz.cuni.amis.pogamut.sposh.executor.IAction;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class represents arguments passed to {@link PrimitiveCall}. {@link PrimitiveCall}
 * can reference either normal {@link IAction}, {@link ActionPattern} or {@link Competence}
 * that will get these arguments, mix them with their own {@link FormalParameters}
 * and create the values for the Action/C/AP.
 *
 * Arguments is stored as list of {@link Argument}, each argument is basically
 * (key; argument) pair. Arguments are sorted according to the key. What is the
 * key? Either a variable name of the argument in the list or
 * arguments. Variable names have '$' prefix, e.g. $name.
 *
 * TODO: This probably doesn;t need to be abstract list
 *
 * Example: call run($dest="street", $speed=$number) will create {@link Arguments}
 * with two arguments, value arg $dest and variable arg $speed.
 *
 * @author Honza
 */
public class Arguments extends AbstractList<Arguments.Argument> {

    public static Arguments EMPTY = new Arguments();
    
    /**
     * Representation of argument in Yaposh plan.
     *
     * @see ValueArgument Representation of value argument, e.g. $degrees=4
     * @see VariableArgument Representatin of variable argument, e.g.
     * $degrees=$ammount
     */
    public static abstract class Argument {

        final private String argumentName;

        /**
         * Create argument with name.
         *
         * @param argumentName
         */
        protected Argument(String argumentName) {
            this.argumentName = argumentName;
        }

        /**
         * Retrieve value of parameter.
         *
         * @return
         */
        public abstract Object getValue();

        /**
         * Is value of this parameter retrieved from variable or value?
         *
         * @return null if this is a value, string with name of parameter
         * variable else
         */
        public abstract String getParameterVariable();

        /**
         * @deprecated Use {@link #getName() } instead, wrongly named.
         */
        @Deprecated
        public String getParameterName() {
            return argumentName;
        }

        public String getName() {
            return argumentName;
        }
        
        @Override
        public abstract String toString();

        @Override
        protected abstract Argument clone();
        
        /**
         * Create new argument object with specified name and value.
         *
         * @param argumentName name of argument.
         * @param value Object representation of the argument value
         * @return
         */
        public static Argument createValueArgument(String argumentName, Object value) {
            return new ValueArgument(argumentName, value);
        }
        
        /**
         * Create new argument with specified name and its value is defined by
         * the content of the variable.
         *
         * @param argumentName name of created argument
         * @param variableName name of variable that will be used to get the
         * value.
         * @return
         */
        public static Argument createVariableArgument(String argumentName, String variableName) {
            return new VariableArgument(argumentName, variableName);
        }
    }

    /**
     * This argument value is dependent on value of some variable.
     */
    protected static final class VariableArgument extends Arguments.Argument {

        protected final String variableName;

        protected VariableArgument(int index, String variableName) {
            super(Integer.toString(index));
            this.variableName = variableName;
        }

        /**
         * New call parameter.
         *
         * @param parameterName name of parameter of called primitive
         * @param variableName variable where is stored the value passed to
         * primitive
         */
        protected VariableArgument(String parameterName, String variableName) {
            super(parameterName);
            this.variableName = variableName;
        }

        @Override
        public String getValue() {
            throw new UnsupportedOperationException("Not yet implemented");
        }

        @Override
        public String getParameterVariable() {
            return variableName;
        }

        @Override
        public String toString() {
            if (getParameterName().startsWith("$")) {
                return getParameterName() + "=" + variableName;
            }
            return variableName;
        }

        @Override
        protected VariableArgument clone() {
            return new VariableArgument(getParameterName(), variableName);
        }
    }

    /**
     * This argument is a static value, neverchanging, e.g. $degrees=5 or
     * $color="red"
     */
    protected static final class ValueArgument extends Arguments.Argument {

        private final Object value;

        /**
         * Create a value argument that represents fixed value. <p> Sequence
         * number is used as name of this argument. Since normal variables
         * starts with $, it won't mix.
         *
         * @param sequenceNumber number of this argument in sequence of all
         * parameters. Starting from 0.
         * @param value value of parameter.
         */
        protected ValueArgument(int sequenceNumber, Object value) {
            super(Integer.toString(sequenceNumber));
            this.value = value;
        }

        protected ValueArgument(String parameterName, Object value) {
            super(parameterName);
            this.value = value;
        }

        @Override
        public Object getValue() {
            return value;
        }

        @Override
        public String getParameterVariable() {
            return null;
        }

        @Override
        public String toString() {
            if (getParameterName().startsWith("$")) {
                return getParameterName() + "=" + Result.toLap(value);
            }
            return Result.toLap(value);
        }

        @Override
        protected ValueArgument clone() {
            return new ValueArgument(getParameterName(), value);
        }
    }
    private List<Arguments.Argument> parameters = new ArrayList<Argument>();

    /**
     * Create a new list of call parameters. <p> Every added variable parameter
     * has to be checked against list of formal parameters.
     */
    public Arguments() {
    }

    /**
     * Copy constructor. Beware, this doesn't copy reference to the formal
     * parameters.
     *
     * @param parameters orginal
     */
    protected Arguments(Arguments parameters) {
        for (int i = 0; i < parameters.size(); ++i) {
            Argument parameter = parameters.get(i);
            Argument clone = parameter.clone();
            this.parameters.add(clone);
        }
    }

    /**
     * Get all names of arguments, sorted.
     * @return Sorted names of all arguments.
     */
    public String[] getAllNames() {
        List<String> names = new ArrayList<String>(this.size());
        for (Arguments.Argument arg : this.parameters) {
            names.add(arg.getName());
        }
        Collections.sort(names);
        return names.toArray(new String[names.size()]);
    }
    
    /**
     * Get indexth argument according to order of specification.
     * @param index Index of argument.
     */
    @Override
    public synchronized Arguments.Argument get(int index) {
        return parameters.get(index);
    }

    /**
     * @return How many {@link Argument arguments} does this list hold.
     */
    @Override
    public synchronized int size() {
        return parameters.size();
    }

    /**
     * Add argument at specified index.
     * @param index Index at which to add argument
     * @param element Argument to add
     */
    @Override
    public void add(int index, Argument element) {
        parameters.add(index, element);
    }
    
    /**
     * Add argument to this list + following check: This list doesn't containe argument with same name as @newArgument.
     * @param newArgument Argument to add to this list.
     * @param formalParams Parameters that are used for check if @newArgument is varaiblae arg.
     * @throws IllegalArgumentException If the check fails
     */
    public synchronized boolean addFormal(Arguments.Argument newArgument, FormalParameters formalParams) {
        // check that named parameter isn't duplicated
        for (int i = 0; i < parameters.size(); ++i) {
            String parameterName = parameters.get(i).getParameterName();
            // check if there isn't already variable name with same name
            if (parameterName != null && parameterName.equals(newArgument.getParameterName())) {
                throw new IllegalArgumentException("Named parameter \"" + newArgument.getParameterName() + "\" has already been defined.");
            }
        }
        return parameters.add(newArgument);
    }

    /**
     * Get string representation of arguments (comma separated arguments,
     * compatible with lap).
     *
     * @return arguments separated with comma (e.g. |"Friends of peace.",
     * $when=now, $who=just_first_player|)
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int argumentIndex = 0; argumentIndex < parameters.size(); ++argumentIndex) {
            if (argumentIndex != 0) {
                sb.append(',');
            }
            Argument argument = parameters.get(argumentIndex);
            sb.append(argument.toString());
        }
        return sb.toString();
    }
}
