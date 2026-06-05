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

package com.japisoft.editix.editor.js;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;

import com.japisoft.framework.collection.FastArrayList;
import com.japisoft.framework.xml.parser.ErrorParsingListener;
import com.japisoft.framework.xml.parser.ParseException;
import com.japisoft.framework.xml.parser.document.Document;
import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.framework.xml.parser.node.NodeFactory;
import com.japisoft.xmlpad.tree.parser.Parser;

public class JSParser implements Parser {

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

			String js = IOUtils.toString( reader );
			String[] lines = js.split( "\n" );

			Document doc = new Document();
			ArrayList<FPNode> flatNodes = new ArrayList<FPNode>(); 
			doc.setFlatNode( flatNodes );

			FPNode root = new FPNode( FPNode.TAG_NODE, "javascript" );
			root.setStartingOffset( 0 );
			root.setStoppingOffset( js.length() );
			doc.setRoot( root );
			root.setDocument( doc );
			
			Pattern pat = Pattern.compile( "function\\s+(\\w+)\\s*\\(" );
			int i = 1;
			int offset = 0;
			
			FPNode nn = null;
			int openCpt = 0;
			
			for ( String line : lines ) {
				Matcher matcher = pat.matcher( line );
				if ( matcher.find() ) {
					String function = matcher.group( 1 );
					int start = matcher.regionStart() + offset;
					int end = matcher.regionEnd() + offset;
					
					nn = new FPNode( FPNode.TAG_NODE, function );
					nn.startingOffset = start;
					nn.stoppingOffset = end;
					nn.setStartingLine( i );
					nn.setStoppingLine( i );
					
					flatNodes.add( nn );
					
					root.appendChild( nn );
				} else {
					
					if ( nn != null ) {

						openCpt += line.length() - line.replace("{", "").length();
						int endP = line.length() - line.replace("}", "").length();
						openCpt -= endP;
						if ( openCpt <= 0 && endP > 0 ) {
							nn.setStoppingOffset( offset );
							nn.setStoppingLine( i );
						}
						
					}
					
				}
				offset += line.length() + 1;
				i++;
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
	
	public static void main( String[] args ) throws Exception {
		JSParser p = new JSParser();
		StringReader sr = new StringReader( "function a() {\n\naa" +
				"}\n\n\n\n" +
				"function b() {}" );
		p.parse( sr, null );
	}
	
}

