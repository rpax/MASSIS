package com.massisframework.massis.sim.ecs.injection;

import com.google.inject.Binder;
import com.google.inject.TypeLiteral;
import com.google.inject.binder.ScopedBindingBuilder;
import com.google.inject.util.Types;

public class TypeLiterals {

	public static <T> TypeLiteral<T> createWildTL(Class<T> type)
	{
		return createParametrizedTL(type, Object.class);
	}

	public static <T> TypeLiteral<T> createParametrizedTL(Class<T> type,
			Class<?> paramType)
	{
		return (TypeLiteral<T>) TypeLiteral.get(Types
				.newParameterizedType(
						type,
						Types.subtypeOf(paramType)));
	}

	public static <T, I extends T> ScopedBindingBuilder bindWild(Class<T> type,
			Class<I> impl, Binder binder)
	{
		binder.bind(type).to(impl);
		return binder.bind(createWildTL(type)).to(impl);
	}

}
