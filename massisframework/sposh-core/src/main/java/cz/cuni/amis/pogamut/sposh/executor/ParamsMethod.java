package cz.cuni.amis.pogamut.sposh.executor;

import cz.cuni.amis.pogamut.sposh.elements.EnumValue;
import cz.cuni.amis.pogamut.sposh.engine.VariableContext;
import cz.cuni.amis.pogamut.sposh.exceptions.FubarException;
import cz.cuni.amis.pogamut.sposh.exceptions.MethodException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Honza
 */
class ParamsMethod<RETURN> {

    private final Class<?>[] allowedParamClasses = new Class<?>[]{
        String.class,
        Integer.class,
        int.class,
        Double.class,
        double.class,
        Boolean.class,
        boolean.class,};
    private final String methodName;
    private final Class<RETURN> returnCls;
    private final Class<?> methodClass;
    private final Method method;

    ParamsMethod(Class methodClass, String methodName, Class<RETURN> returnCls) {
        this.methodClass = methodClass;
        this.methodName = methodName;
        this.returnCls = returnCls;

        this.method = findMethod();
    }

    /**
     * Is every parameter of the @method among @acceptedTypes?
     *
     * @param method Method whose parameters are checked
     * @param isEnumAcceptable Is an enum acceptable parameter?
     * @param acceptedTypes Acceptable types of parameters
     * @return
     */
    private boolean areParamsAcceptable(Method method, boolean isEnumAcceptable, Class<?>... acceptedTypes) {
        for (Class<?> paramType : method.getParameterTypes()) {
            boolean paramAcceptable = false;
            for (Class<?> acceptedType : acceptedTypes) {
                if (paramType.equals(acceptedType)) {
                    paramAcceptable = true;
                }
            }
            if (isEnumAcceptable && paramType.isEnum() && Modifier.isPublic(paramType.getModifiers())) {
                paramAcceptable = true;
            }
            if (!paramAcceptable) {
                return false;
            }
        }
        return true;
    }

    /**
     * Find @seekedAnnotation among array of passed annotations and return it.
     *
     * @param <T> Type of annotation.
     * @param annotations Array of annotations that are being searched in.
     * @param seekedAnnotation Type of annotation this method is looking for
     * @return Return found annotation or null if not present.
     */
    private <T extends Annotation> T getAnnotation(Annotation[] annotations, Class<T> seekedAnnotation) {
        for (Annotation annotation : annotations) {
            if (annotation.annotationType().equals(seekedAnnotation)) {
                return (T) annotation;
            }
        }
        return null;
    }

    /**
     * Are all parameters of @method annotated with {@link Param}?
     *
     * @param method Method whose parameters are checked.
     * @return
     */
    private boolean areParamsAnnotated(Method method) {
        for (Annotation[] paramAnnotations : method.getParameterAnnotations()) {
            if (getAnnotation(paramAnnotations, Param.class) == null) {
                return false;
            }
        }
        return true;
    }

    /**
     * Filter all methods to ensure following conditions:
     *
     * <ul><li>Method name is @name</li>
     *
     * <li>Method is <tt>public<tt></li>
     *
     * <li>Method is not <tt>abstract</tt></li>.
     *
     * <li>Method does not have variable number of arguments</li>
     *
     * <li>Return type of method is @returnType</li>
     *
     * <li>All parameter fields are of type {@link String}, {@link Integer} or {@link Double}</li>
     *
     * <li>All parameters are annotated with {@link Param}</li> </ul>
     *
     * @param methods Array of methods to be filtered.
     * @param seekedName Name of the methods we are looking for.
     * @param returnType Expected return type of filtered methods.
     * @return Array of methods with @name.
     */
    private Method[] filterMethods(Method[] methods, String seekedName, Class<?> returnType) {
        List<Method> filteredMethods = new LinkedList<Method>();

        for (Method testedMethod : methods) {
            String testedMethodName = testedMethod.getName();
            boolean methodIsPublic = (testedMethod.getModifiers() & Modifier.PUBLIC) == Modifier.PUBLIC;
            boolean methodIsAbstract = (testedMethod.getModifiers() & Modifier.ABSTRACT) == Modifier.ABSTRACT;
            boolean correctReturnType = returnType.isAssignableFrom(testedMethod.getReturnType());
            boolean acceptedParams = areParamsAcceptable(testedMethod, true, allowedParamClasses);
            boolean annotatedParams = areParamsAnnotated(testedMethod);

            if (testedMethodName.equals(seekedName)
                    && methodIsPublic
                    && !methodIsAbstract
                    && !testedMethod.isVarArgs()
                    && correctReturnType
                    && acceptedParams
                    && annotatedParams) {
                filteredMethods.add(testedMethod);
            }
        }
        return filteredMethods.toArray(new Method[filteredMethods.size()]);
    }

