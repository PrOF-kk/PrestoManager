package it.colella.prestomanager.view.component;

import static it.colella.prestomanager.view.component.OrderFullTableModel.COL_AMOUNT;
import static it.colella.prestomanager.view.component.OrderFullTableModel.COL_PRICE;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

import it.colella.prestomanager.model.Dish;
import it.colella.prestomanager.model.Order;

/**
 * JTable che tramite un {@link OrderFullTableModel} raffigura un ordine
 */
public class JOrderFullTable extends JTable {

	private Order currentOrder;

	/**
	 * Crea una tabella dall'ordine specificato
	 *
	 * @param order l'ordine iniziale raffigurato
	 */
	public JOrderFullTable(Order order) {
		super(new OrderFullTableModel(order));

		this.currentOrder = order;

		this.setShowGrid(true);
		this.setRowSelectionAllowed(true);
		this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		// Allinea il prezzo a destra
		DefaultTableCellRenderer priceRenderer = new DefaultTableCellRenderer();
		priceRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
		this.getColumnModel().getColumn(COL_PRICE).setCellRenderer(priceRenderer);

		// Imposta larghezza colonne
		this.getColumnModel().getColumn(COL_PRICE).setMinWidth(70);
		this.getColumnModel().getColumn(COL_PRICE).setMaxWidth(70);
		this.getColumnModel().getColumn(COL_AMOUNT).setMinWidth(40);
		this.getColumnModel().getColumn(COL_AMOUNT).setMaxWidth(40);
	}

	@Override
	public OrderFullTableModel getModel() {
		return (OrderFullTableModel) super.getModel();
	}

	/**
	 * Restituisce il piatto selezionato
	 *
	 * @return il piatto selezionato, o {@code null} se nessun piatto è selezionato
	 */
	public Dish getSelectedDish() {
		if (this.getSelectedRow() == -1) {
			return null;
		}
		return this.currentOrder.get(this.getSelectedRow());
	}

}
