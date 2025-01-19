package model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;

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
}
