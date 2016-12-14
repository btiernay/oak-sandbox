package btiernay.jcr.oak.validation;

import static btiernay.jcr.util.NodeStates.getPrimaryType;
import static lombok.AccessLevel.PRIVATE;

import org.apache.jackrabbit.oak.api.CommitFailedException;
import org.apache.jackrabbit.oak.api.PropertyState;
import org.apache.jackrabbit.oak.spi.commit.DefaultEditor;
import org.apache.jackrabbit.oak.spi.commit.Editor;
import org.apache.jackrabbit.oak.spi.state.NodeBuilder;
import org.apache.jackrabbit.oak.spi.state.NodeState;

import btiernay.jcr.oak.SessionService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.val;

@ToString
@RequiredArgsConstructor(access = PRIVATE)
public class ValidationEditor extends DefaultEditor {

  /**
   * State.
   */
  @NonNull
  private final String path;
  private final NodeBuilder parent;
  @NonNull
  private final NodeBuilder current;

  /**
   * Dependencies.
   */
  @NonNull
  private final SessionService service;

  public ValidationEditor(NodeBuilder root, SessionService service) {
    this("", null, root, service);
  }

  @Override
  public void propertyAdded(PropertyState after) throws CommitFailedException {
  }

  @Override
  public void propertyChanged(PropertyState before, PropertyState after) throws CommitFailedException {
  }

  @Override
  public void propertyDeleted(PropertyState before) throws CommitFailedException {
  }

  @Override
  public Editor childNodeAdded(String name, NodeState after) throws CommitFailedException {
    val primaryType = getPrimaryType(after);
    if (primaryType.equals("donors")) {
      current.setProperty("foo", "bar");
    }

    return newChildEditor(name);
  }

  @Override
  public Editor childNodeChanged(String name, NodeState before, NodeState after) throws CommitFailedException {
    return newChildEditor(name);
  }

  @Override
  public Editor childNodeDeleted(String name, NodeState before) throws CommitFailedException {
    return newChildEditor(name);
  }

  private ValidationEditor newChildEditor(String name) {
    val childPath = path + '/' + name;
    val childNode = current.getChildNode(name);
    return new ValidationEditor(childPath, current, childNode, service);
  }

}