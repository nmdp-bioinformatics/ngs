/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.nmdp.ngs.fca;

import java.util.List;
/**
 *
 * @author int33484
 */
public interface Context<G, M> {
  
  public List getObjects();

  public List getAttributes();

  public Concept bottom();

  public Concept top();
  
  public Concept insert(G object, List<M> attributes);

  public Concept greatestLowerBound(final List query);
  
  public Concept leastUpperBound(final List query);

  public int support(final List query);

  public int support(final List left, List right);

  public double marginal(final List query);

  public double joint(final List left, final List right);

  public double conditional(final List left, final List right);
}
