package com.github.monet.algorithms.mst.twophase;

import com.github.monet.algorithms.mst.TwophaseAlgorithm;
import com.github.monet.worker.ServiceDirectory;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class TwoPhaseActivator implements BundleActivator {

	@Override
	public void start(BundleContext bc) throws Exception {
		ServiceDirectory.registerAlgorithm(bc, new TwophaseAlgorithm(
				new com.github.monet.algorithms.Kruskal()));
	}

	@Override
	public void stop(BundleContext bc) throws Exception {
	}
}
