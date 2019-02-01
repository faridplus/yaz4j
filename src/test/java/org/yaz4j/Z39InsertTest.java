package org.yaz4j;

import java.util.LinkedList;
import java.util.List;
import org.junit.*;

public class Z39InsertTest {
  @Test
  public void test() {
    final int sz = 50;
    List<Z39Publisher> l = new LinkedList();
    for (int i = 0; i < sz; i++) {
      l.add(new Z39Publisher("thread" + Integer.toString(i)));
    }
    
    for (Z39Publisher p : l) {
      p.start();
    }
    for (Z39Publisher p : l) {
      Assert.assertTrue(p.join());
    }
  }
}
