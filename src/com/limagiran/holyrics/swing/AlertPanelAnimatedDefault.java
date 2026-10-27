package com.limagiran.holyrics.swing;

import com.limagiran.holyrics.model.AlertSettings;
import com.limagiran.holyrics.settings.Settings;
import com.limagiran.holyrics.util.Values;
import com.limagiran.holyrics.view.CommunicationPanel;
import java.awt.Color;
import java.awt.Font;

public abstract class AlertPanelAnimatedDefault extends AlertPanelAnimated implements Values {
   @Override
   public boolean paint() {
      return this.isShow();
   }

   @Override
   public boolean isOverlap() {
      return this.getAs().isOverlap();
   }

   @Override
   public String getContent() {
      return this.isStageView() && CommunicationPanel.isDisplayAlert() ? CommunicationPanel.getAlertTextToDisplay() : this.getAs().getFullText();
   }

   @Override
   public Color getBackgroundColor() {
      return this.getAs().getBackgroundToPaint();
   }

   @Override
   public Color getForegroundColor() {
      return this.getAs().getForeground();
   }

   @Override
   public Font getTextFont() {
      return this.getAs().getFont();
   }

   @Override
   public double getVelocity() {
      return this.getAs().getVelocity();
   }

   public AlertSettings getAs() {
      return Settings.SET.getAlertSettings();
   }
}
