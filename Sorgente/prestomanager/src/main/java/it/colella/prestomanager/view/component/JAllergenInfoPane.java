package it.colella.prestomanager.view.component;

import java.awt.Component;

import javax.swing.JOptionPane;

import it.colella.prestomanager.model.Dish;

/**
 * {@link JOptionPane} contenente una lista di allergeni del piatto selezionato
 */
public class JAllergenInfoPane extends JOptionPane {

	public static void showAllergenInfo(Component parentComponent, Dish dish) {
		StringBuilder builder = new StringBuilder("<html>Può contenere:<ul>");
		dish.getAllergens().stream().sorted().forEach(a -> {
			builder.append("<li>");
			builder.append(a);
			builder.append("</li>");
		});
		builder.append("</ul></html>");

		JOptionPane.showMessageDialog(parentComponent, builder, "Info allergeni", JOptionPane.WARNING_MESSAGE);
	}

	private JAllergenInfoPane() { }
}
