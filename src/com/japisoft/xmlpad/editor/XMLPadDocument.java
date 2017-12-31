package com.japisoft.xmlpad.editor;

import javax.swing.event.*;
import javax.swing.text.*;
import javax.swing.tree.TreeModel;

import org.xml.sax.InputSource;

import com.japisoft.framework.collection.FastVector;
import com.japisoft.framework.xml.SchemaLocator;
import com.japisoft.framework.xml.parser.FPParser;
import com.japisoft.framework.xml.parser.document.Document;
import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.framework.xml.parser.tools.XMLToolkit;
import com.japisoft.xmlpad.SharedProperties;
import com.japisoft.xmlpad.XMLContainer;
import com.japisoft.xmlpad.XMLDocumentInfo;

import java.awt.Point;
import java.io.StringReader;
import java.util.*;

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
public class XMLPadDocument extends PlainDocument {
	private XMLEditor editor;

	/**
	 * Creates a new <code>DefaultXMLDocument</code> instance. */
	public XMLPadDocument(XMLEditor editor) {
		addDocumentListener(new XMLDocumentListener());
		this.editor = editor;
	}

	public void setXMLEditor( XMLEditor editor ) {
		this.editor = editor;
	}
	
	/** @return the editor using this document */
	public XMLEditor getCurrentEditor() {
		return editor.getXMLContainer().getEditor();
	}

	public void setEditor( XMLEditor editor ) {
		this.editor = editor;
	}
	
	/** @return the main container for this document */
	public XMLContainer getContainer() {
		return editor.getXMLContainer();
	}
	
	/** @return if a DTD/XDF/RNG schema is used */
	public boolean hasSchema() {
		if ( getContainer() == null )
			return false;
		return getContainer().hasSchema();
	}

	public String getSpellableDocument() throws Exception {

		FPParser p = new FPParser();
		p.setFlatView( true );
		p.setBackgroundMode( true );
		p.setParsingMode( FPParser.CONTINUE_PARSING_MODE );
		Document doc = p.parse(new StringReader(getText( 0, getLength())));
		FPNode root = ( FPNode )doc.getRoot();
		if ( root == null ) {
			throw new Exception( "Can't parse this document" );
		}
		StringBuffer sb = new StringBuffer();
		fillSpellableText( root, sb );
		return sb.toString();

	}

	private void fillSpellableText( FPNode node, StringBuffer res ) {
		if ( node.isText() ) {
			String content = node.getContent();
			int offset = node.getStartingOffset();
			while ( res.length() < offset ) {
				res.append( " " );
			}
			res.append( content );
		} else {
			for ( int i = 0; i < node.childCount(); i++ ) {
				fillSpellableText( ( FPNode )node.childAt( i ), res );
			}
		}
	}

	/** Fire an event that the XML document has not the same structure */
	protected void structureDamaged() {
		if ( !structureDamagedSupport )
			return;
		if ( listener != null )
			listener.notifyStructureChanged();
		if ( editor.getXMLContainer().getTreeListeners() != null )
			editor.getXMLContainer().getTreeListeners()
					.notifyStructureChanged();
	}
	
	private StructureDamagedListener listener;

	/** Listener for XML tree changes */
	public void setStructureDamagedListener(StructureDamagedListener listener) {
		this.listener = listener;
	}

	/** @return true if the line contains an opening tag declaration */
	public boolean hasOpeningTag( int line ) {
		Element e = getDefaultRootElement().getElement( line );
		int startingOffset = e.getStartOffset();
		int endingOffset = e.getEndOffset();
		boolean found = false;		
		try {
			String text = getText( startingOffset, endingOffset - startingOffset );
			for ( int i = 0; i < text.length(); i++ ) {
				if ( text.charAt( i ) == '<' ) {
					found = true;
				}
				if ( found ) {
					if ( text.charAt( i ) == '!' ||
							text.charAt( i ) == '/' ||
								text.charAt( i ) == '?' ) {
						found = false;
					} else
						if ( text.charAt( i ) == '>' ) {
							break;
						}
				}
			}
		} catch( BadLocationException exc ) {
		}
		return found;
	}

	/** Editing in a Comment section ? */
	public boolean isInsideComment(int offset) {
		int line = getDefaultRootElement().getElementIndex( offset );
		Element e = getDefaultRootElement().getElement( line );
		int start = e.getStartOffset();
		int stop = e.getEndOffset();
		int cursor = ( offset - start );
		try {
			String content = getText(start, stop - start);
			int i = content.indexOf("<!--");
			int j = content.indexOf("-->");
			if (j > -1)
				j += 3;
			if (cursor > i && cursor < j)
				return true;
			else {

				if (cursor > i && j == -1 && i != -1)
					return true;
				else if (cursor < j && i == -1 && j != -1)
					return true;
				else if ((i == j) && (i == -1)) {
					// Check using the view
					if ( editor.getEditorKit() instanceof XMLEditorKit ) {
						XMLEditorKit kit = (XMLEditorKit) editor.getEditorKit();
						if (kit.lastView instanceof XMLTextView) {
							XMLTextView xv = (XMLTextView) kit.lastView;
							if ( xv == null || xv.lp == null )
								return false;
							return (xv.lp.getLastType(line) == LineElement.COMMENT);
						}
					}
				}
			}

		} catch (BadLocationException exc) {
		}
		return false;
	}

	/** Editing in a CDATA section ? */
	public boolean isInsideCDATA(int offset) {
		int line = getDefaultRootElement().getElementIndex(offset);
		Element e = getDefaultRootElement().getElement(line);
		int start = e.getStartOffset();
		int stop = e.getEndOffset();
		int cursor = offset - start;
		try {
			String content = getText(start, stop - start);
			int i = content.indexOf("<![CDATA[");
			int j = content.indexOf("]]>");
			if (j > -1)
				j += 3;
			if (cursor > i && cursor < j)
				return true;
			else {

				if (cursor > i && j == -1 && i != -1)
					return true;
				else if (cursor < j && i == -1 && j != -1)
					return true;
				else if ((i == j) && (i == -1)) {
					// Check using the view
					if ( editor.getEditorKit() instanceof XMLEditorKit ) {
						XMLEditorKit kit = (XMLEditorKit) editor.getEditorKit();
						if (kit.lastView instanceof XMLView) {
							XMLView xv = (XMLView) kit.lastView;
							return (xv.lp.getLastType(line) == LineElement.CDATA);
						}
					}
				}
			}

		} catch (BadLocationException exc) {
		}
		return false;
	}

