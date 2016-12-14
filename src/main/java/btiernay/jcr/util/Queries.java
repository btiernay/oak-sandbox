package btiernay.jcr.util;

import java.util.Arrays;

import javax.jcr.Session;
import javax.jcr.query.Query;

import lombok.SneakyThrows;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Queries {

  @SneakyThrows
  public static void executeQuery(Session session, String statement) {
    // Execute
    val queryManager = session.getWorkspace().getQueryManager();
    val query = queryManager.createQuery(statement, Query.JCR_SQL2); // SQL
    val result = query.execute();

    // Output
    log.info("Query:          {} ", statement);
    log.info("Column Names:   {}", Arrays.toString(result.getColumnNames()));
    log.info("Selector Names: {}", Arrays.toString(result.getSelectorNames()));
    log.info("Nodes:          Skipped, see Rows instead");

    log.info("Rows:");
    val rows = result.getRows();

    int i = 1;
    while (rows.hasNext()) {
      val row = rows.nextRow();
      log.info(" - Row[{}]: path: {}, score: {}", i, row.getPath(), row.getScore(),
        Arrays.toString(row.getValues()));

      log.info("   values:");
      for (val selectorName : result.getColumnNames()) {
        log.info("   - {} = {}", selectorName, row.getValue(selectorName));
      }
      log.info("   nodes:");
      for (val selectorName : result.getSelectorNames()) {
        log.info("   - {} = {}", selectorName, row.getNode(selectorName));
      }

      i++;
    }
  }
  
}
