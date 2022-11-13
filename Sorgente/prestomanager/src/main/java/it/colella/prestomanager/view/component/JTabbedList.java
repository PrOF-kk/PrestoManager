package it.colella.prestomanager.view.component;

import java.util.Collection;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import it.colella.prestomanager.util.GenericDefaultListCellRenderer;

/**
 * {@link JTabbedPane} con una JList<E> per ogni tab e massimo un elemento
 * selezionato per tab
 *
 * @param <E> tipo di oggetto nelle JList
 */
public class JTabbedList<E> extends JTabbedPane {

	private static final int FIXED_CELL_HEIGHT = 26;

	public JTabbedList() {
		super(SwingConstants.TOP);

		this.addChangeListener(e -> onTabChange());
	}

	public void addTab(String title, Collection<E> values) {
		this.addTab(title, values, new GenericDefaultListCellRenderer<>());
	}
	public void addTab(String title, Collection<E> values, ListCellRenderer<E> renderer) {

		DefaultListModel<E> model = new DefaultListModel<>();
		model.addAll(values);

		JList<E> list = new JList<>(model);
		list.setCellRenderer(renderer);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setFixedCellHeight(FIXED_CELL_HEIGHT);

		list.addListSelectionListener(e -> onSelectionChange());
		list.setSelectedIndex(0);
		this.addTab(title, new JScrollPane(list));
	}

	public void addListSelectionListener(ListSelectionListener l) {
		listenerList.add(ListSelectionListener.class, l);
	}

	protected void fireSelectionValueChanged() {
		ListSelectionEvent event = null;
		Object[] listeners = listenerList.getListenerList();
		// listeners è un array contenente "coppie" Classe - EventListener

	     for (int i = listeners.length-2; i >= 0; i -= 2) {
	         if (listeners[i] == ListSelectionListener.class) {
	             // Lazily create the event:
	             if (event == null) {
	                 event = new ListSelectionEvent(this, 0, 0, false);
	             }
	             ((ListSelectionListener) listeners[i+1]).valueChanged(event);
	         }
	     }
	}

	private void onTabChange() {
		this.fireSelectionValueChanged();
	}

	private void onSelectionChange() {
		this.fireSelectionValueChanged();
	}

	@SuppressWarnings("unchecked")
	@Override
	public JList<E> getSelectedComponent() {
		// Garantito uno JScrollPane con una JList<E> interna
		if (super.getSelectedComponent() == null) {
			return null;
		}
		return (JList<E>) ((JScrollPane) super.getSelectedComponent()).getViewport().getComponent(0);
	}

	/**
	 * @see JList#getSelectedValue()
	 */
	public E getSelectedValue() {
		return (this.getSelectedComponent() == null)
				? null
				: this.getSelectedComponent().getSelectedValue();
	}

}