	/** Find the location of the next tag */
	public int nextTag(int from) {
		try {
			String t = getText(from, getLength());
			byte[] data = t.getBytes();
			for (int i = 0; i < data.length; i++) {
				if ((data[i] == '<') && (i + 1 < data.length)
						&& !(data[i + 1] == '!' || data[i + 1] == '?'))
					return i;
			}
			return data.length - 1;
		} catch (BadLocationException exc) {
		}
		return from;
	}

	
	/** Parse the line at this offset and return the part */
	public List<LineElement> parseLine( int offset ) throws BadLocationException {
		int lineIndex = getDefaultRootElement().getElementIndex( offset );
		Element lineElement = getDefaultRootElement().getElement( lineIndex );
		int start = lineElement.getStartOffset();
		int end = lineElement.getEndOffset();
		String lineContent = getText( start, end - start );
		LineParsing lp = new LineParsing();
		com.japisoft.framework.collection.FastVector v = lp.parse( 
			new Segment( 
				lineContent.toCharArray(), 
				0, 
				lineContent.length() 
			),
			0);
		ArrayList<LineElement> r = new ArrayList<LineElement>();
		int currentPosition = start;
		for ( int i = 0; i < v.size(); i++ ) {
			LineElement le = ( LineElement )v.get( i );
			le.offset = currentPosition;
			r.add( le );
			if ( le.content != null )
				currentPosition += le.content.length();
		}
		return r;
	}
	
	/** Extract a word location */
	public int[] getWordDelimitersAt( int from ) {
		Element e = getDefaultRootElement();
		int index = e.getElementIndex(from);
		e = e.getElement(index);
		try {
			String line = getText(e.getStartOffset(), e.getEndOffset()
					- e.getStartOffset() + 1);
			int s = from - e.getStartOffset();
			int start = 0;
			int stop = 0;
			int lastStart = -1;
			int lastStop = -1;

			for (start = s; start >= 0; start--) {
				if (Character.isSpaceChar(line.charAt(start))) {
					if (lastStart == -1)
						lastStart = start + 1;
				}
				if (line.charAt(start) == '\'' || line.charAt(start) == '"') {
					start++;
					break;
				}
			}

			if (start == -1)
				start = lastStart;

			for (stop = s; stop < line.length(); stop++) {
				if (Character.isSpaceChar(line.charAt(stop))) {
					if (lastStop == -1)
						lastStop = stop - 1;
				}
				if (line.charAt(stop) == '\'' || line.charAt(stop) == '"') {
					stop--;
					break;
				}
			}

			if (stop == line.length())
				stop = lastStop;

			if (stop > start && start != -1) {
				return new int[] { from + start, from + stop };
			}
			return null;

		} catch (BadLocationException exc) {
			return null;
		}		
	}
	
	/** Extract a word at this location */
	public String getWordAt(int from) {
		Element e = getDefaultRootElement();
		int index = e.getElementIndex(from);
		e = e.getElement(index);
		try {
			String line = getText(e.getStartOffset(), e.getEndOffset()
					- e.getStartOffset() + 1);
			int s = from - e.getStartOffset();
			int start = 0;
			int stop = 0;
			int lastStart = -1;
			int lastStop = -1;

			for (start = s; start >= 0; start--) {
				if (Character.isSpaceChar(line.charAt(start))) {
					if (lastStart == -1)
						lastStart = start + 1;
				}
				if (line.charAt(start) == '\'' || line.charAt(start) == '"') {
					start++;
					break;
				}
			}

			if (start == -1)
				start = lastStart;

			for (stop = s; stop < line.length(); stop++) {
				if (Character.isSpaceChar(line.charAt(stop))) {
					if (lastStop == -1)
						lastStop = stop - 1;
				}
				if (line.charAt(stop) == '\'' || line.charAt(stop) == '"') {
					stop--;
					break;
				}
			}

			if (stop == line.length())
				stop = lastStop;

			if (stop > start && start != -1) {
				return line.substring(start, stop + 1);
			}
			return null;

		} catch (BadLocationException exc) {
			return null;
		}
	}
	
	/** @return the previous attribute name at this location */
	public String getForwardAttributeName( 
			XMLPadDocument document, 
			int offset ) {
		try {
			StringBuffer sbRes = null;
			for ( int c = offset; c > 0; c-- ) {
				char ch = 
					document.getText( c, 1 ).charAt( 0 );
				if ( ch == '=' ) {
					sbRes = 
						new StringBuffer();
				} else
					if ( ch == '<' )
						return null;
					else
						if ( sbRes != null ) {
							if ( ch == ' ' || 
									ch == '\t' || 
										ch == '\n' )
								break;
							sbRes.insert( 0, ch );
						}
			}
			if ( sbRes != null )
				return sbRes.toString();
		} catch (BadLocationException e) {}
		return null;
	}	
	
	public FPNode getGreatestNodeAt( int row ) {
		Element e = getDefaultRootElement().getElement( row );
		int startOffset = e.getStartOffset();
		int endOffset = e.getEndOffset();
		FPNode n = getXMLPath( endOffset );
		if ( n != null ) {
			while ( n.getFPParent() != null ) {
				if( n.getFPParent().getStartingOffset() >= startOffset ) {
					n = n.getFPParent();
				} else
					break;
			}
			return n;
		}

		return null;
	}

	/** @return the XMLPath from the caret location */
	public FPNode getXMLPath(int location) {
		if (getContainer().getTree() != null) {
			Object root = ((TreeModel) getContainer().getTree().getModel())
					.getRoot();
			if (root instanceof FPNode) {
				return XMLToolkit.getNodeForOffset(((FPNode) root)
						.getDocument(), location);
			}
		} else {
			if (getContainer().getRootNode() != null) {
				return XMLToolkit.getNodeForOffset(getContainer().getRootNode()
						.getDocument(), location);
			}
		}
		return null;
	}

	private boolean structureDamagedSupport = true;

	/**
	 * Support notification for an XML structure tree change like adding or
	 * removing a tag */
	public void enableStructureDamagedSupport(boolean structure) {
		this.structureDamagedSupport = structure;
	}

	/**
	 * Support notification for an XML structure tree change like adding or
	 * removing a tag */
	public boolean isEnableStructureDamagedSupport() {
		return structureDamagedSupport;
	}

	private boolean autoClose = true;

	/** Support for automatically closing the current inserted tag */
	public void setAutoCloseTag(boolean autoClose) {
		this.autoClose = autoClose;
	}

	/** by default <code>true</code> */
	public boolean isAutoCloseTag() {
		return autoClose;
	}
		
	public boolean isAutoCloseQuote() {
		return getCurrentEditor().getXMLContainer().hasAutoQuoteClosing();
	}

	private boolean autoIndent = false;
	
	public void setAutoIndent( boolean autoIndent ) {
		this.autoIndent = autoIndent;
	}
	
	public boolean isAutoIndent() {
		return autoIndent;
	}
	
	private boolean syntaxPopup = true;

	/** Enable a popup while entering < or & */
	public void setSyntaxPopup(boolean popup) {
		this.syntaxPopup = popup;
	}

	/** Enable a popup for < and &, by default true */
	public boolean isSyntaxPopup() {
		return syntaxPopup;
	}

