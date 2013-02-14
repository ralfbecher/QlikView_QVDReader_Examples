/**
 * 
 * Example implementation of QVDReader:
 * 
 * Program reads a given QVD file record-wise and stores the data into CSV file.
 *
 * Arguments:
 *              args[0] = QVD files to read
 *              args[1] = CSV file to write
 *              args[2] = delimiter for CSV file
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

public class ExampleQVDReader {
    
    public static void main(String[] args) {
        
        QVDReader qvdReader = null;
        CSVWriter csvWriter = null;
        FileOutputStream fos = null;
        String paramQVDfile = args[0];
        String paramCSVfile = args[1];
        char paramDelimiter = args[2].substring(0, 1).toCharArray()[0];
        
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
            
            csvWriter = new CSVWriter(new OutputStreamWriter(fos), paramDelimiter, CSVParser.NULL_CHARACTER, CSVParser.NULL_CHARACTER);
            csvWriter.writeNext(doQouting(qvdReader.getFields(), paramDelimiter));
        }
        catch (IOException ex) {
            ex.printStackTrace();
            System.exit(1);
        }

        long recno = 0;
        while (qvdReader.hasRecord()) {
            csvWriter.writeNext(doQouting(qvdReader.getRecord(), paramDelimiter));
            recno++;
            if (recno%2500==0) System.out.println("QVD to CSV conversion: "+recno+" records processed.");
        }
        if (recno==qvdReader.getNoOfRecords()) {
            System.out.println("QVD to CSV conversion finished. "+recno+" records written.");
        } else {
            System.out.println("QVD to CSV conversion finished. "+recno+" of "+qvdReader.getNoOfRecords()+" records processed.");
        }
                
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
