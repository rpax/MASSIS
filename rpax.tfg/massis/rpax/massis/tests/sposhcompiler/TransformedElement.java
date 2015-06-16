package rpax.massis.tests.sposhcompiler;

public abstract class TransformedElement {
	public abstract String transform(int level);
	public static String createTabs(int n) {
		return createChars(n,'\t');
	}
	public static String createChars(int n,char c) {
		StringBuilder sb=new StringBuilder(n);
		for (int i = 0; i < n; i++) {
			sb.append(c);
		}
		return sb.toString();
	}
}
