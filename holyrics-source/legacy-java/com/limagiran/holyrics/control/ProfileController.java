package com.limagiran.holyrics.control;

import com.limagiran.holyrics.dao.MusicDao;
import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.model.Music;
import com.limagiran.holyrics.model.Profile;
import com.limagiran.holyrics.util.BooleanOptional;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ProfileController implements Serializable {
   private static final long serialVersionUID = 1L;
   private static final String FILE_NAME = "profiles.hly";
   private static final List<Profile> PROFILES = ControllerFactory.load("profiles.hly", Profile.class);

   public static void add(Profile p) {
      if (!PROFILES.contains(p)) {
         PROFILES.add(p);
         save();
      }
   }

   public static void remove(Profile p) {
      PROFILES.remove(p);
      save();
   }

   public static Profile create() {
      String np = I18N.WORD_NEW_PROFILE.val();
      if (!contains(np)) {
         return new Profile(np);
      } else {
         int i = 0;
         String s = np + " (%d)";

         while (contains(String.format(s, ++i))) {
         }

         return new Profile(String.format(s, i));
      }
   }

   public static List<Profile> getProfiles() {
      return new ArrayList<>(PROFILES).stream().filter(Objects::nonNull).collect(Collectors.toList());
   }

   public static List<Profile> getProfilesInOrderOfAmount(boolean nullable) {
      return (nullable ? PROFILES : getProfiles())
         .stream()
         .sorted((p1, p2) -> p1 == null ? Integer.MIN_VALUE : (p2 == null ? Integer.MAX_VALUE : p2.getMusicsId().size() - p1.getMusicsId().size()))
         .collect(Collectors.toList());
   }

   public static void save() {
      if (!PROFILES.contains(null)) {
         PROFILES.add(0, null);
      }

      ControllerFactory.save(PROFILES, "profiles.hly");
   }

   public static void save(List<Profile> list) {
      PROFILES.clear();
      PROFILES.addAll(list.stream().distinct().collect(Collectors.toList()));
      if (!PROFILES.contains(null)) {
         PROFILES.add(0, null);
      }

      ControllerFactory.save(PROFILES, "profiles.hly");
   }

   public static boolean contains(String name) {
      return getProfiles().stream().anyMatch(p -> p.getName().equals(name));
   }

   static {
      BooleanOptional.runIfTrue(!PROFILES.contains(null), () -> PROFILES.add(0, null));
      List<Long> ids = MusicDao.getAll().parallelStream().map(Music::getId).collect(Collectors.toList());
      PROFILES.stream().filter(Objects::nonNull).forEach(p -> p.removeIdsIfNotExists(ids));
   }
}
