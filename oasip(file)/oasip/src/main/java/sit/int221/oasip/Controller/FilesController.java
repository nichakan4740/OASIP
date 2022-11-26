package sit.int221.oasip.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sit.int221.oasip.Service.FileService;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/files")
public class FilesController {

    @Autowired
    private FileService fileService;

        @GetMapping("/{filename:.+}")
        @ResponseBody
        public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
            Resource file = fileService.loadFileAsResource(filename);
            return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(file);
        }
        @PostMapping("/{eventId}")
        public String fileUpload(@RequestParam("file") MultipartFile file, @PathVariable Integer eventId) {
            fileService.store(file, eventId);
            return "You successfully uploaded " + file.getOriginalFilename() + "!";
        }

    @DeleteMapping("/{eventId}")
    public String deleteFileById(@PathVariable Integer eventId) {
        fileService.deleteFileById(eventId);
        return "You successfully deleted file with eventId " + eventId + "!";
    }
    }
