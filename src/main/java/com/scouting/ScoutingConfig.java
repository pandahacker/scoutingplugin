package com.scouting;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("event-scouting")
public interface ScoutingConfig extends Config
{

	@ConfigItem(
			keyName = "callsEndpoint",
			name = "Calls URL",
			description = "Which URL to send events to",
			position = 0
	)
	default String postEventsEndpoint()
	{
		return "https://g98c6e9efd32fb1-scouting.adb.us-ashburn-1.oraclecloudapps.com/ords/scouting/calls/";
	}

	@ConfigItem(
			keyName = "ent",
			name = "Friendly Ent",
			description = "Send Friendly Ent Forestry events to server",
			position = 1
	)
	default boolean entEnabled()
	{
		return true;
	}

	@ConfigItem(
			keyName = "fox",
			name = "Poachers",
			description = "Send Poachers (Fox) Forestry events to server",
			position = 2
	)
	default boolean foxEnabled()
	{
		return true;
	}

	@ConfigItem(
			keyName = "pheasant",
			name = "Pheasant Control",
			description = "Send Pheasant Control Forestry events to server",
			position = 3
	)
	default boolean pheasantEnabled()
	{
		return true;
	}

	@ConfigItem(
			keyName = "notthebees",
			name = "Beehive",
			description = "Send Beehive Forestry events to server",
			position = 4
	)
	default boolean beehiveEnabled()
	{
		return true;
	}

	@ConfigItem(
			keyName = "ritual",
			name = "Enchantment Ritual",
			description = "Send Enchantment Ritual Forestry events to server",
			position = 5
	)
	default boolean ritualEnabled()
	{
		return true;
	}

}
