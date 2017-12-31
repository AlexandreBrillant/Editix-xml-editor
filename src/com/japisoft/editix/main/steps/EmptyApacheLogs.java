package com.japisoft.editix.main.steps;

import org.apache.commons.logging.Log;

// Remove any logs like for FOP
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
