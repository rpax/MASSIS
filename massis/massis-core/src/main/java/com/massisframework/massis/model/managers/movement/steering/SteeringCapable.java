package com.massisframework.massis.model.managers.movement.steering;

import com.massisframework.massis.util.geom.KVector;

public interface SteeringCapable {
	 /**
     * Returns the acceleration of the agent in the current step. This
     * acceleration will be applied to the agent's velocity every time {@link #approachTo(rpax.massis.model.location.Location)
     * } method is called and can vary. Should not be modified.
     *
     * @return the acceleration of the agent in the current step.
     */
    public KVector getAcceleration();
    /**
     * Returns the maximum force that can be applied to the agent in order to
     * obtain the acceleration, which will be applied to the agent's velocity.
     *
     * @see #getAcceleration()
     * @return the maximum force that can be applied to the velocity.
     */
    public double getMaxForce();

    /**
     *
     * @return the maximum speed of the agent
     */
    public double getMaxSpeed();
    /**
     * Retrieves the current velocity of the agent
     *
     * @return the current velocity of the agent
     */
    public KVector getVelocity();
    
    /**
	 * Applies the forces given to the current position and velocity of the
	 * agent, producing a new position and a new velocity. The agent is updated
	 * and moved accordingly
	 *
	 * @param vehicle
	 *            the agent to move
	 * @param forces
	 *            the forces applied to that agent
	 */
	public void applySteeringForcesAndMove(KVector forces);
	
	
	
	
}
