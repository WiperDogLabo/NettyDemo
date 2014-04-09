package org.wiperdog.netty.test;

import groovy.lang.GroovyShell;

import java.io.File;
import java.io.IOException;

import org.osgi.framework.BundleContext;
import org.wiperdog.directorywatcher.Listener;
/**
 * The listener to watching the change of file in groovy file folder 
 * if new file added or file modified ,evaluate file using GroovyShell
 *
 */
public class ServletListener implements Listener {
	public String directory;
	public GroovyShell shell;

	public ServletListener(String directory, GroovyShell shell) {
		this.directory = directory;
		this.shell = shell;
	}

	@Override
	public boolean notifyModified(File target) throws IOException {
		try {
			shell.evaluate(target);
			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean notifyAdded(File target) throws IOException {
		try {
			shell.evaluate(target);
			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean notifyDeleted(File target) throws IOException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getDirectory() {
		return directory;
	}

	@Override
	public long getInterval() {
		return 0;
	}

	@Override
	public boolean filterFile(File file) {

		if (file.getName().endsWith(".groovy")) {
			return true;
		}
		return false;
	}
}
