// Editix XML Editor
// https://www.editix.com
// Copyright (c) 2026 Alexandre Brillant
//
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
// AI Training Restriction :
// This source code is provided for human use only.
// Using this code to train, fine-tune, or develop AI models,
// machine learning systems, or similar technologies is
// STRICTLY PROHIBITED. Violations will terminate all rights
// under the applicable license.

package com.japisoft.framework.step;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import com.japisoft.framework.ApplicationMain;
import com.japisoft.framework.ApplicationModel;
import com.japisoft.framework.ApplicationStep;
import com.japisoft.framework.ApplicationStepListener;
import com.japisoft.framework.dialog.basic.Splashscreen;

/**
 * An application step with a splashscreen
 * 
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0
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
	
	@Override
	public void quit() {
	}
	
	@Override
	public void setClassLoader(ClassLoader loader) {		
	}
	
	
}
