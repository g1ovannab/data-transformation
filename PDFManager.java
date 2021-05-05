import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

public class PDFManager {
    private PDFTextStripper stripper;
    private PDDocument pdDocument;
    
    private String text;
    private String filePath;

    public PDFManager(){

    }

    public String toText() throws IOException{
        this.stripper = null;
        this.pdDocument = null;

        pdDocument = Loader.loadPDF(new File(filePath));
        stripper = new PDFTextStripper();
        pdDocument.getNumberOfPages();
        stripper.setStartPage(79);
        stripper.setEndPage(85);
        text = stripper.getText(pdDocument);

        return text;
    }

    public PDDocument getPDDocument(){
        return this.pdDocument;
    }

    public void setFilePath(String fp) {
        this.filePath = fp;
    }

}
