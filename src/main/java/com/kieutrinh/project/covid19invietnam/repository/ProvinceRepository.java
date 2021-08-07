package com.kieutrinh.project.covid19invietnam.repository;

import com.kieutrinh.project.covid19invietnam.model.Province;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProvinceRepository extends PagingAndSortingRepository<Province,Long> {
    Optional<Province> findByName(String name);

    @Query(value = "SELECT id, case_today, case_total, death_total, name " +
            "FROM public.province " +
            "ORDER BY case_total DESC",
            nativeQuery = true)
    Iterable<Province> getProvincesDataOrderByCase();
}
