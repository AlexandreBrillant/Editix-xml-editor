
package com.japisoft.xpath;

/**
This program is available under two licenses : 

1. For non commercial usage : 

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

2. For commercial usage :

You need to get a commercial license for source usage at : 

http://www.editix.com/buy.html

Copyright (c) 2018 Alexandre Brillant - JAPISOFT SARL - http://www.japisoft.com

@author Alexandre Brillant - abrillant@japisoft.com
@author JAPISOFT SARL - http://www.japisoft.com

*/
class XPathToken {

  /** This character denotes the end of file */
  public static final int YYEOF = -1;

  /** initial size of the lookahead buffer */
  private static final int YY_BUFFERSIZE = 16384;

  /** lexical states */
  public static final int YYINITIAL = 0;

  /** 
   * Translates characters to character classes
   */
  private static final String yycmap_packed = 
    "\11\0\2\34\2\0\1\34\22\0\1\34\1\41\1\1\1\0\1\43"+
    "\2\0\1\2\1\22\1\23\1\43\1\43\1\43\1\16\1\33\1\42"+
    "\12\32\1\35\1\0\1\40\1\37\1\36\1\0\1\43\32\0\1\43"+
    "\1\0\1\43\3\0\1\5\1\26\1\11\1\7\1\12\1\25\1\15"+
    "\1\27\1\14\2\0\1\24\1\31\1\6\1\3\1\10\1\0\1\4"+
    "\1\13\1\17\1\20\1\0\1\30\1\21\3\0\1\44\uff83\0";

  /** 
   * Translates characters to character classes
   */
  private static final char [] yycmap = yy_unpack_cmap(yycmap_packed);


  /** 
   * Translates a state to a row index in the transition table (packed version)
   */
  final private static String yy_rowMap_packed = 
    "\0\0\0\45\0\112\0\157\0\224\0\271\0\336\0\u0103\0\u0128\0\u014d"+
    "\0\u0172\0\u0197\0\u01bc\0\u01e1\0\u0206\0\u022b\0\u0250\0\u0275\0\u029a\0\u02bf"+
    "\0\u02e4\0\u0309\0\45\0\u0197\0\45\0\u032e\0\u0353\0\u0378\0\u039d\0\u03c2"+
    "\0\u03e7\0\u040c\0\u0431\0\u0456\0\u047b\0\u04a0\0\u04c5\0\45\0\u0197\0\u0197"+
    "\0\u0197\0\u0197\0\u0197\0\45\0\u04ea\0\u050f\0\u0534\0\u0559\0\u057e\0\u05a3"+
    "\0\u05c8\0\u05ed\0\u0612\0\u0637\0\u065c\0\u0681\0\u04c5\0\u06a6\0\u06cb\0\45"+
    "\0\u06f0\0\u0715\0\u073a\0\u075f\0\u0784\0\u07a9\0\45\0\u07ce\0\u07f3\0\u0818"+
    "\0\u083d\0\u0862\0\u0887\0\u08ac\0\u08d1\0\u08f6\0\u091b\0\u0940\0\u0965\0\u098a"+
    "\0\u09af\0\u09d4\0\u09f9\0\u0a1e\0\u0197\0\u0a43\0\u0a68\0\u0a8d\0\u0ab2\0\u0ad7"+
    "\0\u0afc\0\u0b21\0\u0b46\0\u0b6b\0\u0b90\0\u0bb5\0\u0bda\0\u0bff\0\u0c24\0\u0c49"+
    "\0\u0c6e\0\u0c93\0\u0cb8\0\u0cdd\0\u0d02\0\u0d27\0\u0d4c\0\u0d71\0\u0d96\0\u0dbb"+
    "\0\u0de0\0\u0e05\0\u0e2a\0\u0e4f\0\u0e74\0\u0e99\0\u0ebe\0\u0ee3\0\u0f08\0\u0f2d"+
    "\0\u0f52\0\u0f77\0\45";

  /** 
   * Translates a state to a row index in the transition table
   */
  final private static int [] yy_rowMap = yy_unpack_rowMap(yy_rowMap_packed);


