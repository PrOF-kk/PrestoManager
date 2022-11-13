package it.colella.prestomanager.view.component;

import javax.swing.table.AbstractTableModel;

import it.colella.prestomanager.model.Dish;
import it.colella.prestomanager.model.Order;
import it.colella.prestomanager.util.PriceFormatter;

/**
 * TableModel per rappresentare tutti i piatti di un ordine
 */
public class OrderFullTableModel extends AbstractTableModel {

	private static final Class<?>[] columnClasses = { String.class, String.class, Integer.class };
	private static final String[] columnNames = { "Piatto", "Prezzo", "Q.tà" };
	public static final int COL_DISH = 0;
	public static final int COL_PRICE = 1;
	public static final int COL_AMOUNT = 2;

	private static final PriceFormatter formatter = new PriceFormatter();

	private Order currentOrder;

	/**
	 * Crea un {@link OrderFullTableModel} dall'ordine specificato
	 *
	 * @param order l'ordine sul quale creare il modello
	 */
	public OrderFullTableModel(Order order) {
		this.currentOrder = order;
	}

	@Override
	public int getRowCount() {
		return this.currentOrder.getMap().size();
	}

	@Override
	public int getColumnCount() {
		return columnClasses.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Dish selectedDish = currentOrder.get(rowIndex);
		return switch (columnIndex) {
			case COL_DISH   -> selectedDish.getName();
			case COL_PRICE  -> formatter.valueToString(selectedDish.getPrice());
			case COL_AMOUNT -> currentOrder.getAmount(selectedDish);
			default -> throw new IllegalArgumentException("Invalid column number: " + columnIndex);
		};
	}

	@Override
	public Class<?> getColumnClass(int c) {
		return columnClasses[c];
	}

	@Override
	public String getColumnName(int column) {
		return columnNames[column];
	}

	/**
	 * Imposta l'ordine raffigurato dal modello e ricostruisce la tabella
	 *
	 * @param newOrder il nuovo ordine da raffigurare
	 */
	public void setOrder(Order newOrder) {
		this.currentOrder = newOrder;
		this.fireTableDataChanged();
	}

	/**
	 * @see Order#add(Dish)
	 */
	public void addDish(Dish d) {
		if (this.currentOrder.getMap().containsKey(d)) {
			this.currentOrder.add(d);
			this.fireTableCellUpdated(this.currentOrder.indexOf(d), COL_AMOUNT);
		}
		else {
			this.currentOrder.add(d);
			this.fireTableRowsInserted(this.getRowCount(), this.getRowCount());
		}
	}

	/**
	 * @see Order#remove(Dish)
	 */
	public void removeDish(Dish d) {

		if (!this.currentOrder.getMap().containsKey(d)) {
			return;
		}

		int removedIndex = this.currentOrder.indexOf(d);

		if (this.currentOrder.getMap().get(d) == 1) {
			this.currentOrder.remove(d);
			this.fireTableRowsDeleted(removedIndex, removedIndex);
		}
		else {
			this.currentOrder.remove(d);
			this.fireTableCellUpdated(removedIndex, COL_AMOUNT);
		}
	}
}
