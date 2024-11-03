package uz.farobiy.lms_clone.db.repository.admin;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import uz.farobiy.lms_clone.db.customDto.admin.DashboardStats;

import java.util.List;

@Repository
public class DashboardRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public List<DashboardStats> getGeneralStats() {
        List<Object[]> result = entityManager.createNativeQuery("""
            SELECT 'Courses' AS title, '  tadan 100% faol' AS suffix, COUNT(c.id) AS value FROM courses c
            UNION ALL
            SELECT 'Groups' AS title, '  tadan 100% faol' AS suffix, COUNT(g.id) AS value FROM groups g
            UNION ALL
            SELECT 'Teachers' AS title, '  tadan 96% faol' AS suffix, COUNT(t.id) AS value FROM users t WHERE role_id = 2
            UNION ALL
            SELECT 'Students' AS title, '  tadan 89% faol' AS suffix, COUNT(s.id) AS value FROM users s WHERE role_id = 3
            """).getResultList();

        return result.stream()
                .map(row -> new DashboardStats(
                        (String) row[0],  // title
                        (String) row[1],  // suffix
                        ((Number) row[2]).intValue()  // value
                ))
                .toList();
    }
}
