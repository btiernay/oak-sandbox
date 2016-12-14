package btiernay;

import static org.apache.jackrabbit.oak.spi.whiteboard.WhiteboardUtils.getService;

import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.jackrabbit.oak.Oak;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;

import btiernay.jcr.oak.SessionService;
import btiernay.jcr.oak.logging.LoggingCommitHook;
import btiernay.jcr.oak.logging.LoggingObserver;
import btiernay.jcr.oak.logging.LoggingValidator;
import btiernay.jcr.oak.schema.SchemaManager;
import btiernay.jcr.oak.validation.ValidationEditorProvider;
import lombok.SneakyThrows;
import lombok.val;

public class OakTest extends AbstractOakTest {

  @Test
  public void testOak() throws Exception {
    // Bootstrap and run within a session
    execute(this::init, this::run);
  }

  private void init(Oak oak) {
    val service = getService(oak.getWhiteboard(), SessionService.class);
    oak
      .with(new SchemaManager())
      .with(new ValidationEditorProvider(service))
      .with(new LoggingCommitHook())
      .with(new LoggingValidator())
      .with(new LoggingObserver());
  }

  @SneakyThrows
  private void run(Session session) {
    // Setup data
    insert(session);

    // Dump contents
    list(session);

    // Run some sample queries
    query(session);
  }

  private void insert(Session session) throws Exception {
    logBanner("Inserting...");
    val root = session.getRootNode();
    val entities = root.addNode("entities", "nt:unstructured");
    val donors = entities.addNode("donors", "donors");

    // Create donor -< specimen -< sample tree
    {
      val donor1 = donors.addNode("d1", "donor");
      donor1.setProperty("donor_id", "d1");
      {
        val specimen = donor1.getNode("specimens");
        {
          val specimen1 = specimen.addNode("sp1", "specimen");
          specimen1.setProperty("specimen_id", "sp1");
          {
            val samples1 = specimen1.getNode("samples");
            {
              val sample11 = samples1.addNode("sa11", "sample");
              sample11.setProperty("sample_id", "sa11");
            }
            {
              val sample12 = samples1.addNode("sa12", "sample");
              sample12.setProperty("sample_id", "sa12");
            }
          }
        }
        {
          val specimen2 = specimen.addNode("sp2", "specimen");
          specimen2.setProperty("specimen_id", "sp2");
        }
      }
    }

    {
      val donor2 = donors.addNode("d2", "donor");

      merge(donor2,
        "{ donor_id: 'd2', specimens: { sp3: { specimen_id: 'sp3', samples: { sa1: { sample_id: 'sa31' } } } } }");
    }

    // Persist to disk
    session.save();

    // Dump contents
    logBanner("Listing...");
    log(entities);
  }

  private void list(Session session) throws PathNotFoundException, RepositoryException, JsonProcessingException {
    logBanner("Listing...");
    val entities = session.getRootNode().getNode("entities");
    log(entities);
  }

  private static void query(Session session) throws PathNotFoundException, RepositoryException {
    // E.g: Look up by a single property by a single property
    query(session, "SELECT d.donor_id FROM donor AS d WHERE donor_id = 'd1'");

    // E.g: Get all (donor_id, specimen_id) combinations
    query(session,
      "SELECT d.donor_id, sp.specimen_id FROM donor AS d INNER JOIN specimen AS sp ON ISCHILDNODE(d, sp)");

    // E.g: Get all properties of an entity at a path
    query(session, "SELECT * FROM [quiddity:entity] WHERE [jcr:path] = '/entities/donors/d1/specimens/sp1'");

    // E.g: Lookup by path
    val specimen = session.getNode("/entities/donors/d1/specimens/sp1");
    log(specimen);

    // E.g: Lookup specimen by id
    query(session, "SELECT * FROM specimen WHERE specimen_id = 'sp1'");

    // E.g: Lookup specimen by node name
    query(session, "SELECT * FROM specimen AS sp WHERE NAME(sp) = 'sp1'");
  }

}
