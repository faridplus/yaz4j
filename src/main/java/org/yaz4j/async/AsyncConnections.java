/*
 * Copyright (c) 1995-2015, Index Data
 * All rights reserved.
 * See the file LICENSE for details.
 */
package org.yaz4j.async;

import java.util.ArrayList;
import java.util.List;
import org.yaz4j.Connection;
import org.yaz4j.jni.SWIGTYPE_p_p_ZOOM_connection_p;
import static org.yaz4j.jni.yaz4jlib.*;
import static java.lang.System.out;
import org.yaz4j.util.Unstable;

/**
 * Allows to group and execute asynchronous connections within an event loop.
 * 
 * @author jakub
 */
@Unstable
public class AsyncConnections {
  private List<AsyncConnection> conns = new ArrayList<AsyncConnection>();
  
  /**
   * Include async connection in the event loop processing.
   * @param conn 
   */
  public void add(AsyncConnection conn) {
    conns.add(conn);
  }

  /**
   * List all included connections.
   * @return 
   */
  public List<AsyncConnection> getConnections() {
    return conns;
  }
  
  /**
   * Start the event loop, which effectively executes and processes all 
   * async connections.
   */
  public void start() {
    SWIGTYPE_p_p_ZOOM_connection_p c_conns = new_zoomConnectionArray(conns.size());
    try {
      for (int i=0; i<conns.size(); i++) {
        AsyncConnection conn = conns.get(i);
        zoomConnectionArray_setitem(c_conns, i, conn.getNativeConnection());
      }
      int ret = 0;
      while ((ret = ZOOM_event(conns.size(), c_conns)) != 0) {
        int idx = ret - 1;
        int last = ZOOM_connection_last_event(zoomConnectionArray_getitem(c_conns, idx));
        AsyncConnection conn = conns.get(idx);
        String event = ZOOM_get_event_str(last);
        out.println("Received event " + event + " on connection #"+idx);
        switch (last) {
          case ZOOM_EVENT_RECV_SEARCH: conn.handleSearch(); break;
          case ZOOM_EVENT_RECV_RECORD: conn.handleRecord(); break;
          case ZOOM_EVENT_END: conn.handleError(); break;
        }
      }
    } finally {
      delete_zoomConnectionArray(c_conns);
    }
  }

}
