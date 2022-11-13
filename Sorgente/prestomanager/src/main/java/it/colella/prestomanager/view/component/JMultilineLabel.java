package it.colella.prestomanager.view.component;

import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.UIManager;

/**
 * {@link JTextArea} con proprietà preimpostate per apparire come un
 * {@link JLabel} multilinea
 */
public class JMultilineLabel extends JTextArea {

	/**
	 * Crea un JMultilineLabel senza testo
	 */
	public JMultilineLabel() {
		this(null);
	}

	/**
	 * Crea un JMultilineLabel partendo dal testo specificato
	 *
	 * @param text il testo iniziale
	 */
	public JMultilineLabel(String text) {
		super(text);

		this.setLineWrap(true);
		this.setWrapStyleWord(true);
		this.setOpaque(false);
		this.setEditable(false);
		this.setFocusable(false);
		this.setBorder(UIManager.getBorder("Label.border"));
	}
}
