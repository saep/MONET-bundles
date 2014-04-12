package com.github.monet.algorithms.ea.main;

import com.github.monet.worker.ServiceDirectory;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * Activator class for the evoltuonary algorithm.
 *
 * @author Sven Selmke
 *
 */
public class EaActivator implements BundleActivator {

    @Override
    public void start(BundleContext context) throws Exception {
    	System.out.println("EA started");
    	ServiceDirectory.registerAlgorithm(context, new Evolution());
    }

    @Override
    public void stop(BundleContext context) throws Exception {
    	System.out.println("EA finished");
    }

}
