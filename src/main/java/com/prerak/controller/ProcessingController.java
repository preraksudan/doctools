package com.prerak.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.prerak.config.FileStorageConfig;
import com.prerak.entity.Job;

import com.prerak.dto.JobResponse;

import com.prerak.service.ProcessingService;

import jakarta.annotation.PostConstruct;
import lombok.Value;

import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
public class ProcessingController {

    @Autowired
    private ProcessingService service;
    
    @Autowired
    private FileStorageConfig config;
    
    @PostMapping("/")
    public String check() {
    	String outputPath = config.outputDir + System.currentTimeMillis() + ".pdf";
        System.out.println("OUTPUT path: " + outputPath);
        return ("OUTPUT path: " + outputPath);
    }

    @PostMapping("/process") /* Debugging needed for this function service and strategy portion */
    public JobResponse process(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "signature", required = false) MultipartFile signature,
            @RequestParam("tool") String tool
    ) throws Exception {

       Job job = service.process(file, signature, tool);
    	
       JobResponse res = new JobResponse();
       res.setId(job.getId());
       res.setOutputUrl(job.getOutputPath());
       res.setStatus(job.getStatus());
       return res;
    }
    
        
    @GetMapping("/job/{id}")
    public JobResponse getJob(
            @PathVariable Long id
    ) throws Exception {
       Job job = service.getJob(id);
    	
       JobResponse res = new JobResponse();
       res.setId(job.getId());
       res.setOutputUrl(job.getOutputPath());
       res.setStatus(job.getStatus());
       return res;
    }
}