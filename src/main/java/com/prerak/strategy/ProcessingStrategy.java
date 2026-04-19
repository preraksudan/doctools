package com.prerak.strategy;

import java.io.File;

import java.io.File;

public interface ProcessingStrategy {
    String process(File mainFile) throws Exception;

	String process(File mainFile, File optionalFile) throws Exception;
}