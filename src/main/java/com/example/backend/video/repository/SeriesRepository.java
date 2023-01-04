package com.example.backend.video.repository;

import com.example.backend.video.domain.Series;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeriesRepository extends JpaRepository<Series, Long> {
    //param 을 안붙이고 하면 Parameter value [\] match type error 발생 -> hibernate 오류라고 함 (spring boot 2.6.8 에서 해결)
    List<Series> findBySeriesNameContainsIgnoreCase(@Param("nameSearch") String nameSearch);
    Optional<Series> findBySeriesName(String seriesName);
}
