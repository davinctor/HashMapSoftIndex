
import com.hashmap.OpenAddressingHashMap;
import org.junit.Test;

import java.util.Iterator;
import java.util.Map;

import static org.junit.Assert.*;

public class HashMapTest {

    @Test
    public void basicTest() {
        Map<Long, Integer> map = new OpenAddressingHashMap<Long, Integer>();

        assertTrue(map.isEmpty());
        assertEquals(0, map.size());

        assertNotNull(map.keySet());
        assertNotNull(map.values());

        assertEquals(0, map.keySet().size());
        assertEquals(0, map.values().size());


        assertNotNull(map.put(1L, 15));
        assertEquals(1, map.size());

        assertFalse(map.containsKey(0.75f));
        assertTrue(map.containsKey(1L));

        map.put(2L, 30);
        assertEquals(2, map.size());
        // Because map.remove() wait for Object and "2" cast by default to Integer
        // that's why hash code different and Object by this key doesn't exists in map
        assertNotEquals(Integer.valueOf(30), map.remove(2));
        assertEquals(Integer.valueOf(30), map.remove(2L));

        map.put(3L, 45);
        map.put(4L, 60);
        assertEquals(Integer.valueOf(45), map.get(3L));
        // Because map.get() wait for Object and "4" cast by default to Integer
        // that's why hash code different and Object by this key doesn't exists in map
        assertNotEquals(Integer.valueOf(60), map.get(4));

        Iterator<Long> iter = map.keySet().iterator();
        int count = 0;
        while (iter.hasNext()) {
            Long key = iter.next();
            if (!map.containsKey(key))
                fail("Key(Long) getting from map("+key+") check failed by containsValue()");
            count++;
        }
        assertEquals(3, count);

        Iterator<Integer> iter2 = map.values().iterator();
        count = 0;
        while (iter2.hasNext()) {
            Integer value = iter2.next();
            if (!map.containsValue(value))
                fail("Value(Integer) getting from map(" + value +") check failed by values()");
            count++;
        }
        assertEquals(count, 3);

        Iterator<Map.Entry<Long,Integer>> iter3 = map.entrySet().iterator();
        Map.Entry<Long,Integer> entry;
        while (iter3.hasNext()) {
            entry = iter3.next();
            assertNotNull(entry.getKey());
            assertNotNull(entry.getValue());
            iter3.remove();
        }
        assertEquals(0, map.size());

        map.put(3L, 45);
        map.put(4L, 60);
        map.clear();
        assertEquals(0, map.size());

        map.put(Long.MAX_VALUE, Integer.MAX_VALUE);
        assertEquals(null, map.get(Long.MAX_VALUE - 1L));
    }

    @Test
    public void constructorTests() {

    }

    @Test
    public void iteratorConcurrentModificationTest() {

    }
}
