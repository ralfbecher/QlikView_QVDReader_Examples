import au.com.bytecode.opencsv.CSVReader;
import de.tiq.solutions.data.conversion.QVDWriter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;
 
/**
 * 
 * Example implementation of QVDWriter:
 * 
 * Program reads CSV file and creates QVD file with the given meta data in QVDWriter.properties file.
 *
 * @author Ralf Becher, (c) 2012 TIQ Solutions GmbH, Leipzig/Germany
 * contact: ralf.becher@tiq-solutions.de
 * 
 * 
 */
public class ExampleQVDWriter {
    
    static boolean USE_CACHE_FILES = false;
    static String INPUT_FILE = "";
    static String OUTPUT_FILE = "";
    static String TABLE_NAME = "";
    static String INTPUT_CHARSET = "ISO-8859-1";
    
    static String DEF_NUMBER_FORMAT_PATTERN = "";
    static String DEF_DATE_FORMAT = "";
    static String DEF_DATETIME_FORMAT = "";
    static char DEF_DECIMAL_SEPARATOR_CHARACTER = '.';
    static char DEF_GROUPING_SEPARATOR_CHARACTER = ',';
    static String FIELD_NAMES[] = {}; 
    static String FIELD_TYPES[] = {}; 
    static String FIELD_FORMATS[] = {}; 
    
    static char CSV_SEPARATOR = ';';
    static char CSV_QUOTE_CHARACTER = '"';
    static char CSV_ESCAPE_CHARACTER = '\\';
    static int CSV_SKIP_LINES = 0;
    static boolean CSV_STRICT_QUOTES = false;
    static boolean CSV_IGNORE_LEADING_WHITESPACE = true;
    //private static final int MASK = 0xff;

    public static void main(String[] args) throws IOException {
        
        Properties prop = new Properties();

        try {
               //load a properties file
    		prop.load(new FileInputStream("QVDWriter.properties"));
 
               //get the property values
                USE_CACHE_FILES = "true".equals(prop.getProperty("USE_CACHE_FILES"));
                INPUT_FILE = prop.getProperty("INPUT_FILE");
                OUTPUT_FILE = prop.getProperty("OUTPUT_FILE");
                TABLE_NAME = prop.getProperty("TABLE_NAME");
                INTPUT_CHARSET = prop.getProperty("INTPUT_CHARSET");
                
                DEF_NUMBER_FORMAT_PATTERN = prop.getProperty("DEF_NUMBER_FORMAT_PATTERN");
                DEF_DATE_FORMAT = prop.getProperty("DEF_DATE_FORMAT");
                DEF_DATETIME_FORMAT = prop.getProperty("DEF_DATETIME_FORMAT");
                DEF_DECIMAL_SEPARATOR_CHARACTER = prop.getProperty("DEF_DECIMAL_SEPARATOR_CHARACTER").charAt(0);
                DEF_GROUPING_SEPARATOR_CHARACTER = prop.getProperty("DEF_GROUPING_SEPARATOR_CHARACTER").charAt(0);  
                FIELD_NAMES = prop.getProperty("FIELD_NAMES").split(";");
                FIELD_TYPES = prop.getProperty("FIELD_TYPES").split(";");
                FIELD_FORMATS = prop.getProperty("FIELD_FORMATS").split(";");
                
                CSV_SEPARATOR = prop.getProperty("CSV_SEPARATOR").charAt(0);
                CSV_QUOTE_CHARACTER = prop.getProperty("CSV_QUOTE_CHARACTER").charAt(0);
                CSV_ESCAPE_CHARACTER = prop.getProperty("CSV_ESCAPE_CHARACTER").charAt(0);
                
                CSV_SKIP_LINES = Integer.parseInt(prop.getProperty("CSV_SKIP_LINES"));
                        
                CSV_STRICT_QUOTES = "true".equals(prop.getProperty("CSV_STRICT_QUOTES"));
                CSV_IGNORE_LEADING_WHITESPACE = "true".equals(prop.getProperty("CSV_IGNORE_LEADING_WHITESPACE"));
 
    	} catch (IOException ex) {
    		ex.printStackTrace();
        }

        CSVReader csvReader = new CSVReader(new InputStreamReader(new FileInputStream(INPUT_FILE), INTPUT_CHARSET), 
                CSV_SEPARATOR,
                CSV_QUOTE_CHARACTER,
                CSV_ESCAPE_CHARACTER,
                CSV_SKIP_LINES,
                CSV_STRICT_QUOTES,
                CSV_IGNORE_LEADING_WHITESPACE);
        
        // create QVXWriter instance and create QVX file
        QVDWriter qvdWriter = null;
//        try {
//            qvdWriter = new QVDWriter(OUTPUT_FILE, TABLE_NAME, true, 0, 
//                        DEF_NUMBER_FORMAT_PATTERN, DEF_DECIMAL_SEPARATOR_CHARACTER, DEF_GROUPING_SEPARATOR_CHARACTER, 
//                        DEF_DATE_FORMAT, DEF_DATETIME_FORMAT, FIELD_NAMES, FIELD_TYPES, FIELD_FORMATS);
            qvdWriter = new QVDWriter(USE_CACHE_FILES, OUTPUT_FILE, TABLE_NAME, 0, 
                        DEF_NUMBER_FORMAT_PATTERN, DEF_DECIMAL_SEPARATOR_CHARACTER, DEF_GROUPING_SEPARATOR_CHARACTER, 
                        DEF_DATE_FORMAT, DEF_DATETIME_FORMAT, FIELD_NAMES, FIELD_TYPES, FIELD_FORMATS);
//        } catch (FileNotFoundException ex) {
//            ex.printStackTrace();
//            System.exit(1);
//        }
                
        // write QVX Header
        //qvdWriter.writeHeader();
            
        System.out.println("Input : "+INPUT_FILE);
        System.out.println("Output: "+OUTPUT_FILE);

        String [] nextLine;
        if ((nextLine = csvReader.readNext()) != null) {
            // skip header            
        }
       long recno = 0;
       while ((nextLine = csvReader.readNext()) != null) {
            // write data record (append)
            if (!qvdWriter.writeRecord(nextLine.clone())){
                // limited rows reached
                break;
            }
            recno++;
            if (recno%2500==0) System.out.println("CSV to QVD conversion: "+recno+" records processed.");
        }	
        System.out.println("CSV to QVD conversion: phase 1 finished. "+recno+" records processed.");
//        System.out.println("records written:"+qvdWriter.getCurrentRecord());
//        System.out.println("max record size:"+qvdWriter.getMaxRecordSize());
//
        qvdWriter.close();
        System.out.println("CSV to QVD conversion: phase 2 finished.");
        System.out.println("CSV to QVD conversion: QVD file created.");
        csvReader.close();
    }
}
