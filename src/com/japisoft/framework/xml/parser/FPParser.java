package com.japisoft.framework.xml.parser;

import java.util.HashMap;
import java.io.*;

import com.japisoft.framework.xml.parser.HandlerException;
import com.japisoft.framework.xml.parser.ParseException;
import com.japisoft.framework.xml.parser.FPParser;
import com.japisoft.framework.xml.parser.document.*;
import com.japisoft.framework.xml.parser.node.*;
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
public class FPParser {
	private static final int ERROR_STATE = 0;
	private static final int STATE_COUNT = 22;
	private static final int STATE_INIT = 1;
	private static final int HIGH_BYTE_MASK = ( 255 << 8 );

	static {
		initAutomaton();
	}

	public FPParser() {}
		
	private static byte[][] cAutomaton;
	private static byte[] cAutomatonDefaut;
	private static boolean[] cBufferMode;
	private static boolean[] cTransitionPreservedChar;
	
	protected String cCurrentFile;

	private NodeFactory nf;

	/** Update the factory for building node */
	public void setNodeFactory(NodeFactory nf) {
		this.nf = nf;
	}

	/** @return the factory for building node */
	public NodeFactory getNodeFactory() {
		if ( nf == null )
			nf = NodeFactoryImpl.getFactory();
		return nf;
	}

	// Reset the parsing automaton
	private static void initAutomaton() {
		byte[] defaut = new byte[STATE_COUNT];

		// ELEMENT

		byte[] e1, e1b, e2, e3, e4, e5, e6, e7, e8, e9, e10, e11, e12, e13, e14, e15, e16, e17, e18, e19, e20;

		// '<' : 1 + WS

		byte state = STATE_INIT;
		e1 = createStateDecider();
		e1['<'] = (byte) (state + 2);
		blank(e1, state);
		defaut[state] = (byte) (state + 1);

		// '<' : 1B
		state++;
		e1b = createStateDecider();
		e1b['<'] = (byte) (state + 1);
		defaut[state] = state;

		// ELEMENT : 2

		state++;
		e2 = createStateDecider();
		blank(e2, (byte) (state + 1));
		e2[':'] = (byte) (state + 14);
		e2['!'] = (byte) (state + 9);
		e2['?'] = (byte) (state + 18);
		defaut[state] = state;
		e2['>'] = STATE_INIT;

		// FIN ELEMENT : 3

		state++;
		e3 = createStateDecider();
		blank(e3, state);
		defaut[state] = (byte) (state + 1);
		e3['/'] = (byte) (state + 5);
		e3['>'] = STATE_INIT;

		// ATTRIBUT
		state++;
		e4 = createStateDecider();
		defaut[state] = state;
		blank(e4, (byte) (state + 1));
		e4['='] = (byte) (state + 2);
		e4[':'] = (byte) (state + 13);

		// FIN BLA, ATTENTE "=" : 4

		state++;
		e5 = createStateDecider();
		blank(e5, state);
		e5['='] = (byte) (state + 1);

		// '"' : 5

		state++;
		e6 = createStateDecider();
		blank(e6, state);
		e6['"'] = (byte) (state + 1);
		e6['\''] = (byte) (state + 12);

		// VALEUR ATTRIBUT : 6

		state++;
		e7 = createStateDecider();
		defaut[state] = state;
		e7['"'] = (byte) (state + 1);

		// NOUVEL ATTRIBUT ? : 7
		state++;
		e8 = createStateDecider();
		e8['>'] = STATE_INIT;
		e8['/'] = (byte) (state + 1);
		e8['?'] = (byte) (state + 1);
		blank(e8, state);
		defaut[state] = (byte) (state - 4);

		// FIN : 8

		state++;
		e9 = createStateDecider();
		blank(e9, state);
		e9['>'] = STATE_INIT;

		// FIN 2 : 9
		state++;
		e10 = createStateDecider();
		e10['>'] = STATE_INIT;
		defaut[state] = state;

		// Comment ?
		state++;
		e11 = createStateDecider();
		e11['-'] = (byte) (state + 1);
		e11['['] = (byte) (state + 3);
		e11['>'] = STATE_INIT;
		defaut[state] = (byte) (state - 1);

		// Ok for comment parse to the end "-" and ">"
		state++;
		e12 = createStateDecider();
		e12['-'] = (byte) (state + 1);
		defaut[state] = state;

		// End of comment
		state++;
		e13 = createStateDecider();
		e13['-'] = state;
		e13['>'] = STATE_INIT;
		defaut[state] = (byte) (state - 1);

		// Start for CDATA
		state++;
		e14 = createStateDecider();
		e14[']'] = (byte) (state + 1);
		defaut[state] = state;

		// End of CDATA
		state++;
		e15 = createStateDecider();
		e15['>'] = STATE_INIT;
		e15[']'] = state;
		defaut[state] = (byte) (state - 1);

		// Namespace ELEMENT
		state++;
		e16 = createStateDecider();
		blank(e16, (byte) (state - 13));
		defaut[state] = state;
		e16['>'] = STATE_INIT;

		// Namespace ATTRIBUTE

		state++;
		e17 = createStateDecider();
		defaut[state] = state;
		blank(e17, (byte) (state - 12));
		e17['='] = (byte) (state - 11);

		// DEBUT VALEUR ATTRIBUT '

		// FIN VALEUR ATTRIBUT '

		state++;
		e18 = createStateDecider();
		defaut[state] = state;
		e18['\''] = (byte) (state - 10);

		// Processing instruction

		state++;
		e19 = createStateDecider();
		defaut[state] = state;
		e19['?'] = (byte) (state + 1);

		state++;
		e20 = createStateDecider();
		defaut[state] = (byte) state;
		e20['>'] = STATE_INIT;

		cAutomatonDefaut = defaut;
		cAutomaton = new byte[][] { null, e1, e1b, e2, e3, e4, e5, e6, e7, e8,
				e9, e10, e11, e12, e13, e14, e15, e16, e17, e18, e19, e20 };

		cBufferMode = new boolean[] { false, false, true, true, false, true,
				false, false, true, false, true, false, false, false, false,
				true, false, true, true, true, false, false };
		cTransitionPreservedChar = new boolean[cBufferMode.length];
		cTransitionPreservedChar[2] = true;
	}

