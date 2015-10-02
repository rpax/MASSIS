/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpax.massis.ai.sposh.senses;


import rpax.massis.ai.sposh.SimulationContext;
import cz.cuni.amis.pogamut.sposh.executor.Param;
import cz.cuni.amis.pogamut.sposh.executor.PrimitiveInfo;

/**
 * Returns if the specified mental state contains the provided value
 *
 * @author rpax
 */
@PrimitiveInfo(name = "Mental State Contains", description = "Returns if the specified mental state contains the provided value")
public class MentalStateContains extends SimulationSense<Boolean> {

    public MentalStateContains(SimulationContext ctx)
    {
        super(ctx);
    }

    public Boolean query(
            @Param("$mentalVariable") String mentalVariable,
            @Param("$value") Integer value)
    {
        Object valueRetrieved = this.ctx.getMentalState().get(mentalVariable);
        return value.equals(valueRetrieved);

    }
}
