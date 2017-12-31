package com.japisoft.editix.ui.xmlpad;

import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.util.Iterator;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

import com.japisoft.editix.main.EditixApplicationModel;
import com.japisoft.framework.ApplicationModel;
import com.japisoft.framework.dialog.DialogManager;
import com.japisoft.framework.preferences.Preferences;

import com.japisoft.xmlpad.SharedProperties;
import com.japisoft.xmlpad.XMLContainer;
import com.japisoft.xmlpad.editor.XMLEditor;
import com.japisoft.xmlpad.look.XMLPadLook;
import com.japisoft.xmlpad.tree.renderer.FastTreeRenderer;

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
public class EditixLook extends XMLPadLook {

	static {
		Color c = new Color( 200, 200, 200 );
		Border FOCUS_BORDER = new LineBorder( c, 1, true );
		UIManager.put( "xmlpad.editor.focusBorder", FOCUS_BORDER );
		UIManager.put( "xmlpad.helper.backgroundColor", new Color( Integer.parseInt( "0052A4", 16 ) ) );

		if ( EditixApplicationModel.MACOSX_MODE )
			UIManager.put( "xmlpad.tableElementView.highlightColor", c.brighter() );
		new EditixComponentFactory();
		SharedProperties.WRAPPED_LINE = 
			Preferences.getPreference( "editor", "lineWrapped", false );
		SharedProperties.FULL_TEXT_VIEW =
			Preferences.getPreference( "editor", "fullTextView", true );

		Font f_ = null;
		
		try {
			GraphicsEnvironment ge = 
		         GraphicsEnvironment.getLocalGraphicsEnvironment();
			ge.registerFont( 
				Font.createFont(
					Font.TRUETYPE_FONT,
					EditixLook.class.getResourceAsStream( "consola.ttf" ) ) 
			);
		} catch( Exception e ) {
			ApplicationModel.debug( e );
		}

		if ( !ApplicationModel.isMacOSXPlatform() ) {
			UIManager.put( 
				"xmlpad.editor.font", 
				f_ = Preferences.getPreference( 
					"editor", 
					"font", 
					new Font( "consolas", Font.PLAIN, 14 ) 
				) 
			);
		}
		
		SharedProperties.HELPER_PANEL =
			Preferences.getPreference( "editor", "assistantWithComment", true );
	}

