package de.newsarea.homecockpit.connector.iocard.domain;

import org.testng.annotations.Test;

import java.util.Map;
import java.util.TreeMap;

import static org.testng.AssertJUnit.assertEquals;

public class IOCardIdMapTest {

    @Test
    public void shouldCreateFromString() throws Exception {
        Map<Integer, Boolean> map = new TreeMap<>();
        map.put(10, false);
        map.put(11, true);
        map.put(200, true);
        IOCardIdMap ioCardIdMap = new IOCardIdMap(map);
        // ~
        assertEquals(ioCardIdMap, IOCardIdMap.fromString("!10, 11, 200"));
    }

    @Test
    public void shouldReturnToString() throws Exception {
        Map<Integer, Boolean> map = new TreeMap<>();
        map.put(10, false);
        map.put(11, true);
        map.put(200, true);
        IOCardIdMap ioCardIdMap = new IOCardIdMap(map);
        // ~
        assertEquals("!10, 11, 200", ioCardIdMap.toString());
    }
}
