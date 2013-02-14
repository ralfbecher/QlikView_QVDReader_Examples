/**
 * 
 * Example implementation of QVDReader:
 * 
 * Program reads a given QVD file and searches the matching symbol and stores result to CSV file.
 * 
 * Arguments:
 *              args[0] = path for QVD files (e.g. .\examples)
 *              args[1] = search pattern for QVD files with wildcards (e.g. "Sales*.qvd", use double qoutes here!)
 *              args[2] = file name for CSV output file (e.g. found.csv)
 *              args[3] = delimiter for CSV output file (e.g. "|")
 *              args[4] = search pattern for symbol occurance with wildcards, case sensitive (e.g. '*West' to find 'North West' and 'South West')
 *              args[5] = search mode (otional): -all for all occurances (default)
 *                                               -first for first occurance
 *
 * @author Ralf Becher, (c) 2012 TIQ Solutions GmbH, Leipzig/Germany,
 * contact: ralf.becher@tiq-solutions.de
 * 
 * 
 */
import au.com.bytecode.opencsv.CSVParser;
import au.com.bytecode.opencsv.CSVWriter;
import de.tiq.solutions.data.conversion.QVDReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.regex.Pattern;

public class QVDSearchSymbols {
    
    public static void main(String[] args) {
        
        String[] header = { "File", "Field", "SymNo", "Symbol" };
        String[] record = { "", "", "", "" };
        QVDReader qvdReader = null;
        CSVWriter csvWriter = null;
        FileOutputStream fos = null;
        String paramQVDpath = args[0];
        if(!paramQVDpath.endsWith("\\")) {
            paramQVDpath = paramQVDpath +"\\";
        }
        String paramQVDfile = args[1];
        String paramCSVfile = args[2];
        char paramDelimiter = args[3].substring(0, 1).toCharArray()[0];
        String paramSearch = args[4];
        String paramMode = "-all";
        if (args.length > 5) {
            paramMode = args[5].toLowerCase();
        }
        
        String paramRegex = wildcardToRegex(paramSearch);
        Pattern pattern = Pattern.compile(paramRegex);

        String path = paramQVDpath;
        String filepattern = paramQVDfile;
        File dir = new File(path);
        File[] files = dir.listFiles(new WildCardFileFilter(filepattern));
        String filename = "";
 
        System.out.println("Input : "+paramQVDpath+paramQVDfile);
        System.out.println("Output: "+paramCSVfile);
        System.out.println("Search: "+paramSearch);
        System.out.println("RegEx : "+paramRegex);
        System.out.println("Mode  : "+paramMode);
        
        try {
            // new file and write BOM bytes first
            fos = new FileOutputStream(paramCSVfile);
            byte[] bom = new byte[] { (byte)0xEF, (byte)0xBB, (byte)0xBF };
            fos.write(bom);

            csvWriter = new CSVWriter(new OutputStreamWriter(fos), paramDelimiter, CSVParser.NULL_CHARACTER, CSVParser.NULL_CHARACTER);
            csvWriter.writeNext(header);            
        }
        catch (IOException ex) {
            ex.printStackTrace();
            System.exit(1);
        }

        long recno = 0;

        // for each QVD file
        for (File file : files) {
            long found = 0;
            filename = file.getName();
            
            try {
                // open QVD file
                qvdReader = new QVDReader(path + filename); 
            }
            catch (FileNotFoundException ex) {
                ex.printStackTrace();
                System.exit(1);
            }

            record[0] = path + filename;

            // loop every field
            for (int col=0; col<qvdReader.getNoOfFields(); col++) {
                record[1] = qvdReader.getFieldName(col);
                // loop every symbol
                for (int sym=0; sym<qvdReader.getFieldNoOfSymbols(col); sym++) {
                    record[2] = Integer.toString(sym);
                    record[3] = qvdReader.getFieldSymbol(col, sym);
                    if(pattern.matcher(record[3]).matches()){
                        csvWriter.writeNext(doQouting(record, paramDelimiter));
                        found++;
                        recno++;
                        if(paramMode.equals("-first")) {
                            break;
                        }
                    }
                    if (recno>0 && recno%2500==0) {
                        System.out.println("QVD symbol search: "+recno+" symbols processed.");
                    }
                }
            }
            if (found>0) {
                System.out.println("QVD symbol search: found "+found+" occurances.");
            }
            try {
                qvdReader.close();
                qvdReader = null;
            } catch (IOException ex) {

            }
            System.out.println("");
        }
        

        System.out.println("QVD symbol search finished. "+recno+" symbol records written.");
                
        try {
            csvWriter.close();
        } catch (IOException ex) {
            
        }
    }
    
    public static class WildCardFileFilter implements FileFilter
    {
        private String _pattern;

        public WildCardFileFilter(String pattern)
        {
            _pattern = wildcardToRegex(pattern);
        }

        @Override
        public boolean accept(File file)
        {
            return Pattern.compile(_pattern).matcher(file.getName()).find();
        }
    }
    
    private static String wildcardToRegex(String wildcard){
        StringBuilder s = new StringBuilder(wildcard.length());
        s.append('^');
        for (int i = 0, is = wildcard.length(); i < is; i++) {
            char c = wildcard.charAt(i);
            switch(c) {
                case '*':
                    s.append(".*");
                    break;
                case '?':
                    s.append(".");
                    break;
                    // escape special regexp-characters
                case '(': case ')': case '[': case ']': case '$':
                case '^': case '.': case '{': case '}': case '|':
                case '\\':
                    s.append("\\");
                    s.append(c);
                    break;
                default:
                    s.append(c);
                    break;
            }
        }
        s.append('$');
        return(s.toString());
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
