package it.colella.prestomanager.model;

public class OrderTest {

	/**
	 * Un ordine non può avere numero di tavolo negativo
	 */
	public void testNegativeTable() {
		Order o = new Order();
		try {
			o.setTableNumber(-4);
			assert false;
		}
		catch (IllegalArgumentException e) {
			assert true;
		}
	}

	/**
	 * Aggiungere quantità negative di un piatto non è consentito
	 */
	public void testAddNegativeAmount() {
		Order o = new Order();
		Dish d = new Dish();

		try {
			o.add(d, -5);
			assert false;
		}
		catch (IllegalArgumentException e) {
			assert true;
		}
	}

	/**
	 * Rimuovere quantità negative di un piatto non è consentito
	 */
	public void testRemoveNegativeAmount() {
		Order o = new Order();
		Dish d = new Dish();

		try {
			// ...a prescindere che il piatto sia nell'ordine o meno
			o.remove(d, -5);
			assert false;
		}
		catch (IllegalArgumentException e) {
			assert true;
		}
	}

	/**
	 * Order::getMap deve essere read-only
	 */
	public void testUnmodifiableMap() {
		Order o = new Order();
		try {
			o.getMap().put(new Dish(), 5);
			assert false;
		}
		catch (UnsupportedOperationException e) {
			assert true;
		}
	}
}
