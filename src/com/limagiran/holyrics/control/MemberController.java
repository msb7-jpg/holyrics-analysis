package com.limagiran.holyrics.control;

import com.limagiran.holyrics.model.Member;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MemberController implements Serializable {
   private static final long serialVersionUID = 12491L;
   private static final String FILE_NAME = "staff.hly";
   private static final List<Member> STAFF = ControllerFactory.load("staff.hly", Member.class);

   public static void add(Member m) {
      if (!STAFF.contains(m)) {
         STAFF.add(m);
         save();
      }
   }

   public static void remove(Member m) {
      STAFF.remove(m);
      save();
   }

   public static List<Member> getAll(boolean onlyActive) {
      return new ArrayList<>(STAFF.stream().filter(m -> !onlyActive || m.isActive()).collect(Collectors.toList()));
   }

   public static Member get(long id) {
      return STAFF.stream().filter(m -> m.id == id).findFirst().orElse(null);
   }

   public static void save() {
      sort();
      ControllerFactory.save(STAFF, "staff.hly");
   }

   public static void sort() {
      STAFF.sort(Member.sortByGroups());
   }
}
