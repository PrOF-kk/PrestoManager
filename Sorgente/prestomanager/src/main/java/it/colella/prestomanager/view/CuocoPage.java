package it.colella.prestomanager.view;

import static javax.swing.SpringLayout.EAST;
import static javax.swing.SpringLayout.HORIZONTAL_CENTER;
import static javax.swing.SpringLayout.NORTH;
import static javax.swing.SpringLayout.SOUTH;
import static javax.swing.SpringLayout.WEST;

import java.awt.GridLayout;
import java.awt.event.MouseEvent;

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
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;
import javax.swing.SpringLayout;
import javax.swing.event.MouseInputAdapter;

import com.formdev.flatlaf.extras.FlatSVGIcon;

import it.colella.prestomanager.model.Dish;
import it.colella.prestomanager.model.Order;
import it.colella.prestomanager.model.OrderManager;
import it.colella.prestomanager.util.SimpleAction;
import it.colella.prestomanager.view.component.JOrderCompletionTable;
import it.colella.prestomanager.view.component.JShortcutInfoPane;
import it.colella.prestomanager.view.component.OrderCompletionTableModel;

/**
 * Pagina per l'evasione di ordini da parte del cuoco
 */
public class CuocoPage extends Page {

	private Order currentOrder;

	private JPanel pnlLeft;
	private OrderCompletionTableModel tblModel;
	private JOrderCompletionTable tblOrder;

	private JPanel pnlRight;
	private JLabel lblTable;
	private JComboBox<Integer> cmbTable;
	private JLabel lblDishInfo;
	private JLabel lblOrdersInQueue;
	private JToggleButton btnEvadi;
	private JButton btnBack;

	private Action actionEvadi;
	private Action actionBack;

