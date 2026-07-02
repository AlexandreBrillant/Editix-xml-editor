package com.japisoft.editix.action.file;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.editix.ui.EditixFrame;
import com.japisoft.framework.xml.SchemaLocator;
import com.japisoft.xmlpad.XMLContainer;

public class OpenCurrentSchema extends AbstractAction {

	@Override
	public void actionPerformed(ActionEvent e) {
		XMLContainer container = EditixFrame.THIS.getSelectedContainer();
		if ( container != null ) {
			String currentFile = container.getCurrentDocumentLocation();
			String schema = container.getSchemaAccessibility().getCurrentSchema();
			
			if ( schema == null )
				EditixFactory.buildAndShowWarningDialog( "No schema found ?" );
			else {
				String target = new SchemaLocator( currentFile, schema ).getSource();
				OpenAction.openFile( "XSD",false, target, null, null );
			}
		} else
			EditixFactory.buildAndShowErrorDialog( "Unknown document ?" );
	}

}
