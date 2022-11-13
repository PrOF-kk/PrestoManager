package it.colella.prestomanager.view;

import static javax.swing.SpringLayout.EAST;
import static javax.swing.SpringLayout.HORIZONTAL_CENTER;
import static javax.swing.SpringLayout.NORTH;
import static javax.swing.SpringLayout.SOUTH;
import static javax.swing.SpringLayout.VERTICAL_CENTER;
import static javax.swing.SpringLayout.WEST;

import java.awt.GridLayout;
import java.util.Map;

import javax.swing.Action;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;

import com.formdev.flatlaf.extras.FlatSVGIcon;

import it.colella.prestomanager.model.Order;
import it.colella.prestomanager.model.OrderManager;
import it.colella.prestomanager.model.ReceiptWriter;
import it.colella.prestomanager.util.PriceFormatter;
import it.colella.prestomanager.util.SimpleAction;
import it.colella.prestomanager.view.component.JOrderFullTable;
import it.colella.prestomanager.view.component.JShortcutInfoPane;

/**
 * Pagina per pagare il conto di un tavolo e stamparne lo scontrino
 */
public class CassaPage extends Page {

	private static final PriceFormatter formatter = new PriceFormatter();

	private Order currentOrder;

	private Map<Integer, Order> payableOrders;
	private Integer[] payableTables;


	private JPanel pnlLeft;
	private JOrderFullTable tblOrder;

	private JPanel pnlRight;
	private JLabel lblTable;
	private JComboBox<Integer> cmbTable;
	private JLabel lblTotal;
	private JButton btnPay;
	private JButton btnBack;

	private Action actionPay;
	private Action actionBack;

	/**
	 * Crea una {@link CassaPage}
	 *
	 * @param frame il JFrame contenitore
	 */
	public CassaPage(JFrame frame) {
		super(frame);
		this.initActions();

		setLayout(new GridLayout(1, 2, 0, 0));

		payableOrders = OrderManager.getInstance().getPayableOrders();
		payableTables = OrderManager.getInstance().getPayableTables();

		if (payableTables.length == 0) {
			this.currentOrder = new Order();
		}
		else {
			this.currentOrder = payableOrders.get(payableTables[0]);
		}

		SpringLayout leftLayout = new SpringLayout();
		pnlLeft = new JPanel(leftLayout);
		tblOrder = new JOrderFullTable(this.currentOrder);
		JScrollPane scrlTablePane = new JScrollPane(tblOrder);

		leftLayout.putConstraint(NORTH, scrlTablePane, GENERIC_SPACING, NORTH, pnlLeft);
		leftLayout.putConstraint(SOUTH, scrlTablePane, -GENERIC_SPACING, SOUTH, pnlLeft);
		leftLayout.putConstraint(EAST, scrlTablePane, -GENERIC_SPACING, EAST, pnlLeft);
		leftLayout.putConstraint(WEST, scrlTablePane, GENERIC_SPACING, WEST, pnlLeft);

		pnlLeft.add(scrlTablePane);


		SpringLayout rightLayout = new SpringLayout();
		pnlRight = new JPanel(rightLayout);
		lblTable = new JLabel("Tavolo");
		cmbTable = new JComboBox<>(payableTables);
		lblTotal = new JLabel();
		btnPay = new JButton(actionPay);
		btnBack = new JButton(actionBack);

		lblTable.putClientProperty("FlatLaf.styleClass", "h1");
		rightLayout.putConstraint(VERTICAL_CENTER, lblTable, 0, VERTICAL_CENTER, cmbTable);
		rightLayout.putConstraint(EAST, lblTable, -BUTTON_SPACING, WEST, cmbTable);

		cmbTable.addActionListener(e -> this.onTableChange());
		cmbTable.putClientProperty("FlatLaf.styleClass", "h1");
		rightLayout.putConstraint(NORTH, cmbTable, GENERIC_SPACING, NORTH, pnlRight);
		rightLayout.putConstraint(EAST, cmbTable, -GENERIC_SPACING, EAST, pnlRight);

		// Init label del totale
		this.calculateTotal();
		lblTotal.setHorizontalAlignment(SwingConstants.RIGHT);
		lblTotal.putClientProperty("FlatLaf.styleClass", "h00");
		rightLayout.putConstraint(SOUTH, lblTotal, -GENERIC_SPACING, NORTH, btnBack);
		rightLayout.putConstraint(EAST, lblTotal, 0, EAST, btnBack);
		rightLayout.putConstraint(WEST, lblTotal, 0, WEST, btnPay);

		btnPay.setText("Paga");
		btnPay.setIcon(new FlatSVGIcon("icons/intellij/inspectionsOK.svg"));
		btnPay.putClientProperty("FlatLaf.styleClass", "large");
		actionPay.setEnabled(payableTables.length > 0);
		rightLayout.putConstraint(NORTH, btnPay, 0, NORTH, btnBack);
		rightLayout.putConstraint(SOUTH, btnPay, 0, SOUTH, btnBack);
		rightLayout.putConstraint(EAST, btnPay, -BUTTON_SPACING/2, HORIZONTAL_CENTER, pnlRight);
		rightLayout.putConstraint(WEST, btnPay, GENERIC_SPACING, WEST, pnlRight);

		btnBack.setText("Indietro");
		btnBack.setIcon(new FlatSVGIcon("icons/intellij/back.svg"));
		btnBack.putClientProperty("FlatLaf.styleClass", "large");
		rightLayout.putConstraint(SOUTH, btnBack, -GENERIC_SPACING, SOUTH, pnlRight);
		rightLayout.putConstraint(EAST, btnBack, -GENERIC_SPACING, EAST, pnlRight);
		rightLayout.putConstraint(WEST, btnBack, BUTTON_SPACING/2, HORIZONTAL_CENTER, pnlRight);
		rightLayout.putConstraint(SpringLayout.HEIGHT, btnBack, 0, SpringLayout.HEIGHT, lblTotal);

		pnlRight.add(lblTable);
		pnlRight.add(cmbTable);
		pnlRight.add(lblTotal);
		pnlRight.add(btnPay);
		pnlRight.add(btnBack);


		add(pnlLeft);
		add(pnlRight);
	}

