package model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;
import java.util.Objects;

@Setter
@Getter
@Table("measurements")
public class Measurement {

    @Id
    private Long id;

    @Column("project_contract")
    private String projectContract;

    @Column("start_date")
    private LocalDate startDate;

    @Column("end_date")
    private LocalDate endDate;

    @Column("year_month")
    private String yearMonth;

    public Measurement() {
    }

    public Measurement(String projectContract, LocalDate startDate, LocalDate endDate, String yearMonth) {
        this.projectContract = projectContract;
        this.startDate = startDate;
        this.endDate = endDate;
        this.yearMonth = yearMonth;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Measurement that = (Measurement) o;
        return Objects.equals(id, that.id) && Objects.equals(projectContract, that.projectContract) && Objects.equals(startDate, that.startDate) && Objects.equals(endDate, that.endDate) && Objects.equals(yearMonth, that.yearMonth);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, projectContract, startDate, endDate, yearMonth);
    }
}
