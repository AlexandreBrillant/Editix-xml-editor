package com.japisoft.framework.dialog.command;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.text.*;

import java.io.*;
import java.util.*;

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
public class ExternalToolPane extends JPanel implements ActionListener {
  JComboBox cbAction = new JComboBox();
  JButton btnStore = new JButton();
  JButton btnDelete = new JButton();
  JLabel jLabel1 = new JLabel();
  JLabel jLabel3 = new JLabel();
  JScrollPane jScrollPane2 = new JScrollPane();
  JTextField txfCommand = new JTextField();
  JTextArea txaArguments = new JTextArea();
  JButton btnArg = new JButton();
  JComboBox cbArgs = new JComboBox();
  JScrollPane jScrollPane1 = new JScrollPane();
  JLabel jLabel2 = new JLabel();
  JButton btnRun = new JButton();
  JTextArea txaConsole = new JTextArea();
  JButton btnCommand = new JButton();
  JButton btnClean = new JButton();
  JButton btnCopy = new JButton();
  GridBagLayout gridBagLayout1 = new GridBagLayout();

  public static final String CURRENT_PATH_MACRO = "cf";
  public static final String CURRENT_DIRECTORY_MACRO = "cd";
  public static final String CURRENT_FILENAME_MACRO = "cn";
  //public static final String CURRENT_FILEPATH_MACRO = "cp";
  public static final String CURRENT_HOME_MACRO = "home";

  public ExternalToolPane() {
  	//super( "External Tools", "External Tools", "Call an external tool" );
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    setPreferredSize(new Dimension(500, 550));
  }

  public void addNotify() {
    super.addNotify();
    btnDelete.addActionListener( this );
    btnStore.addActionListener( this );
    btnArg.addActionListener( this );
    btnCommand.addActionListener( this );
    btnRun.addActionListener( this );
    btnClean.addActionListener( this );
    btnCopy.addActionListener( this );
    cbAction.addActionListener( this );

    setMacro( CURRENT_HOME_MACRO, System.getProperty( "user.home" ) );
    if ( cbAction.getItemCount() > 0 )
      cbAction.setSelectedIndex( 0 );
  }

  public void removeNotify() {
    super.removeNotify();
    btnDelete.removeActionListener( this );
    btnStore.removeActionListener( this );
    btnArg.removeActionListener( this );
    btnCommand.removeActionListener( this );
    btnRun.removeActionListener( this );
    btnClean.removeActionListener( this );
    btnCopy.removeActionListener( this );
    cbAction.removeActionListener( this );
  }

  public void setActionItems( ArrayList list ) {
    cbAction.removeAllItems();
    Iterator it = list.iterator();
    while ( it.hasNext() ) {
      ActionItem item = ( ActionItem )it.next();
      cbAction.addItem( item );
    }
  }

  public ArrayList getActionsItems() {
    ArrayList l = new ArrayList();
    for ( int i = 0; i < cbAction.getItemCount(); i++ )
      l.add( cbAction.getItemAt( i ) );
    return l;
  }

  private Hashtable htMacro = new Hashtable();

  public void setMacro( String macroName, String macroValue ) {
    htMacro.put( macroName, macroValue );
  }

  public void setMacro( String[] macros ) {
  	cbArgs.setModel( new DefaultComboBoxModel( macros ) );
    cbArgs.setSelectedIndex( 0 );
  }
  
