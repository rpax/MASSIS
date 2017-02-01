package com.massisframework.massis.model.components.impl;

import com.jme3.math.Quaternion;
import com.jme3.math.Transform;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.util.TempVars;
import com.massisframework.massis.model.components.TransformComponent;
import com.massisframework.massis.sim.ecs.zayes.SimulationEntity;

public class TransformImpl implements TransformComponent {

	protected Transform localTransform = new Transform();
	protected Transform worldTransform = new Transform();

	private SimulationEntity entity;

	private boolean needsRefresh;

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
		return this.entity.getParent().getC(TransformImpl.class);
	}

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

	public float getAngle()
	{
		TempVars tmp = TempVars.get();
		float angle = this.localTransform.getRotation().toAngles(tmp.fWdU)[1];
		tmp.release();
		return angle;
	}

	public TransformImpl setAngle(float angle)
	{
		TempVars vars = TempVars.get();
		this.setRotation(vars.quat1.fromAngles(0, angle, 0));
		vars.release();
		this.needsRefresh = true;
		return this;
	}

	public TransformImpl rotate(float angle)
	{
		return this.rotate(0, angle, 0);
	}

	public TransformImpl rotate(float xAngle, float yAngle, float zAngle)
	{
		TempVars vars = TempVars.get();
		Quaternion q = vars.quat1;
		q.fromAngles(xAngle, yAngle, zAngle);
		rotate(q);
		vars.release();
		this.needsRefresh = true;
		return this;
	}

	public TransformImpl rotate(Quaternion rot)
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

}
