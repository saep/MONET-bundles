package com.github.monet.parser;


import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import com.github.monet.worker.ServiceDirectory;

public class Activator implements BundleActivator {

	@Override
	public void start(BundleContext context) throws Exception {
		System.out.println("Monet parser activated.");
		ServiceDirectory.registerGraphParser(context, new MonetHeurGridParser());
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		System.out.println("Monet parser stopped.");
	}

}
