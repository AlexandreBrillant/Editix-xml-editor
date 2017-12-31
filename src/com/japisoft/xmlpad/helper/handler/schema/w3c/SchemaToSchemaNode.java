package com.japisoft.xmlpad.helper.handler.schema.w3c;

import java.util.Calendar;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.Hashtable;

import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.framework.xml.parser.walker.AndCriteria;
import com.japisoft.framework.xml.parser.walker.AttributeCriteria;
import com.japisoft.framework.xml.parser.walker.NodeNameCriteria;
import com.japisoft.framework.xml.parser.walker.OrCriteria;
import com.japisoft.framework.xml.parser.walker.TreeWalker;
import com.japisoft.xmlpad.helper.model.AttDescriptor;
import com.japisoft.xmlpad.helper.model.SchemaNode;
import com.japisoft.xmlpad.helper.model.SchemaNodeProducer;
import com.japisoft.xmlpad.helper.model.TagDescriptor;

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
public class SchemaToSchemaNode implements SchemaNodeProducer {

	private Hashtable htPrefixNamespaces = new Hashtable();

	public SchemaNode getSchemaNode(Object element) {
		FPNode node = (FPNode) element;
		FPNode schemaRoot = (FPNode) node.getDocument().getRoot();
		SchemaNode root = new SchemaNode(SchemaNode.ROOT);
		root.namespace = schemaRoot.getAttribute("targetNamespace");

		for (int i = 0; i < schemaRoot.getViewAttributeCount(); i++) {
			String attName = schemaRoot.getViewAttributeAt(i);
			if (attName.startsWith("xmlns:")) {
				String value = schemaRoot.getViewAttribute(attName);
				String prefix = attName.substring(7);
				htPrefixNamespaces.put(prefix, value);
			}
		}
		processElement(node, root, false);
		return root;
	}

	public void processElement(FPNode element, SchemaNode node,
			boolean attributeMode) {
		String name = element.getAttribute("name");
		if (name == null) {
			name = element.getAttribute("ref");
		}

		if (name != null) {
			int i = 0;
			if ((i = name.indexOf(":")) > -1) {
				try {
					name = name.substring(i + 1);
				} catch (IndexOutOfBoundsException exc) {
				}
			}
		}

		node.element = new TagDescriptor(name, true);

		// Search comment inside the element
				
		String comment = getAnnotation( element );
		
		FPNode def = getElementDefinition(element);
		
		if ( comment == null && def != null ) {
			comment = getAnnotation( def );
		}
		
		if ( comment == null ) {	// Check for reference two
			if ( element.hasAttribute( "ref" ) ) {
				FPNode refNode = resolveElementRefence( element, element.getAttribute( "ref" ) );
				if ( refNode != null ) {
					comment = getAnnotation( refNode );
				}
			}
		}

		node.element.setComment( comment );
		
		if (def != null) {
			processType(def, node, attributeMode);
		} else { // primitive type found like xs:string
			node.element.empty = false;
		}

		// Search the element with the same substitutionGroup to this element

		if (attributeMode && name != null) {
			TreeWalker tw = new TreeWalker((FPNode) element.getDocument()
					.getRoot());
			Enumeration enume = tw.getNodeByCriteria(new AndCriteria(
					new NodeNameCriteria("element"), new AttributeCriteria(
							"substitutionGroup", name, true)), false);
			while (enume.hasMoreElements()) {
				FPNode elementNode = (FPNode) enume.nextElement();
				if (elementNode != element) {
					SchemaNode tmpElement = new SchemaNode(SchemaNode.ELEMENT);
					processElement(elementNode, tmpElement, attributeMode);
					node.element.addSynonymousTagDescriptor(tmpElement.element);
				}
			}
		}
	}

	private void processType(FPNode type, SchemaNode node,
			boolean attributeMode) {
		if (type.matchContent("complexType")) {
			if ("true".equals(type.getAttribute("mixed"))) {
				if (node.isElement())
					node.element.empty = false;
			}
			processComplexType(type, node, attributeMode);
		} else if (type.matchContent("simpleType")) {
			if (node.isElement())
				node.element.empty = false;
		}
	}

