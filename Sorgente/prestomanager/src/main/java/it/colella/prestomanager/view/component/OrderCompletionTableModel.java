package it.colella.prestomanager.view.component;

import java.util.HashMap;
import java.util.Map;

import javax.swing.table.AbstractTableModel;

import it.colella.prestomanager.model.Dish;
import it.colella.prestomanager.model.Order;

/**
 * TableModel per rappresentare tutti i piatti di un ordine con il
 * loro stato di completezza
 */
public class OrderCompletionTableModel extends AbstractTableModel {

	private static final Class<?>[] columnClasses = { String.class, Integer.class, Boolean.class };
	private static final String[] columnNames = { "Piatto", "Q.tà", "Evaso" };
	public static final int COL_DISH = 0;
	public static final int COL_AMOUNT = 1;
	public static final int COL_COMPLETION = 2;

	private Order currentOrder;
	private Map<Dish, Boolean> completionMap;

	/**
	 * Crea un {@link OrderCompletionTableModel} dall'ordine specificato
	 *
	 * @param order l'ordine sul quale creare il modello
	 */
	public OrderCompletionTableModel(Order order) {
		this.currentOrder = order;

		this.initMap();
	}

	/**
	 * Inizializza completionMap { piatti : false }
	 */
	private void initMap() {
		this.completionMap = new HashMap<>(this.currentOrder.getMap().size());
		this.currentOrder.getMap().keySet().forEach(d -> this.completionMap.put(d, false));
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
		Dish dishAtRow = currentOrder.get(rowIndex);
		return switch (columnIndex) {
			case COL_DISH       -> dishAtRow.getName();
			case COL_AMOUNT     -> this.currentOrder.getAmount(dishAtRow);
			case COL_COMPLETION -> this.completionMap.get(dishAtRow);
			default -> throw new IllegalArgumentException("Invalid column number: " + columnIndex);
		};
	}

	/**
	 * Restituisce il piatto raffigurato in una riga
	 *
	 * @param rowIndex la riga del piatto
	 * @return il piatto
	 */
	public Dish getDishAt(int rowIndex) {
		return currentOrder.get(rowIndex);
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
	 * Segna il piatto come completato o meno per l'ordine corrente
	 *
	 * @param d     il piatto
	 * @param value {@code true} se evaso, {@code false} altrimenti
	 */
	public void setCompleted(Dish d, boolean value) {
		this.completionMap.put(d, value);
		int changedRow = this.currentOrder.indexOf(d);
		this.fireTableCellUpdated(changedRow, COL_COMPLETION);
	}

	/**
	 * Segna il piatto alla riga {@code row} come completato o meno per l'ordine
	 * corrente
	 *
	 * @param row   riga del piatto da segnare completato o meno
	 * @param value {@code true} se evaso, {@code false} altrimenti
	 */
	public void setCompleted(int row, boolean value) {
		this.completionMap.put(this.currentOrder.get(row), value);
		this.fireTableCellUpdated(row, COL_COMPLETION);
	}

	/**
	 * Restituisce se un piatto è stato evaso o meno
	 *
	 * @param d il piatto
	 * @return {@code true} se il piatto è stato evaso, {@code false} altrimenti
	 */
	public boolean isCompleted(Dish d) {
		return this.completionMap.get(d);
	}

	/**
	 * Restituisce se il piatto alla riga {@code row} stato evaso o meno
	 *
	 * @param row la riga del piatto
	 * @return {@code true} se il piatto è stato evaso, {@code false} altrimenti
	 */
	public boolean isCompleted(int row) {
		return this.completionMap.get(this.currentOrder.get(row));
	}

	/**
	 * Segna il piatto alla riga {@code row} come evaso se non lo era, o viceversa
	 *
	 * @param row la riga del piatto
	 */
	public void toggleCompleted(int row) {
		Dish dishForRow = this.currentOrder.get(row);
		this.completionMap.put(dishForRow, !this.completionMap.get(dishForRow));
		this.fireTableCellUpdated(row, COL_COMPLETION);
	}

	/**
	 * Imposta l'ordine raffigurato dal modello e ricostruisce la tabella. Le
	 * informazioni sulla completezza dei piatti sono scartate
	 *
	 * @param newOrder il nuovo ordine da raffigurare
	 */
	public void setOrder(Order newOrder) {
		this.currentOrder = newOrder;
		this.initMap();
		this.fireTableDataChanged();
	}
}

