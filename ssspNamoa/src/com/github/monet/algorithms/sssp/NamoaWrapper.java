package com.github.monet.algorithms.sssp;

import com.github.monet.interfaces.Meter;
import com.github.monet.worker.Job;
import com.github.monet.worker.ServiceDirectory;
import com.github.monet.graph.*;

public class NamoaWrapper implements com.github.monet.interfaces.Algorithm {

	@Override
	public void execute(Job job, Meter meter, ServiceDirectory serviceDir)
			throws Exception {
		Namoa<SimpleDirectedGraph, SimpleNode, SimpleEdge> namoa = new Namoa<SimpleDirectedGraph, SimpleNode, SimpleEdge>();
		namoa.execute(job, meter, serviceDir);
	}

}
