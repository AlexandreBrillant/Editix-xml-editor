package com.japisoft.p3;

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
public final class Manager {
	
	public static int MAGIC_NUMBER_2 = 10;
	public static int MAGIC_NUMBER_1 = 5;
	public static int PREVIOUS_INC = 10;

	public static String PERS_TYPE = "pers";
	public static String PROF_TYPE = "prof";
	public static String STUD_TYPE = "stud";
	public static String ENT_TYPE = "ent";

	public static String PERS_SIGNATURE = null;
	public static String PROF_SIGNATURE = null;
	public static String ENT_SIGNATURE = null;
	public static String STUD_SIGNATURE = null; 

	public static boolean registered( String user, String password ) throws Exception {
		return true;
	}

	public static boolean isForPersonal() {
		return Checker.personal;
	}

	public static boolean isForProfessional() {
		return Checker.professional;
	}

	public static boolean isForEnterprise() {
		return Checker.enterprise;
	}

	public static boolean isForStudent() {
		return Checker.student;
	}

	static void control(String user) {
	}

	static String check(String user, String password ) {
		return null;
	}

	static void unlocked(String user, String password) throws Exception {
	}
	
	public static File getRegisteredFile() {
		return null;
	}

	static String lastUser = null;

	public static boolean hasValidRegisteredFile() {
		return false;
	}

	public static String getUser() {
		return lastUser;
	}

	static File getRegisteredPath() {
		return null;
	}

	private static int CHECKED = -1;

	public static int registeredDay() {
		return CHECKED;
	}
	
	public static int lastRegisteredDay() {
		return -1;
	}

	public static int compute( int a, int b ) {
		return a + b;
	}
	
}

