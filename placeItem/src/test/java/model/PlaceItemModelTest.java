package model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class PlaceItemModelTest {

    @Test
    void testConstructorAndGetters() {
        // Arrange
        String placeName = "Place A";
        String projectContract = "Contract123";
        String itemName = "Item X";
        String itemUnit = "kg";
        BigDecimal predictedValue = BigDecimal.valueOf(100.50);
        BigDecimal accumulatedValue = BigDecimal.valueOf(50.25);

        // Act
        PlaceItem placeItem = new PlaceItem();
        placeItem.setPlaceName(placeName);
        placeItem.setProjectContract(projectContract);
        placeItem.setItemName(itemName);
        placeItem.setItemUnit(itemUnit);
        placeItem.setPredictedValue(predictedValue);
        placeItem.setAccumulatedValue(accumulatedValue);

        // Assert
        assertNull(placeItem.getId());
        assertEquals(placeName, placeItem.getPlaceName());
        assertEquals(projectContract, placeItem.getProjectContract());
        assertEquals(itemName, placeItem.getItemName());
        assertEquals(itemUnit, placeItem.getItemUnit());
        assertEquals(predictedValue, placeItem.getPredictedValue());
        assertEquals(accumulatedValue, placeItem.getAccumulatedValue());
    }

    @Test
    void testDefaultConstructor() {
        // Act
        PlaceItem placeItem = new PlaceItem();

        // Assert
        assertNull(placeItem.getId());
        assertNull(placeItem.getPlaceName());
        assertNull(placeItem.getProjectContract());
        assertNull(placeItem.getItemName());
        assertNull(placeItem.getItemUnit());
        assertNull(placeItem.getPredictedValue());
        assertNull(placeItem.getAccumulatedValue());
    }

    @Test
    void testSettersAndGetters() {
        // Arrange
        PlaceItem placeItem = new PlaceItem();

        Long id = 1L;
        String placeName = "Place B";
        String projectContract = "Contract456";
        String itemName = "Item Y";
        String itemUnit = "liters";
        BigDecimal predictedValue = BigDecimal.valueOf(200.75);
        BigDecimal accumulatedValue = BigDecimal.valueOf(100.25);

        // Act
        placeItem.setPlaceName(placeName);
        placeItem.setProjectContract(projectContract);
        placeItem.setItemName(itemName);
        placeItem.setItemUnit(itemUnit);
        placeItem.setPredictedValue(predictedValue);
        placeItem.setAccumulatedValue(accumulatedValue);

        // Assert
        assertNull(placeItem.getId());
        assertEquals(placeName, placeItem.getPlaceName());
        assertEquals(projectContract, placeItem.getProjectContract());
        assertEquals(itemName, placeItem.getItemName());
        assertEquals(itemUnit, placeItem.getItemUnit());
        assertEquals(predictedValue, placeItem.getPredictedValue());
        assertEquals(accumulatedValue, placeItem.getAccumulatedValue());
    }

    @Test
    void testEqualsAndHashCode() {
        // Arrange
        PlaceItem placeItem1 = new PlaceItem();
        placeItem1.setPlaceName("Place C");
        placeItem1.setProjectContract("Contract789");
        placeItem1.setItemName("Item Z");
        placeItem1.setItemUnit("boxes");
        placeItem1.setPredictedValue(BigDecimal.valueOf(300.00));
        placeItem1.setAccumulatedValue(BigDecimal.valueOf(150.00));

        PlaceItem placeItem2 = new PlaceItem();
        placeItem2.setPlaceName("Place C");
        placeItem2.setProjectContract("Contract789");
        placeItem2.setItemName("Item Z");
        placeItem2.setItemUnit("boxes");
        placeItem2.setPredictedValue(BigDecimal.valueOf(300.00));
        placeItem2.setAccumulatedValue(BigDecimal.valueOf(150.00));

        PlaceItem placeItem3 = new PlaceItem();
        placeItem3.setPlaceName("Place D");
        placeItem3.setProjectContract("Contract987");

        // Act & Assert
        assertEquals(placeItem1, placeItem2);
        assertEquals(placeItem1.hashCode(), placeItem2.hashCode());
        assertNotEquals(placeItem1, placeItem3);
        assertNotEquals(placeItem1.hashCode(), placeItem3.hashCode());
    }
}
