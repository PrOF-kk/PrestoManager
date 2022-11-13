package it.colella.prestomanager.view.component;

import static it.colella.prestomanager.view.component.OrderCompletionTableModel.COL_AMOUNT;
import static it.colella.prestomanager.view.component.OrderCompletionTableModel.COL_COMPLETION;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;

import com.formdev.flatlaf.extras.FlatSVGIcon;

import it.colella.prestomanager.model.Order;
import it.colella.prestomanager.util.IconBooleanTableCellRenderer;

/**
 * JTable che tramite un {@link OrderCompletionTableModel} raffigura un ordine
 * e lo stato di completezza di ogni suo piatto
 */
public class JOrderCompletionTable extends JTable {

	/**
	 * Crea una tabella dall'ordine specificato con nessun piatto evaso
	 *
	 * @param order l'ordine usato per la creazione della tabella
	 */
	public JOrderCompletionTable(Order order) {
		super(new OrderCompletionTableModel(order));

		this.setShowGrid(true);
		this.setRowSelectionAllowed(true);
		this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		// Rappresenta se evaso o no come icona
		this.setDefaultRenderer(Boolean.class,
				new IconBooleanTableCellRenderer(
					new FlatSVGIcon("icons/intellij/inspectionsOK.svg"),
					new FlatSVGIcon("icons/intellij/cancel.svg"),
					new FlatSVGIcon("icons/intellij_modified/inspectionsOK_white.svg"),
					new FlatSVGIcon("icons/intellij_modified/cancel_white.svg")));

		// Imposta larghezza colonne
		this.getColumnModel().getColumn(COL_AMOUNT).setMinWidth(40);
		this.getColumnModel().getColumn(COL_AMOUNT).setMaxWidth(40);
		this.getColumnModel().getColumn(COL_COMPLETION).setMinWidth(60);
		this.getColumnModel().getColumn(COL_COMPLETION).setMaxWidth(60);
	}

	@Override
	public OrderCompletionTableModel getModel() {
		return (OrderCompletionTableModel) super.getModel();
	}
}
