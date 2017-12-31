package com.japisoft.framework.xml.imp;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;

import com.japisoft.framework.ApplicationModel;
import com.japisoft.framework.dialog.DialogManager;
import com.japisoft.framework.xml.XMLToolkit;

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
		JFileChooser jfc = new JFileChooser();
		CSVPanel confImport = new CSVPanel();
		if (jfc.showOpenDialog(ApplicationModel.MAIN_FRAME) == JFileChooser.APPROVE_OPTION) {
			File file = jfc.getSelectedFile();
			if (DialogManager.showDialog(ApplicationModel.MAIN_FRAME, "CVS Import",
					"CVS Import", "Import a CSV document to XML", null,
					confImport, new Dimension(400, 550)) == DialogManager.OK_ID) {
				try {
					impCSV(file, confImport);
				} catch( IOException exc ) {
				}
			}
		}
	}

	public static String impCSV(File dataFile, CSVImportParams config) throws IOException {
		StringBuffer sb = new StringBuffer();
		sb.append("<?xml version=\"1.0\"?>\n\n<document>\n");

		char sep = ',';
		if (config.isSemiColonSelected())
			sep = ';';
		else if (config.isSpaceSelected())
			sep = ' ';
		else if (config.isTabSelected())
			sep = '\t';
		else if (config.isOtherSelected()) {
			if (config.getOther() != null
					&& config.getOther().length() > 0)
				sep = config.getOther().charAt(0);
		}

		char chrDelim = 0;
		if (config.getTextQualifier() != null)
			chrDelim = ((String) config.getTextQualifier())
					.charAt(0);

		String row = "row";
		String col = "col";

		if (config.getRowName() != null
				&& config.getRowName().length() > 0)
			row = config.getRowName();

		try {

			BufferedReader br = new BufferedReader(new FileReader(dataFile));

			try {

				String line = null;
				int startingRow = config.getStartingRow();
				int rowNumber = 1;

				while ( ( line = br.readLine() ) != null ) {
					startingRow--;
					if (startingRow <= 0) {
						// Process it
						sb.append("\t<" + row + " id=\"" + rowNumber + "\">\n");

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

								String columnName = "" + ( c + 1 );
								if (config.getColumnName().getSize() > c) {
									columnName = ( String ) config.getColumnName().getElementAt( c );
								}

								// Add a new colum
								sb.append("\t\t<" + col + " id=\"" + columnName
										+ "\">");
								sb.append( XMLToolkit.resolveCharEntities( sbCol.toString() ) );
								sb.append("</" + col + ">\n");

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
							String columnName = "" + ( c + 1 );
							if (config.getColumnName().getSize() > c) {
								columnName = (String) config.getColumnName().getElementAt( c );
							}							
							sb.append("\t\t<" + col + " id=\"" + columnName
									+ "\">");
							sb.append( XMLToolkit.resolveCharEntities( sbCol.toString() ) );
							sb.append("</" + col + ">\n");							
						}
						
						sb.append("\t</" + row + ">\n");
						
					}
					rowNumber++;
				}

				sb.append("\n</document>");

				return sb.toString();
				
			} finally {
				br.close();
			}

		} catch (FileNotFoundException exc) {
			return null;
		}
	}

}
