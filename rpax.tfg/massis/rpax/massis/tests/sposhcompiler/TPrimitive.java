package rpax.massis.tests.sposhcompiler;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import cz.cuni.amis.pogamut.sposh.executor.Param;
 
public abstract class TPrimitive extends TransformedElement {
	protected String name;
	protected Map<String, Object> params;

	public TPrimitive(String name, Map<String, Object> map2) {
		this.name = name;
		this.params = map2;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Map<String, Object> getParams() {
		return params;
	}

	public void setParams(Map<String, Object> params) {
		this.params = params;
	}

	public abstract String getBaseMethodName();
	public  abstract Class<?> getReturnType();
	/**
	 * Go through all methods of the {@link #methodClass} and find the one which
	 * has suits our tastes (see
	 * {@link #filterMethods(java.lang.reflect.Method[], java.lang.String, java.lang.Class)
     * }
	 * ) for details.
	 * 
	 * @throws NoSuchMethodError
	 *             If no such method exists
	 * @throws UnsupportedOperationException
	 *             If there is more than one such method.
	 * 
	 * @return Found method.
	 */

	protected Method findMethod(Class<?> methodClass, String methodName,
			Class<?> returnType) {
		Method[] methods = filterMethods(methodClass.getMethods(), methodName,
				returnType);
		if (methods.length == 0)
		{
			throw new NoSuchMethodError("Unable to find method " + methodName);
		}
		if (methods.length > 1)
		{
			throw new UnsupportedOperationException("Multiple ("
					+ methods.length + ") possible " + methodName
					+ " methods, overloading is not supported.");
		}
		return methods[0];
	}

	protected Method[] filterMethods(Method[] methods, String seekedName,
			Class<?> returnType) {
		List<Method> filteredMethods = new LinkedList<Method>();

		for (Method testedMethod : methods)
		{
			String testedMethodName = testedMethod.getName();
			boolean methodIsPublic = (testedMethod.getModifiers() & Modifier.PUBLIC) == Modifier.PUBLIC;
			boolean methodIsAbstract = (testedMethod.getModifiers() & Modifier.ABSTRACT) == Modifier.ABSTRACT;
			boolean correctReturnType = returnType
					.isAssignableFrom(testedMethod.getReturnType());
			boolean acceptedParams = areParamsAcceptable(testedMethod, true,
					allowedParamClasses);
			boolean annotatedParams = areParamsAnnotated(testedMethod);

			if (testedMethodName.equals(seekedName) && methodIsPublic
					&& !methodIsAbstract && !testedMethod.isVarArgs()
					&& correctReturnType && acceptedParams && annotatedParams)
			{
				filteredMethods.add(testedMethod);
			}
		}
		return filteredMethods.toArray(new Method[filteredMethods.size()]);
	}

	/**
	 * Is every parameter of the @method among @acceptedTypes?
	 * 
	 * @param method
	 *            Method whose parameters are checked
	 * @param isEnumAcceptable
	 *            Is an enum acceptable parameter?
	 * @param acceptedTypes
	 *            Acceptable types of parameters
	 * @return
	 */
	protected boolean areParamsAcceptable(Method method,
			boolean isEnumAcceptable, Class<?>... acceptedTypes) {
		for (Class<?> paramType : method.getParameterTypes())
		{
			boolean paramAcceptable = false;
			for (Class<?> acceptedType : acceptedTypes)
			{
				if (paramType.equals(acceptedType))
				{
					paramAcceptable = true;
				}
			}
			if (isEnumAcceptable && paramType.isEnum()
					&& Modifier.isPublic(paramType.getModifiers()))
			{
				paramAcceptable = true;
			}
			if (!paramAcceptable)
			{
				return false;
			}
		}
		return true;
	}

	/**
	 * Are all parameters of @method annotated with {@link Param}?
	 * 
	 * @param method
	 *            Method whose parameters are checked.
	 * @return
	 */
	protected boolean areParamsAnnotated(Method method) {
		for (Annotation[] paramAnnotations : method.getParameterAnnotations())
		{
			if (getAnnotation(paramAnnotations, Param.class) == null)
			{
				return false;
			}
		}
		return true;
	}

	/**
	 * Find @seekedAnnotation among array of passed annotations and return it.
	 * 
	 * @param <T>
	 *            Type of annotation.
	 * @param annotations
	 *            Array of annotations that are being searched in.
	 * @param seekedAnnotation
	 *            Type of annotation this method is looking for
	 * @return Return found annotation or null if not present.
	 */
	@SuppressWarnings("unchecked")
	protected <T extends Annotation> T getAnnotation(Annotation[] annotations,
			Class<T> seekedAnnotation) {
		for (Annotation annotation : annotations)
		{
			if (annotation.annotationType().equals(seekedAnnotation))
			{
				return (T) annotation;
			}
		}
		return null;
	}
	
	protected final Class<?>[] allowedParamClasses = new Class<?>[] {
			String.class, Integer.class, int.class, Double.class, double.class,
			Boolean.class, boolean.class, };

	@Override
	public String transform(int level) {

		try
		{
			Class<?> clazz = Class.forName(this.name);
			Method method = findMethod(clazz, this.getBaseMethodName(),
					this.getReturnType());

			Class<?>[] paramTypes = method.getParameterTypes();
			Annotation[][] paramsAnnotations = method.getParameterAnnotations();

			int paramCount = paramTypes.length;

			ArrayList<String> paramList = new ArrayList<String>();
			for (int paramIndex = 0; paramIndex < paramCount; ++paramIndex)
			{
				Annotation[] paramAnnotations = paramsAnnotations[paramIndex];
				Param param = getAnnotation(paramAnnotations, Param.class);

				String variableName = param.value();
				Object variableValue = params.get(variableName);

				if (variableValue instanceof Integer)
				{
					paramList.add(String.valueOf(variableValue));
				}
				else
				{
					paramList.add("\"" + String.valueOf(variableValue) + "\"");
				}

			}
			StringBuilder sb = new StringBuilder(createTabs(level));
			sb.append(this.name.replace(".", "_"));
			sb.append("."+this.getBaseMethodName());
			sb.append("(");
			for (int i = 0; i < paramList.size(); i++)
			{
				sb.append(paramList.get(i));
				if (i != paramList.size() - 1)
					sb.append(',');
			}
			sb.append(')');
			return sb.toString();

		}
		catch (ClassNotFoundException e)
		{
			return (createTabs(level)+"_" + this.name + "()");
		}

	}

	
}
