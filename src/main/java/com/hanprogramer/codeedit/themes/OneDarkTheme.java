package com.hanprogramer.codeedit.themes;

import android.graphics.Color;

public class OneDarkTheme extends CodeThemeDefinition{
    public OneDarkTheme() {
        Background = Color.parseColor("#282C34");
        Text = Color.parseColor("#979FAD");
        CommentSingle = Color.parseColor("#59626F");
        CommentMulti = Color.parseColor("#59626F");
        Operator = Color.parseColor("#61AFEF");
        Number = Color.parseColor("#D19A66");
        String = Color.parseColor("#98C379");
        Keyword = Color.parseColor("#C679DD");
    }
}