	// complexType content :
	// (annotation?, (simpleContent | complexContent | ((group | all | choice |
	// sequence)?, ((attribute | attributeGroup)*, anyAttribute?))))
	private void processComplexType(FPNode complexType, SchemaNode node,
			boolean attributeMode) {

		for (int i = 0; i < complexType.childCount(); i++) {
			FPNode child = complexType.childAt(i);

			if (child.matchContent("complexContent")
					|| child.matchContent("simpleContent")
					|| child.matchContent("restriction")
					|| child.matchContent("extension")) {
				processComplexType(child, node, attributeMode);
			} else if (child.matchContent("attribute")) {
				processAttribute(child, node);
			} else if (child.matchContent("attributeGroup")) {
				processAttributeGroup(child, node);
			} else {

				if (child.matchContent("group")) {
					node.element.empty = false;
					if (!attributeMode)
						processGroup(complexType, node);
				} else if (child.matchContent("all")) {
					node.element.empty = false;
					if (!attributeMode)
						processAll(child, node);
				} else if (child.matchContent("choice")) {
					node.element.empty = false;
					if (!attributeMode)
						processChoice(child, node);
				} else if (child.matchContent("sequence")) {
					node.element.empty = false;
					if (!attributeMode)
						processSequence(child, node);
				}
			}
		}
	}

	private void processGroup(FPNode group, SchemaNode node) {
		String ref = group.getAttribute("ref");
		if (ref != null) {
			// Search the group id
			TreeWalker tw = new TreeWalker((FPNode) group.getDocument()
					.getRoot());
			FPNode foundGroup = tw.getOneNodeByCriteria(new AndCriteria(
					new NodeNameCriteria("group"), new OrCriteria(
							new AttributeCriteria("name", ref, true),
							new AttributeCriteria("id", ref))), false);
			if (foundGroup != null)
				group = foundGroup;
		}
		processComplexType(group, node, false);
	}

	public boolean allattributeMode = false;

	private void processAttribute(FPNode attribute, SchemaNode node) {
		String ref = attribute.getAttribute("ref");
		boolean required = "required".equals(attribute.getAttribute("use"));

		AttDescriptor addedDescriptor = null;
		FPNode fromNode = attribute;

		if (required || allattributeMode) {

			if (ref != null) {
				TreeWalker tw = new TreeWalker((FPNode) attribute
						.getDocument().getRoot());
				FPNode foundType = tw.getOneNodeByCriteria(new AndCriteria(
						new NodeNameCriteria("attribute"),
						new AttributeCriteria("name", ref)), false);

				if (foundType != null) {
					fromNode = foundType;
					node.element
							.addAttDescriptor(addedDescriptor = getDescriptorForNode( foundType ) );
				}

			} else {
				node.element
						.addAttDescriptor(addedDescriptor = getDescriptorForNode( attribute ));
			}
		}

		if (addedDescriptor != null) {
			prepareEnumValuesForAttributeNode(fromNode, addedDescriptor);
		}
	}
	
	private String getAnnotation( FPNode node ) {

		if ( !node.isLeaf() ) {			
			FPNode n = node.childAt(0 );
			if ( n.matchContent( "annotation" ) ) {
				// Search for documentation inside
				if ( !n.isLeaf() ) {
					StringBuffer sb = null;
					for ( int i = 0; i < n.childCount(); i++ ) {
						FPNode m = n.childAt( i );
						if ( m.matchContent( "documentation" ) ) {
							if ( sb == null )
								sb = new StringBuffer();
							if ( !m.isLeaf() ) {
								sb.append( m.childAt( i ) );
							}
						}
					}
					if ( sb != null )
						return sb.toString();
					else
						return null;
				} else
					return null;
			} else
				return null;
		} else
			return null;

	}

	private AttDescriptor getDescriptorForNode( FPNode attribute ) {
		boolean required = "required".equals( attribute.getAttribute( "use" ) );
		String defaultValue = attribute.getAttribute( "default" );
		AttDescriptor att =  
			new AttDescriptor( 
					attribute.getAttribute( "name" ),
					defaultValue,
					required );
		
		att.setComment( getAnnotation( attribute ) );
		return att;
	}

	String f( int i ) {
		if ( i < 10 )
			return "0" + i;
		else
			return "" + i;
	}
	