	/** @return the starting and the end offset inside an attribute value from the offset parameter */
	public int[] getAttributeValueLocation( int offset ) {	
		// Search for the next " or '
		try {
			int starti = 0;
			int stopi = 0;
			int startType = 0;
			int endType = 0;
			int i = ( offset );
			// Forward
			while ( i < getLength() ) {
				String tmp = getText( i, 1 );
				if ( "'".equals( tmp ) || 
						"\"".equals( tmp ) ) {
					String tmp2 = getText( ( i + 1 ), 1 );
					if ( " ".equals( tmp2 ) ||
							"\t".equals( tmp2 ) ||
								">".equals( tmp2 ) ||
									"/".equals( tmp2 ) ) {
						stopi = i;
						endType = tmp.charAt( 0 );
						break;
					}
				} else
				if ( ">".equals( tmp ) ) 
					break;
				else
				if ( "=".equals ( tmp ) ) {
					String tmp2 = getText( i + 1, 1 );
					if ( "'".equals( tmp2 ) || 
							"\"".equals( tmp2 ) )
						break;
				}
				i++;
			}
			// Backward
			i = ( offset - 1);
			while ( i > 0 ) {
				String tmp = getText( i, 1 );
				if ( "'".equals( tmp ) || 
						"\"".equals( tmp ) ) {
					String tmp2 = getText( ( i - 1 ), 1 );
					if ( "=".equals( tmp2 ) ) {
						starti = i;
						startType = tmp.charAt( 0 );
						break;
					}
				} else
					if ( "<".equals( tmp ) ) 
						break;
				i--;
			}
			
			if ( ( starti > 0 ) && 
					( stopi > 0 ) ) {
				return new int[] { starti, stopi, startType, endType };
			}

		} catch ( BadLocationException e ) {
		}
		return null;
	}

	/** @return <code>true</code> if this offset is before the root node */
	public boolean isInProlog( int offset ) {
		if ( getContainer().getRootNode() == null )
			return true;
		if ( getContainer().getRootNode().getStartingOffset() >= offset )
			return true;
		return false;
	}

	public boolean isInsideAttributeValue( int offset ) {
		return getAttributeValueLocation( offset ) != null;
	}
	
	public boolean isInsideQuote( int offset ) {
		
		try {
			List<LineElement> vector = parseLine( offset );			
			for ( LineElement le : vector ) {
				if ( le.offset >= offset ) {
					System.out.println( "FOUND " + vector + " => " + le );
					return ( le.type == LineElement.LITERAL || le.type == LineElement.LITERAL2 ); 
				}
			}
		} catch( BadLocationException ble ) {
		}

		return false;
	}
	
	/** @return <code>true</code> if the offset is inside a tag and not in an attribute value */
	public boolean isInsideTagExceptAttributeValue(int offset) {
		return isInsideTag(offset, true, true);
	}
	
	public boolean isInsideTag( int offset ) {
		return isInsideTag( offset, false, false );
	}

	public boolean isInsideTag(
			int offset, 
			boolean exceptAttributeValue,
			boolean exceptEndTag ) {
		return isInsideTag(offset, exceptAttributeValue, exceptEndTag, true);
	}
	
	public String getInsideTagName( int offset ) {
		try {
		
			Element e = getDefaultRootElement();
			int index = e.getElementIndex(offset);
			Element l = e.getElement(index);
			int start = l.getStartOffset();
			int end = l.getEndOffset();

			StringBuffer sb = new StringBuffer();
			boolean good = false;
			
			for ( int i = start; i < end; i-- ) {
				char c = getText( i, 1 ).charAt( 0 );
				if ( c == '<' ) {
					good = offset > i;
				} else {
					if ( good ) {
						if ( Character.isWhitespace( c ) || c == '/' || c == '>' ) {
							good = offset < i;
							break;
						} else {
							sb.append( c );
						}
					}
				}
			}

			if ( good )
				return sb.toString();
			else
				return null;
			
		} catch( BadLocationException ble ) {
			return null;
		}
	}

	/** @return true if the offset is inside a tag */
	boolean isInsideTag(int offset, boolean exceptAttributeValue,
			boolean exceptEndTag, boolean recurse) {
		int index = 0;
		Element e = null;

		if ( exceptAttributeValue )
			if ( isInsideAttributeValue( offset ) )
				return false;
		
		try {
			e = getDefaultRootElement();
			index = e.getElementIndex(offset);
			Element l = e.getElement(index);
			int start = l.getStartOffset();
			int end = l.getEndOffset();

			if (recurse)
				start = Math.max(0, start - 300);

			String line = getText(start, end - start);

			int d = (offset - start - 1);
			int quotePassed = 0;
			char p = ' ';
			char c = ' ';
			for (int j = d; j >= 0; j--) {
				p = c;
				c = line.charAt(j);
				if (c == '<') {
					if (p == '?')
						return false;
					else if (p == '!')
						return false;
					else if (p == '/' && exceptEndTag)
						return false;
/*					return !exceptAttributeValue || 
								( exceptAttributeValue && ( quotePassed % 2 == 0) ) || 
									( quotePassed == 0 ); */
					return true;
				} else if (c == '>')
					return false;
				else if (c == '\'' || c == '"') {
					quotePassed++;
				}
			}
		} catch (BadLocationException exc) {
		} catch (Throwable th) {
		}

		return false;
	}
	
	/**
	 * The starting and stopping comment position or null if no comment is found
	 * @param offset Current document location
	 * @return start and stop location */
	public Integer[] getCommentDelimiters(int offset) {
		try {
			String txt = getText(0, getLength());
			byte[] array = txt.getBytes();
			if (offset >= array.length)
				return null;
			for (int i = offset - 2; i >= 0; i--) {
				if (array[i] == '<') {
					if (array[i + 1] == '!' && array[i + 2] == '-') {
						for (int j = offset; j + 2 <= array.length; j++) {
							if (array[j] == '-' && array[j + 1] == '-'
									&& array[j + 2] == '>') {
								return new Integer[] { new Integer(i),
										new Integer(j + 2) };
							}
						}
					} //else	// try all
					 //	break;
				}
			}
		} catch (BadLocationException exc) {
		}
		return null;
	}

	// Temporary buffer while asynchronous completion
	private StringBuffer bufferHelper = null;

	private boolean completionMode = false;

	/** For inner usage */
	public void resetCompletionMode(boolean enabled) {
		if (!enabled) {
			bufferHelper = null;
			completionMode = false;
		} else {
			editor.setEnabledXPathLocation(false);
			completionMode = true;
			bufferHelper = new StringBuffer();
		}
	}

	public StringBuffer getCompletionBuffer() {
		return bufferHelper;
	}

	public boolean manageCompletion(boolean insertBefore, int offset, String str) {

		return editor.getXMLContainer().getHelperManager().activateContentAssistant(
				getContainer().getCurrentElementNode(),
				null, 
				insertBefore, 
				offset, 
				str );
		
	}

