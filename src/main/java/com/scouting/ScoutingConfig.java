package com.scouting;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup("event-scouting")
public interface ScoutingConfig extends Config
{

	@ConfigSection(
			name = "Info",
			description = "",
			position = 0
	)
	String infoSection = "Info section";

	@ConfigItem(
			keyName = "Calls discord link",
			name = "Calls Discord link [Hover]",
			description = "Join Log Hunters to view calls on Discord and enable notifications for certain events." +
					" Your calls may take some time to start showing up.",
			position = 0,
			section = infoSection
	)
	default String discordLink() {
		return "https://discord.gg/loghunters";
	}

	@ConfigSection(
			name = "Events",
			description = "Which events to scout for",
			position = 1
	)
	String eventsSection = "Events section";

	@ConfigItem(
			keyName = "ent",
			name = "Friendly Ent",
			description = "Send Friendly Ent Forestry events to server",
			position = 1,
			section = eventsSection
	)
	default boolean entEnabled()
	{
		return true;
	}

	@ConfigItem(
			keyName = "fox",
			name = "Poachers",
			description = "Send Poachers (Fox) Forestry events to server",
			position = 2,
			section = eventsSection
	)
	default boolean foxEnabled()
	{
		return true;
	}

	@ConfigItem(
			keyName = "pheasant",
			name = "Pheasant Control",
			description = "Send Pheasant Control Forestry events to server",
			position = 3,
			section = eventsSection
	)
	default boolean pheasantEnabled()
	{
		return true;
	}

	@ConfigItem(
			keyName = "notthebees",
			name = "Beehive",
			description = "Send Beehive Forestry events to server",
			position = 4,
			section = eventsSection
	)
	default boolean beehiveEnabled()
	{
		return true;
	}

	@ConfigItem(
			keyName = "ritual",
			name = "Enchantment Ritual",
			description = "Send Enchantment Ritual Forestry events to server",
			position = 5,
			section = eventsSection
	)
	default boolean ritualEnabled()
	{
		return true;
	}

	@ConfigSection(
			name = "Debug",
			description = "Extra settings for advanced users",
			position = 2,
			closedByDefault = true
	)
	String debugSection = "Debug section";

	@ConfigItem(
			keyName = "callsEndpoint",
			name = "Calls URL",
			description = "Which URL to send events to",
			position = 0,
			section = debugSection
	)
	default String postEventsEndpoint()
	{
		return "https://g98c6e9efd32fb1-scouting.adb.us-ashburn-1.oraclecloudapps.com/ords/scouting/calls/";
	}

}
