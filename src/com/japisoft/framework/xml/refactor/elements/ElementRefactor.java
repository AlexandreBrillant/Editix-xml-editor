package com.japisoft.framework.xml.refactor.elements;

import java.util.ArrayList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.framework.xml.refactor.ui.RefactorTable;

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
public class ElementRefactor extends AbstractRefactor {
	private static final String RENAME_ACTION = "(V1) RENAME TO (V2)";

	private static final String DELETE_ACTION = "DELETE (V1)";

	private static final String ADD_ATTR_ACTION = "ADD ATTR(V2) to (V1)";
	
	private static final String E2ATT_ACTION = "(V1) : ATTR(S) TO ELEMENT(S)";

	private static final String ATT2E_ACTION = "(V1) : TO ATTR(S)";

	public static String[] ACTIONS = new String[] { RENAME_ACTION,
			DELETE_ACTION, ADD_ATTR_ACTION, E2ATT_ACTION, ATT2E_ACTION };

	public ElementRefactor() {
		super(Node.ELEMENT_NODE);
	}

	public String[] getActions() {
		return ACTIONS;
	}

	public String getName() {
		return "Element";
	}

	protected Node preRefactorIt(Node node, RefactorAction ra) {
		if (ATT2E_ACTION.equals(ra.getAction())) {
			// Check for children
			NodeList nl = node.getChildNodes();
			ArrayList mustDelete = null;
			for (int i = 0; i < nl.getLength(); i++) {
				if (ra.matchOldValue(nl.item(i).getLocalName())) {
					// Delete it and set it as attribute
					if (mustDelete == null)
						mustDelete = new ArrayList();
					mustDelete.add(nl.item(i));
				}
			}
			if (mustDelete != null) {
				for (int i = 0; i < mustDelete.size(); i++) {
					Node n = (Node) mustDelete.get(i);
					node.removeChild((Node) mustDelete.get(i));
					Element e = (Element) node;
					String attName = ((Node) mustDelete.get(i)).getLocalName();
					if (e.getAttributeNode(attName) != null) {
						String newName = attName;
						for (int j = 2; j < 100; j++) {
							attName = newName + j;
							if (e.getAttributeNode(attName) == null)
								break;
						}
					}
					e.setAttribute(attName, ((Node) mustDelete.get(i))
							.getTextContent());
				}
			}
		}
		return node;
	}

	protected Node refactorIt(Node node, RefactorAction ra) {
		Element e = (Element) node;
		if (RENAME_ACTION.equals(ra.getAction())) {
			if (ra.matchOldValue(node.getLocalName())) {
				ElementProxyNode newNode = new ElementProxyNode(e);
				if (!ra.isNewValueEmpty()) {
					newNode.setNewLocalName(ra.getNewValue());
					return newNode;
				}
			}
		} else if (DELETE_ACTION.equals(ra.getAction())) {
			if (node.getLocalName().equals(ra.getOldValue())) {
				return null;
			}
		} else
			if ( ADD_ATTR_ACTION.equals( ra.getAction() ) ) {
				if (ra.matchOldValue(node.getLocalName())) {
					if ( !ra.isNewValueEmpty() ) {
						String v2 = ra.getNewValue();
						int i = v2.indexOf( "=" );
						if ( i > -1 ) {
							e.setAttribute( v2.substring( 0, i ), v2.substring( i + 1 ) );
						} else {
							e.setAttribute( v2, "" );
						}
					}
				}
			} else		
		if (E2ATT_ACTION.equals(ra.getAction())) {

			if (ra.matchOldValue(node.getLocalName())) {

				NamedNodeMap nnm = node.getAttributes();
				if (nnm != null) {
					for (int i = 0; i < nnm.getLength(); i++) {

						Node n = nnm.item(i);

						if (n.getNodeName().indexOf(":") == -1) {

							Document d = node.getOwnerDocument();
							Element e2 = d.createElement(n.getNodeName());
							e2.appendChild(d.createTextNode(n.getNodeValue()));
							node.appendChild(e2);

						}

					}

					// Remove it

					for (int i = 0; i < nnm.getLength(); i++) {

						Node n = nnm.item(i);

						if (n.getNodeName().indexOf(":") == -1) {

							i--;
							nnm.removeNamedItem(n.getNodeName());
						}

					}

				}

			}

		}
		return node;
	}

	public void initTable( RefactorTable table, FPNode context ) {
		table.init( 0, context.getContent() );
	}

}
