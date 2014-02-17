package com.internetitem.gitdown;

import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.TreeFilter;

public class GitFileFilter extends TreeFilter {

	private String path;
	private boolean caseSensitive;

	public GitFileFilter(boolean caseSensitive, String path) {
		System.err.println("Case sensitive: " + caseSensitive);
		this.caseSensitive = caseSensitive;
		this.path = path;
	}

	/** @return the path this filter matches. */
	public String getPath() {
		return path;
	}

	@Override
	public boolean include(TreeWalk walker) {
		if (caseSensitive) {
			return isPathPrefix(walker.getPathString(), path);
		} else {
			return isPathPrefix(walker.getPathString().toLowerCase(), path.toLowerCase());
		}
	}

	private boolean isPathPrefix(String path1, String path2) {
		int ci;

		for (ci = 0; ci < path1.length() && ci < path2.length(); ci++) {
			if (path1.charAt(ci) != path2.charAt(ci)) {
				return false;
			}
		}

		if (ci < path1.length()) {
			// Ran out of pattern but we still had current data.
			// If c[ci] == '/' then pattern matches the subtree.
			// Otherwise we cannot be certain so we return -1.
			//
			return path1.charAt(ci) == '/' ? true : false;
		}

		if (ci < path2.length()) {
			// Ran out of current, but we still have pattern data.
			// If p[ci] == '/' then pattern matches this subtree,
			// otherwise we cannot be certain so we return -1.
			//
			return path2.charAt(ci) == '/' ? true : false;
		}

		// Both strings are identical.
		//
		return true;
	}

	@Override
	public boolean shouldBeRecursive() {
		System.err.println("shouldBeRecursive: " + path.contains("/"));
		return path.contains("/");
	}

	@Override
	public GitFileFilter clone() {
		return this;
	}

	public String toString() {
		return "PATH(\"" + path + "\")";
	}

	/**
	 * @param walker
	 *            The walk to check against.
	 * @return {@code true} if the path length of this filter matches the length
	 *         of the current path of the supplied TreeWalk.
	 */
	public boolean isDone(TreeWalk walker) {
		return path.length() == walker.getPathLength();
	}
}
