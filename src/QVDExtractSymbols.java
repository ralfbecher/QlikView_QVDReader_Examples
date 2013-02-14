/**
 * 
 * Example implementation of QVDReader:
 * 
 * It reads a given QVD file and stores the symbol data into CSV file.
 *
 * @author Ralf Becher, (c) 2012 TIQ Solutions GmbH, Leipzig/Germany,
 * contact: ralf.becher@tiq-solutions.de
 * 
 * 
 */
import au.com.bytecode.opencsv.CSVParser;
import au.com.bytecode.opencsv.CSVWriter;
import de.tiq.solutions.data.conversion.QVDReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class QVDExtractSymbols {
    
    public static void main(String[] args) {
        
        String[] header = { "Field", "SymNo", "Symbol" };
        String[] record = { "", "", "" };
        QVDReader qvdReader = null;
        CSVWriter csvWriter = null;
        FileOutputStream fos = null;
        String paramQVDfile = args[0];
        String paramCSVfile = args[1];
        char cDelimiter = args[2].substring(0, 1).toCharArray()[0];
        
        try {
            qvdReader = new QVDReader(paramQVDfile); 
        }
        
        catch (FileNotFoundException ex) {
            ex.printStackTrace();
            System.exit(1);
        }
        System.out.println("Input : "+paramQVDfile);
        System.out.println("Output: "+paramCSVfile);
        
        
        try {
            // new file and write BOM bytes first
            fos = new FileOutputStream(paramCSVfile);
            byte[] bom = new byte[] { (byte)0xEF, (byte)0xBB, (byte)0xBF };
            fos.write(bom);
            
            csvWriter = new CSVWriter(new OutputStreamWriter(fos), cDelimiter, CSVParser.NULL_CHARACTER, CSVParser.NULL_CHARACTER);
            csvWriter.writeNext(header);            
        }
        catch (IOException ex) {
            ex.printStackTrace();
            System.exit(1);
        }
        
        long recno = 0;
        // loop every field
        for (int col=0; col<qvdReader.getNoOfFields(); col++) {
            record[0] = qvdReader.getFieldName(col);
            // loop every symbol
            for (int sym=0; sym<qvdReader.getFieldNoOfSymbols(col); sym++) {
                record[1] = Integer.toString(sym);
                record[2] = qvdReader.getFieldSymbol(col, sym);
                csvWriter.writeNext(doQouting(record, cDelimiter));
                recno++;
                if (recno%2500==0) {
                    System.out.println("QVD symbol extraction: "+recno+" symbol records processed.");
                }
            }
        }

        System.out.println("QVD symbol extraction finished. "+recno+" symbol records written.");
                
        try {
            qvdReader.close();
            csvWriter.close();
        } catch (IOException ex) {
            
        }
    }

    private static String[] doQouting(String[] values, char delim) {
        for (int i=0; i<values.length; i++) {
            if (values[i].indexOf(delim)>=0) {
                values[i] = "\""+ values[i] +"\"";
            }
        }
        return values;
    }
   
}