	public void install( XMLContainer container, XMLEditor editor) {
		super.install( container, editor);
		
		editor.setErrorLineRenderer( EditixErrorLineRenderer.getSharedInstance() );
		editor.setSelectionLineRenderer( EditixSelectionLineRenderer.getSharedInstance() );
		editor.setXPathLineRenderer( EditixLineRenderer.getSharedInstance() );

		editor.setBackground( Preferences.getPreference( "editor", "background", Color.WHITE ) );

		editor.setColorForAttribute( Preferences.getPreference( "editor", "attribute", Color.BLUE.brighter() ) );
		editor.setColorForLiteral( Preferences.getPreference( "editor", "litteral", Color.RED.darker() ) );
		
		Color _ = Color.BLUE.darker();
		editor.setColorForTag( Preferences.getPreference( "editor", "tag", _ ) );
		editor.setColorForDTDNotation( Preferences.getPreference( "editor", "dtdnotation", _ ) );
		editor.setColorForDTDElement( Preferences.getPreference( "editor", "dtdelement", Color.CYAN.darker() ) );
		
		editor.setColorForDTDAttribute( Preferences.getPreference( "editor", "dtdattribute", Color.GREEN.darker() ) );
		editor.setColorForDTDEntity( Preferences.getPreference( "editor", "dtdentity", Color.ORANGE.darker() ) );

		editor.setColorForDocType( Preferences.getPreference( "editor", "docType", Color.DARK_GRAY) );
		editor.setColorForComment( Preferences.getPreference( "editor", "comment", Color.DARK_GRAY) );
		editor.setColorForNameSpace( Preferences.getPreference( "editor", "namespace", Color.BLUE ) );
		editor.setColorForDeclaration( Preferences.getPreference( "editor", "declaration", Color.DARK_GRAY ) );
		editor.setColorForTagDelimiter( Preferences.getPreference( "editor", "tagDelimiter", Color.DARK_GRAY ) );
		
		editor.setColorForCurrentLine( 
			Preferences.getPreference( 
				"editor", 
				"currentLine", 
				new Color( 230, 250, 230 )
			)
		);

		editor.setEnableHighlightCurrentLine(
			Preferences.getPreference( "editor", "currentLineDisplay", true ) 
		);

		if ( Preferences.getPreference( "editor", "fullTextView", false ) ) {		
			editor.setColorForTagEnd( editor.getColorForTagDelimiter() );
			editor.setColorForDocTypeBackground( editor.getColorForDocType() );
			editor.setColorForCDATABackground( editor.getColorForCDATA() );
			editor.setColorForDeclarationBackground( editor.getColorForDeclaration() );
			editor.setColorForCommentBackground( editor.getColorForComment() );
		}

		editor.setSelectionColor( Preferences.getPreference( "editor", "selection", new Color( 190, 190, 240 ) ) );
		editor.setColorForText( Preferences.getPreference( "editor", "text", Color.BLACK ) );
		editor.setSelectionHighlightColor( Color.BLUE );

		editor.setHighlightExpressionColor(
				Preferences.getPreference( "editor", "exprHighlight", new Color( 180, 240, 180 ) ) );
		
		editor.setColorForAttributeSeparator( editor.getColorForText() );
		
		Color __ = new Color( 0, 128, 0 );
		editor.setColorForPrefix( "xsl", __ = Preferences.getPreference( "editor", "xslttag", __ ) );
		editor.setColorForPrefix( "xs", __ );
		
		editor.setCaretColor(
			Preferences.getPreference( "editor", "cursor", Color.BLACK )				
		);
		
		editor.setInfoToolTip( 
				Preferences.getPreference( "editor", "nodeTooltip", true ) );
		
		editor.setColorOpenCloseTipBackground( Preferences.getPreference( "interface", "table-color-even", new Color( Integer.parseInt( "c8eaa5", 16 ) ) ) );
		editor.setColorOpenCloseTip( Preferences.getPreference( "interface", "table-color-even", new Color( Integer.parseInt( "c8eaa5", 16 ) ) ).darker() );
	}

	static {
		DialogManager.setDefaultDialogIcon( "images/dialog.png" );
	}

	public void install( XMLContainer container, JTree tree) {
		super.install( container, tree );
		FastTreeRenderer renderer = ( FastTreeRenderer ) tree.getCellRenderer();
		renderer.setTextFont( Preferences.getPreference( "tree", "font", new Font( "dialog", Font.PLAIN, 13 ) ) );
		renderer.setTextColor( Preferences.getPreference( "tree", "text", Color.BLUE.darker() ) );
		renderer.setSelectionColor( Preferences.getPreference( "tree", "selection", Color.BLUE.darker() ) );		
		tree.setBackground( Preferences.getPreference( "tree", "background", Color.WHITE ) );
		
		try {
			renderer.setElementIcon(
				new ImageIcon(
					ClassLoader.getSystemClassLoader().getResource(
						"images/element.png" ) ) );
			renderer.setTextIcon(
				new ImageIcon(
					ClassLoader.getSystemClassLoader().getResource(
						"images/text.png" ) ) );			
		} catch (Throwable th) {
		}

		// tree.renderer.node.

		Iterator it = container.getProperties();
		if ( it != null ) {
			while ( it.hasNext() ) {
				String name = ( String )it.next();				
				if ( name.startsWith( "tree.renderer.node." ) ) {
					String nodeName = name.substring(
							"tree.renderer.node.".length()		
					);
					String nodeValue = 
						( String )container.getProperty( name );
					renderer.setAttribute( nodeName, nodeValue );
				}
			}
		}
	}

	@Override
	protected Color getTreeSelectionBackgroundColor() {
		return new Color( 220, 220, 220 );
	}

}
