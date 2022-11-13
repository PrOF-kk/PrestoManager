package it.colella.prestomanager.util;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * Renderer per oggetti {@link Boolean}.
 * Supporta opzionalmente icone differenti per quando la cella è selezionata.
 *
 * <p>Oggetti null o non di tipo Boolean vengono trattati come falsi
 */
public class IconBooleanTableCellRenderer extends DefaultTableCellRenderer {

	private Icon trueIcon;
	private Icon trueSelectedIcon;
	private Icon falseIcon;
	private Icon falseSelectedIcon;

	public IconBooleanTableCellRenderer(Icon trueIcon, Icon falseIcon) {
		this(trueIcon, falseIcon, trueIcon, falseIcon);
	}

	public IconBooleanTableCellRenderer(Icon trueIcon, Icon falseIcon, Icon trueSelectedIcon, Icon falseSelectedIcon) {
		super();
		this.trueIcon = trueIcon;
		this.trueSelectedIcon = trueSelectedIcon;
		this.falseIcon = falseIcon;
		this.falseSelectedIcon = falseSelectedIcon;
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {

		var label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		label.setText(null);
		label.setHorizontalAlignment(SwingConstants.CENTER);

		Icon t = (isSelected) ? trueSelectedIcon : trueIcon;
		Icon f = (isSelected) ? falseSelectedIcon : falseIcon;

		// Se non è un Boolean, oppure è null, oppure è falso
		if (!(value instanceof Boolean b) || !b) {
			label.setIcon(f);
		}
		else {
			label.setIcon(t);
		}

		return label;
	}

}
