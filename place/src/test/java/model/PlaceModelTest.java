package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class PlaceModelTest {

    @Test
    void testPlaceGettersAndSetters() {
        Place place = new Place();
        place.setName("Test Place");
        place.setProjectContract("Test Contract");

        assertEquals("Test Place", place.getName());
        assertEquals("Test Contract", place.getProjectContract());
    }

    @Test
    void testPlaceConstructor() {
        Place place = new Place("Test Place", "Test Contract");

        assertNotNull(place);
        assertEquals("Test Place", place.getName());
        assertEquals("Test Contract", place.getProjectContract());
    }
}
