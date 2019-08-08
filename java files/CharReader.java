package generate;


/**
 * <p>Title: CharReader</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2004</p>

 * @author not attributable
 * @version 1.0
 */
import java.io.*;

class CharReader
{
   public final static char EOLN = '\n';
   public final static char EOFCH = (char)( -1);

    private LineNumberReader  lineNumberReader;
    private String     fileName;

    /**
     * Construct a CharReader from a file name.
     *
     */

    public CharReader( String fileName )
        throws FileNotFoundException
    {
        lineNumberReader = new LineNumberReader(
                                   new FileReader( fileName ) );
        this.fileName = fileName;
    }



    /**
     * Scan the next character.
     * and return the character scanned.
     */

    public char getChar()
        throws IOException
    {
        return (char) lineNumberReader.read();
    }

    /**
     * The current line number in the source file
     * Begin counting at 1.
     * return the current line number.
     */

    public int getLineNumber()
    {
        // LineNumberReader counts lines from 0. So add 1

        return lineNumberReader.getLineNumber() + 1;
    }


    /**
     * Close the file
     */

    public void close () throws IOException
    {
        lineNumberReader.close ();
    }


}