  public void actionPerformed( ActionEvent e ) {
    if ( e.getSource() == btnArg ) {
      addArg();
    } else
    if ( e.getSource() == btnCommand ) {
      setCommand();
    } else
    if ( e.getSource() == btnRun ) {
      if ( "Run".equals( btnRun.getText() ) )
        run();
      else
        stop();
    } else
    if ( e.getSource() == btnClean ) {
      cleanConsole();
    } else
    if ( e.getSource() == btnCopy ) {
      copyConsole();
    } else
    if ( e.getSource() == btnStore ) {
      ActionItem item = new ActionItem(
        ( ( JTextField )cbAction.getEditor().getEditorComponent() ).getText(),
        txfCommand.getText(),
        txaArguments.getText() );
      cbAction.addItem( item );
    } else
    if ( e.getSource() == btnDelete ) {
      int index = cbAction.getSelectedIndex();
      if ( index > -1 )
        cbAction.removeItemAt( index );
      if ( cbAction.getItemCount() > 0 )
        cbAction.setSelectedIndex( 0 );
    } else
    if ( e.getSource() == cbAction ) {
      if ( cbAction.getSelectedItem() instanceof ActionItem ) {
        ActionItem item = ( ActionItem )cbAction.getSelectedItem();
        selectAction( item );
      }
    }
   }

  private void selectAction( ActionItem item ) {
    txfCommand.setText( item.command );
    txaArguments.setText( item.arguments );
    txaConsole.setText( null );
  }

  private void addArg() {
    String content = ( String )cbArgs.getSelectedItem();
    int i = content.indexOf( "-" );
    String arg = content.substring( i + 1 );
    txaArguments.append( arg );
  }

  private void setCommand() {
    JFileChooser chooser = new JFileChooser( txfCommand.getText( ) );
    chooser.setMultiSelectionEnabled( false );
    if ( chooser.showOpenDialog( this ) == JFileChooser.APPROVE_OPTION ) {
      txfCommand.setText( chooser.getSelectedFile().toString() );
    }
  }

  private void cleanConsole() {
    txaConsole.setText( null );
  }

  private void copyConsole() {
    txaConsole.copy();
  }

  Process currentProcess;

  private void stop() {
    if ( currentProcess != null ) {
      currentProcess.destroy();
      btnRun.setText( "Run" );
    }
  }

  private void run() {
    try {

      String args = txaArguments.getText();

      // Résolutionn macro

      for ( Enumeration keys = htMacro.keys(); keys.hasMoreElements(); ) {
        String init = ( String )keys.nextElement();
        String key = "\\$\\{" + init + "\\}";
        String value = (String)htMacro.get( init );

        StringBuffer sb = new StringBuffer();
        for ( int i = 0; i < value.length(); i++ ) {
          char c = value.charAt( i );
          if ( c == '\\' )
            sb.append( "\\\\" );
          else
            sb.append( c );
        }

        args = args.replaceAll( key, sb.toString() );
      }

      txaConsole.setText( null );
      
      currentProcess = Runtime.getRuntime().exec(
        txfCommand.getText() + " " + args );
      
      new ThreadPoller( currentProcess.getInputStream() ).start();
      new ThreadPoller( currentProcess.getErrorStream() ).start();

      try {

        btnRun.setText( "Stop" );

        int resultat = currentProcess.waitFor();

        if ( resultat > 0 ) {
          JOptionPane.showMessageDialog( this, "Abnormal terminaison" );
        } else {
        	JOptionPane.showMessageDialog( this, "Completed" );
        }

      } catch( InterruptedException exc ) {
      }

    } catch( IOException exc2 ) {
      JOptionPane.showMessageDialog( this, "Can't run this command" );
    }

    btnRun.setText( "Run" );
  }

  public static void main(String[] args) {
    ExternalToolPane test21 = new ExternalToolPane();
    test21.setVisible( true );
  }

