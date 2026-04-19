package com.prerak.dto;

import com.prerak.entity.JobStatus;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

@Data
public class JobResponse {

    private Long id;
    private String outputUrl;
    
	@Enumerated(EnumType.STRING)
    private JobStatus status;
	

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getOutputUrl() {
		return outputUrl;
	}
	public void setOutputUrl(String outputUrl) {
		this.outputUrl = outputUrl;
	}

    public JobStatus getStatus() {
		return status;
	}
	public void setStatus(JobStatus status) {
		this.status = status;
	}
}