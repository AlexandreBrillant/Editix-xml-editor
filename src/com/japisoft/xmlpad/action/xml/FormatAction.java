package com.japisoft.xmlpad.action.xml;

import java.io.*;
import java.util.ArrayList;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import com.japisoft.xmlpad.XMLContainer;
import com.japisoft.xmlpad.action.Properties;
import com.japisoft.xmlpad.action.XMLAction;
import com.japisoft.xmlpad.xml.validator.DefaultValidator;
import com.japisoft.xmlpad.xml.validator.Validator;

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
public class FormatAction extends XMLAction {

	/** Particular property inside the XMLContainer */
	public static final String PREF_APOSENTITY = "prefAPosEntity";

	public static final String ID = FormatAction.class.getName();

	public static boolean CANONICAL = false;

	/** Mode for having a popup dialog when the formatting is impossible */
	public static boolean DIALOG_ERROR = true;

	/** Preserve whitespaces string for attribute value */
	public static boolean PRESERVE_EMPTY_ATTRIBUTE_VALUE = true;

	public FormatAction() {
		super();
	}

	private Integer indentSize = new Integer(4);

	private Character indentChar = new Character(' ');

	/** Support for INDENT_CHAR_PROPERTY, INDENT_SIZE_PROPERTY properties */
	public void setProperty(String propertyName, Object value) {
		if (Properties.INDENT_SIZE_PROPERTY.equals(propertyName)) {
			if (value instanceof Integer)
				indentSize = ((Integer) value);
		} else if (Properties.INDENT_CHAR_PROPERTY.equals(propertyName)) {
			if (value instanceof Character) {
				indentChar = ((Character) value);
			} else if (value instanceof String) {
				if (value.toString().length() > 0)
					indentChar = new Character(value.toString().charAt(0));
			}
		} else
			super.setProperty(propertyName, value);
	}

	public Object getProperty(String propertyName, Object defaultValue) {
		if (Properties.INDENT_CHAR_PROPERTY.equals(propertyName))
			return indentChar;
		else if (Properties.INDENT_SIZE_PROPERTY.equals(propertyName)) {
			return indentSize;
		}
		return super.getProperty(propertyName, defaultValue);
	}

	public boolean notifyAction() {
		DefaultValidator mA = new DefaultValidator(true, false);
		if (mA.validate(container,false) == Validator.ERROR) {
			if (DIALOG_ERROR)
				JOptionPane.showMessageDialog(container.getView(),
						getMessageForError(container));
			return XMLAction.INVALID_ACTION;
		} else {

			Boolean b = (Boolean) container.getProperty(PREF_APOSENTITY);
			if (b != null)
				setReplaceAPos(b.booleanValue());

			return formatAction(mA);
		}
	}

	public String getMessageForError(XMLContainer container) {
		return container.getLocalizedMessage("FORMAT_ERROR",
				"Can't format with parsing error(s)");
	}

	private boolean formatAction(DefaultValidator mA) {
		try {
			if (!mA.hasError()) {
				if (mA.getDocument() == null)
					return INVALID_ACTION;
				return formatAction(mA.getDocument());
			} else
				return INVALID_ACTION;
		} finally {
			mA.dispose();
		}
	}

