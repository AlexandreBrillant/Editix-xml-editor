package com.japisoft.framework.xml.refactor;

import java.io.File;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import com.japisoft.framework.xml.refactor.elements.RefactorObj;

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
public class Refactor {

	/** Size of the indentation in char number */
	public static int INDENT_SIZE_PROPERTY = 1;
	/** Character for the indentation like a tab or a space */
	public static String INDENT_CHAR_PROPERTY = "\t";

	public static boolean CANONICAL = false;

	/** Preserve whitespaces string for attribute value */
	public static boolean PRESERVE_EMPTY_ATTRIBUTE_VALUE = true;

	private RefactorObj[] params;

	public String refactor(Node startingNode, RefactorObj[] params) {
		StringBuffer sb = new StringBuffer();
		this.params = params;
		if( contextRefactor != null ) {
			// Apply XPath for the contextual node
			startingNode = resolveXPathContext( contextRefactor, startingNode );
		}
		print( 0, startingNode, sb );
		if ( params != null )
			for ( int i = 0; i < params.length; i++ )
				params[ i ].stop();
		return sb.toString();
	}

	private Node resolveXPathContext( String xpathContext, Node startingNode ) {
		if ( xpathContext.startsWith( "/" ) )
			xpathContext = xpathContext.substring( 1 );
		// Extract next path
		int i = xpathContext.indexOf( "/" );
		String pathElement = xpathContext;
		if ( i > -1 )
			pathElement = xpathContext.substring( 0, i );

		// Search for this element
		int childNumber = 0;
		String nodeName = pathElement;
		if ( pathElement.endsWith( "]" ) ) {
			int j = pathElement.indexOf( "[" );
			nodeName = pathElement.substring( 0, j );
			childNumber = Integer.parseInt(
					pathElement.substring( j + 1, pathElement.length() - 1 ) );
		}

		NodeList nl = startingNode.getChildNodes();
		for ( int j = 0; j < nl.getLength(); j++ ) {
			Node n = nl.item( j );
			if ( n.getNodeType() == Node.ELEMENT_NODE ) {
				if ( n.getLocalName().equals( nodeName ) ) {
					childNumber--;
					if ( childNumber <= 0 ) {
						if ( i == -1 )
							return n;
						return resolveXPathContext( 
								xpathContext.substring( i + 1 ), n );
					}
				}
			}
		}
		return startingNode;
	}

	public String refactor( Node startingNode ) {
		return refactor( startingNode, 
							RefactorManager.getRefactors() );
	}

