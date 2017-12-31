package com.japisoft.framework.job;

import java.util.Vector;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

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
public final class JobManager implements Runnable {

	/** Delay for job. This is a way to manage the job frequency */
	public static int JOB_DELAY_BOUND = 200;

	// Need a synchronized version
	private Vector workList = new Vector();
	
	private boolean aliveMode = false;

	private static int ADAPTING_JOB_DELAY = JOB_DELAY_BOUND; 
	
	public void run() {
		try {
			boolean beFast = false;
			for (; aliveMode;) {
				flushJobs = false;
				Thread.sleep(ADAPTING_JOB_DELAY);
				flushJobs = true;
				beFast = runAll();
				if ( beFast )
					ADAPTING_JOB_DELAY = ( JOB_DELAY_BOUND / 2 );
				else
					ADAPTING_JOB_DELAY = JOB_DELAY_BOUND;
			}
		} catch (InterruptedException exc) {
			// Dye it
		}
	}

	public static JobManager COMMON_MANAGER = new JobManager();

	private static Thread COMMON_MANAGER_THREAD = null;

	private static JobManagerListener listener = null;

	/** Set a listener about current job */
	public static void setJobManagerListener(JobManagerListener vlistener) {
		listener = vlistener;
	}

	private boolean flushJobs = false;

	/** @return <code>true</code> if this job is running */
	public static boolean isRunning( Job run ) {
		return COMMON_MANAGER.runningJob == run;
	}

	/** Add a new job. It will be managed when possible */
	public static void addJob(Job run) {

		boolean addIt = true;
		boolean stop = false;
		
		if ( run instanceof HeavyJob ) {
			COMMON_MANAGER.run( (HeavyJob)run );
		} else {
		
			if (run.isAlone()) {
				// Search for the same JOB class in the current
				// workList and replace it by this one
				Object source = run.getSource();
	
				try {
				
				for (int i = 0; i < COMMON_MANAGER.workList.size(); i++) {
					JobWrapper jw = ( JobWrapper )COMMON_MANAGER.workList.get( i );
					Job j = jw.job;

					if (j.getSource() == source
							&& j.getClass() == run.getClass() ) {
						jw.markedStopped = true;
						j.stopIt();
						stop = true;
					}
				}
				
				} catch( ArrayIndexOutOfBoundsException exc ) {
					// ?
				}
			}
	
			if( COMMON_MANAGER_THREAD == null || 
					!COMMON_MANAGER_THREAD.isAlive() ){
				COMMON_MANAGER_THREAD = new Thread(
						COMMON_MANAGER, 
						"COMMON_MANAGER_THREAD" );
				COMMON_MANAGER.aliveMode = true;
				COMMON_MANAGER_THREAD.start();
			}

			COMMON_MANAGER.workList.add( new JobWrapper( run ) );
		}
	}

	/** Dispose the current manager. It will stop the Thread */
	public void dispose() {
		aliveMode = false;
		COMMON_MANAGER.notify();
		try {
			COMMON_MANAGER_THREAD.join();
		} catch (InterruptedException th) {
		}
	}

	/**
	 * @return <code>true</code> if this job is terminated or has not been
	 *         managed
	 */
	public boolean isTerminated(String jobName) {
		try {
			for (int i = 0; i < workList.size(); i++) {
				Object o = workList.get(i);
				if (o instanceof KnownJob) {
					if (jobName.equals(((KnownJob) o).getName()))
						return false;
				}
			}
		} catch (Throwable th) {
			return false;
		}
		return true;
	}

	private Runnable runningJob;
	public static boolean working = false;

	private boolean runAll() throws InterruptedException {
		if (workList.size() == 0) {
			working = false;
			return true;
		}
		boolean fastJob = true;
		// Create a snapshot for workList change
		Object[] content = workList.toArray();
		working = content.length > 0;
		for (int i = 0; i < content.length; i++) {
			JobWrapper jw = ( JobWrapper )content[ i ];
			if ( !jw.markedStopped ) {
				run( runningJob = jw.job );
				runningJob = null;
				if ( !( jw.job instanceof FastJob ) )
					fastJob = false;					
				dispose( jw.job );
				Thread.yield();
				Thread.sleep( 5 );
			}
		}
		for  (int i = 0; i < content.length; i++ ) {
			workList.remove( content[ i ] );
		}
		working = false;
		return fastJob;
	}

	public static boolean isTaskWorking( Class[] jobCl ) {
		// Create a snapshot for workList change
		Object[] content = COMMON_MANAGER.workList.toArray();
		for (int i = 0; i < content.length; i++) {
			JobWrapper jw = ( JobWrapper )content[ i ];
			if ( !jw.markedStopped ) {
				for ( int j = 0; j < jobCl.length; j++ ) {
					if ( jw.job.getClass() == jobCl[ j ] )
						return true;
				}
			}
		}
		return false;
	}

	private void run(Runnable job) {
		try {
			if (listener != null) {
				if (job instanceof KnownJob)
					notifyStartJob( (KnownJob)job, false );
			}
			if (job instanceof SwingEventSynchro) {
				boolean result = ((SwingEventSynchro) job).preRun();
				if (result) {
					
					// If the event thread is started

					SwingUtilities.invokeAndWait(job);
				}
			} else
				job.run();
		} catch (Throwable th) {
			th.printStackTrace();
		}

		if ( listener != null ) {
			if ( job instanceof KnownJob )
				notifyStopJob( ( KnownJob )job, false );
		}
	}
	
	void notifyStartJob( KnownJob job, boolean heavy ) {
		if ( listener != null )
			listener.startKnownJob( 
					job, 
					( ( KnownJob ) job ).getName(), heavy );
	}

	void notifyStopJob( KnownJob job, boolean heavy ) {
		if ( listener != null ) {
			String errorMsg = job.getErrorMessage();
			if ( errorMsg == null && job.hasErrors() ) {
				errorMsg = "Error(s) found";
			}
			listener.stopKnownJob( job.getName(), errorMsg, heavy );
		}
	}

	private void run( HeavyJob job ) {
		new Thread( new HeavyJobProxy( job ) ).start();
	}
	
	private void dispose( Object job ) {
		if (job instanceof Job) {
			try {
				((Job) job).dispose();
			} catch( Throwable th ) {
			}
		}
	}
	
	static class JobWrapper {
		Job job;
		boolean markedStopped;
		
		public JobWrapper( Job job )  {
			this.job = job;
		}
	}
	
}
