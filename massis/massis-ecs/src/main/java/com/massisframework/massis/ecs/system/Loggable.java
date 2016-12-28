package com.massisframework.massis.ecs.system;

import java.util.logging.Logger;

public interface Loggable {

	public default Logger logger(){
		return Logger.getLogger(getClass().getName());
	}
}
