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

package com.japisoft.framework.xml.dtdparser.node;

/**
 * <b>Created Sat Feb 15 11:21:34 2003</b>
 * <p>
 * Comments
 * </p>
 * 
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0
 * @see DTDNode
 */
public class EntityDTDNode extends DTDNode {

	public static final int INTERNAL_ENTITY = 0;

	public static final int SYSTEM_ENTITY = 1;

	public static final int PUBLIC_ENTITY = 2;

	public static final String IGNORE = "IGNORE";

	public static final String INCLUDE = "INCLUDE";

	public EntityDTDNode() {
		super();
		setNodeType(ENTITY);
	}

	public EntityDTDNode(String name, String value) {
		this();
		setName(name);
		setValue(value);
	}

	private boolean parameter;

	/** Set this entity as parameter : example &lt;!ENTITY A % "B"&gt; */
	public void setParameter(boolean parameter) {
		this.parameter = parameter;
	}

	/** @return true if this entity is a parameter for other DTD part */
	public boolean isParameter() {
		return parameter;
	}

	/** Set the entity nature : INTERNAL, PUBLIC or SYSTEM */
	public void setType(int type) {
		this.type = type;
	}

	/** @return the entity nature */
	public int getType() {
		return type;
	}

	private int type = INTERNAL_ENTITY;

	private String name;

	private String value;

	/** Set the name of the entity */
	public void setName(String name) {
		this.name = name;
	}

	/** @return the name of the entity */
	public String getName() {
		return name;
	}

	/** Set the value of the entity */
	public void setValue(String value) {
		this.value = value;
	}

	/** @return the value of the entity */
	public String getValue() {
		return value;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer( getDTDComment() ).append( "<!ENTITY ").append(getName());
		if (isParameter())
			sb.append(" %");
		if (type == SYSTEM_ENTITY)
			sb.append(" SYSTEM");
		else if (type == PUBLIC_ENTITY)
			sb.append(" PUBLIC ").append("\"-//\"");
		sb.append(" \"").append(getValue()).append("\">");
		return sb.toString();
	}

}

