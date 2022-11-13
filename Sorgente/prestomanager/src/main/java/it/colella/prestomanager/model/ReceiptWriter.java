package it.colella.prestomanager.model;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.colella.prestomanager.util.PriceFormatter;

/**
 * Responsabile della scrittura di scontrini su disco fisso
 */
public class ReceiptWriter {

	private static final Logger log = LoggerFactory.getLogger(ReceiptWriter.class);

	private static final String RECEIPT_FOLDER_PATH = "receipts/";
	private static final PriceFormatter formatter = new PriceFormatter();

	/** Se aprire o meno il file con lo scontrino dopo la scrittura */
	private static final boolean OPEN_AFTER_WRITE = true;

	/**
	 * Numero di caratteri necessari per stampare il numero di porzioni N
	 * <pre>
	 * ' xN '
	 * '1234'
	 * </pre>
	 */
	private static final int CHARS_AMOUNT = 4;
	/**
	 * Numero di caratteri necessari per stampare il prezzo
	 * <pre>
	 * '000,00 €'
	 * '12345678'
	 * </pre>
	 */
	private static final int CHARS_PRICE = 8;

	// Blocca inizializzazione
	private ReceiptWriter() { }

	/**
	 * Scrive lo scontrino per un ordine su disco fisso.
	 * <p>
	 * È noto che i piatti con nomi contenenti caratteri unicode con più di un
	 * <b>code point</b> non vengono stampati con il padding corretto.
	 *
	 * <p>Ciò è causato dal fatto che, per esempio,
	 * <b>Á</b> è un carattere singolo, ma può essere codificato sia come
	 *
	 * <pre>
	 * U+00C1    LATIN CAPITAL LETTER A WITH ACUTE
	 * --> "À".length() viene 1
	 * </pre>
	 *
	 * che come
	 *
	 * <pre>
	 * U+0041    LATIN CAPITAL LETTER A
	 * U+0301    COMBINING ACUTE ACCENT
	 * --> "À".length() viene 2
	 * </pre>
	 *
	 * @param order l'ordine del quale stampare lo scontrino
	 * @return {@code true} se lo scontrino stato salvato con successo,
	 *         {@code false} altrimenti
	 */
	public static boolean write(Order order) {
		LocalDateTime now = LocalDateTime.now();

		// Formato ISO 8601
		// Non usiamo DateTimeFormatter.ISO_LOCAL_DATE_TIME perché includerebbe anche i
		// nanosecondi, se supportati
		String filename = RECEIPT_FOLDER_PATH + now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'kk:mm:ss"));

		File destFile = new File(filename);
		destFile.getParentFile().mkdirs();

		// Get longest dish/setting length
		int maxLen = longestLine(order);
		maxLen += CHARS_AMOUNT + CHARS_PRICE;

		StringBuilder receipt = new StringBuilder();

		// Nome
		appendCenterPad(receipt, SettingsManager.getInstance().get(Setting.NAME), maxLen);
		// P.IVA
		appendCenterPad(receipt, SettingsManager.getInstance().get(Setting.VAT), maxLen);
		// Indirizzo
		appendCenterPad(receipt, SettingsManager.getInstance().get(Setting.ADDRESS), maxLen);
		// Telefono
		appendCenterPad(receipt, SettingsManager.getInstance().get(Setting.PHONE), maxLen);

		appendLine(receipt, maxLen);
		appendDishes(receipt, order, maxLen);
		appendLine(receipt, maxLen);

		appendTotal(receipt, order, maxLen);

		try (PrintWriter writer = new PrintWriter(filename)) {
			writer.write(receipt.toString());
			writer.flush();
		}
		catch (IOException e) {
			log.error("Could not write receipt", e);
			return false;
		}

		if (OPEN_AFTER_WRITE) {
			try {
				Desktop.getDesktop().open(destFile);
			}
			catch (IOException e) {
				// Volutamente ignora eccezioni,
				// è una funzione prettamente dimostrativa
				log.error("Could not open receipt", e);
			}
		}

		return true;
	}

