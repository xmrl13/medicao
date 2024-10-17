package measurement.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.YearMonth;

@Getter
@Entity
@Table(name = "measurement")
public class Measurement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Setter
    @Column(name = "project_contract", nullable = false)
    @NotBlank
    private String projectContract;


    @Setter
    @Column(name = "start_date", nullable = false)
    private LocalDate start;

    @Setter
    @Column(name = "end_date", nullable = false)
    private LocalDate end;

    @Setter
    @Column(name = "year_moth")
    private YearMonth yearMonth;

    public Measurement() {
    }

    public Measurement(String projectContract, LocalDate start, LocalDate end, YearMonth yearMonth) {
        this.projectContract = projectContract;
        this.start = start;
        this.end = end;
        this.yearMonth = yearMonth;
    }
}
