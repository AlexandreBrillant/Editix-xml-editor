package com.japisoft.dtdparser.node;

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

// EntityDTDNode ends here
