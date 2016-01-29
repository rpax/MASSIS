package cz.cuni.amis.pogamut.shady;

/**
 * Interface that unified various arguments used when shade is calling
 * the primitive. 
 * @author Honza
 */
public interface IArgument<T> {

    T getValue();
}

/**
 * TODO:Stub
 * @author Honza
 */
class Arg<T> implements IArgument<T> {

    private final T value;

    public Arg(T value) {
        this.value = value;
    }

    @Override
    public T getValue() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof IArgument)) {
            return false;
        }
        IArgument arg = (IArgument) obj;
        return this.value == null ? arg.getValue() == null : value.equals(arg.getValue());
    }
}

class ArgInt extends Arg<Integer> {

    public ArgInt(int number) {
        super(number);
    }
}

class ArgFloat extends Arg<Double> {

    public ArgFloat(double number) {
        super(number);
    }
}

class ArgChar extends Arg<Character> {

    public ArgChar(char character) {
        super(character);
    }

    /**
     * The the escaped char (e.g. '\n' but without quotes) and return its actual
     * value.
     * <p/>
     * There is slight difference in valid strings ("'", "\'" and "\"") vs valid 
     * chars ('"', '\"' and '\''), but not all are interchangeable.
     * 
     */
    public static char unescape(String escapedChar) throws ParseException {
        if (escapedChar.length() == 0) {
            throw new ParseException("The string representing the escaped character must have legth > 0.");
        }
        StringBuilder sb = new StringBuilder(escapedChar);
        char result = parseCharacter(sb);
        if (sb.length() > 0)
            throw new ParseException("There still are some unparsed characters remaining(" + sb.length() + "):" + sb.toString());
        return result;
    }

    /**
     * Take string starting with a single escaped character (e.g. 'ahoj', 
     * '\nWorld'..) try to parse the first character and return it. Remove the 
     * characters representing the escaped character form the sb.
     * @param sb string that should start with escaped character
     * @return parsed character
     * @throws ParseException
     */
    protected static char parseCharacter(StringBuilder sb) throws ParseException {
        char result = sb.charAt(0);
        sb.deleteCharAt(0);
        if (result == '\\') {
            Character parsed = ArgChar.parseEscapeSequence(sb);
            if (parsed == null) {
                throw new ParseException("Unable to unescape sequence:" + sb.toString());
            }
            result = parsed;
        }
        return result;
    }

    
    /**
     * The the escaped character literal (e.g.  'a', '\n', '\0' or '\u3067' 
     * including quotes) and return the character that is represented by this literal.
     */
    public static char parseCharacterListeral(String charLiteral) throws ParseException {
        StringBuilder sb = new StringBuilder(charLiteral);
        if (sb.charAt(0) != '\'') {
            throw new ParseException("Expecting \' at the start of " + charLiteral.toString());
        }
        sb.deleteCharAt(0);

        char result = parseCharacter(sb);

        if (sb.length() != 1) {
            throw new ParseException("Expecting exactly one character (single quote), but " + sb.length() + " characters remain:" + sb.toString());
        }
        if (sb.charAt(0) != '\'') {
            throw new ParseException("Expecting ending double quote, but got: " + sb.charAt(0));
        }
        return result;
    }

    /**
     * Try to parse single escape sequence (we assume that backslash has already 
     * been eaten):
     * <ul>
     *  <li>[0-3][0-7][0-7] - into character with specified octal number</li>
     *  <li>[0-7][0-7]</li>
     *  <li>[0-7]</li>
     *  <li>b,t,n,r,f,",',\ - into backspace, tab, LF, CR, FF, ", ' and \</li>
     *  <li>(u)+\p{XDigit}\p{XDigit}\p{XDigit}\p{XDigit} - into character with specified hexa number</li>
     * </ul>
     * If there is no escape sequence, don't touch sb and return null.
     * @param sb the escaped char should be at the beginning of sb.
     * @return character represented by escape sequence or null
     */
    protected static Character parseEscapeSequence(StringBuilder sb) {
        // take care of octal first, it uses no prefix
        Character octal = parseOctal(sb);
        if (octal != null) {
            return octal;
        }
        // take care of single char escape sequence
        Character simpleEscape = parseSingleEscape(sb);
        if (simpleEscape != null) {
            return simpleEscape;
        }
        Character unicode = parseUnicode(sb);
        if (unicode != null) {
            return unicode;
        }
        return null;
    }

    /**
     * Try to parse allowed octal number (0-255 ~o0-o377) at the start of 
     * the passed string.
     * @param sb string buffer that may have octal number at the start. If octal 
     *        number is found, remove it from the start.
     * @return parsed number or null if not found.
     */
    protected static Character parseOctal(StringBuilder sb) {
        if (sb.length() >= 3) {
            String octal3 = sb.substring(0, 3);
            if (octal3.matches("[0-3][0-7][0-7]")) {
                sb.delete(0, 3);
                return Character.toChars(Integer.parseInt(octal3, 8))[0];
            }
        }
        if (sb.length() >= 2) {
            String octal2 = sb.substring(0, 2);
            if (octal2.matches("[0-7][0-7]")) {
                sb.delete(0, 2);
                return Character.toChars(Integer.parseInt(octal2, 8))[0];
            }
        }
        String octal1 = sb.substring(0, 1);
        if (octal1.matches("[0-7]")) {
            sb.delete(0, 1);
            return Character.toChars(Integer.parseInt(octal1, 8))[0];
        }
        return null;
    }

    /**
     * Check if there is a single char escape sequence (we assume that backslash
     * has already been deleted) and if it is, remove it from sb and return 
     * the escaped character.
     * Example: sequence 'nAndy said.' could would return character '\n' and sb 
     * would delete first character, thus being 'Andy said.'
     * @param sb
     * @return escaped character or null if there is something else (e.g. m)
     */
    protected static Character parseSingleEscape(StringBuilder sb) {
        char ch = sb.charAt(0);
        char res;
        switch (ch) {
            case 'b':
                res = '\b'; // backspace BS
                break;
            case 't':
                res = '\t'; // horizontal tab
                break;
            case 'n':
                res = '\n'; // linefeed LF
                break;
            case 'f':
                res = '\f'; // form feed FF
                break;
            case 'r':
                res = '\r'; // carriage return CR
                break;
            case '"':
                res = '\"'; // double quote
                break;
            case '\'':
                res = '\''; // single quote
                break;
            case '\\':
                res = '\\'; // backslash
                break;
            default:
                return null;
        }
        sb.deleteCharAt(0);
        return res;
    }

    /**
     * Check if there is unicode escape at the start of sb (without backslash)
     * and if it is, parse it, remove it from the sb and return parsed unicode
     * character. 
     * Example: 'u306A\u306b \u30673059 \u304B?' ('nani desu ka?') would return
     * character '\u306A' (na) and in sb would be '\u306b \u30673059 \u304B?'
     * @param sb sb, unescaped character will be removed
     * @return unescaped character or null
     */
    protected static Character parseUnicode(StringBuilder sb) {
        String regexp = "u+\\p{XDigit}\\p{XDigit}\\p{XDigit}\\p{XDigit}.*";
        if (sb.toString().matches(regexp)) {
            while (sb.charAt(0) == 'u') {
                sb.deleteCharAt(0);
            }
            String hexaString = sb.substring(0, 4);
            sb.delete(0, 4);
            return Character.toChars(Integer.parseInt(hexaString, 16))[0];
        }
        return null;
    }
}
