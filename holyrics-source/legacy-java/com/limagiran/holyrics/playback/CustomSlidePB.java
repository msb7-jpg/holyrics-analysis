package com.limagiran.holyrics.playback;

import com.alee.laf.button.WebButton;
import com.alee.laf.label.WebLabel;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.spinner.WebSpinner;
import com.limagiran.holyrics.control.MusicController;
import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.util.EnumSlideSizePreview;
import com.limagiran.holyrics.util.PresentationSlideUtils;
import com.limagiran.holyrics.util.StringUtils;
import com.limagiran.holyrics.util.SwingUtils;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

class CustomSlidePB extends WebPanel {
   private static final Border BORDER_SHOW = new LineBorder(new Color(0, 255, 0), 4);
   private static final Border BORDER_NONE = new EmptyBorder(4, 4, 4, 4);
   private final WebLabel labelSlide;
   private final SpinnerNumberModel spinnerModel;
   private final WebSpinner spinnerDuration;
   private final WebLabel labelTime;
   private WebPanel buttonsPanel;
   public final MusicController.Paragraph paragraph;
   private boolean selected;
   protected ImageIcon icSelected;
   protected ImageIcon icUnselected;
   private final CustomSlidePBListener listener;
   private int startAt;
   private long currentAudioTime;

   public CustomSlidePB(MusicController.Paragraph paragraph, int duration, CustomSlidePBListener listener) {
      super(true, new BorderLayout(0, 4));
      this.paragraph = paragraph;
      this.listener = listener;
      this.labelSlide = new WebLabel("", 0, new Object[0]);
      this.spinnerModel = new SpinnerNumberModel((double)duration / 1000.0, (double)PlayBack.MIN_DURATION / 1000.0, (double)PlayBack.MAX_DURATION / 1000.0, 0.1);
      this.spinnerDuration = new WebSpinner(this.spinnerModel);
      this.labelTime = new WebLabel("", 0, new Object[0]);
      this.init();
   }

   private void init() {
      this.updateSize();
      this.labelSlide.setFont(new Font("Arial", 0, 12));
      this.labelSlide.setIcon(this.icUnselected);
      this.labelSlide.setBorder(BORDER_NONE);
      this.labelTime.addMouseListener(new MouseAdapter() {
         @Override
         public void mousePressed(MouseEvent e) {
            PlayBackUtils.setStartAt(CustomSlidePB.this.startAt, newValue -> CustomSlidePB.this.listener.changeStartAt(CustomSlidePB.this, newValue));
         }
      });
      this.labelTime.setCursor(Cursor.getPredefinedCursor(12));
      this.add(this.labelSlide, "Center");
      this.add(this.setupDurationPanel(), "Last");
      this.updateLabelTime();
   }

   protected void commit() {
      SwingUtils.commitEdit(this.spinnerDuration);
   }

