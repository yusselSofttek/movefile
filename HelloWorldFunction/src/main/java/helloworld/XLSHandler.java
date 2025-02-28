package helloworld;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.apache.poi.hssf.usermodel.HSSFWorkbook; // Para .xls
import org.apache.poi.xssf.usermodel.XSSFWorkbook; // Para .xlsx
import org.apache.poi.ss.usermodel.*;

import java.io.*;
import java.util.Map;

public class XLSHandler implements RequestHandler<Map<String, String>, String> {

    private final AmazonS3 s3Client = AmazonS3ClientBuilder.defaultClient();

    @Override
    public String handleRequest(Map<String, String> event, Context context) {
        // Validar que el evento contiene el nombre del archivo de entrada
        if (event == null || !event.containsKey("inputFileName")) {
            return "❌ Error: 'inputFileName' is required in the input JSON.";
        }

        String inputBucket = "inputbucketlcy";
        String outputBucket = "outputbucketlcy";
        String inputFileName = event.get("inputFileName");
        String outputFileName = inputFileName.replace(".xls", "-output.xls")
                                           .replace(".xlsx", "-output.xlsx");

        try {
            // Descargar archivo desde S3
            System.out.println("📥 Descargando archivo desde S3: " + inputFileName);
            S3Object s3Object = s3Client.getObject(inputBucket, inputFileName);
            InputStream inputStream = s3Object.getObjectContent();

            // Determinar el tipo de archivo (.xls o .xlsx)
            Workbook workbook;
            if (inputFileName.endsWith(".xlsx")) {
                workbook = new XSSFWorkbook(inputStream); // Para .xlsx
            } else if (inputFileName.endsWith(".xls")) {
                workbook = new HSSFWorkbook(inputStream); // Para .xls
            } else {
                return "❌ Error: El archivo debe ser .xls o .xlsx.";
            }

            // Leer el contenido del archivo Excel
            Sheet sheet = workbook.getSheetAt(0);
            StringBuilder fileContent = new StringBuilder();

            for (Row row : sheet) {
                for (Cell cell : row) {
                    fileContent.append(getCellValue(cell)).append("\t");
                }
                fileContent.append("\n");
            }

            System.out.println("📝 Contenido del archivo:\n" + fileContent);

            // Guardar el nuevo archivo en S3
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            InputStream outputInputStream = new ByteArrayInputStream(outputStream.toByteArray());

            System.out.println("⬆️ Subiendo archivo modificado a S3: " + outputFileName);
            s3Client.putObject(new PutObjectRequest(outputBucket, outputFileName, outputInputStream, null));

            workbook.close();
            return "✅ Archivo procesado y guardado en S3: " + outputFileName;

        } catch (Exception e) {
            e.printStackTrace();
            return "❌ Error al procesar el archivo: " + e.getMessage();
        }
    }

    // Método para obtener el valor de una celda en formato String
    private String getCellValue(Cell cell) {
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf(cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            default:
                return "";
        }
    }
}