	private void prepareEnumValuesForAttributeNode(FPNode attribute,
			AttDescriptor descriptor) {
		String type = attribute.getAttribute( "type" );
		if ( (type != null) && type.indexOf( ":" ) > -1 ) {
			
			if ( type.endsWith( "boolean" ) ) {
				descriptor.addEnumValue( "false" );
				descriptor.addEnumValue( "true" );
			} else {
								
				GregorianCalendar cal = ( GregorianCalendar )GregorianCalendar.getInstance();
				
				String m = f( cal.get( Calendar.MONTH ) );
				String d = f( cal.get( Calendar.DAY_OF_MONTH ) );
				String th = f( cal.get( Calendar.HOUR ) );
				String tm = f( cal.get( Calendar.MINUTE ) );
				String ts = f( cal.get( Calendar.SECOND ) );

				if ( type.endsWith( "dateTime" ) ) {

					String basicDateTime = cal.get( Calendar.YEAR ) + "-" + m + "-" + d + "T" + th + ":" + tm + ":" + ts; 
					descriptor.addEnumValue( basicDateTime );
					for ( int i = -12; i < 12; i++ ) {

						int ii = Math.abs( i );
						String si = ( ii < 10 ) ? ( "0" + ii) : ( "" + ii );
						if ( i < 0 )
							si = "-" + si;
						else
							si = "+" + si;

						descriptor.addEnumValue( basicDateTime + si + ":00" );

					}

				} else
				if ( type.endsWith( "time" ) ) {

					String basicDateTime = th + ":" + tm + ":" + ts; 
					descriptor.addEnumValue( basicDateTime );
					for ( int i = -12; i < 12; i++ ) {

						int ii = Math.abs( i );
						String si = ( ii < 10 ) ? ( "0" + ii ) : ( "" + ii);
						if ( i < 0 )
							si = "-" + si;
						else
							si = "+" + si;

						descriptor.addEnumValue( basicDateTime + si + ":00" );
					}
										
				} else
				if ( type.endsWith( "date" ) ) {

					String basicDateTime = cal.get( Calendar.YEAR ) + "-" + m + "-" + d; 
					descriptor.addEnumValue( basicDateTime );
					for ( int i = -12; i < 12; i++ ) {

						int ii = Math.abs( i );
						String si = ( ii < 10 ) ? ( "0" + ii ) : ( "" + ii );
						if ( i < 0 )
							si = "-" + si;
						else
							si = "+" + si;

						descriptor.addEnumValue( basicDateTime + si + ":00" );
					}
					
				} else
				if ( type.endsWith( "gYearMonth" ) ) {

					String basicDateTime = cal.get( Calendar.YEAR ) + "-" + m;					
					descriptor.addEnumValue( basicDateTime );
					
				} else
				if ( type.endsWith( "gYear" ) ) {
					
					String basicDateTime = cal.get( Calendar.YEAR ) + "";
					descriptor.addEnumValue( basicDateTime );
					
				} else
				if ( type.endsWith( "gMonthDay" ) ) {

					String basicDateTime = "--" + m + "-" + d;					
					descriptor.addEnumValue( basicDateTime );
										
				} else
				if ( type.endsWith( "gDay" ) ) {

					String basicDateTime = "---" + d + "";					
					descriptor.addEnumValue( basicDateTime );
										
				} else
				if ( type.endsWith( "gMonth" ) ) {

					String basicDateTime = m + "";					
					descriptor.addEnumValue( basicDateTime );
										
				}
			}
			return;
		}

		FPNode simpleType = null;

		// Check for local definition

		if (attribute.childCount() > 0) {
			if (attribute.childAt(0).matchContent("simpleType")) {
				simpleType = attribute.childAt(0);
			}
		} else {

			// Search for a global definition

			if (type != null) {

				// Search for a simpleType with a name matching the type
				TreeWalker walker = new TreeWalker((FPNode) attribute
						.getDocument().getRoot());
				Enumeration enume = walker.getNodeByCriteria(new AndCriteria(
						new NodeNameCriteria("simpleType"),
						new AttributeCriteria("name", type)), false);
				if (enume.hasMoreElements()) {
					simpleType = (FPNode) enume.nextElement();
				}

			}
		}

		if (simpleType != null) {

			// Search all the values inside
			TreeWalker walker = new TreeWalker(simpleType);
			Enumeration enume = walker.getNodeByCriteria(new NodeNameCriteria(
					"enumeration"), true);
			while (enume.hasMoreElements()) {
				FPNode enumNode = (FPNode) enume.nextElement();
				descriptor.addEnumValue(enumNode.getAttribute("value"));
			}

		}
	}