	private static byte[] createStateDecider() {
		return new byte[255];
	}

	/** Support for CDATA Section : By default this is enabled */
	public void cdataEnabled(boolean enabled) {
		cBufferMode[14] = enabled;
	}

	/** @return is CDATA secion is enabled or not */
	public boolean isCdataEnabled() {
		return cBufferMode[14];
	}

	/** Save XML comment : by default this is disabled */
	public void preserveComment(boolean comment) {
		cBufferMode[13] = comment;
	}

	private static void blank(byte[] e, byte next) {
		e[0x20] = next;
		e[0x9] = next;
		e[0xD] = next;
		e[0xA] = next;
	}

	private boolean buffering = true;

	/**
	 * @deprecated This method has no effet
	 */
	public void bufferingMode(boolean buffering) {
		this.buffering = buffering;
	}

	public boolean hasBufferingMode() {
		return buffering;
	}

	private boolean flatView;

	/**
	 * @return <code>true</code> if a flat mode view is available
	 */
	public boolean isFlatView() {
		return flatView;
	}

	/**
	 * @param b
	 *            Reset the flat view. If <code>true</code> a flat view for
	 *            the current tree will be built by the current document. By
	 *            default <code>false</code>
	 */
	public void setFlatView(boolean b) {
		flatView = b;
	}

	private boolean prolog = true;

	/** Set the prolog mandatory, but default to <code>true</code> */
	public void setMandatoryProlog(boolean prolog) {
		this.prolog = prolog;
	}

	/** @return <code>true</code> is the XML prolog is mandatory */
	public boolean isMandatoryProlog() {
		return prolog;
	}

	private boolean prologFound;

	private boolean ws;

	/**
	 * Eliminate extra white space between tag. By default to <code>false</code>
	 * 
	 * @deprecated
	 */
	public void preserveWhiteSpace(boolean ws) {
		this.ws = ws;
	}

	/**
	 * @return eliminate extra white space between tag. By default to
	 *         <code>false</code>
	 * @deprecated
	 */
	public boolean isPreserveWhiteSpace() {
		return ws;
	}

	private boolean interruptParsing = false;

	/**
	 * Interrupt the current parsing, this is useful for asynchronous parsing
	 * usage
	 */
	public void interruptParsing() {
		interruptParsing = true;
	}

	/** @return true if the parsing has been interrupted */
	public boolean isInterrupted() {
		return interruptParsing;
	}

