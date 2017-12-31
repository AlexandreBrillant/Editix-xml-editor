package com.japisoft.xmlpad.action.xml;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import com.japisoft.xmlpad.XMLContainer;
import com.japisoft.xmlpad.action.XMLAction;
import com.japisoft.xmlpad.xml.validator.DefaultValidator;
import com.japisoft.xmlpad.xml.validator.RelaxNGValidator;

import com.japisoft.xmlpad.xml.validator.Validator;

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
public class ParseAction extends XMLAction implements CheckableAction {

	public static final String ID = ParseAction.class.getName();

	public ParseAction() {
		super();
	}

	private static Validator defVal;
	
	public static void setDefaultValidator( Validator val ) {
		defVal = val;
	}
	
	public boolean checkDocument(XMLContainer container, boolean silentMode ) {
		setXMLContainer( container );
		try { 
			return 
				notifyAction( silentMode );
		} finally {
			dispose();
		}
	}	

	public boolean notifyAction() {
		return notifyAction( false );
	}
	
	public boolean notifyAction( boolean silentMode ) {
		container.getErrorManager().flushLastError();
		container.getErrorManager().initErrorProcessing();
		try {
			Validator v = container.getDocumentInfo().getCustomValidator();

			if ( v == null ) {
				if ( container.getSchemaAccessibility().getRelaxNGValidationLocation() != null ) {
					v = new RelaxNGValidator();
				} else {
					if ( defVal != null )
						v = defVal;
					else
						v = new DefaultValidator();
				}
			}

			int res = v.validate( container, silentMode );

			if ( res == Validator.OK ) {
				container.getErrorManager().notifyNoError( false );
				if ( popupOk && !silentMode ) {
					JOptionPane.showMessageDialog( container.getView(), getMessageForOk( container )  );					
				}
			}

			SwingUtilities.invokeLater(
				new Runnable() {
					public void run() {
						// TODO Auto-generated method stub
						container.getEditor().requestFocus();			
					}
				}
			);

			return res == Validator.OK;
		} finally {
			container.getErrorManager().stopErrorProcessing();
		}
	}

	private boolean popupOk = true;

	/** Enabled/Disable a dialog box confirming the parsing step is correct. By default <code>true</code> */
	public void setPopupForOk( boolean popup ) {
		this.popupOk = popup;
	}
	
	private String messageForOk = null;
	
	/** Reset the message when the parsing step is correct */
	public void setMessageForOk( String message ) {
		this.messageForOk = message;
	}

	public String getMessageForOk( XMLContainer container ) {
		if ( messageForOk == null ) {
			messageForOk = container.getLocalizedMessage( "PARSE_OK", "Your document is correct" );
		}
		return messageForOk;
	}

}

// NewAction ends here
