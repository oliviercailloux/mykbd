package io.github.oliviercailloux.mykbd;

import io.github.oliviercailloux.jaris.xml.DomHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App {
  @SuppressWarnings("unused")
  private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

  public static final DomHelper DOM_HELPER = DomHelper.domHelper();
  
  public static void main(String[] args) throws Exception {
    MyKbd myKbd = MyKbd.create();
    myKbd.patch();
    myKbd.write();
  }
}
