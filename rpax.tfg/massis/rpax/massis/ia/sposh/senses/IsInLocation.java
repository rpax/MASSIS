package rpax.massis.ia.sposh.senses;

import rpax.massis.ia.sposh.MentalStateKeys;
import rpax.massis.ia.sposh.SimulationContext;
import rpax.massis.model.agents.SPOSHAgent;
import rpax.massis.model.location.Location;
import cz.cuni.amis.pogamut.sposh.executor.Param;
import cz.cuni.amis.pogamut.sposh.executor.PrimitiveInfo;

@PrimitiveInfo(name = "Is in location", description = "True it is in a named location")
public class IsInLocation<V extends SPOSHAgent> extends
		SimulationSense<V, Boolean> implements MentalStateKeys {

	public IsInLocation(SimulationContext<V> ctx) {
		super(ctx);
	}

	public Boolean query(@Param("$location") String attr) {
		Location namedLocation = this.getAgent().getEnvironment()
				.getNamedLocation(attr);
		return namedLocation.isInSameFloor(namedLocation)
				&& namedLocation.distance2D(this.getAgent().getLocation()) < 100;
	}
}