   private WebPanel setupDurationPanel() {
      Font font = new Font("Courier New", 0, 14);
      this.spinnerDuration.setSize(75, 25);
      this.spinnerDuration.setPreferredSize(this.spinnerDuration.getSize());
      this.spinnerModel.addChangeListener(e -> this.listener.durationChanged(this));
      WebLabel labelDuration = new WebLabel(I18N.WORD_DURATION.val());
      WebLabel labelSeconds = new WebLabel("s");
      WebPanel spinnerPanel = new WebPanel(new FlowLayout(1, 5, 0), new Component[]{labelDuration, this.spinnerDuration, labelSeconds});
      spinnerPanel.setOpaque(false);
      WebButton buttonPlay = new WebButton("►", evt -> this.listener.play(this, false));
      buttonPlay.setShadeWidth(0);
      buttonPlay.setUndecorated(true);
      buttonPlay.setToolTipText(I18N.MSG_PLAY_FROM_HERE.val());
      WebButton buttonPlayOnlyThis = new WebButton("|►|", evt -> this.listener.play(this, true));
      buttonPlayOnlyThis.setUndecorated(true);
      buttonPlayOnlyThis.setToolTipText(I18N.MSG_PLAY_ONLY_THIS_SLIDE.val());
      this.buttonsPanel = new WebPanel(new FlowLayout(1, 5, 3), this.labelTime, Box.createHorizontalStrut(16), buttonPlay, buttonPlayOnlyThis) {
         private final Dimension size = new Dimension(0, 8);
         private final Rectangle rect = new Rectangle(0, 0, CustomSlidePB.super.size.width, CustomSlidePB.super.size.height);

         protected void paintComponent(Graphics g) {
            this.size.width = this.getWidth() - 8;
            boolean flag = false;
            double wProgress;
            if ((long)CustomSlidePB.this.getEndAt() <= CustomSlidePB.this.currentAudioTime) {
               wProgress = 0.0;
            } else if ((long)CustomSlidePB.this.startAt > CustomSlidePB.this.currentAudioTime) {
               wProgress = (double)CustomSlidePB.this.getDuration();
            } else {
               wProgress = (double)((int)((long)CustomSlidePB.this.getEndAt() - CustomSlidePB.this.currentAudioTime));
               flag = true;
            }

            if (flag) {
               if (CustomSlidePB.this.labelSlide.getBorder() == CustomSlidePB.BORDER_NONE) {
                  SwingUtilities.invokeLater(() -> CustomSlidePB.this.labelSlide.setBorder(CustomSlidePB.BORDER_SHOW));
                  CustomSlidePB.this.labelSlide.repaint();
               }
            } else if (CustomSlidePB.this.labelSlide.getBorder() == CustomSlidePB.BORDER_SHOW) {
               SwingUtilities.invokeLater(() -> CustomSlidePB.this.labelSlide.setBorder(CustomSlidePB.BORDER_NONE));
               CustomSlidePB.this.labelSlide.repaint();
            }

            double duration = (double)CustomSlidePB.this.getDuration();
            if (wProgress > 0.0 && duration > 0.0) {
               int dif = (int)((double)this.size.width * (wProgress / duration));
               Graphics2D g2 = (Graphics2D)g.create(4, this.getHeight() - 10, this.size.width, this.size.height);
               g2.setColor(new Color(0, 128, 0));
               g2.fillRect(this.size.width - dif, 0, this.getWidth(), this.size.height);
            }

            try {
               Point p = CustomSlidePB.this.buttonsPanel.getLocationOnScreen();
               this.rect.setBounds(p.x + 2, p.y + this.getHeight() - 12, this.size.width + 4, this.size.height + 4);
               p = MouseInfo.getPointerInfo().getLocation();
               if (this.rect.contains(p)) {
                  Graphics2D g2 = (Graphics2D)g.create(p.x - this.rect.x, this.getHeight() - 12, 4, this.size.height + 4);
                  g2.setColor(new Color(0, 48, 0));
                  g2.fillRect(0, 0, 64, 64);
               }
            } catch (Exception var9) {
            }

            super.paintComponent(g);
         }
      };
      MouseAdapter mouseAdapter = new MouseAdapter() {
         private final Rectangle r = new Rectangle();
         private long lastMilli;

         private void updateBounds() {
            this.r.setBounds(2, CustomSlidePB.this.buttonsPanel.getHeight() - 12, CustomSlidePB.this.buttonsPanel.getWidth() - 4, 12);
         }

         @Override
         public void mouseMoved(MouseEvent e) {
            this.refreshToolTip(e);
         }

         @Override
         public void mouseDragged(MouseEvent e) {
            this.refreshToolTip(e);
         }

         @Override
         public void mouseExited(MouseEvent e) {
            try {
               CustomSlidePB.this.listener.getToolTip().setVisible(false);
               ((JDialog)CustomSlidePB.this.getRootPane().getParent()).getGlassPane().setVisible(false);
            } catch (Exception var3) {
            }
         }

         @Override
         public void mousePressed(MouseEvent e) {
            this.updateBounds();
            if (this.r.contains(e.getPoint())) {
               CustomSlidePB.this.listener.changePosition(this.lastMilli);
            }
         }

         private void refreshToolTip(MouseEvent e) {
            try {
               this.updateBounds();
               if (!this.r.contains(e.getPoint())) {
                  CustomSlidePB.this.listener.getToolTip().setVisible(false);
                  return;
               }

               long duration = (long)CustomSlidePB.this.getDuration();
               double percentage = (double)(e.getX() - this.r.x) / (double)this.r.width;
               this.lastMilli = (long)CustomSlidePB.this.startAt + (long)((double)duration * percentage);
               CustomSlidePB.this.listener.getToolTip().setText(StringUtils.formatTimeMm_Ss_MSms(this.lastMilli));
               CustomSlidePB.this.listener.getToolTip().refresh((JDialog)CustomSlidePB.this.getRootPane().getParent(), true);
            } catch (Exception var6) {
            }
         }
      };
      this.buttonsPanel.addMouseListener(mouseAdapter);
      this.buttonsPanel.addMouseMotionListener(mouseAdapter);
      this.buttonsPanel.setOpaque(false);
      this.buttonsPanel.setMargin(0, 0, 10, 0);
      Arrays.asList(this.labelTime, this.spinnerDuration, labelDuration, labelSeconds, buttonPlay, buttonPlayOnlyThis).forEach(c -> c.setFont(font));
      WebPanel _return = new WebPanel(new BorderLayout());
      _return.add(spinnerPanel, "Center");
      _return.add(this.buttonsPanel, "Last");
      _return.setOpaque(false);
      return _return;
   }

   public void updateSize() {
      this.icSelected = new ImageIcon(
         PresentationSlideUtils.createThumb(this.paragraph.getText(), EnumSlideSizePreview.THEME_SELECTED, EditPlayBackDialog.SLIDE_SIZE)
      );
      this.icUnselected = new ImageIcon(
         PresentationSlideUtils.createThumb(this.paragraph.getText(), EnumSlideSizePreview.THEME_UNSELECTED, EditPlayBackDialog.SLIDE_SIZE)
      );
      this.setSelected(this.selected);
   }

   public boolean isSelected() {
      return this.selected;
   }

   public void setSelected(boolean selected) {
      this.selected = selected;
      SwingUtilities.invokeLater(() -> this.labelSlide.setIcon(this.selected ? this.icSelected : this.icUnselected));
      this.labelSlide.repaint();
      this.repaint();
   }

   public void unselect() {
      this.setSelected(false);
   }

   public void select() {
      this.setSelected(true);
   }

   public int getDuration() {
      return (int)(this.spinnerModel.getNumber().doubleValue() * 1000.0);
   }

   private void updateLabelTime() {
      SwingUtilities.invokeLater(() -> this.labelTime.setText("<html><u>" + StringUtils.formatTimeMm_Ss_MSms((long)this.startAt) + "</u>"));
   }

   protected void setStartAt(int startAt) {
      this.startAt = startAt;
      this.updateLabelTime();
   }

   protected int getStartAt() {
      return this.startAt;
   }

   protected void setCurrentAudioTime(long timeMillis) {
      this.currentAudioTime = timeMillis;
      if (this.buttonsPanel != null) {
         this.buttonsPanel.repaint();
      }
   }

   protected int getEndAt() {
      return this.getStartAt() + this.getDuration();
   }
}
