package com.prerak.service;

import java.io.File;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.prerak.controller.FileController;
import com.prerak.entity.Job;
import com.prerak.entity.JobStatus;
import com.prerak.repository.JobRepository;
import com.prerak.strategy.ProcessingStrategy;
import com.prerak.strategy.StrategyFactory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AsyncProcessingService {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(FileController.class);
    
    private final JobRepository jobRepository;
    private final StrategyFactory factory;

    AsyncProcessingService(JobRepository jobRepository, StrategyFactory factory ){
    	this.jobRepository=jobRepository;
    	this.factory = factory;
    }
    @Value("${app.base-url}")
    private String baseUrl;

    @Async
    public void processJob(Long jobId) {

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> {
                    log.error("❌ Job {} not found in DB", jobId);
                    return new RuntimeException("Job not found");
                });

        try {
            log.info("🚀 Starting job {}", jobId);

            job.setStatus(JobStatus.PROCESSING);
            jobRepository.save(job);

            ProcessingStrategy strategy = factory.get(job.getToolType());

            File mainFile = new File(job.getInputPath());

            File optionalFile = job.getSignaturePath() != null
                    ? new File(job.getSignaturePath())
                    : null;

            String outputPath = strategy.process(mainFile);

            String fileName = Paths.get(outputPath).getFileName().toString();
            String fileUrl = baseUrl + "/files/" + fileName;

            job.setOutputPath(fileUrl);
            job.setStatus(JobStatus.COMPLETED);

            log.info("✅ Job {} completed: {}", jobId, fileUrl);

        } catch (Exception e) {
            log.error("❌ Job {} failed", jobId, e);
            job.setStatus(JobStatus.FAILED);
            job.setErrorMessage(e.getMessage());
        } finally {
            jobRepository.save(job);
        }
    }
}