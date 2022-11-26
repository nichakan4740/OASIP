package sit.int221.oasip.Service;


import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import sit.int221.oasip.property.FileStorageProperties;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileService {
    private final Path fileStorageLocation;

    @Autowired
    public FileService(FileStorageProperties fileStorageProperties) {
        this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir()).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException(
                    "Could not create the directory where the uploaded files will be stored.", ex);
        }
    }


    public String store(MultipartFile file, Integer eventId) {
// Normalize file name
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        try {
// Check if the file's name contains invalid characters
            if (fileName.contains("..")) {
                throw new RuntimeException("Sorry! Filename contains invalid path sequence " + fileName);
            }
// Copy file to the target location (Replacing existing file with the same name)
            Path newPath = newFolder(String.valueOf(eventId));
            Path targetLocation = this.fileStorageLocation.resolve(String.valueOf(eventId) + "/" + fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return fileName;
        } catch (IOException ex) {
            throw new RuntimeException("Could not store file " + fileName + ". Please try again!", ex);
        }
    }

    public Path newFolder(String folderName) throws IOException {
        Path folder = this.fileStorageLocation.resolve(folderName);
        Resource resource = new UrlResource(folder.toUri());
        if (!resource.exists()) {
            Path path = Files.createDirectory(folder);
            System.out.println(path);
            return folder;
        }else {
            FileUtils.cleanDirectory(folder.toFile());
            System.out.println(fileStorageLocation + "\\" + folderName);
            return folder;
        }
    }

//    แสดง file

    public Resource loadFileAsResource(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new RuntimeException("File not found " + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new RuntimeException("File not found " + fileName, ex);
        }
    }




//    ลบ file
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(fileStorageLocation.toFile());
    }

    public void deleteFileById(Integer eventId) {
        String dest = eventId.toString();
        FileSystemUtils.deleteRecursively(fileStorageLocation.resolve(dest).toFile());
    }

}
