package cz.cuni.amis.pogamut.sposh.engine;

import cz.cuni.amis.pogamut.sposh.elements.Arguments;
import cz.cuni.amis.pogamut.sposh.elements.Arguments.Argument;
import cz.cuni.amis.pogamut.sposh.elements.FormalParameters;
import cz.cuni.amis.pogamut.sposh.elements.Result;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * This class is responsible for storing and passing arguments throught the posh plan.
 * Unnamed varibales are named as number (e.g. "0", "1"...), named variables
 * have same name as in plan.
 * @author Honza
 */
final public class VariableContext {
    private VariableContext parent;
    private Map<String, Object> vars = new HashMap<String, Object>();

    public VariableContext() {
        this.parent = null;
    }
    
    /**
     * Create new VariableContext that will contain only variables from call arguments.
     * @param callArgs
     */
    public VariableContext(VariableContext ctx, Arguments callArgs) {
        this.parent = ctx;
        for (Arguments.Argument param : callArgs) {
            String argumentVariable = param.getParameterVariable();
            if (argumentVariable != null) {
                vars.put(param.getName(), ctx.getValue(argumentVariable));
            } else {
                vars.put(param.getName(), param.getValue());
            }
        }
    }

    /**
     * Create new variable context for diving into another function.
     * New context will contain only variables from formal parameters, and its
     * values will be values passed from call arguments or (if missing) default value
     * from formal parametrs.
     * 
     * @param ctx
     * @param callParameters
     * @param formalParameters
     */
    public VariableContext(VariableContext ctx, Arguments callParameters, FormalParameters formalParameters) {
        this.parent = ctx;
        HashSet<Argument> unusedArgs = new HashSet<Argument>(callParameters);
        for (int index = 0; index < formalParameters.size(); ++index) {
            String ctxVariableName = formalParameters.get(index).getName();
            Object ctxVariableValue = formalParameters.get(index).getDefaultValue();

            // if user supplied value, get it and put it into ctxVariableValue instead of default value
            for (Argument param : callParameters) {
                String argumentName = param.getName();
                try {
                    int paramterIndex = Integer.parseInt(argumentName);
                    if (index != paramterIndex) {
                        continue;
                    }
                } catch (NumberFormatException ex) {
                    // nope, this is not a unnamed parametr, this one has a name
                    // Is name among formal parametrs?
                    String parameterName = param.getName();
                    if (!parameterName.equals(ctxVariableName))
                        continue;
                }
                
                unusedArgs.remove(param);
                
                String variableName = param.getParameterVariable();
                if (variableName != null) {
                    ctxVariableValue = ctx.getValue(variableName);
                } else {
                    ctxVariableValue = param.getValue();
                }
            }

            vars.put(ctxVariableName, ctxVariableValue);
        }
        for (Argument unusedArg : unusedArgs) {
            String variableName = unusedArg.getParameterVariable();
            if (variableName != null) {
                if (ctx.hasVariable(variableName)) {
                    vars.put(unusedArg.getName(), ctx.getValue(variableName));
                }
            } else {
                vars.put(unusedArg.getName(), unusedArg.getValue());
            }
        }
    }

    /**
     * Put another variable into this context
     * @param parameterName name of new parameter
     * @param value value of parameter
     * @return old value for parameterName or null if it is a new parameter.
     */
    public synchronized Object put(String parameterName, Object value) {
        return vars.put(parameterName, value);
    }

    /**
     * Get keys of all variables in this context.
     * @return
     */
    public synchronized String[] getKeys() {
        Set<String> allKeys = new HashSet<String>();
        VariableContext currentCtx = this;
        while (currentCtx != null) {
            allKeys.addAll(currentCtx.vars.keySet());
            currentCtx = currentCtx.parent;
        }
        return allKeys.toArray(new String[allKeys.size()]);
    }

    /**
     * Get value stored in the 
     * @param variableName
     * @return
     * @throws IllegalArgumentException If such variable is not present.
     */
    public synchronized Object getValue(String variableName) {
        if (vars.containsKey(variableName)) {
            return vars.get(variableName);
        }
        if (this.parent != null) {
            return parent.getValue(variableName);
        }
        throw new IllegalArgumentException("There is no variable in the context with name \"" + variableName + "\".");
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append('[');
        String[] list = new String[vars.size()];
        int index = 0;
        for (Entry entry : vars.entrySet()) {
            list[index++] = entry.getKey() + "=" + Result.toLap(entry.getValue());
        }
        Arrays.sort(list);
        for (int i = 0; i < list.length; ++i) {
            if (i != 0) {
                sb.append(", ");
            }
            sb.append(list[i]);
        }

        sb.append(']');
        return sb.toString();
    }

    public synchronized int size() {
        return this.vars.size();
    }

    public boolean hasVariable(String variableName) {
        if (vars.containsKey(variableName)) {
            return true;
        }
        if (this.parent != null) {
            return parent.hasVariable(variableName);
        }
        return false;
    }
}
