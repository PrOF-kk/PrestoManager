package it.colella.prestomanager.view.component;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import it.colella.prestomanager.model.Dish;
import it.colella.prestomanager.util.GenericDefaultListCellRenderer;

/**
 * {@link ListCellRenderer} per oggetti {@link Dish}
 */
public class DishListCellRenderer extends GenericDefaultListCellRenderer<Dish> {

	@Override
	public Component getListCellRendererComponent(JList<? extends Dish> list, Dish value,
			int index, boolean isSelected, boolean cellHasFocus) {

		JLabel rendererComponent = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		rendererComponent.setText(value.getName());
		return rendererComponent;
	}

}
