package model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProjectModelTest {

    @Test
    void testConstructorAndGetters() {
        // Arrange
        String name = "SES Project";
        String contract = "Contract123";
        BigDecimal budget = BigDecimal.valueOf(100000.00);
        List<String> userEmails = List.of("user1@example.com", "user2@example.com");

        // Act
        Project project = new Project();
        project.setName(name);
        project.setContract(contract);
        project.setBudget(budget);
        project.setUserEmail(userEmails);

        // Assert
        assertNull(project.getId());
        assertEquals(name, project.getName());
        assertEquals(contract, project.getContract());
        assertEquals(budget, project.getBudget());
        assertEquals(userEmails, project.getUserEmail());
    }

    @Test
    void testDefaultConstructor() {
        // Act
        Project project = new Project();

        // Assert
        assertNull(project.getId());
        assertNull(project.getName());
        assertNull(project.getContract());
        assertNull(project.getBudget());
        assertNull(project.getUserEmail());
    }

    @Test
    void testSettersAndGetters() {
        // Arrange
        Project project = new Project();

        String name = "SES Updated Project";
        String contract = "UpdatedContract456";
        BigDecimal budget = BigDecimal.valueOf(500000.00);
        List<String> userEmails = List.of("user3@example.com", "user4@example.com");

        // Act
        project.setName(name);
        project.setContract(contract);
        project.setBudget(budget);
        project.setUserEmail(userEmails);

        // Assert
        assertNull(project.getId()); // ID não é configurável diretamente no modelo
        assertEquals(name, project.getName());
        assertEquals(contract, project.getContract());
        assertEquals(budget, project.getBudget());
        assertEquals(userEmails, project.getUserEmail());
    }

    @Test
    void testEqualsAndHashCode() {
        // Arrange
        Project project1 = new Project();
        project1.setName("SES Equality Test");
        project1.setContract("Contract789");
        project1.setBudget(BigDecimal.valueOf(250000.00));
        project1.setUserEmail(List.of("user@example.com"));

        Project project2 = new Project();
        project2.setName("SES Equality Test");
        project2.setContract("Contract789");
        project2.setBudget(BigDecimal.valueOf(250000.00));
        project2.setUserEmail(List.of("user@example.com"));

        Project project3 = new Project();
        project3.setName("SES Different Project");
        project3.setContract("Contract999");

        // Act & Assert
        assertEquals(project1, project2);
        assertEquals(project1.hashCode(), project2.hashCode());
        assertNotEquals(project1, project3);
        assertNotEquals(project1.hashCode(), project3.hashCode());
    }
}
