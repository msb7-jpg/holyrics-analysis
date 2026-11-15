package com.limagiran.holyrics.model;

import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.util.Images;
import com.limagiran.holyrics.util.Utils;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import javax.swing.Icon;
import javax.swing.ImageIcon;

public class Member implements Serializable {
   private static final long serialVersionUID = 12491L;
   public final long id;
   private String name;
   private final TreeSet<Member.EnumGroups> groups = new TreeSet<>();
   private String skills;
   private boolean active;
   private long version = 1L;

   public Member(String name, String skills, List<Member.EnumGroups> groups) {
      this.id = Utils.generateMillisecondId();
      this.name = name;
      this.skills = skills;
      this.groups.addAll(groups);
      this.active = true;
   }

   public Member(String name, String skills, Member.EnumGroups... groups) {
      this(name, skills, Arrays.asList(groups));
   }

   public Member(String name, String skills) {
      this(name, skills, new ArrayList<>());
   }

   public Member(String name, Member.EnumGroups... groups) {
      this(name, "", Arrays.asList(groups));
   }

   public Member() {
      this("", "", new ArrayList<>());
   }

   public Member(String name) {
      this(name, "", new ArrayList<>());
   }

   @Override
   public String toString() {
      return this.name.isEmpty() ? " " : this.name;
   }

   public String getName() {
      return this.name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getSkills() {
      return this.skills;
   }

   public void setSkills(String skills) {
      this.skills = skills;
   }

   public TreeSet<Member.EnumGroups> getGroups() {
      return this.groups;
   }

   public void setGroup(Member.EnumGroups group, boolean flag) {
      if (flag) {
         this.addGroup(group);
      } else {
         this.removeGroup(group);
      }
   }

   public void addGroup(Member.EnumGroups group) {
      if (!this.groups.contains(group)) {
         this.groups.add(group);
      }
   }

   public void removeGroup(Member.EnumGroups group) {
      this.groups.remove(group);
   }

   public int getMinGroup() {
      return this.groups.stream().map(Enum::ordinal).min((o1, o2) -> o1 - o2).orElse(999);
   }

   public boolean isVoice() {
      return this.groups.contains(Member.EnumGroups.VOICE);
   }

   public boolean isInstrument() {
      return this.groups.contains(Member.EnumGroups.INSTRUMENT);
   }

   public boolean isOrchestra() {
      return this.groups.contains(Member.EnumGroups.ORCHESTRA);
   }

   public boolean isOthers() {
      return this.groups.contains(Member.EnumGroups.OTHERS);
   }

   public boolean isActive() {
      return this.active;
   }

   public void setActive(boolean active) {
      this.active = active;
   }

   public String toStringFull() {
      return String.format(
         "%s: %s%n%s: %s%n%s: %s", I18N.WORD_NAME.val(), this.name, I18N.WORD_SKILLS.val(), this.skills, I18N.WORD_GROUPS.val(), this.groupsToString()
      );
   }

   public String toStringFullHTML() {
      return String.format(
         "<html>%s: %s<br>%s: %s<br>%s: %s",
         I18N.WORD_NAME.val(),
         this.name,
         I18N.WORD_SKILLS.val(),
         this.skills,
         I18N.WORD_GROUPS.val(),
         this.groupsToString()
      );
   }

   public String groupsToString() {
      return !this.groups.isEmpty() ? this.groups.toString().substring(1, this.groups.toString().length() - 1) : "";
   }

   @Override
   public boolean equals(Object o) {
      return o instanceof Member ? ((Member)o).id == this.id : super.equals(o);
   }

   @Override
   public int hashCode() {
      return 371 + (int)(this.id ^ this.id >>> 32);
   }

   public static Comparator<? super Member> sortByGroups() {
      return (o1, o2) -> {
         if (o1 == null) {
            return o2 == null ? 0 : -1;
         } else if (o2 == null) {
            return 1;
         } else if (o1.isActive() != o2.isActive()) {
            return Boolean.compare(o2.active, o1.active);
         } else {
            return o1.getMinGroup() != o2.getMinGroup() ? o1.getMinGroup() - o2.getMinGroup() : o1.name.compareToIgnoreCase(o2.name);
         }
      };
   }

   public Icon getIcon() {
      return new ImageIcon(this.groups.isEmpty() ? Member.EnumGroups.OTHERS.image : this.groups.first().image);
   }

   private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
      ois.defaultReadObject();
      this.version = 1L;
   }

   public static enum EnumGroups {
      VOICE(I18N.WORD_VOICE.val(), Images.MEMBER_GROUP_VOICE),
      INSTRUMENT(I18N.WORD_INSTRUMENT.val(), Images.MEMBER_GROUP_INSTRUMENT),
      ORCHESTRA(I18N.WORD_ORCHESTRA.val(), Images.MEMBER_GROUP_ORCHESTRA),
      OTHERS(I18N.WORD_OTHERS.val(), Images.MEMBER_GROUP_OTHERS);

      public final String name;
      public final BufferedImage image;

      private EnumGroups(String name, BufferedImage image) {
         this.name = name;
         this.image = image;
      }

      @Override
      public String toString() {
         return this.name;
      }
   }
}
