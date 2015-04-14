/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.nmdp.ngs.fca;

import java.util.List;

import com.google.common.collect.ImmutableList;

import org.dishevelled.bitset.MutableBitSet;

/**
 *
 * @author int33484
 */
public class TestUtil {
    public static MutableBitSet bits(long... indexes) {
        MutableBitSet bits = new MutableBitSet();
      
        for (long index : indexes) {
          bits.flip(index);
        }
        return bits;
    }
    
    public static List list(String... elements) {
        return new ImmutableList.Builder<String>().add(elements).build();
    }
}
