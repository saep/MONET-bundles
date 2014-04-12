package com.github.monet.graph.interfaces;

import java.io.OutputStream;

/**
 * Contains a method to write the graph into an output stream.
 *
 * @author Christopher Morris
 *
 */
public interface WritableObject {
	/**
	 * @param s
	 *            output stream
	 */
	public void writeObject(OutputStream s);
}
