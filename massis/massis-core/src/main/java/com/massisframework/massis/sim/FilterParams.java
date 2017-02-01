package com.massisframework.massis.sim;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.massisframework.massis.sim.ecs.zayes.SimulationComponent;

@Retention(RUNTIME)
@Target(FIELD)
public @interface FilterParams {

	public Class<? extends SimulationComponent>[] all() default {};

	public Class<? extends SimulationComponent>[] one() default {};

	public Class<? extends SimulationComponent>[] none() default {};
}