	public boolean manageCompletion(FPNode currentElementNode, boolean insertBefore, int offset, String str) {

		return editor.getXMLContainer().getHelperManager().activateContentAssistant(
				currentElementNode,
				null, 
				insertBefore, 
				offset, 
				str );
		
	}

	/** Insert a string at this offset ignoring the tree synchronization */
	public void insertStringWithoutStructureDamaged(int offset, String str,
			AttributeSet a) {
		try {
			rawInsertString(offset, str, a);
		} catch (BadLocationException exc) {
		}
	}

	public void rawInsertString( int offset, String str, AttributeSet a ) throws BadLocationException {
		int currentLine = getDefaultRootElement().getElementIndex( offset );
		XMLEditor editor = getCurrentEditor();
		int newLineNumber = 0;
		if ( str == null )
			return;
		for ( int i = 0; i < str.length(); i++ ) {
			if ( str.charAt( i ) == '\n' ) {
				newLineNumber++;
			}
		}
		editor.checkClosedElement( currentLine, newLineNumber, true );
		super.insertString( offset, str, a );
	}

	/** Insert a string at this offset ignore syntax popup helper */
	public void insertStringWithoutHelper( int offset, String str, AttributeSet a ) {
		try {
			rawInsertString(offset, str, a);
			structureDamaged();
			getCurrentEditor().getXMLContainer().setModifiedState(true);
		} catch (BadLocationException exc) {
		}
	}

	public void insertStringWithoutHelper( int offset, String str, AttributeSet a, boolean caretMoved ) {
		insertStringWithoutHelper( offset, str, a );
		if ( caretMoved ) {
			getCurrentEditor().setCaretPositionWithoutNotification( offset + str.length() );
		}
	}

	boolean completionActivation = false;
	
	/** Insert a string at this offset showing if needed a syntax popup helper */
	public void insertString( int offset, String str, AttributeSet a )
			throws BadLocationException {

		if (!getCurrentEditor().getXMLContainer().isEditableDocumentMode())
			return;

		if (getCurrentEditor().getXMLContainer().getDocumentIntegrity()
				.isProtectTag()) {
			if (isInsideTag(offset, false, false))
				return;
		}

		if ( completionMode ) {
			if ( bufferHelper == null )
				bufferHelper = new StringBuffer();
			bufferHelper.append(str);
		} else {
			if ( syntaxPopup ) {				
				if ( manageCompletion(false, offset, str) ) {					
					completionActivation = true;
					return;
				}
			}
		}

		if ( isAutoIndent() ) {
			if ( "<".equals( str ) ) {
				if ( tagOnLine( offset ) ) {
					rawInsertString(offset, "\n", a );
					offset++;
				}
				int indent = manageAutoIndent( offset );
				int currentIndent = getIndentAt( offset );
				String indentSeq = "";
				for ( int i = currentIndent; i < indent; i++ ) {
					indentSeq += "\t";
				}
				str = indentSeq + str;
			}
		}

		rawInsertString( offset, str, a );

		boolean damaged = false;

		if ( str.endsWith( "\n" ) || 
				getContainer().isRealTimeTreeOnTextChange() )
			damaged = true;

		if ("\n".equals(str)) {
			String indent = getIndentAtOffset(offset);
			if (indent != null)
				rawInsertString(offset + 1, indent, a);
		}

		
		if ( isAutoCloseTag() ) {
			if ( ">".equals( str ) ) { // User closes it				
				damaged = manageAutoClose( offset, a );
			}
		}

		if ( isAutoCloseQuote() ) {
			if ( "\"".equals( str ) || "'".equals( str ) ) {
				if ( isInsideQuote( offset ) ) {
					rawInsertString( offset, str, a );
					getCurrentEditor().setCaretPosition( offset + 1 );
				}
			}
		}
		
		String newTagName = getInsideTagName( offset );
		if ( newTagName != null ) {
			
			System.out.println( "NEW TAG NAME = " + newTagName );
			
			// Search for closing tag
			FPNode node = editor.getXMLContainer().getCurrentElementNode();
			if ( node != null ) {
				int endLine = node.getStoppingLine();
				
			}			
		}

		if ( damaged ) {
			if ( !"<".equals( str ) )
				structureDamaged();
		}

		editor.getXMLContainer().setModifiedState( true );
	}

	private int manageAutoIndent( int offset ) throws BadLocationException {
		int indent = 0;
		for ( int i = offset - 1; i >= 0; i-- ) {
			char c = getText(i, 1).charAt(0);
			if ( c == '<' ) {
				if ( getText( i + 1, 1 ).charAt( 0 ) == '/' ) {	// Closing tag
					indent = getIndentAt( i );
					break;
				} else {
					indent = getIndentAt( i ) + 1;
					break;
				}
			} else
			if ( c == '>' ) {
				if  ( getText( i + 1, 1 ).charAt( 0 ) == '>' ) {	// Closing tag
					indent = getIndentAt( i );
					break;
				}
			}
		}
		return indent;
	}
	
	private int getIndentAt( int offset ) throws BadLocationException {
		int index = getDefaultRootElement().getElementIndex( offset );
		Element e = getDefaultRootElement().getElement( index );
		int indent = 0;
		for ( int i = e.getStartOffset(); i <= e.getEndOffset(); i++ ) {
			char c = getText( i, 1).charAt( 0 );
			if ( c == '\t' )
				indent++;
			else
				break;
		}
		return indent;
	}
	
	private boolean tagOnLine( int offset ) throws BadLocationException {
		int index = getDefaultRootElement().getElementIndex( offset );
		Element e = getDefaultRootElement().getElement( index );
		for ( int i = offset - 1; i >= e.getStartOffset(); i-- ) {
			char c = getText( i, 1).charAt( 0 );
			if ( c == '>' || c == '<' )
				return true;
		}
		return false;
	}
	
	private boolean isLegalAutoClose( String tagName ) {
		XMLDocumentInfo info = getContainer().getDocumentInfo();
		return info.isLegalAutoClose( tagName );
	}

	private boolean manageAutoClose( int offset, AttributeSet a ) throws BadLocationException {
		boolean damaged = false;
		// Retreive the tagName
		StringBuffer sb = new StringBuffer();
		for (int i = (offset - 1); i >= 0; i--) {
			char c = getText(i, 1).charAt(0);

			if (c == '/' && i == (offset - 1)) // Closing tag
				break;

			if (c == '>') // Abnormal case
				break;

			if ( c == ' ' || 
					c == '\t' || 
						c == '\n' ) {
				sb = new StringBuffer();
			} else {
				if ( c == '<' ) {
					char c2 = getText( i + 1, 1 ).charAt( 0 );
					if ( c2 == '!' || c2 == '?' || c2 == '/' )
						break;
					String tagName = sb.toString();
					if ( isLegalAutoClose( tagName ) ) {
					
						if (tagName.length() > 0 && !hasNextClosingTag( offset, tagName ) ) {
							rawInsertString((offset + 1),
									getClosingTagPart(
											getIndentAtOffset(offset),
											tagName), a);
	
							damaged = true;
							forceLocation = true;
							getContainer().getEditor().setCaretPosition(
									offset + 1 );
						}
						
					}
					break;
				} else
					sb.insert(0, c);
			}
		}
		return damaged;
	}

