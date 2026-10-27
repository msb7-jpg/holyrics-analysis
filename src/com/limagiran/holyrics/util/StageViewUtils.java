package com.limagiran.holyrics.util;

import com.limagiran.holyrics.control.MusicController;
import com.limagiran.holyrics.control.ThemeController;
import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.model.Presentation;
import com.limagiran.holyrics.model.ScreenDeviceSettings;
import com.limagiran.holyrics.model.theme.Theme;
import com.limagiran.holyrics.view.PresentationJDialog;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class StageViewUtils {
   private static final String HACK_BREAK_LINE = "<#4(|<-|3|_@>";
   public static final String HELP_MESSAGE_GRAPHICS_CARD_HTML = "Obs.: A função <b>visão do palco</b> funciona apenas com saídas de vídeos diferentes diretamente da placa de vídeo do computador. Mesmo que você utilize 2 ou mais monitores para projeção mas eles utilizem a mesma saída de vídeo (com um adaptador que duplica o cabo, por exemplo), não é possível utilizar o modo <b>visão do palco</b>.";

   public static Presentation apply(Presentation display, List<Presentation> presentations, PresentationJDialog frame) {
      return apply(display, presentations, frame.device.deviceType, frame.device.getScreenDeviceSettings().getStageViewSettings(), frame);
   }

   public static Presentation apply(
      Presentation display, List<Presentation> presentations, ScreenDeviceSettings.EnumDeviceType deviceType, ScreenDeviceSettings.StageViewSettings svs
   ) {
      return apply(display, presentations, deviceType, svs, null);
   }

   public static Presentation apply(
      Presentation _display,
      List<Presentation> _presentations,
      ScreenDeviceSettings.EnumDeviceType deviceType,
      ScreenDeviceSettings.StageViewSettings svs,
      PresentationJDialog updateTheme
   ) {
      try {
         Presentation display = Presentation.create(_display.text.getClone(), _display.theme, _display.separatorOrEnd);
         if (svs.isEnabled() && (deviceType.equals(ScreenDeviceSettings.EnumDeviceType.MUSIC) || deviceType.equals(ScreenDeviceSettings.EnumDeviceType.BIBLE))) {
            List<Presentation> presentations = _presentations.stream()
               .map(p -> Presentation.create(p.text.getClone(), p.theme, p.separatorOrEnd))
               .collect(Collectors.toList());
            if (display.text.isAutomaticShow() && display.text.getSource() instanceof Presentation) {
               presentations.add((Presentation)display.text.getSource());
            }

            Presentation currentPresentation = presentations.remove(0);
            Function<StageViewUtils.EnumStageViewMode, Presentation> getNewPresentation = en -> {
               Supplier<Boolean> nextPresentationIsSeparatorOrEnd = () -> presentations.isEmpty() || presentations.get(0).separatorOrEnd;
               switch (en) {
                  case CURRENT_AND_NEXT_SLIDE:
                     String text;
                     if (nextPresentationIsSeparatorOrEnd.get()) {
                        text = StageViewUtils.StageViewFormat.W_END;
                     } else {
                        Presentation nextPresentation = presentations.get(0);
                        if (nextPresentation.text.getSlideType().isTitle()) {
                           text = StageViewUtils.StageViewFormat.titleAndArtist(nextPresentation.text.getText().split("\n"));
                        } else {
                           text = nextPresentation.text.getText();
                        }
                     }

                     MusicController.Paragraph parag = currentPresentation.text
                        .getClone()
                        .setText(StageViewUtils.StageViewFormat.formatIfTitleOrEnd(currentPresentation) + en.getSeparator() + text);
                     return Presentation.create(parag, currentPresentation.theme);
                  case FIRST_LINE_OF_THE_NEXT_SLIDE_WITH_SEPARATOR:
                  case FIRST_LINE_OF_THE_NEXT_SLIDE_WITHOUT_SEPARATOR:
                     String text;
                     if (nextPresentationIsSeparatorOrEnd.get()) {
                        text = StageViewUtils.StageViewFormat.W_END;
                     } else {
                        Presentation nextPresentation = presentations.get(0);
                        if (nextPresentation.text.getSlideType().isTitle()) {
                           text = StageViewUtils.StageViewFormat.titleAndArtist(nextPresentation.text.getText().split("\n"));
                        } else {
                           text = "..." + nextPresentation.text.getText().split("\n")[0];
                        }
                     }

                     MusicController.Paragraph parag = currentPresentation.text
                        .getClone()
                        .setText(StageViewUtils.StageViewFormat.formatIfTitleOrEnd(currentPresentation) + en.getSeparator() + text);
                     return Presentation.create(parag, currentPresentation.theme);
                  case CURRENT_SLIDE:
                     return Presentation.create(currentPresentation.text, currentPresentation.theme);
                  case NEXT_SLIDE:
                     if (nextPresentationIsSeparatorOrEnd.get()) {
                        return Presentation.create(new MusicController.Paragraph(StageViewUtils.StageViewFormat.W_END), currentPresentation.theme);
                     }

                     Presentation nextPresentation = presentations.get(0);
                     MusicController.Paragraph parag = nextPresentation.text
                        .getClone()
                        .setText(StageViewUtils.StageViewFormat.formatIfTitleOrEnd(nextPresentation));
                     return Presentation.create(parag, nextPresentation.theme);
                  default:
                     return display;
               }
            };
            Presentation newP = getNewPresentation.apply(StageViewUtils.EnumStageViewMode.valueOfById(svs.getId()));
            if (svs.isUpperCase()) {
               newP.text.setText(newP.text.getText().toUpperCase());
            }

            if (svs.isRemoveLineBreak() && !newP.text.getSlideType().isTitle()) {
               newP.text.setText(newP.text.getText().replace("\n", " • "));
               newP.text.setHackBreakLine(true);
            }

            newP.text.setText(newP.text.getText().replace("<#4(|<-|3|_@>", "\n"));
            return newP;
         } else {
            return display;
         }
      } catch (Exception var10) {
         return _display;
      }
   }

   public static Theme getTheme(Theme theme, ScreenDeviceSettings.EnumDeviceType deviceType, ScreenDeviceSettings.StageViewSettings svs) {
      if (svs.isEnabled() && svs.isThemeEnabled()) {
         switch (deviceType) {
            case MUSIC:
            case BIBLE:
               if (deviceType.equals(ScreenDeviceSettings.EnumDeviceType.MUSIC) || svs.isBibleEnabled()) {
                  Theme t = ThemeController.getOrDefault(svs.getThemeId(), null);
                  return t != null ? t : theme;
               }
         }

         return theme;
      } else {
         return theme;
      }
   }

   public static enum EnumStageViewMode {
      CURRENT_SLIDE(0, I18N.MSG_STAGE_VIEW_LABEL_TYPE_0.val()),
      FIRST_LINE_OF_THE_NEXT_SLIDE_WITH_SEPARATOR(1, I18N.MSG_STAGE_VIEW_LABEL_TYPE_1.val()),
      FIRST_LINE_OF_THE_NEXT_SLIDE_WITHOUT_SEPARATOR(2, I18N.MSG_STAGE_VIEW_LABEL_TYPE_2.val()),
      NEXT_SLIDE(3, I18N.MSG_STAGE_VIEW_LABEL_TYPE_3.val()),
      CURRENT_AND_NEXT_SLIDE(4, I18N.MSG_STAGE_VIEW_LABEL_TYPE_4.val());

      public final int id;
      public final String label;

      private EnumStageViewMode(int id, String label) {
         this.id = id;
         this.label = label;
      }

      public static StageViewUtils.EnumStageViewMode getDefault() {
         return FIRST_LINE_OF_THE_NEXT_SLIDE_WITH_SEPARATOR;
      }

      public static StageViewUtils.EnumStageViewMode valueOfById(int id) {
         for (StageViewUtils.EnumStageViewMode en : values()) {
            if (en.id == id) {
               return en;
            }
         }

         return getDefault();
      }

      public String getSeparator() {
         switch (this) {
            case CURRENT_AND_NEXT_SLIDE:
            case FIRST_LINE_OF_THE_NEXT_SLIDE_WITH_SEPARATOR:
               return "<#4(|<-|3|_@><#4(|<-|3|_@>";
            default:
               return "<#4(|<-|3|_@>";
         }
      }

      @Override
      public String toString() {
         return this.label;
      }
   }

   protected interface StageViewFormat {
      String W_END = "[" + I18N.WORD_END.val() + "]";
      String CHAR_FIRST_LINE = "...";
      String CHAR_LINE_START = " • ";

      static String titleAndArtist(String[] lines) {
         return lines.length == 2 ? String.format("[%s]%s[%s]", lines[0], "<#4(|<-|3|_@>", lines[1]) : lines[0];
      }

      static String formatIfTitleOrEnd(Presentation nextPresentation) {
         if (nextPresentation.separatorOrEnd) {
            return W_END;
         } else {
            return nextPresentation.text.getSlideType().isTitle()
               ? titleAndArtist(nextPresentation.text.getText().split("\n"))
               : nextPresentation.text.getText();
         }
      }
   }
}
