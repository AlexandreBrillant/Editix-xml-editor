package com.japisoft.editix.main.steps;

import javax.swing.JOptionPane;

import com.japisoft.dtdparser.DTDMapperFactory;
import com.japisoft.editix.action.xml.ParseAction;
import com.japisoft.editix.toolkit.EditiXSAXParserFactory;
import com.japisoft.framework.ApplicationStep;
import com.japisoft.framework.preferences.Preferences;
import com.japisoft.framework.xml.XMLParser;
import com.japisoft.xmlpad.SharedProperties;
import com.japisoft.xmlpad.action.ActionModel;
import com.japisoft.xmlpad.action.xml.FormatAction;
import com.japisoft.xmlpad.xml.validator.DefaultValidator;
import com.japisoft.xmlpad.xml.validator.XMLPadSAXParserFactory;

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
public class XMLApplicationStep implements ApplicationStep {

	public boolean isFinal() {
		return false;
	}

	public void start( String[] args ) {
		// Format preference

		ActionModel.setProperty(FormatAction.class.getName(),
				FormatAction.INDENT_CHAR_PROPERTY, "\t");

		ActionModel.setProperty(FormatAction.class.getName(),
				FormatAction.INDENT_SIZE_PROPERTY, new Integer(
						Preferences.getPreference("xmlconfig",
								"format-space", 1)));

		SharedProperties.EDITOR_LINE_NUMBER =
			Preferences.getPreference( "editor", "line-number", true );

		FormatAction.CANONICAL = Preferences.getPreference("xmlconfig",
				"format-canonical", false);

		FormatAction fa = 
			( FormatAction )ActionModel.getActionByName( ActionModel.FORMAT_ACTION );

		fa.setReplaceAmp( Preferences.getPreference( "xmlconfig", "format-replaceAmp", true ) );
		fa.setReplaceGt( Preferences.getPreference( "xmlconfig", "format-replaceGt", true ) );
		fa.setReplaceLt( Preferences.getPreference( "xmlconfig", "format-replaceLt", true ) );
		fa.setReplaceAPos( Preferences.getPreference( "xmlconfig", "format-replaceAPos", true ) );
		fa.setReplaceQuote( Preferences.getPreference( "xmlconfig", "format-replaceQuote", true ) );

		//////////// XML

		XMLParser.resetDefaultParser();

		XMLPadSAXParserFactory
				.setDelegate( new EditiXSAXParserFactory() );

		try {
			ParseAction.setDefaultValidator( 
				( DefaultValidator )Class.forName( "com.japisoft.editix.action.xml.EditixValidator" ).newInstance() 
			);
		} catch (InstantiationException e2) {
		} catch (IllegalAccessException e2) {
		} catch (ClassNotFoundException e2) {
		}

		DTDMapperFactory.setDTDMapper( EditixDTDMapper.getInstance() );
		SharedProperties.SCHEMA_CACHING = true; // For sharing multiple content
		
		try {
			SharedProperties.DEFAULT_ENTITY_RESOLVER = EditixEntityResolver
					.getInstance();
		} catch ( Throwable th ) {
			JOptionPane
					.showMessageDialog(
							null,
							"Error found inside the XML catalog files,\nPlease fix it using the XML Menu"
			);
		}
	}

	public void stop() {
	}

}
