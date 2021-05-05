import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

public class App{

    public static String[] fields;

    public static void main(String[] args) {

        /* Creating the instance of PDFManager who'll read the PDF for me. */
        PDFManager pdfManager = new PDFManager();
        pdfManager.setFilePath("./tiss.pdf");

        try {

            /* Plain text of the PDF (but only the pages 79 to 85 - in which the tables that we want are located). */
            String text = pdfManager.toText();

            String board30 = text.substring(
                text.indexOf("Tabela de Tipo do Demandante"), 
                text.indexOf("Fonte: Elaborado pelos autores.")
            );
            String[] table0 = board30.split("\n");
            //System.out.println(board30);



            /* 
            * TRATAR ESSA TABELA
            */
            String board31 = text.substring(
                text.indexOf("Tabela de Categoria do Padrão TISS"), 
                text.indexOf("Fonte: Elaborado pelos autores.", text.indexOf("Fonte: Elaborado pelos autores.") + 1)
            );
            String[] table1 = board31.split("\n");
            //System.out.println(board31);



            String board32 = text.substring(
                text.indexOf("Tabela de Tipo de Solicitação"), 
                text.length()
            );
            String[] table2 = board32.split("\n");
            //System.out.println(board32);



            ArrayList<String[]> tables = new ArrayList<>();
            tables.add(table0);
            tables.add(table1);
            tables.add(table2);


            for (int i = 0; i < tables.size(); i++){

                String[] table = tables.get(i);

                // String fileName = table[0].replaceAll(" ", "");
                
                OutputStream fos = new FileOutputStream(new File("table" + (i + 1) + ".csv"));
                // PrintWriter pw = new PrintWriter(new OutputStreamWriter(fos, "UTF-8"));
                DataOutputStream dos = new DataOutputStream(fos);


                if (i == 0 || i == 2){
                    String[] columnsName = table[1].split(" ", 2);
                    
                    String str = "";
                    for (int k = 0; k < columnsName.length; k++){
                        if (k == columnsName.length - 1)
                            str += columnsName[k];
                         else
                            str += columnsName[k] + ";";
                    }
                    // System.out.println(str);
                    dos.writeBytes(str);


                    for (int l = 2; l < table.length; l++){
                        String[] fields = table[l].split(" ", 2);
                        
                        String line = "";
                        for (int k = 0; k < fields.length; k++){
                            if (k == fields.length - 1)
                                line += fields[k];    
                            else
                                line += fields[k] + ";";    

                        }

                        // System.out.println(line);
                        dos.writeBytes(line);
                    }

                } else {
                    //tratar
                }




                dos.close();
            }
            









            System.out.println("File created. Data sended.");


        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
}