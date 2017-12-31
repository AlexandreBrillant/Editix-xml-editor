package com.japisoft.framework.xml.parser.document;

import java.util.Iterator;
import java.util.Stack;

import com.japisoft.framework.collection.FastVector;
import com.japisoft.framework.xml.parser.Messages;
import com.japisoft.framework.xml.parser.FPParser;
import com.japisoft.framework.xml.parser.document.Document;
import com.japisoft.framework.xml.parser.document.DocumentBuilder;
import com.japisoft.framework.xml.parser.document.DocumentBuilderException;
import com.japisoft.framework.xml.parser.document.NamespaceContext;
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
public class DocumentBuilderImpl implements DocumentBuilder {
	private MutableNode root;
	private Stack stack;
	private FastVector header;
	private Document doc;
	private NamespaceContext nscontext;

	public DocumentBuilderImpl() {
		doc = new Document();
	}

	public void dispose() {}

	public boolean isTerminated() {
		return stack == null || stack.isEmpty();
	}

	private boolean flatMode;

	private FastVector flatView;

	/** Store all node in a flat view */
	public void setFlatView(boolean flatMode) {
		this.flatMode = flatMode;
		if (flatMode)
			flatView = new FastVector();
		else
			flatView = null;
		doc.setFlatNode(flatView);
	}

	/**
	 * @return the current Flat view. This is null if the FlatView mode was to
	 *         false
	 */
	public FastVector getFlatView() {
		return flatView;
	}

	private MutableNode current;

	/** Reset the current node */
	public Object openNode(FPParser p, String prefix, String prefixURI, String tag)
			throws DocumentBuilderException {

		if (checkPrefix && nscontext != null && prefix != null) {
			if (prefixURI == null) {
				prefixURI = nscontext.getPrefixURI(prefix);
				//if (prefixURI == null) {
				//	throw new DocumentBuilderException(
				//		"Unknown prefix URI for " + prefix);
				//}
			}
		}

		String tagName = tag;
		boolean singleTag = tagName.endsWith("/");
		
		if (singleTag)
			tagName = tagName.substring(0, tagName.length() - 1);

		// Search for the id
		// int idStr = doc.getStringId( tagName );
		MutableNode node = p.getNodeFactory().getTagNode( tagName );
		node.setDocument( doc );

		if (flatMode)
			flatView.add(node);

		if (nscontext != null && prefix == null) {
			prefixURI = nscontext.currentDefaultNamespace();
		}

		node.setNameSpace(prefix, prefixURI);
		if ( p != null )
			node.setStartingLine(p.line);

		int l = tagName.length();
		if (prefix != null)
			l += prefix.length() + 1;

		
		int offset = 0;
		if ( p != null )
			offset = p.offset;

		if (singleTag)
			offset--;
		
		node.setStartingOffset(offset - l - 1);

		if (root == null) {
			root = node;
			stack = new Stack();
		} else {
			if ( current != null )
				current.addNode(node);
		}

		if (!singleTag) {
			current = node;
			stack.push(current);
		} else {
			node.setAutoClose(true);
			if ( p != null )
				node.setStoppingLine(p.line);
			if (singleTag)
				offset++;
			node.setStoppingOffset(offset);
		}
		
		return node;
	}

	private boolean checkCloseTag = true;

	/**
	 * Check the closing node :<code>false</code> should be used for always
	 * well formed document : by default true
	 */
	public void setCheckForCloseTag(boolean check) {
		this.checkCloseTag = check;
	}

	private boolean checkPrefix = true;

	/**
	 * Check if the namespace prefix of qualified name is valid : by default
	 * true
	 */
	public void setCheckPrefixForNamespace(boolean check) {
		this.checkPrefix = check;
	}

	/**
	 * Close the current node, an exception is thrown for invalid tag name,
	 * prefix is for namespace
	 */
	public Object closeNode(FPParser p, String prefix, String tag)
			throws DocumentBuilderException {

		if (stack == null)
			throw new DocumentBuilderException(Messages.ERROR_TAG2 + " " + tag);

		current = (MutableNode) stack.pop();

		if (prefix == null) {
			if (checkCloseTag
					&& (current == null || !(tag.equals(current
							.getNodeContent())))) {

				if (current == null) {
					throw new DocumentBuilderException(Messages.ERROR_TAG2
							+ " " + tag);
				}
				throw new DocumentBuilderException(Messages.ERROR_TAG2 + " "
						+ tag + " " + Messages.ERROR_TAG3 + " </"
						+ current.getNodeContent() + ">");
			}
		} else {
			if (checkCloseTag
					&& (current == null || !(tag.equals(current
							.getNodeContent()) && prefix.equals(current
							.getNameSpacePrefix())))) {
				if (current == null) {
					throw new DocumentBuilderException(Messages.ERROR_TAG2
							+ " " + tag);
				}
				throw new DocumentBuilderException(

				Messages.ERROR_TAG2 + " " + tag + " " + Messages.ERROR_TAG3
						+ " </" + current.getNameSpacePrefix() + ":"
						+ current.getNodeContent() + ">");
			}
		}

		if (checkPrefix && current.getNameSpacePrefix() != null
				&& current.getNameSpaceURI() == null) {
			throw new DocumentBuilderException(Messages.ERROR_PREFIX1 + " "
					+ current.getNameSpacePrefix());
		}

		if ( p != null ) {
			current.setStoppingLine(p.line);
			current.setStoppingOffset(p.offset);
		}

		updateNamespaceScope();

		if ( prefixToCheck != null ) {
			for ( int i = 0; i < prefixToCheck.size(); i++ ) {
				String prefixAtt = ( String )prefixToCheck.get( i );
				if ( nscontext.getPrefixURI( prefixAtt ) == null ) {
					if ( p != null ) {
						p.line = ( (ViewableNode)current ).getStartingLine() - 2;
						p.col = ( (ViewableNode)current ).getStartingOffset();
					}
					throw new RuntimeException( "Unkown prefix '" + prefixAtt + "'" );
				}
			}
			prefixToCheck = null;
		}

		Object tmp = current;
		
		// Get the last stack item
		if (stack.size() > 0)
			current = (MutableNode) stack.peek();
		else
			current = null;
		
		return tmp;
	}

