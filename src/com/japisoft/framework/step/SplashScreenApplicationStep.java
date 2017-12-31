package com.japisoft.framework.step;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import com.japisoft.framework.ApplicationMain;
import com.japisoft.framework.ApplicationModel;
import com.japisoft.framework.ApplicationStep;
import com.japisoft.framework.ApplicationStepListener;
import com.japisoft.framework.dialog.basic.Splashscreen;

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
public class SplashScreenApplicationStep implements ApplicationStep, ApplicationStepListener {
	private Icon image;

	private int registeredDay;

	private int delay = 0;

	public SplashScreenApplicationStep() {
		this(0);
	}

	/**
	 * Use the ApplicationModel.APP_IMG_PATH key, the delay is for delaying the
	 * starting in ms
	 * 
	 * @param delay
	 */
	public SplashScreenApplicationStep(int delay) {
		this(ApplicationModel.APP_IMG_PATH);
		this.delay = delay;
	}
	
	public SplashScreenApplicationStep(String imagePath) {
		this(new ImageIcon(ClassLoader.getSystemResource(imagePath)), -1);
	}

	public SplashScreenApplicationStep(String imagePath, int registeredDay) {
		this(new ImageIcon(ClassLoader.getSystemResource(imagePath)),
				registeredDay);
	}

	public SplashScreenApplicationStep(Icon image, int registeredDay) {
		this.image = image;
		this.registeredDay = registeredDay;
	}

	public void run(ApplicationStep step, int indice, int maxIndice) {
		Splashscreen.progress( indice, maxIndice );
	}	
	
	public boolean isFinal() {
		return false;
	}

	public void start(String[] args) {
		ApplicationMain.addApplicationStepListener( this );
		Splashscreen.start(image, registeredDay);

		if (delay != 0)
			try {
				Thread.sleep(delay);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

	}

	public void stop() {
		Splashscreen.stop(false);
		ApplicationMain.removeApplicationStepListener( this );
	}

}
