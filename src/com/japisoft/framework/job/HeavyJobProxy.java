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

package com.japisoft.framework.job;

/**
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0
 * */
public class HeavyJobProxy extends BasicJob implements HeavyJob {

	private HeavyJob job;

	/** If the job implements the interface SilenceJob, no notification will be
	 * done
	 */
	public HeavyJobProxy( HeavyJob job ) {
		this.job = job;
	}

	public String getName() {
		return job.getName();
	}

	public boolean isAlone() {
		return job.isAlone();
	}
	
	public String getErrorMessage() {
		return job.getErrorMessage();
	}

	public void stopIt() {
		job.stopIt();
	}
	
	public Object getSource() {
		return job.getSource();
	}
	
	public void dispose() {
		job.dispose();
	}
	
	public void run() {
		try {
			if ( !( job instanceof SilenceJob ) )
				JobManager.COMMON_MANAGER.notifyStartJob( job, true );
			job.run();
		} finally {
			if ( !( job instanceof SilenceJob ) )
				JobManager.COMMON_MANAGER.notifyStopJob( job, true );
			job.dispose();
		}
	}
	
}

