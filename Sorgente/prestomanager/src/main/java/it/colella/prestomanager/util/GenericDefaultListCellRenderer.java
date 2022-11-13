package it.colella.prestomanager.util;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 * Permette di usare un {@link DefaultListCellRenderer} per JList con tipo generico
 *
 * @param <E> il tipo della JList alla quale si assegna questo renderer
 */
public class GenericDefaultListCellRenderer<E> implements ListCellRenderer<E> {

	private DefaultListCellRenderer renderer;

	public GenericDefaultListCellRenderer() {
		this.renderer = new DefaultListCellRenderer();
	}

	@Override
	public Component getListCellRendererComponent(JList<? extends E> list, E value,
			int index, boolean isSelected, boolean cellHasFocus) {

		return this.renderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
	}

}
