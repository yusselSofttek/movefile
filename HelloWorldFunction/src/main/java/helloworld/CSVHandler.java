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
        // Verificar si el evento tiene las claves correctas
        if (event == null || !event.containsKey("inputFilePath") || !event.containsKey("outputFilePath")) {
            return "❌ Error: inputFilePath y outputFilePath son requeridos en el JSON de entrada.";
        }

        String inputFilePath = event.get("inputFilePath");
        String outputFilePath = event.get("outputFilePath");

        // Validar que inputFilePath no sea null
        if (inputFilePath == null || inputFilePath.trim().isEmpty()) {
            return "❌ Error: inputFilePath es nulo o vacío.";
        }

        try {
            // Leer contenido del archivo CSV
            System.out.println("📥 Leyendo el archivo: " + inputFilePath);
            String content = Files.lines(Paths.get(inputFilePath)).collect(Collectors.joining("\n"));

            // Imprimir en consola
            System.out.println("📝 Contenido del archivo:\n" + content);

            // Mover el archivo
            Files.move(Paths.get(inputFilePath), Paths.get(outputFilePath), StandardCopyOption.REPLACE_EXISTING);
            System.out.println("✅ Archivo movido a: " + outputFilePath);

            return "Archivo procesado correctamente: " + outputFilePath;
        } catch (IOException e) {
            e.printStackTrace();
            return "❌ Error al procesar el archivo: " + e.getMessage();
        }
    }
}
