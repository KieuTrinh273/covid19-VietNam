package com.kieutrinh.project.covid19invietnam.repository;

import com.kieutrinh.project.covid19invietnam.model.vnDailyReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface vnDailyReportRepository extends JpaRepository<vnDailyReport, Long> {

    Optional<vnDailyReport> findByDate(LocalDate date);

}