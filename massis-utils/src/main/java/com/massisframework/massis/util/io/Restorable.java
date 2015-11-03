package com.massisframework.massis.util.io;

/**
 * Element that can be saved or restored from a {@link JsonState}
 * 
 * @author rpax
 * 
 */
public interface Restorable {

	public JsonState getState();

}
