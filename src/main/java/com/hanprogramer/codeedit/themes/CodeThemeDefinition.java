package com.hanprogramer.codeedit.themes;

import android.graphics.Color;

public class CodeThemeDefinition {
    public int Background;
    public int Text;
    public int CommentSingle;
    public int CommentMulti;
    public int Operator;
    public int Number;
    public int String;
    public int Keyword;

    public int Error = Color.parseColor("#FF1919");
    public int Warning = Color.parseColor("#FFE605");

    public int GetColor(ThemeColorName name){
        switch(name){
            case CommentMulti:
                return this.CommentMulti;
            case CommentSingle:
                return this.CommentSingle;
            case Keyword:
                return this.Keyword;
            case String:
                return this.String;
            case Operator:
                return this.Operator;
            case Number:
                return this.Number;
            case Background:
                return this.Background;
            case Text:
                return this.Text;
        }
        return 0;
    }
}
