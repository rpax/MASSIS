package com.massisframework.massis.sim.ecs.zayes;

/**
 * Temporary variables assigned to each thread. Engine classes may access these
 * temp variables with TempVars.get(), all retrieved TempVars instances must be
 * returned via TempVars.release(). This returns an available instance of the
 * TempVar class ensuring this particular instance is never used elsewhere in
 * the mean time.
 */
public class MassisTempVars {

	/**
	 * Allow X instances of TempVars in a single thread.
	 */
	private static final int STACK_SIZE = 5;

	/**
	 * <code>TempVarsStack</code> contains a stack of TempVars. Every time
	 * TempVars.get() is called, a new entry is added to the stack, and the
	 * index incremented. When TempVars.release() is called, the entry is
	 * checked against the current instance and then the index is decremented.
	 */
	private static class TempVarsStack {

		int index = 0;
		MassisTempVars[] tempVars = new MassisTempVars[STACK_SIZE];
	}

	/**
	 * ThreadLocal to store a TempVarsStack for each thread. This ensures each
	 * thread has a single TempVarsStack that is used only in method calls in
	 * that thread.
	 */
	private static final ThreadLocal<TempVarsStack> varsLocal = new ThreadLocal<TempVarsStack>() {

		@Override
		public TempVarsStack initialValue()
		{
			return new TempVarsStack();
		}
	};
	/**
	 * This instance of TempVars has been retrieved but not released yet.
	 */
	private boolean isUsed = false;

	private MassisTempVars()
	{
	}

	/**
	 * Acquire an instance of the TempVar class. You have to release the
	 * instance after use by calling the release() method. If more than
	 * STACK_SIZE (currently 5) instances are requested in a single thread then
	 * an ArrayIndexOutOfBoundsException will be thrown.
	 * 
	 * @return A TempVar instance
	 */
	public static MassisTempVars get()
	{
		TempVarsStack stack = varsLocal.get();

		MassisTempVars instance = stack.tempVars[stack.index];

		if (instance == null)
		{
			// Create new
			instance = new MassisTempVars();

			// Put it in there
			stack.tempVars[stack.index] = instance;
		}

		stack.index++;

		instance.isUsed = true;

		return instance;
	}

	/**
	 * Releases this instance of TempVars. Once released, the contents of the
	 * TempVars are undefined. The TempVars must be released in the opposite
	 * order that they are retrieved, e.g. Acquiring vars1, then acquiring
	 * vars2, vars2 MUST be released first otherwise an exception will be
	 * thrown.
	 */
	public void release()
	{
		if (!isUsed)
		{
			throw new IllegalStateException(
					"This instance of TempVars was already released!");
		}

		isUsed = false;

		TempVarsStack stack = varsLocal.get();

		// Return it to the stack
		stack.index--;

		// Check if it is actually there
		if (stack.tempVars[stack.index] != this)
		{
			throw new IllegalStateException(
					"An instance of TempVars has not been released in a called method!");
		}
	}

	//
	public final Class[/* size */][/* Class arrays */] classArrays = new Class[8][8];

}
