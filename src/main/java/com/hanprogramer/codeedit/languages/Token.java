package com.hanprogramer.codeedit.languages;

import com.hanprogramer.codeedit.themes.ThemeColorName;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Token {
    public String name;
    public String regex;
    public ThemeColorName color;
    public Pattern pattern;
    public Token(String name, String regex, ThemeColorName color) {
        this.name = name;
        this.regex = regex;
        this.color = color;
        pattern = Pattern.compile(regex, Pattern.MULTILINE);
    }
}
