package com.japisoft.dtdparser.document;

import com.japisoft.dtdparser.CannotFindElementException;
import com.japisoft.dtdparser.node.*;

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
public class DefaultDTDDocumentBuilder extends AbstractDTDDocumentBuilder {

	public DefaultDTDDocumentBuilder() {
		super();
	}

	public void notifyStartDTD() {
		if ( root == null )
			root = ( RootDTDNode )factory.getNodeForType( DTDNode.ROOT );
	}

	public void notifyStopDTD() {
		// NOP
	}

	private StringBuffer storedComment = null;

	public void notifyComment( String comment ) {
		// Bound the comment to the current element
		if ( storedComment == null )
			storedComment = new StringBuffer();
		storedComment.append( comment );	
	}

	// Particular case for the first element
	private void flushStoredComment() {
		if ( storedComment != null && 
				currentNode != null ) {
			currentNode.addNodeComment( 
					storedComment.toString() );
			storedComment = null;
		}
	}

	public void notifyEntity(
		String entity,
		boolean parameter,
		int type,
		String value) {
		EntityDTDNode node =
			(EntityDTDNode) factory.getNodeForType(DTDNode.ENTITY, entity);
		currentNode = node;
		flushStoredComment();
		node.setValue(value);
		if (INTERNAL_ENTITY == type)
			node.setType(EntityDTDNode.INTERNAL_ENTITY);
		else if (SYSTEM_ENTITY == type)
			node.setType(EntityDTDNode.SYSTEM_ENTITY);
		else if (PUBLIC_ENTITY == type)
			node.setType(EntityDTDNode.PUBLIC_ENTITY);
		node.setParameter(parameter);
		root.addDTDNode(node);
	}

	private DTDNode currentNode = null;
	private ElementDTDNode currentElement = null;
	private ElementDTDNode lastProcessedElement = null;
	private ElementDTDNode openElement = null;

	public void notifyStartElement(String e) {
		currentElement =
			(ElementDTDNode) factory.getNodeForType(DTDNode.ELEMENT, e);
		currentNode = currentElement;
		flushStoredComment();
		if ( firstElement == null )
			firstElement = e;
		
		root.addDTDNode(currentElement);
		lastProcessedElement = currentElement;
		openElement = currentElement;
	}

	public void notifyStopElement() {
		// NOP
	}

	/** Item equals element name or #PCDATA */
	public void notifyElementChoiceItem(String item) {
		if ( item == null ) {
			
			if ( currentElement instanceof ElementSetDTDNode ) {
				((ElementSetDTDNode) currentElement).setType(
						ElementSetDTDNode.CHOICE_TYPE);
			}
			
		} else {
		
		ElementDTDNode element =
			(ElementDTDNode) factory.getNodeForType(DTDNode.ELEMENT_REF, item);

		if ("#PCDATA".equals(item))
			element.setPCDATA(true);

		currentElement.addDTDNode(element);
		((ElementSetDTDNode) currentElement).setType(
			ElementSetDTDNode.CHOICE_TYPE);
		lastProcessedElement = element;
		}
	}

	/** Item equals element name or EMPTY or ANY or #PCDATA */
	public void notifyElementIncludeItem(String item) {
		if ( item == null ) {
			// Get the previous added

			if ( currentElement instanceof ElementSetDTDNode ) {
				((ElementSetDTDNode) currentElement).setType(
						ElementSetDTDNode.SEQUENCE_TYPE);
			}
			
		} else {
			
			ElementDTDNode element =
				(ElementDTDNode) factory.getNodeForType(DTDNode.ELEMENT_REF, item);
			boolean ok = true;
	
			if ("EMPTY".equals(item)) {
				openElement.setEmptyElement(true);
				ok = false;
			} else if ("ANY".equals(item)) {
				openElement.setANY(true);
				ok = false;
			} else if ("#PCDATA".equals(item)) {
				element.setPCDATA(true);
			}
	
			if (ok) {
				currentElement.addDTDNode(element);
				if (currentElement instanceof ElementSetDTDNode)
					((ElementSetDTDNode) currentElement).setType(
						ElementSetDTDNode.SEQUENCE_TYPE);
				lastProcessedElement = element;
			} 
		}
	}

	/** Notify '(' meet for the element declaration */
	public void notifyStartElementChildren() {
		ElementDTDNode element =
			(ElementDTDNode) factory.getNodeForType(DTDNode.ELEMENT_SET);
		element.setDTDParentNode(currentElement);
		currentElement.addDTDNode(element);
		currentElement = element;
		lastProcessedElement = element;
	}

	/** Notify operator '+' or '*' or '?' */
	public void notifyOperator(char op) {
		if (op == '+') {
			lastProcessedElement.setOperator(
				ElementDTDNode.ONE_MORE_ITEM_OPERATOR);
		} else if (op == '*')
			lastProcessedElement.setOperator(
				ElementDTDNode.ZERO_MORE_ITEM_OPERATOR);
		else if (op == '?')
			lastProcessedElement.setOperator(
				ElementDTDNode.ZERO_ONE_ITEM_OPERATOR);
	}

	/** Notify ')' meet for the element declaration */
	public void notifyStopElementChildren() {
		lastProcessedElement = currentElement;
		currentElement = (ElementDTDNode) currentElement.getDTDParentNode();
	}

	public void notifyAttribute(
		String element,
		String id,
		int valueType,
		String[] enume,
		int attDec,
		String def) throws CannotFindElementException {
		AttributeDTDNode att =
			(AttributeDTDNode) factory.getNodeForType(DTDNode.ATTRIBUTE, id);
		currentNode = att;
		att.setType(valueType);
		att.setUsage(attDec);
		att.setDefaultValue(def);
		att.setEnumeration(enume);

		// Search for the element node
		ElementDTDNode dtdelement =
			((RootDTDNode) root).getElementDefinitionByName(element);
		if (dtdelement != null) {
			if ( storedComment != null ) {
				dtdelement.addAttComment( storedComment.toString() );
				storedComment = null;
			}
			dtdelement.addDTDNode(att);
		}
	}

	private String firstElement = null;

	/** @return the name of the first element */
	public String getFirstElement() {
		return firstElement;
	}	

}

// DefaultDTDDocumentBuilder ends here
