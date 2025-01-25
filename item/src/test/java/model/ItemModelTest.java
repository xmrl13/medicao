package model;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class ItemModelTest {

    @Test
    void testEqualsAndHashCode() {
        Item item1 = new Item("item1", "kg");
        Item item2 = new Item("item1", "kg");

        assertThat(item1).isEqualTo(item2);
        assertThat(item1.hashCode()).isEqualTo(item2.hashCode());
    }

    @Test
    void testNotEqualsForDifferentItems() {
        Item item1 = new Item("item1", "kg");
        Item item2 = new Item("item2", "litro");

        assertThat(item1).isNotEqualTo(item2);
    }

    @Test
    void testSettersAndGetters() {
        Item item = new Item();
        item.setName("item1");
        item.setUnit("kg");

        assertThat(item.getName()).isEqualTo("item1");
        assertThat(item.getUnit()).isEqualTo("kg");
    }
}
