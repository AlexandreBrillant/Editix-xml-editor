// Editix XML Editor
// https://www.editix.com
// Copyright (c) 2025 Alexandre Brillant
// 
// For non-commercial usage :
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
// See the GNU General Public License for more details: https://www.gnu.org/licenses/gpl-3.0
// 
// For commercial use or integration into proprietary software :
// A commercial license is required. Visit https://www.editix.com for details.

package com.japisoft.editix.editor.css;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;

import org.apache.commons.io.IOUtils;

import com.japisoft.framework.collection.FastArrayList;
import com.japisoft.framework.xml.parser.ErrorParsingListener;
import com.japisoft.framework.xml.parser.ParseException;
import com.japisoft.framework.xml.parser.document.Document;
import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.framework.xml.parser.node.NodeFactory;
import com.japisoft.xmlpad.tree.parser.Parser;

public class CSSParser implements Parser {

	public CSSParser() {
	}
	
	public boolean hasError() {
		return false;
	}

	public void interruptParsing() {
	}

	public boolean isInterrupted() {
		return false;
	}
	
	@Override
	public void setLightweightMode(boolean b) {
	}
	@Override
	public boolean isLightweightMode() {
		return false;
	}

	public Document parse(Reader reader, Object context ) throws ParseException {
		try {
			String json = IOUtils.toString( reader );
			Lexer l = new Lexer( json );
			Token t = null;
			Token old = null;
			
			Document doc = new Document();
			ArrayList<FPNode> flatNodes = new ArrayList<FPNode>(); 
			doc.setFlatNode( flatNodes );
			
			FPNode root = new FPNode( FPNode.TAG_NODE, "css" );
			root.setStartingOffset( 0 );
			root.setStoppingOffset( json.length() );
			doc.setRoot( root );
			root.setDocument( doc );
			FPNode selector = null;
			FPNode property = null;
			
			flatNodes.add( root );
			
			boolean inSelector = false;
			
			Token selectorPart = null;
			
			while ( ( t = l.nextToken() ) != null ) {
				if ( "{".equals( t.value ) ) {
					if ( old == null )
						throw new ParseException( "No Selector ? ", l.offset, l.line );

					inSelector = true;
					
					if ( selectorPart != null ) {
						
						old.value = selectorPart.value + ":" + old.value;
						
						selectorPart = null;
						
					}
					selector = new FPNode( FPNode.TAG_NODE, old.value );
					selector.setStartingOffset( old.start );
					selector.setStoppingOffset( old.stop );
					selector.setStartingLine( old.line );
					selector.setStoppingLine( old.line );
					selector.setDocument( doc );

					root.appendChild( selector );
					
					flatNodes.add( selector );
				}
				
				if ( "}".equals( t.value ) ) {
					inSelector = false;
				}
				
				if ( ";".equals( t.value ) ) {
					
					if ( inSelector ) {
						
						if ( old == null )
							throw new ParseException( "No Property ?", l.offset, l.line );
						
						if ( selector == null )
							throw new ParseException( "No Selector ?", l.offset, l.line );
						
						if ( old.value.contains( ":" ) ) {
							old.value = old.value.split( ":" )[ 0 ];
						} else {
							throw new ParseException( "Invalid property, ':' is required", l.offset, l.line );
						}

						property = new FPNode( FPNode.TAG_NODE, old.value );
						property.setStartingOffset( old.start );
						property.setStoppingOffset( old.stop );
						property.setStartingLine( old.line );
						property.setStoppingLine( old.line );
						property.setDocument( doc );
						
						flatNodes.add( property );
						
						selector.appendChild( property );
						
					} else {
				
						selectorPart = old;
						
					}
				}
				
				old = t;
			}
			
			return doc;
			
		} catch( IOException exc ) {
			throw new ParseException( exc.getMessage() );
		}
	}
	
	public void setBackgroundMode(boolean b) {
	}

	public void setErrorSignal(ErrorParsingListener parsingErrorListener) {
	}

	public void setFlatView(boolean b) {
	}

	public void setNodeFactory(NodeFactory factory) {
	}

	public void setParsingMode(int continueParsingMode) {
	}

	///////////////////////////////////////////////////////////////////////////////////////////
	
	class Token {
		int start,stop,line;
		String value;
	}
	
	class Lexer {
		char[] content;
		int offset = 0;
		int oldOffset = 0;
		int line = 1;
		
		public Lexer( String content ) {
			this.content = content.toCharArray();
		}

		private boolean isSeparator(char c) {
			return c == '{' || c == '}' || c == ';';
		}
		
		public Token nextToken() {

			StringBuffer buffer = null;
			boolean startComment = false;
			
			while ( offset < content.length ) {
				char c = content[ offset ];
				
				if ( c == '*' && offset > 0 && content[ offset - 1 ] == '/' ) {
					startComment = true;
					buffer = null;
				}
				
				if ( c == '\n' )
					line++;
				else {

					if ( !startComment ) {

						if ( !isSeparator( c ) ) {
							if ( buffer == null ) {
								buffer = new StringBuffer();
								oldOffset = offset;
							}
						}

						if ( isSeparator( c ) ) {
							if ( buffer != null ) {
								Token t = new Token();
								t.value = buffer.toString();
								t.start = oldOffset;
								t.stop = offset;
								t.line = line;
								buffer = null;
								if ( !isSeparator( c ) )
									offset++;
								
								return t;
							}
						}
						
						if ( isSeparator( c ) ) {
							Token t = new Token();
							t.value = "" + c;
							t.start = offset;
							t.stop = offset;
							t.line = line;
							offset++;
							return t;
						}
	
						if ( buffer != null ) {
							buffer.append( c );
						}
						
					} else {
						
						if ( c == '/' && offset > 0 && content[ offset - 1 ] == '*' ) {
							startComment = false;
						}						

					}
				}
				offset++;
				if ( offset == content.length )
					return null;
			}
			
			return null;
		}
	}

}

