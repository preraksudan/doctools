package com.prerak.strategy;

import com.prerak.entity.Job;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
public class ImageToPdfStrategy implements ProcessingStrategy {

    @Value("${file.output-dir}")
    private String outputDir;

    // Main process method (No Signature)
    @Override
    public String process(File inputFile,File optionalFile) throws Exception {
        return processWithSignature(inputFile, optionalFile);
    }

    // Overloaded method to handle the Signature logic
    public String processWithSignature(File inputFile, File signatureFile) throws Exception {
    	
    	// handle input file and signature file using Job entity
        Files.createDirectories(Paths.get(outputDir));
        String outputPath = outputDir + System.currentTimeMillis() + ".pdf";

        try (PDDocument document = new PDDocument()) {
            // 1. Create page based on main image dimensions
            PDImageXObject mainImage = PDImageXObject.createFromFile(inputFile.getAbsolutePath(), document);
            PDRectangle pageSize = new PDRectangle(mainImage.getWidth(), mainImage.getHeight());
            PDPage page = new PDPage(pageSize);
            document.addPage(page);

            // 2. Draw the main image
            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.drawImage(mainImage, 0, 0);
                
                // 3. Process and add signature if provided
                if (signatureFile != null && signatureFile.exists()) {
                    processSignature(document, contentStream, page, signatureFile);
                }
            }

            document.save(outputPath);
            return outputPath;
        }
    }

    /**
     * Helper method to overlay a signature image onto the current PDF page
     */
    private void processSignature(PDDocument doc, PDPageContentStream contentStream, PDPage page, File signatureFile) throws Exception {
        PDImageXObject signature = PDImageXObject.createFromFile(signatureFile.getAbsolutePath(), doc);

        // Define Signature Size (Example: Fixed width of 150 units, maintain aspect ratio)
        float desiredWidth = 150f;
        float scale = desiredWidth / signature.getWidth();
        float sigWidth = desiredWidth;
        float sigHeight = signature.getHeight() * scale;

        // Define Position (Bottom Right with 30px padding)
        float x = page.getMediaBox().getWidth() - sigWidth - 30;
        float y = 30;

        // Draw the signature on top of the existing content
        contentStream.drawImage(signature, x, y, sigWidth, sigHeight);
    }

	@Override
	public String process(File mainFile) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}