	public String refactor(File documentPath, RefactorObj[] params)
			throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(false);
		factory.setNamespaceAware(true);
		factory.setExpandEntityReferences( false );
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document d = builder.parse(documentPath);
		return refactor(d, params);
	}

	public String refactor(File documentPath) throws Exception {
		return refactor(documentPath, RefactorManager.getRefactors());
	}
	
	public String format( Document source ) throws Exception {
		return refactor(source,null);		
	}

	public String format(File documentPath) throws Exception {
		return refactor(documentPath, null);
	}

	private String contextRefactor;
	
	public void setContext( String contextRefactor ) {
		this.contextRefactor = contextRefactor;
	}

	private String XMLEncoding = null;

	public String getXMLEncoding() {
		if (XMLEncoding == null)
			XMLEncoding = "UTF-8";
		return XMLEncoding;
	}

	private Node refactorIt(Node node) {
		if (node == null)
			return null;
		if (params != null) {
			for (int i = 0; i < params.length; i++) {
				node = params[i].refactor(node);
				//System.out.println( params[ i ] + " node " + node.getNodeType() );
				if (node == null)
					return null;
			}
			return node;
		} else
			return node;
	}

	private Node preRefactorIt(Node node) {
		if (node == null)
			return null;
		if (params != null) {
			for (int i = 0; i < params.length; i++) {
				node = params[i].preRefactor(node);
				if (node == null)
					return null;
			}
			return node;
		} else
			return node;
	}

	public void print(int indent, Node node, StringBuffer sb) {
		if ((node = preRefactorIt(node)) == null) {
			return;
		}
		if ((node = refactorIt(node)) == null) {
			return;
		}
		
		boolean canonical = false;
		int type = node.getNodeType();

		switch ( type ) {

		case Node.DOCUMENT_TYPE_NODE: {
			DocumentType dt = (DocumentType) node;
			sb.append("<!DOCTYPE ");
			sb.append(dt.getName());
			if (dt.getPublicId() == null) {

				if (dt.getSystemId() != null) {
					sb.append(" SYSTEM \"");
					sb.append(dt.getSystemId());
					sb.append("\"");
				}
			} else {

				sb.append(" PUBLIC \"");
				sb.append(dt.getPublicId());
				sb.append("\" ");

				if (dt.getSystemId() != null) {
					sb.append("\"");
					sb.append(dt.getSystemId());
					sb.append("\"");
				}
			}

			if (dt.getInternalSubset() != null) {
				sb.append("[").append(dt.getInternalSubset()).append("]");
			}

			sb.append(">\n");
			break;
		}

		// print document
		case Node.DOCUMENT_NODE: {
			Document d = (Document) node;

			String encoding = null;

/*			if (d instanceof org.apache.xerces.dom.DeferredDocumentImpl) {
				org.apache.xerces.dom.DeferredDocumentImpl ddi = (org.apache.xerces.dom.DeferredDocumentImpl) d;
				encoding = ddi.getEncoding();
			} */

			String version = "1.0";

			sb.append("<?xml version=\"").append(version).append("\"");
			if (encoding != null) {
				sb.append(" encoding=\"").append(encoding).append("\"");
				XMLEncoding = encoding;
			}
			sb.append("?>\n");

			NodeList children = node.getChildNodes();
			for (int iChild = 0; iChild < children.getLength(); iChild++) {
				print(indent, children.item(iChild), sb);
			}
			break;
		}

		// print element with attributes
		case Node.ELEMENT_NODE: {

			if (!(node.getParentNode() instanceof Document))
				for (int i = 0; i < indent; i++) {
					sb.append(INDENT_CHAR_PROPERTY);
				}

			sb.append('<');

			String prefix = node.getPrefix();
			if (prefix != null && prefix.length() > 0) {
				sb.append(prefix);
				sb.append(':');
			}

			if (node.getLocalName() != null)
				sb.append(node.getLocalName());
			else
				sb.append(node.getNodeName());
			Attr attrs[] = sortAttributes(node.getAttributes());
			for (int i = 0; i < attrs.length; i++) {
				Attr attr = attrs[i];
				if ((attr = (Attr) refactorIt(attr)) == null)
					continue;
				sb.append(' ');
				sb.append(attr.getNodeName());
				sb.append("=\"");
				sb.append(normalize(attr.getNodeValue(),
						PRESERVE_EMPTY_ATTRIBUTE_VALUE));
				sb.append('"');
			}
			if (!node.hasChildNodes())
				sb.append("/>\n");
			else {
				// Not a text child node
				if ( !(node.getChildNodes().getLength() == 1 && node
						.getChildNodes().item(0) instanceof Text ) ) {
					sb.append(">\n");
				} else
					sb.append(">");
			}
			NodeList children = node.getChildNodes();
			if (children != null) {
				int len = children.getLength();
				for (int i = 0; i < len; i++) {
					print(indent + INDENT_SIZE_PROPERTY, children.item( i ), sb);
				}
			}
			break;
		}

		// handle entity reference nodes
		case Node.ENTITY_REFERENCE_NODE : {
			if (canonical) {
				NodeList children = node.getChildNodes();
				if (children != null) {
					int len = children.getLength();
					for (int i = 0; i < len; i++) {
						sb.append(children.item(i));
					}
				}
			} else {
				sb.append('&');
				sb.append(node.getNodeName());
				sb.append(';');
			}
			break;
		}

		// print cdata sections
		case Node.CDATA_SECTION_NODE: {
			if (canonical) {
				sb.append(normalize(node.getNodeValue(), true));
			} else {
				sb.append("<![CDATA[");
				sb.append(node.getNodeValue());
				sb.append("]]>");
			}
			break;
		}

		// print text
		case Node.TEXT_NODE: {
			sb.append(normalize(node.getNodeValue(), false));
			break;
		}

		// print processing instruction
		case Node.PROCESSING_INSTRUCTION_NODE: {
			sb.append("<?");
			sb.append(node.getNodeName());
			String data = node.getNodeValue();
			if (data != null && data.length() > 0) {
				sb.append(' ');
				sb.append(data);
			}
			sb.append("?>");
			break;
		}

		case Node.COMMENT_NODE: {
			sb.append("<!--");
			sb.append(node.getNodeValue());
			sb.append("-->\n");
			break;
		}
		}

		if (type == Node.ELEMENT_NODE) {

			if (node.hasChildNodes()) {
				if (!(node.getParentNode() instanceof Document)) {
					// Except if the node has only text inside
					NodeList l = node.getChildNodes();
					if (!(l.getLength() == 1 && node.getFirstChild() instanceof Text)) {
						for (int i = 0; i < indent; i++) {
							sb.append(INDENT_CHAR_PROPERTY);
						}
					}
				}
				sb.append("</");
				if (node.getPrefix() != null)
					sb.append(node.getPrefix() + ":");
				if (node.getLocalName() != null)
					sb.append(node.getLocalName());
				else
					sb.append(node.getNodeName());
				sb.append(">\n");
			}
		}

	}

	private boolean replaceLt = true;
	private boolean replaceGt = true;
	private boolean replaceAmp = true;
	private boolean replaceQuote = true;
	private boolean replaceAPos = true;

	/** @return true if the amp character is resolved as entity */
	public boolean isReplaceAmp() {
		return replaceAmp;
	}

	/** Resolve the amp character as entity. By default <code>true</code> */
	public void setReplaceAmp(boolean replaceAmp) {
		this.replaceAmp = replaceAmp;
	}

	/** @return true if the ' character is resolved as entity */
	public boolean isReplaceAPos() {
		return replaceAPos;
	}

	/** Resolve the ' character as entity. By default <code>true</code> */
	public void setReplaceAPos(boolean replaceAPos) {
		this.replaceAPos = replaceAPos;
	}

	/** @return true if the gt character is resolved as entity */
	public boolean isReplaceGt() {
		return replaceGt;
	}

	/**
	 * Resolved the gt character is resolved as entity. By default
	 * <code>true</code>
	 */
	public void setReplaceGt(boolean replaceGt) {
		this.replaceGt = replaceGt;
	}

	/** @return true if the lt character is resolved as entity */
	public boolean isReplaceLt() {
		return replaceLt;
	}

	/** Resolved the lt character as entity. By default <code>true</code> */
	public void setReplaceLt(boolean replaceLt) {
		this.replaceLt = replaceLt;
	}

	/** @return true if the " character is resolved as entity */
	public boolean isReplaceQuote() {
		return replaceQuote;
	}

	/** Resolved the " character as entity. By default <code>true</code> */
	public void setReplaceQuote(boolean replaceQuote) {
		this.replaceQuote = replaceQuote;
	}

	/** Normalizes the given string. */
	protected String normalize(String s, boolean keepWhiteSpace) {
		StringBuffer str = new StringBuffer();
		boolean canonical = CANONICAL;
		boolean empty = true;
		boolean ignore = true;

		int len = (s != null) ? s.length() : 0;
		int i = 0;
		while (i < len) {
			char ch = s.charAt(i);
			if (ch != ' ' && ch != '\n')
				empty = false;
			if ((ch == ' ' && keepWhiteSpace) || ch == '\n')
				ignore = ignore && true;
			else
				ignore = false;

			switch (ch) {
			case '<': {
				if (isReplaceLt())
					str.append("&lt;");
				else
					str.append(ch);
				break;
			}
			case '>': {
				if (isReplaceGt())
					str.append("&gt;");
				else
					str.append(ch);
				break;
			}
			case '&': {
				if (i + 4 < len
						&& (s.charAt(i + 1) == '#' && s.charAt(i + 2) == '1'
								&& s.charAt(i + 3) == '0' && s.charAt(i + 4) == ';')) {
					str.append("&#10;");
					i += 4;
				} else {
					if (isReplaceAmp())
						str.append("&amp;");
					else
						str.append(ch);
				}

				break;
			}
			case '"': {
				if (isReplaceQuote())
					str.append("&quot;");
				else
					str.append(ch);
				break;
			}
			case '\'': {
				if (isReplaceAPos())
					str.append("&apos;");
				else
					str.append(ch);
				break;
			}
			case '\r':
			case '\n': {
				if (canonical) {
					str.append("&#");
					str.append(Integer.toString(ch));
					str.append(';');
					break;
				}
				// else, default append char
			}
			default: {
				if (!ignore)
					str.append(ch);
			}
			}

			i++;
		}

		if (empty) {
			if (keepWhiteSpace)
				return s;
			return "";
		}

		String _s = str.toString();
		int m = -1;

		for (i = (_s.length() - 1); i >= 0; i--) {
			if (_s.charAt(i) == ' ' || _s.charAt(i) == '\t'
					|| _s.charAt(i) == '\n' || _s.charAt(i) == '\r') {
				m = i;
			} else
				break;
		}

		if (m != -1)
			return _s.substring(0, m);

		return _s;

	} // normalize(String):String

	/** Returns a sorted list of attributes. */
	protected Attr[] sortAttributes(NamedNodeMap attrs) {

		int len = (attrs != null) ? attrs.getLength() : 0;
		ArrayList l = new ArrayList();

		// Attr array[] = new Attr[len];
		for (int i = 0; i < len; i++) {
			// array[i] = (Attr) attrs.item(i);
			// if (attrs.item(i) instanceof AttrNSImpl)
			l.add(attrs.item(i));
		}

		Attr[] array = new Attr[l.size()];
		for (int i = 0; i < l.size(); i++)
			array[i] = (Attr) l.get(i);
		len = array.length;

		for (int i = 0; i < len - 1; i++) {
			String name = array[i].getNodeName();
			int index = i;
			for (int j = i + 1; j < len; j++) {
				String curName = array[j].getNodeName();
				if (curName.compareTo(name) < 0) {
					name = curName;
					index = j;
				}
			}
			if (index != i) {
				Attr temp = array[i];
				array[i] = array[index];
				array[index] = temp;
			}
		}
		return (array);
	}

}
