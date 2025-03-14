package com.example.charchweb.repo;


import com.example.charchweb.DTO.photoAlbum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface photoAlbumRepository extends JpaRepository<photoAlbum, Long> {

    // 제목으로 검색
    Page<photoAlbum> findByTitleContainingIgnoreCase(String keyword, Pageable pageable);

    // 연도별 검색
    @Query("SELECT a FROM photoAlbum a WHERE YEAR(a.eventDate) = :year")
    Page<photoAlbum> findByEventDateYear(@Param("year") int year, Pageable pageable);

    // 카테고리별 검색
    Page<photoAlbum> findByCategory(String category, Pageable pageable);

    // 연도 및 카테고리별 검색
    @Query("SELECT a FROM photoAlbum a WHERE YEAR(a.eventDate) = :year AND a.category = :category")
    Page<photoAlbum> findByEventDateYearAndCategory(@Param("year") int year, @Param("category") String category, Pageable pageable);

    // 전체 연도 목록 조회
    @Query("SELECT DISTINCT YEAR(a.eventDate) FROM photoAlbum a ORDER BY YEAR(a.eventDate) DESC")
    List<Integer> findDistinctYears();

    // 전체 카테고리 목록 조회
    @Query("SELECT DISTINCT a.category FROM photoAlbum a WHERE a.category IS NOT NULL ORDER BY a.category")
    List<String> findDistinctCategories();



    Page<photoAlbum> findByEventDateBetween(LocalDate startDate, LocalDate endDate, Pageable pageable);

    // 같은 날짜 내에서 ID가 더 작은 앨범 중 가장 큰 ID를 가진 앨범
    Optional<photoAlbum> findFirstByEventDateEqualsAndIdLessThanOrderByIdDesc(
            LocalDateTime eventDate, Long id);

    // 같은 날짜 내에서 ID가 더 큰 앨범 중 가장 작은 ID를 가진 앨범
    Optional<photoAlbum> findFirstByEventDateEqualsAndIdGreaterThanOrderByIdAsc(
            LocalDateTime eventDate, Long id);

    // 다음 날짜의 앨범들 중 가장 가까운 날짜의 가장 작은 ID를 가진 앨범
    photoAlbum findFirstByEventDateAfterAndIdGreaterThanOrderByEventDateAscIdAsc(LocalDateTime eventDate,Long currentId);

    // 이전 날짜의 앨범들 중 가장 최근 날짜의 가장 큰 ID를 가진 앨범
    photoAlbum findFirstByEventDateBeforeAndIdLessThanOrderByEventDateDescIdDesc(LocalDateTime currentDate, Long currentId);
}