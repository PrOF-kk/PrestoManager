package it.colella.prestomanager.model;

public class DishTest {

	/**
	 * Consentiamo esplicitamente nomi più lunghi di quanto consentito da GUI, nel
	 * caso venga modificato direttamente il JSON
	 */
	public void testLongName() {
		try {
			new Dish(
					"Vivamus volutpat cursus magna, et condimentum arcu luctus in. In tincidunt velit at ullamcorper euismod. Aenean euismod rhoncus dui, sit.",
					10d,
					"",
					DishType.ANTIPASTO);
			assert true;
		}
		catch (Exception e) {
			assert false;
		}
	}

	/**
	 * Consentiamo esplicitamente descrizioni più lunghe di quanto consentito da
	 * GUI, nel caso venga modificato direttamente il JSON
	 */
	public void testLongDesc() {
		try {
			new Dish(
					"Lorem",
					10d,
					"""
					Nulla id dictum ipsum. Fusce facilisis ex ut quam dictum, dictum eleifend mi ultrices. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nulla id venenatis arcu, a ullamcorper lectus. Fusce in auctor lacus. Aenean malesuada fringilla felis, nec tincidunt lacus scelerisque vel. Sed commodo felis mauris, sed placerat dolor tempus id. Quisque consectetur semper luctus.
					Proin posuere aliquet dui, ut fermentum est imperdiet sed. Suspendisse sit amet aliquet diam, eget tincidunt quam. Proin tempor ornare imperdiet. Praesent pretium volutpat malesuada. Duis volutpat felis neque, et tristique orci volutpat vitae. Nulla venenatis in augue sit amet aliquet. Fusce sodales, magna id sodales ullamcorper, neque enim malesuada sem, gravida elementum diam nunc a lorem. Duis et nulla sed eros aliquet dapibus nec non mauris. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Vestibulum gravida, nisl ut dictum cursus, arcu purus finibus purus, at feugiat libero tortor in risus. Sed sollicitudin lacus et molestie egestas. Donec elit erat, facilisis ut nisl eu, lacinia hendrerit risus.
					Cras lacus erat, consequat at vestibulum nec, tempor quis orci. Curabitur cursus imperdiet eros eget tempor. Suspendisse dignissim urna nec eros gravida tincidunt. Mauris egestas nibh ligula, quis viverra lorem placerat non. Aenean fringilla ex eget pretium feugiat. Duis vehicula blandit velit et cursus. Sed a leo semper, dictum enim id, efficitur orci. Nulla dui purus, iaculis quis feugiat non, eleifend ut augue. Aenean fermentum tellus eu lacus venenatis, eget placerat diam lacinia.
					Suspendisse in facilisis nunc. Quisque iaculis, nunc non vestibulum auctor, erat nisl elementum nibh, eu facilisis nulla sem a magna. Praesent auctor turpis non felis feugiat, nec pulvinar nisl placerat. Integer quis tortor tincidunt, porta nibh ut, interdum felis. Donec eget leo varius, posuere justo sit amet, vulputate dui. Etiam id hendrerit augue. Maecenas suscipit malesuada mi eget dignissim. Suspendisse sit amet risus maximus, convallis tortor ac, vulputate massa. Mauris quam mauris, gravida suscipit velit vitae, faucibus efficitur lacus. Aliquam at turpis gravida est vestibulum feugiat. Nulla nec arcu eleifend felis scelerisque consectetur quis vitae nisl. Pellentesque sed vulputate mauris. Proin hendrerit libero nec libero consequat, quis vestibulum odio tincidunt. Aliquam luctus est tortor, quis pulvinar massa egestas vitae.
					Nunc eleifend libero nec nisl pellentesque viverra. Integer nec tincidunt felis. Maecenas sodales justo tincidunt, interdum lorem ac, sodales elit. Donec volutpat enim justo, ac lobortis odio commodo at. Morbi rutrum risus id augue mollis, sed volutpat ante mollis. Etiam enim nunc, ultrices ac lectus a, sodales tristique ipsum. Vestibulum ac tempus eros. Sed luctus metus eget neque dignissim, vel tincidunt diam semper.
					Morbi vestibulum augue sit amet ipsum venenatis, nec fermentum nulla placerat. Suspendisse facilisis semper velit.
					""",
					DishType.ANTIPASTO);
			assert true;
		}
		catch (Exception e) {
			assert false;
		}
	}

	/**
	 * Consentiamo esplicitamente prezzi negativi. Nonostante ciò non sia possibile
	 * per l'utente da GUI, lasciamo aperta la possibilità di espandere il programma
	 * con, per esempio, supporto per sconti selezionabili dal cameriere
	 * (implementabili semplicemente come Dish con prezzo negativo)
	 */
	public void testNegativePrice() {

		try {
			Dish d = new Dish("Sconto veterani WWII", -5d, "Sconto per veterani della seconda guerra mondiale", DishType.PRIMO);
			assert d.getPrice() == -5d;
		}
		catch (Exception e) {
			assert false;
		}
	}

