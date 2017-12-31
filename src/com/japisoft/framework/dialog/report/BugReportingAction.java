package com.japisoft.framework.dialog.report;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.japisoft.framework.ApplicationModel;
import com.japisoft.framework.dialog.DialogManager;
import com.japisoft.framework.dialog.actions.AbstractDialogAction;
import com.japisoft.framework.dialog.actions.CancelAction;
import com.japisoft.framework.dialog.actions.ClosableAction;
import com.japisoft.framework.dialog.actions.DialogActionModel;
import com.japisoft.framework.ui.Toolkit;

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
public class BugReportingAction extends AbstractAction {

	protected String type = "BUG";	
	private DataReportingPanel panel;
	protected String userInformation = "Insert a bug in the field below, this interface will include automatically your release and operating system. The Title and Description are required. If you wish to receive a reply, please insert your email. Note that you MUST have an active internet connection for using this form.";
	protected String image = "images/bug_red.png";
	protected String dialogTitle = "Bug reporting";
	
	/** @param url Remote server processing user information */
	public BugReportingAction( String url ) {
		setURLReport( url );
	}

	public BugReportingAction() {
		setURLReport( ApplicationModel.REPORTING_URL );
	}
	
	private String url = null;

	/** @param url Remote server processing user information */
	public void setURLReport( String url ) {
		this.url = url;
	}
	
	public String getURLReport() {
		return url;
	}

	/** @return the current type of error. By default BUG */
	public String getType() {
		return type;
	}
	
	/** Override the default type of error */
	public void setType(String type) {
		this.type = type;
	}

	public void actionPerformed(ActionEvent e) {
		DialogActionModel model = new DialogActionModel();
		model.addDialogAction( new CancelAction() );
		model.addDialogAction( new SendAction() );
		showDialog( model, panel = new DataReportingPanel() );
	}
	
	protected void showDialog( DialogActionModel model, JPanel panel ) {
		DialogManager.showDialog(
				ApplicationModel.MAIN_FRAME,
				dialogTitle,
				dialogTitle,
				userInformation,
				Toolkit.getIconFromClasspath( image ),
				panel,
				model,
				new Dimension( 500, 400 ) );
	}	

	class SendAction extends AbstractDialogAction implements ClosableAction {

		public SendAction() {
			super( 10 );
			putValue( Action.NAME, "Send" );
		}

		public void actionPerformed(ActionEvent e) {

			String title = panel.getTitle();
			String description = panel.getDescription();
			
			if ( title.length() < 10 || description.length() < 10 ) {
				JOptionPane.showMessageDialog( 
						ApplicationModel.MAIN_FRAME,
						"Invalid title or description (at least 10 chars)" );
				vetoClosingDialog( true );
				return;
			}

			if ( JOptionPane.showConfirmDialog( ApplicationModel.MAIN_FRAME, "Do you confirm ?" ) == 
				JOptionPane.YES_OPTION ) {
				
				String email = panel.getEMail();

				try {
					
					if ( url == null )
						url = ApplicationModel.REPORTING_URL;
					
					URL _url = new URL( url );
					
					URLConnection connection = _url.openConnection();
					connection.setDoOutput( true );
					connection.setDoInput( true );

					String data = URLEncoder.encode( "version", "UTF-8" ) + "=" + URLEncoder.encode( ApplicationModel.getAppVersion(), "UTF-8" );
			        data += "&" + URLEncoder.encode( "os", "UTF-8" ) + "=" + URLEncoder.encode( System.getProperty( "os.name" ), "UTF-8" );
			        data += "&" + URLEncoder.encode( "type", "UTF-8" ) + "=" + URLEncoder.encode( type, "UTF-8" );
			        data += "&" + URLEncoder.encode( "title", "UTF-8" ) + "=" + URLEncoder.encode( title, "UTF-8" );			        
			        data += "&" + URLEncoder.encode( "content", "UTF-8" ) + "=" + URLEncoder.encode( description, "UTF-8" );
			        data += "&" + URLEncoder.encode( "email", "UTF-8" ) + "=" + URLEncoder.encode( email, "UTF-8" );			        

			        ApplicationModel.debug( "Send [" + data + "] to " + url );
			        
					OutputStreamWriter wr = new OutputStreamWriter( connection.getOutputStream() );
			        wr.write( data );
			        wr.flush();
			        
			        // Get the response
			        BufferedReader rd = new BufferedReader( new InputStreamReader(
			        		connection.getInputStream() ) );
			        String line;
			        while ( ( line = rd.readLine() ) != null ) {
			            // Process line...
			        }
			        wr.close();
			        rd.close();					
			        
			        
			        
				} catch( Throwable exc ) {
					JOptionPane.showMessageDialog( 
							ApplicationModel.MAIN_FRAME,
							"Can't send. Please send us a mail to " + ApplicationModel.MAIN_SUPPORT_EMAIL );
				}

			}
		}
	}
	
	/** @return the user information available in the dialog header */
	public String getUserInformation() {
		return userInformation;
	}
	/** Reset the user information available in the dialog header */
	public void setUserInformation(String userInformation) {
		this.userInformation = userInformation;
	}
}