  /** 
   * Unpacks the compressed row translation table.
   *
   * @param packed   the packed row translation table
   * @return         the unpacked row translation table
   */
  private static int [] yy_unpack_rowMap(String packed) {
    int [] map = new int[246];
    int i = 0;  /* index in packed string  */
    int j = 0;  /* index in unpacked array */
    while (i < 246) {
      int high = ((int) packed.charAt(i++)) << 16;
      map[j++] = high | packed.charAt(i++);
    }
    return map;
  }
  /** 
   * The packed transition table of the DFA (part 0)
   */
  private static final String yy_packed0 = 
    "\1\2\1\3\1\4\1\5\1\2\1\6\1\7\1\10"+
    "\1\11\1\12\1\2\1\13\2\2\1\14\1\15\2\2"+
    "\2\14\1\2\1\16\4\2\1\17\1\20\1\21\1\22"+
    "\1\23\1\14\1\24\1\25\1\26\1\14\1\27\1\2"+
    "\2\0\17\2\2\0\10\2\10\0\1\2\1\3\1\30"+
    "\43\3\2\4\1\30\42\4\1\2\2\0\1\2\1\31"+
    "\15\2\2\0\10\2\10\0\2\2\2\0\3\2\1\32"+
    "\10\2\1\33\2\2\2\0\10\2\10\0\2\2\2\0"+
    "\1\34\1\2\1\35\14\2\2\0\10\2\10\0\2\2"+
    "\2\0\7\2\1\36\7\2\2\0\10\2\10\0\2\2"+
    "\2\0\1\2\1\37\1\40\14\2\2\0\10\2\10\0"+
    "\2\2\2\0\17\2\2\0\3\2\1\41\4\2\10\0"+
    "\2\2\2\0\7\2\1\42\7\2\2\0\10\2\10\0"+
    "\1\2\45\0\1\2\2\0\7\2\1\43\7\2\2\0"+
    "\10\2\10\0\2\2\2\0\1\44\16\2\2\0\10\2"+
    "\10\0\1\2\32\0\1\17\1\45\11\0\1\2\2\0"+
    "\17\2\2\0\7\2\1\46\10\0\1\2\34\0\1\21"+
    "\45\0\1\47\46\0\1\50\44\0\1\51\44\0\1\52"+
    "\47\0\1\53\2\0\1\2\2\0\4\2\1\54\1\2"+
    "\1\55\10\2\2\0\10\2\10\0\2\2\2\0\14\2"+
    "\1\56\2\2\2\0\10\2\10\0\2\2\2\0\4\2"+
    "\1\57\12\2\2\0\10\2\10\0\2\2\2\0\17\2"+
    "\2\0\5\2\1\60\2\2\10\0\2\2\2\0\10\2"+
    "\1\61\6\2\2\0\10\2\10\0\2\2\2\0\1\62"+
    "\6\2\1\63\7\2\2\0\10\2\10\0\2\2\2\0"+
    "\1\2\1\64\15\2\2\0\10\2\10\0\2\2\2\0"+
    "\11\2\1\65\5\2\2\0\10\2\10\0\2\2\2\0"+
    "\17\2\2\0\1\66\7\2\10\0\2\2\2\0\16\2"+
    "\1\67\2\0\10\2\10\0\2\2\2\0\17\2\2\0"+
    "\1\70\7\2\10\0\1\2\32\0\1\71\12\0\1\2"+
    "\2\0\7\2\1\72\7\2\2\0\10\2\10\0\2\2"+
    "\2\0\1\2\1\73\15\2\2\0\10\2\10\0\2\2"+
    "\2\0\7\2\1\74\7\2\2\0\10\2\10\0\2\2"+
    "\2\0\7\2\1\75\7\2\2\0\10\2\10\0\2\2"+
    "\2\0\6\2\1\76\10\2\2\0\10\2\10\0\2\2"+
    "\2\0\6\2\1\77\10\2\2\0\10\2\10\0\2\2"+
    "\2\0\6\2\1\100\10\2\2\0\10\2\10\0\2\2"+
    "\2\0\7\2\1\101\7\2\2\0\10\2\10\0\2\2"+
    "\2\0\17\2\2\0\1\102\7\2\10\0\2\2\2\0"+
    "\17\2\2\0\1\2\1\103\6\2\10\0\2\2\2\0"+
    "\14\2\1\104\2\2\2\0\10\2\10\0\2\2\2\0"+
    "\17\2\2\0\1\105\7\2\10\0\2\2\2\0\10\2"+
    "\1\106\6\2\2\0\10\2\10\0\2\2\2\0\11\2"+
    "\1\107\5\2\2\0\10\2\10\0\2\2\2\0\10\2"+
    "\1\110\6\2\2\0\10\2\10\0\2\2\2\0\7\2"+
    "\1\111\7\2\2\0\10\2\10\0\2\2\2\0\7\2"+
    "\1\112\7\2\2\0\10\2\10\0\2\2\2\0\7\2"+
    "\1\113\7\2\2\0\10\2\10\0\2\2\2\0\3\2"+
    "\1\114\13\2\2\0\10\2\10\0\2\2\2\0\4\2"+
    "\1\103\12\2\2\0\10\2\10\0\2\2\2\0\17\2"+
    "\1\115\1\0\10\2\10\0\2\2\2\0\1\116\16\2"+
    "\2\0\10\2\10\0\2\2\2\0\14\2\1\117\2\2"+
    "\2\0\10\2\10\0\2\2\2\0\17\2\2\0\2\2"+
    "\1\120\5\2\10\0\2\2\2\0\5\2\1\121\11\2"+
    "\2\0\10\2\10\0\2\2\2\0\3\2\1\122\13\2"+
    "\2\0\10\2\10\0\2\2\2\0\10\2\1\123\6\2"+
    "\2\0\10\2\10\0\2\2\2\0\4\2\1\124\12\2"+
    "\2\0\10\2\10\0\2\2\2\0\14\2\1\103\2\2"+
    "\2\0\10\2\10\0\1\2\23\0\1\125\21\0\1\2"+
    "\2\0\17\2\2\0\4\2\1\124\3\2\10\0\2\2"+
    "\2\0\1\126\16\2\2\0\10\2\10\0\2\2\2\0"+
    "\15\2\1\127\1\2\2\0\10\2\10\0\2\2\2\0"+
    "\2\2\1\130\14\2\2\0\10\2\10\0\2\2\2\0"+
    "\4\2\1\131\12\2\2\0\10\2\10\0\2\2\2\0"+
    "\10\2\1\132\6\2\2\0\10\2\10\0\2\2\2\0"+
    "\11\2\1\133\5\2\2\0\10\2\10\0\2\2\2\0"+
    "\1\2\1\134\15\2\2\0\10\2\10\0\2\2\2\0"+
    "\14\2\1\135\2\2\2\0\10\2\10\0\2\2\2\0"+
    "\6\2\1\135\10\2\2\0\10\2\10\0\2\2\2\0"+
    "\2\2\1\136\14\2\2\0\10\2\10\0\2\2\2\0"+
    "\11\2\1\137\5\2\2\0\10\2\10\0\2\2\2\0"+
    "\3\2\1\140\13\2\2\0\10\2\10\0\2\2\2\0"+
    "\13\2\1\141\3\2\2\0\10\2\10\0\2\2\2\0"+
    "\7\2\1\103\7\2\2\0\10\2\10\0\2\2\2\0"+
    "\3\2\1\142\13\2\2\0\10\2\10\0\2\2\2\0"+
    "\3\2\1\143\13\2\2\0\10\2\10\0\2\2\2\0"+
    "\12\2\1\144\4\2\2\0\10\2\10\0\2\2\2\0"+
    "\1\145\16\2\2\0\10\2\10\0\2\2\2\0\14\2"+
    "\1\134\2\2\2\0\10\2\10\0\2\2\2\0\12\2"+
    "\1\146\4\2\2\0\10\2\10\0\2\2\2\0\13\2"+
    "\1\147\3\2\2\0\10\2\10\0\2\2\2\0\1\2"+
    "\1\150\15\2\2\0\10\2\10\0\2\2\2\0\13\2"+
    "\1\151\3\2\2\0\10\2\10\0\2\2\2\0\10\2"+
    "\1\152\6\2\2\0\10\2\10\0\2\2\2\0\13\2"+
    "\1\153\3\2\2\0\10\2\10\0\2\2\2\0\11\2"+
    "\1\154\5\2\2\0\10\2\10\0\2\2\2\0\11\2"+
    "\1\155\5\2\2\0\10\2\10\0\2\2\2\0\10\2"+
    "\1\13\6\2\2\0\10\2\10\0\2\2\2\0\3\2"+
    "\1\156\13\2\2\0\10\2\10\0\2\2\2\0\17\2"+
    "\2\0\2\2\1\157\5\2\10\0\2\2\2\0\10\2"+
    "\1\160\6\2\2\0\10\2\10\0\2\2\2\0\17\2"+
    "\2\0\1\161\7\2\10\0\2\2\2\0\14\2\1\162"+
    "\2\2\2\0\10\2\10\0\2\2\2\0\11\2\1\163"+
    "\5\2\2\0\10\2\10\0\2\2\2\0\1\2\1\164"+
    "\15\2\2\0\10\2\10\0\2\2\2\0\3\2\1\165"+
    "\13\2\2\0\10\2\10\0\2\2\2\0\15\2\1\166"+
    "\1\2\2\0\10\2\10\0\2\2\2\0\12\2\1\103"+
    "\4\2\2\0\10\2\10\0\2\2\2\0\6\2\1\167"+
    "\10\2\2\0\10\2\10\0\2\2\2\0\14\2\1\170"+
    "\2\2\2\0\10\2\10\0\2\2\2\0\11\2\1\171"+
    "\5\2\2\0\10\2\10\0\2\2\2\0\1\172\16\2"+
    "\2\0\10\2\10\0\2\2\2\0\3\2\1\173\13\2"+
    "\2\0\10\2\10\0\1\2";

