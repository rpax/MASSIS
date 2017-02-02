package com.massisframework.massis.model.components.impl;

import com.jme3.math.Quaternion;
import com.jme3.math.Transform;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.util.TempVars;
import com.massisframework.massis.model.components.TransformComponent;
import com.massisframework.massis.sim.ecs.SimulationEntity;

public class TransformImpl implements TransformComponent {

	protected Transform localTransform = new Transform();
	protected Transform worldTransform = new Transform();

	private SimulationEntity entity;

	private boolean needsRefresh;

	@Override
	public Vector2f getPosition(Vector2f store)
	{
		Vector3f tr = localTransform.getTranslation();
		return store.set(tr.x, tr.z);
	}

	private TransformImpl getParent()
	{
		if (this.entity.getParent() == null)
		{
			return null;
		}
		return this.entity.getParent().get(TransformImpl.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.massisframework.massis.model.components.impl.TransformImpl2#
	 * setLocalTranslation(com.jme3.math.Vector2f)
	 */
	@Override
	public void setLocalTranslation(Vector2f tr)
	{
		Vector3f current = this.localTransform.getTranslation();
		this.localTransform.setTranslation(tr.x, current.y, tr.y);
		this.needsRefresh = true;
	}

	public Quaternion getRotation(Quaternion store)
	{
		return this.localTransform.getRotation(store);
	}

	public void setRotation(Quaternion rotation)
	{
		this.localTransform.setRotation(rotation);
		this.needsRefresh = true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.massisframework.massis.model.components.impl.TransformImpl2#getAngle(
	 * )
	 */
	@Override
	public float getAngle()
	{
		TempVars tmp = TempVars.get();
		float angle = this.localTransform.getRotation().toAngles(tmp.fWdU)[1];
		tmp.release();
		return angle;
	}

	@Override
	public TransformComponent setAngle(double angle)
	{
		return this.setAngle((float) angle);
	}

	public TransformComponent setAngle(float angle)
	{
		TempVars vars = TempVars.get();
		this.setRotation(vars.quat1.fromAngles(0, angle, 0));
		vars.release();
		this.needsRefresh = true;
		return this;
	}

	@Override
	public TransformComponent rotate(double angle)
	{
		return rotate((float) angle);
	}

	public TransformComponent rotate(float angle)
	{
		return this.rotate(0, angle, 0);
	}

	public TransformComponent rotate(float xAngle, float yAngle, float zAngle)
	{
		TempVars vars = TempVars.get();
		Quaternion q = vars.quat1;
		q.fromAngles(xAngle, yAngle, zAngle);
		rotate(q);
		vars.release();
		this.needsRefresh = true;
		return this;
	}

	public TransformComponent rotate(Quaternion rot)
	{
		this.localTransform.getRotation().multLocal(rot);
		this.needsRefresh = true;
		return this;
	}

	private static final ThreadLocal<TransformImpl[]> spatialStack = ThreadLocal
			.withInitial(() -> new TransformImpl[32]);

	protected void updateWorldTransforms()
	{
		if (getParent() == null)
		{
			worldTransform.set(localTransform);
			this.needsRefresh = false;
		} else
		{
			// check if transform for parent is updated
			// assert ((parent.refreshFlags & RF_TRANSFORM) == 0);
			worldTransform.set(localTransform);
			worldTransform.combineWithParent(getParent().worldTransform);
			this.needsRefresh = false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.massisframework.massis.model.components.impl.TransformImpl2#
	 * ensureUpdated()
	 */
	@Override
	public void ensureUpdated()
	{
		this.updateWorldTransforms();
	}

	/**
	 * Computes the world transform of this Spatial in the most efficient manner
	 * possible.
	 */
	void checkDoTransformUpdate()
	{
		if (!needsRefresh)
		{
			return;
		}

		if (getParent() == null)
		{
			worldTransform.set(localTransform);
			needsRefresh = false;
		} else
		{

			TransformImpl[] stack = spatialStack.get();
			TransformImpl rootNode = this;
			int i = 0;
			while (true)
			{
				TransformImpl hisParent = rootNode.getParent();
				if (hisParent == null)
				{
					rootNode.worldTransform.set(rootNode.localTransform);
					rootNode.needsRefresh = false;
					i--;
					break;
				}

				stack[i] = rootNode;

				if (!needsRefresh)
				{
					break;
				}

				rootNode = hisParent;
				i++;
			}

			for (int j = i; j >= 0; j--)
			{
				rootNode = stack[j];
				// rootNode.worldTransform.set(rootNode.localTransform);
				// rootNode.worldTransform.combineWithParent(rootNode.parent.worldTransform);
				// rootNode.refreshFlags &= ~RF_TRANSFORM;
				rootNode.updateWorldTransforms();
			}
		}
	}

	@Override
	public float getX()
	{
		return this.localTransform.getTranslation().x;
	}

	@Override
	public float getY()
	{
		return this.localTransform.getTranslation().z;
	}

	@Override
	public TransformComponent setX(double x)
	{
		return setX((float) x);
	}

	@Override
	public TransformComponent setY(double y)
	{
		return setY((float) y);
	}

	@Override
	public TransformComponent setX(float x)
	{
		Vector3f current = this.localTransform.getTranslation();
		this.localTransform.setTranslation(x, current.y, current.z);
		this.needsRefresh = true;
		return this;
	}

	@Override
	public TransformComponent setY(float y)
	{
		Vector3f current = this.localTransform.getTranslation();
		this.localTransform.setTranslation(current.x, current.y, y);
		this.needsRefresh = true;
		return this;
	}

	@Override
	public float distance2D(double x, double y)
	{
		return (float) Math.sqrt(distanceSquared2D(x, y));
	}
	@Override
	public double distanceSquared2D(double otherX, double otherY)
	{
		double dx = getX() - otherX;
		double dy = getY() - otherY;
		return (dx * dx + dy * dy);
	}

}
