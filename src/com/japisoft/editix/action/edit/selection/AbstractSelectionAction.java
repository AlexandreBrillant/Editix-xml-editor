package com.japisoft.editix.action.edit.selection;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.text.BadLocationException;

import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.editix.ui.EditixFrame;
import com.japisoft.xmlpad.IXMLPanel;
import com.japisoft.xmlpad.XMLContainer;

public abstract class AbstractSelectionAction extends AbstractAction {

	@Override
	public void actionPerformed(ActionEvent e) {
		IXMLPanel panel = EditixFrame.THIS.getSelectedPanel();
		if ( panel == null ) {
			EditixFactory.buildAndShowWarningDialog( "No editor ?" );
			return;
		}
		
		XMLContainer container = panel.getMainContainer();
		if ( container == null ) {
			EditixFactory.buildAndShowWarningDialog( "Can't find current editor ?" );
			return;
		}
		int start = container.getEditor().getSelectionStart();
		int end = container.getEditor().getSelectionEnd();
		if ( start == -1 || end == -1 || start == end ) {
			EditixFactory.buildAndShowWarningDialog( "No selected text ?" );
			return;
		}
		try {
			String selection = container.getDocument().getText( start, end - start );
			String new_selection = processSelection( selection );
			if ( new_selection != null ) {
				container.getEditor().replaceSelection( new_selection );
			}
		} catch( BadLocationException exc ) {
			EditixFactory.buildAndShowErrorDialog( "Can't get the selected text ? [" + exc.getMessage() + "]" );
		}

	}
	
	 abstract protected String processSelection( String selection );
	
	
}
