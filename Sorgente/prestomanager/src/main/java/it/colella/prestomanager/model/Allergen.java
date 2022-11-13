package it.colella.prestomanager.model;

/**
 * Rappresenta un allergene alimentare tra quelli del REG. (UE) n. 1169/2011
 */
public enum Allergen {
	GLUTINE("Glutine"),
	CROSTACEI("Crostacei"),
	UOVA("Uova"),
	PESCE("Pesce"),
	ARACHIDI("Arachidi"),
	SOIA("Soia"),
	LATTE("Latte"),
	FRUTTA_GUSCIO("Frutta a guscio"),
	SEDANO("Sedano"),
	SENAPE("Senape"),
	SESAMO("Semi di sesamo"),
	SO2("Anidride solforosa o solfiti"),
	LUPINO("Lupino"),
	MOLLUSCHI("Molluschi");

	private String formattedName;

	Allergen(String formattedName) {
		this.formattedName = formattedName;
	}

	/**
	 * Restituisce il nome <b>formattato</b> dell'enum, in modo che sia facilmente
	 * usabile per la GUI
	 */
	@Override
	public String toString() {
		return this.formattedName;
	}
}
