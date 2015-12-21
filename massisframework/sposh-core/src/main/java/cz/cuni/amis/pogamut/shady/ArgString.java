package cz.cuni.amis.pogamut.shady;

/**
 * Store the string value as an argument. We want to allow escaping (e.g. \n, \t
 * ..) and we are using syntax defined by the ({@linkplain http://java.sun.com/docs/books/jls/second_edition/html/lexical.doc.html#101084 java lexical structure}).
 * <p/>
 * <em>The constructor won't automatically unescape the passed value</em>. Use {@link ArgString#parseStringLiteral(java.lang.StringBuilder)
 * }.
 *
 * @author Honza
 */
public class ArgString extends Arg<String> {

    /**
     * Take the string and use it as the value of the argument. Be careful, in
     * most cases, use of {@link ArgString#unescape(java.lang.String)} is
     * recommended (it parses escaped string into an unescaped form).
     *
     * @param string value of the argument
     */
    public ArgString(String string) {
        super(string);
    }

    /**
     * Get unescaped version of passed string.
     *
     * @param escapedString string escaped according to java lexical structure,
     * without double quotes (e.g. Hello\nWorld)
     * @return unescaped string
     * @throws ParseException if there is an error in the escaping
     */
    public static String unescape(String escapedString) throws ParseException {
        if (escapedString.length() == 0) {
            return escapedString;
        }
        return parseStringCharacters(new StringBuilder(escapedString));
    }

    /**
     * Take the escaped string, parse it and return the unescaped value.
     *
     * @param string string to parse, incl. quotes (e.g. "Foo\nBar")
     * <p/>
     * <cite>StringLiteral: " [StringCharacters] "</cite>
     * @return parsed string
     */
    protected static String parseStringLiteral(String escaped) throws ParseException {
        StringBuilder sb = new StringBuilder(escaped);
        if (sb.charAt(0) != '\"') {
            throw new ParseException("Expecting \" at the start of " + escaped.toString());
        }
        sb.deleteCharAt(0);

        // StringCharacters nonterminal is optional. If it is missing, ther is 
        // only ending double quote.
        String parsed = "";
        if (sb.length() != 1) {
            parsed = parseStringCharacters(sb);
        }

        if (sb.length() != 1) {
            throw new ParseException("Expecting exactly one character (double quote), but " + sb.length() + " characters remain:" + sb.toString());
        }

        if (sb.charAt(0) != '\"') {
            throw new ParseException("Expecting ending double quote, but got: " + sb.charAt(0));
        }

        return parsed.toString();
    }

    /**
     * Parse nonterminal <em>StringCharacters: ( StringCharacter )+</em>
     *
     * @param sb escaped string to be parsed, something may be even after last
     * <em>StringCharacter</em>. This will be modified during progress, the
     * parsed characters will be "eaten"
     * @return unescaped string
     */
    private static String parseStringCharacters(StringBuilder sb) throws ParseException {
        Character ch;
        StringBuilder stringCharacters = new StringBuilder();
        while ((ch = parseStringCharacter(sb)) != null) {
            stringCharacters.append(ch);
        }
        return stringCharacters.toString();
    }

    /**
     * Extract the <em>StringCharacter</em> from the sb and return it.
     * <p/>
     * <
     * pre>
     * StringCharacter: InputCharacter but not " or \
     * StringCharacter: EscapeSequence
     * InputCharacter: UnicodeInputCharacter but not CR or LF
     * </pre> Basically when you expand it all, you will get
     * <pre>
     *  \(u)+ [0-9a-fA-F] [0-9a-fA-F] [0-9a-fA-F]  ~ Unicode char
     *  \[btnfr"'\]
     *  \[0-3][0-7][0-7]
     *  \[0-7][0-7]
     *  \[0-7]
     * </pre>
     *
     * @param sb sequence of chars that is used to extract the character. Is
     * modified (characters are removed) during parsing.
     * @return found character if there is a <em>StringCharacter</em>, null
     * otherwise
     */
    protected static Character parseStringCharacter(StringBuilder sb) throws ParseException {
        if (sb.length() == 0) {
            return null;
        }
        char ch = sb.charAt(0);
        if (ch == '\"' || ch == '\r' || ch == '\n') {
            return null;
        }

        sb.deleteCharAt(0);
        // Is that an escape sequence
        if (ch == '\\') {
            Character res = ArgChar.parseEscapeSequence(sb);
            if (res == null) {
                throw new ParseException("Unable to unescape sequence:" + sb.toString());
            }
            return res;
        }
        return ch;
    }
}
