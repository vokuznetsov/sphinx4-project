import java.io.*;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author vkuzn on 03.12.2015.
 */
public class Parser {

    private String pathToFile;
    private String outputForFile;
    private BufferedReader in;
    private PrintWriter out;

    public Parser(String source, String output) {
        this.pathToFile = source;
        this.outputForFile = output;
    }

    public String getSource() {
        return pathToFile;
    }

    public void setSource(String source) {
        this.pathToFile = source;
    }

    public String getOutput() {
        return outputForFile;
    }

    public void setOutput(String output) {
        this.outputForFile = output;
    }

    /**
     * Extract phonemes from file and put ONLY unique in @output file.
     * For example: ABOVE	AH B AH V. This parser put in file AH, B, V phonemes.
     */
    public void parser() throws IOException {

        File source = openFile(pathToFile);
        File output = createFile(outputForFile);
        Set<String> transcription = new TreeSet<String>();   // TreeSet, because I need to store only unique transcription.

        // exclude whole word
        in = new BufferedReader(new FileReader(source));
        String str;
        while ((str = in.readLine()) != null) {
            if (str.contains(" ")) {
                String phonemes = str.substring(str.indexOf(" ") + 1, str.length()); // +1 because exclude space after whole word.
                for (String phoneme : phonemes.split(" "))
                    transcription.add(phoneme);
            }
        }

        // write to file
        out = new PrintWriter(output.getAbsoluteFile());
        for (String aTranscription : transcription) {
            out.println(aTranscription);
        }

        in.close();
        out.close();
    }

    private File createFile(String path) throws IOException {

        File output = new File(path);
        if (output.exists()) {
            output.delete();
            output.createNewFile();
        }
        return output;
    }

    private File openFile(String path) throws FileNotFoundException {
        //InputStream is = Parser.class.getClass().getResourceAsStream(path);

        File file = new File(path);
        if (!file.exists()) {
            System.out.println("Source file don't found.");
            throw new FileNotFoundException("Source file don't found");
        }
        return file;
    }
}
