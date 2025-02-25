package helloworld;

import java.util.Map;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import java.io.*;
import java.nio.file.*;
import java.util.stream.Collectors;

public class CSVHandler implements RequestHandler<Map<String, String>, String> {

    @Override
    public String handleRequest(Map<String, String> event, Context context) {
        // Check if the event contains the required keys
        if (event == null || !event.containsKey("inputFilePath") || !event.containsKey("outputFilePath")) {
            return "❌ Error: inputFilePath and outputFilePath are required in the input JSON.";
        }

        String inputFilePath = event.get("inputFilePath");
        String outputFilePath = event.get("outputFilePath");

        // Validate that inputFilePath is not null
        if (inputFilePath == null || inputFilePath.trim().isEmpty()) {
            return "❌ Error: inputFilePath is null or empty.";
        }

        try {
            // Read the content of the CSV file
            System.out.println("📥 Reading file: " + inputFilePath);
            String content = Files.lines(Paths.get(inputFilePath)).collect(Collectors.joining("\n"));

            // Print to console
            System.out.println("📝 File content:\n" + content);

            // Move the file
            Files.move(Paths.get(inputFilePath), Paths.get(outputFilePath), StandardCopyOption.REPLACE_EXISTING);
            System.out.println("✅ File moved to: " + outputFilePath);

            return "File successfully processed: " + outputFilePath;
        } catch (IOException e) {
            e.printStackTrace();
            return "❌ Error processing the file: " + e.getMessage();
        }
    }
}
