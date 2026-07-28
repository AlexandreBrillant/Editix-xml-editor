package com.japisoft.editix.action.edit;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.editix.ui.windows.EditixFrame;
import com.japisoft.framework.ApplicationModel;
import com.japisoft.framework.descriptor.InterfaceBuilder;
import com.japisoft.xmlpad.XMLContainer;

public class AskLLMAction extends AbstractAction {

	@Override
	public void actionPerformed( ActionEvent e ) {
		XMLContainer container = EditixFrame.THIS.getSelectedContainer();
		if ( container == null )
			EditixFactory.buildAndShowWarningDialog( "Can't find your document ?" );
		InterfaceBuilder ib = ApplicationModel.INTERFACE_BUILDER;		
		if ( container.getSelectedText() != null ) {
			if ( !ib.runAction( "toLLM" ) ) {
				EditixFactory.buildAndShowErrorDialog( "Can't run this action [toLLM] ?" );
			}
		} else {
			if ( container.getCurrentNode() != null ) {
				if ( !ib.runAction( "toLLM2" ) ) {
					EditixFactory.buildAndShowErrorDialog( "Can't run this action [toLLM2] ?" );
				}				
			}
		}
	}

}