	/** @return the opening tag part from this offset inside an opening tag part with the namespace prefix */
	public String getPreviousOpeningTagInsideATagPartWithoutPrefix( int offset ) throws BadLocationException {
		String s = getPreviousOpeningTagInsideATagPart( offset );
		if ( s != null ) {
			int i = s.indexOf( ":" );
			if ( i > -1 )
				return s.substring( i + 1 );
			return s;
			
		} else
			return null;
	}

	/* ** @return the opening tag part inside an opening tag part */
	public String getPreviousOpeningTagInsideATagPart( int offset ) throws BadLocationException {
		StringBuffer sb = null;
		for ( int i = offset; i >= 0; i-- ) {
			char c = getText( i, 1 ).charAt( 0 );
			if ( c == ' ' || 
					c == '\t' || 
						c == '\n' ) {
				sb = new StringBuffer();
			} else
			if ( c == '<' )
				return sb.toString();
			else
				if ( sb != null )
					sb.insert( 0, c );
		}
		if ( sb == null )
			return null;
		return sb.toString();
	}

	/** @return the opening tag part from this offset with the namespace prefix */
	public String getPreviousOpeningTag( int offset ) throws BadLocationException {
		// Search for opening tag before
		StringBuffer sb = null;
		for ( int i = offset; i >= 0; i-- ) {
			char c = getText( i, 1 ).charAt( 0 );
			if ( c == '>' ) {
				sb = new StringBuffer();
			} else
			if ( c == '<' ) {
				if ( sb == null )	// ?
					return null;
				String tag = sb.toString();
				if ( tag.startsWith( "/" ) || 
						tag.endsWith( "/" ) )
					return null;
				return tag;
			} else
			if ( sb != null ) {
				if ( c == ' ' || 
						c == '\t' || 
							c == '\n' ) {
					if ( sb.toString().endsWith( "/" ) )
						return null;
					sb  = new StringBuffer();
				} else
					sb.insert( 0, c );
			}
		}
		return null;
	}

	// Check for closing tag part from this offset
	private boolean hasNextClosingTag( int offset, String tagname ) throws BadLocationException {
		StringBuffer sb = null;
		for ( int i = offset; 
					i < getLength(); 
						i++ ) {
			char c = getText( i, 1 ).charAt( 0 );
			if ( c == '<' ) {
				if ( sb != null )
					return false;
				sb = new StringBuffer();
			} else
			if ( c == '>' ) {
				if ( sb != null && 
						sb.toString().equals( "/" + tagname ) )
					return true;
			} else
			if ( sb != null )
				sb.append( c );
		}
		return false;
	}

	private boolean forceLocation = false;

	/** For inner usage only. Don't call it */
	public boolean forceLocation() {
		boolean _ = forceLocation;
		forceLocation = false;
		return _;
	}

	/** Search and parse an XML on the current document schema */
	public boolean parseSchema() {
		String schemaKey = "http://www.w3.org/2001/XMLSchema-instance";
		String schemaLocationKey = "schemaLocation";
		String schemaLocationKey2 = "noNamespaceSchemaLocation";

		try {
			for (int i = 0; i < getLength(); i++) {
				// Search for the first tag
				if ("<".equals(getText(i, 1))) {
					if (!(getText(i + 1, 1).equals("!") || getText(i + 1, 1)
							.equals("?"))) {
						StringBuffer sb = new StringBuffer();
						String _;
						String tagName = null;
						for (int j = (i + 1); j < getLength(); j++) {
							_ = getText(j, 1);
							if (tagName == null
									&& (" ".equals(_) || "\t".equals(_) || "\n"
											.equals(_))) {
								tagName = sb.toString();
							}
							sb.append(_);
							if (">".equals(_))
								break;
						}
						int si = sb.indexOf(schemaKey);
						if (si > -1) {
							String currentKey = schemaLocationKey;
							int so = sb.indexOf(currentKey);
							if (so == -1) {
								so = sb
										.indexOf(currentKey = schemaLocationKey2);
							}
							if (so > -1) {
								for (int k = so + currentKey.length() + 1; k < sb
										.length(); k++) {
									if (sb.charAt(k) == '"'
											|| sb.charAt(k) == '\'') {
										StringBuffer sb2 = new StringBuffer();
										for (int l = k + 1; l < sb.length(); l++) {
											if (sb.charAt(l) == '"'
													|| sb.charAt(l) == '\'')
												break;
											sb2.append(sb.charAt(l));
										}

										if (sb2.length() > 0) {

											String location = sb2.toString();
											ArrayList vnamespaces = new ArrayList();
											ArrayList vlocations = new ArrayList();

											String[] namespaces = new String[] { null };
											String[] locations = new String[] { location };

											if (currentKey == schemaLocationKey) { // Schema
																				   // location

												StringTokenizer st = new StringTokenizer(
														location, " \t\n\r");

												int counter = 0;

												while (st.hasMoreTokens()) {
													if (counter % 2 == 0) {
														// Namespace
														vnamespaces.add(st
																.nextToken());
													} else {
														// Location
														vlocations.add(st
																.nextToken());
													}
													counter++;
												}

												namespaces = new String[vnamespaces
														.size()];
												locations = new String[vlocations
														.size()];
												for (int _i = 0; _i < vnamespaces
														.size(); _i++)
													namespaces[_i] = (String) vnamespaces
															.get(_i);
												for (int _i = 0; _i < vlocations
														.size(); _i++)
													locations[_i] = (String) vlocations
															.get(_i);

											}

											int lineLocation = getDefaultRootElement()
													.getElementIndex(
															so
																	+ currentKey
																			.length()
																	+ 1);

											getCurrentEditor()
													.getXMLContainer()
													.getSchemaAccessibility()
													.setSchema(tagName,
															namespaces,
															locations,
															lineLocation);

											return true;
										}
									}
								}
							}
						}
						break;
					}
				}
			}
		} catch (BadLocationException exc) {
		}
		getCurrentEditor().getXMLContainer().getSchemaAccessibility()
				.setSchema(null, null, null, -1);
		return false;
	}

