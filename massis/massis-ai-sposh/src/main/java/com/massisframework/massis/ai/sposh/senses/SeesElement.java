package com.massisframework.massis.ai.sposh.senses;

import cz.cuni.amis.pogamut.sposh.executor.Param;
import cz.cuni.amis.pogamut.sposh.executor.PrimitiveInfo;
import com.massisframework.massis.ai.sposh.SimulationContext;
import com.massisframework.massis.model.agents.LowLevelAgent;

/**
 * True if sees another with the attribute and value provided
 *
 * @author rpax
 *
 * @param <V>
 */
@PrimitiveInfo(
        name = "Sees Element",
        description =
        "True if sees another with the attribute and value provided")
public class SeesElement extends SimulationSense<Boolean> {

    public SeesElement(SimulationContext ctx)
    {
        super(ctx);
    }

    public Boolean query(@Param("$attr") String attr,
            @Param("$value") Integer val)
    {
        for (LowLevelAgent v : this.ctx.getBot().getAgentsInVisionRadio())
        {
            if (val.equals(v.getProperty(attr)))
            {
                return true;
            }
        }
        return false;
    }
}
