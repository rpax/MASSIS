//package com.massisframework.massis.model.managers;
//
//import com.massisframework.massis.model.agents.LowLevelAgent;
//import com.massisframework.massis.model.components.Location;
//import com.massisframework.massis.sim.engine.SimulationEngine;
//import com.massisframework.massis.sim.engine.SimulationSystem;
//import com.massisframework.massis.util.collections.filters.Filters;
//
///**
// * Manages the environment information of an agent
// *
// * @author rpax
// *
// */
//public class EnvironmentManager implements SimulationSystem{
//
//	
//    private SimulationEngine engine;
//	@Override
//	public void addedToEngine(SimulationEngine simEngine)
//	{
//		this.engine=simEngine;
//	}
//
//	@Override
//	public void update(float deltaTime)
//	{
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public void removedFromEngine(SimulationEngine simEngine)
//	{
//		// TODO Auto-generated method stub
//		
//	}
//	
////	public Iterable<LowLevelAgent> getAgentsInRange(Location l, double range)
////    {
////        return l.getFloor().getAgentsInRange((int) (l.getX() - range),
////                (int) (l.getY() - range), (int) (l.getX() + range),
////                (int) (l.getY() + range));
////
////    }
////
////    public Iterable<LowLevelAgent> getAgentsInRange(LowLevelAgent a, double range)
////    {
////        return Filters.allExcept(this.getAgentsInRange(a.getLocation(), range),
////                a);
////    }
//
//
//
//}
