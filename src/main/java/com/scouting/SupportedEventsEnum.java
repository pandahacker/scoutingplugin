package com.scouting;


import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum SupportedEventsEnum {

    // TODO: remove
//    MAN_3106(3106, "Man"),
//    MAN_3107(3107, "Man"),
    ENT_12543(12543, "Entling");
    // TODO: ritual, fox, pheasant

    private int npcId;
    private String name;

    private static final Map<Integer, SupportedEventsEnum> map;
    static {
        map = new HashMap<>();
        for (SupportedEventsEnum e : SupportedEventsEnum.values())  {
            map.put(e.npcId, e);
        }
    }

    public static SupportedEventsEnum findById(int id) {
        return map.get(id);
    }

    public static boolean hasIdByNameFuzzy(String name) {
        return getIdByNameFuzzy(name) != -1;
    }

    public static int getIdByNameFuzzy(String name) {
        String lowerName = name.toLowerCase();
        for (SupportedEventsEnum event : values()) {
            if (event.name.toLowerCase().contains(lowerName))
                return event.npcId;
        }
        return -1;
    }

    public static int getIdByNameStrict(String name) {
        for (SupportedEventsEnum event : values()) {
            if (name.contains(event.name))
                return event.npcId;
        }
        return -1;
    }
}
