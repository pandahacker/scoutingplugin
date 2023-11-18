package com.scouting;

import com.google.inject.Provides;
import javax.inject.Inject;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.NpcSpawned;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.task.Schedule;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@PluginDescriptor(
	name = "Scouting"
)
public class ScoutingPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private ScoutingConfig config;

	@Inject
	private ScoutingWebManager webManager;

	@Getter(AccessLevel.PACKAGE)
	private List<EventData> eventsToUpload = new ArrayList<>();

	protected static String postEventsEndpoint =
			"https://g98c6e9efd32fb1-scouting.adb.us-ashburn-1.oraclecloudapps.com/ords/scouting/calls/";

	// Every X seconds, upload any events found since the last check
	private static final int UPLOAD_INTERVAL_SECONDS = 5;

	List<EventData> recentEvents = new ArrayList<>();

	@Subscribe
	public void onNpcSpawned(NpcSpawned npcSpawned) {
		final NPC npc = npcSpawned.getNpc();

		SupportedEventsEnum eventType = SupportedEventsEnum.findByNpcId(npc.getId());
		if (eventType == null) {
			// event not found for this NPC
			return;
		}

		if (!clientOptedIntoEventType(eventType))
			return;

		EventData event = makeEvent(eventType, npc.getWorldArea(), npc.getIndex());

		// remove any stale events, since events older than the dedupe duration could never match any new events anyway.
		recentEvents.removeIf(e -> Math.abs(e.getDiscovered_time().getEpochSecond() - Instant.now().getEpochSecond())
				> EventData.EVENT_DEDUPE_DURATION);
		// only attempt to upload the event if it has not already been seen
		if (!recentEvents.contains(event)) {
			eventsToUpload.add(event);
			recentEvents.add(event);
		}
	}

	private EventData makeEvent(SupportedEventsEnum eventType, WorldArea eventLocation, Integer npcIndex) {
		int world = client.getWorld();
		WorldPoint point = eventLocation.toWorldPoint();
		return EventData.builder()
				.eventType(eventType.name())
				.world(world)
				.xcoord(point.getX())
				.ycoord(point.getY())
				.plane(point.getPlane())
				.discovered_time(Instant.now())
				.npcIndex(npcIndex)
				.rsn(client.getLocalPlayer().getName())
				.build();
	}

	// Only send events if the client is interested in contributing to scouting this event type
	private boolean clientOptedIntoEventType(SupportedEventsEnum eventType) {
		if (eventType == SupportedEventsEnum.ENT) {
			return config.entEnabled();
		}
		if (eventType == SupportedEventsEnum.FOX) {
			return config.foxEnabled();
		}
		if (eventType == SupportedEventsEnum.PHEASANT) {
			return config.pheasantEnabled();
		}
		if (eventType == SupportedEventsEnum.BEEHIVE) {
			return config.beehiveEnabled();
		}
		if (eventType == SupportedEventsEnum.RITUAL) {
			return config.ritualEnabled();
		}

		return false;
	}

	@Schedule(
			period = UPLOAD_INTERVAL_SECONDS,
			unit = ChronoUnit.SECONDS,
			asynchronous = true
	)
	public void uploadEvents() {
		// List is cleared by webManager after uploading successfully
		if (eventsToUpload.size() > 0)
			webManager.postEvents();
	}

	@Provides
	ScoutingConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(ScoutingConfig.class);
	}
}