	public boolean formatAction(Node node) {
		try {
			StringWriter osw = new StringWriter();
			out = new PrintWriter(osw);

			ignoreProcessingInstruction = true;
			print(node);
			out.flush();

			// Save the text until the first tag

			String txt = editor.getText();
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < txt.length(); i++) {
				if (txt.charAt(i) == '<') {
					if (i + 1 < txt.length()) {
						if (!((txt.charAt(i + 1) == '!') || 
								txt.charAt(i + 1) == '?')) {
							break;
						}
					}
				}

				sb.append(txt.charAt(i));

				if (txt.charAt(i) == '>') {
					if (i > 0 && txt.charAt(i - 1) == '?') {
						boolean toAdd = false;
						for (int ii = i + 1; ii < txt.length(); ii++) {
							if (txt.charAt(ii) == '\n')
								break;
							else if (txt.charAt(ii) == '<') {
								toAdd = true;
								break;
							}
						}
						if (toAdd)
							sb.append("\n");
					}
				}
			}

			container.setText(sb.toString() + osw.toString(), false );

		} catch (Throwable th) {
			if ("true".equals(System.getProperty("xmlpad.debug")))
				th.printStackTrace();
			return INVALID_ACTION;
		}
		return VALID_ACTION;
	}

	// ////////////////////////////////////////////////////////

	private static String PRINTWRITER_ENCODING = "UTF8";

	public static String getWriterEncoding() {
		return (PRINTWRITER_ENCODING);
	}

	protected PrintWriter out;

	public void print(Node node) {
		print(0, node);
	}

	private int getIndentSpace() {
		return indentSize.intValue();
	}

	private boolean ignoreProcessingInstruction = false;

	/** Prints the specified node, recursively. */
	public void print(int indent, Node node) {

		// is there anything to do?
		if (node == null) {
			return;
		}

		boolean canonical = false;

		int type = node.getNodeType();
		switch (type) {
		// print document
		case Node.DOCUMENT_NODE: {

			NodeList children = node.getChildNodes();
			for (int iChild = 0; iChild < children.getLength(); iChild++) {
				if (!(children.item(iChild).getNodeType() == Node.COMMENT_NODE))
					print(children.item(iChild));
			}
			out.flush();
			break;
		}

		// print element with attributes
		case Node.ELEMENT_NODE: {
			ignoreProcessingInstruction = false;

			if (!(node.getParentNode() instanceof Document))
				for (int i = 0; i < indent; i++) {
					out.print(indentChar.charValue());
				}

			out.print('<');

			String prefix = node.getPrefix();
			if (prefix != null && prefix.length() > 0) {
				out.print(prefix);
				out.print(':');
			}

			if ( node.getLocalName() != null )
				out.print( node.getLocalName() );
			else
				out.print( node.getNodeName() );
			Attr attrs[] = sortAttributes(node.getAttributes());
			String lastAttr = null;
			for (int i = 0; i < attrs.length; i++) {
				Attr attr = attrs[i];
				if ( attr.getNodeName().equals( lastAttr ) )
					continue;
				lastAttr = attr.getNodeName();
				out.print(' ');
				out.print(attr.getNodeName());
				out.print("=\"");
				out.print(
					normalize( attr.getNodeValue(),
									PRESERVE_EMPTY_ATTRIBUTE_VALUE ) );
				out.print('"');
			}

			if (!node.hasChildNodes())
				out.print("/>\n");
			else {
				// Not a text child node
				if (!(node.getChildNodes().getLength() == 1 && node
						.getChildNodes().item(0) instanceof Text)) {
					out.print(">\n");
				} else
					out.print(">");
			}
			NodeList children = node.getChildNodes();
			if (children != null) {
				int len = children.getLength();
				for (int i = 0; i < len; i++) {
					print(indent + getIndentSpace(), children.item(i));
				}
			}
			break;
		}

		// handle entity reference nodes
		case Node.ENTITY_REFERENCE_NODE: {
			if (canonical) {
				NodeList children = node.getChildNodes();
				if (children != null) {
					int len = children.getLength();
					for (int i = 0; i < len; i++) {
						print(children.item(i));
					}
				}
			} else {
				out.print('&');
				out.print(node.getNodeName());
				out.print(';');
			}
			break;
		}

		// print cdata sections
		case Node.CDATA_SECTION_NODE: {
			if (canonical) {
				out.print(normalize(node.getNodeValue(), true));
			} else {
				out.print("<![CDATA[");
				out.print(node.getNodeValue());
				out.print("]]>");
			}
			break;
		}

		// print text
		case Node.TEXT_NODE: {
			out.print(normalize(node.getNodeValue(), false));
			break;
		}

		// print processing instruction
		case Node.PROCESSING_INSTRUCTION_NODE: {

			if (!ignoreProcessingInstruction) {
				out.print("<?");
				out.print(node.getNodeName());
				String data = node.getNodeValue();
				if (data != null && data.length() > 0) {
					out.print(' ');
					out.print(data);
				}
				out.println("?>");
			}
			break;
		}

		case Node.COMMENT_NODE: {
			out.print("<!--");
			out.print(node.getNodeValue());
			out.print("-->\n");
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
							out.print(indentChar.charValue());
						}
					}
				}
				out.print("</");
				if (node.getPrefix() != null)
					out.print(node.getPrefix() + ":");
				if ( node.getLocalName() != null )
					out.print(node.getLocalName());
				else
					out.print(node.getNodeName());
				out.print(">\n");
			}
		}

		out.flush();

	} // print(Node)

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

// FormatAction ends here