	private void updateNamespaceScope() {
		Iterator<String> prefixs = current.getNameSpaceDeclaration();
		if (prefixs != null && nscontext != null) {
			while (prefixs.hasNext()) {
				nscontext.removePrefixScope((String) prefixs.next());
			}
		}
		if (current.getDefaultNamespace() != null)
			nscontext.popDefaultNamespace();
	}

	public void closeNode(FPParser p) throws DocumentBuilderException {
		if (current == null)
			throw new DocumentBuilderException(Messages.ERROR_TAG4);

		if ( p != null ) {
			current.setStoppingLine(p.line);
			current.setStoppingOffset(p.offset);
		}
		current.setAutoClose(true);

		updateNamespaceScope();

		// Ignore null current node : may be a bad behavior of FastParser ?
		if (stack.size() != 0) {
			stack.pop();
			if (stack.size() != 0) {
				current = (MutableNode) stack.pop();
				stack.push(current);
			}
		}
	}

	/** Reset the namespace definition */
	public void setNameSpace(String prefix, String prefixURI) {
		if (nscontext == null)
			nscontext = new NamespaceContext();
		if (prefix != null)
			nscontext.addPrefixScope(prefix, prefixURI);
		else
			nscontext.pushDefaultNamespace(prefixURI);
		if (current != null) {
			if (prefix != null) {
				current.addNameSpaceDeclaration(prefix, prefixURI);
				if (current.getNameSpacePrefix() != null
						&& prefix.equals(current.getNameSpacePrefix()))
					current.setNameSpace(prefix, prefixURI);
			} else {
				//if ( current.getNameSpacePrefix() == null )
				//	current.setNameSpace(prefix, prefixURI);
				current.setDefaultNamespace(prefixURI);
			}
		}
	}

	FastVector prefixToCheck = null;
	
	/**
	 * Reset attribute for the current node, an exception is thrown for no
	 * current node. Prefix is for namespace
	 */
	public void setAttribute(String prefix, String prefixURI, String att,
			String value) throws DocumentBuilderException {

		if (checkPrefix && nscontext != null && prefix != null
				&& !"xml".equals(prefix)) {
			if (prefixURI == null) {
				if ( prefixToCheck == null )
					prefixToCheck = new FastVector();
				prefixToCheck.add( prefix );

/*				prefixURI = nscontext.getPrefixURI(prefix);
				if (prefixURI == null) {
					throw new DocumentBuilderException(Messages.ERROR_PREFIX2
							+ " " + prefix);
				} */

			}
		}

		if (current != null) {
			String key = att;
			if ( prefix != null )
				key = prefix + ":" + att;
			if ( current.hasAttribute( key ) ) {
				throw new DocumentBuilderException( "Duplicate attribute " + key );
			}
			if ( prefix == null ) {
				current.setNodeAttribute( key, value );
			}
			else {
				current.setNodeAttribute( key, value );
			}
		}
	}

	/** Add a new node for the current node */
	public Object addTextNode(FPParser p,String text) throws DocumentBuilderException {
		MutableNode n = p.getNodeFactory().getTextNode(trimText ? text.trim() : text);

		n.setStartingLine( p.lineText );
		n.setStartingOffset(p.offset - text.length());
		n.setStoppingLine(p.line);
		n.setStoppingOffset(p.offset);

		if ( flatMode )
			flatView.add( n );
		if (current != null)
			current.addNode(n);
		
		return n;
	}

	/** Add a comment node */
	public void addCommentNode(FPParser p,String comment) {
		if (current == null) {
			if (header == null)
				header = new FastVector();
			header.add(p.getNodeFactory().getCommentNode(comment));
		} else
			current.addNode(p.getNodeFactory().getCommentNode(comment));
	}

	/** @return current document */
	public Document getDocument() {
		Document d = doc;
		if (header != null)
			d.setHeaderNode(header);
		d.setRoot(root);
		return d;
	}

	private boolean trimText = false;

	/** Remove start and end white space for text */
	public void trimTextNode(boolean trim) {
		trimText = trim;
	}

}

// DocumentBuilderImpl ends here
