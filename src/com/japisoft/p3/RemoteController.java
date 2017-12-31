package com.japisoft.p3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import com.japisoft.framework.ApplicationModel;

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
public class RemoteController implements Runnable {

	public static void checkUser( String url, String userName, HackerController controller ) {
		RemoteController runner = new RemoteController();
		runner.controller = controller;
		runner.userName = userName;
		runner.url = url;
		Thread th = new Thread( runner );
		th.start();
	}

	private HackerController controller;
	private String url;
	private String userName;
	
	public void run() {

		try {

			try {
				Thread.sleep( 120000 );
			} catch ( InterruptedException e1 ) {
			}			

			if ( controller == null ) {
				throw new RuntimeException( "Controller expected !" );
			}
			
			URL _url = new URL( url );

			URLConnection connection = _url.openConnection();
			connection.setDoOutput( true );
			connection.setDoInput( true );

			String data = URLEncoder.encode( "version", "UTF-8" ) + "=" + URLEncoder.encode( ApplicationModel.getAppVersion(), "UTF-8" );
			data += "&" + URLEncoder.encode( "os", "UTF-8" ) + "=" + URLEncoder.encode( System.getProperty( "os.name" ), "UTF-8" );
			data += "&" + URLEncoder.encode( "user", "UTF-8" ) + "=" + URLEncoder.encode( userName, "UTF-8" ); 

			OutputStreamWriter wr = new OutputStreamWriter( connection.getOutputStream() );
			wr.write( data );
			wr.flush();
			
			InputStreamReader reader = new InputStreamReader( connection.getInputStream() );
			BufferedReader buffered = new BufferedReader( reader );
			String line = null;
			line = buffered.readLine();

			buffered.close();
			wr.close();
			
			if ( "HA".equals( line ) ) {
				controller.notifyHackerDetected();
			}
		} catch (MalformedURLException e) {
		//	ApplicationModel.debug( e );
		} catch (UnsupportedEncodingException e) {
		//	ApplicationModel.debug( e );
		} catch (IOException e) {
		//	ApplicationModel.debug( e );
		}
	}
	
}

