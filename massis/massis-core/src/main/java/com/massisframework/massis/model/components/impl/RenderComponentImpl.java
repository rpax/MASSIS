package com.massisframework.massis.model.components.impl;

import com.massisframework.massis.model.components.JFXRenderer;
import com.massisframework.massis.model.components.RenderComponent;

public class RenderComponentImpl implements RenderComponent{

	private JFXRenderer renderer;

	@Override
	public void setRenderer(JFXRenderer r)
	{
		this.renderer=r;
		
	}

}
