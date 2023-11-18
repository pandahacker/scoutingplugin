package com.scouting;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("scouting")
public interface ScoutingConfig extends Config
{
	@ConfigItem(
		keyName = "ent",
		name = "Friendly Ent",
		description = "Send Friendly Ent Forestry events to server"
	)
	default boolean entEnabled()
	{
		return true;
	}

	@ConfigItem(
			keyName = "fox",
			name = "Poachers",
			description = "Send Poachers (Fox) Forestry events to server"
	)
	default boolean foxEnabled()
	{
		return true;
	}

	@ConfigItem(
			keyName = "pheasant",
			name = "Pheasant Control",
			description = "Send Pheasant Control Forestry events to server"
	)
	default boolean pheasantEnabled()
	{
		return true;
	}

	@ConfigItem(
			keyName = "notthebees",
			name = "Beehive",
			description = "Send Beehive Forestry events to server"
	)
	default boolean beehiveEnabled()
	{
		return true;
	}

	@ConfigItem(
			keyName = "ritual",
			name = "Enchantment Ritual",
			description = "Send Enchantment Ritual Forestry events to server"
	)
	default boolean ritualEnabled()
	{
		return true;
	}

}
