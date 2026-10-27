package com.limagiran.holyrics.util;

import java.awt.Color;
import javax.swing.border.LineBorder;

public interface StyleUI {
   Color COLOR_BLUE = new Color(32, 205, 255);
   Color COLOR_BLUE_LIGHT = new Color(125, 225, 255);
   Color COLOR_BORDER_SELECTED = Color.RED;
   Color COLOR_BORDER_UNSELECTED = new Color(80, 80, 80);
   LineBorder BORDER_SELECTED = new LineBorder(COLOR_BORDER_SELECTED, 3);
   LineBorder BORDER_UNSELECTED = new LineBorder(COLOR_BORDER_UNSELECTED, 3);
   Color FOREGROUND = new Color(50, 50, 50);
   Color FOREGROUND_DARK = new Color(15, 15, 15);
   Color FOREGROUND_LIGHT = new Color(48, 48, 48);
   Color BACKGROUND = new Color(252, 252, 252);
   Color COLOR_ERROR = new Color(255, 0, 0);
   Color COLOR_HIGHLIGHT_TEXT = new Color(150, 232, 255);
   String COLOR_HEX_HIGHLIGHT_TEXT = Utils.getHexColor(COLOR_HIGHLIGHT_TEXT);
   Color LIST_SELECTED_TRUE = Color.GRAY;
   Color LIST_SELECTED_FALSE = Color.DARK_GRAY;
   Color TOOL_TIP_TEXT_COLOR = new Color(180, 180, 180, 180);
   Color COLOR_ALTERNATE_ROW = new Color(225, 225, 225);
   Color COLOR_ALERT_SHOW = new Color(128, 255, 128);
   Color COLOR_LIVE_VIEW_ON = new Color(192, 255, 192);
   Color COLOR_VLC_PLAYER_PLAYING = new Color(92, 255, 92);
   Color COLOR_BLUE_ACCENT = Utils.decodeColor("#448AFF", Color.BLUE);
   Color COLOR_FAST_SEARCH_BACKGROUND = new Color(250, 250, 250);
   Color COLOR_FAST_SEARCH_FOREGROUND_TITLE = new Color(5, 5, 5);
   Color COLOR_FAST_SEARCH_FOREGROUND_VALUE = new Color(25, 25, 25);
   Color COLOR_FAST_SEARCH_FOREGROUND_HINT = new Color(80, 80, 80);
   Color FOREGROUND_DISABLED_OBJECT = new Color(150, 150, 150);
   Color CELL_RENDERER_TEXT_MODE = new Color(222, 237, 175);
}
