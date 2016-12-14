package btiernay.jcr.oak;

import javax.jcr.LoginException;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.SimpleCredentials;

import btiernay.jcr.util.ClosableSession;
import lombok.SneakyThrows;
import lombok.ToString;
import lombok.val;

@ToString
public class SessionService {

  Repository repository;

  @SneakyThrows
  public ClosableSession createSession() {
    return createSession(repository);
  }

  private ClosableSession createSession(Repository repository) throws LoginException, RepositoryException {
    val admin = new SimpleCredentials("admin", "admin".toCharArray());
    return new ClosableSession(repository.login(admin));
  }
  
}