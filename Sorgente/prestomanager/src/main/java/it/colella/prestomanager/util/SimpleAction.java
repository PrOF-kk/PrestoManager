package it.colella.prestomanager.util;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;

/**
 * Semplifica la creazione di {@link Action} consentendo di usare lambda o
 * method reference
 *
 * <pre>
 * Action action = new SimpleAction(this::performAction);
 * // Oppure
 * Action action = new SimpleAction((arg) -> this.performAction(arg));
 *
 * // Invece di
 *
 * Action action = new AbstractAction() {
 *
 * 	&#64;Override
 * 	public void actionPerformed(ActionEvent e) {
 * 		this.performAction();
 * 	}
 * };
 * </pre>
 *
 * L'uso di {@link Runnable} come semplice interfaccia funzionale senza
 * parametri né valori di ritorno, non eseguita in un Thread separato, è
 * dibattuto <a href=
 * "https://stackoverflow.com/questions/23868733/java-8-functional-interface-with-no-arguments-and-no-return-value">
 * QUI (StackOverflow)</a>
 */
public class SimpleAction extends AbstractAction {

	private Runnable callback;

	public SimpleAction(Runnable callback) {
		super();
		this.callback = callback;
	}

	public SimpleAction(String name, Runnable callback) {
		super(name);
		this.callback = callback;
	}

	public SimpleAction(String name, Icon icon, Runnable callback) {
		super(name, icon);
		this.callback = callback;
	}

	public SimpleAction(Icon icon, Runnable callback) {
		super(null, icon);
		this.callback = callback;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		callback.run();
	}
}
