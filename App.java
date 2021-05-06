import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;


public class App{

    public static String[] fields;

    public static void main(String[] args) {

        /* Creating the instance of PDFManager who'll read the PDF for me. */
        PDFManager pdfManager = new PDFManager();
        pdfManager.setFilePath("./tiss.pdf");

        try {

            /* Plain text of the PDF (but only the pages 79 to 85 - in which the tables that we want are located). */
            String text = pdfManager.toText();

            /* Function that gets all the tables ready to be set. */
            ArrayList<ArrayList<String>> tables = getTables(text); 


            for (int i = 0; i < tables.size(); i++){

                ArrayList<String> table = tables.get(i);

                /* We'll name the file as the name of the actual table, but we'll
                remove the 'Tabela de' string bc it's irrelevant. */
                String path = table.get(0).replace("Tabela de", "").replaceAll("\\s+","");


                File file = new File(path + ".csv");

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
            }
            
            //todo arrumar a segunda tabela            
            //todo zipar os arquivos

        } catch (IOException ex) {
            System.out.println(ex.getMessage());
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
            text.indexOf("Tabela de Categoria do Padrão TISS"), 
            text.indexOf("Fonte: Elaborado pelos autores.", text.indexOf("Fonte: Elaborado pelos autores.") + 1)
        ).replaceAll("\r", "");;
        ArrayList<String> table31_toNormalize = new ArrayList<String>(Arrays.asList(board31.split("\n")));


        String board32 = text.substring(
            text.indexOf("Tabela de Tipo de Solicitação"), 
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

            /* If the string has any of the following characters (whitespaces, 
            the given phase, or even the numbers) we won't add to the list. */
            if (!StringUtils.isBlank(str) && !str.equals(" ") && !str.contains("Padrão TISS - ") && 
                !str.contains("80") && !str.contains("81") && !str.contains("82") && 
                !str.contains("83") && !str.contains("84"))
                table.add(str);
        }

        return table;
    }

}