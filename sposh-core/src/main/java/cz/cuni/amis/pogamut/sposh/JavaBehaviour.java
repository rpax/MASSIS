package cz.cuni.amis.pogamut.sposh;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import cz.cuni.amis.pogamut.sposh.executor.ActionResult;


/**
 * Base class for definitions of senses and acts of SPOSH bot.
 * <p/>
 * User will implement actions and senses used in the plan by deriving this
 * class and by creating senses and actions for SPOSH plan in following fashion:
 * <p>
 * actions are methods annotated with @SPOSHAction annotation. Name of method is
 * arbitrary and return type should one of the standard ones (String + numbers).
 * SPOSH engine will use toString() method for the return values of user defined 
 * classes (probably), but because there are some issues with jython-java
 * interaction, it may produce some weird bugs.
 * <ul>
 *   <li>in plan file: shoot</li>
 *   <li>in behaviour: @SPOSHAction public void shoot()</li>
 * </ul>
 * Sense is a method returning value annotated with @SPOSHSense. Name and return
 * restrictions are similar to action.
 * <ul>
 *   <li>in plan file: hear</li>
 *   <li>in behaviour: @SPOSHSense public boolean hearNoise()</li>
 * </ul>
 *
 * For simplest action see {@link JavaBehaviour#doNothing()} or sense {@link JavaBehaviour#fail()}.
 *
 * The actions can utilize log, that is available through {@code log} member of class
 * (it is a {@link AgentLogger} of the SPOSH bot.
 *
 * This basic class (as well all derived ones) implements following actions
 * <ul>
 *   <li>doNothing</li>
 * </ul>
 * and senses
 * <ul>
 *   <li>fail</li>
 *   <li>succeed</li>
 * </ul>
 *
 * @author Honza
 * @author Jimmy
 */
public class JavaBehaviour<AGENT> {

    private String name = "behaviour";
    private String[] actions = null;
    private String[] senses = null;   
    protected AGENT bot = null;

 //
 // 2nd CHANGE (ubrani parametru IAgentLogger)
 //
    
    /**
     * Create new behaviour.
     * @param name Name of behaviour so it is easy to distinguish between 
     *             different behaviours classes in the log and error messages
     * @param bot Class of the bot that this behaviour is serving. 
     *            Used by sense and actions for gathering info and manipulation of the bot.
     */
    public JavaBehaviour(String name, AGENT bot) {
        this.name = name;
        this.bot = bot;
        this.getActions();
        this.getSenses();
    }
    
    /**
     * Get name of the behaviour specified in the constructor.
     * @return Name of the behaviour
     */
    public String getName() {
        return this.name;
    }

    /**
     * Returns list of actions that are declared in the behaviour class. Search
     * for the actions is performed only on first execution of method.
     * <p>
     * Use Java Reflection API to search through the methods for methods
     * with&nbsp;annotations @SPOSHActions and return list of strings
     * that contains the methods.
     * @return list with names of actions methods
     */
    public String[] getActions() {
        if (this.actions != null) {
            return this.actions;
        }

        Method methods[] = this.getClass().getMethods();
        List<String> result = new ArrayList<String>();

        for (Method method : methods) {
            if (method.isAnnotationPresent(SPOSHAction.class)) {
                result.add(method.getName());
            }
        }
        this.actions = result.toArray(new String[0]);
        return this.actions;
    }

    /**
     * Returns list of senses that are declared in the behaviour class. Search
     * for the senses is performed only on first execution of method.
     * <p>
     * Use Java Reflection API to search through the methods for methods 
     * with&nbsp;annotations @SPOSHSense and return list of strings
     * that contains the methods.
     * @return list with names of sense methods
     */
    public String[] getSenses() {
        if (this.senses != null) {
            return this.senses;
        }
        Method methods[] = this.getClass().getMethods();
        List<String> result = new ArrayList<String>();
        for (Method method : methods) {
            if (method.isAnnotationPresent(SPOSHSense.class)) {
                result.add(method.getName());
            }
        }
        this.senses = result.toArray(new String[0]);
        return this.senses;
    }

    /**
     * Standard action that has to be implemented everywhere. Sleep for 250ms.
     * @return true
     */
    @SPOSHAction
    public ActionResult doNothing() {
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
        }
        return ActionResult.FINISHED;
    }

    /**
     * Standard sense that has to be implemented everywhere.
     * @return false
     */
    @SPOSHSense
    public boolean fail() {
        return false;
    }

    /**
     * Standard sense that has to be implemented everywhere.
     * @return true
     */
    @SPOSHSense
    public boolean succeed() {
        return true;
    }
    
    /**
	 * Method that is triggered every time the plan for executor is evaluated. It is triggered right before the plan evaluation. 
	 */
	public void logicBeforePlan() {		
	}
	
	/**
	 * Method that is triggered every time the plan for executor is evaluated. It is triggered right after the plan evaluation. 
	 */
	public void logicAfterPlan() {		
	}
	
	/**
	 * Returns underlying AGENT instance.
	 * @return
	 */
	public AGENT getBot() {
		return bot;
	}
	
}