	/** Continue parsing even with xml error */
	public static final int CONTINUE_PARSING_MODE = 0;

	/** Don't check for closing node : only for well formed document */
	public static final int LOW_PARSING_MODE = 1;

	/** Default mode for parsing */
	public static final int MEDIUM_PARSING_MODE = 2;

	/** Validate the document : not available */
	public static final int HIGH_PARSING_MODE = 3;

	private int mode = MEDIUM_PARSING_MODE;

	/**
	 * Reset the parsing mode: this choice can impact on the parser velocity.
	 * 
	 * @param mode
	 *            <code>CONTINUE_PARSING_MODE</code> for ignoring xml error,
	 *            <code>LOW_PARSING_MODE</code> for not checking closing tag :
	 *            always for well formed document,
	 *            <code>MEDIUM_PARSING_MODE</code> by default,
	 *            <code>HIGH_PARSING_MODE</code> not available
	 */
	public void setParsingMode(int mode) {
		this.mode = mode;
	}

	/** @return the current parsing mode */
	public int getParsingMode() {
		return mode;
	}

	private boolean currentNS = false;

	protected boolean realTimeCurrentNS = false;

	private boolean entityMode = false;

	// Convert character reference to char
	private char hexaToChar(String hexa) {
		int r = 0;
		int s = hexa.length();
		if (s == 0)
			throw new RuntimeException(Messages.ERROR_ENTITY2);
		for (int i = 0; i < s; i++) {
			char c = hexa.charAt(i);
			int d = (s - i - 1) << 2;
			if (c >= '0' && c <= '9')
				r += (c - '0') << d;
			else if (c >= 'a' && c <= 'f') {
				r += (10 + c - 'a') << d;
			} else if (c >= 'A' && c <= 'F') {
				r += (10 + c - 'A') << d;
			} else
				throw new RuntimeException(Messages.ERROR_ENTITY3 + " :" + hexa);
		}
		return (char) r;
	}

	public int line;

	public int lineText;	// Specific for the text starting
	
	public int col;

	public int offset;

	private boolean error = false;

	private boolean backgroundMode = false;

	/** @return <code>true</code> if the last parsing has error */
	public boolean hasError() {
		return error;
	}
	
	public Document parse(Reader reader) throws ParseException {
		return parse(reader,null);
	}

	public Document parseContent( String content) throws ParseException {
		return parse( new StringReader( content ), null );
	}
	
	public Document parse(InputStream reader) throws ParseException {
		try {
			return parse( new InputStreamReader( reader, "UTF-8" ) );
		} catch( UnsupportedEncodingException exc ) {
			throw new ParseException("Wrong default encoding ? " + exc.getMessage() );
		}
	}
	
