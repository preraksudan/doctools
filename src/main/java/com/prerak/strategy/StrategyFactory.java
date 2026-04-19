package com.prerak.strategy;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StrategyFactory {

    @Autowired
    private ImageToPdfStrategy imageToPdfStrategy;

    public ProcessingStrategy get(String tool) {
    	System.out.println("Requested tool: [" + tool + "]");
    	if ("IMAGE_TO_PDF".equalsIgnoreCase(tool.trim())) { 
            return imageToPdfStrategy;
        }
        throw new RuntimeException("Unsupported tool: " + tool);
    }
}