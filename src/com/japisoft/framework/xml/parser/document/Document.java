package com.japisoft.framework.xml.parser.document;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import com.japisoft.framework.collection.FastVector;
import com.japisoft.framework.xml.parser.node.MutableNode;
import com.japisoft.framework.xml.parser.node.NodeFactory;
import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.framework.xml.parser.node.NodeFactoryImpl;
import com.japisoft.framework.xml.parser.node.ViewableNode;
import com.japisoft.framework.xml.parser.tools.XMLToolkit;

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
public class Document {
	
	public Document() {
		super();
	}

	/** Reset the current document root */
	public Document( MutableNode root ) {
		setRoot( root );
	}
	
	private NodeFactory nf;

	/*
	//////////////////////// MEMORY OPTIMISATION //////////////////////////
	private String[] elementDictionnary = null;
	private int idCounter = 0; 
	public int getStringId( String name ) {
		if ( elementDictionnary == null )
			elementDictionnary = new String[ 30 ];
		for ( int i = 0; i < elementDictionnary.length; i++ ) {
			if ( elementDictionnary[ i ] == null )
				break;
			if ( elementDictionnary[ i ].equals( name ) )
				return i;
		}
		if ( idCounter == elementDictionnary.length ) {
			// Double the capacity
			String[] tmp = new String[ elementDictionnary.length * 2 ];
			// Copy the old array to the new one
			System.arraycopy( elementDictionnary, 0, tmp, 0, elementDictionnary.length );
			elementDictionnary = tmp;
		}
		elementDictionnary[ idCounter ] = name;
		// Store it
		idCounter++;
		return ( idCounter - 1 );
	}
	
	public String getStringById( int id ) {
		return elementDictionnary[ id ]; 
	}
	public String[] getElementDictionnary() {
		return elementDictionnary;
	}
	///////////////////////////////////////////////////////////////////////
	 */
	
	/** @return the current node factory */
	public NodeFactory getNodeFactory() {
		if ( nf == null )
			return NodeFactoryImpl.getInstance();
		return nf;
	}

	/** Reset the node factory */
	public void setNodeFactory( NodeFactory nf ) {
		this.nf = nf;
	}

	private MutableNode root;

	/** @reset the document root node */
	public void setRoot( MutableNode root ) {
		this.root = root;
	}

	/** @return the document root node */
	public MutableNode getRoot() {
		return root;
	}

	/** Write the document content in out. out is automatically closed at end */
	public void write(OutputStream out) throws IOException {
		checkRoot();
		String res = createView( ( ViewableNode ) root );
		try {
			out.write(res.getBytes());
			out.flush();
		} finally {
			out.close();
		}
	}

	private void checkRoot() {
		if (root == null) {
			throw new RuntimeException("No document root");
		}
		if (!(root instanceof ViewableNode))
			throw new RuntimeException("Need a ViewableNode type");
	}

	/** Write the document content in writer. writer is automatically closed at end */
	public void write( Writer writer ) throws IOException {
		checkRoot();
		String res = createView( ( ViewableNode ) root );
		try {
			writer.write( res );
		} finally {
			writer.close();
		}
	}

	private String createView(ViewableNode node) {
		StringBuffer sb = new StringBuffer();
		writeHeader(sb);
		writeNode(node, sb, 0);
		return sb.toString();
	}

	private String indentPrefix = " ";

	/** Reset the prefix for node tabulation */
	public void setIndentPrefix(String prefix) {
		this.indentPrefix = prefix;
	}

	private void writeNode(ViewableNode node, StringBuffer r, int indent) {
		if (node.isViewText()) // Text
			r.append(XMLToolkit.resolveCharEntities(node.getViewContent()));
		else if (node.isViewComment()) { // Comment
			r.append("<!-- " + node.getViewContent() + "-->");
		} else { // Tag

//			r.append("\n");

			for (int i = 0; i < indent; i++) {
				r.append(indentPrefix);
			}

			r.append("<");
			if (node.getNameSpacePrefix() != null)
				r.append(node.getNameSpacePrefix()).append(":");
			r.append(node.getViewContent());

			// NameSpace declaration
			Iterator<String> enume = node.getNameSpaceDeclaration();
			if (enume != null) {
				r.append(" ");
				boolean sp = false;
				while (enume.hasNext()) {
					if (sp)
						r.append(" ");
					String prefix = (String) enume.next();
					String uri = node.getNameSpaceDeclarationURI(prefix);
					r.append("xmlns:").append(prefix);
					r.append("=\"").append(uri).append("\"");
					sp = true;
				}
			}

			if (node.getDefaultNamespace() != null) {
				r.append(" xmlns=\"").append(
					node.getDefaultNamespace()).append(
					"\"");
			}

			if ( node.getViewAttributeCount() > 0 ) {
				r.append(" ");
				boolean sp = false;
				for ( int i = 0; i < node.getViewAttributeCount(); i++ ) {
					if (sp)
						r.append(" ");
					String att = (String) node.getViewAttributeAt( i );
					String v = node.getViewAttribute(att);
					r.append(att).append("=\"").append(XMLToolkit.resolveCharEntities(v)).append("\"");
					sp = true;					
				}
			}
						
			if (node.isViewLeaf())
				r.append("/>");
			else {
				r.append(">\n");
				for (int i = 0; i < node.getViewChildCount(); i++) {
					writeNode(node.getViewChildAt(i), r, indent + 1);
				}

				r.append("\n");

				for (int i = 0; i < indent; i++) {
					r.append(indentPrefix);
				}
				
				if (node.getNameSpacePrefix() != null)
					r.append("</" + node.getNameSpacePrefix() + ":" + node.getViewContent() + ">\n");
				else
					r.append("</" + node.getViewContent() + ">\n");
			}
		}
	}

