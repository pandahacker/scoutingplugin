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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@PluginDescriptor(
	name = "ScoutingPlugin"
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

	private Logger logger;

	protected static String postEventsEndpoint = "https://g98c6e9efd32fb1-scouting.adb.us-ashburn-1.oraclecloudapps.com/ords/scouting/calls";

	private static final int UPLOAD_INTERVAL_SECONDS = 5;
	// remove any stale events since NPC indexes are eventually recycled
	// 1800 sec = 0.5 hour
	private static final int MAX_EVENT_STALENESS_SECONDS = 1800;

	List<EventData> recentEvents = new ArrayList<>();

	@Override
	protected void startUp() throws Exception
	{
		logger = LoggerFactory.getLogger(ScoutingPlugin.class);
	}

	@Subscribe
	public void onNpcSpawned(NpcSpawned npcSpawned) {
		final NPC npc = npcSpawned.getNpc();

		final int npcId = SupportedEventsEnum.getIdByNameFuzzy(npc.getName());
		if (npcId == -1)
			return;

		SupportedEventsEnum eventType = SupportedEventsEnum.findById(npcId);

		if (!clientOptedIntoEventType(eventType))
			return;

		// TODO: Prevent client from sending too frequently
		// TODO: try npc.getIndex (cached index) to deduplicate requests if loading/unloading the NPC. Is this index
		// consistent if the NPC goes in and out of view?

		// TODO: delete before uploading
		System.out.println("Found event: " + eventType.getName());
		EventData event = makeEvent(eventType, npc.getWorldArea(), npc.getIndex());

		logger.debug(event.toString());

		// remove any stale events since NPC indexes are eventually recycled
		recentEvents.removeIf(e -> Math.abs(e.getDiscovered_time().getEpochSecond() - Instant.now().getEpochSecond()) > MAX_EVENT_STALENESS_SECONDS);

		// only attempt to upload the event if it has not been added this session
		if (!recentEvents.contains(event)) {
			eventsToUpload.add(event);
			recentEvents.add(event);
		}
	}

	private EventData makeEvent(SupportedEventsEnum eventType, WorldArea eventLocation, Integer npcIndex) {
		int world = client.getWorld();
		WorldPoint point = eventLocation.toWorldPoint();
		return EventData.builder()
				.eventType(eventType.getName())
				.world(world)
				.xcoord(point.getX())
				.ycoord(point.getY())
				.plane(point.getPlane())
				.discovered_time(Instant.now())
				.npcIndex(npcIndex)
				.build();
	}

	private boolean clientOptedIntoEventType(SupportedEventsEnum eventType) {
		if (eventType == SupportedEventsEnum.ENT_12543) {
			return config.entEnabled();
		}

		// TODO: set this to false before release
		return true;
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
