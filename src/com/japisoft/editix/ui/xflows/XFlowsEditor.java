package com.japisoft.editix.ui.xflows;

import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.japisoft.editix.action.xml.format.FormatAction;
import com.japisoft.editix.editor.xsd.Factory;
import com.japisoft.editix.editor.xsd.VisualXSDEditor;
import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.framework.xml.format.FormatterConfig;
import com.japisoft.framework.xml.parser.ParseException;
import com.japisoft.framework.xml.parser.FPParser;
import com.japisoft.framework.xml.parser.document.Document;
import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.framework.xml.refactor2.AbstractRefactor;
import com.japisoft.xflows.task.Task;
import com.japisoft.xflows.task.ui.builder.ScenarioBuilder;
import com.japisoft.xmlpad.IXMLPanel;
import com.japisoft.xmlpad.XMLContainer;
import com.japisoft.xmlpad.XMLDocumentInfo;
import com.japisoft.xmlpad.action.ActionModel;
import com.japisoft.xmlpad.bookmark.BookmarkContext;
import com.japisoft.xmlpad.tree.parser.Parser;

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
public class XFlowsEditor extends JTabbedPane 
	implements 
	IXMLPanel,
	ChangeListener {

	private IXMLPanel container = null;
	private ScenarioBuilder editor = null;
	
	public XFlowsEditor() {
		super( JTabbedPane.BOTTOM );

		container = EditixFactory.buildNewContainer();

		addTab( "Source Editor", 
				new ImageIcon( getClass().getResource( 
						"document_edit.png" ) ),
				container.getView() );
		addTab( "Visual Editor",
				new ImageIcon( getClass().getResource( 
						"flash.png" ) ),
				( editor = new ScenarioBuilder() ).getView() );

	}
	
	public IXMLPanel getPanelParent() {
		return null;
	}
	
	public Parser createNewParser() {
		return null;
	}
	
	public void addNotify() {
		super.addNotify();
		addChangeListener( this );
	}

	public void removeNotify() {
		super.removeNotify();
		removeChangeListener( this );
	}

	public List<Task> getTasks() {
		if ( getSelectedIndex() == 1 ) {
			return editor.getTasks();
		} else {
			
			String content = container.getMainContainer().getText();
			return getTasks( content );
		}
	}

	public void cut() {
		if ( getSelectedIndex() == 0 ) {
			container.cut();
		}
	}

	public void copy() {
		if ( getSelectedIndex() == 0 ) {
			container.copy();
		}		
	}

	public void paste() {
		if ( getSelectedIndex() == 0 ) {
			container.paste();
		}		
	}
		
	@Override
	public Object print() {
		if ( getSelectedIndex() == 0 )
			return container;
		return editor;
	}	
	
	public List<Task> getTasks( String content ) {

		if ( content.indexOf( "<scenario>" ) == -1 ) {
			content += "<scenario></scenario>";	// Empty scenario
		}
		
		FPParser p = new FPParser();
		Document doc = null;
		
		try {
			doc = p.parse(new StringReader(content));
		} catch( ParseException exc ) {
			EditixFactory.buildAndShowWarningDialog( "Error found inside your XML scenario file : " + exc.getMessage() + ", please fix it" );
			setSelectedIndex( 0 );
			return null;
		}

		List<Task> tasks = new ArrayList<Task>();

		FPNode root = ( FPNode )doc.getRoot();
		for ( int i = 0; i < root.childCount(); i++ ) {
			FPNode taskNode = ( FPNode )root.childAt( i );
			Task t = new Task();
			t.updateFromXML( taskNode );
			tasks.add( t );
		}			
		
		return tasks;
		
	}
	
	public void stateChanged(ChangeEvent e) {
		if ( getSelectedIndex() == 0 ) {
			editor.updateCurrentParams();
			editor.stopEditing();
			
			FPNode node = new FPNode( 
					FPNode.TAG_NODE, 
					"scenario" 
			);

			if ( editor.getTasks() != null ) {
				List<Task> list = editor.getTasks();
				for ( int i = 0; i < list.size(); i++ ) {
					Task t = ( Task )list.get( i );
					if ( t != null ) {
						node.appendChild( t.toXML() );
					}
				}
			}

			String content = node.getRawXML();
			container.getMainContainer().setText( "<?xml version='1.0'?>\n" + content );

			FormatAction.format( 
				container.getMainContainer(), 
				null, 
				null,
				null 
			);

		} else {
			
			String content = container.getMainContainer().getText();
			List<Task> tasks = getTasks( content );
			if ( tasks != null ) {
				editor.setTasks( tasks );
			}
			
		}
	}

	public void dispose() {
		container.dispose();
		container = null;
	}

	public Action getAction(String actionId) {
		return container.getAction( actionId );
	}

	public BookmarkContext getBookmarkContext() {
		return container.getBookmarkContext();
	}

	public XMLContainer getMainContainer() {
		return container.getMainContainer();
	}

	public Iterator getProperties() {
		return container.getProperties();
	}

	public Object getProperty(String name, Object def) {
		return container.getProperty( name, def );
	}

	public Object getProperty(String name) {
		return container.getProperty( name );
	}

	public XMLContainer getSelectedContainer() {
		return container.getMainContainer();
	}

	public XMLContainer getSubContainer(String type) {
		return container.getMainContainer();
	}

	public XMLContainer getSubContainerAt(int index) {
		return null;
	}

	public int getSubContainerCount() {
		return 0;
	}

	public JComponent getView() {
		return this;
	}

	public void postLoad() {
		container.postLoad();
	}

	public void prepareToSave() {
		setSelectedIndex( 0 );
		container.prepareToSave();
	}

	public boolean reload() {
		return container.reload();
	}

	public void selectSubContainer(IXMLPanel panel) {
	}

	public void setAutoDisposeMode(boolean disposeMode) {
		container.setAutoDisposeMode(disposeMode);
	}

	public void setDocumentInfo(XMLDocumentInfo info) {
		container.setDocumentInfo( info );
	}

	public void setProperty(String name, Object content) {
		container.setProperty( name, content );
	}

}
