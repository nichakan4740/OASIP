package sit.int221.oasip.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sit.int221.oasip.Entity.EventCategoryOwner;

import java.util.List;

public interface EventCategoryOwnerRepository extends JpaRepository<EventCategoryOwner, Integer> {

//    lecturer ไหนดูแลแต่ละ category
//    @Query("select e.eventCategory.id from EventCategoryOwner e where e.user.id = :id ")
//    List<Integer> findAllByUserId(@Param("id") int id);

}