package it.colella.prestomanager.util;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

import javax.swing.JFormattedTextField.AbstractFormatter;

/**
 * Formatter per convertire quantità di denaro da String a Double e viceversa
 */
public class PriceFormatter extends AbstractFormatter {

	private NumberFormat formatter;
	private String currencySymbol;
	private double minimum;
	private double maximum;

	/**
	 * Crea un PriceFormatter senza valore massimo o minimo
	 */
	public PriceFormatter() {
		this.formatter = NumberFormat.getCurrencyInstance();
		this.currencySymbol = Currency.getInstance(Locale.getDefault()).getSymbol();
		this.minimum = Double.MIN_VALUE;
		this.maximum = Double.MAX_VALUE;
	}

	/**
	 * Crea un PriceFormatter che limita il valore restituito tra un minimo e un
	 * massimo
	 *
	 * @param minimum minimo
	 * @param maximum massimo
	 */
	public PriceFormatter(double minimum, double maximum) {
		this();

		if (minimum > maximum) {
			throw new IllegalArgumentException("minimum cannot be greater than maximum");
		}

		this.minimum = minimum;
		this.maximum = maximum;
	}

	@Override
	public Double stringToValue(String text) {
		try {
			String cleanedInput = text.replace(this.currencySymbol, "").replace(',', '.');
			return this.ensureBounds(Double.parseDouble(cleanedInput));
		}
		catch (NumberFormatException e) {
			return null;
		}
	}

	@Override
	public String valueToString(Object value) {
		return this.formatter.format(value);
	}

	private double ensureBounds(double value) {
		if (value < this.minimum) {
			return this.minimum;
		}
		if (value > this.maximum) {
			return this.maximum;
		}
		return value;
	}

}