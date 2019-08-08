package generate;

/**
 * <p>Title: Scanner </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2004</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
import java.io.*;
import java.util.*;


 class Scanner {

  private final static char EOFCH = CharReader.EOFCH;
  private CharReader source;
  private char currentChar;
  private StringBuffer currentSpelling;
  private boolean currentlyScanningToken, comment = false;
  private int lineNumber;
  private int pos=1, startPos=0;
  private int totalErrors = 0;
  private int errorKind = 0;
  private String[] errorMessage = {"Syntax Error: illegal character"};
  //private Vector code = new Vector();
 ////////////////////////////////////////////////////////////////////////////////////

  private boolean isLetter(char c) {
    return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
  }

  private boolean isDigit(char c) {
    return (c >= '0' && c <= '9');
  }

  private void error (int n)
  {
    System.out.println(" Error occured in line "+lineNumber+"\n "+errorMessage[n]);
    totalErrors++;
  }
////////////////////////////////////////////////////////////////////////////////////

  public Scanner(CharReader input) {
    source = input;
    try {

           currentChar = source.getChar();
    }
    catch (Exception e){System.out.println("Unable to read char from input");}
  }


  // takeIt appends the current character to the current token, and gets
  // the next character from the source program.

  private void takeIt() {
    if (currentlyScanningToken)
      currentSpelling.append(currentChar);
    try {
          currentChar = source.getChar();
    }
   catch (Exception e){System.out.println("Unable to read char from input");}
    pos++;
    if (currentChar == '\n') pos = 0;

  }

  // scanSeparator skips a single separator.

  private void scanSeparator() {
    switch (currentChar) {
    case ' ': case '\n': case '\t':
      takeIt();
      break;
    }
  }

  private int scanToken() {

    switch (currentChar) {

    case 'a':  case 'b':  case 'c':  case 'd':  case 'e':
    case 'f':  case 'g':  case 'h':  case 'i':  case 'j':
    case 'k':  case 'l':  case 'm':  case 'n':  case 'o':
    case 'p':  case 'q':  case 'r':  case 's':  case 't':
    case 'u':  case 'v':  case 'w':  case 'x':  case 'y':
    case 'z':  case 'A':  case 'B':  case 'C':  case 'D':
    case 'E':  case 'F':  case 'G':  case 'H':  case 'I':
    case 'J':  case 'K':  case 'L':  case 'M':  case 'N':
    case 'O':  case 'P':  case 'Q':  case 'R':  case 'S':
    case 'T':  case 'U':  case 'V':  case 'W':  case 'X':
    case 'Y':  case 'Z':

    startPos = pos;
      takeIt();
      while (isLetter(currentChar) || isDigit(currentChar))
        takeIt();
      return Token.IDENTIFIER;

    case '0':  case '1':  case '2':  case '3':  case '4':
    case '5':  case '6':  case '7':  case '8':  case '9':
      startPos = pos;
      takeIt();
      while (isDigit(currentChar))
        takeIt();
      return Token.INTLITERAL;

  case '"':
      startPos = pos;
      takeIt();
      return Token.QUOTE;


  case '+':
      startPos = pos;
      takeIt();
      return Token.ADD;

  case '-':
      startPos = pos;
      takeIt();
      return Token.SUB;


  case '<':
      startPos = pos;
      takeIt();
      if(currentChar == '='){
          takeIt();
          return Token.GEQ;
      }
      return Token.GT;

  case '>':
      startPos = pos;
      takeIt();
      if(currentChar == '='){
          takeIt();
          return Token.LEQ;
      }
      return Token.LT;

  case '*':
      startPos = pos;
      takeIt();
      if(currentChar == '/'){
          comment = false;
          takeIt();
          return Token.RCOMMENT;
      }
      return Token.MUL;

  case '/':
      startPos = pos;
      takeIt();
      if(currentChar == '*'){
          comment = true;
          takeIt();
          return Token.LCOMMENT;
      }
      return Token.DIV;

    case '=':
        startPos = pos;
        takeIt();
        if(currentChar == '='){
          takeIt();
          return Token.EQ;
        }
        return Token.BECOMES;

    case '!':
        startPos = pos;
        takeIt();
        if(currentChar == '='){
            takeIt();
            return Token.NEQ;
        }
        return Token.ERROR;

    case ';':
      startPos = pos;
      takeIt();
      return Token.SEMICOLON;

    case ',':
      startPos = pos;
      takeIt();
      return Token.COMMA;

    case '(':
      startPos = pos;
      takeIt();
      return Token.LPAREN;

    case ')':
      startPos = pos;
      takeIt();
      return Token.RPAREN;

    case '{':
      startPos = pos;
      takeIt();
      return Token.LCURLY;

    case '}':
      startPos = pos;
      takeIt();
      return Token.RCURLY;

    case EOFCH:
      return Token.EOF;

    default:
      startPos = pos;
      takeIt();
      errorKind = 0;
      return Token.ERROR;

    }
  }

  public Vector scan () {
    Token tok;
    int kind,c=1, i=0;
    Vector code = new Vector();

   while (true) {
       currentlyScanningToken = false;
       while (currentChar == ' ' || currentChar == '\n')
           scanSeparator();
       currentlyScanningToken = true;
       currentSpelling = new StringBuffer("");
       lineNumber = source.getLineNumber();
       kind = scanToken();
       code.add(new Token(kind, currentSpelling.toString()));

       if (kind == Token.EOF)break;

   }

    return code;
  }

}