	private void initActions() {
		InputMap glblInputMap = this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);

		actionPay = new SimpleAction(this::onPay);
		this.getActionMap().put("Pay", actionPay);
		glblInputMap.put(KeyStroke.getKeyStroke("ENTER"), "Pay");

		actionBack = new SimpleAction(() -> this.switchPage(new MenuPage(this.getFrame())));
		this.getActionMap().put("Back", actionBack);
		glblInputMap.put(KeyStroke.getKeyStroke("ESCAPE"), "Back");

		// Solo da tastiera

		Action actionPrevTable = new SimpleAction(this::prevTable);
		this.getActionMap().put("Prev", actionPrevTable);
		glblInputMap.put(KeyStroke.getKeyStroke("LEFT"), "Prev");

		Action actionNextTable = new SimpleAction(this::nextTable);
		this.getActionMap().put("Next", actionNextTable);
		glblInputMap.put(KeyStroke.getKeyStroke("RIGHT"), "Next");

		Action actionShortcutInfo = new SimpleAction(() ->
			JShortcutInfoPane.showShortcutInfoMessage(this,
					"←", "Tavolo precedente",
					"→", "Tavolo successivo",
					"Enter", "Evadi",
					"Esc", "Indietro")
		);
		this.getActionMap().put("Shortcuts", actionShortcutInfo);
		glblInputMap.put(KeyStroke.getKeyStroke("F1"), "Shortcuts");
	}

	private void nextTable() {
		if (cmbTable.getItemCount() < 2) {
			return;
		}
		if (cmbTable.getSelectedIndex() + 1 < cmbTable.getItemCount()) {
			cmbTable.setSelectedIndex(cmbTable.getSelectedIndex() + 1);
		}
		else {
			cmbTable.setSelectedIndex(0);
		}
	}

	private void prevTable() {
		if (cmbTable.getItemCount() < 2) {
			return;
		}
		if (cmbTable.getSelectedIndex() - 1 > 0) {
			cmbTable.setSelectedIndex(cmbTable.getSelectedIndex() - 1);
		}
		else {
			cmbTable.setSelectedIndex(cmbTable.getItemCount() - 1);
		}
	}

	private void onTableChange() {
		this.currentOrder = payableOrders.get(cmbTable.getSelectedItem());
		tblOrder.getModel().setOrder(this.currentOrder);

		this.calculateTotal();
	}

	private void onPay() {

		OrderManager.getInstance().payTable((int) cmbTable.getSelectedItem());

		JOptionPane.showMessageDialog(this,
				"Pagato conto del tavolo " + cmbTable.getSelectedItem(),
				null,
				JOptionPane.INFORMATION_MESSAGE);

		ReceiptWriter.write(this.currentOrder);

		// Resetta la pagina
		this.switchPage(new CassaPage(this.getFrame()));
	}

	/**
	 * Calcola il totale e lo stampa in lblTotal
	 */
	private void calculateTotal() {
		lblTotal.setText("Totale: " + formatter.valueToString(currentOrder.calculateTotal()));
	}
}