  /** 
   * The transition table of the DFA
   */
  private static final int yytrans [] = yy_unpack();


  /* error codes */
  private static final int YY_UNKNOWN_ERROR = 0;
  private static final int YY_ILLEGAL_STATE = 1;
  private static final int YY_NO_MATCH = 2;
  private static final int YY_PUSHBACK_2BIG = 3;

  /* error messages for the codes above */
  private static final String YY_ERROR_MSG[] = {
    "Unkown internal scanner error",
    "Internal error: unknown state",
    "Error: could not match input",
    "Error: pushback value was too large"
  };

  /**
   * YY_ATTRIBUTE[aState] contains the attributes of state <code>aState</code>
   */
  private static final byte YY_ATTRIBUTE[] = {
     0,  1,  0,  0,  1,  1,  1,  1,  1,  1,  1,  9,  1,  1,  1,  1, 
     1,  1,  1,  1,  0,  1,  1,  9,  1,  1,  1,  1,  1,  1,  1,  1, 
     1,  1,  1,  1,  0,  1,  9,  9,  9,  9,  9,  1,  1,  1,  1,  1, 
     1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1, 
     1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  0,  1,  1,  1, 
     1,  1,  1,  1,  9,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1, 
     1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1, 
     1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1
  };

  /** the input device */
  private java.io.Reader yy_reader;

