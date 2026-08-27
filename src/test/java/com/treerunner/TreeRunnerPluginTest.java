package com.treerunner;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class TreeRunnerPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(
				TreeRunnerPlugin.class
		);

		RuneLite.main(args);
	}
}