package btiernay.jcr.util;

import javax.jcr.Session;

import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;

@RequiredArgsConstructor
public class ClosableSession implements Session, AutoCloseable {

  @Delegate
  private final Session delegate;

  @Override
  public void close() {
    delegate.logout();
  }

}