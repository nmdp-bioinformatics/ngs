/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nmdp.ngs.fca;
/**
 * An interface for lattices.
 */
public interface Lattice {
  /**
   * Enumerated directions for lattice traversal.
   */
  public static enum Direction {
    /**
     * Go up the lattice in order
     */
    UP,
    /**
     * Go down the dual lattice in reverse order
     */
    DOWN
  }
}
