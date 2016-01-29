package com.massisframework.massis.pathfinding.straightedge;

import java.util.ArrayList;

import straightedge.geom.path.KNodeOfObstacle;
import straightedge.geom.path.PathBlockingObstacleImpl;

public class ObstacleReachableNodesCache {

    PathBlockingObstacleImpl obst;
    ArrayList<Integer> nodesContainedState;
    ArrayList<ArrayList<KNodeOfObstacle>> copyOfEachNodesReachableNodes;

    public ObstacleReachableNodesCache(PathBlockingObstacleImpl obst)
    {
        this.obst = obst;
        nodesContainedState = new ArrayList<Integer>();
        copyOfEachNodesReachableNodes = new ArrayList<ArrayList<KNodeOfObstacle>>();
        for (int i = 0; i < obst.getNodes().size(); i++)
        {
            KNodeOfObstacle node = obst.getNodes().get(i);
            nodesContainedState.add(node.getContained());
            ArrayList<KNodeOfObstacle> currentNodesReachableNodes = new ArrayList<KNodeOfObstacle>();
            for (int j = 0; j < node.getConnectedNodes().size(); j++)
            {
                KNodeOfObstacle reachableNode = (KNodeOfObstacle) node
                        .getConnectedNodes().get(j);
                currentNodesReachableNodes.add(reachableNode);
            }
            copyOfEachNodesReachableNodes.add(currentNodesReachableNodes);
        }
    }

    public void clearAndRefillObstReachableNodes()
    {
        for (int i = 0; i < obst.getNodes().size(); i++)
        {
            KNodeOfObstacle node = obst.getNodes().get(i);
            node.getConnectedNodes().clear();
            ArrayList<KNodeOfObstacle> oldReachableNodes = copyOfEachNodesReachableNodes.get(
                    i);
            for (int j = 0; j < oldReachableNodes.size(); j++)
            {
                KNodeOfObstacle oldReachableNode = oldReachableNodes.get(j);
                node.getConnectedNodes().add(oldReachableNode);
            }
            node.setContained(nodesContainedState.get(i));
        }
    }
}
