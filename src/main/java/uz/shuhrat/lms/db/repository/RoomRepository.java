package uz.shuhrat.lms.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.shuhrat.lms.db.domain.Room;

public interface RoomRepository extends JpaRepository<Room, Long> {
}
