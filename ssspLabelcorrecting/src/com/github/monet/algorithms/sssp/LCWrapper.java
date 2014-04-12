package com.github.monet.algorithms.sssp;

import com.github.monet.interfaces.Meter;
import com.github.monet.worker.Job;
import com.github.monet.worker.ServiceDirectory;
import com.github.monet.graph.*;

public class LCWrapper implements com.github.monet.interfaces.Algorithm {

	@Override
	public void execute(Job job, Meter meter, ServiceDirectory serviceDir) throws Exception{
		LabelCorrecting<SimpleDirectedGraph, SimpleNode, SimpleEdge> lc = new LabelCorrecting<SimpleDirectedGraph, SimpleNode, SimpleEdge>();
		lc.execute(job, meter, serviceDir);
	}
}
