package com.japisoft.xflows.task;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.text.PlainDocument;

import com.japisoft.framework.preferences.Preferences;
import com.japisoft.framework.ui.text.FileTextField;
import com.japisoft.xflows.XFlowsApplicationModel;

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
public class AbstractTaskUI extends JPanel implements TaskUI, ActionListener {

	public static final String IGNORE_COMPONENT = "_";
	
	protected TaskParams params;
	private String taskType = null; 

	public AbstractTaskUI( String type ) {
		this.taskType = type;
	}

	public void setParams( TaskParams params ) {
		if ( this.params == params )
			return;
		this.params = params;
		preSetParams();
		prepareComponents( this );
		postSetParams();
	}

	public void preSetParams() {}
	public void postSetParams() {}

	private String getParam( String name ) {
		String value = params.getParamValue( name );
		if ( value == null ) {
			value = Preferences.getPreference( 
					taskType.toLowerCase(), 
					name, 
					( String )null );
		}
		return value;
	}

	public void actionPerformed( ActionEvent e ) {
		
		if ( e.getSource() instanceof JComboBox ) {
			params.setParam( 
					e.getActionCommand(), 
					( String )( ( JComboBox )e.getSource() ).getSelectedItem() );
		} else
		if ( e.getSource() instanceof JCheckBox ) {
			params.setParam(
					e.getActionCommand(), 
					( ( JCheckBox )e.getSource() ).isSelected() ? "true" : "false" );
		}
	}

	public void removeNotify() {
		super.removeNotify();
	}

	private ArrayList actionListeners = null;
	
	protected void prepareComponents( Container container ) {
		for ( int i = 0; i < container.getComponentCount(); i++ ) {
			Component c = container.getComponent( i );
			String name = c.getName();
			
			if ( IGNORE_COMPONENT.equals( name ) )
				continue;
			
			if ( c instanceof FileTextField ) {
				if ( name == null )
					System.err.println( "Warning no name for FileTextField inside " + this );
				else {
					FileTextField ftf = ( FileTextField )c;
					((JTextField)(ftf.combo.getEditor().getEditorComponent())).setDocument( new CustomPlainDocument( name ) );
					ftf.setText( getParam( name ) );
				}
			} else
			if ( c instanceof JComboBox ) {
				if ( name == null )
					System.err.println( "Warning no name for JComboBox insde " + this );
				JComboBox cb = ( JComboBox )c;
				
				if ( cb.isEditable() ) {				
					if ( cb.getEditor().getEditorComponent() instanceof JTextField ) {

						( ( JTextField )cb.getEditor().getEditorComponent() ).
							setDocument( new CustomPlainDocument( name ) );
						( ( JTextField )cb.getEditor().getEditorComponent() ).setText( getParam( name ) );

					}
				} else {
					
					if ( getParam( name ) != null )
						cb.setSelectedItem( getParam( name ) );
					else {
						cb.setSelectedIndex( -1 );
					}
					cb.setActionCommand( name );
					if ( actionListeners == null )
						actionListeners = new ArrayList();
					if ( !actionListeners.contains( cb ) ) {
						cb.addActionListener( this );
						actionListeners.add( cb );
					}
					
				}

			} else
				
			if ( c instanceof JCheckBox ) {
				
				JCheckBox cb = ( JCheckBox )c;

				if ( getParam( name ) != null ) {
					cb.setSelected(
							"true".equals(
									getParam( name ) ) );
					
				}

				cb.setActionCommand( name );

				if ( actionListeners == null )
					actionListeners = new ArrayList();
				if ( !actionListeners.contains( cb ) ) {
					cb.addActionListener( this );
					actionListeners.add( cb );
				}
				
			} else

			if ( c instanceof JTextComponent ) {

				JTextComponent tf = ( JTextComponent ) c;
				tf.setDocument( new CustomPlainDocument( name ) );
				tf.setText( getParam( name ) );

			} else
				
			if ( c instanceof Container ) {
				prepareComponents( ( Container )c );
			}
		}
	}

	public JComponent getView() {
		return this;
	}

	public void start() {
		if ( actionListeners != null ) {
			for ( int i = 0; i < actionListeners.size(); i++ ) {
				JComponent compo = ( JComponent )actionListeners.get( i );
				if ( compo instanceof JComboBox )
					( ( JComboBox )compo ).addActionListener( this );
				else
				if ( compo instanceof JCheckBox )
					( ( JCheckBox )compo ).addActionListener( this );
			}
		}		
	}	

	public void stop() {
		if ( actionListeners != null ) {
			for ( int i = 0; i < actionListeners.size(); i++ ) {
				JComponent compo = ( JComponent )actionListeners.get( i );
				if ( compo instanceof JComboBox )
					( ( JComboBox )compo ).removeActionListener( this );
				else
				if ( compo instanceof JCheckBox )
					( ( JCheckBox )compo ).removeActionListener( this );
			}
		}
	}

	//////////////////////////////////////////////////

	class CustomPlainDocument extends PlainDocument {

		private String name;
		
		public CustomPlainDocument( String name ) {
			this.name = name;
		}
		
		public void insertString( int offs, String str, AttributeSet a )
				throws BadLocationException {
			super.insertString( offs, str, a );
			params.setParam( name, getText( 0, getLength() ) );
			XFlowsApplicationModel.setModified();
		}

		public void remove( int offs, int len ) throws BadLocationException {
			super.remove( offs, len );
			params.setParam( name, getText( 0, getLength() ) );
			XFlowsApplicationModel.setModified();
		}

	}

}
