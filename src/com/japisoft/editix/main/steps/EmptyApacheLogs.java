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

package com.japisoft.editix.main.steps;

import org.apache.commons.logging.Log;

public class EmptyApacheLogs implements Log {

	public EmptyApacheLogs( String message ) {
	}

	public void debug(Object arg0, Throwable arg1) {}
	public void debug(Object arg0) {}

	public void error(Object arg0, Throwable arg1) {
	}

	public void error(Object arg0) {
	}

	public void fatal(Object arg0, Throwable arg1) {
	}
	public void fatal(Object arg0) {
	}
	public void info(Object arg0, Throwable arg1) {}
	public void info(Object arg0) {}

	public boolean isDebugEnabled() {
		return false;
	}

	public boolean isErrorEnabled() {
		return false;
	}

	public boolean isFatalEnabled() {
		return false;
	}

	public boolean isInfoEnabled() {
		return false;
	}

	public boolean isTraceEnabled() {
		return false;
	}

	public boolean isWarnEnabled() {
		return false;
	}

	public void trace(Object arg0, Throwable arg1) {}
	public void trace(Object arg0) {}
	public void warn(Object arg0, Throwable arg1) {}
	public void warn(Object arg0) {}

}

