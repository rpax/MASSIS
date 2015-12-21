package cz.cuni.amis.pogamut.sposh.elements;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

/**
 * List of parameters for element. The only {@link PoshElement elements} that can have parameters are {@link Competence} and {@link ActionPattern}.
 * 
 * Specialized class is used instead of some Collection, because I want to
 * keep order in which parameters were added, but also throw error on duplicates.
 * 
 * @author Honza
 */
final public class FormalParameters extends AbstractList<FormalParameters.Parameter> {

    /**
     * Copy constructor.
     * @param parameters
     */
    FormalParameters(FormalParameters parameters) {
        this.parameters = new ArrayList<Parameter>();
        for (FormalParameters.Parameter param : parameters) {
            this.add(param);
        }
    }

    /**
     * This class represents one formal parameter of C or AP.
     * <p>
     * Formal parameters are typeless variables that always have default value that
     * is used when caller "forgets" to specify the parameter.
     * @author Honza
     */
    final public static class Parameter {

        private final String name;
        private Object defaultValue;

        /**
         * Create new formal parameter for C or AP.
         * @param name name of parameter, always start with "$"
         * @param defaultValue value that is used when no value is passed for this parameter
         */
        public Parameter(String name, Object defaultValue) {
            // XXX: this should test if same as VARIABLE token in posh parser.
            assert name.startsWith("$");
            assert !name.matches("\\s"); 
            this.name = name;
            this.defaultValue = defaultValue;
        }

        /**
         * @return the name of parameter, always starts with $.
         */
        public String getName() {
            return name;
        }

        /**
         * @return the defaultValue of parameter, as parsed object.
         */
        public Object getDefaultValue() {
            return defaultValue;
        }

        /**
         * Set the default value of parameter to the passed object.
         * @return the defaultValue of parameter
         */
        public void setDefaultValue(Object defaultValue) {
            this.defaultValue = defaultValue;
        }

        @Override
        public String toString() {
            return getName() + "=" + Result.toLap(getDefaultValue());
        }
    }
    private List<Parameter> parameters;

    public FormalParameters() {
        parameters = new ArrayList<Parameter>();
    }

    @Override
    public Parameter get(int index) {
        return parameters.get(index);
    }

    @Override
    public int size() {
        return parameters.size();
    }

    @Override
    public Parameter set(int index, Parameter element) {
        for (Parameter param : parameters) {
            if (param.getName().equals(element.getName()) && !param.equals(element)) {
                throw new IllegalArgumentException("Parameter with name \"" + element.getName() + "\" has already been specified in formal parameters.");
            }
        }
        return parameters.set(index, element);
    }

    @Override
    public void add(int index, Parameter element) {
        //Check that parameter with same name hasn't already been inserted
        for (Parameter param : parameters) {
            if (param.getName().equals(element.getName())) {
                throw new IllegalArgumentException("Parameter with name \"" + element.getName() + "\" has already been specified in formal parameters.");
            }
        }
        parameters.add(index, element);
    }

    @Override
    public Parameter remove(int index) {
        return parameters.remove(index);
    }

    /**
     * Does this list of formal parameters contains variable with name
     * variableName?
     * @param variableName variableName we are checking.
     * @return true if it contais variable with same name.
     */
    public boolean containsVariable(String variableName) {
        for (FormalParameters.Parameter param : parameters) {
            if (param.getName().equals(variableName)) {
                return true;
            }
        }
        return false;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("");

        for (int i=0; i<parameters.size();++i) {
            String parameterString = parameters.get(i).toString();
            if (i==0) {
                sb.append(parameterString);
            } else {
                sb.append(", " + parameterString);
            }
        }
        return sb.toString();
    }
}
