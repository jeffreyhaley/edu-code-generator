package generate;


/**
 * <p>Title: Token </p>
 *
 * <p>Description: Identifies a string of text within an L1 source file </p>
 *
 * <p>Copyright: Copyright (c) 2004</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */


final class Token  {

  public int kind;
  public String spelling;


  public Token(int k, String s) {

    if (k == Token.IDENTIFIER) {
      int currentKind = firstReservedWord;
      boolean searching = true;

      while (searching) {
        int comparison = tokenTable[currentKind].compareTo(s);
        if (comparison == 0) {
          kind = currentKind;
          searching = false;
        } else if (comparison > 0 || currentKind == lastReservedWord) {
          kind = Token.IDENTIFIER;
          searching = false;
        } else {
          currentKind ++;
        }
      }
    } else
        kind = k;
    spelling = s;
  }


  public static String spell (int kind) {
    return tokenTable[kind];
  }


  public String toString() {
      return spelling + "\t"+"\t" + tokenTable[kind] ;
  }


  // Token classes...

  public static final int

    // literals, identifiers, operators...
    INTLITERAL	= 0,
    IDENTIFIER	= 1,
    OPERATOR	= 2,

    // operators...

    ADD         = 6,
    SUB         = 7,
    MUL         = 8,
    DIV         = 9,
    LT          = 10,
    LEQ         = 11,
    GT          = 12,
    GEQ         = 13,
    EQ          = 14,
    NEQ         = 15,

    // reserved words - must be in alphabetical order...

    BREAK       = 20,
    CLASS       = 21,
    DO          = 22,
    ELSE        = 23,
    EOF         = 24,
    FOR         = 25,
    IF	        = 26,
    INT         = 27,
    PRINT       = 28,
    READ        = 29,
    RETURN      = 30,
    WHILE	= 31,

    // punctuation...

    BECOMES     = 35,
    COMMA	= 36,
    SEMICOLON	= 37,

    // brackets...
    LPAREN	= 40,
    RPAREN	= 41,
    LCURLY	= 42,
    RCURLY	= 43,

    // special tokens...
    ERROR	= 46,
    LCOMMENT    = 47,
    RCOMMENT    = 48,
    QUOTE       = 49,
    TEMP        = 50;

 /*
   Table for print names for token types
  */
  private static String[] tokenTable = new String[] {
    "<int>",
    "<identifier>",
    "<operator>",
    "",
    "",
    "",
    "add",
    "sub",
    "mul",
    "div",
    "lt",
    "leq",
    "gt",
    "geq",
    "eq",
    "neq",
    "",
    "",
    "",
    "",
    "break",
    "class",
    "do",
    "else",
    "eof",
    "for",
    "if",
    "int",
    "print",
    "read",
    "return",
    "while",
    "",
    "",
    "",
    "",
    "becomes",
    "comma",
    "semicolon",
    "",
    "",
    "lparen",
    "rparen",
    "lcurly",
    "rcurly",
    "",
    "eof",
    "<error>",
    "lcomment",
    "rcomment"
  };

  private final static int	firstReservedWord = Token.CLASS,
                                lastReservedWord  = Token.WHILE;



}
