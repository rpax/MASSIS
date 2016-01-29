package cz.cuni.amis.pogamut.sposh.elements;
/**
 * It is more or less time and units of time. Used for timeouts of POSH plan.
 * 
 * @author Honza
 */
@Deprecated
public class SolTime {

    @Deprecated
	public enum TimeUnits {
		HOURS("hours", 0),
		MINUTES("minutes", 1),
		SECONDS("seconds", 2),
		NONE("none", 3);

		private final String name;
		private final int id;

		private TimeUnits(String name, int id) {
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
	
	private TimeUnits _units;
	private double _count;
	
	SolTime(TimeUnits units, String count) {
		this._count = new Double(count).doubleValue();
		this._units = units;
	}

	/**
	 * Create a default time "0 none"
	 */
	public SolTime() {
		this(TimeUnits.NONE, "0");
	}
	
	@Override
	public String toString() {
		if (!TimeUnits.NONE.equals(_units))
			return "("+getUnits()+" "+getCount()+")";
		return "";
	}

	/**
	 * @return the _units
	 */
	public TimeUnits getUnits() {
		return _units;
	}

	/**
	 * @param units the _units to set
	 */
	public void setUnits(TimeUnits units) {
		this._units = units;
	}

	/**
	 * @return the _count
	 */
	public double getCount() {
		return _count;
	}

	/**
	 * @param count the _count to set
	 */
	public void setCount(double count) {
		this._count = count;
	}

}