	/** Search and parse document DTD on the current document */
	public boolean parseDTD() {
		boolean foundDTD = false;
		int pi = 0, pj = 0;

		try {

			String lastWord = null; // For local DTD only !
			StringBuffer sbWord = null;

			// Parse DTD header
			all: for (int i = 0; i < getLength(); i++) {
				if ("<".equals(getText(i, 1))) {
					pi = i;
					boolean decl = "!".equals(getText(i + 1, 1));
					if (decl && !("-".equals(getText(i + 2, 1)))) {
						all2: for (int j = (i + 1); j < getLength(); j++) {
							String _ = getText(j, 1);

							if (!Character.isWhitespace(_.charAt(0))) {
								if (sbWord == null)
									sbWord = new StringBuffer();
								sbWord.append(_);
							} else if (sbWord != null) {
								lastWord = sbWord.toString();
								sbWord = null;
							}

							if (">".equals(_)) {
								pj = j;
								parseDTDLine(i + 2, j - 1);
								foundDTD = true;
								break all;
							} else if ("[".equals(_)) {
								// Search for ']'
								all3: for (int k = (j + 1); k < getLength(); k++) {
									if ("]".equals(getText(k, 1))) {
										// Local DTD ?
										String localDTDContent = getText(j + 1,
												k - j - 1);

										if (localDTDContent
												.indexOf("<!ELEMENT") > -1) {

											try {

												// LOCAL DTD

												getCurrentEditor()
														.getXMLContainer()
														.getSchemaAccessibility()
														.setDTD(
																lastWord,
																new SchemaLocator(
																		new StringReader(
																				localDTDContent)));

											} catch (Exception exc) {
												System.err
														.println("Unknown ERROR : "
																+ exc
																		.getMessage());
											}

											return true;
										} else {
											
											parseDTDLine(i + 2, j - 1);
											foundDTD = true;
											break all;
											
											// break all3;
										}
									}
								}
							}
						}
					} else if (!decl) {
						if (!("?".equals(getText(i + 1, 1)))) // First tag
							break;
					}
				}
			}

		} catch (BadLocationException exc) {
		}

		lastDTDLocation = null;

		if (!foundDTD)
			getCurrentEditor().getXMLContainer().getSchemaAccessibility()
					.setDTD((String) null, (String) null, -1);
		else
			lastDTDLocation = new Point(pi, pj);

		return foundDTD;
	}

	Point lastDTDLocation = null;

	/** @return the last DTD declaration location */
	public Point getLastDTDLocation() {
		return lastDTDLocation;
	}

	private void parseDTDLine(int from, int to) {
		try {
			String s = getText(from, to - from + 1);
			StringTokenizer st = new StringTokenizer(s, " \t\n\r");
			String root = null;
			String dtd = null;
			int dtdLocation = 0;

			if (st.hasMoreTokens()) {
				String a = st.nextToken();
				if ("DOCTYPE".equals(a)) {
					if (st.hasMoreTokens()) {
						root = st.nextToken();
						if (st.hasMoreTokens()) {
							a = st.nextToken();
							if ("SYSTEM".equals(a)) {
								if (st.hasMoreTokens()) {
									StringBuffer res = null;
									boolean first = false;
									while (st.hasMoreTokens()) {
										a = st.nextToken();
										if ((a.startsWith("\"")
												|| a.endsWith("\"")
												|| a.startsWith("'") || a
												.endsWith("'"))) {

											if (res == null)
												res = new StringBuffer();
											else
												res.append(" ");

											if (a.length() > 2) {
												if (a.charAt(0) == '"'
														|| a.charAt(0) == '\'')
													a = a.substring(1);
												if (a.charAt(a.length() - 1) == '"'
														|| a
																.charAt(a
																		.length() - 1) == '\'') {
													a = a.substring(0, a
															.length() - 1);
												}
												res.append(a);
											}

											if (first)
												break;
											first = true;

										} else {
											if (res == null)
												res = new StringBuffer();
											else
												res.append(" ");
											res.append(a);
										}
									}
									if (res != null)
										dtd = res.toString();
								}
							} else if ("PUBLIC".equals(a)) {

								StringBuffer sbPublicId = new StringBuffer();
								boolean pstart = false;
								while (st.hasMoreTokens()) {
									String token = st.nextToken();
									sbPublicId.append(token);
									if (token.startsWith("\"")
											|| token.startsWith("'"))
										pstart = true;
									if (token.endsWith("\"")
											|| token.endsWith("'")) {
										if (pstart)
											break;
									}
									if (pstart)
										sbPublicId.append(" ");
								}

								if (pstart) {
									sbPublicId.deleteCharAt(0);
									sbPublicId
											.deleteCharAt(sbPublicId.length() - 1);
									String publicId = sbPublicId.toString();
									if (SharedProperties.DEFAULT_ENTITY_RESOLVER != null) {
										try {
											InputSource source = SharedProperties.DEFAULT_ENTITY_RESOLVER
													.resolveEntity(publicId,
															null);
											if (source != null) {
												SchemaLocator locator = null;
												if (source.getCharacterStream() != null)
													locator = new SchemaLocator(
															source
																	.getCharacterStream());
												else if (source.getByteStream() != null)
													locator = new SchemaLocator(
															source
																	.getByteStream());
												else if (source.getSystemId() != null)
													locator = new SchemaLocator(
															source
																	.getSystemId());

												if (locator != null) {
													getCurrentEditor()
															.getXMLContainer()
															.getSchemaAccessibility()
															.setDTD(root,
																	locator);
													return;
												}
											}
										} catch (Exception exc) {
										}
									}
								}

								if (st.hasMoreTokens()) {
									while (st.hasMoreTokens()) {
										a = st.nextToken();
										if ((a.startsWith("\"") && a
												.endsWith("\""))
												|| a.startsWith("'")
												&& a.endsWith("'")) {
											dtd = a
													.substring(1,
															a.length() - 1);
											break;
										}
									}
								}
							}
						}
					}
				}
			}

			int line = getDefaultRootElement().getElementIndex(from);

			if (dtd != null) {
				getCurrentEditor().getXMLContainer().getSchemaAccessibility()
						.setDTD(root, dtd, line);
			} else
				getCurrentEditor().getXMLContainer().getSchemaAccessibility()
						.setDTD((String) null, (String) null, -1);

		} catch (BadLocationException exc) {
		}
	}


	/** Check for usage between &lt; or &gt; */
	public boolean isBetweenTagDelimiters( int offset ) {
		int index = getDefaultRootElement().getElementIndex( offset );
		Element e = getDefaultRootElement().getElement( index );
		int start = e.getStartOffset();
		int end = e.getEndOffset();
		boolean startFound = true;
		int lastStart = 0;
		try {
			for ( int i = start; i < end; i++ ) {
				if ( !startFound && 
						"<".equals( getText( i, 1 ) ) ) {
					startFound = true;
					lastStart = i;
				} else
				if ( startFound && 
						">".equals( getText( i, 1 ) ) ) {
					if ( offset > lastStart && 
							offset <= i )
						return true;
					startFound = false;
				}
			}
		} catch ( BadLocationException ex ) {
		}
		return false;
	}

	/** Inside &lt;!ELEMENT&gt; definition */
	public boolean isInsideDTDElementDefinition( int offset ) {
		return isInsideDTDDefinition( "<!ELEMENT", offset );
	}