  private void jbInit() throws Exception {
    setLayout(gridBagLayout1);
    cbAction.setEditable(true);
    btnStore.setEnabled(false);
    btnStore.setText("Store");
    btnDelete.setEnabled(false);
    btnDelete.setText("Delete");
    jLabel1.setText("Command");
    jLabel3.setText("Arguments");
    btnArg.setEnabled(false);
    btnArg.setText("Arg");
    jLabel2.setText("Console");
    btnRun.setEnabled(false);
    btnRun.setText("Run");
    txaConsole.setBackground(Color.black);
    txaConsole.setFont(new java.awt.Font("Monospaced", 0, 14));
    txaConsole.setForeground(Color.white);
    btnCommand.setText("...");
    btnClean.setText("Clean");
    btnCopy.setText("Copy");
    add(btnDelete,    new GridBagConstraints(3, 0, 2, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 7, 0, 14), 0, 0));
    add(jScrollPane2,  new GridBagConstraints(0, 4, 5, 1, 1.0, 1.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(8, 13, 0, 14), 430, 45));
    add(btnArg,  new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(6, 13, 0, 9), 0, 0));
    add(cbArgs,  new GridBagConstraints(1, 5, 4, 1, 1.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(7, 0, 0, 14), 340, 0));
    add(jScrollPane1,  new GridBagConstraints(0, 7, 5, 1, 1.0, 1.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(8, 13, 0, 14), 428, 129));
    jScrollPane1.getViewport().add(txaConsole, null);
    add(jLabel2,  new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(14, 19, 0, 13), 0, 0));
    add(btnStore,    new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 8, 0, 0), 6, 0));
    add(btnRun,  new GridBagConstraints(3, 8, 2, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(6, 12, 7, 14), 10, 0));
    add(jLabel3,  new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(12, 13, 0, 0), 0, 0));
    add(jLabel1,  new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(12, 13, 0, 0), 0, 0));
    add(txfCommand,    new GridBagConstraints(0, 2, 4, 1, 1.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(9, 13, 0, 0), 10, 0));
    add(btnClean,  new GridBagConstraints(0, 8, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 13, 7, 0), 0, 0));
    add(btnCopy,  new GridBagConstraints(1, 8, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 11, 7, 5), 1, 0));
    add(btnCommand, new GridBagConstraints(4, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(9, 1, 0, 0), -6, -6));
    add(cbAction,       new GridBagConstraints(0, 0, 2, 1, 1.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 9, 3, 4), 0, 0));
    jScrollPane2.getViewport().add(txaArguments, null);


    txaConsole.setEditable( false );
    txfCommand.setDocument( new TestDocument( txfCommand ) );
    ((JTextField)cbAction.getEditor().getEditorComponent()).setDocument( new TestDocument( ( JTextField )cbAction.getEditor().getEditorComponent() ) );
  }



  private void updateTextFileStatus( JTextField tf, boolean empty ) {
    if ( tf == txfCommand ) {
      btnArg.setEnabled( !empty );
      btnRun.setEnabled( !empty );
    } else
    if ( tf == cbAction.getEditor().getEditorComponent() ) {
      btnStore.setEnabled( !empty );
      btnDelete.setEnabled( !empty );
    }
  }

  class TestDocument extends PlainDocument {

    private JTextField ref;

    public TestDocument( JTextField text ) {
      this.ref = text;
    }

    public void insertString(int offs, String str, AttributeSet a)
      throws BadLocationException  {
      super.insertString( offs, str, a );
      updateTextFileStatus( ref, ref.getText().length() == 0 );
    }

    public void remove(int offs,
                   int len)
            throws BadLocationException {
      super.remove( offs, len );
      updateTextFileStatus( ref, ref.getText().length() == 0 );
    }

    protected  void removeUpdate(AbstractDocument.DefaultDocumentEvent chng) {
      super.removeUpdate( chng );
      updateTextFileStatus( ref, ref.getText().length() == 0 );
    }
  }

  class ThreadPoller extends Thread {
    private InputStream input;

    public ThreadPoller( InputStream input ) {
      this.input = input;
    }

    public void run() {
      int c;
      try {
      	byte[] buffer = new byte[ 80 ];
        while ( ( c = input.read( buffer ) ) != -1 ) {
          txaConsole.append( "" + new String( buffer, 0, c ) );
          Thread.sleep( 50 );
        }
      } catch( IOException exc ) {}
        catch( InterruptedException exc2 ) {
      }
    }
  }

}