	/**
	 * Restituisce la lunghezza massima necessaria per stampare lo scontrino
	 *
	 * @param order l'ordine da stampare
	 * @return numero di caratteri necessari per stampare una riga di scontrino
	 */
	private static int longestLine(Order order) {
		int maxDish = longestDish(order);
		int maxSetting = longestSetting();

		return (maxDish > maxSetting)
				? maxDish
				: maxSetting;
	}

	/**
	 * Restituisce la lunghezza del nome di piatto più lungo in quest'ordine
	 *
	 * @param order l'ordine da stampare
	 * @return numero di caratteri del nome del piatto più lungo nell'ordine
	 */
	private static int longestDish(Order order) {
		return order.getMap()
				.keySet().stream()
				.mapToInt(d -> d.getName().length())
				.max()
				.orElse(0);
	}

	/**
	 * Restituisce la lunghezza del nome dell'impostazione più lunga
	 *
	 * @return numero di caratteri del nome dell'impostazione più lunga
	 */
	private static int longestSetting() {
		return SettingsManager.getInstance().getSettings()
				.values().stream()
				.mapToInt(String::length)
				.max()
				.orElse(0);
	}

	/**
	 * Appende del testo allineato a destra
	 *
	 * @param builder  lo StringBuilder da usare
	 * @param toAppend stringa da stampare a destra
	 * @param lineLen  lunghezza della riga da stampare
	 */
	private static void appendLeftPad(StringBuilder builder, String toAppend, int lineLen) {
		builder.append(" ".repeat(lineLen - toAppend.length()));
		builder.append(toAppend);
		builder.append('\n');
	}

	/**
	 * Appende del testo allineato al centro
	 *
	 * @param builder  lo StringBuilder da usare
	 * @param toAppend stringa da stampare al centro
	 * @param lineLen  lunghezza della riga da stampare
	 */
	private static void appendCenterPad(StringBuilder builder, String toAppend, int lineLen) {
		if (lineLen < toAppend.length()) {
			throw new IllegalArgumentException("Line length less than String length");
		}

		// Se lineLen - toAppend.length() è dispari c'è un errore voluto di 1 char
		int padding = (lineLen - toAppend.length()) / 2;

		builder.append(" ".repeat(padding));
		builder.append(toAppend);
		builder.append('\n');
	}

	/**
	 * Appende la lista di piatti allo scontrino
	 *
	 * @param builder lo StringBuilder da usare
	 * @param order   l'ordine del quale stampare i piatti
	 * @param lineLen la lunghezza fissa di ogni riga da stampare
	 */
	private static void appendDishes(StringBuilder builder, Order order, int lineLen) {

		order.getMap().forEach((dish, amount) -> {
			int padLen = lineLen - dish.getName().length() - CHARS_PRICE - CHARS_AMOUNT;
			String paddedPrice = String.format("%8s", formatter.valueToString(dish.getPrice()));

			builder.append(dish.getName());
			builder.append(" ".repeat(padLen));

			builder.append(" x");
			builder.append(order.getAmount(dish));
			builder.append(' ');

			builder.append(paddedPrice);
			builder.append('\n');
		});
	}

	/**
	 * Appende il totale allo scontrino
	 *
	 * @param builder lo StringBuilder da usare
	 * @param order   l'ordine del quale stampare il totale
	 * @param lineLen la lunghezza della stringa da stampare
	 */
	private static void appendTotal(StringBuilder builder, Order order, int lineLen) {
		String totale = "TOTALE: ";

		appendLeftPad(builder, totale + formatter.valueToString(order.calculateTotal()), lineLen);
	}

	/**
	 * Stampa una riga orizzontale
	 *
	 * @param builder lo StringBuilder da usare
	 * @param lineLen la lunghezza della riga
	 */
	private static void appendLine(StringBuilder builder, int lineLen) {
		builder.append("-".repeat(lineLen));
		builder.append('\n');
	}

}