	/**
	 * Crea una {@link CuocoPage}
	 *
	 * @param frame il JFrame contenitore
	 */
	public CuocoPage(JFrame frame) {
		super(frame);
		this.initActions();

		this.currentOrder = (!OrderManager.getInstance().getCuocoOrders().isEmpty())
				? OrderManager.getInstance().getCuocoOrders().get(0)
				: new Order();

		setLayout(new GridLayout(1, 2, 0, 0));

		// Sinistra

		SpringLayout leftLayout = new SpringLayout();
		pnlLeft = new JPanel(leftLayout);

		tblOrder = new JOrderCompletionTable(this.currentOrder);
		tblModel = tblOrder.getModel();
		JScrollPane scrlTablePane = new JScrollPane(tblOrder);

		// Evadi piatto su doppio click
		tblOrder.addMouseListener(new MouseInputAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getClickCount() > 1 && tblOrder.getSelectedRow() != -1) {
					CuocoPage.this.onEvadi();
				}
			}
		});

		tblOrder.getSelectionModel().addListSelectionListener(e -> onSelectionChange());
		leftLayout.putConstraint(NORTH, scrlTablePane, GENERIC_SPACING, NORTH, pnlLeft);
		leftLayout.putConstraint(SOUTH, scrlTablePane, -GENERIC_SPACING, SOUTH, pnlLeft);
		leftLayout.putConstraint(EAST, scrlTablePane, 0, EAST, pnlLeft);
		leftLayout.putConstraint(WEST, scrlTablePane, GENERIC_SPACING, WEST, pnlLeft);

		pnlLeft.add(scrlTablePane);

		// Destra

		SpringLayout rightLayout = new SpringLayout();
		pnlRight = new JPanel(rightLayout);

		lblTable = new JLabel("Tavolo corrente");
		// Volutamente non in ordine di numero ma in ordine di inserzione
		// Inoltre, ci possono essere più ordini per uno stesso tavolo
		Integer[] cuocoOrders = OrderManager.getInstance().getCuocoOrders().stream()
				.map(Order::getTableNumber)
				.toArray(Integer[]::new);
		cmbTable = new JComboBox<>(cuocoOrders);
		lblDishInfo = new JLabel();
		lblOrdersInQueue = new JLabel();
		btnEvadi = new JToggleButton(actionEvadi);
		btnBack = new JButton(actionBack);

		lblTable.putClientProperty("FlatLaf.styleClass", "h1");
		rightLayout.putConstraint(NORTH, lblTable, GENERIC_SPACING, NORTH, pnlRight);
		rightLayout.putConstraint(WEST, lblTable, GENERIC_SPACING, WEST, pnlRight);

		cmbTable.addActionListener(e -> this.onTableChange());
		cmbTable.putClientProperty("FlatLaf.styleClass", "h1");
		rightLayout.putConstraint(NORTH, cmbTable, 0, NORTH, lblTable);
		rightLayout.putConstraint(WEST, cmbTable, BUTTON_SPACING, EAST, lblTable);

		lblDishInfo.putClientProperty("FlatLaf.styleClass", "h00");
		rightLayout.putConstraint(NORTH, lblDishInfo, GENERIC_SPACING, SOUTH, lblTable);
		rightLayout.putConstraint(EAST, lblDishInfo, -GENERIC_SPACING, EAST, pnlRight);
		rightLayout.putConstraint(WEST, lblDishInfo, 0, WEST, lblTable);

		lblOrdersInQueue.setText("Ordini in coda: " + OrderManager.getInstance().getCuocoOrders().size());
		lblOrdersInQueue.putClientProperty("FlatLaf.styleClass", "h1");
		rightLayout.putConstraint(SOUTH, lblOrdersInQueue, -GENERIC_SPACING, NORTH, btnBack);
		rightLayout.putConstraint(EAST, lblOrdersInQueue, 0, EAST, btnBack);
		rightLayout.putConstraint(WEST, lblOrdersInQueue, 0, WEST, btnEvadi);

		btnEvadi.setText("Evadi");
		btnEvadi.setIcon(new FlatSVGIcon("icons/intellij/inspectionsOK.svg"));
		btnEvadi.setSelectedIcon(new FlatSVGIcon("icons/intellij/cancel.svg"));
		btnEvadi.putClientProperty("FlatLaf.styleClass", "large");
		actionEvadi.setEnabled(!OrderManager.getInstance().getCuocoOrders().isEmpty());
		rightLayout.putConstraint(SOUTH, btnEvadi, 0, SOUTH, btnBack);
		rightLayout.putConstraint(WEST, btnEvadi, GENERIC_SPACING, WEST, pnlRight);
		rightLayout.putConstraint(EAST, btnEvadi, -BUTTON_SPACING/2, HORIZONTAL_CENTER, pnlRight);

		btnBack.setText("Indietro");
		btnBack.setIcon(new FlatSVGIcon("icons/intellij/back.svg"));
		btnBack.putClientProperty("FlatLaf.styleClass", "large");
		rightLayout.putConstraint(SOUTH, btnBack, -GENERIC_SPACING, SOUTH, pnlRight);
		rightLayout.putConstraint(EAST, btnBack, -GENERIC_SPACING, EAST, pnlRight);
		rightLayout.putConstraint(WEST, btnBack, BUTTON_SPACING/2, HORIZONTAL_CENTER, pnlRight);

		pnlRight.add(lblTable);
		pnlRight.add(cmbTable);
		pnlRight.add(lblDishInfo);
		pnlRight.add(lblOrdersInQueue);
		pnlRight.add(btnEvadi);
		pnlRight.add(btnBack);

		add(pnlLeft);
		add(pnlRight);

		this.initPostActions();
	}

	private void initActions() {
		InputMap glblInputMap = this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);

		actionEvadi = new SimpleAction(this::onEvadi);
		this.getActionMap().put("Evadi", actionEvadi);
		glblInputMap.put(KeyStroke.getKeyStroke("ENTER"), "Evadi");

		actionBack = new SimpleAction(this::onBack);
		this.getActionMap().put("Back", actionBack);
		glblInputMap.put(KeyStroke.getKeyStroke("ESCAPE"), "Back");

		Action actionShortcutInfo = new SimpleAction(() ->
				JShortcutInfoPane.showShortcutInfoMessage(this,
						"Enter", "Evadi",
						"Esc", "Indietro")
		);
		this.getActionMap().put("Shortcuts", actionShortcutInfo);
		glblInputMap.put(KeyStroke.getKeyStroke("F1"), "Shortcuts");
	}

	/**
	 * Inizializza scorciatoie aggiuntive, dopo aver creato le Action principali
	 * e i vari JComponent
	 */
	private void initPostActions() {
		InputMap tblInputMap = tblOrder.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

		// Sovrascrivi comportamento di default ("vai alla riga dopo")
		tblOrder.getActionMap().put("Evadi", actionEvadi);
		tblInputMap.put(KeyStroke.getKeyStroke("ENTER"), "Evadi");
	}

	private void onTableChange() {
		this.currentOrder = OrderManager.getInstance().getCuocoOrders().get(cmbTable.getSelectedIndex());
		tblModel.setOrder(this.currentOrder);

		actionEvadi.setEnabled(true);
	}

	private void onBack() {

		// Se almeno un piatto è stato evaso
		boolean anyEvaso = false;
		for (int i = 0; i < tblModel.getRowCount(); i++) {
			if (tblModel.isCompleted(i)) {
				anyEvaso = true;
				break;
			}
		}

		if (anyEvaso) {
			int result = JOptionPane.showConfirmDialog(this,
					"Tornando al menù principale l'ordine corrente sarà perso.\n"
					+ "Continuare?",
					null,
					JOptionPane.QUESTION_MESSAGE);

			if (result == JOptionPane.YES_OPTION) {
				this.switchPage(new MenuPage(this.getFrame()));
			}
		}
		else {
			this.switchPage(new MenuPage(this.getFrame()));
		}
	}

	private void onEvadi() {

		int selectedRow = tblOrder.getSelectedRow();
		tblModel.toggleCompleted(selectedRow);
		btnEvadi.setSelected(tblModel.isCompleted(selectedRow));

		boolean orderDone = true;
		for (int i = 0; i < tblModel.getRowCount(); i++) {
			if (!tblModel.isCompleted(i)) {
				orderDone = false;
				break;
			}
		}
		if (orderDone) {

			JOptionPane.showMessageDialog(this,
					"<html>Completato ordine per il tavolo <b>%d</b></html>".formatted(this.currentOrder.getTableNumber()),
					null,
					JOptionPane.INFORMATION_MESSAGE);

			OrderManager.getInstance().evadi(cmbTable.getSelectedIndex());
			this.switchPage(new CuocoPage(this.getFrame()));
		}
	}

	private void onSelectionChange() {
		int selectedRow = tblOrder.getSelectedRow();
		btnEvadi.setSelected(tblModel.isCompleted(selectedRow));

		Dish selectedDish = tblModel.getDishAt(selectedRow);
		lblDishInfo.setText(selectedDish.getName() + " ×" + this.currentOrder.getAmount(selectedDish));
	}

}

