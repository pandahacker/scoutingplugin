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

        // We are dealing with an NPC
        if (this.npcIndex != null && other.npcIndex != null)
            return this.npcIndex.equals(other.npcIndex);

        // We are dealing with a game object, not NPC, so use their position since it's fixed
        if (this.xcoord != other.xcoord)
            return false;
        if (this.ycoord != other.ycoord)
            return false;

        return true;
    }
}
