package it.colella.prestomanager.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.Border;

/**
 * Descrive una pagina, cioè un JPanel che occupa e gestisce tutto lo spazio
 * fornito dal JFrame
 */
public abstract class Page extends JPanel {

	/** Spaziatura tra componenti generici */
	protected static final int GENERIC_SPACING = 10;
	/** Spaziatura ridotta tra componenti più piccoli come pulsanti */
	protected static final int BUTTON_SPACING  = 6;

	private JFrame frame;

	// Gestione bordi di debug
	private Map<JComponent, Border> borderCache;
	private boolean showingDebugBorders;

	protected Page(JFrame frame) {
		super();
		this.frame = frame;

		this.showingDebugBorders = false;
		this.borderCache = new HashMap<>();

		// Gestisci questi tasti indipendentemente dalle scorciatoie della pagine singole
		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(e -> {
			if (e.getID() != KeyEvent.KEY_RELEASED) {
				return false;
			}

			if (e.getKeyCode() == KeyEvent.VK_F3) {
				this.setShowingDebugBorders(!this.showingDebugBorders);
			}

			return false;
		});

		this.setOpaque(true);
		this.repaint();
	}

	/**
	 * Ottiene tutti i JComponent in un JPanel c. Se uno dei componenti di c è
	 * anch'esso un JPanel, ottiene anche tutti i JComponent di quel panel, e così
	 * via.
	 *
	 * @param c il JPanel del quale trovare tutti i JComponent
	 */
	private static Set<JComponent> getJComponentsRecursive(JPanel c) {

		Set<JComponent> ret = new HashSet<>();

		for (Component comp : c.getComponents()) {

			if (comp instanceof JComponent jcomp) {
				ret.add(jcomp);

				if (jcomp instanceof JPanel jpnl) {
					ret.addAll(getJComponentsRecursive(jpnl));
				}
			}
		}
		return ret;
	}

	/**
	 * Imposta la pagina corrente per un JFrame, utile subito dopo la sua creazione.
	 * Per passare da una pagina all'altra usare invece {@link #switchPage(Page)}
	 *
	 * @param frame JFrame contenitore
	 * @param page  pagina da impostare nel frame
	 */
	public static void setPage(JFrame frame, Page page) {
		frame.getContentPane().removeAll();
		frame.getContentPane().add(page);
	}

	/**
	 * Cambia la pagina corrente da quella chiamante a quella destinazione
	 *
	 * @param destination la pagina destinazione
	 */
	protected void switchPage(Page destination) {
		this.frame.getContentPane().removeAll();
		this.frame.getContentPane().add(destination);

		this.frame.validate();
		this.frame.repaint();
	}

	/**
	 * Imposta se mostare o meno i bordi di debug della pagina.
	 *
	 * @param value {@code true} per mostrarli, {@code false} per nasconderli
	 */
	protected final void setShowingDebugBorders(boolean value) {
		this.showingDebugBorders = value;

		Set<JComponent> jcomponents = getJComponentsRecursive(this);

		if (this.showingDebugBorders) {

			for (JComponent jc : jcomponents) {
				this.borderCache.putIfAbsent(jc, jc.getBorder());

				jc.setBorder(
						BorderFactory.createCompoundBorder(
								BorderFactory.createLineBorder(Color.RED),
								jc.getBorder()));
			}
		}
		else {
			for (JComponent jc : jcomponents) {
				jc.setBorder(this.borderCache.get(jc));
			}
		}
	}

	/**
	 * Restituisce il JFrame contenitore della pagina
	 */
	protected JFrame getFrame() {
		return this.frame;
	}
}
