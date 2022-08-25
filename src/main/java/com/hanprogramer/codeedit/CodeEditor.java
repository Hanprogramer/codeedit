package com.hanprogramer.codeedit;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ReplacementSpan;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.content.ContextCompat;

import com.hanprogramer.codeedit.languages.CodeLanguageDefinition;
import com.hanprogramer.codeedit.languages.LuaLanguageDefinition;
import com.hanprogramer.codeedit.languages.Token;
import com.hanprogramer.codeedit.themes.CodeThemeDefinition;
import com.hanprogramer.codeedit.themes.OneDarkTheme;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CodeEditor extends AppCompatEditText {
    public CodeLanguageDefinition language = new LuaLanguageDefinition();
    public CodeThemeDefinition theme = new OneDarkTheme();

    public interface OnTextChangedListener {
        void onTextChanged(String text);
    }

    private static final Pattern PATTERN_LINE = Pattern.compile(
            ".*\\n");
    private static final Pattern PATTERN_TRAILING_WHITE_SPACE = Pattern.compile(
            "[\\t ]+$",
            Pattern.MULTILINE);
    private final Handler updateHandler = new Handler();
    private final Runnable updateRunnable = new Runnable() {
        @Override
        public void run() {
            Editable e = getText();

            if (onTextChangedListener != null) {
                onTextChangedListener.onTextChanged(
                        removeNonAscii(e.toString()));
            }

            highlightWithoutChange(e);
        }
    };

    private OnTextChangedListener onTextChangedListener;
    private int updateDelay = 1000;
    private int errorLine = 0;
    private boolean dirty = false;
    private boolean modified = true;
    private int colorError;
    private int colorNumber;
    private int colorKeyword;
    private int colorBuiltin;
    private int colorComment;
    private int tabWidthInCharacters = 0;
    private int tabWidth = 0;

    public static String removeNonAscii(String text) {
        return text.replaceAll("[^\\x0A\\x09\\x20-\\x7E]", "");
    }

    public CodeEditor(Context context) {
        super(context);
        init(context);
    }

    public CodeEditor(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public void setOnTextChangedListener(OnTextChangedListener listener) {
        onTextChangedListener = listener;
    }

    public void setUpdateDelay(int ms) {
        updateDelay = ms;
    }

    public void setTabWidth(int characters) {
        if (tabWidthInCharacters == characters) {
            return;
        }

        tabWidthInCharacters = characters;
        tabWidth = Math.round(getPaint().measureText("m") * characters);
    }

    public boolean hasErrorLine() {
        return errorLine > 0;
    }

    public void setErrorLine(int line) {
        errorLine = line;
    }

    public void updateHighlighting() {
        highlightWithoutChange(getText());
    }

    public boolean isModified() {
        return dirty;
    }

    public void setTextHighlighted(CharSequence text) {
        if (text == null) {
            text = "";
        }

        cancelUpdate();

        errorLine = 0;
        dirty = false;

        modified = false;
        String src = removeNonAscii(text.toString());
        setText(highlight(new SpannableStringBuilder(src)));
        modified = true;

        if (onTextChangedListener != null) {
            onTextChangedListener.onTextChanged(src);
        }
    }

    public String getCleanText() {
        return PATTERN_TRAILING_WHITE_SPACE
                .matcher(getText())
                .replaceAll("");
    }

    public void insertTab() {
        int start = getSelectionStart();
        int end = getSelectionEnd();

        getText().replace(
                Math.min(start, end),
                Math.max(start, end),
                "\t",
                0,
                1);
    }

    private void init(Context context) {
        setHorizontallyScrolling(true);
        setBackgroundColor(theme.Background);
        setFilters(new InputFilter[]{(source, start, end, dest, dstart, dend) -> {
            if (modified &&
                    end - start == 1 &&
                    start < source.length() &&
                    dstart < dest.length()) {
                char c = source.charAt(start);

                if (c == '\n') {
                    return autoIndent(source, dest, dstart, dend);
                }
            }

            return source;
        }});

        addTextChangedListener(new TextWatcher() {
            private int start = 0;
            private int count = 0;

            @Override
            public void onTextChanged(
                    CharSequence s,
                    int start,
                    int before,
                    int count) {
                this.start = start;
                this.count = count;
            }

            @Override
            public void beforeTextChanged(
                    CharSequence s,
                    int start,
                    int count,
                    int after) {
            }

            @Override
            public void afterTextChanged(Editable e) {
                cancelUpdate();
                convertTabs(e, start, count);

                if (!modified) {
                    return;
                }

                dirty = true;
//                updateHandler.postDelayed(updateRunnable, updateDelay);
                updateHighlighting();
            }
        });

        setSyntaxColors(context);
//        setUpdateDelay(LODE.preferences.getUpdateDelay());
//        setTabWidth(LODE.preferences.getTabWidth());
    }

    private void setSyntaxColors(Context context) {
//        colorError = ContextCompat.getColor(
//                context,
//                R.color.syntax_error);
//        colorNumber = ContextCompat.getColor(
//                context,
//                R.color.syntax_number);
//        colorKeyword = ContextCompat.getColor(
//                context,
//                R.color.syntax_keyword);
//        colorBuiltin = ContextCompat.getColor(
//                context,
//                R.color.syntax_builtin);
//        colorComment = ContextCompat.getColor(
//                context,
//                R.color.syntax_comment);
    }

    private void cancelUpdate() {
        updateHandler.removeCallbacks(updateRunnable);
    }

    private void highlightWithoutChange(Editable e) {
        modified = false;
        highlight(e);
        modified = true;
    }

    private Editable highlight(Editable e) {
        try {
            int length = e.length();

            // don't use e.clearSpans() because it will
            // remove too much
            clearSpans(e, length);

            if (length == 0) {
                return e;
            }

            if (errorLine > 0) {
                Matcher m = PATTERN_LINE.matcher(e);

                for (int i = errorLine; i-- > 0 && m.find(); ) {
                    // {} because analyzers don't like for (); statements
                }

                e.setSpan(
                        new BackgroundColorSpan(colorError),
                        m.start(),
                        m.end(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

//            if (App.preferences.disableHighlighting() &&
//                    length > 4096) {
//                return e;
//

            for(Token token : language.tokens){
                for(Matcher m = token.pattern.matcher(e); m.find(); ){
                    e.setSpan(
                            new ForegroundColorSpan(theme.GetColor(token.color)),
                            m.start(),
                            m.end(),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
        } catch (IllegalStateException ex) {
            // raised by Matcher.start()/.end() when
            // no successful match has been made what
            // shouldn't ever happen because of find()
        }

        return e;
    }

    private static void clearSpans(Editable e, int length) {
        // remove foreground color spans
        {
            ForegroundColorSpan[] spans = e.getSpans(
                    0,
                    length,
                    ForegroundColorSpan.class);

            for (int i = spans.length; i-- > 0; ) {
                e.removeSpan(spans[i]);
            }
        }

        // remove background color spans
        {
            BackgroundColorSpan[] spans = e.getSpans(
                    0,
                    length,
                    BackgroundColorSpan.class);

            for (int i = spans.length; i-- > 0; ) {
                e.removeSpan(spans[i]);
            }
        }
    }

    private CharSequence autoIndent(
            CharSequence source,
            Spanned dest,
            int dstart,
            int dend) {
        String indent = "";
        int istart = dstart - 1;

        // find start of this line
        boolean dataBefore = false;
        int pt = 0;

        for (; istart > -1; --istart) {
            char c = dest.charAt(istart);

            if (c == '\n') {
                break;
            }

            if (c != ' ' && c != '\t') {
                if (!dataBefore) {
                    // indent always after those characters
                    if (c == '{' ||
                            c == '+' ||
                            c == '-' ||
                            c == '*' ||
                            c == '/' ||
                            c == '%' ||
                            c == '^' ||
                            c == '=') {
                        --pt;
                    }

                    dataBefore = true;
                }

                // parenthesis counter
                if (c == '(') {
                    --pt;
                } else if (c == ')') {
                    ++pt;
                }
            }
        }

        // copy indent of this line into the next
        if (istart > -1) {
            char charAtCursor = dest.charAt(dstart);
            int iend;

            for (iend = ++istart; iend < dend; ++iend) {
                char c = dest.charAt(iend);

                // auto expand comments
                if (charAtCursor != '\n' &&
                        c == '/' &&
                        iend + 1 < dend &&
                        dest.charAt(iend) == c) {
                    iend += 2;
                    break;
                }

                if (c != ' ' && c != '\t') {
                    break;
                }
            }

            indent += dest.subSequence(istart, iend);
        }

        // add new indent
        if (pt < 0) {
            indent += "\t";
        }

        // append white space of previous line and new indent
        return source + indent;
    }

    private void convertTabs(Editable e, int start, int count) {
        if (tabWidth < 1) {
            return;
        }

        String s = e.toString();

        for (int stop = start + count;
             (start = s.indexOf("\t", start)) > -1 && start < stop;
             ++start) {
            e.setSpan(
                    new TabWidthSpan(tabWidth),
                    start,
                    start + 1,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }

    private static class TabWidthSpan extends ReplacementSpan {
        private final int width;

        private TabWidthSpan(int width) {
            this.width = width;
        }

        @Override
        public int getSize(
                @NonNull Paint paint,
                CharSequence text,
                int start,
                int end,
                Paint.FontMetricsInt fm) {
            return width;
        }

        @Override
        public void draw(
                @NonNull Canvas canvas,
                CharSequence text,
                int start,
                int end,
                float x,
                int top,
                int y,
                int bottom,
                @NonNull Paint paint) {
        }
    }
}