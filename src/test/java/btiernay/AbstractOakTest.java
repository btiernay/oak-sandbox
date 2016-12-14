package btiernay;

import static com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_COMMENTS;
import static com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_SINGLE_QUOTES;
import static com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES;
import static com.google.common.base.Strings.repeat;

import java.util.function.Consumer;

import javax.jcr.Node;
import javax.jcr.Repository;
import javax.jcr.Session;

import org.apache.jackrabbit.oak.Oak;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import btiernay.jcr.convert.NodeJsonConverter;
import btiernay.jcr.convert.NodeJsonMerger;
import btiernay.jcr.oak.RepositoryService;
import btiernay.jcr.util.Queries;
import lombok.SneakyThrows;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AbstractOakTest {
  
  Repository repository;

  void execute(Consumer<Session> sessionHandler) throws Exception {
    execute(oak -> {
    }, sessionHandler);
  }

  void execute(Consumer<Oak> oakHandler, Consumer<Session> sessionHandler) throws Exception {
    val service = new RepositoryService();
    
    // Create repository
    this.repository = service.createRepository(oakHandler, sessionHandler);
  }

  @SneakyThrows
  static void query(Session session, String statement) {
    // Output
    logBanner("Executing \"{}\"", statement);

    Queries.executeQuery(session, statement);
  }

  static void logBanner(String format, Object... arguments) {
    log.info("{}", repeat("#", 100));
    log.info(format, arguments);
    log.info("{}", repeat("#", 100));
  }

  static void log(Node node) {
    log.info("JSON:\n{}", json(node));
  }

  static String json(Node node) {
    return prettyPrint(NodeJsonConverter.convertNode(node));
  }

  @SneakyThrows
  static void merge(Node node, String json) {
    val mapper = new ObjectMapper()
      .configure(ALLOW_UNQUOTED_FIELD_NAMES, true)
      .configure(ALLOW_SINGLE_QUOTES, true)
      .configure(ALLOW_COMMENTS, true);

    val objectNode = (ObjectNode) mapper.readTree(json);

    merge(node, objectNode);
  }

  static void merge(Node node, ObjectNode objectNode) {
    new NodeJsonMerger().merge(node, objectNode);
  }

  @SneakyThrows
  static String prettyPrint(JsonNode json) {
    return new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(json);
  }

}
