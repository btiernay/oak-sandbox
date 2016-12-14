package btiernay.jcr.oak.validation;

import org.apache.jackrabbit.oak.api.CommitFailedException;
import org.apache.jackrabbit.oak.spi.commit.CommitInfo;
import org.apache.jackrabbit.oak.spi.commit.Editor;
import org.apache.jackrabbit.oak.spi.commit.EditorProvider;
import org.apache.jackrabbit.oak.spi.state.NodeBuilder;
import org.apache.jackrabbit.oak.spi.state.NodeState;

import btiernay.jcr.oak.SessionService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor 
public class ValidationEditorProvider implements EditorProvider {

  @NonNull
  private final SessionService service;

  @Override
  public Editor getRootEditor(NodeState before, NodeState after, NodeBuilder root, CommitInfo info)
    throws CommitFailedException {
    return new ValidationEditor(root, service);
  }

}