package dev.meirong.showcase.bookstore.utils;

import com.github.f4b6a3.ulid.UlidCreator;

public class KeyGenerator {
  private KeyGenerator() {
    throw new IllegalStateException("Utility class");
  }
  public static String next() {
      return UlidCreator.getUlid().toString();
  }
}
