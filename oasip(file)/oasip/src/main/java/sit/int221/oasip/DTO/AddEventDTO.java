package sit.int221.oasip.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sit.int221.oasip.Entity.Category;
import sit.int221.oasip.Entity.User;

import java.time.Instant;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddEventDTO {
    private Integer id;
    private String bookingName;
    private String eventEmail;
    private String eventNotes;
    private LocalDateTime eventStartTime;
    private Integer eventDuration;
    private Category eventCategoryID;
//    private User userID;


}