	/** Se il nome è diverso, i piatti sono diversi */
	public void testNotEquals() {
		Dish one = new Dish("Uno", 0, "", DishType.PRIMO);
		Dish two = new Dish("Due", 0, "", DishType.PRIMO);

		// Basta che il nome sia diverso
		assert !one.equals(two);
	}

	/** Se il nome è uguale, i piatti sono uguali */
	public void testEquals() {
		Dish one = new Dish("Uno", 0, "", DishType.PRIMO);

		// Basta che il nome sia uguale
		Dish oneDifferent = new Dish("Uno", 5432d, "Lorem Ipsum dolor sit amet", DishType.DESSERT);
		assert one.equals(oneDifferent);
	}

	/** Whitespace normalmente ignorato da equals(), qui non lo deve essere */
	public void testDeepEqualsName() {
		Dish one = new Dish(
				"Uno",
				10d,
				"The quick brown fox jumps over the lazy dog",
				DishType.SECONDO,
				Allergen.GLUTINE, Allergen.LATTE);
		Dish oneMod = new Dish(
				" Uno ",
				10d,
				"The quick brown fox jumps over the lazy dog",
				DishType.SECONDO,
				Allergen.GLUTINE, Allergen.LATTE);
		assert !one.deepEquals(oneMod);
	}

	/** deepEquals() - prezzo diverso */
	public void testDeepEqualsPrice() {
		Dish one = new Dish(
				"Uno",
				10d,
				"The quick brown fox jumps over the lazy dog",
				DishType.SECONDO,
				Allergen.GLUTINE, Allergen.LATTE);
		Dish oneMod = new Dish(
				"Uno",
				99d,
				"The quick brown fox jumps over the lazy dog",
				DishType.SECONDO,
				Allergen.GLUTINE, Allergen.LATTE);
		assert !one.deepEquals(oneMod);
	}

	/** deepEquals() - descrizione diversa */
	public void testDeepEqualsDesc() {
		Dish one = new Dish(
				"Uno",
				10d,
				"The quick brown fox jumps over the lazy dog",
				DishType.SECONDO,
				Allergen.GLUTINE, Allergen.LATTE);
		Dish oneMod = new Dish(
				"Uno",
				10d,
				"Ma la volpe col suo balzo ha raggiunto il quieto Fido",
				DishType.SECONDO,
				Allergen.GLUTINE, Allergen.LATTE);
		assert !one.deepEquals(oneMod);
	}

	/** deepEquals() - tipo diverso */
	public void testDeepEqualsType() {
		Dish one = new Dish(
				"Uno",
				10d,
				"The quick brown fox jumps over the lazy dog",
				DishType.SECONDO,
				Allergen.GLUTINE, Allergen.LATTE);
		Dish oneMod = new Dish(
				"Uno",
				10d,
				"The quick brown fox jumps over the lazy dog",
				DishType.DESSERT,
				Allergen.GLUTINE, Allergen.LATTE);
		assert !one.deepEquals(oneMod);
	}

	/** deepEquals() - allergeni diversi */
	public void testDeepEqualsAllergens() {
		Dish one = new Dish(
				"Uno",
				10d,
				"The quick brown fox jumps over the lazy dog",
				DishType.SECONDO,
				Allergen.GLUTINE, Allergen.LATTE);
		Dish oneMod = new Dish(
				"Uno",
				99d,
				"The quick brown fox jumps over the lazy dog",
				DishType.SECONDO,
				Allergen.GLUTINE, Allergen.LATTE, Allergen.ARACHIDI);
		assert !one.deepEquals(oneMod);
	}

	/** deepEquals() - tutto uguale */
	public void testDeepEquals() {
		Dish one = new Dish(
				"Uno",
				10d,
				"The quick brown fox jumps over the lazy dog",
				DishType.SECONDO,
				Allergen.GLUTINE, Allergen.LATTE);
		Dish oneMod = new Dish(
				"Uno",
				10d,
				"The quick brown fox jumps over the lazy dog",
				DishType.SECONDO,
				Allergen.GLUTINE, Allergen.LATTE);
		assert one.deepEquals(oneMod);
	}

	/* Il set restituito deve essere read-only */
	public void testUnmodifiableSet() {
		Dish d = new Dish();
		try {
			d.getAllergens().add(Allergen.LATTE);
			// Non ha dato eccezione, il set è modificabile
			assert false;
		}
		catch (UnsupportedOperationException e) {
			// Set correttamente non modificabile
			assert true;
		}
	}

}
