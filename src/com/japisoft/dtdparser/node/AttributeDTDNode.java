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

package com.japisoft.dtdparser.node;

/**
 * Attribute node. This node is only for ElementDTDNode.
 * 
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0
 */
public class AttributeDTDNode extends DTDNode {

	// Attribute value type
	public static final int ID_ATT_VAL = 0;

	public static final int IDREF_ATT_VAL = 1;

	public static final int ENTITY_ATT_VAL = 2;

	public static final int ENTITIES_ATT_VAL = 3;

	public static final int NMTOKEN_ATT_VAL = 4;

	public static final int NMTOKENS_ATT_VAL = 5;

	public static final int CDATA_ATT_VAL = 6;

	/** Special type for enumeration */
	public static final int ENUM_ATT_VAL = 7;

	// Attribute usage
	public static final int REQUIRED_ATT = 0;

	public static final int IMPLIED_ATT = 1;

	public static final int FIXED_ATT = 2;

	public AttributeDTDNode() {
		super();
		setNodeType(ATTRIBUTE);
	}

	/** Attribute id */
	public AttributeDTDNode(String name) {
		this();
		setName(name);
	}

	private String name;

	/** Set the attribute name */
	public void setName(String name) {
		this.name = name;
	}

	/** @return the attribute name */
	public String getName() {
		return name;
	}

	private int type;

	/**
	 * Choose the attribute type : -<code>ID_ATT_VAL</code>-
	 * <code>IDREF_ATT_VAL</code>-<code>ENTITY_ATT_VAL</code>-
	 * <code>ENTITIES_ATT_VAL</code>-<code>NMTOKEN_ATT_VAL</code>-
	 * <code>NMTOKENS_ATT_VAL</code>-<code>CDATA_ATT_VAL</code>
	 */
	public void setType(int type) {
		this.type = type;
	}

	/** @return the attribute type */
	public int getType() {
		return type;
	}

	private int usage;

	/**
	 * Choose the attribute usage :<code>REQUIRED_ATT</code>
	 <code>IMPLIED_ATT</code>
	 <code>FIXED_ATT</code>
	 */
	public void setUsage(int usage) {
		this.usage = usage;
	}

	/** @return the attribute usage */
	public int getUsage() {
		return usage;
	}

	String def = "";

	/** Set the default attribute value */
	public void setDefaultValue(String defaultValue) {
		this.def = defaultValue;
	}

	/**
	 * @return the default attribute value :<code>null</code> is returned for
	 *         no default value
	 */
	public String getDefaultValue() {
		return def;
	}

	private String[] values;

	/** Set of supported values */
	public void setEnumeration(String[] values) {
		this.values = values;
		if (values != null)
			if (values.length > 0)
				setType(ENUM_ATT_VAL);
	}

	/** @return true if value is valide for this attribute */
	public boolean isValueValid(String value) {
		if (values != null) {
			for (int i = 0; i < values.length; i++) {
				if (values[i].equals(value))
					return true;
			}
			return false;
		}
		return true;
	}

	/**
	 * @return the list of available value :<code>null</code> is returned for
	 *         no enumeration
	 */
	public String[] getEnumeration() {
		if (values != null && values.length == 0)
			return null;
		return values;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		// Attribute name
		sb.append(getName()).append(" ");

		// Attribute type
		switch (getType()) {
		case ID_ATT_VAL:
			sb.append("ID");
			break;
		case IDREF_ATT_VAL:
			sb.append("IDREF");
			break;
		case ENTITY_ATT_VAL:
			sb.append("ENTITY");
			break;
		case ENTITIES_ATT_VAL:
			sb.append("ENTITIES");
			break;
		case NMTOKEN_ATT_VAL:
			sb.append("NMTOKEN");
			break;
		case NMTOKENS_ATT_VAL:
			sb.append("NMTOKENS");
			break;
		case CDATA_ATT_VAL:
			sb.append("CDATA");
			break;
		case ENUM_ATT_VAL:
			sb.append("(");
			for (int i = 0; i < values.length; i++) {
				if (i > 0)
					sb.append("|");
				sb.append(values[i]);
			}
			sb.append(")");
			break;
		}

		sb.append(" ");

		if (getDefaultValue() == null || "".equals(getDefaultValue())) {

			switch (getUsage()) {
			case REQUIRED_ATT:
				sb.append("#REQUIRED");
				break;
			case IMPLIED_ATT:
				sb.append("#IMPLIED");
				break;
			case FIXED_ATT:
				sb.append("#FIXED \"\"");
				break;
			}

			sb.append(" ");

		} else {
			sb.append("\"");
			sb.append(getDefaultValue());
			sb.append("\"");
		}

		return sb.toString();
	}

}