	/** Parse the current document */
	public Document parse(Reader cInput,DocumentBuilder db) throws ParseException {				
		if (cInput == null)
			throw new ParseException( "No reader ?" );
		
		if ( db == null )
			db = new DocumentBuilderImpl();
		
		preserveWhiteSpace(false);
		bufferingMode(true);
		htNameSpaceURI = new HashMap();
		htNameSpaceURI.put("xmlns", "");
		htNameSpaceURI.put("xml", "");		
		
		int lastC = 0;
		int state = STATE_INIT;
		int oldState = state;
		byte[] e = cAutomaton[state];
		line = 1;
		offset = 0;
		prologFound = false;
		interruptParsing = false;
		error = false;

		if (mode == LOW_PARSING_MODE) {
			db.setCheckForCloseTag(false);
		} else
			db.setCheckForCloseTag(true);

		db.setFlatView(flatView);

		char[] streamBuffer = new char[8192];
		int nr = 0;
		
		char[] sbBuffer = new char[16];
		int sbBufferCount = 0;
		char[] sbEntity = new char[64];
		int sbEntityCount = 0;
		
		try {
			char c;

			if ( nr != -1 )
			do {
			
				try {

					for (int i = 0; i < nr; i++) {
						c = streamBuffer[i];

						if (interruptParsing)
							break;

						int d = cAutomatonDefaut[state];
						realTimeCurrentNS = false;

						if (c == ':' && (state == 3 || state == 5)) {
							currentNS = true;
							realTimeCurrentNS = true;
						}

						try {
							if ( ( c & HIGH_BYTE_MASK ) == 0 )
								state = e[c & 255];
						} catch (ArrayIndexOutOfBoundsException exc) {
						}

						if (state == 0)
							state = d;

						if ( state != oldState ) {
							if ( cBufferMode[ oldState ] ) {
								if ( oldState == 19 )
									// Particular case for ' character and " for
									// the attribute value
									oldState = 8;

								if ( sbBufferCount > 0 ) {

									if ( oldState != 13 || enabledComment ) {
										String s = new String( sbBuffer, 0, sbBufferCount );
										fireItemFound( db, oldState, s );
									}
									sbBufferCount = 0;

								} else {
									if ( oldState == 8 && attTmpName != null )
										fireItemFound( db, 8, "" );									
								}

							} else if (cTransitionPreservedChar[state]) {
								if (c == '&') {
									entityMode = true;
									sbEntity[sbEntityCount++] = c;
								} else {
									sbBuffer[sbBufferCount++] = c;
									if (sbBufferCount == sbBuffer.length) {
										// Oversize
										char[] newOne = new char[sbBufferCount * 2];
										System.arraycopy(sbBuffer, 0, newOne,
												0, sbBufferCount);
										sbBuffer = newOne;
									}
								}
							}
						}

						lastC = c;
						e = cAutomaton[state];

						if (cBufferMode[state]) {

							if (state == oldState || state == 5 || state == 10) {
								// Entity check

								if (c == '&' && state != 15 ) {
									entityMode = true;
									sbEntity[sbEntityCount++] = c;
								} else {
									if (!entityMode) {

										sbBuffer[sbBufferCount++] = c;

									} else {
										if (c == ' ')
											throw new RuntimeException(
													Messages.ERROR_ENTITY4);
										else {
											if (c == ';') { // End of entity
												sbEntity[sbEntityCount++] = (char) c;
												if (sbEntityCount == 2)
													throw new RuntimeException(
															Messages.ERROR_ENTITY5);
												if (sbEntity[1] == '#') {
													if (sbEntityCount == 3)
														throw new RuntimeException(
																Messages.ERROR_ENTITY5);

													// Unicode character
													String sTmp;
													int liv = 0;
													try {

														if (sbEntity[2] == 'x') {
															if (sbEntityCount == 4)
																throw new RuntimeException(
																		Messages.ERROR_ENTITY5);

															sTmp = new String(
																	sbEntity,
																	3,
																	sbEntityCount - 4);

															liv = hexaToChar(sTmp);
														} else {

															sTmp = new String(
																	sbEntity,
																	2,
																	sbEntityCount - 3);

															try {
																liv = Integer
																		.parseInt(sTmp);
															} catch (Throwable th) {
																throw new RuntimeException(
																		Messages.ERROR_ENTITY6
																				+ " "
																				+ sTmp);
															}

														}

														sbBuffer[sbBufferCount++] = (char) liv;
													} catch (NumberFormatException exc) {
														throw new RuntimeException(
																Messages.ERROR_ENTITY1
																		+ " "
																		+ new String(
																				sbEntity,
																				1,
																				sbEntityCount - 2));
													}
												} else {
													// Predefine entity
													String entityName = new String(
															sbEntity, 1,
															sbEntityCount - 2);
													if ("lt".equals(entityName)) {
														sbBuffer[sbBufferCount++] = '<';
													} else if ("gt"
															.equals(entityName)) {
														sbBuffer[sbBufferCount++] = '>';
													} else if ("quot"
															.equals(entityName)) {
														sbBuffer[sbBufferCount++] = '"';
													} else if ("apos"
															.equals(entityName)) {
														sbBuffer[sbBufferCount++] = '\'';
													} else if ("amp"
															.equals(entityName)) {
														sbBuffer[sbBufferCount++] = '&';
													} else {
														unknownEntityDetected(entityName);

														// Put it as normal
														// value
														if (sbBufferCount
																+ sbEntityCount >= sbBuffer.length) {
															// Increase it
															char[] newOne = new char[sbBufferCount * 2];
															System
																	.arraycopy(
																			sbBuffer,
																			0,
																			newOne,
																			0,
																			sbBufferCount);
															sbBuffer = newOne;
														}
														// Copy the entity
														// inside
														System.arraycopy(
																sbEntity, 0,
																sbBuffer,
																sbBufferCount,
																sbEntityCount);
														sbBufferCount += sbEntityCount;
													}
												}
												sbEntityCount = 0;
												entityMode = false;
											} else {
												sbEntity[sbEntityCount++] = (char) c;
											}
										}
									}
								}

								if (sbBufferCount == sbBuffer.length) {
									// Oversize
									char[] newOne = new char[sbBufferCount * 2];
									System.arraycopy(sbBuffer, 0, newOne, 0,
											sbBufferCount);
									sbBuffer = newOne;
								}
							}
						} else if ('/' == c && state != 21) {

							if (CDATABuffer != null && state != 15) {
								db.addTextNode(this,CDATABuffer.substring(6, CDATABuffer.length()));
								CDATABuffer = null;
							}
							
							// Single tag
							if ((state != 11) && (state != 13) && (state != 15)) {
								db.closeNode(this);
							}
						} else if ( state != 15 && CDATABuffer != null ) {

							if (CDATABuffer != null && state != 15) {
								try {
									db.addTextNode(this,CDATABuffer.substring(6, CDATABuffer.length()));
								} catch( StringIndexOutOfBoundsException exc ) {
									db.addTextNode(this,CDATABuffer.toString() );
								}
								CDATABuffer = null;
							}
														
						}

						if ( state == 2 && oldState != 2 ) {
							lineText = line;
						}

						oldState = state;

						// Cursor information
						col++;
						if (c == 10) {
							line++;
							col = 0;
						}

						offset++;

					}

				} catch (Throwable th) {
					notifyError(th);
					if (mode == CONTINUE_PARSING_MODE) {
						state = STATE_INIT;
					} else
						throw th;
				}
				
				nr = cInput.read( streamBuffer );
				
			} while ( nr != -1 );

			if (!interruptParsing) {
				if (!error && !db.isTerminated()) {
					try {
						line = ((ViewableNode) (db.getDocument().getRoot()))
								.getStartingLine();
					} catch (Throwable th) {
					}
					notifyError(new RuntimeException(Messages.ERROR_TAG1));
				}
			}

		} catch (IOException exc) {

		} catch (Throwable exc) {
			
			
			htNameSpaceURI = null;
			ParseException et = null;
			
			if ("true".equals(System.getProperty("fp.debug")))
				exc.printStackTrace();

			if (exc instanceof ParseException)
				et = (ParseException) exc;
			else
				et = new ParseException(Messages.ERROR2
						+ (cCurrentFile != null ? cCurrentFile : "") + " "
						+ Messages.LINE + " " + (line + 1));

			et.setCauseBy(exc.getMessage());
			et.setCaret(offset);
			et.setLine(line + 1);
			et.setCol(col);

			if (exc instanceof HandlerException) {
				((HandlerException) exc).getCause().printStackTrace();
			}

			db.dispose();

			throw et;
		} finally {

			if ( cInput != null ) {
				try { cInput.close(); } catch( IOException exc ) {}
			}
			cInput = null;

		}

		db.dispose();
		htNameSpaceURI = null;

		return db.getDocument();
	}

