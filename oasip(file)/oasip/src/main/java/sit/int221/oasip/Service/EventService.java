package sit.int221.oasip.Service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.server.ResponseStatusException;
import sit.int221.oasip.DTO.AddEventDTO;
import sit.int221.oasip.DTO.EditUserDTO;
import sit.int221.oasip.DTO.EventDTO;
import sit.int221.oasip.DTO.ResponseCreateTokenDTO;
import sit.int221.oasip.Entity.Category;
import sit.int221.oasip.Entity.Event;
import sit.int221.oasip.Entity.User;
import sit.int221.oasip.Repository.CategoryRepository;
import sit.int221.oasip.Repository.EventCategoryOwnerRepository;
import sit.int221.oasip.Repository.EventRepository;
import sit.int221.oasip.Repository.UserRepository;
import sit.int221.oasip.Utils.ListMapper;
import sit.int221.oasip.config.JwtTokenUtil;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class EventService {

    @Autowired
    private EventRepository repository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ListMapper listMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private EmailServiceImpl emailService;

    @Autowired
    private FileService fileService;

    public User getEmailFromRequest(HttpServletRequest request) {
        if (request.getHeader("Authorization") != null) {
            String token = request.getHeader("Authorization").substring(7);
            String userEmail = jwtTokenUtil.getUsernameFromToken(token);
            return  userRepository.findByEmail(userEmail);
        }
        return null;
    }


    //Get all Event
    public List<EventDTO> getAllEvent(String sortBy,HttpServletRequest request) {
        User userfromtoken = getEmailFromRequest(request);
        List<Event> events = new ArrayList<>();

//        admin สามารถดู event ได้้ทั้งหมด
        if (userfromtoken != null && (request.isUserInRole("ROLE_admin"))) {
            events = repository.findAll(Sort.by(sortBy).descending());
        }
//        student สามารถดูได้เฉพาะ email ของตัวเอง
          else if (userfromtoken != null && (request.isUserInRole("ROLE_student"))) {
            List<Event> eventsListByEmail = repository.findByOwner(userfromtoken.getEmail());
            return listMapper.mapList(eventsListByEmail, EventDTO.class,modelMapper);

//        lecturer สามารถดูได้เฉพาะ event ที่ตัวเองดูแล
        } else if (userfromtoken != null && (request.isUserInRole("ROLE_lecturer"))) {
//            List<Integer> categoriesId = eventCategoryOwnerRepository.findAllByUserId(userfromtoken.getId());
//            events = repository.findAllByEventCategory(categoriesId);
            List<Event> categories = repository.findEventCategoryOwnerByID(userfromtoken.getId());
            return listMapper.mapList(categories, EventDTO.class, modelMapper);
        }
        return listMapper.mapList(events, EventDTO.class, modelMapper);
    }

    //Get Event with id
    public EventDTO getEventById(Integer id , HttpServletRequest httpServletRequest) {
        User userfromtoken = getEmailFromRequest(httpServletRequest);

        Event event = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Event id " + id +
                        "Does Not Exist !!!"
                ));

        if (userfromtoken != null && (httpServletRequest.isUserInRole("ROLE_student")&& !userfromtoken.getEmail().equals(event.getEventEmail()))){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "booking email must be the same as the student's email");
        }
        if (userfromtoken != null && (httpServletRequest.isUserInRole("ROLE_lecturer"))) {
            List<Event> categories = repository.findEventCategoryOwnerByID(userfromtoken.getId());

            if(!categories.contains(event)){
                throw new ResponseStatusException(HttpStatus.FORBIDDEN,"You are not authorized to view events that are not yours");
            }
        }

        return modelMapper.map(event, EventDTO.class);
    }

    //Add new Event
    public EventDTO save(AddEventDTO newEvent, HttpServletRequest httpServletRequest) {
        Event event = modelMapper.map(newEvent, Event.class);
        User userfromtoken = getEmailFromRequest(httpServletRequest);

        if (userfromtoken != null && (httpServletRequest.isUserInRole("ROLE_student") && !userfromtoken.getEmail().equals(event.getEventEmail()))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "booking email must be the same as the student's email");
        }
        if (userfromtoken != null && (httpServletRequest.isUserInRole("ROLE_lecturer"))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Lecturer can't create event");
        }

        repository.saveAndFlush(event);

        Category category = categoryRepository.findById(event.getEventCategoryID().getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Event id " + event.getEventCategoryID().getId() +
                        "Does Not Exist !!!"
                ));

        LocalDateTime startTime = event.getEventStartTime();
        String pattern = "E MMM dd, yyyy HH:mm";
        String formatDate = startTime.format(DateTimeFormatter.ofPattern(pattern));

        String subject = "[OASIP] "+ category.getEventCategoryName() + " @ " + formatDate + " - "+
                findEndDate(event.getEventStartTime(), event.getEventDuration()).toString().substring(11) + " ( ICT ) ";

        String message = "Booking Name : " + event.getBookingName() +'\n'
                        + "Event Category : " + category.getEventCategoryName() + '\n'
                        + "When : " + formatDate + " - "+
                                    findEndDate(event.getEventStartTime(),event.getEventDuration()).toString().substring(11) + " ( ICT ) " + '\n'
                        + "Event Notes : " + event.getEventNotes();

        try{
            emailService.sendEmail(event.getEventEmail(),subject,message);
            System.out.println("sent email success");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("sent email failed");
        }

        return modelMapper.map(event, EventDTO.class);
    }

//    เช็ค end time
    public LocalDateTime findEndDate(LocalDateTime startDate , int duration){
        LocalDateTime endDate = startDate.plusMinutes(duration);
        return endDate;
    }

//    public Instant findDate(Instant startDate , int duration){
//        Instant endDate = startDate.plus(duration, ChronoUnit.MINUTES);
//        return endDate;
//    }

    //delete event with id = ?
    public void deleteById(Integer id, HttpServletRequest httpServletRequest) {
        Event event = repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                id + " does not exist !!!"));

        User userfromtoken = getEmailFromRequest(httpServletRequest);
        if (userfromtoken != null && (httpServletRequest.isUserInRole("ROLE_student")&& !userfromtoken.getEmail().equals(event.getEventEmail()))){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "booking email must be the same as the student's email");
        }
        repository.deleteById(id);
        fileService.deleteFileById(id);
    }


    //update event with id = ?
    public Event updateId(@RequestBody Event updateEvent, @PathVariable Integer id, HttpServletRequest httpServletRequest) {
        Event event = repository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, id + "does not exist!!!"));

        event.setEventNotes(updateEvent.getEventNotes().trim());
        event.setEventStartTime(updateEvent.getEventStartTime());

        User userfromtoken = getEmailFromRequest(httpServletRequest);

        if (userfromtoken != null && (httpServletRequest.isUserInRole("ROLE_student")&& !userfromtoken.getEmail().equals(event.getEventEmail()))){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "booking email must be the same as the student's email");
        }
            repository.saveAndFlush(event);

        return updateEvent;
    }
}


