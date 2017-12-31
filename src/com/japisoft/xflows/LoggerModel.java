package com.japisoft.xflows;

import com.japisoft.framework.xml.parser.node.FPNode;

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
public class LoggerModel {
	
	private boolean fileLogInfoEnabled = false;
	private boolean fileLogWarningEnabled = false;
	private boolean fileLogErrorEnabled = false;
	
	private boolean mailLogInfoEnabled = false;
	private boolean mailLogWarningEnabled = false;
	private boolean mailLogErrorEnabled = false;

	private String fileLogInfo = null;
	private String fileLogWarning = null;
	private String fileLogError = null;
	
	private String mailLogInfo = null;
	private String mailLogWarning = null;
	private String mailLogError = null;

	public String getFileLogError() {
		return fileLogError;
	}
	public void setFileLogError(String fileLogError) {
		this.fileLogError = fileLogError;
	}
	public boolean isFileLogErrorEnabled() {
		return fileLogErrorEnabled;
	}
	public void setFileLogErrorEnabled(boolean fileLogErrorEnabled) {
		this.fileLogErrorEnabled = fileLogErrorEnabled;
	}
	public String getFileLogInfo() {
		return fileLogInfo;
	}
	public void setFileLogInfo(String fileLogInfo) {
		this.fileLogInfo = fileLogInfo;
	}
	public boolean isFileLogInfoEnabled() {
		return fileLogInfoEnabled;
	}
	public void setFileLogInfoEnabled(boolean fileLogInfoEnabled) {
		this.fileLogInfoEnabled = fileLogInfoEnabled;
	}
	public String getFileLogWarning() {
		return fileLogWarning;
	}
	public void setFileLogWarning(String fileLogWarning) {
		this.fileLogWarning = fileLogWarning;
	}
	public boolean isFileLogWarningEnabled() {
		return fileLogWarningEnabled;
	}
	public void setFileLogWarningEnabled(boolean fileLogWarningEnabled) {
		this.fileLogWarningEnabled = fileLogWarningEnabled;
	}
	public String getMailLogError() {
		return mailLogError;
	}
	public void setMailLogError(String mailLogError) {
		this.mailLogError = mailLogError;
	}
	public boolean isMailLogErrorEnabled() {
		return mailLogErrorEnabled;
	}
	public void setMailLogErrorEnabled(boolean mailLogErrorEnabled) {
		this.mailLogErrorEnabled = mailLogErrorEnabled;
	}
	public String getMailLogInfo() {
		return mailLogInfo;
	}
	public void setMailLogInfo(String mailLogInfo) {
		this.mailLogInfo = mailLogInfo;
	}
	public boolean isMailLogInfoEnabled() {
		return mailLogInfoEnabled;
	}
	public void setMailLogInfoEnabled(boolean mailLogInfoEnabled) {
		this.mailLogInfoEnabled = mailLogInfoEnabled;
	}
	public String getMailLogWarning() {
		return mailLogWarning;
	}
	public void setMailLogWarning(String mailLogWarning) {
		this.mailLogWarning = mailLogWarning;
	}
	public boolean isMailLogWarningEnabled() {
		return mailLogWarningEnabled;
	}
	public void setMailLogWarningEnabled(boolean mailLogWarningEnabled) {
		this.mailLogWarningEnabled = mailLogWarningEnabled;
	}

	public FPNode toXML() {
		FPNode logger = new FPNode( FPNode.TAG_NODE, "logger" );
		FPNode fileLogger = new FPNode( FPNode.TAG_NODE, "file" );
		fileLogger.setAttribute( "info", isFileLogInfoEnabled() );
		fileLogger.setAttribute( "warning", isFileLogWarningEnabled() );
		fileLogger.setAttribute( "error", isFileLogErrorEnabled() );
		
		fileLogger.setAttribute( "pathInfo", getFileLogInfo() );
		fileLogger.setAttribute( "pathWarning", getFileLogWarning() );
		fileLogger.setAttribute( "pathError", getFileLogError() );

		logger.appendChild( fileLogger );
		FPNode mailLogger = new FPNode( FPNode.TAG_NODE, "mail" );
		mailLogger.setAttribute( "info", isMailLogInfoEnabled() );
		mailLogger.setAttribute( "warning", isMailLogWarningEnabled() );
		mailLogger.setAttribute( "error", isMailLogErrorEnabled() );
		
		mailLogger.setAttribute( "pathInfo", getMailLogInfo() );
		mailLogger.setAttribute( "pathWarning", getMailLogWarning() );
		mailLogger.setAttribute( "pathError", getMailLogError() );

		logger.appendChild( mailLogger );

		return logger;
	}

	public void updateFromXML( FPNode task ) {
		// Skip the logger node

		FPNode file = task.childAt( 0 );
		setFileLogInfoEnabled( "true".equals( file.getAttribute( "info" ) ) );
		setFileLogWarningEnabled( "true".equals( file.getAttribute( "warning" ) ) );
		setFileLogErrorEnabled( "true".equals( file.getAttribute( "error" ) ) );

		setFileLogInfo( file.getAttribute( "pathInfo" ) );
		setFileLogWarning( file.getAttribute( "pathWarning" ) );
		setFileLogError( file.getAttribute( "pathError" ) );

		FPNode mail = task.childAt( 1 );
		
		setMailLogInfoEnabled( "true".equals( mail.getAttribute( "info" ) ) );
		setMailLogWarningEnabled( "true".equals( mail.getAttribute( "warning" ) ) );
		setMailLogErrorEnabled( "true".equals( mail.getAttribute( "error" ) ) );

		setMailLogInfo( mail.getAttribute( "pathInfo" ) );
		setMailLogWarning( mail.getAttribute( "pathWarning" ) );
		setMailLogError( mail.getAttribute( "pathError" ) );		

	}
	
}
