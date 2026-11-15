package com.limagiran.javaupdate;

public class JavaUpdate {
   public final Update update;
   public final IUpdate app;

   public JavaUpdate(IUpdate app) {
      this.app = app;
      this.update = new Update(app);
   }
}
