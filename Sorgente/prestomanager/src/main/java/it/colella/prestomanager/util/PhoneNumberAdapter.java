package it.colella.prestomanager.util;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JTextField;

/**
 * {@link KeyAdapter} che consente solo numeri di telefono nel {@link JTextField} collegato
 * <p><b>Per funzionare correttamente deve essere il primo listener aggiunto</b></p>
 *
 * <p>Permette di inserire un carattere se e solo se è uno dei seguenti:</p>
 * <ul>
 * <li>Un <b>+</b> all'inizio</li>
 * <li>Uno <b>spazio</b> non all'inizio <b>e</b> non di seguito ad un altro spazio</li>
 * <li>Una <b>cifra</b></li>
 * </ul>
 */
public class PhoneNumberAdapter extends KeyAdapter {

	private final JTextField field;

	/**
	 * Crea un PhoneNumberAdapter
	 *
	 * @param field il JTextField collegato a questo adapter
	 */
	public PhoneNumberAdapter(JTextField field) {
		this.field = field;
	}

	@Override
	public void keyTyped(KeyEvent e) {

		char c = e.getKeyChar();
		String text = this.field.getText();

		// N.B. non unire if annidati

		// Blocca uno spazio all'inizio o dopo un altro spazio
		if (c == ' ') {
			if (text.isEmpty() || text.endsWith(" ")) {
				e.consume();
			}
		}
		// Blocca un '+' se non all'inizio
		else if (c == '+') {
			if (!text.isEmpty()) {
				e.consume();
			}
		}
		// ͟S͟e͟ ͟n͟o͟n͟ ͟è͟ ͟u͟n͟o͟ ͟d͟i͟ ͟q͟u͟e͟l͟l͟i͟ ͟s͟o͟p͟r͟a͟, blocca se non un numero (non riordinare)
		else if (!Character.isDigit(c)) {
			e.consume();
		}
	}
}
