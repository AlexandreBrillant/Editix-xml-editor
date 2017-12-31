package com.japisoft.editix.action.file.imp;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;

import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.editix.ui.EditixFrame;
import com.japisoft.framework.dialog.DialogManager;
import com.japisoft.framework.ui.toolkit.BrowserCaller;
import com.japisoft.framework.ui.toolkit.FileManager;
import com.japisoft.framework.xml.XMLToolkit;
import com.japisoft.p3.Manager;
import com.japisoft.xmlpad.IXMLPanel;
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
public class CSVImport extends AbstractAction {

	public void actionPerformed(ActionEvent e) {
		
		//£££
		
		File csvFile = FileManager.getSelectedFile( true, "csv", "CSV File" );

		if ( csvFile != null ) {
			
			CSVPanel confImport = new CSVPanel();				

			if (DialogManager.showDialog(EditixFrame.THIS, "CSV Import",
					"CSV Import", "Import a CSV document to XML", null,
					confImport, new Dimension(400, 550)) == DialogManager.OK_ID) {
				
				String content = impCSV(csvFile, confImport);
				
				IXMLPanel panel = EditixFactory.buildNewContainer("XML", (String)null);
				XMLContainer container = panel.getMainContainer();
				container.setText( content );
				EditixFrame.THIS.addContainer(panel);

			}
		} 
		//££
	}

	public static String impCSV(File dataFile, CSVPanel config) {
		StringBuffer sb = new StringBuffer();
		sb.append("<?xml version=\"1.0\"?>\n\n<document>\n");

		char sep = config.getSeparator();
		
		char chrDelim = config.getStringDelimiter();

		String row = config.getRowName();
		String col = "col";

		try {

			BufferedReader br = new BufferedReader( 
				new FileReader( dataFile ) 
			);

			try {

				String line = null;
				int startingRow = config.getStartingRow();
				int rowNumber = 1;

				while ( ( line = br.readLine() ) != null ) {
					startingRow--;
					if (startingRow <= 0) {
						// Process it
						sb.append( "\t<" + row );
						if ( config.hasRowNumber() ) {
							sb.append( " id=\"" + rowNumber + "\"" );
						}
						sb.append( ">\n");

						int i = 0;
						int c = 0;
						StringBuffer sbCol = null;
						boolean inQualifiedString = false;
						while ( i < line.length() ) {
							char ch = line.charAt( i );
							if (sbCol == null) {
								sbCol = new StringBuffer();
							}
							if (ch == sep && !inQualifiedString) {

								String columnName = config.getColName( c, "" + ( c + 1 ) );
								
								String idAttribute = "";
								
								String tmpCol = col;
								if ( config.convertColToElement() ) {
									tmpCol = columnName;
								} else
									idAttribute = " id=\"" + columnName	+ "\"";

								sb.append("\t\t<" + tmpCol + idAttribute + ">");
								sb.append( XMLToolkit.resolveCharEntities( sbCol.toString() ) );
								sb.append("</" + tmpCol + ">\n");							

								sbCol = null;
								c++;
							} else {

								if (chrDelim != 0) {

									if (ch == chrDelim) {
										inQualifiedString = !inQualifiedString;
									} else
										sbCol.append(ch);

								} else
									sbCol.append(ch);
							}
							i++;
						}

						if ( sbCol != null ) {
							String columnName = config.getColName( c, "" + ( c + 1 ) );
							
							String idAttribute = "";
							
							String tmpCol = col;
							if ( config.convertColToElement() ) {
								tmpCol = columnName;
							} else
								idAttribute = " id=\"" + columnName	+ "\"";

							sb.append("\t\t<" + tmpCol + idAttribute + ">");
							sb.append( XMLToolkit.resolveCharEntities( sbCol.toString() ) );
							sb.append("</" + tmpCol + ">\n");							
						}

						sb.append("\t</" + row + ">\n");
						
					}
					rowNumber++;
				}

			
			} finally {
				br.close();
			}

		} catch (FileNotFoundException exc) {
			EditixFactory.buildAndShowErrorDialog("File error : " + dataFile);
		} catch (IOException exc) {
			EditixFactory.buildAndShowErrorDialog("Can't parse : " + dataFile);
		}

		sb.append("\n</document>");		
		return sb.toString();
	}

}
