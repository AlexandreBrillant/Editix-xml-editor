package com.japisoft.xflows.task;

import java.io.File;

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
public class AbstractTaskContext implements TaskContext {

	public void addError(String message) {		
	}

	public void addInfo(String message) {
	}

	public void addWarning(String message) {
	}

	public Task currentTask() {
		return null;
	}

	public File getCurrentSourceFile() {
		return null;
	}

	public File getCurrentTargetFile() {
		return null;
	}

	public String getParam(String name, String def) {
		return null;
	}

	public String getParam(String name) {
		return null;
	}

	public String getParamForPath(String name) {
		return null;
	}

	public TaskParams getParams() {
		return null;
	}

	public boolean hasErrorFound() {
		return false;
	}

	public boolean hasParam(String name) {
		return false;
	}

	private boolean interrupted = false;
	
	public void interrupt() {
		interrupted = true;
	}

	public boolean isInterrupted() {
		return interrupted;
	}
	
	private String result;
	
	public void setTaskResult( String result ) {
		this.result = result;
	}

	public String getTaskResult() {
		return result;
	}

	private String source;
	
	public void setTaskSource( String source ) {
		this.source = source;
	}
	
	public String getTaskSource() {
		return source;
	}
	
	private String encoding;
	
	public void setDefaultEncoding( String encoding ) {
		this.encoding = encoding;
	}
	
	public String getDefaultEncoding() {
		return encoding;
	}

	public void setCurrentSourceFile(File f) {
	}

	public void setCurrentTargetFile(File f) {
	}

	public void setQuietErrorMode(boolean quiet) {
	}

	public void setQuietInfoMode(boolean quiet) {
	}

	public void setQuietWarningMode(boolean quiet) {
	}

}