    /**
     * Go through all methods of the {@link #methodClass} and find the one which
     * has suits our tastes (see {@link #filterMethods(java.lang.reflect.Method[], java.lang.String, java.lang.Class)
     * }) for details.
     *
     * @throws NoSuchMethodError If no such method exists
     * @throws UnsupportedOperationException If there is more than one such
     * method.
     *
     * @return Found method.
     */
    final Method findMethod() {
        Method[] methods = filterMethods(methodClass.getMethods(), methodName, returnCls);
        if (methods.length == 0) {
            throw new NoSuchMethodError("Unable to find method " + methodName);
        }
        if (methods.length > 1) {
            throw new UnsupportedOperationException("Multiple (" + methods.length + ") possible " + methodName + " methods, overloading is not supported.");
        }
        return methods[0];
    }

    /**
     * Take the passed @thisObject and call parametrized method on it.
     *
     * @param params Variable context that will fill the parameters of the
     * method with its variables ({@link Param#value() ) is used as name of variable in the context.
     * @throws InvocationTargetException When invoked method throws an
     * exception.
     */
    public final RETURN invoke(Object thisObject, VariableContext params) throws InvocationTargetException {
        Class<?>[] paramTypes = method.getParameterTypes();
        Annotation[][] paramsAnnotations = method.getParameterAnnotations();

        assert paramsAnnotations.length == paramTypes.length;

        int paramCount = paramTypes.length;

        List methodArguments = new LinkedList();
        for (int paramIndex = 0; paramIndex < paramCount; ++paramIndex) {
            Annotation[] paramAnnotations = paramsAnnotations[paramIndex];
            Param param = getAnnotation(paramAnnotations, Param.class);

            String variableName = param.value();
            Object argumentValue = getArgumentValue(thisObject, params, paramTypes[paramIndex], variableName);
            methodArguments.add(argumentValue);
        }

        try {
            Object ret = method.invoke(thisObject, methodArguments.toArray());
            return (RETURN) ret;
        } catch (IllegalAccessException ex) {
            throw new FubarException("findMethod filters for public methods", ex);
        } catch (IllegalArgumentException ex) {
            throw new FubarException("Error with parameter maching code", ex);
        } catch (InvocationTargetException ex) {
            throw ex;
        }
    }

    private Object getArgumentValue(Object thisObject, VariableContext ctx, Class<?> paramType, String variableName) {
        try {
            Object argumentValue = ctx.getValue(variableName);
            // If the parameter is an enum, the argument passed should be string containing FQN
            // of the enum value.
            if (paramType.isEnum()) {
                if (!argumentValue.getClass().equals(EnumValue.class)) {
                    throw new MethodException("Variable " + variableName + " should be an " + EnumValue.class.getSimpleName() + " string containing the FQN of an enum value.");
                }
                argumentValue = convertToEnumConstant((Class<Enum>)paramType, (EnumValue) argumentValue);
            }

            return argumentValue;
        } catch (IllegalArgumentException ex) {
            String thisObjectName = thisObject.getClass().getName();
            throw new MethodException("No variable " + variableName + " for " + thisObjectName + '.' + methodName + '.', ex);
        }
    }

    /**
     *
     * @param enumClass Class of an enum.
     * @param enumValueFQN Fully qualified name of an value of the enum.
     * @return
     * @throws IllegalArgumentException When enumValueFQN doesn't match the
     * enumClass and its values.
     */
    private Object convertToEnumConstant(Class<Enum> enumClass, EnumValue enumValue) {
        String enumValueFQNPrefix = enumClass.getName() + '.';
        String enumValueFQN = enumValue.getName();
        if (!enumValueFQN.startsWith(enumValueFQNPrefix)) {
            throw new IllegalArgumentException("Unable to convert \"" + enumValueFQN + "\" to the value of enum " + enumClass.getName() + ".");
        }
        String enumName = enumValueFQN.substring(enumValueFQNPrefix.length());
        return Enum.valueOf(enumClass, enumName);
    }
}
