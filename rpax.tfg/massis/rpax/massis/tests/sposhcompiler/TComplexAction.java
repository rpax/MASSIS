package rpax.massis.tests.sposhcompiler;

import java.util.ArrayList;
import java.util.List;

public class TComplexAction extends TransformedElement {

	private ArrayList<TPrimitiveSense> senses = new ArrayList<>();
	private ArrayList<TransformedElement> actions = new ArrayList<>();

	public List<TPrimitiveSense> getSenses() {
		return senses;
	}

	public void addSense(TPrimitiveSense sense) {
		this.senses.add(sense);
	}

	public void addSenses(List<TPrimitiveSense> senses) {
		this.senses.addAll(senses);
	}

	public void addAction(TransformedElement action) {
		this.actions.add(action);
	}

	public List<TransformedElement> getActions() {
		return actions;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();

		if (!senses.isEmpty())
		{
			builder.append("if (");

			for (int i = 0; i < senses.size(); i++)
			{
				builder.append(senses.get(i));
				if (i < senses.size() - 1)
				{
					builder.append("\n && ");
				}
			}
			builder.append(")\n");
		}

		for (int i = 0; i < actions.size(); i++)
		{
			builder.append("\t").append(actions.get(i)).append("\n");

		}

		return builder.toString();
	}

	@Override
	public String transform(int level) {
		StringBuilder sb = new StringBuilder();
		String tabs = createTabs(level);

		if (!senses.isEmpty())
		{
			sb.append(tabs).append("if").append('\n');
			sb.append(tabs).append("(").append('\n');
			for (TPrimitiveSense sense : senses)
			{
				sb.append(sense.transform(level + 1)).append('\n');
			}
			sb.append(tabs).append(')').append('\n');
			// sb.append(tabs).append("=>").append('\n');
		}

		sb.append(tabs).append("{").append('\n');
		sb.append(actions.get(0).transform(level + 1)).append('\n');
		// for (TransformedElement action : this.actions)
		for (int i = 1; i < actions.size() - 1; i++)
		{
			sb.append(tabs).append("else \n");
			sb.append(actions.get(i).transform(level + 1)).append('\n');
		}
		if (actions.size() > 1)
		{
			sb.append(tabs).append("else \n");
			sb.append(actions.get(actions.size() - 1).transform(level + 1))
					.append('\n');
		}
		sb.append(tabs).append("}").append('\n');
		return sb.toString();
	}
}
