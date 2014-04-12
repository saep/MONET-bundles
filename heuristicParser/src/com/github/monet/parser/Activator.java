package com.github.monet.parser;

import com.github.monet.worker.ServiceDirectory;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

	@Override
	public void start(BundleContext context) throws Exception {
		System.out.println("Monet parser activated.");
		ServiceDirectory.registerGraphParser(context, new MonetHeurParser());
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		System.out.println("Monet parser stopped.");
	}

}
