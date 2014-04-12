package com.github.monet.generator;

import java.awt.geom.Point2D;

public class EuclideanMetric implements Metric {
	public Double d(Point2D.Double p1, Point2D.Double p2) {
		double distance = Math.sqrt(Math.abs(p1.getX() - p2.getX()) + Math.abs(p1.getY() - p2.getY()));
		return distance;
	}
}
