package it.colella.prestomanager.model;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;

/**
 * Rappresenta l'ordine di un tavolo. Un tavolo potrà fare altri ordini in seguito,
 * quindi va immaginato più come <i>comanda<i/> che come <i>conto<i/>
 */
public class Order implements Iterable<Entry<Dish, Integer>> {

	/**
	 * Mappa <b>ordinata</b> di tipo { piatto : numero porzioni }
	 */
	private Map<Dish, Integer> dishMap;
	private int tableNumber;

	public Order() {
		this.tableNumber = 1;
		this.dishMap = new LinkedHashMap<>();
	}

	/**
	 * Aggiunge una porzione di un piatto all'ordine
	 *
	 * @param d il piatto da aggiungere
	 */
	public void add(Dish d) {
		this.dishMap.put(d, this.dishMap.getOrDefault(d, 0) + 1);
	}
	/**
	 * Rimuove una porzione di un piatto dall'ordine
	 *
	 * @param d il piatto da rimuovere
	 */
	public void remove(Dish d) {
		if (!this.dishMap.containsKey(d)) {
			return;
		}
		if (this.dishMap.get(d) <= 1) {
			this.dishMap.remove(d);
		}
		else {
			this.dishMap.put(d, this.dishMap.get(d) - 1);
		}
	}

	/**
	 * Aggiunge multiple porzioni di un piatto all'ordine
	 *
	 * @param d      il piatto da aggiungere
	 * @param amount il numero di porzioni da aggiungere
	 * @throws IllegalArgumentException se {@code amount} è ≤ 0
	 */
	public void add(Dish d, int amount) {
		if (amount <= 0) {
			throw new IllegalArgumentException("Negative dish amount for addDishAmount");
		}

		this.dishMap.put(d, this.dishMap.getOrDefault(d, 0) + amount);
	}

	/**
	 * Rimuove multiple porzioni di un piatto dall'ordine.
	 * Se l'ordine non contiene quel piatto, NOP
	 *
	 * @param d      il piatto da rimuovere
	 * @param amount il numero di porzioni da rimuovere
	 * @throws IllegalArgumentException se {@code amount} è ≤ 0
	 */
	public void remove(Dish d, int amount) {
		if (amount <= 0) {
			throw new IllegalArgumentException("Negative dish amount for removeDishAmount");
		}

		Integer prevAmount = this.dishMap.get(d);
		if (prevAmount != null && prevAmount > amount) {
			this.dishMap.put(d, prevAmount - amount);
		}
		else {
			this.dishMap.remove(d);
		}
	}

	/**
	 * Aggiunge tutte le porzioni di tutti i piatti di un altro ordine a questo
	 *
	 * @param o l'ordine dal quale aggiungere i piatti
	 */
	public void addAll(Order o) {
		o.getMap().forEach((dish, amount) -> this.add(dish, amount));
	}

	public int getTableNumber() {
		return this.tableNumber;
	}
	/**
	 * Imposta il numero del tavolo dell'ordine
	 *
	 * @param tableNumber il numero del tavolo
	 * @throws IllegalArgumentException se {@code tableNumber} ≤ 0
	 */
	public void setTableNumber(int tableNumber) {
		if (tableNumber <= 0) {
			throw new IllegalArgumentException("Invalid table number: " + tableNumber);
		}
		this.tableNumber = tableNumber;
	}

	/**
	 * Restituisce quante porzioni di un piatto sono state ordinate
	 *
	 * @param d il piatto
	 */
	public int getAmount(Dish d) {
		return dishMap.getOrDefault(d, 0);
	}

	/**
	 * Restituisce una mappa <b>ordinata</b> read-only di tipo { piatto : numero porzioni }
	 */
	public Map<Dish, Integer> getMap() {
		return Collections.unmodifiableMap(this.dishMap);
	}

	/**
	 * Calcola e restituisce il prezzo totale per quest'ordine
	 *
	 * @return il totale, o {@code 0} se l'ordine è vuoto
	 */
	public double calculateTotal() {
		// in caso di ordine vuoto sum() restituisce 0d
		return this.getMap().entrySet().stream()
				.mapToDouble(
						entry -> entry.getKey().getPrice() * entry.getValue())
				.sum();
	}

	/**
	 * Restituisce il piatto all'indice {@code index} di quest'ordine
	 *
	 * @throws NoSuchElementException Se l'ordine non contiene un piatto a
	 *                                quell'indice
	 */
	public Dish get(int index) {
		Iterator<Dish> iter = this.dishMap.keySet().iterator();
		for (int i = 0; i < index; i++) {
			iter.next();
		}
		return iter.next();
	}

	/**
	 * Restituisce l'indice del piatto {@code d} in quest'ordine
	 *
	 * @param d il piatto
	 * @return l'indice del piatto se presente, altrimenti {@code -1}
	 */
	public int indexOf(Dish d) {

		// Short circuit se il piatto non è contenuto
		if (!this.dishMap.containsKey(d)) {
			return -1;
		}

		int c = 0;
		Iterator<Dish> iter = this.dishMap.keySet().iterator();
		while (iter.hasNext()) {
			if (iter.next().equals(d)) {
				return c;
			}
			c++;
		}

		// Non arriviamo mai qui
		return -1;
	}

	@Override
	public Iterator<Entry<Dish, Integer>> iterator() {
		return this.dishMap.entrySet().iterator();
	}

	@Override
	public String toString() {
		return "Order(" + this.dishMap.toString() + ")tavolo=" + this.getTableNumber();
	}
}
