package projects.springboot.expensetracker.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import projects.springboot.expensetracker.entity.Income;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface IncomeRepository extends JpaRepository<Income,Long> {

    List<Income> findByProfileIdOrderByDateDesc(Long profileId);

    /* select * from income where profile_id=?1 order by date desc limit 5 */
    List<Income> findTop5ByProfileIdOrderByDateDesc(Long profileId);

    @Query("SELECT SUM(exp.amount) FROM Income exp WHERE exp.profile.id=:profileId")
    BigDecimal findTotalIncomeByProfileId(@Param("profileId") Long profileId);

    /* select * from income where profile_id =?1 and date between ?2 and ?3 and name like %4% */
    List<Income> findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(
            Long profileId,
            LocalDate startDate,
            LocalDate endDate,
            String keyword,
            Sort sort
    );

        @Query("""
        SELECT i FROM Income i
        WHERE i.profile.id = :profileId
        AND (cast(:startDate as localdate) IS NULL OR i.date >= :startDate)
        AND (cast(:endDate as localdate) IS NULL OR i.date <= :endDate)
        AND (:keyword IS NULL OR LOWER(i.name) LIKE LOWER(CONCAT('%', :keyword, '%')))
    """)
        List<Income> filterIncome(
                @Param("profileId") Long profileId,
                @Param("startDate") LocalDate startDate,
                @Param("endDate") LocalDate endDate,
                @Param("keyword") String keyword,
                Sort sort
        );
 //Case	What to do Using Sort parameter	, Don’t write ORDER BY. No Sort parameter, Write ORDER BY in query

    /* select * from income where profile=?1 and date between ?2 and ?3 */
    List<Income> findByProfileIdAndDateBetween(Long profileId,LocalDate startDate, LocalDate endDate);

}
