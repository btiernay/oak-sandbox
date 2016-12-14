package btiernay.jcr.oak;

import static java.util.Collections.emptyMap;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.function.Consumer;

import javax.jcr.Repository;
import javax.jcr.Session;

import org.apache.jackrabbit.commons.cnd.CndImporter;
import org.apache.jackrabbit.oak.Oak;
import org.apache.jackrabbit.oak.jcr.Jcr;

import com.google.common.io.CharStreams;
import com.google.common.io.Resources;

import lombok.SneakyThrows;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RepositoryService {

  @SneakyThrows
  public Repository createRepository(Consumer<Oak> oakHandler, Consumer<Session> sessionHandler) {
    val service = new SessionService();

    val oak = new Oak();
    oak.getWhiteboard().register(SessionService.class, service, emptyMap());
    oakHandler.accept(oak);

    val repository = new Jcr(oak).createRepository();
    service.repository = repository;
    
    val session = service.createSession();
    register(session);
    
    sessionHandler.accept(session);

    return repository;
  }
  
  private void register(Session session) throws Exception {
    // Namespaces
    val namespaceRegistry = session.getWorkspace().getNamespaceRegistry();
    namespaceRegistry.registerNamespace("quiddity", "http://quiddity.org/ns/quddity");
    namespaceRegistry.registerNamespace("user", "http://quiddity.org/ns/user");

    // Node types
    log.info("Node Types:");
    for (val line : CharStreams.readLines(getNodeTypes())) {
      log.info("{}", line);
    }
    CndImporter.registerNodeTypes(getNodeTypes(), session);
  }
  
  private static Reader getNodeTypes() throws IOException {
    return new InputStreamReader(Resources.getResource("node-types.cnd").openStream());
  }


}
