package com.kieutrinh.project.covid19invietnam.repository;

import com.kieutrinh.project.covid19invietnam.model.ProvinceData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface ProvinceDataRepository extends JpaRepository<ProvinceData,Integer> {
    ProvinceData findByDateAndName(Date date, String name);
    @Query(value = "SELECT id, date, name, num_of_case " +
            "FROM public.province_data " +
            "WHERE province_data.date = ?1 " +
            "AND name LIKE ?2", nativeQuery = true)
    ProvinceData selectByDateAndName(Date date, String name);
}