	/** Inside &lt;!ATTRIBUTE&gt; definition */
	public boolean isInsideDTDAttributeDefinition( int offset ) {
		return isInsideDTDDefinition( "<!ATTLIST", offset );
	}

	private boolean isInsideDTDDefinition( String defPart, int offset ) {
		try {
			boolean rightPart = false;
			for ( int i = offset; i < getLength(); i++ ) {	// Search for '>'
				if ( "<".equals(
						getText( i, 1 ) ) )
					return false;
				else
					if ( ">".equals( 
							getText( i, 1 ) ) ) {
						rightPart = true;
						break;
					}
			}
			if ( !rightPart )
				return false;
			// Search for "<!ELEMENT without > to the left
			for ( int i = offset - 1; i > 0; i-- ) {
				if ( ">".equals( getText( i, 1 ) ) )
					return false;
				if ( "<".equals( getText( i, 1 ) ) )
					return false;
				if ( i > 9 ) {
					String previousContent = 
						getText( i - 9, 9 );
					if ( defPart.equals( previousContent ) )
						return true;
				}
			}
		} catch ( BadLocationException e ) {
			return false;
		}
		return false;
	}

	/** @return the DTD element definition tag for this offset */
	public String getDTDElementDefinitionFor( int offset ) {
		String sequence = "<!ELEMENT ";
		int index = getDefaultRootElement().getElementIndex( offset );
		Element e = getDefaultRootElement().getElement( index );
		int s1 = e.getStartOffset();
		int e1 = e.getEndOffset();
		try {
			String content = getText( s1, e1 - s1 + 1 );
			int i = content.indexOf( sequence );
			if ( i > -1 ) {
				content = content.substring( i + sequence.length() );
				content = content.trim();
				int j = content.indexOf( " " );
				if ( j > -1 ) {
					return content.substring( 0, j );
				}
			}
		} catch (BadLocationException e2) {
		}
		return null;
	}

	/** @return the available &gt;!ELEMENT name */
	public String[] getElementsFromDTD() {
		String sequence = "<!ELEMENT ";
		ArrayList res = null;

		try {
			String fullContent = getText( 0, getLength() );
			int i = 0; 
			while ( i != -1 ) {
				i = fullContent.indexOf( sequence, i );
				if ( i != -1 ) {
					StringBuffer sbRes = null;
					for ( int j = i + sequence.length(); j < getLength(); j++ ) {
						if ( !Character.isSpaceChar( 
								fullContent.charAt( j ) ) ) {
							if ( sbRes == null )
								sbRes = new StringBuffer();
							sbRes.append( 
									fullContent.charAt( j ) );
						} else
							if ( sbRes != null ) {
								if ( res == null )
									res = new ArrayList();
								res.add( sbRes.toString() );
								break;
							} else 
								if ( fullContent.charAt( j ) == '>' )
									break;	// Wrong state
					}
					i++;
				}
			}
		} catch (BadLocationException e) {
		}

		if ( res == null )
			return null;
		else {
			String[] s = new String[ res.size() ];
			for ( int i = 0; i < res.size(); i++ )
				s[ i ] = ( String )res.get( i );
			return s;
		}
	}

	/** Remove a text at this offset, it will not synchronize the tree */
	public void removeWithoutStructureDamaged(int offset, int len) {
		try {
			rawRemove(offset, len);
			editor.getXMLContainer().setModifiedState(true);
		} catch (BadLocationException exc) {
		}
	}

	public void rawRemove(int offs, int len) throws BadLocationException {
		int rowNumber = 0;
		String txt = getText( offs, len );
		for ( int i = 0; i < txt.length(); i++ ) {
			if ( txt.charAt( i ) == '\n' ) {
				rowNumber++;
			}
		}
		editor.checkClosedElement( 
			getDefaultRootElement().getElementIndex( offs ), 
			rowNumber, 
			false 
		);
		super.remove( offs, len );
	}

	/** Remove a text at this offset. It will synchronize the tre */
	public void remove(int offs, int len) throws BadLocationException {

		if (!getCurrentEditor().getXMLContainer().isEditableDocumentMode())
			return;

		if (getCurrentEditor().getXMLContainer().getDocumentIntegrity()
				.isProtectTag()) {
			if (isInsideTagExceptAttributeValue(offs))
				return;
		}

		boolean damaged = false;

		if (len > 0 && isEnableStructureDamagedSupport()) {
			String txt = getText(offs, len);

			if (txt.endsWith("\n")
					|| getContainer().isRealTimeTreeOnTextChange())
				damaged = true;

			if (!damaged)
				for (int i = 0; i < txt.length(); i++) {
					if (txt.charAt(i) == '<') {
						damaged = true;
						break;
					}
				}
		}

		rawRemove(offs, len);
		if (damaged)
			structureDamaged();
		editor.getXMLContainer().setModifiedState(true);
	}
	
	/** @return a set of ordered element name if possible */
	public List<String> getCollectionOfElements() {
		HashSet<String> r = new HashSet<String>();
		FPNode root = 
			getContainer().getRootNode();
		if ( root != null ) {
			FastVector v = root.getDocument().getFlatNodes();
			if ( v != null ) {
				for ( int i = 0; i < v.size(); i++ ) {
					FPNode n = ( FPNode )v.get( i );
					if ( n.isTag() ) {
						r.add( n.getContent() );
					}
				}
			}
		}
		ArrayList<String> l = new ArrayList<String>(r);
		Collections.sort(l);
		return l;
	}

	/** @return a set of ordered attributes name found for the elementName parameter */
	public List<String> getCollectionOfAttributes( String elementName ) {
		HashSet<String> r = new HashSet<String>();
		FPNode root = 
			getContainer().getRootNode();
		if ( root != null ) {
			FastVector v = root.getDocument().getFlatNodes();
			if ( v != null ) {
				for ( int i = 0; i < v.size(); i++ ) {
					FPNode n = ( FPNode )v.get( i );
					if ( n.isTag() ) {
						if ( n.matchContent( elementName ) ) {
							for ( int j = 0; j < n.getViewAttributeCount(); j++ ) {
								String attName = n.getViewAttributeAt( j );
								r.add( attName );
							}
						}
					}
				}
			}
		}
		ArrayList<String> l = new ArrayList<String>(r);
		Collections.sort(l);
		return l;		
	}

	/** @return a set of ordered children content for the elementName parameter */
	public List<String> getCollectionOfChildren( String elementName ) {
		HashSet<String> r = new HashSet<String>();
		FPNode root = 
			getContainer().getRootNode();
		if ( root != null ) {
			FastVector v = root.getDocument().getFlatNodes();
			if ( v != null ) {
				for ( int i = 0; i < v.size(); i++ ) {
					FPNode n = ( FPNode )v.get( i );
					if ( n.isTag() ) {
						if ( n.matchContent( elementName ) ) {
							for ( int j = 0; j < n.childCount(); j++ ) {
								FPNode m = n.childAt(j);
								if ( m.isTag() ) {
									r.add( m.getContent() );
								}
							}
						}
					}
				}
			}
		}
		ArrayList<String> l = new ArrayList<String>(r);
		Collections.sort(l);
		return l;				
	}