	int maj = 1;
	int min = 0;

	/** Reset the XML version, by default 1.0 */
	public void setXmlVersion(int maj, int min) {
		this.maj = maj;
		this.min = min;
	}

	String encoding = null;

	/** Set the document encoding, use the ENCODING_... constant for this */
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	boolean standalone = false;

	/** standalone mode */
	public void setStandalone(boolean standalone) {
		this.standalone = standalone;
	}

	// Encoding type

	public static String ENCODING_UTF_8 = "UTF-8";
	public static String ENCODING_UTF_16 = "UTF-16";
	public static String ENCODING_ISO_10646_UCS_2 = "ISO-10646-UCS-2";
	public static String ENCODING_ISO_10646_UCS_4 = "ISO-10646-UCS-4";
	public static String ENCODING_ISO_8859_1 = "ISO-8859-1";
	public static String ENCODING_ISO_8859_2 = "ISO-8859-2";
	public static String ENCODING_ISO_8859_3 = "ISO-8859-3";
	public static String ENCODING_ISO_8859_4 = "ISO-8859-4";
	public static String ENCODING_ISO_8859_5 = "ISO-8859-5";
	public static String ENCODING_ISO_8859_6 = "ISO-8859-6";
	public static String ENCODING_ISO_8859_7 = "ISO-8859-7";
	public static String ENCODING_ISO_8859_8 = "ISO-8859-8";
	public static String ENCODING_ISO_8859_9 = "ISO-8859-9";
	public static String ENCODING_ISO_2022_JP = "ISO-2022-JP";
	public static String ENCODING_Shift_JIS = "Shift_JIS";
	public static String ENCODING_EUC_JP = "EUC-JP";

	private FastVector vHeader;

	/** Reset the comment header node, this is a vector of SimpleNode */
	public void setHeaderNode(FastVector v) {
		this.vHeader = v;
	}

	private FastVector flatNodes;

	/** Reset the flat nodes */
	public void setFlatNode( FastVector v ) {
		flatNodes = v;
	}
	
	/** @return a flat view of the current document */
	public FastVector getFlatNodes() {
		return flatNodes;
	}

	/** @return a collection of ordered nodes using the flat nodes */
	public List<String> getOrderedTags() {
		ArrayList<String> l = new ArrayList<String>();
		if ( flatNodes != null ) {
			for ( int i = 0; i < flatNodes.size(); i++ ) {
				FPNode sn = ( FPNode )flatNodes.get( i );
				if ( sn.isTag() ) {
					if ( !l.contains( sn.getNodeContent() ) )
						l.add( sn.getNodeContent() );
				}
			}
			Collections.sort( l );
		}
		return l;
	}

	/** @return a collection of founded attributes for the tag argument, it uses the flat nodes */
	public List<String> getOrderedAttributes( String tag ) {
		ArrayList<String> l = new ArrayList<String>();
		if ( flatNodes != null ) {
			for ( int i = 0; i < flatNodes.size(); i++ ) {
				FPNode sn = ( FPNode )flatNodes.get( i );
				if ( sn.isTag() ) {
					if ( sn.matchContent( tag ) ) {
						for ( int j = 0; j < sn.getViewAttributeCount(); j++ ) {
							String att = sn.getViewAttributeAt( j );
							if ( !l.contains( att ) )
								l.add( att );
						}
					}
				}
			}
			Collections.sort( l );
		}
		return l;
	}
	
	private void writeHeader(StringBuffer sb) {
		sb.append("<?xml version=\"").append(maj).append(".").append(
			min).append(
			"\"");
		if (encoding != null)
			sb.append(" encoding=\"").append(encoding).append("\"");
		if (standalone) {
			sb.append(" standalone=\"").append(standalone).append("\"");
		}
		sb.append("?>\n");
		if (vHeader != null) {
			sb.append("\n");
			for (int i = 0; i < vHeader.size(); i++) {
				ViewableNode node = (ViewableNode) vHeader.get(i);
				sb.append("<!" + node.getViewContent() + ">\n");
			}
		}
	}

}
