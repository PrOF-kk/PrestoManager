package it.colella.prestomanager.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

/**
 * Rappresenta un piatto. Immutabile
 */
public class Dish implements Comparable<Dish> {

	private String name;
	private double price;
	private String description;
	private Set<Allergen> allergens;
	private DishType type;

	/**
	 * Crea un piatto con dati di default:
	 * <li><b>Nome:</b> ""</li>
	 * <li><b>Prezzo:</b> 0.0</li>
	 * <li><b>Descrizione:</b> ""</li>
	 * <li><b>Allergeni:</b> Nessuno</li>
	 * <li><b>Tipo:</b> Bevanda</li>
	 */
	public Dish() {
		this.name = "";
		this.price = 0d;
		this.description = "";
		this.allergens = EnumSet.noneOf(Allergen.class);
		this.type = DishType.BEVANDA;
	}

	/**
	 * Crea un piatto con dati di default e un nome dato
	 *
	 * @param name il nome del piatto
	 * @see Dish#Dish()
	 */
	public Dish(String name) {
		this();
		this.name = name;
	}

	/**
	 * Crea un piatto dai dati forniti con nessun allergene
	 *
	 * @param name        nome (unico)
	 * @param price       prezzo
	 * @param description descrizione
	 * @param type        vedi {@link DishType}
	 */
	public Dish(String name, double price, String description, DishType type) {
		this.name = name;
		this.price = price;
		this.description = description;
		this.allergens = EnumSet.noneOf(Allergen.class);
		this.type = type;
	}

	/**
	 * Crea un piatto dai dati forniti. È consigliato l'uso di un {@link EnumSet}
	 * anziché un set generico
	 *
	 * @param name        nome (unico)
	 * @param price       prezzo
	 * @param description descrizione
	 * @param type        vedi {@link DishType}
	 * @param allergens   set di {@link Allergen}
	 */
	public Dish(String name, double price, String description, DishType type, Set<Allergen> allergens) {
		this(name, price, description, type);
		this.allergens = EnumSet.copyOf(allergens);
	}

	/**
	 * Crea un piatto dai dati forniti. È consigliato l'uso di un {@link EnumSet}
	 * anziché un set generico
	 *
	 * @param name        nome (unico)
	 * @param price       prezzo
	 * @param description descrizione
	 * @param type        vedi {@link DishType}
	 * @param allergens   vedi {@link Allergen}
	 */
	public Dish(String name, double price, String description, DishType type, Allergen... allergens) {
		this(name, price, description, type, EnumSet.copyOf(Arrays.asList(allergens)));
	}

	/**
	 * Determina se due patti sono uguali tra loro.
	 * Due piatti sono uguali se e solo se hanno lo stesso nome.
	 * Le differenze in capitalizzazione e gli spazi all'inizio o fine del nome sono ignorati
	 */
	@Override
	public boolean equals(Object other) {
		if (other instanceof Dish d) {
			return this.getName().trim().equalsIgnoreCase(d.getName().trim());
		}
		return false;
	}
	@Override
	public int hashCode() {
		return this.getName().trim().toLowerCase().hashCode();
	}

	/**
	 * Determina se due piatti sono esattamente uguali tra loro, quindi se nome,
	 * prezzo, descrizione, allergeni e tipo sono esattamente uguali
	 *
	 * @param other l'oggetto con cui effettuare la comparazione
	 * @return {@code true} se quest'oggetto è esattamente lo stesso dell'oggetto
	 *         parametro; altrimenti {@code false}.
	 */
	public boolean deepEquals(Dish other) {

		if (other == null) {
			return false;
		}

		return (this.getName().equals(other.getName())
				&& this.getPrice() == other.getPrice()
				&& this.getDescription().equals(other.getDescription())
				&& this.getAllergens().equals(other.getAllergens())
				&& this.getType() == other.getType());

		// Versione originale, sostituita il 10-05-2022
		/*
		if (other == null) {
			return false;
		}

		for (Field f : Dish.class.getDeclaredFields()) {
			try {
				if (!f.get(this).equals(f.get(other))) {
					return false;
				}
			}
			catch (IllegalArgumentException | IllegalAccessException e) {
				log.error("Exception in Dish#deepEquals", e);
				return false;
			}
		}
		return true;
		*/
	}

	@Override
	public int compareTo(Dish arg0) {
		return this.getName().compareTo(arg0.getName());
	}

	@Override
	public String toString() {
		return "Dish(%s|%.2f|%s)".formatted(this.getName(), this.getPrice(), this.getType());
	}

	public String getName() {
		return this.name;
	}

	public double getPrice() {
		return this.price;
	}

	public String getDescription() {
		return this.description;
	}

	/**
	 * Restituisce un Set read-only degli allergeni del piatto
	 */
	public Set<Allergen> getAllergens() {
		return Collections.unmodifiableSet(this.allergens);
	}

	public DishType getType() {
		return this.type;
	}

}