	private void notifyError( Throwable th ) {
		error = true;
		if (th.getMessage() != null)
			fireErrorFound(th.getMessage(), offset, line, col );
		else
			fireErrorFound(Messages.ERROR1, offset, line, col );
	}

	private String attTmpName;
	private String attTmpLocalName;

	private String lastTag = null;
	private String lws = null;
	private boolean prologState = false;
	private boolean enabledNameSpace = true;
	private HashMap htNameSpaceURI = null;

	/** Fired in low parsing mode for each error */
	protected void fireErrorFound(String message, int offset, int line, int column ) {
		if (errorSignal != null)
			errorSignal.parsingError(message, offset, line, column );
	}

	/**
	 * @param enabled
	 *            Enabled for nameSpace (true by default)
	 */
	public void setEnabledNameSpace(boolean enabled) {
		this.enabledNameSpace = enabled;
		if (!enabled) {
			cAutomaton[2][':'] = 2;
			cAutomaton[4][':'] = 4;
		} else {
			cAutomaton[2][':'] = 16;
			cAutomaton[4][':'] = 17;
		}
	}

	/** @return true if nameSpace are enabled */
	public boolean isEnabledNameSpace() {
		return enabledNameSpace;
	}
	
	private boolean enabledComment = false; 
	
	/** @param enable / Enabled for comment (false by default) */
	public void setEnabledComment( boolean enabled ) {
		this.enabledComment = enabled;
	}
	
	/** @return true if comment are managed */
	public boolean isEnabledComment() {
		return enabledComment;
	}

