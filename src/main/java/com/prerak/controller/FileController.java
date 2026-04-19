package com.prerak.controller;

import com.prerak.config.FileStorageConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@RestController
@RequestMapping("/files")
public class FileController {

    private final Path rootLocation;
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(FileController.class);

    public FileController(FileStorageConfig config) {
        this.rootLocation = Paths.get(config.outputDir).toAbsolutePath().normalize();
    }

    /*
     * Check if this can be refactored again. to make it modular to handle diff. file types and move business logic. 
    */
    @GetMapping("/{filename:.+}")
    public ResponseEntity<Resource> getFile(@PathVariable String filename) {
        try {
            Path filePath = this.rootLocation.resolve(filename).normalize();

            if (!filePath.startsWith(this.rootLocation)) {
                log.warn("Unauthorized access attempt: {}", filename);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {// handling for a generic output file ** needed.
                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_PDF) 
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                        .body(resource);
            }
            
            return ResponseEntity.notFound().build();
            
        } catch (Exception e) {
            log.error("Error serving file {}: ", filename, e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
