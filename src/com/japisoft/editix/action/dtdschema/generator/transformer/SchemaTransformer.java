package com.japisoft.editix.action.dtdschema.generator.transformer;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.japisoft.editix.action.dtdschema.generator.MetaAttribute;
import com.japisoft.editix.action.dtdschema.generator.MetaNode;
import com.japisoft.editix.action.dtdschema.generator.MetaObject;
import com.japisoft.framework.preferences.Preferences;

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
public class SchemaTransformer extends AbstractTransformer {

	public String getType() {
		return "XSD";
	}

	private String schemaPrefix = null;

	public SchemaTransformer() {
		super();
		schemaPrefix = Preferences.getPreference("xmlconfig",
				"W3C XML Schema prefix", "xs");
	}

	private boolean namespace;

	@Override
	protected void initTransform(MetaNode root, StringBuffer sb) {

		namespace = root.getNamespace() != null;

		sb
				.append("<")
				.append(schemaPrefix)
				.append(":schema xmlns:")
				.append(schemaPrefix)
				.append(
						"=\"http://www.w3.org/2001/XMLSchema\" elementFormDefault=\"qualified\"");

		if (root.getNamespace() != null) {
			sb.append(" targetNamespace=\"" + root.getNamespace()
					+ "\" xmlns:editix=\"" + root.getNamespace() + "\"");
		}

		sb.append(">");
		sb.append(System.getProperty("line.separator"));

	}

	@Override
	protected void closeTransform(MetaNode root, StringBuffer sb) {
		sb.append("</").append(schemaPrefix).append(":schema>");
	}

	public boolean hasVersion() {
		return true;
	}

	private boolean hasTextNode(List<MetaNode> list) {
		for (int i = 0; i < list.size(); i++) {
			MetaNode node = list.get(i);
			if (node.acceptText())
				return true;
		}
		return false;
	}

	@Override
	protected void generateMetaNode(MetaNode node, StringBuffer sb) {

		boolean hasText = node.acceptText();

		sb.append("\n");

		if (node.getChildren().size() == 0 && !node.hasAttributes()) {
			if (node.acceptText()) {
				sb.append("  <").append(schemaPrefix)
						.append(":element name=\"").append(node.getName())
						.append("\" type=\"").append(schemaPrefix).append(
								":string\"/>");
				sb.append(System.getProperty("line.separator"));
				return;
			}
		}

		sb.append("  <").append(schemaPrefix).append(":element name=\"")
				.append(node.getName()).append("\">").append(
						System.getProperty("line.separator"));

		boolean complexType = node.getChildren().size() >= 1
				|| (node.getChildren().size() == 0 && !(node.acceptText()));

		boolean simpleContent = !complexType && node.hasAttributes();

		if (complexType) {
			sb.append("    <").append(schemaPrefix).append(":complexType");
			if (hasText)
				sb.append(" mixed=\"true\">");
			else
				sb.append(">");
			sb.append(System.getProperty("line.separator"));
		} else {
			if (simpleContent) {
				sb.append("    <").append(schemaPrefix).append(":complexType>");
				sb.append(System.getProperty("line.separator"));
				sb.append("     <").append(schemaPrefix).append(
						":simpleContent>");
				sb.append(System.getProperty("line.separator"));
				sb.append("       <").append(schemaPrefix).append(
						":extension base=\"").append(schemaPrefix).append(":")
						.append("string\">");
				sb.append(System.getProperty("line.separator"));
			}
		}

		boolean sequence = node.getChildren().size() == 1
				&& node.hasMultipleOccurence((MetaNode) node.getChildren().get(
						0));

		boolean choiceMode = false;

		if (node.getChildCount() > 0) {

			if (sequence || sequenceMode) {
				sb.append("      <").append(schemaPrefix).append(":sequence>");
			} else {
				sb.append("      <").append(schemaPrefix).append(
						":choice minOccurs=\"0\" maxOccurs=\"unbounded\">");
				choiceMode = true;
			}

		}

		if (node.getChildren().size() > 0)
			sb.append(System.getProperty("line.separator"));

		for (int i = 0; i < node.getChildren().size(); i++) {
			MetaNode child = (MetaNode) node.getChildren().get(i);
			sb.append("        ").append("<").append(schemaPrefix).append(
					":element ref=\"").append(namespace ? "editix:" : "")
					.append(child.getName()).append("\"");

			if (!choiceMode) {
				if (node.canBeMissing(child)) {
					sb.append(" minOccurs=\"0\"");
				}
				if (node.hasMultipleOccurence(child)) {
					sb.append(" maxOccurs=\"unbounded\"");
				}
			}

			sb.append("/>");
			sb.append(System.getProperty("line.separator"));
		}

		if (node.getChildCount() > 0) {

			if (sequence || sequenceMode) {
				sb.append("      </").append(schemaPrefix).append(":sequence>");
				sb.append(System.getProperty("line.separator"));
			} else {
				sb.append("      </").append(schemaPrefix).append(":choice>");
				sb.append(System.getProperty("line.separator"));
			}

		}

		// Attributes

		if (node.hasAttributes()) {
			Vector al = node.getAttributes();
			for (int i = 0; i < al.size(); i++) {
				MetaAttribute ma = (MetaAttribute) al.get(i);
				sb.append("      <").append(schemaPrefix).append(
						":attribute name=\"").append(ma.name).append("\"");
				sb.append(" type=\"");
				sb.append(schemaPrefix).append(":");
				sb.append(translateType(ma.getType()));
				sb.append("\"");

				if (ma.isAlways())
					sb.append(" use=\"required\"");
				sb.append("/>").append(System.getProperty("line.separator"));
			}
		}

		if (complexType) {
			sb.append("    </").append(schemaPrefix).append(":complexType>");
		} else if (simpleContent) {
			sb.append("       </").append(schemaPrefix).append(":extension>");
			sb.append(System.getProperty("line.separator"));
			sb.append("     </").append(schemaPrefix).append(":simpleContent>");
			sb.append(System.getProperty("line.separator"));
			sb.append("    </").append(schemaPrefix).append(":complexType>");
		}

		sb.append(System.getProperty("line.separator"));

		sb.append("  </").append(schemaPrefix).append(":element>");
		sb.append(System.getProperty("line.separator"));

	}

	static String translateType(String type) {
		String finalType = "string";
		if (MetaObject.BOOL_TYPE.equals(type)) {
			finalType = "boolean";
		} else if (MetaObject.DATE_TYPE.equals(type)) {
			finalType = "date";
		} else if (MetaObject.DECIMAL_TYPE.equals(type)) {
			finalType = "decimal";
		} else if (MetaObject.DOUBLE_TYPE.equals(type)) {
			finalType = "double";
		} else if (MetaObject.ID_TYPE.equals(type)) {
			finalType = "ID";
		} else if (MetaObject.IDREF_TYPE.equals(type)) {
			finalType = "IDREF";
		} else if (MetaObject.TIME_TYPE.equals(type)) {
			finalType = "time";
		} else if (MetaObject.URI_TYPE.equals(type)) {
			finalType = "anyURI";
		}
		return finalType;
	}

}
