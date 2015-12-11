package com.massisframework.testdata.ai;

import java.util.Map;

import com.massisframework.massis.model.agents.HighLevelController;
import com.massisframework.massis.model.agents.LowLevelAgent;

public class HelloHighLevelController extends HighLevelController {

	private static final long serialVersionUID = 1L;

	public HelloHighLevelController(LowLevelAgent agent,
			Map<String, String> metadata, String resourcesFolder)
	{
		super(agent, metadata, resourcesFolder);
		agent.setHighLevelData(this);
	}

	@Override
	public void stop()
	{
	}

	@Override
	public void step()
	{
		System.out.println("HELLO!");
	}

}
