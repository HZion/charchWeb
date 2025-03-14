package com.example.charchweb.repo;

// SermonRepository.java

import  com.example.charchweb.DTO.Sermon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface sermonRepository extends JpaRepository<Sermon, Long> {

    // 가장 최근 설교 찾기
    Optional<Sermon> findTopByOrderByDateDesc();

    // 최근 설교 목록 (ID로 제외하고 날짜 내림차순)
    List<Sermon> findByIdNotOrderByDateDesc(Long id, Pageable pageable);

    // 모든 설교 날짜 내림차순으로 가져오기
    Page<Sermon> findAllByOrderByDateDesc(Pageable pageable);

    // 검색 기능
    Page<Sermon> findByTitleContainingOrPreacherContainingOrBibleVerseContainingOrContentContaining(
            String title, String preacher, String bibleVerse, String content, Pageable pageable);
    //필터기능
    Page<Sermon> findAll(Specification<Sermon> spec, Pageable pageable);


    List<String> findDistinctPreacherBy();

    @Query("SELECT DISTINCT s.preacher FROM Sermon s")
    List<String> findDistinctPreachers();

    // 연도 범위로 필터링
    Page<Sermon> findByDateBetween(LocalDate startDate, LocalDate endDate, Pageable pageable);

    // 설교자로 필터링
    Page<Sermon> findByPreacher(String preacher, Pageable pageable);

    // 설교자와 연도 범위로 필터링
    Page<Sermon> findByPreacherAndDateBetween(
            String preacher,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable
    );

    Optional<Sermon> findFirstByIdLessThanOrderByIdDesc(Long id);

    Optional<Sermon> findFirstByIdGreaterThanOrderByIdAsc(Long id);

    List<Sermon> findByPreacherAndIdNot(String preacher, Long id, Pageable pageable);
}