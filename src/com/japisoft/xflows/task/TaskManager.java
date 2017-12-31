package com.japisoft.xflows.task;

import java.util.List;

import com.japisoft.framework.preferences.Preferences;
import com.japisoft.framework.toolkit.Logger;
import com.japisoft.xflows.XFlowsApplicationModel;
import com.japisoft.xflows.task.ui.XFlowsFactory;

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
public class TaskManager {

	public static final int FOREGROUND = 0;

	public static final int BACKGROUND = 1;

	public static final int BACKGROUND_DIALOG = 2;

	static ThreadTask currentBackgroundThread = null;

	static TaskRunnerListener listener = null;
	
	public static void setTaskRunnerListener( TaskRunnerListener l ) {
		listener = l;
	}
	
	public static boolean run(Task t, int mode) { // false : OK
		if ( t == null || 
				t.getType() == null ) {
			Logger.addWarning( "No task found" );
			if ( mode == BACKGROUND_DIALOG ) {
				XFlowsFactory.buildAndShowErrorDialog( "No task found" );
			}
			return false;
		}

		TaskLogDialog dialog = null;
		boolean errorFound = false;

		if ( mode == FOREGROUND ) {
			errorFound = basicRunner( t );
		} else if ( mode == BACKGROUND_DIALOG ) {
			dialog = new TaskLogDialog( t );
		}

		if ( mode != FOREGROUND ) {
			currentBackgroundThread = new ThreadTask( t );
			currentBackgroundThread.start();

			//			try {
			//				currentBackgroundThread.join();
			//				errorFound = currentBackgroundThread.hasErrorFound();
			//			} catch( InterruptedException exc ) {
			//			}

			currentBackgroundThread = null;
		}

		if ( mode == BACKGROUND_DIALOG ) {
			dialog.setVisible(true);

			if ( currentContext != null ) {
				try {
					currentContext.interrupt();
				} catch ( Throwable th ) {
				}
			}
			dialog.dispose();
		}

		return errorFound;
	}

	static TaskContext currentContext = null;

	public static boolean stopIt = false;

	public static void stopCurrentTask() {
		stopIt = true;
		if (currentContext != null)
			currentContext.interrupt();
	}

	static boolean basicRunner(Task t) {
		if ( listener != null )
			listener.run( t );
		currentContext = new BasicTaskContext(t);		
		try {
			TaskRunner runner = TaskElementFactory
					.getRunnerForType(t.getType());
			if (runner == null) {
				throw new RuntimeException(
						"Inner exception no runner for the type " + t.getType());
			}
			long ct = System.currentTimeMillis();
			currentContext.addInfo("Starting [" + t.getName() + "]");
			runner.run(currentContext);
			currentContext.addInfo("Task [" + t.getName() + "] terminated in "
					+ (System.currentTimeMillis() - ct) + " ms");
			boolean state = currentContext.hasErrorFound();
			currentContext = null;
			return state;
		} catch (Throwable th) {
			currentContext.addError( "Unknown task error : " + th.getMessage() );
			return true;
		}
	}

	public static boolean runXFlowsApplicationModel() {
		return run(XFlowsApplicationModel.ACCESSOR.getTasks(),
				TaskManager.FOREGROUND);
	}

	public static boolean run(List<Task> taskList, int mode) {
		stopIt = false;
		boolean find = false;

		if (mode == TaskManager.FOREGROUND) {
			for (int i = 0; i < taskList.size() && !stopIt; i++) {
				Task t = (Task) taskList.get(i);
				if (t != null && t.getType() != null) {
					find = true;
					boolean errorFound = run(t, mode);
					if (errorFound || stopIt) {
						if (true == Preferences.getPreference("scenario",
								"interruptOnError", true)) {
							Logger.addInfo("Scenario Interrupted");
							return true;
						}
					}

				}
			}
			if (!find)
				Logger.addWarning("No task found");
		} else {
			new ThreadTasks(taskList).start();
		}

		return false;
	}

	///////////////////////////////////////////////////////////////////////

	static class ThreadTasks extends Thread {
		private List<Task> tasks = null;
		
		public ThreadTasks( List<Task> tasks ) {
			this.tasks = tasks;
		}

		public void run() {
			TaskManager.run(tasks, FOREGROUND);
			tasks = null;
		}
	}

	static class ThreadTask extends Thread {

		private Task t;

		public ThreadTask(Task t) {
			this.t = t;
		}

		private boolean errorFound = false;

		public boolean hasErrorFound() {
			return errorFound;
		}

		public void run() {
			if (stopIt)
				return;
			try {
				errorFound = basicRunner(t);
			} finally {
				t = null;
			}
		}

	}

}
