package com.github.monet.algorithms.sssp;

import com.github.monet.worker.ServiceDirectory;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * Activator class for the LabelCorrecting algorithm.
 *
 * @author Michael Capelle
 *
 */
public class LabelCorrectingActivator implements BundleActivator {

    @Override
    public void start(BundleContext context) throws Exception {
    	System.out.println("LabelCorrecting started");
    	ServiceDirectory.registerAlgorithm(context, new LCWrapper());
    }

    @Override
    public void stop(BundleContext context) throws Exception {
    	System.out.println("LabelCorrecting finished");
    }

}
