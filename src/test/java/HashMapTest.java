
import com.hashmap.OpenAddressingHashMap;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import static org.junit.Assert.*;

public class HashMapTest {

    Map<Long, Integer> mapOA;   // open addressing
    Map<Long, Integer> mapJU;   // java.util

    @Before
    public void beforeTest() {
        mapOA = new OpenAddressingHashMap<Long, Integer>();
    }

    @Test
    public void sizeTest() {
        assertTrue(mapOA.isEmpty());
        assertEquals(0, mapOA.size());

        assertNotNull(mapOA.keySet());
        assertNotNull(mapOA.values());

        assertEquals(0, mapOA.keySet().size());
        assertEquals(0, mapOA.values().size());

        mapOA.put(1L, 15);
        mapOA.put(2L, 30);
        assertEquals(2, mapOA.size());
        assertEquals(2, mapOA.keySet().size());
        assertEquals(2, mapOA.values().size());
    }

    @Test
    public void putGetTest() {
        mapOA.put(3L, 45);
        mapOA.put(4L, 60);
        assertEquals(Integer.valueOf(45), mapOA.get(3L));
        // Because map.get() wait for Object and "4" cast by default to Integer
        // that's why hash code different and Object by this key doesn't exists in map
        assertNotEquals(Integer.valueOf(60), mapOA.get(4));
    }

    @Test
    public void containsTest() {
        mapOA.put(1L, 45);
        mapOA.put(2L, 60);
        mapOA.put(3L, 75);
        assertFalse(mapOA.containsKey(0.75f));
        assertTrue(mapOA.containsKey(1L));
        assertTrue(mapOA.containsValue(45));

        Iterator<Long> iter = mapOA.keySet().iterator();
        int count = 0;
        while (iter.hasNext()) {
            Long key = iter.next();
            if (!mapOA.containsKey(key))
                fail("Key(Long) getting from map("+key+") check failed by containsValue()");
            count++;
        }
        assertEquals(3, count);

        Iterator<Integer> iter2 = mapOA.values().iterator();
        count = 0;
        while (iter2.hasNext()) {
            Integer value = iter2.next();
            if (!mapOA.containsValue(value))
                fail("Value(Integer) getting from map(" + value +") check failed by values()");
            count++;
        }
        assertEquals(3, count);

        Iterator<Map.Entry<Long,Integer>> iter3 = mapOA.entrySet().iterator();
        while (iter3.hasNext()) {
            iter3.next();
            iter3.remove();
        }
        assertEquals(0, mapOA.size());
    }

    @Test
    public void clearTest() {
        mapOA.put(3L, 45);
        mapOA.put(4L, 60);
        mapOA.clear();
        assertEquals(0, mapOA.size());
    }

    @Test
    public void removeTest() {
        mapOA.put(2L, 45);
        mapOA.put(4L, 60);

        // Because map.remove() wait for Object and "2" cast by default to Integer
        // that's why hash code different and Object by this key doesn't exists in map
        assertNull(mapOA.remove(2));
        assertEquals(Integer.valueOf(60), mapOA.remove(4L));


        for (int i = 1; i <= 100; i++)
            mapOA.put(Long.valueOf(i+10), Integer.valueOf(i));

        // Checking access to all elements after remove one of them
        Random rand = new Random();
        int randNum;
        for (int i = 1; i <= 10; i++) {
            randNum = rand.nextInt(110);
            mapOA.remove(Long.valueOf(randNum));
            for (int j = 1; j <= 100; j++)
                mapOA.get(Long.valueOf(j + 10));
        }

    }

    @Test
    public void constructorTests() {
        assertEquals(0, mapOA.size());
    }

    /**
     * TODO: Create test for throwing ConcurrentModificationException after adding support of fail-fast
     * @throw ConcurrentModificationException when iterator work with not actual entry set
     *
    @Test
    public void iteratorConcurrentModificationTest() {

    }
    **/

    private static int LOAD_COUNT = 50000;


    private void hashMapSpeedTest(Map<Long, Integer> map, boolean isPut) {
        long t0 = System.currentTimeMillis();
        for (int i = 0; i < LOAD_COUNT; i++) {
            if (isPut)
                map.put(Long.valueOf(i), Integer.valueOf(i));
            else
                map.get(Long.valueOf(i));
        }
        long t1 = System.currentTimeMillis();
        System.out.println(map.getClass() + (isPut ? ".put() " : ".get() ")
                + (t1 - t0) + "ms");
    }


    @Test
    public void hashMapSpeedTest() {
        mapJU = new HashMap<Long, Integer>();
        hashMapSpeedTest(mapOA, true);
        hashMapSpeedTest(mapJU, true);
        hashMapSpeedTest(mapOA, false);
        hashMapSpeedTest(mapJU, false);
    }

    @Test
    public void iteratorTest() {
        mapOA.put(150L, 400);
        mapOA.put(300L, 700);
        mapOA.put(600L, 1300);

        Iterator<Long> iter = mapOA.keySet().iterator();
        Long key;
        while (iter.hasNext()) {
            key = iter.next();
            assertNotNull(mapOA.get(key));
        }
    }

    @Test
    public void equalsTest() {
        mapJU = new HashMap<Long, Integer>();

        mapOA.put(150L, 400);
        mapOA.put(300L, 700);
        mapOA.put(600L, 1300);

        mapJU.put(150L, 400);
        mapJU.put(300L, 700);
        mapJU.put(600L, 1300);

        assertTrue(mapOA.equals(mapJU));

        mapJU.put(1200L, 2500);
        assertFalse(mapOA.equals(mapJU));

        try {
            mapOA.put(1200L, null);
            fail("Excepting " + UnsupportedOperationException.class
                    + " by inserting value = null");
        } catch (UnsupportedOperationException e) {
            // Ok, we waited for this
        }
        assertFalse(mapOA.equals(mapJU));

        mapJU.remove(Long.valueOf(1200));
        assertTrue(mapOA.equals(mapJU));
    }
}