	// (annotation?, ((attribute | attributeGroup)*, anyAttribute?))
	private void processAttributeGroup(FPNode attribute, SchemaNode node) {
		String ref = attribute.getAttribute("ref");
		if (ref != null) {
			TreeWalker tw = new TreeWalker((FPNode) attribute.getDocument()
					.getRoot());
			FPNode foundType = tw.getOneNodeByCriteria(new AndCriteria(
					new NodeNameCriteria("attributeGroup"), new OrCriteria(
							new AttributeCriteria("name", ref, true),
							new AttributeCriteria("id", ref))), false);

			if (foundType != null)
				processAttributeGroup(foundType, node);
		} else {
			for (int i = 0; i < attribute.childCount(); i++) {
				FPNode child = attribute.childAt(i);
				if (child.matchContent("attribute")) {
					processAttribute(child, node);
				} else if (child.matchContent("attributeGroup"))
					processAttributeGroup(child, node);
			}
		}
	}

	// (annotation?, element*)
	private void processAll(FPNode all, SchemaNode node) {
		processSequence(all, node);
	}

	private SchemaNode commonProcessChoiceSequence(FPNode child,
			TagDescriptor td) {
		SchemaNode resultNode = null;

		if (child.matchContent("element")) {

			resultNode = new SchemaNode(SchemaNode.ELEMENT);
			processElement(child, resultNode, true);

		} else if (child.matchContent("group")) {

			resultNode = new SchemaNode(SchemaNode.ROOT);
			resultNode.element = td;
			processGroup(child, resultNode);

		} else if (child.matchContent("choice")) {

			resultNode = new SchemaNode(SchemaNode.ROOT);
			resultNode.element = td;
			processChoice(child, resultNode);

		} else if (child.matchContent("sequence")) {

			resultNode = new SchemaNode(SchemaNode.ROOT);
			resultNode.element = td;
			processSequence(child, resultNode);

		} else if (child.matchContent("any")) {

			// ?

		}

		return resultNode;
	}

	// (annotation?, (element | group | choice | sequence | any)*)
	private void processChoice(FPNode choice, SchemaNode node) {

		SchemaNode orNode = new SchemaNode(SchemaNode.OP_OR);
		int cminOccurs = getMinOccurs(choice);
		int cmaxOccurs = getMaxOccurs(choice);

		for (int i = 0; i < choice.childCount(); i++) {
			FPNode child = choice.childAt(i);
			SchemaNode resultNode = commonProcessChoiceSequence(child,
					node.element);

			int minOccurs = getMinOccurs(child);
			int maxOccurs = getMaxOccurs(child);

			if (resultNode != null) {
				SchemaNode parent = orNode;
				if (minOccurs == 0) {
					parent = new SchemaNode(SchemaNode.OP_OR);
					orNode.addNext(parent);
					parent.addNext(new SchemaNode(SchemaNode.EMPTY));
				} else if (maxOccurs == 1) {
					orNode.addNext(resultNode);
				} else if (maxOccurs > 1) {
					// Create a special AND case
					SchemaNode andNode = new SchemaNode(SchemaNode.OP_AND);
					orNode.addNext(andNode);

					if (maxOccurs == Integer.MAX_VALUE) {
						andNode.addNext(resultNode);
						resultNode.addNext(resultNode);
					} else {
						SchemaNode oneClone = (SchemaNode) resultNode.clone();
						for (int j = 1; j <= maxOccurs; j++) {
							andNode.addNext(oneClone);
						}
					}
				}
			}
		}

		if (orNode.getSchemaNodeCount() > 0) // We need it
			node.addNext(orNode);

		if (cmaxOccurs == Integer.MAX_VALUE) // Loop on itself
			orNode.addNext(orNode);
		else {

			SchemaNode oneClone = (SchemaNode) orNode.clone();
			// -1 because we already have the set in the orNode
			for (int i = 0; i < cmaxOccurs - 1; i++) {
				orNode.addNext(oneClone);
			}
		}
	}

