package it.colella.prestomanager.view.component;

import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JFormattedTextField;
import javax.swing.JFormattedTextField.AbstractFormatterFactory;

import it.colella.prestomanager.util.PriceFormatter;

import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

/**
 * JSpinner specializzato nella visualizzazione e input di valori di prezzo (double)
 * <br/>
 *
 * Consente di selezionare tutto il contenuto dello spinner con un solo click
 * e di modificare il valore con la rotella del mouse
 *
 */
public class JPriceSpinner extends JSpinner {

	/**
	 * Crea un {@link JPriceSpinner} con valore {@code Double} limitato tra 0 e
	 * 999.99 a intervalli di 0.5
	 */
	public JPriceSpinner() {
		super(new SpinnerNumberModel(0d, 0d, 999.99d, 0.5d));

		// Consenti di usare la rotella del mouse per modificare il valore
		// restando tra il minimo e il massimo
		this.addMouseWheelListener(e -> {

			Object valueToSet = (e.getWheelRotation() > 0)
					? this.getModel().getPreviousValue()
					: this.getModel().getNextValue();

			if (valueToSet != null) {
				this.getModel().setValue(valueToSet);
			}
		});

		// Ottieni l'editor interno del JSpinner
		JFormattedTextField editorTextField = ((JSpinner.DefaultEditor) this.getEditor()).getTextField();
		// Imposta il cursore a ↕
		editorTextField.setCursor(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
		// Seleziona tutto con click singolo
		editorTextField.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				editorTextField.selectAll();
			}
		});

		editorTextField.setFormatterFactory(new PriceFormatterFactory());
	}

	@Override
	public SpinnerNumberModel getModel() {
		return (SpinnerNumberModel) super.getModel();
	}
}

class PriceFormatterFactory extends AbstractFormatterFactory {

	@Override
	public PriceFormatter getFormatter(JFormattedTextField tf) {
		if (tf.getFormatter() instanceof PriceFormatter pf) {
			return pf;
		}
		return new PriceFormatter(0d, 999.99d);
	}
}
