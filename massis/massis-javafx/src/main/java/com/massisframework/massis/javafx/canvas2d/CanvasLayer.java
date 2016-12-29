package com.massisframework.massis.javafx.canvas2d;

import javafx.scene.canvas.GraphicsContext;

public abstract class CanvasLayer<Model> {

	private String name;
	private boolean enabled;
	private Model model;

	public <T extends CanvasDrawable<Model>> CanvasLayer(T drawable,
			String name)
	{
		this.name = name;
		this.enabled = true;
		this.model = drawable.getModel();
	}

	public final void draw(GraphicsContext gc)
	{
		if (this.enabled)
		{
			this.draw(this.model, gc);
		}
	}

	protected abstract void draw(Model model, GraphicsContext gc);

	public String getName()
	{
		return this.name;
	}

	public boolean isEnabled()
	{
		return enabled;
	}

	public void setEnabled(boolean enabled)
	{
		this.enabled = enabled;
	}

	public void setName(String name)
	{
		this.name = name;
	}

}
