import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.lang3.StringUtils;


public class App{

    public static String[] fields;

    public static void main(String[] args) {

        /* Creating the instance of PDFManager who'll read the PDF for me. */
        PDFManager pdfManager = new PDFManager();
        pdfManager.setFilePath("pdf/tiss.pdf");

        try {

            /* Plain text of the PDF (but only the pages 79 to 85 - in which the tables that we want are located). */
            String text = pdfManager.toText();

            /* Function that gets all the tables ready to be set. */
            ArrayList<ArrayList<String>> tables = getTables(text); 


            File zip = new File("Teste_Intuitive_Care_Giovanna_Bueno.zip");
            ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zip));
            
            for (int i = 0; i < tables.size(); i++){

                ArrayList<String> table = tables.get(i);

                /* We'll name the file as the name of the actual table, but we'll
                remove the 'Tabela de' string bc it's irrelevant. */
                String path = table.get(0).replace("Tabela de", "").replaceAll("\\s+","");


                File file = new File("files/" + path + ".csv");


                if (!file.exists()){
                    OutputStream fos = new FileOutputStream(file);
                    DataOutputStream dos = new DataOutputStream(fos);
    
                    /* Here, we collect the name of the columns for our table. */
                    String[] columnsName = table.get(1).split(" ", 2);
                        
    
                    /* Writes the columns names on the table. */
                    setColumnsNames(columnsName, dos);
                    /* Writes the content of the fields on the table. */
                    setFields(table, dos);
    
                    dos.close();

                    System.out.println("\nFile " + path + " created with success.");
                } else 
                    System.out.println("\nFile " + path + " already exists.");

                zipFiles(zip, file, zos);

            }

            zos.close();


        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public static void zipFiles(File zip, File file, ZipOutputStream zos) throws IOException {
    
        try {
            String name = file.getName();
            
            ZipEntry entry = new ZipEntry(name);
            zos.putNextEntry(entry);
      
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(file);
                byte[] byteBuffer = new byte[1024];
                int bytesRead = -1;
                while ((bytesRead = fis.read(byteBuffer)) != -1) {
                    zos.write(byteBuffer, 0, bytesRead);
                }
              zos.flush();
            } finally {
                try {
                    fis.close();
                } catch (Exception e) {
                
                }
            }
            zos.closeEntry();
      
            zos.flush();

            System.out.println("\nFile " + name + " zipped with success.");
        } finally {
            
        }
    }

    public static void setFields(ArrayList<String> table, DataOutputStream dos) throws IOException {
        
        for (int l = 2; l < table.size(); l++){
            /* From the index 1 of the table's lines, we start to write our content. */
            String[] fields = table.get(l).split(" ", 2);

            
            String field = "";
            for (int k = 0; k < fields.length; k++){
                if (k == fields.length - 1)
                    field += fields[k] + "\n";    
                else
                    field += fields[k] + ";";    
            }
            dos.writeBytes(field);
            dos.flush();
        }
    }

    public static void setColumnsNames(String[] name, DataOutputStream dos) throws IOException {
        String column = "";

        for (int k = 0; k < name.length; k++){
            /* If the part is the last one, we'll add a line break, and if 
            it don't, we'll add the ';' character which represents a tab on the table.  */
            if (k == name.length - 1)
                column += name[k] + "\n";
            else
                column += name[k] + ";";
        }
        dos.writeBytes(column);
    }

    public static ArrayList<ArrayList<String>> getTables(String text) {
        
        /* Here, we get a limited part of the string based on the start 
        and the end of the table so we can get only the info we want. */

        String board30 = text.substring(
            text.indexOf("Tabela de Tipo do Demandante"), 
            text.indexOf("Fonte: Elaborado pelos autores.")
        );
        ArrayList<String> table30 = new ArrayList<String>(Arrays.asList(board30.split("\n")));
        

        String board31 = text.substring(
            text.indexOf("Tabela de Categoria do Padr??o TISS"), 
            text.indexOf("Fonte: Elaborado pelos autores.", text.indexOf("Fonte: Elaborado pelos autores.") + 1)
        ).replaceAll("\r", "");;
        ArrayList<String> table31_toNormalize = new ArrayList<String>(Arrays.asList(board31.split("\n")));


        String board32 = text.substring(
            text.indexOf("Tabela de Tipo de Solicita????o"), 
            text.length()
        );
        ArrayList<String> table32 = new ArrayList<String>(Arrays.asList(board32.split("\n")));


        /* The table 31 needs to be normalized once it has some strange 
        characters we don't want it. */
        ArrayList<String> table31 = getTableNormalized(table31_toNormalize);


        ArrayList<ArrayList<String>> tables = new ArrayList<>();
        tables.add(table30);
        tables.add(table31);
        tables.add(table32);

        return tables;
    }

    public static ArrayList<String> getTableNormalized(ArrayList<String> t) {
        
        ArrayList<String> table = new ArrayList<>();

        for (int i = 0; i < t.size(); i++){

            String str = t.get(i);

            /* This is due to the inconsistency of the data on the pdf text extracted.
            The data in the column "C??digo" with values 102, 119, 130, 143, 144, 146 and 147
            wereseparated by a line break. */
            if (str.contains("102") || str.contains("119") || str.contains("130") || 
            str.contains("143") || str.contains("144") || str.contains("146") || str.contains("147")){
                str = str.concat(t.get(i+1)).concat(t.get(i+2));
                table.add(str);
                t.remove(t.get(i));
                t.remove(t.get(i+1));
            } 

            /* If the string has any of the following characters (whitespaces, the given 
            phase, or even the numbers) we won't add to the list. 
            This is due to the inconsistency of the data on the pdf text extracted.
            The text included the page numbers of the pdf, along wih the title of the pdf. */
            else if (!StringUtils.isBlank(str) && !str.equals(" ") && !str.contains("Padr??o TISS - ") && 
                !str.contains("80") && !str.contains("81") && !str.contains("82") && 
                !str.contains("83") && !str.contains("84"))
                table.add(str);
        }

        return table;
    }

}