	// Auto indent

	private String getClosingTagPart(String indent, String tagName) {
		StringBuffer b = new StringBuffer();
		b.append("\n");
		if (indent != null)
			b.append(indent);
		b.append("</");
		b.append(tagName);
		b.append(">");
		return b.toString();
	}

	protected String getIndentAtOffset(int offset) {
		try {
			Element root = getDefaultRootElement();
			int row = root.getElementIndex(offset);
			Element e = root.getElement(row);
			int lineStart = e.getStartOffset();
			int lineEnd = e.getEndOffset();
			String txt = getText(lineStart, lineEnd - lineStart);
			int length = txt.length();
			int i = 0;
			for (; i < length; i++) {
				char ch = txt.charAt(i);
				if (ch > ' ' || ch == '\n')
					break;
			}
			if (i > 0)
				return txt.substring(0, i);
		} catch (BadLocationException e) {
		}
		return "";
	}
	
	///////////////////////////////////////////////////////////////////////

	/**
	 * An implementation of <code>DocumentListener</code> that inserts and
	 * deletes lines from the token marker's state.remove */
	public class XMLDocumentListener implements DocumentListener {
		Segment line = null;

		public XMLDocumentListener() {
			line = new Segment();
		}

		public void insertUpdate(DocumentEvent evt) {
			notifiedChange();
		}

		public void removeUpdate(DocumentEvent evt) {
			notifiedChange();
		}

		public void changedUpdate(DocumentEvent evt) {
			notifiedChange();
		}
		
		private void notifiedChange() {
			editor.notifyDocumentChanged();
			if (!isEnableStructureDamagedSupport())
				return;
			XMLPadDocument.this.structureDamaged();
		}
	}

	private int getStartEndOffset(int endOffset) throws BadLocationException {
		for (int i = Math.min(getLength() - 1, endOffset); i > 0; i--) {
			if ("<".equals(getText(i, 1)))
				return i;
		}
		return 0;
	}

	private int getEndStartOffset(int startOffset) throws BadLocationException {
		for (int i = startOffset; i < getLength(); i++) {
			if (">".equals(getText(i, 1)))
				return i;
		}
		return 0;
	}

	/** Used mainly by the elementView for updating the element content */
	public Point updateElement(String startElement, String endElement,
			int startOffset, int endOffset) {

		boolean sd = isEnableStructureDamagedSupport();
		boolean xl = getCurrentEditor().isEnabledXPathLocation();
		enableStructureDamagedSupport(false);
		getCurrentEditor().setEnabledXPathLocation(false);
		Point p = null;

		try {

			try {
				int startEndOffset = 0;

				if (endElement != null)
					startEndOffset = getStartEndOffset(endOffset);

				int endStartOffset = getEndStartOffset(startOffset);

				if (startEndOffset == -1 || endStartOffset == -1)
					return null;

				p = new Point(0, 0);

				// Replace the end part
				if (endElement != null && endOffset > 0) {
					int length = endOffset - startEndOffset + 1;

					if (length + startEndOffset > getLength()) {
						length = getLength() - startEndOffset;
					}

					replace(startEndOffset, length, endElement, null);

					// Check for extra char

					int delta = startEndOffset + endElement.length();

					try {
						for (int i = 0; i < 30; i++) {
							if (getText(i + delta, 1).equals("<"))
								break;
							if (getText(i + delta, 1).equals(">")) { // Error ?
								replace(delta, i + 1, "", null);
								break;
							}
						}
					} catch (BadLocationException exc) {
					}
				}

				int length = endStartOffset - startOffset + 1;

				// Replace the start part
				replace(startOffset, length, startElement, null);

				p.y = startEndOffset
						- (length - startElement.length())
						+ ((endElement != null ? (endElement.length() - 1) : 0));

			} catch (BadLocationException exc) {
				exc.printStackTrace();
				// ?
			}

		} finally {
			enableStructureDamagedSupport(sd);
			getCurrentEditor().setEnabledXPathLocation(xl);
			getCurrentEditor().setDelayedStructureDamaged(true);
		}

		return p;
	}

	/** Replace this node by this new content */
	public void updateNode(FPNode sn, String newContent) {
		int from = sn.getStartingOffset();
		int end = sn.getStoppingOffset();
		try {
			replace(from, end - from + 1, newContent, null);
		} catch (BadLocationException ble) {
		}
	}

	/**
	 * Update the node changed inside the editor. Only the opening part and the
	 * closing part will be updated. The content of this node WILL not be
	 * updated.
	 * @param node Node to update */
	public void updateNodeOpeningClosing( FPNode node ) {

		String opening = null;
		String closing = null;

		StringBuffer sbOpening = new StringBuffer("<");
		if (node.getNameSpacePrefix() != null)
			sbOpening.append(node.getNameSpacePrefix()).append(":");

		sbOpening.append(node.getNodeContent());

		for (int i = 0; i < node.getViewAttributeCount(); i++) {
			String attributeName = node.getViewAttributeAt(i);
			String attributeValue = node.getAttribute(attributeName);
			sbOpening.append(" ");
			sbOpening.append(attributeName);
			sbOpening.append("=\"");
			sbOpening.append(XMLToolkit.resolveCharEntities(attributeValue));
			sbOpening.append("\"");
		}

		// Namespace definition

		for (Iterator<String> enume = node.getNameSpaceDeclaration(); enume != null
				&& enume.hasNext();) {
			String xmlnsPrefix = (String) enume.next();
			String xmlnsValue = node.getNameSpaceDeclarationURI(xmlnsPrefix);
			sbOpening.append(" ");
			sbOpening.append("xmlns:").append(xmlnsPrefix);
			sbOpening.append("=\"").append(xmlnsValue).append("\"");
		}
		
		if ( node.getDefaultNamespace() != null ) {
			sbOpening.append(" ");			
			sbOpening.append("xmlns=\"").append(node.getDefaultNamespace()).append("\"");			
		}
		
		if (node.isAutoClose()) {
			sbOpening.append("/");
		}

		sbOpening.append(">");

		opening = sbOpening.toString();

		if (!node.isAutoClose()) {
			StringBuffer sbClosing = new StringBuffer("</");
			if (node.getNameSpacePrefix() != null)
				sbClosing.append(node.getNameSpacePrefix()).append(":");
			sbClosing.append(node.getNodeContent());
			sbClosing.append(">");
			closing = sbClosing.toString();
		}

		updateElement(opening, closing, node.getStartingOffset(), node
				.getStoppingOffset());
	}

}
