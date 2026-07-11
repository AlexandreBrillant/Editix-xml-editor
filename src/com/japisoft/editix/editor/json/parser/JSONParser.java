// Editix XML Editor
// https://www.editix.com
// Copyright (c) 2026 Alexandre Brillant
//
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
// AI Training Restriction :
// This source code is provided for human use only.
// Using this code to train, fine-tune, or develop AI models,
// machine learning systems, or similar technologies is
// STRICTLY PROHIBITED. Violations will terminate all rights
// under the applicable license.

package com.japisoft.editix.editor.json.parser;

import java.util.ArrayList;

import java.util.List;
import java.util.Stack;

import com.japisoft.framework.xml.parser.node.FPNode;

public class JSONParser {

	public FPNode parse( String content ) {
		char[] chrs = content.toCharArray();
		return parse( chrs );
	}
	
	private Stack<FPNode> stacks = null;
	private FPNode current = null;
	private  int offset = 0;
	
	private FPNode parse( char[] content ) {
		stacks = new Stack<FPNode>();
		FPNode root = null;
		StringBuffer string = null;
		String lastString = null;
		int lastStringOffset = 0;
		FPNode node = null;
		
		while ( offset < content.length ) {
			char c = content[ offset ];
			switch( c ) {
				case '{' :
					node = new FPNode( FPNode.TAG_NODE, "object" );
					node.setApplicationObject( "object" );
					if ( root == null )
						root = node;
					stacks.push( node );
					break;
				case '}' :
					List<FPNode> res = new ArrayList<FPNode>();
					boolean closeLast = false;
					
					while ( stacks.size() > 0 ) {

						FPNode value = stacks.pop();
						FPNode property = stacks.pop();
						
						property.appendChild( value );
						res.add( property );
						
						if ( "object".equals( stacks.peek().getApplicationObject() ) )
								break;
						
						/*
						
						// Close the last property
						node = stacks.pop();
						if ( "property".equals( 
							node.getApplicationObject() ) && !closeLast ) {
							if ( res.size() > 0 ) {
								node.addNode( res.get( 0 ) );
								res.remove( 0 );
							}
							closeLast = true;
						}
						
						res.add( 0, node );
						
						*/
					}
					
					java.util.Collections.reverse( res );
					
					if ( stacks.size() > 0 ) {
						FPNode object = stacks.peek();
						object.stoppingOffset = offset;

						for( FPNode co : res ) {
							object.addNode( co );
						}
						if ( stacks.size() > 0 ) {
							// node = stacks.pop();
							// node.addNode( object );
							// stacks.push( node );
						}
					} else {
						// Error
					}
					break;
				case '[' :
					break;
				case ']':
					break;
				case ':':
					node = stacks.pop();
					if ( node.getType() == FPNode.TEXT_NODE ) {
						FPNode stringNode = node;
						node = new FPNode( FPNode.TAG_NODE, stringNode.getContent() );
						node.setApplicationObject( "property" );
						node.startingOffset = stringNode.startingOffset;
						node.stoppingOffset = offset;
						stacks.push( node );
					}
					break;
				case '"':
					if ( string == null ) {
						string = new StringBuffer();
						lastStringOffset = offset;
					}
					else
					{
						lastString = string.toString();
						node = new FPNode( FPNode.TEXT_NODE, lastString );
						node.startingOffset = lastStringOffset;
						node.stoppingOffset = offset + 1;
						stacks.push( node );
						string = null;
					}
					break;
				case ',' :
					/*
					FPNode value = stacks.pop();
					FPNode property = stacks.pop();
					property.addNode( value );
					stacks.push( property );
					*/
					break;
				default :
					if ( string != null ) {
						string.append( c );
					}
			}
			
			offset++;
		}
		
		return root;
	}
	
	public static void main( String[] args ) {
		
		JSONParser p = new JSONParser();
		FPNode root = p.parse( "{ \"aa\" : \"bb\", \"cc\" : { \"dd\" : \"ee\", \"ff\":\"gg\" }, \"hh\":\"ii\" }" );
		// FPNode root = p.parse( "{ \"aa\" : \"bb\", \"cc\" : { \"dd\" : \"ee\" }, }" );
		// FPNode root = p.parse( "{ \"aa\" : \"bb\", \"cc\" : \"dd\" }" );		
		System.out.println( root.getRawXML( 1 ) );
		
	}
	
}

