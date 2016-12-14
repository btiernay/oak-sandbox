package btiernay.jcr.util;

import org.apache.jackrabbit.JcrConstants;
import org.apache.jackrabbit.oak.api.Type;
import org.apache.jackrabbit.oak.spi.state.NodeState;

import lombok.val;

public class NodeStates {

  public static String getPrimaryType(NodeState state) {
    val property = state.getProperty(JcrConstants.JCR_PRIMARYTYPE);
    if (property == null) return null;
    
    return property.getValue(Type.NAME);
  }

  public static Iterable<String> getMixins(NodeState state) {
    val property = state.getProperty(JcrConstants.JCR_MIXINTYPES);
    if (property == null) return null;
    
    return property.getValue(Type.NAMES);
  }

  
}
