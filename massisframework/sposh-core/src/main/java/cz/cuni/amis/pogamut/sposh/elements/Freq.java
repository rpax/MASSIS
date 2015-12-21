package cz.cuni.amis.pogamut.sposh.elements;

/**
 * Class for holding information about frequency of running various parts of
 * POSH plan. 
 *
 * @author Honza
 */
@Deprecated
public class Freq {

    /**
     * Infinite frequency. In order for an event to have this frequency, it
     * would have to occur each 1/inf ms.
     */
    public static Freq INFINITE = new Freq(0, FreqUnits.SECONDS);

    /**
     * Compare two frequenties.
     *
     * @param freq1
     * @param freq2
     * @return -1 if freq1 < freq2 0 if freq1 == freq2 1 if freq1 > freq2
     */
    public static int compare(long freq1, long freq2) {
        if (freq1 < freq2) {
            return -1;
        }
        if (freq1 == freq2) {
            return 0;
        }
        return 1;
    }

    public static int compare(Freq freq1, Freq freq2) {
        return compare(freq1.tick(), freq2.tick());
    }

	/**
	 * Units for frequency
	 * @author HonzaH
	 */
	public enum FreqUnits {

		HOURS("hours", 0),
		MINUTES("minutes", 1),
		SECONDS("seconds", 2),
		NONE("none", 3),
		HZ("hz", 4),
		PM("pm", 5);

		private final String name;
		private final int id;

		private FreqUnits(String name, int id) {
			this.name = name;
			this.id = id;
		}

		@Override
		public String toString() {
			return name;
		}

		public int getId() {
			return id;
		}
	}
	
	
	private FreqUnits units;
	private double ammount;

	Freq() {
		this.ammount = 0;
		this.units = FreqUnits.NONE;
	}

	Freq(double count, FreqUnits units) {
		this.ammount = count;
                this.units = units;
	}

	public Freq(Freq original) {
		this.ammount = original.ammount;
		this.units = original.units;
	}

	@Override
	public String toString() {
		if (!FreqUnits.NONE.equals(units)) {
			return "(" + getUnits() + " " + getAmmount() + ")";
		}
		return "";
	}

	public FreqUnits getUnits() {
		return units;
	}

	public void setUnits(FreqUnits units) {
		this.units = units;
	}

	public double getAmmount() {
		return ammount;
	}

	public void setAmmount(double ammount) {
		this.ammount = ammount;
	}

    /**
     * How long does one tick lasts
     * @return length of tick in ms
     */
    public long tick() {
        switch (units) {
            case HOURS:
                return (long) (3600000L * ammount);
            case MINUTES:
                return (long) (60000L * ammount);
            case SECONDS:
                return (long) (1000L * ammount);
            case HZ:
                return (long) (1000L / ammount);
            case PM:
                return (long) (60000 / ammount);
            case NONE:
                return (long) ammount;
            default:
                throw new IllegalStateException("Invalid units.");
        }
    }
}
