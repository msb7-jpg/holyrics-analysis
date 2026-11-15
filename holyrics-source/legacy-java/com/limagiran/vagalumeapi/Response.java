package com.limagiran.vagalumeapi;

import java.util.ArrayList;
import java.util.List;

public class Response {
   public final int numFound;
   public final int start;
   public final List<Doc> docs = new ArrayList<>();

   public Response(int numFound, int start) {
      this.numFound = numFound;
      this.start = start;
   }
}