  /** the current state of the DFA */
  private int yy_state;

  /** the current lexical state */
  private int yy_lexical_state = YYINITIAL;

  /** this buffer contains the current text to be matched and is
      the source of the yytext() string */
  private char yy_buffer[] = new char[YY_BUFFERSIZE];

  /** the textposition at the last accepting state */
  private int yy_markedPos;

  /** the textposition at the last state to be included in yytext */
  private int yy_pushbackPos;

  /** the current text position in the buffer */
  private int yy_currentPos;

  /** startRead marks the beginning of the yytext() string in the buffer */
  private int yy_startRead;

  /** endRead marks the last character in the buffer, that has been read
      from input */
  private int yy_endRead;

  /** number of newlines encountered up to the start of the matched text */
  private int yyline;

  /** the number of characters up to the start of the matched text */
  private int yychar;

  /**
   * the number of characters from the last newline up to the start of the 
   * matched text
   */
  private int yycolumn; 

  /** 
   * yy_atBOL == true <=> the scanner is currently at the beginning of a line
   */
  private boolean yy_atBOL = true;

  /** yy_atEOF == true <=> the scanner is at the EOF */
  private boolean yy_atEOF;

  /** denotes if the user-EOF-code has already been executed */
  private boolean yy_eof_done;

  /* user code: */
  private XPathParser yyparser;

  public int getCurrentPos() { return yy_currentPos; }

  public XPathToken(java.io.Reader r, XPathParser yyparser) {
    this(r);
    this.yyparser = yyparser;
  }


  /**
   * Creates a new scanner
   * There is also a java.io.InputStream version of this constructor.
   *
   * @param   in  the java.io.Reader to read input from.
   */
  XPathToken(java.io.Reader in) {
    this.yy_reader = in;
  }

