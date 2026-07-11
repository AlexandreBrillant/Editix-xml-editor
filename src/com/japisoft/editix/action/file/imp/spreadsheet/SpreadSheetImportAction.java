// Editix XML Editor
// https://www.editix.com
// Copyright (c) 2026 Alexandre Brillant
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
// See the GNU General Public License for more details: https://www.gnu.org/licenses/gpl-3.0
//
// AI Training Restriction :
// This source code is provided for human use only.
// Using this code to train, fine-tune, or develop AI models,
// machine learning systems, or similar technologies is
// STRICTLY PROHIBITED. Violations will terminate all rights
// under the applicable license.

package com.japisoft.editix.action.file.imp.spreadsheet;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.japisoft.editix.action.xml.format.FormatAction;
import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.editix.ui.EditixFrame;
import com.japisoft.framework.ApplicationModel;
import com.japisoft.framework.dialog.DialogManager;
import com.japisoft.framework.toolkit.FileToolkit;
import com.japisoft.framework.ui.toolkit.FileManager;
import com.japisoft.framework.xml.XMLFileData;
import com.japisoft.framework.xml.XMLToolkit;
import com.japisoft.xmlpad.IXMLPanel;
import com.japisoft.xmlpad.XMLContainer;

public class SpreadSheetImportAction extends AbstractAction {

	@Override
	public void actionPerformed(ActionEvent e) {

		try {
			
			XMLFileData xfd = getXMLWizard( new String[] { "csv", "xlsx" }, new String[] { "CSV File", "XLSX File" } );
			IXMLPanel container = null;
			EditixFrame.THIS.addContainer( container = EditixFactory.buildNewContainer( "XML", xfd ) );
			FormatAction.format( (XMLContainer)container, null, null, null );

		} catch( Exception exc ) {
			ApplicationModel.debug( exc );
			EditixFactory.buildAndShowErrorDialog( "Can't process this file : " + exc.getMessage() );			
		}

	}
	
	public static XMLFileData getXMLWizard( String[] fileExts, String[] fileDescriptions ) throws Exception {
		
		File f = FileManager.getSelectedFile( true, fileExts, fileDescriptions );
		if ( f != null ) {
			
			SpreadSheetConfPanel conf = new SpreadSheetConfPanel();				
			conf.setExcelMode( !FileToolkit.matchExt( f, "csv" ) );
			SpreadSheet sh = SpreadSheetFactory.instance().getSpreadSheet( conf, f );
			if ( conf.isExcelMode() ) {
				if ( sh.getSheetCount() > 1 )
				for ( int i = 1; i <= sh.getSheetCount(); i++ ) {
					conf.addSheet( sh.getSheet( i ).getName() );
				}
			}
			
			if (DialogManager.showDialog(EditixFrame.THIS, "Spreadsheet Import",
					"CSV Import", "Import a Spreadsheet document to XML", null,
					conf, new Dimension(400, 550)) == DialogManager.OK_ID) {
					
					if ( sh == null ) {
						throw new Exception( "Unknown format ?" );
					}
					
					Document doc = process( f, conf, sh );
					if ( doc == null ) {
						throw new Exception( "Can't process this file ?" );
					}
					
					String newXML = XMLToolkit.nodeToText( doc );				
					XMLFileData xfd = new XMLFileData( "UTF-8", newXML );
					xfd.uri = f.toString();
					return xfd;
			}
			
		}
		
		return null;
	}
	
	private static Document process( File source, SpreadSheetConfPanel conf, SpreadSheet sh ) throws Exception {
		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		Element root = doc.createElement( "spreadsheet" );
		doc.appendChild( root );
		root.setAttribute( "source", source.toString() );
		
		int startingSheet = 1;
		int endingSheet = sh.getSheetCount();
		
		if ( !conf.allSheets() ) {
			for ( int i = 1; i <= sh.getSheetCount(); i++ ) {
				endingSheet = i;
				if ( conf.sheetName().equals( sh.getSheet( i ).getName() ) ) {
					startingSheet = i;
					break;
				}
			}
		}
		
		for ( int i = startingSheet; i <= endingSheet; i++ ) {
			Element sheet = doc.createElement( "sheet" );
			root.appendChild( sheet );
			String name = sh.getSheet( i ).getName();
			if ( name != null ) {
				sheet.setAttribute( "id", name );
			}
			process( sh.getSheet( i ), conf, sheet );
		}
		
		return doc;
	}
	
	private static void process( Sheet source, SpreadSheetConfPanel conf, Element target ) {
		
		if ( conf.useFirstRowForColNames() ) {
			Row r = source.getRow( 1 );
			if ( r != null )
				for ( int i = 0; i < r.getColCount(); i++ ) {
					Col c = r.getCol( i );
					if ( c != null )
						conf.setColName( i , c.getValue() );
				}
		}
		
		int fromTheRow = conf.fromTheRow();		
		int toTheRow = source.getRowCount();

		if ( !conf.allTheRow() )
			toTheRow = conf.toTheRow();

		if ( !conf.allTheRow() )
			toTheRow = conf.toTheRow();
		
		for ( int i = Math.max( 0, fromTheRow - 1 ); i < toTheRow; i++ ) {
			Row r = source.getRow( i );
			
			Element row = target.getOwnerDocument().createElement( "row" );
			row.setAttribute( "id", Integer.toString( i ) );
			target.appendChild( row );

			process( r, conf, row );
		}

	}
	
	private static void process( Row source, SpreadSheetConfPanel conf, Element target ) {
		
		// int fromTheCol = conf.fromTheCol();
		int fromTheCol = 0;
		int toTheCol = source.getColCount();

		if ( !conf.allTheCol() )
			toTheCol = conf.toTheCol();
		
		for ( int i = fromTheCol; i< toTheCol; i++ ) {
			Col c = source.getCol( i );
			if ( c == null )
				continue;
			
			String name = conf.getColName( i );
			if ( conf.useFirstRowForColNames() ) {
				if ( name == null )
					name = "col" + i;
				name = XMLToolkit.validTagName( name );
			}
			
			if ( conf.attributeCellValue() ) {
			
				target.setAttribute( name, c.getValue() );
				
			} else {
			
				Element cell = target.getOwnerDocument().createElement( name );
				
				if ( !conf.useFirstRowForColNames() )
					cell.setAttribute( "id", Integer.toString( i ) );
				
				cell.setTextContent( c.getValue() );
				target.appendChild( cell );
				
			}
			
		}
		
	}

}

