package it.colella.prestomanager.model;

/**
 * Descrive il tipo di un piatto
 */
public enum DishType {
	BEVANDA("Bevanda"),
	ANTIPASTO("Antipasto"),
    PRIMO("Primo"),
    SECONDO("Secondo"),
    CONTORNO("Contorno"),
    DESSERT("Dessert");

	private String formattedName;

	DishType(String formattedName) {
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
