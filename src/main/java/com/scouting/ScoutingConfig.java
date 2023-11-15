package com.scouting;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("scouting")
public interface ScoutingConfig extends Config
{
	@ConfigItem(
		keyName = "ent",
		name = "Ent",
		description = "Send Friendly Ent Forestry events to server"
	)
	default boolean entEnabled()
	{
		return true;
	}
}
