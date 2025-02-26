package helloworld;

import java.io.IOException;
import java.nio.file.*;
import java.util.Map;

public class CSVHandler {

    public String handleRequest(Map<String, String> event) {
        // Validar que el evento contiene outputFilePath
        if (event == null || !event.containsKey("outputFilePath")) {
            return "❌ Error: 'outputFilePath' is required in the input JSON.";
        }

        // Definir el contenido del CSV
        String input = "Course ID,Course Name,Instructor Lname,Instructor Fname,Instructor Title,StartDate,EndDate\n" +
                       "62376,Legal_Writing_S01_(Campbell),Campbell,Samuel,Dr.,01/10/2013,20/12/2013";

        // Obtener la ruta de salida del evento JSON
        String outputFilePath = event.get("outputFilePath");

        try {
            // Guardar el CSV en la carpeta temporal /tmp/ de AWS Lambda
            String tempFilePath = "/tmp/tempCSV.csv"; // Ruta válida en Lambda
            Files.write(Paths.get(tempFilePath), input.getBytes());
            System.out.println("📥 Archivo temporal creado en: " + tempFilePath);

            // Intentar mover el archivo a la ubicación final (esto fallará si la carpeta es de solo lectura)
            Path destinationPath = Paths.get(outputFilePath);
            Files.move(Paths.get(tempFilePath), destinationPath, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("✅ Archivo movido a: " + outputFilePath);

            return "Archivo procesado correctamente en: " + outputFilePath;
        } catch (IOException e) {
            e.printStackTrace();
            return "❌ Error al procesar el archivo: " + e.getMessage();
        }
    }
}
