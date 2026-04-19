package com.prerak.service;

import com.prerak.controller.FileController;
import com.prerak.entity.Job;
import com.prerak.entity.JobStatus;
import com.prerak.repository.JobRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProcessingService {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(FileController.class);
    @Value("${file.upload-dir}")
    private String uploadDir;

    private final AsyncProcessingService asyncProcessingService;
    private final JobRepository jobRepository;
    
    ProcessingService (AsyncProcessingService asyncProcessingService, JobRepository jobRepository){
    	this.asyncProcessingService =   asyncProcessingService;
    	this.jobRepository = jobRepository;
    }

    public Job process(MultipartFile file, MultipartFile signature, String tool) throws Exception {

        validateFile(file);

        if (signature != null && !signature.isEmpty()) {
            validateFile(signature);
        }

        // 2. Ensure upload directory exists
        Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(uploadPath);

        // 3. Save files
        File mainFile = saveMultipartFile(file, uploadPath);

        File sigFile = null;
        if (signature != null && !signature.isEmpty()) {
            sigFile = saveMultipartFile(signature, uploadPath);
        }

        // 4. Create job
        Job job = new Job();
        job.setToolType(tool);
        job.setStatus(JobStatus.PENDING);
        job.setInputPath(mainFile.getAbsolutePath());

        if (sigFile != null) {
            job.setSignaturePath(sigFile.getAbsolutePath());
        }

        job = jobRepository.save(job);

        log.info("📝 Job created with ID: {}", job.getId());

        // 5. Trigger async processing
        asyncProcessingService.processJob(job.getId());

        return job;
    }

    private File saveMultipartFile(MultipartFile multipartFile, Path uploadPath) throws Exception {

        String originalName = multipartFile.getOriginalFilename();
        String safeName = (originalName != null) ? originalName : "file";

        String fileName = UUID.randomUUID() + "_" + safeName;

        File destFile = uploadPath.resolve(fileName).toFile();
        multipartFile.transferTo(destFile);

        return destFile;
    }

    // 🔥 File validation (IMPORTANT)
    private void validateFile(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new RuntimeException("File is empty");
        }

        String contentType = file.getContentType();

        if (contentType == null || !contentType.startsWith("image/")) {
            throw new RuntimeException("Only image files are allowed");
        }

        // Optional: size limit (5MB)
        long maxSize = 5 * 1024 * 1024;
        if (file.getSize() > maxSize) {
            throw new RuntimeException("File size exceeds 5MB limit");
        }
    }

    public Job getJob(Long id) {
        return jobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found"));
    }
}