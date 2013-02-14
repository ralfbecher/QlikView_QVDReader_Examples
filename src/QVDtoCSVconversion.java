import au.com.bytecode.opencsv.CSVParser;
import au.com.bytecode.opencsv.CSVWriter;
import de.tiq.solutions.data.conversion.QVDReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 *
 * Example implementation of QVDReader:
 * 
 * Program converts a given QVD file to a CSV file.
 *
 * Arguments:
 *              args[0] = QVD files to read
 *              args[1] = CSV file to write
 *              args[2] = delimiter for CSV file (optional, default delimiter is |)
 *              args[4] = null tag (optional, e.g. "<null>")
 * 
 * @author Ralf Becher, (c) 2012 TIQ Solutions GmbH, Leipzig/Germany
 * Contact: ralf.becher@tiq-solutions.de
 * 
 * 
 */
public class QVDtoCSVconversion {
    
    public static void main(String[] args) {
        
        QVDReader qvdReader = null;
        CSVWriter csvWriter = null;
        FileOutputStream fos = null;
        
        String paramQVDfile = "", paramCSVfile = "", paramNullTag = "";
        char paramDelimiter = '|'; 
        
        if (args.length >= 3 && !args[0].equals("")) {
            paramQVDfile = args[0];
            paramCSVfile = args[1];
            if (args[2].equals("\\t")) {
                paramDelimiter = '\t';
            } else {
                paramDelimiter = args[2].charAt(0);
            }
            if (args.length >= 4) {
                paramNullTag = args[3];
            }
        } else {
            System.out.println("Program   : QVD to CSV file conversion");
            System.out.println("Copyright : TIQ Solutions 2012");
            System.out.println("License   : Demonstration");
            System.out.println("Author    : Ralf Becher");
            System.out.println("Contact   : info@tiq-solutions.de");
            System.out.println("Limitation: 10,000 rows");
            System.out.println("Parameters:");
            System.out.println("\t\t1. QVD file name");
            System.out.println("\t\t2. CSV file name");
            System.out.println("\t\t3. Delimiter char (e.g. \"|\" for pipe or \"\\t\" for tab)");
            System.out.println("\t\t4. Optional: tag for null values (e.g. \"<null>\")");
            System.out.println("Quoting   : none");
            
            System.exit(0);
        }

        try {
            qvdReader = new QVDReader(paramQVDfile, paramNullTag); 
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
            csvWriter.writeNext(doQouting(qvdReader.getFields(),paramDelimiter));
        }
        catch (IOException ex) {
            ex.printStackTrace();
            System.exit(1);
        }

        long recno = 0;
        while (qvdReader.hasRecord()) {
            csvWriter.writeNext(doQouting(qvdReader.getRecord(),paramDelimiter));
            recno++;
            if (recno%2500==0) System.out.println("QVD to CSV conversion: "+recno+" records written.");
        }
        if (recno==qvdReader.getNoOfRecords()) {
            System.out.println("QVD to CSV conversion finished. "+recno+" records written.");
        } else {
            System.out.println("QVD to CSV conversion finished. "+recno+" of "+qvdReader.getNoOfRecords()+" records written.");
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
