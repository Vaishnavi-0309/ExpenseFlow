package projects.springboot.expensetracker.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import projects.springboot.expensetracker.entity.Expense;
import projects.springboot.expensetracker.entity.Income;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense,Long> {

    List<Expense> findByProfileIdOrderByDateDesc(Long profileId);

    /* select * from expense where profile_id=?1 order by date desc limit 5 */
    List<Expense> findTop5ByProfileIdOrderByDateDesc(Long profileId);

    @Query("SELECT SUM(exp.amount) FROM Expense exp WHERE exp.profile.id=:profileId")
    BigDecimal findTotalExpenseByProfileId(@Param("profileId") Long profileId);

    /* select * from expense where profile_id =?1 and date between ?2 and ?3 and name like %4% */
    List<Expense> findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(
            Long profileId,
            LocalDate startDate,
            LocalDate endDate,
            String keyword,
            Sort sort
    );

    @Query("""
        SELECT i FROM Expense i
        WHERE i.profile.id = :profileId
        AND (cast(:startDate as localdate) IS NULL OR i.date >= :startDate)
        AND (cast(:endDate as localdate) IS NULL OR i.date <= :endDate)
        AND (:keyword IS NULL OR LOWER(i.name) LIKE LOWER(CONCAT('%', :keyword, '%')))
    """)
    List<Expense> filterExpense(
            @Param("profileId") Long profileId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("keyword") String keyword,
            Sort sort
    );

    /* select * from expense where profile=?1 and date between ?2 and ?3 */
    List<Expense> findByProfileIdAndDateBetween(Long profileId,LocalDate startDate, LocalDate endDate);

    /* select * from expense where profile=?1 and date ?2  */
    List<Expense> findByProfileIdAndDate(Long profileId,LocalDate date);
}