	// (annotation?, (element | group | choice | sequence | any)*)
	private void processSequence(FPNode sequence, SchemaNode node) {

		SchemaNode andNode = new SchemaNode(SchemaNode.OP_AND);
		int cminOccurs = getMinOccurs(sequence);
		int cmaxOccurs = getMaxOccurs(sequence);

		for (int i = 0; i < sequence.childCount(); i++) {
			FPNode child = sequence.childAt(i);
			SchemaNode resultNode = commonProcessChoiceSequence(child,
					node.element);

			int minOccurs = getMinOccurs(child);
			int maxOccurs = getMaxOccurs(child);

			if (resultNode != null) {
				SchemaNode parent = andNode;
				if (minOccurs == 0) {
					parent = new SchemaNode(SchemaNode.OP_OR);
					andNode.addNext(parent);
					parent.addNext(new SchemaNode(SchemaNode.EMPTY));
				}
				if (maxOccurs == Integer.MAX_VALUE) {
					parent.addNext(resultNode);
					// parent.addNext( parent );
					resultNode.addNext(resultNode);
				} else {
					for (int j = 1; j <= maxOccurs; j++) {
						if (resultNode.isElement())
							parent.addNext(resultNode);
						else {
							// Copy the content
							for (int k = 0; k < resultNode.getSchemaNodeCount(); k++)
								parent.addNext(resultNode.getSchemaNode(k));
						}
					}
				}
			}
		}

		if (andNode.getSchemaNodeCount() > 0)
			node.addNext(andNode);

		if (cmaxOccurs == Integer.MAX_VALUE)
			andNode.addNext(andNode);
		else {
			SchemaNode oneClone = (SchemaNode) andNode.clone();
			//	-1 because we already have the set in the andNode
			for (int i = 0; i < cmaxOccurs - 1; i++) {
				andNode.addNext(oneClone);
			}
		}

	}

	private FPNode getFirstChildWithoutAnnotation(FPNode node) {
		if (node.childCount() > 0) {
			FPNode child = node.childAt(0);
			if (child.matchContent("annotation")) {
				if (node.childCount() > 1)
					return (FPNode) node.childAt(1);
				else
					return null;
			} else
				return child;
		} else
			return null;
	}

	private FPNode resolveElementRefence( FPNode element, String ref ) {
		TreeWalker tw = new TreeWalker((FPNode) element
				.getDocument().getRoot());
		FPNode foundType = tw.getOneNodeByCriteria(new AndCriteria(
				new NodeNameCriteria("element"), new AttributeCriteria(
						"name", ref, true)), false);
		return foundType;
	}	
	
	private FPNode getElementDefinition(FPNode element) {

		String type = element.getAttribute("type");

		if (type != null) {
			// Search the type
			int i = 0;
			String otherTypeName = null;
			// Search for this type
			if ((i = type.indexOf(":")) > -1) {
				otherTypeName = type.substring(i + 1);
			}

			TreeWalker tw = new TreeWalker((FPNode) element.getDocument()
					.getRoot());
			FPNode foundType = tw.getOneNodeByCriteria(new AndCriteria(
					new OrCriteria(new NodeNameCriteria("complexType"),
							new NodeNameCriteria("simpleType")),
					new OrCriteria(new AttributeCriteria("name", type),
							otherTypeName != null ? new AttributeCriteria(
									"name", otherTypeName) : null)), false);

			return foundType;

		} else {

			// Reference case
			String ref = element.getAttribute("ref");
			if (ref != null) {
				FPNode foundType = resolveElementRefence( element, ref );
				if (foundType != null)
					return getElementDefinition(foundType);
				else
					return null;
			} else {

				String sg = element.getAttribute("substitutionGroup");
				if (sg != null) {
					TreeWalker tw = new TreeWalker((FPNode) element
							.getDocument().getRoot());
					FPNode oneNode = tw.getOneNodeByCriteria(
							new AndCriteria(new NodeNameCriteria("element"),
									new AttributeCriteria("name", sg, true)),
							false);
					if (oneNode != null)
						return getElementDefinition(oneNode);
				}

				return getFirstChildWithoutAnnotation(element);
			}
		}
	}

	private int getMinOccurs(FPNode node) {
		String strMinOccurs = node.getAttribute("minOccurs");
		if (strMinOccurs != null) {
			try {
				return Integer.parseInt(strMinOccurs);
			} catch (NumberFormatException exc) {
			}
		}
		return 1;
	}

	private int getMaxOccurs(FPNode node) {
		String strMaxOccurs = node.getAttribute("maxOccurs");
		if ("unbounded".equals(strMaxOccurs))
			return Integer.MAX_VALUE;
		else {
			try {
				return Integer.parseInt(strMaxOccurs);
			} catch (NumberFormatException exc) {
			}
		}
		return 1;
	}

}
