// Editix XML Editor
// https://www.editix.com
// Copyright (c) 2025 Alexandre Brillant
// 
// For non-commercial usage :
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
// See the GNU General Public License for more details: https://www.gnu.org/licenses/gpl-3.0
// 
// For commercial use or integration into proprietary software :
// A commercial license is required. Visit https://www.editix.com for details.

package com.japisoft.xflows.task;

import java.io.File;

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

