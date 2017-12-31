package com.japisoft.xmlpad.tree.action;

import javax.swing.tree.TreePath;

import com.japisoft.xmlpad.XMLContainer;
import com.japisoft.xmlpad.action.TreeAction;
import com.japisoft.xmlpad.action.XMLAction;
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
public abstract class AbstractTreeAction extends XMLAction implements TreeAction {

	public AbstractTreeAction() {
		setPopable(false);
		setToolbarable(false);
	}

	public AbstractTreeAction(String referenceAction) {
		super(referenceAction);
		setPopable(false);
		setToolbarable(false);
	}

	/** This override controls if the container has a tree */
	public void setXMLContainer(XMLContainer container) {
		this.container = container;
		if (container != null && container.getTree() != null)
			notifyXMLContainer();
	}

	public boolean notifyAction() {
		if ( container.getTree() == null )
			return XMLAction.INVALID_ACTION;
		if ( container.getTree().getSelectionPath() == null )
			return XMLAction.INVALID_ACTION;
		if ( container.getTree().getSelectionPath() != null )
			actionForSelection( container.getTree().getSelectionPath() );
		else
			return XMLAction.INVALID_ACTION;
		return XMLAction.VALID_ACTION;
	}
	
	protected void actionForSelection( TreePath tp ) {}
	
	protected boolean treeToolBar = true;

	/** <code>true</code> if this action could appear inside the tree toolbar. By default <code>true</code> */
	public void setTreeToolBarable( boolean treeToolBar ) {
		this.treeToolBar = treeToolBar;
	}

	/** @return <code>true</code> if this action could be shown in the tree toolBar */
	public boolean isTreeToolBarable() {
		return treeToolBar;
	}

	protected boolean treePopable = true;

	/** <code>true</code> if this action is available in the tree popup. By default <code>true</code> */
	public void setTreePopable( boolean treePopable ) {
		this.treePopable = treePopable;
	}

	/** @return <code>true</code> if this action is visible in the tree popup */
	public boolean isTreePopable() {
		return treePopable;
	}

}
