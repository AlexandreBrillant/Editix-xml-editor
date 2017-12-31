package com.japisoft.editix.action.docbook;

import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import com.japisoft.editix.ui.EditixDialog;
import com.japisoft.framework.ui.text.FileTextField;
import com.japisoft.xmlpad.XMLContainer;


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
public class DocBookDialog extends EditixDialog {

	private DocBookDialogPanel panel = null;
	
	public DocBookDialog() {
		super(
			"DocBook",
			"DocBook usage",
			"Transform your DocBook document to multiple outputs.Note that this is only for \"book\" document. Choose an output file. Store these parameters using a project for the next time." );
		panel = new DocBookDialogPanel();
		add( panel );
	}

	
	protected Dimension getDefaultSize() {
		return new Dimension( 350, 250 );
	}
	
	public void init( XMLContainer container ) {
		panel.init( container );
	}
	
	public void store( XMLContainer container ) {	
		panel.store( container );
	}

}	
