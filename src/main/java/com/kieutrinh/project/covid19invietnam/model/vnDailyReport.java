package com.kieutrinh.project.covid19invietnam.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;

@Entity(name="report")
@Table (name = "DailyReport")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class vnDailyReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(
            name = "Date"
    )
    private LocalDate date;

    @Column(name = "total_confirmed")
    private long totalConfirmed;

    @Column(name = "total_deaths")
    private long totalDeaths;

    @Column(name = "total_recovered")
    private long totalRecovered;

    public void setDate() {
        this.date = LocalDate.now();
    }
    public void setDate(LocalDate date) {
        this.date = date;
    }

}
