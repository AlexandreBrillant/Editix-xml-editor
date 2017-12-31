package com.japisoft.editix.action.dtdschema;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.StringReader;
import java.net.URL;

import javax.swing.AbstractAction;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.japisoft.editix.action.xml.EditixValidator;
import com.japisoft.editix.action.xsl.XSLT2Action;
import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.editix.ui.EditixFrame;
import com.japisoft.framework.ui.toolkit.FileManager;
import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.xmlpad.XMLContainer;
import com.japisoft.xmlpad.xml.validator.DefaultValidator;

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
public class SchematronAction extends AbstractAction {

	public static int checkSchematron( XMLContainer container, File f, boolean messageMode ) {
		
		try {
			
			// Transform the schematron file with XSLT
			URL u = ClassLoader
					.getSystemResource( "schematron/iso_svrl_for_xslt2.xsl" );
			javax.xml.transform.TransformerFactory factory = XSLT2Action.getTransformerFactoryV2( false );
			Transformer t = factory.newTransformer( 
				new StreamSource( u.toString() )
			);
			DOMResult dr = null;
			t.transform( new StreamSource( f ), dr = new DOMResult() );
			
			// Apply the result to the source document
			DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = db.parse( new InputSource( new StringReader( container.getText()  ) ) );
			
			t = factory.newTransformer( new DOMSource( dr.getNode() ) );
			
			DOMResult schematronRes = null;
			
			t.transform( new DOMSource( doc ), schematronRes = new DOMResult() );
			Document docRes = ( Document )schematronRes.getNode();
			NodeList nl = docRes.getElementsByTagNameNS( "http://purl.oclc.org/dsdl/svrl", "*" );

			if ( nl.getLength() > 0 ) {
			
				container.getErrorManager().initErrorProcessing();
				
				boolean errorFound = false;
				
				for ( int i = 0; i < nl.getLength(); i++ ) {
					Element ass = ( Element )nl.item( i );
					
					if ( !( "failed-assert".equals( ass.getLocalName() ) || "successful-report".equals( ass.getLocalName() ) ) )
						continue;
					
					String test = ass.getAttribute( "test" );
					String location = ass.getAttribute( "location" );
					// Test on doc location
					
					String message = test;
					
					NodeList nl2 = ass.getChildNodes();
					for ( int j = 0; j < nl2.getLength(); j++ ) {
						if ( nl2.item( j ) instanceof Element ) {
							Element ee = ( Element )nl2.item( j );
							if ( "text".equals( ee.getLocalName() ) ) {
								message = ee.getTextContent();
							}
						}
					}

					message = ass.getLocalName() + ":" + message;
					
					int line = 0;
					
					FPNode root = container.getRootNode();
					if ( root != null ) {
						FPNode result = root.getNodeForXPathLocation( location, true );
						if ( result != null ) {
							line =  result.getStartingLine();
						}
					}
					
					container.getErrorManager().notifyError( null, true, null, line, 0, 0, message, false );
				}

				container.getErrorManager().stopErrorProcessing();

				if ( container.getErrorManager().hasLastError() )
					return EditixValidator.ERROR;
				
				return EditixValidator.OK;
				
			} else {

				container.getErrorManager().notifyNoError( false );
				if ( messageMode )
					EditixFactory.buildAndShowInformationDialog( "Your document is correct" );
				
				return EditixValidator.OK;
				
			}

			/*
			<svrl:schematron-output xmlns:xhtml="http://www.w3.org/1999/xhtml"
                    xmlns:xs="http://www.w3.org/2001/XMLSchema"
                    xmlns:schold="http://www.ascc.net/xml/schematron"
                    xmlns:iso="http://purl.oclc.org/dsdl/schematron"
                    xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                    schemaVersion=""
                    title=""><!--  --><svrl:active-pattern document="" name="CELL"/>
				   <svrl:fired-rule context="table/cell"/>
				   <svrl:failed-assert test="not(p)" location="/table/cell[1]">
				      <svrl:text>P manquant</svrl:text>
				   </svrl:failed-assert>
				   <svrl:fired-rule context="table/cell"/>
				   <svrl:failed-assert test="not(p)" location="/table/cell[2]">
				      <svrl:text>P manquant</svrl:text>
				   </svrl:failed-assert>
				   <svrl:fired-rule context="table/cell"/>
				</svrl:schematron-output>
			*/
			
		} catch( Exception exc ) {

			container.getErrorManager().notifyUniqueError( false, f.toString(), 0, 0, 0, "Invalid schematron : " + exc.getMessage(), false );
			return DefaultValidator.ERROR;
			
		}
		
		
	}
	
	@Override
	public void actionPerformed( ActionEvent e ) {

		XMLContainer container = EditixFrame.THIS.getSelectedContainer();
		if ( container != null ) {

			File f = FileManager.getSelectedFile( 
				true,
				"sch",
				"Schematron file"
			);
			if ( f != null ) {
				
				checkSchematron( container, f, true );

			}

		}

	}

}
