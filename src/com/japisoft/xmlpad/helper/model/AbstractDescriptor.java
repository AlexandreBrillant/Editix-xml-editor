package com.japisoft.xmlpad.helper.model;

import java.awt.Color;

import javax.swing.Action;
import javax.swing.Icon;
import com.japisoft.xmlpad.helper.handler.AbstractHelperHandler;

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
public abstract class AbstractDescriptor implements Descriptor {

	public boolean startsWith(String sequence) {
		if ( sequence == null )
			return false;
		String name = getName();
		return name != null && name.startsWith( sequence );
	}

	private String sequence;
	
	public void setSequence(String sequence) {
		this.sequence = sequence;
	}
	
	public String getSequence() {
		return sequence;
	}

	private String comment = null;

	public void setComment( String comment ) {
		this.comment = comment;
	}
	
	public String getComment() {
		return comment;
	}

	private Icon icon = null;
	protected String addedPart = null;

	public Icon getIcon() {
		return icon;
	}

	public void setIcon( Icon icon ) {
		this.icon = icon;
	}
	
	protected Color color;
	
	public void setColor( Color color ) {
		this.color = color;
	}

	public Color getColor() {
		return color;
	}
	
	public void setAddedPart( String added ) {
		this.addedPart = added;
	}

	private AbstractHelperHandler source;

	public void setSource( AbstractHelperHandler source ) {
		this.source = source;
	}

	public AbstractHelperHandler getSource() {
		return source;
	}

	public void dispose() {
		this.source = null;
	}	

	private boolean autoNext = false;
	
	public void setAutomaticNextHelper( boolean next ) {
		this.autoNext = next;
	}
	
	public boolean hasAutomaticNextHelper() {
		return autoNext;
	}
	
	protected String type;
	
	public void setType( String type ) { 
		this.type = type;
	}
	
	public String getType() {
		return type;
	}	

	private Action a;
	
	public Action getSpecificAction() {
		return a;
	}

	public void setSpecificAction( Action a ) {
		this.a = a;
	}

}
