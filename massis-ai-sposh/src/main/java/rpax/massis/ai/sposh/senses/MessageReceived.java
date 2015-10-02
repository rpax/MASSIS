/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpax.massis.ai.sposh.senses;

import java.util.Map;

import rpax.massis.ai.sposh.SimulationContext;
import cz.cuni.amis.pogamut.sposh.executor.Param;
import cz.cuni.amis.pogamut.sposh.executor.PrimitiveInfo;
import java.util.HashMap;
import rpax.massis.ai.sposh.SPOSHLogicProperty;

/**
 * Returns if a specific message was received
 *
 * @author rpax
 */
@PrimitiveInfo(name = "Message Received", description = "Returns if a specific message was received")
public class MessageReceived extends SimulationSense<Boolean> {

    public MessageReceived(SimulationContext ctx)
    {
        super(ctx);
    }

    public Boolean query(
            @Param("$message") String message,
            @Param("$from") String from)
    {

        final HashMap<String, Object> mentalState = this.ctx.getMentalState();
        if (mentalState.containsKey(SPOSHLogicProperty.MESSAGES.toString()))
        {
            Map<String, SimulationContext> messages = (Map<String, SimulationContext>) mentalState.get(
                    SPOSHLogicProperty.MESSAGES.toString());

            SimulationContext senderCtx = messages.remove(message);
            if (senderCtx == null)
            {
                return false;
            } else
            {
                HashMap<String, Object> senderMS = senderCtx.getMentalState();
                String type = (String)
                        senderMS.get(SPOSHLogicProperty.TYPE.toString());
                if (SPOSHLogicProperty.ANY.toString().equals(type)
                        || from.equals(type))
                {
                    return true;
                }

            }
        }
        return false;
    }
}
