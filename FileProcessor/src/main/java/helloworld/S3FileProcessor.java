package helloworld;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.services.lambda.runtime.events.models.s3.S3EventNotification;
import org.apache.poi.xssf.usermodel.*;
import com.fasterxml.jackson.databind.*;
import java.io.*;
import java.util.*;

public class S3FileProcessor {
    
    private final AmazonS3 s3Client = AmazonS3ClientBuilder.defaultClient();

    public void handleRequest(S3Event s3event, Context context) {
        for (S3EventNotification.S3EventNotificationRecord record : s3event.getRecords()) {
            String bucketName = record.getS3().getBucket().getName();
            String fileName = record.getS3().getObject().getKey();
            
            System.out.println("📥 Procesando archivo: " + fileName + " desde el bucket: " + bucketName);
            
            try {
                // Descargar archivo desde S3
                S3Object s3Object = s3Client.getObject(bucketName, fileName);
                InputStream inputStream = s3Object.getObjectContent();

                // Determinar si el archivo es TXT o XLSX
                List<Map<String, String>> jsonData;
                if (fileName.endsWith(".txt")) {
                    jsonData = processTxtFile(inputStream);
                } else if (fileName.endsWith(".xlsx")) {
                    jsonData = processXlsxFile(inputStream);
                } else {
                    System.out.println("❌ Tipo de archivo no compatible.");
                    return;
                }

                // Convertir datos a JSON y mostrar en consola
                ObjectMapper objectMapper = new ObjectMapper();
                String jsonOutput = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonData);
                System.out.println("📝 JSON generado:\n" + jsonOutput);

                // Mover el archivo al bucket de salida
                String destinationBucket = "outputbucketlcy";
                s3Client.copyObject(bucketName, fileName, destinationBucket, fileName);
                s3Client.deleteObject(bucketName, fileName);
                System.out.println("✅ Archivo movido a: " + destinationBucket);

            } catch (Exception e) {
                System.err.println("❌ Error procesando archivo: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    // 📌 Método para procesar archivos TXT
    private List<Map<String, String>> processTxtFile(InputStream inputStream) throws IOException {
        List<Map<String, String>> dataList = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String[] headers = reader.readLine().split("\t"); // Leer encabezados
        
        String line;
        while ((line = reader.readLine()) != null) {
            String[] values = line.split("\t");
            Map<String, String> row = new HashMap<>();
            for (int i = 0; i < headers.length; i++) {
                row.put(headers[i], values[i]);
            }
            dataList.add(row);
        }
        return dataList;
    }

    // 📌 Método para procesar archivos XLSX
    private List<Map<String, String>> processXlsxFile(InputStream inputStream) throws IOException {
        List<Map<String, String>> dataList = new ArrayList<>();
        XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
        XSSFSheet sheet = workbook.getSheetAt(0);
        
        Iterator<org.apache.poi.ss.usermodel.Row> rowIterator = sheet.iterator();
        String[] headers = null;

        while (rowIterator.hasNext()) {
            org.apache.poi.ss.usermodel.Row row = rowIterator.next();
            Iterator<org.apache.poi.ss.usermodel.Cell> cellIterator = row.cellIterator();
            List<String> cellValues = new ArrayList<>();

            while (cellIterator.hasNext()) {
                cellValues.add(cellIterator.next().toString());
            }

            if (headers == null) {
                headers = cellValues.toArray(new String[0]);
            } else {
                Map<String, String> rowData = new HashMap<>();
                for (int i = 0; i < headers.length; i++) {
                    rowData.put(headers[i], cellValues.get(i));
                }
                dataList.add(rowData);
            }
        }
        workbook.close();
        return dataList;
    }
}
