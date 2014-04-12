package com.github.monet.algorithms.sssp;

import com.github.monet.worker.ServiceDirectory;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * Activator class for the Namoa* algorithm.
 *
 * @author Michael Capelle
 *
 */
public class NamoaActivator implements BundleActivator {

    @Override
    public void start(BundleContext context) throws Exception {
    	System.out.println("Namoa* started");
    	ServiceDirectory.registerAlgorithm(context, new NamoaWrapper());
    }

    @Override
    public void stop(BundleContext context) throws Exception {
    	System.out.println("Namoa* finished");
    }

}
