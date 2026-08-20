package com.yuseix300.dmzgenerations.client;

import com.dragonminez.common.stats.character.Character;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

public final class AgeCharacterLink {

    private AgeCharacterLink() {}

    private static final Map<Character, Integer> MAP = Collections.synchronizedMap(new WeakHashMap<>());

    public static void link(Character character, int entityId) {
        if (character != null) MAP.put(character, entityId);
    }

    public static double ageFor(Character character) {
        if (CreationAgePreview.active) return CreationAgePreview.previewAge();
        Integer id = MAP.get(character);
        return id == null ? -1.0 : ClientAgeData.ageOf(id);
    }
}