	private String CDATABuffer = null;

	protected void fireItemFound(DocumentBuilder db, int state, String item) throws ParseException {

		try {
//			if ("".equals(item))
//				return;

			if (CDATABuffer != null && state != 15) {
				db.addTextNode(this,CDATABuffer.substring(6, CDATABuffer.length()));
				CDATABuffer = null;
			}
			
			switch (state) {
			case 1: // WS
				lws = item;
				break;
			case 2: // TEXT
				if (ws) { // Preserved ws
					if (lws != null)
						item = lws + item;
				}
				db.addTextNode(this,item);
				lws = null;
				break;
			case 3:
				boolean prolog = false;
				// TAG
				if (item.startsWith("?xml")) {
					prologFound = true;
					prolog = true;
				}

				if (!prologFound && prolog) {
					if (!(item.startsWith("?xml")))
						throw new ParseException(Messages.ERROR_PROLOG);
				}

				if (!currentNS) {
					if (item.startsWith("/"))
						db.closeNode(this,null, item.substring(1));
					else {
						if (!prolog)
							db.openNode(this,null, null, item);
					}
				} else
					currentNS = false;
				lastTag = item;
				break;
			case 5:
				attTmpName = item;
				break;
			case 8:
				if (lastTag != null && !lastTag.startsWith("?")) {
					if (currentNS) {
						boolean reset = true;

						if (enabledNameSpace) {
							if ("xmlns".equals(attTmpName)) {
								reset = false;
								htNameSpaceURI.put(attTmpLocalName, item);
								db.setNameSpace(attTmpLocalName, item);
							}
						}

						if (reset) {
							db.setAttribute(attTmpName, (String) htNameSpaceURI
									.get(attTmpName), attTmpLocalName, item);
						}

					} else {
						if (enabledNameSpace && "xmlns".equals(attTmpName)) {
							db.setNameSpace(null, item);
						} else
							db.setAttribute(null, null, attTmpName, item);
					}
					currentNS = false;
				}
				break;
			case 10:
				if ("/".equals(item)) {
					db.closeNode(this);
				} else if (prologFound && !prologState) {
					prologState = true;
				}
				break;
			case 13: // COMMENT
				db.addCommentNode(this,item);
				break;
			case 15: // CDATA
				if (CDATABuffer == null) {
					CDATABuffer = item;
				} else
					CDATABuffer += "]" + item;
				break;
			case 17: // PREFIX ELEMENT

				if (lastTag.startsWith("/")) {
					db.closeNode(this,lastTag.substring(1), item);
				} else {
					db.openNode(this,lastTag, (String) htNameSpaceURI.get(lastTag),
							item);
				}
				break;

			case 18: // PREFIX ATTRIBUTE / => THE LOCAL NAME...
				attTmpLocalName = item;
				break;
			}

		} catch (DocumentBuilderException exc) {
			if ( "true".equals(System.getProperty( "fp.debug" ) ) )
				exc.printStackTrace();
			ParseException pe = new ParseException(exc.getMessage());
			throw pe;
		}
	}

	/**
	 * This method does nothing, this is just invokated while the parsing
	 * processing
	 */
	protected void unknownEntityDetected(String entity) {
	}

	/**
	 * @return true if the parsing process must sleep a little by calling
	 *         Thread.sleep every item parsed. This is needed for a Thread
	 *         usage. by default to <code>false</code>
	 */
	public boolean isBackgroundMode() {
		return backgroundMode;
	}

	/**
	 * @param backgroundMode
	 *            for Thread usage that needs to interrupt the parsing process
	 *            easily
	 */
	public void setBackgroundMode(boolean backgroundMode) {
		this.backgroundMode = backgroundMode;
	}

	ErrorParsingListener errorSignal;

	/** Interface for catching parsing error */
	public void setErrorSignal(ErrorParsingListener signal) {
		this.errorSignal = signal;
	}
	
	public static void main( String[] args ) throws Throwable {
		FPParser p = new FPParser();
		p.setFlatView(true);
		System.setProperty("fp.debug", "true" );
		for ( int i=0; i < 10;i++ ) {
			System.out.println( "Start parsing" );
			long a = System.currentTimeMillis();		
			p.parse(new FileReader("C:/travail/soft/japisoft-commonFramework/tst/big2.XML"));
			System.out.println( ( System.currentTimeMillis() - a ) + "ms" );
		}
	}
	
}
