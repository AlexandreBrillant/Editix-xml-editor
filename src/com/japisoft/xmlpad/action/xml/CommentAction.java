package com.japisoft.xmlpad.action.xml;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.*;
import javax.swing.text.BadLocationException;

import com.japisoft.xmlpad.XMLContainer;
import com.japisoft.xmlpad.action.XMLAction;
import com.japisoft.xmlpad.dialog.DialogManager;
import com.japisoft.xmlpad.editor.XMLPadDocument;

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
public class CommentAction extends XMLAction {
	
	public static final String ID = CommentAction.class.getName();
	
	public CommentAction() {
		super();
	}

	public boolean notifyAction() {
		String ini = null;
		int loc = editor.getCaretPosition();

		if ( ( (XMLPadDocument)editor.getDocument() ).isInsideTagExceptAttributeValue( loc ) ) {
			JOptionPane.showMessageDialog( editor, "Can't insert a comment here !" );
			return INVALID_ACTION;
		}
		
		int startSelection = editor.getSelectionStart();
		int stopSelection = editor.getSelectionEnd();

		Integer[] startStop = ( ( XMLPadDocument)editor.getDocument() ).getCommentDelimiters( loc );
		int start = -1;
		int stop = -1;

		int starter = 4;
		int stopper = 7;

		if ( startStop != null ) {
			start = startStop[ 0 ].intValue() + 4;
			stop = startStop[ 1 ].intValue() - 2;
		
			try {
				ini = editor.getDocument().getText( start, stop - start );
				if ( !checkComment( ini ) )
					return !XMLAction.VALID_ACTION;
			} catch( BadLocationException exc ) {
			}
		} else {
			if ( startSelection < stopSelection ) {
				try {
					ini = editor.getDocument().getText( startSelection, stopSelection - startSelection );
					if ( !checkComment( ini ) )
						return !XMLAction.VALID_ACTION;
					start = startSelection;
					stop = stopSelection;
					starter = 0;
					stopper = 0;
				} catch( BadLocationException exc ) {
				}
			}
		}
		
		CommentPane mDia = new CommentPane();		
		mDia.setText( ini );
		
		if ( DialogManager.showDialog(
				SwingUtilities.getWindowAncestor( container.getView() ),
				"Comment", 
				"Insert/Update", 
				"Insert or update at the current selection of your XML comment\nMultiple lines can be used",
				null,
				mDia ) != DialogManager.OK )
			return VALID_ACTION;

		String comment = mDia.getText();

		if (comment != null) {
			boolean empty = ( comment.length() == 0 );
			comment = "<!--" + comment + "-->";

			if (ini == null)
				editor.insertText(comment);
			else {
				try {
					if ( start != - 1 )
						editor.getDocument().remove(start - starter, stop - start + stopper);
					if ( !empty )
						editor.getDocument().insertString(start - starter, comment, null);
				} catch (Throwable th) {
					//th.printStackTrace();
				}
			}
		}
		return VALID_ACTION;
	}
	
	/** Check if the text has XML comment and showes a dialog box if this is the case */
	private boolean checkComment( String text ) {
		if ( text.indexOf( "<!--" ) > -1 ) {
			JOptionPane.showMessageDialog( 
					container.getView(), 
					XMLContainer.getLocalizedMessage( "COMMENT_ERROR2", "Can't comment this selected text, you must remove the comments" ),
					XMLContainer.getLocalizedMessage( "ERROR", "Error" ),
					JOptionPane.ERROR_MESSAGE );
			return false;
		}
		return true;
	}
	
	protected void notifyXMLContainer() {
		setEnabled( container.isEditable() );
	}

	///////////////////////////

	class CommentPane extends JPanel {
		private JTextArea text;

		public CommentPane() {
			init();
			setPreferredSize( new Dimension( 400, 300 ) );
		}

		void init() {
			setLayout( new BorderLayout() );
			add( new JScrollPane( text = new JTextArea() ) );
			text.setForeground( editor.getColorForComment() );
			text.setBackground( editor.getBackground() );
		}

		public void addNotify() {
			super.addNotify();
			text.requestFocus();
		}

		public void setText( String content ) {
			text.setText( content );
		}
		
		public String getText() {
			return text.getText();
		}
	}

}

// CommentAction ends here
