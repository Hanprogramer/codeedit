package com.hanprogramer.codeedit.languages;

import com.hanprogramer.codeedit.themes.ThemeColorName;

public class LuaLanguageDefinition extends CodeLanguageDefinition {
    public LuaLanguageDefinition() {
        langName = "lua";
        fileExtensions = new String[]{".lua"};
        tokens = new Token[]{
                new Token("number", "\\b(\\d*[.]?\\d+)\\b", ThemeColorName.Number),
                new Token("operator", "[\\+\\-\\*\\/\\%\\^\\-]", ThemeColorName.Operator),
                new Token("logicalOperator", "[\\=\\~\\>\\<]", ThemeColorName.Operator),
                new Token("logicalKeyword", "and|not|or", ThemeColorName.Keyword),
                new Token("miscOperator", "[\\.\\#\\%\\[\\]\\(\\)]", ThemeColorName.Operator),
                new Token("keyword",
                        "\\b(and|break|do|else|elseif|end|false|for|"
                                + "function|if|in|local|nil|not|or|repeat|" +
                                "return|then|true|until|while|var)\\b", ThemeColorName.Keyword),
                new Token("string", "\\\".*?\\\"|'.*'", ThemeColorName.String),
                new Token("singleLineComment", "--(?!\\[\\[).*?$", ThemeColorName.CommentSingle),
                new Token("multiLineComment", "--\\[\\[((.|\\n)*)\\]\\]", ThemeColorName.CommentMulti),
        };
    }
}
