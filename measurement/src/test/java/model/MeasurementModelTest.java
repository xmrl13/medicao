package model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class MeasurementModelTest {

    @Test
    void testConstructorAndGetter() {
        // Arrange
        String projectContract = "Project123";
        LocalDate startDate = LocalDate.of(2025, 1, 1);
        LocalDate endDate = LocalDate.of(2025, 12, 31);
        String yearMonth = "2025-01";

        // Act
        Measurement measurement = new Measurement(projectContract, startDate, endDate, yearMonth);

        // Assert
        assertEquals(projectContract, measurement.getProjectContract());
        assertEquals(startDate, measurement.getStartDate());
        assertEquals(endDate, measurement.getEndDate());
        assertEquals(yearMonth, measurement.getYearMonth());
    }

    @Test
    void testSettersAndGetters() {
        // Arrange
        Measurement measurement = new Measurement();

        String projectContract = "Project456";
        LocalDate startDate = LocalDate.of(2026, 1, 1);
        LocalDate endDate = LocalDate.of(2026, 12, 31);
        String yearMonth = "2026-01";

        // Act
        measurement.setProjectContract(projectContract);
        measurement.setStartDate(startDate);
        measurement.setEndDate(endDate);
        measurement.setYearMonth(yearMonth);

        // Assert
        assertEquals(projectContract, measurement.getProjectContract());
        assertEquals(startDate, measurement.getStartDate());
        assertEquals(endDate, measurement.getEndDate());
        assertEquals(yearMonth, measurement.getYearMonth());
    }

    @Test
    void testDefaultConstructor() {

        Measurement measurement = new Measurement();

        assertNull(measurement.getProjectContract());
        assertNull(measurement.getStartDate());
        assertNull(measurement.getEndDate());
        assertNull(measurement.getYearMonth());
    }

    @Test
    void testEqualsAndHashCode() {

        Measurement measurement1 = new Measurement("Project789", LocalDate.of(2027, 1, 1), LocalDate.of(2027, 12, 31), "2027-01");
        Measurement measurement2 = new Measurement("Project789", LocalDate.of(2027, 1, 1), LocalDate.of(2027, 12, 31), "2027-01");

        assertEquals(measurement1, measurement2);
        assertEquals(measurement1.hashCode(), measurement2.hashCode());
    }

}