  /**
   * Creates a new scanner.
   * There is also java.io.Reader version of this constructor.
   *
   * @param   in  the java.io.Inputstream to read input from.
   */
  XPathToken(java.io.InputStream in) {
    this(new java.io.InputStreamReader(in));
  }

  /** 
   * Unpacks the split, compressed DFA transition table.
   *
   * @return the unpacked transition table
   */
  private static int [] yy_unpack() {
    int [] trans = new int[3996];
    int offset = 0;
    offset = yy_unpack(yy_packed0, offset, trans);
    return trans;
  }

  /** 
   * Unpacks the compressed DFA transition table.
   *
   * @param packed   the packed transition table
   * @return         the index of the last entry
   */
  private static int yy_unpack(String packed, int offset, int [] trans) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      value--;
      do trans[j++] = value; while (--count > 0);
    }
    return j;
  }

  /** 
   * Unpacks the compressed character translation table.
   *
   * @param packed   the packed character translation table
   * @return         the unpacked character translation table
   */
  private static char [] yy_unpack_cmap(String packed) {
    char [] map = new char[0x10000];
    int i = 0;  /* index in packed string  */
    int j = 0;  /* index in unpacked array */
    while (i < 118) {
      int  count = packed.charAt(i++);
      char value = packed.charAt(i++);
      do map[j++] = value; while (--count > 0);
    }
    return map;
  }


  /**
   * Refills the input buffer.
   *
   * @return      <code>false</code>, iff there was new input.
   * 
   * @exception   IOException  if any I/O-Error occurs
   */
  private boolean yy_refill() throws java.io.IOException {

    /* first: make room (if you can) */
    if (yy_startRead > 0) {
      System.arraycopy(yy_buffer, yy_startRead, 
                       yy_buffer, 0, 
                       yy_endRead-yy_startRead);

      /* translate stored positions */
      yy_endRead-= yy_startRead;
      yy_currentPos-= yy_startRead;
      yy_markedPos-= yy_startRead;
      yy_pushbackPos-= yy_startRead;
      yy_startRead = 0;
    }

    /* is the buffer big enough? */
    if (yy_currentPos >= yy_buffer.length) {
      /* if not: blow it up */
      char newBuffer[] = new char[yy_currentPos*2];
      System.arraycopy(yy_buffer, 0, newBuffer, 0, yy_buffer.length);
      yy_buffer = newBuffer;
    }

    /* finally: fill the buffer with new input */
    int numRead = yy_reader.read(yy_buffer, yy_endRead, 
                                            yy_buffer.length-yy_endRead);

    if (numRead < 0) {
      return true;
    }
    else {
      yy_endRead+= numRead;  
      return false;
    }
  }


  /**
   * Closes the input stream.
   */
  public final void yyclose() throws java.io.IOException {
    yy_atEOF = true;            /* indicate end of file */
    yy_endRead = yy_startRead;  /* invalidate buffer    */

    if (yy_reader != null)
      yy_reader.close();
  }


  /**
   * Closes the current stream, and resets the
   * scanner to read from a new input stream.
   *
   * All internal variables are reset, the old input stream 
   * <b>cannot</b> be reused (internal buffer is discarded and lost).
   * Lexical state is set to <tt>YY_INITIAL</tt>.
   *
   * @param reader   the new input stream 
   */
  public final void yyreset(java.io.Reader reader) throws java.io.IOException {
    yyclose();
    yy_reader = reader;
    yy_atBOL  = true;
    yy_atEOF  = false;
    yy_endRead = yy_startRead = 0;
    yy_currentPos = yy_markedPos = yy_pushbackPos = 0;
    yyline = yychar = yycolumn = 0;
    yy_lexical_state = YYINITIAL;
  }


  /**
   * Returns the current lexical state.
   */
  public final int yystate() {
    return yy_lexical_state;
  }


  /**
   * Enters a new lexical state
   *
   * @param newState the new lexical state
   */
  public final void yybegin(int newState) {
    yy_lexical_state = newState;
  }


  /**
   * Returns the text matched by the current regular expression.
   */
  public final String yytext() {
    return new String( yy_buffer, yy_startRead, yy_markedPos-yy_startRead );
  }


  /**
   * Returns the character at position <tt>pos</tt> from the 
   * matched text. 
   * 
   * It is equivalent to yytext().charAt(pos), but faster
   *
   * @param pos the position of the character to fetch. 
   *            A value from 0 to yylength()-1.
   *
   * @return the character at position pos
   */
  public final char yycharat(int pos) {
    return yy_buffer[yy_startRead+pos];
  }


  /**
   * Returns the length of the matched text region.
   */
  public final int yylength() {
    return yy_markedPos-yy_startRead;
  }


  /**
   * Reports an error that occured while scanning.
   *
   * In a wellformed scanner (no or only correct usage of 
   * yypushback(int) and a match-all fallback rule) this method 
   * will only be called with things that "Can't Possibly Happen".
   * If this method is called, something is seriously wrong
   * (e.g. a JFlex bug producing a faulty scanner etc.).
   *
   * Usual syntax/scanner level error handling should be done
   * in error fallback rules.
   *
   * @param   errorCode  the code of the errormessage to display
   */
  private void yy_ScanError(int errorCode) {
    String message;
    try {
      message = YY_ERROR_MSG[errorCode];
    }
    catch (ArrayIndexOutOfBoundsException e) {
      message = YY_ERROR_MSG[YY_UNKNOWN_ERROR];
    }

    throw new Error(message);
  } 


  /**
   * Pushes the specified amount of characters back into the input stream.
   *
   * They will be read again by then next call of the scanning method
   *
   * @param number  the number of characters to be read again.
   *                This number must not be greater than yylength()!
   */
  private void yypushback(int number)  {
    if ( number > yylength() )
      yy_ScanError(YY_PUSHBACK_2BIG);

    yy_markedPos -= number;
  }


  /**
   * Contains user EOF-code, which will be executed exactly once,
   * when the end of file is reached
   */
  private void yy_do_eof() throws java.io.IOException {
    if (!yy_eof_done) {
      yy_eof_done = true;
      yyclose();
    }
  }


  /**
   * Resumes scanning until the next regular expression is matched,
   * the end of input is encountered or an I/O-Error occurs.
   *
   * @return      the next token
   * @exception   IOException  if any I/O-Error occurs
   */
  public int yylex() throws java.io.IOException {
    int yy_input;
    int yy_action;

    // cached fields:
    int yy_currentPos_l;
    int yy_startRead_l;
    int yy_markedPos_l;
    int yy_endRead_l = yy_endRead;
    char [] yy_buffer_l = yy_buffer;
    char [] yycmap_l = yycmap;

    int [] yytrans_l = yytrans;
    int [] yy_rowMap_l = yy_rowMap;
    byte [] yy_attr_l = YY_ATTRIBUTE;

    while (true) {
      yy_markedPos_l = yy_markedPos;

      yy_action = -1;

      yy_startRead_l = yy_currentPos_l = yy_currentPos = 
                       yy_startRead = yy_markedPos_l;

      yy_state = yy_lexical_state;


      yy_forAction: {
        while (true) {

          if (yy_currentPos_l < yy_endRead_l)
            yy_input = yy_buffer_l[yy_currentPos_l++];
          else if (yy_atEOF) {
            yy_input = YYEOF;
            break yy_forAction;
          }
          else {
            // store back cached positions
            yy_currentPos  = yy_currentPos_l;
            yy_markedPos   = yy_markedPos_l;
            boolean eof = yy_refill();
            // get translated positions and possibly new buffer
            yy_currentPos_l  = yy_currentPos;
            yy_markedPos_l   = yy_markedPos;
            yy_buffer_l      = yy_buffer;
            yy_endRead_l     = yy_endRead;
            if (eof) {
              yy_input = YYEOF;
              break yy_forAction;
            }
            else {
              yy_input = yy_buffer_l[yy_currentPos_l++];
            }
          }
          int yy_next = yytrans_l[ yy_rowMap_l[yy_state] + yycmap_l[yy_input] ];
          if (yy_next == -1) break yy_forAction;
          yy_state = yy_next;

          int yy_attributes = yy_attr_l[yy_state];
          if ( (yy_attributes & 1) == 1 ) {
            yy_action = yy_state; 
            yy_markedPos_l = yy_currentPos_l; 
            if ( (yy_attributes & 8) == 8 ) break yy_forAction;
          }

        }
      }

      // store back cached position
      yy_markedPos = yy_markedPos_l;

      switch (yy_action) {

        case 41: 
          { 
       yyparser.yylval = new XPathParserVal( yytext() );
       return XPathParser.DIFF;
 }
        case 124: break;
        case 40: 
          { 
       yyparser.yylval = new XPathParserVal( yytext() );
       return XPathParser.INFE;
 }
        case 125: break;
        case 39: 
          { 
       yyparser.yylval = new XPathParserVal( yytext() );
       return XPathParser.SUPE;
 }
        case 126: break;
        case 1: 
        case 4: 
        case 5: 
        case 6: 
        case 7: 
        case 8: 
        case 9: 
        case 10: 
        case 12: 
        case 13: 
        case 25: 
        case 26: 
        case 27: 
        case 28: 
        case 29: 
        case 30: 
        case 31: 
        case 32: 
        case 33: 
        case 34: 
        case 35: 
        case 44: 
        case 45: 
        case 46: 
        case 47: 
        case 48: 
        case 49: 
        case 50: 
        case 51: 
        case 52: 
        case 53: 
        case 54: 
        case 55: 
        case 57: 
        case 58: 
        case 60: 
        case 61: 
        case 62: 
        case 63: 
        case 64: 
        case 65: 
        case 67: 
        case 68: 
        case 69: 
        case 70: 
        case 71: 
        case 72: 
        case 73: 
        case 74: 
        case 75: 
        case 77: 
        case 78: 
        case 79: 
        case 80: 
        case 81: 
        case 82: 
        case 83: 
        case 85: 
        case 86: 
        case 87: 
        case 88: 
        case 89: 
        case 90: 
        case 92: 
        case 93: 
        case 94: 
        case 95: 
        case 96: 
        case 97: 
        case 98: 
        case 100: 
        case 101: 
        case 102: 
        case 103: 
        case 104: 
        case 105: 
        case 106: 
        case 107: 
        case 108: 
        case 109: 
        case 110: 
        case 111: 
        case 112: 
        case 113: 
        case 114: 
        case 115: 
        case 116: 
        case 117: 
        case 118: 
        case 119: 
        case 120: 
        case 121: 
          { 
     yyparser.yylval = new XPathParserVal( yytext() );
     return XPathParser.NAME;
 }
        case 127: break;
        case 11: 
        case 15: 
        case 17: 
        case 18: 
        case 19: 
        case 21: 
        case 22: 
          {  return (int) yycharat(0);  }
        case 128: break;
        case 16: 
          {   }
        case 129: break;
        case 84: 
          { 
       yyparser.yylval = new XPathParserVal( yytext() );
       return XPathParser.TEXT_TYPE;
 }
        case 130: break;
        case 66: 
        case 91: 
        case 99: 
          { 
       yyparser.yylval = new XPathParserVal( yytext() );
       return XPathParser.AXIS_NAME;
 }
        case 131: break;
        case 59: 
          { 
       yyparser.yylval = new XPathParserVal( yytext() );
       return XPathParser.NODE_TYPE;
 }
        case 132: break;
        case 14: 
        case 56: 
          { 
       yyparser.yylval = new XPathParserVal( yytext() );
       return XPathParser.NUMBER;
 }
        case 133: break;
        case 38: 
          { 
       yyparser.yylval = new XPathParserVal( yytext() );
       return XPathParser.AXIS_SEP;
 }
        case 134: break;
        case 23: 
          {  
       yyparser.yylval = new XPathParserVal( yytext() );
       return XPathParser.LITERAL; 
 }
        case 135: break;
        case 122: 
          { 
       yyparser.yylval = new XPathParserVal( yytext() );
       return XPathParser.PI;
 }
        case 136: break;
        case 43: 
          { 
       yyparser.yylval = new XPathParserVal( yytext() );
       return XPathParser.AND;
 }
        case 137: break;
        case 42: 
          { 
       yyparser.yylval = new XPathParserVal( yytext() );
       return XPathParser.DS;
 }
        case 138: break;
        case 24: 
          { 
       yyparser.yylval = new XPathParserVal( yytext() );
       return XPathParser.OR;
 }
        case 139: break;
        case 37: 
          { 
       yyparser.yylval = new XPathParserVal( yytext() );
       return XPathParser.DP;
 }
        case 140: break;
        default: 
          if (yy_input == YYEOF && yy_startRead == yy_currentPos) {
            yy_atEOF = true;
            yy_do_eof();
              { return 0; }
          } 
          else {
            yy_ScanError(YY_NO_MATCH);
          }
      }
    }
  }


}
