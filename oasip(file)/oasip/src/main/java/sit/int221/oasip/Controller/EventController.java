package sit.int221.oasip.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sit.int221.oasip.DTO.AddEventDTO;
import sit.int221.oasip.DTO.EventDTO;
import sit.int221.oasip.Entity.Event;
import sit.int221.oasip.Service.EventService;
import sit.int221.oasip.Service.FileService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/events")
class EventController {
    @Autowired
    private EventService service;
    @Autowired
    private FileService fileService;

    //Get all Event
    @GetMapping("")
    public List<EventDTO> getAllEvent(@RequestParam(defaultValue = "eventStartTime") String sortBy, HttpServletRequest request) {
        return service.getAllEvent(sortBy, request);
    }

    //Get Event with id
    @GetMapping("/{id}")
    public EventDTO getEventById(@PathVariable Integer id, HttpServletRequest httpServletRequest) {
        return service.getEventById(id, httpServletRequest);
    }

    //Add new Event
    @PostMapping("")
    public EventDTO create(@Validated @RequestBody AddEventDTO newEvent, HttpServletRequest httpServletRequest,
                           @RequestParam("file") MultipartFile file, @PathVariable Integer eventId) {
        fileService.store(file, eventId);
        return service.save(newEvent, httpServletRequest );


    }

    //Delete an event with id = ?
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id, HttpServletRequest httpServletRequest) {
        service.deleteById(id, httpServletRequest);
    }

    //Update an event with id = ?
    @PutMapping("/{id}")
    public Event update(@RequestBody Event updateEvent, @PathVariable Integer id, HttpServletRequest httpServletRequest) {
        return service.updateId(updateEvent, id, httpServletRequest);
    }
}



//    // addfile
//
//    @GetMapping("/{filename:.+}")
//    @ResponseBody
//    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
//        Resource file = fileService.loadFileAsResource(filename);
//        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(file);
//    }
//    @PostMapping("/{Id}")
//    public String fileUpload(@RequestParam("file") MultipartFile file, @PathVariable Integer Id) {
//        fileService.store(file, Id);
//        return "You successfully uploaded " + file.getOriginalFilename() + "!";
//    }
//
//    @DeleteMapping("/{Id}")
//    public String deleteFileById(@PathVariable Integer Id) {
//        fileService.deleteFileById(Id);
//        return "You successfully deleted file with eventId " + Id + "!";
//    }
//}


