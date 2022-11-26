package sit.int221.oasip.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sit.int221.oasip.Entity.Event;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Integer> {

    @Query("select e from Event e where e.eventEmail = :OwnerEmail ")
    List<Event> findByOwner(@Param("OwnerEmail") String OwnerEmail);

//    หา event by category all
//    @Query("select e from Event e where e.eventCategoryID.id in :CateID")
//    List<Event> findAllByEventCategory(@Param("CateID") List<Integer> CateID);

    @Query("select e from Event e join EventCategoryOwner eo on e.eventCategoryID.id = eo.eventCategory.id join User u on u.id = eo.user.id where u.id = :id")
    List<Event> findEventCategoryOwnerByID(@Param("id") Integer id);


}

