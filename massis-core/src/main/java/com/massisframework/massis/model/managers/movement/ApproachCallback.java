package com.massisframework.massis.model.managers.movement;

import com.massisframework.massis.model.agents.LowLevelAgent;
import com.massisframework.massis.model.location.Location;
import com.massisframework.massis.pathfinding.straightedge.FindPathResult.PathFinderErrorReason;

public interface ApproachCallback {
	
	public void onSucess(LowLevelAgent agent);
	public void onTargetReached(LowLevelAgent agent);
	public void onPathFinderError(PathFinderErrorReason reason);
	

}
