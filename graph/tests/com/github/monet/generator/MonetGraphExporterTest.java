package com.github.monet.generator;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;

import com.github.monet.generator.MonetGraphExporter;
import com.github.monet.generator.MonetGraphGenerator;
import com.github.monet.graph.AnnotatedGraph;
import com.github.monet.graph.SimpleDirectedGraph;
import com.github.monet.graph.SimpleEdge;
import com.github.monet.graph.SimpleNode;

public class MonetGraphExporterTest {

  @Test
  public void testExporting() {
    // build up path to test file
    String savePath = System.getProperty("user.dir") + "/";
    String fileName = "monetExportTest.txt";

    // initialize graph generator to generate test graph
    MonetGraphGenerator<SimpleDirectedGraph> gn = new MonetGraphGenerator<>("undirected", 5, 2, 1.2);

    AnnotatedGraph<SimpleNode, SimpleEdge, SimpleDirectedGraph> graph = gn.getGraph();
    MonetGraphExporter<SimpleNode, SimpleEdge, SimpleDirectedGraph> exporter =
        new MonetGraphExporter<SimpleNode, SimpleEdge, SimpleDirectedGraph>();
    exporter.export(savePath, fileName, graph);

    File generatedFile = new File(savePath + fileName);
    assertTrue(generatedFile.isFile());
    assertTrue(generatedFile.delete());
  }
}
