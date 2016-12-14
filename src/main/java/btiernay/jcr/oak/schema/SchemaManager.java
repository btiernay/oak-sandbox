package btiernay.jcr.oak.schema;

import org.apache.jackrabbit.oak.spi.lifecycle.RepositoryInitializer;
import org.apache.jackrabbit.oak.spi.state.NodeBuilder;

import lombok.val;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SchemaManager implements RepositoryInitializer {

  @Override
  public void initialize(NodeBuilder builder) {
    log.info("Initializing schema...");
    val schemas = getSchemas(builder);
    if (!schemas.exists()) {
      log.info("Loading schema...");
    }
  }

  private NodeBuilder getSchemas(NodeBuilder builder) {
    return builder.child("schemas");
  }

}
