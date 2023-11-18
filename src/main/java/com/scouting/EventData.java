package com.scouting;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Getter;
import lombok.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Objects;

@Value
@Builder
public class EventData implements Comparable {

    private static Logger logger = LoggerFactory.getLogger(EventData.class);

    // Note: These thresholds are a bit conservative since we can always further dedupe events down the line.
    // Any events within X tiles of one another may be considered the same event
    public static int EVENT_DEDUPE_DISTANCE = 20;
    // Any events within X seconds of one another may be considered the same event
    public static int EVENT_DEDUPE_DURATION = 180;

    @Getter
    @SerializedName("event_type")
    private String eventType;

    @Getter
    @SerializedName("world")
    private int world;

    @Getter
    @SerializedName("x_coord")
    private int xcoord;

    @Getter
    @SerializedName("y_coord")
    private int ycoord;

    @Getter
    @SerializedName("plane")
    private int plane;

    @Getter
    @SerializedName("discovered_time")
    private Instant discovered_time;

    @Getter
    @SerializedName("npc_index")
    private Integer npcIndex;

    @Getter
    @SerializedName("custom")
    // Note: must be strictly equal for events to be considered equal
    private String custom;

    @Getter
    @SerializedName("rsn")
    // not considered part of the event in terms of event equality,
    // but sent along with the event for crediting, leaderboards, etc.
    private String rsn;

    @Override
    public String toString() {
        return eventType + "  " + world + "  " + xcoord + "  " + ycoord + "  " + plane + "  " + discovered_time.toString() + "  "
                + npcIndex + "  " + custom;
    }

    @Override
    // uses discovered_time for sequential ordering
    public int compareTo(Object o) {
        if (!(o instanceof EventData))
            return -1;

        EventData other = (EventData) o;
        return this.discovered_time.compareTo(other.getDiscovered_time());
    }

    // equal if the objects likely refer to the same event, regardless of timestamp or exact coordinates (for NPCs)
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof EventData))
            return false;

        EventData other = (EventData) o;
        if (!Objects.equals(this.eventType, other.eventType))
            return false;
        if (this.world != other.world)
            return false;
        if (this.plane != other.plane)
            return false;
        if (!Objects.equals(this.custom, other.custom))
            return false;

        // At this point, we know that two events of the same type are in the same world/plane. For now, assume that any
        // events within X tiles of one another are the same event. Even if this isn't completely accurate,
        // people will go to one of the locations and see both events, so it's fine.
        if (Math.abs(this.xcoord - other.xcoord) > EVENT_DEDUPE_DISTANCE) {
            return false;
        }
        if (Math.abs(this.ycoord - other.ycoord) > EVENT_DEDUPE_DISTANCE) {
            return false;
        }

        if (Math.abs(this.discovered_time.getEpochSecond() - other.discovered_time.getEpochSecond()) > EVENT_DEDUPE_DURATION) {
            return false;
        }

        return true;
    }
}
