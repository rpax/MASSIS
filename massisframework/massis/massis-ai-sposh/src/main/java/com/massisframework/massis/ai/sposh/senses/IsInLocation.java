package com.massisframework.massis.ai.sposh.senses;

import cz.cuni.amis.pogamut.sposh.executor.Param;
import cz.cuni.amis.pogamut.sposh.executor.PrimitiveInfo;
import com.massisframework.massis.ai.sposh.SimulationContext;
import com.massisframework.massis.model.agents.LowLevelAgent;

@PrimitiveInfo(name = "Is in location", description = "True it is in a named location")
public class IsInLocation extends SimulationSense<Boolean>  {

    public IsInLocation(SimulationContext ctx)
    {
        super(ctx);
    }

    public Boolean query(@Param("$location") String attr)
    {
        final LowLevelAgent agent = this.ctx.getBot();
        return agent.isInNamedLocation(attr,100);
                
    }
}
