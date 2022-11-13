package it.colella.prestomanager.view;

import static javax.swing.SpringLayout.EAST;
import static javax.swing.SpringLayout.HORIZONTAL_CENTER;
import static javax.swing.SpringLayout.NORTH;
import static javax.swing.SpringLayout.SOUTH;
import static javax.swing.SpringLayout.VERTICAL_CENTER;
import static javax.swing.SpringLayout.WEST;

import java.awt.GridLayout;
import java.util.List;

import javax.swing.Action;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.KeyStroke;
import javax.swing.SpinnerNumberModel;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;

import com.formdev.flatlaf.extras.FlatSVGIcon;

import it.colella.prestomanager.model.Dish;
import it.colella.prestomanager.model.DishManager;
import it.colella.prestomanager.model.DishType;
import it.colella.prestomanager.model.Order;
import it.colella.prestomanager.model.OrderManager;
import it.colella.prestomanager.util.PriceFormatter;
import it.colella.prestomanager.util.SimpleAction;
import it.colella.prestomanager.view.component.DishListCellRenderer;
import it.colella.prestomanager.view.component.JAllergenInfoPane;
import it.colella.prestomanager.view.component.JMultilineLabel;
import it.colella.prestomanager.view.component.JOrderFullTable;
import it.colella.prestomanager.view.component.JShortcutInfoPane;
import it.colella.prestomanager.view.component.JTabbedList;

/**
 * Pagina per la creazione di ordini per un tavolo
 */
public class CamerierePage extends Page {

	private static final PriceFormatter formatter = new PriceFormatter();

	private Dish selectedDish;
	private Order currentOrder;

	private JPanel pnlLeft;
	private JTabbedList<Dish> lstDishes;

	private JPanel pnlCenter;
	private JOrderFullTable tblOrder;
	private JLabel lblSubtot;

	private JPanel pnlRight;
	private JLabel lblTable;
	private JSpinner spnrTable;
	private JLabel lblPrice;
	private JMultilineLabel txtDesc;
	private JButton btnAllergens;
	private JButton btnFinalize;
	private JButton btnAdd;
	private JButton btnRemove;
	private JButton btnBack;

	private Action actionShowAllergens;
	private Action actionFinalize;
	private Action actionAdd;
	private Action actionRemove;
	private Action actionBack;

	/**
	 * Crea una {@link CamerierePage}
	 *
	 * @param frame il JFrame contenitore
	 */
	public CamerierePage(JFrame frame) {
		super(frame);
		this.initActions();

		this.currentOrder = new Order();

		this.setLayout(new GridLayout(1, 3, 0, 0));

		// Sinistra

		SpringLayout leftLayout = new SpringLayout();
		pnlLeft = new JPanel(leftLayout);

		DishListCellRenderer renderer = new DishListCellRenderer();

		lstDishes = new JTabbedList<>();
		// Per ogni tipo di piatto
		for (DishType type : DishType.values()) {

			// Se non ci sono piatti di quel tipo, continue
			List<Dish> sortedDishesOfType = DishManager.getInstance().getDishSet().stream()
					// Conviene ordinare qui anziché riordinare la lista dopo
					.sorted()
					.filter(d -> d.getType() == type)
					.toList();

			if (sortedDishesOfType.isEmpty()) {
				continue;
			}

			// Aletrimenti aggiungi la pagina
			lstDishes.addTab(type.toString(), sortedDishesOfType, renderer);
		}
		// Aggiungi listener solo dopo aver aggiunto tutti i tab per evitare callback inutili
		lstDishes.addListSelectionListener(e -> onSelectionChange(lstDishes.getSelectedValue()));

		leftLayout.putConstraint(NORTH, lstDishes, GENERIC_SPACING, NORTH, pnlLeft);
		leftLayout.putConstraint(SOUTH, lstDishes, -GENERIC_SPACING, SOUTH, pnlLeft);
		leftLayout.putConstraint(EAST, lstDishes, -GENERIC_SPACING, EAST, pnlLeft);
		leftLayout.putConstraint(WEST, lstDishes, GENERIC_SPACING, WEST, pnlLeft);

		pnlLeft.add(lstDishes);

		// Centro

		SpringLayout centerLayout = new SpringLayout();
		pnlCenter = new JPanel(centerLayout);

		tblOrder = new JOrderFullTable(currentOrder);
		JScrollPane scrlTablePane = new JScrollPane(tblOrder);
		lblSubtot = new JLabel();

		tblOrder.getSelectionModel().addListSelectionListener(e -> onSelectionChange(tblOrder.getSelectedDish()));
		centerLayout.putConstraint(NORTH, scrlTablePane, GENERIC_SPACING, NORTH, pnlCenter);
		centerLayout.putConstraint(EAST, scrlTablePane, 0, EAST, pnlCenter);
		centerLayout.putConstraint(WEST, scrlTablePane, 0, WEST, pnlCenter);
		centerLayout.putConstraint(SOUTH, scrlTablePane, -GENERIC_SPACING, NORTH, lblSubtot);

		lblSubtot.setHorizontalAlignment(SwingConstants.RIGHT);
		this.calculateSubtotal();
		centerLayout.putConstraint(SOUTH, lblSubtot, -GENERIC_SPACING, SOUTH, pnlCenter);
		centerLayout.putConstraint(EAST, lblSubtot, 0, EAST, pnlCenter);

		pnlCenter.add(scrlTablePane);
		pnlCenter.add(lblSubtot);

		// Destra

		SpringLayout rightLayout = new SpringLayout();
		pnlRight = new JPanel(rightLayout);

		lblTable = new JLabel("Tavolo");
		spnrTable = new JSpinner(new SpinnerNumberModel(1, 1, 99, 1));
		lblPrice = new JLabel();
		txtDesc = new JMultilineLabel();
		btnAllergens = new JButton(actionShowAllergens);
		btnFinalize = new JButton(actionFinalize);
		btnAdd = new JButton(actionAdd);
		btnRemove = new JButton(actionRemove);
		btnBack = new JButton(actionBack);

		// Inizializza label
		this.onSelectionChange(lstDishes.getSelectedValue());

		lblTable.putClientProperty("FlatLaf.styleClass", "large");
		rightLayout.putConstraint(VERTICAL_CENTER, lblTable, 0, VERTICAL_CENTER, spnrTable);
		rightLayout.putConstraint(EAST, lblTable, -BUTTON_SPACING, WEST, spnrTable);

		spnrTable.putClientProperty("FlatLaf.styleClass", "large");
		rightLayout.putConstraint(NORTH, spnrTable, GENERIC_SPACING, NORTH, pnlRight);
		rightLayout.putConstraint(EAST, spnrTable, -GENERIC_SPACING, EAST, pnlRight);

		lblPrice.putClientProperty("FlatLaf.styleClass", "large");
		rightLayout.putConstraint(VERTICAL_CENTER, lblPrice, 0, VERTICAL_CENTER, spnrTable);
		rightLayout.putConstraint(WEST, lblPrice, GENERIC_SPACING, WEST, pnlRight);

		rightLayout.putConstraint(NORTH, txtDesc, GENERIC_SPACING, SOUTH, lblPrice);
		rightLayout.putConstraint(SOUTH, txtDesc, -GENERIC_SPACING, NORTH, btnAllergens);
		rightLayout.putConstraint(EAST, txtDesc, -GENERIC_SPACING, EAST, pnlRight);
		rightLayout.putConstraint(WEST, txtDesc, GENERIC_SPACING, WEST, pnlRight);

		btnAllergens.setText("Info Allergeni");
		btnAllergens.setIcon(new FlatSVGIcon("icons/intellij/warning.svg"));
		btnAllergens.putClientProperty("FlatLaf.styleClass", "large");
		rightLayout.putConstraint(SOUTH, btnAllergens, -GENERIC_SPACING, NORTH, btnRemove);
		rightLayout.putConstraint(EAST, btnAllergens, 0, EAST, btnRemove);
		rightLayout.putConstraint(WEST, btnAllergens, 0, WEST, btnFinalize);

		btnFinalize.setText("Finalizza ordine");
		btnFinalize.setIcon(new FlatSVGIcon("icons/intellij/inspectionsOK.svg"));
		btnFinalize.putClientProperty("FlatLaf.styleClass", "large");
		actionFinalize.setEnabled(false);
		rightLayout.putConstraint(NORTH, btnFinalize, 0, NORTH, btnRemove);
		rightLayout.putConstraint(SOUTH, btnFinalize, 0, SOUTH, btnBack);
		rightLayout.putConstraint(EAST, btnFinalize, -BUTTON_SPACING, WEST, btnBack);
		rightLayout.putConstraint(WEST, btnFinalize, GENERIC_SPACING, WEST, pnlRight);

		btnAdd.setIcon(new FlatSVGIcon("icons/intellij/add.svg"));
		rightLayout.putConstraint(NORTH, btnAdd, 0, NORTH, btnRemove);
		rightLayout.putConstraint(SOUTH, btnAdd, 0, SOUTH, btnRemove);
		rightLayout.putConstraint(EAST, btnAdd, -BUTTON_SPACING/2, HORIZONTAL_CENTER, btnBack);
		rightLayout.putConstraint(WEST, btnAdd, 0, WEST, btnBack);

		btnRemove.setIcon(new FlatSVGIcon("icons/intellij/remove.svg"));
		rightLayout.putConstraint(SOUTH, btnRemove, -BUTTON_SPACING, NORTH, btnBack);
		rightLayout.putConstraint(EAST, btnRemove, 0, EAST, btnBack);
		rightLayout.putConstraint(WEST, btnRemove, BUTTON_SPACING/2, HORIZONTAL_CENTER, btnBack);
		// Imposta btnRemove (e di conseguenza btnAdd) come quadrati
		rightLayout.putConstraint(SpringLayout.HEIGHT, btnRemove, 0, SpringLayout.WIDTH, btnRemove);

		btnBack.setText("Indietro");
		btnBack.setIcon(new FlatSVGIcon("icons/intellij/back.svg"));
		rightLayout.putConstraint(SOUTH, btnBack, -GENERIC_SPACING, SOUTH, pnlRight);
		rightLayout.putConstraint(EAST, btnBack, -GENERIC_SPACING, EAST, pnlRight);
		// Rendi della stessa altezza di btnAdd e btnRemove
		rightLayout.putConstraint(SpringLayout.HEIGHT, btnBack, 0, SpringLayout.HEIGHT, btnRemove);

		pnlRight.add(lblTable);
		pnlRight.add(spnrTable);
		pnlRight.add(lblPrice);
		pnlRight.add(txtDesc);
		pnlRight.add(btnAllergens);
		pnlRight.add(btnFinalize);
		pnlRight.add(btnAdd);
		pnlRight.add(btnRemove);
		pnlRight.add(btnBack);


		add(pnlLeft);
		add(pnlCenter);
		add(pnlRight);

		this.initPostActions();
	}

	private void initActions() {
		InputMap glblInputMap  = this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);

		this.actionShowAllergens = new SimpleAction(() -> JAllergenInfoPane.showAllergenInfo(this, selectedDish));

		this.actionFinalize = new SimpleAction(this::onFinalize);
		this.getActionMap().put("Finalize", actionFinalize);
		glblInputMap.put(KeyStroke.getKeyStroke("shift ENTER"), "Finalize");

		this.actionAdd = new SimpleAction(this::onAdd);
		this.getActionMap().put("Add", actionAdd);
		glblInputMap.put(KeyStroke.getKeyStroke('+'), "Add");
		glblInputMap.put(KeyStroke.getKeyStroke("ENTER"), "Add");

		this.actionRemove = new SimpleAction(this::onRemove);
		this.getActionMap().put("Remove", actionRemove);
		glblInputMap.put(KeyStroke.getKeyStroke('-'), "Remove");

		this.actionBack = new SimpleAction(this::onBack);
		this.getActionMap().put("Back", actionBack);
		glblInputMap.put(KeyStroke.getKeyStroke("ESCAPE"), "Back");

		Action actionShortcutInfo = new SimpleAction(() ->
				JShortcutInfoPane.showShortcutInfoMessage(this,
						"⇧Shift + Enter", "Finalizza ordine",
						"Enter", "Aggiungi",
						"+", "Aggiungi",
						"-", "Rimuovi",
						"Esc", "Indietro")
		);
		this.getActionMap().put("Shortcuts", actionShortcutInfo);
		glblInputMap.put(KeyStroke.getKeyStroke("F1"), "Shortcuts");
	}

	/**
	 * Inizializza action aggiuntive dopo aver creato tutte quelle principali
	 * e tutti i JComponent
	 */
	private void initPostActions() {
		InputMap tblInputMap = tblOrder.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

		// Sovrascrivi comportamento di default ("vai alla riga dopo") per ENTER
		tblOrder.getActionMap().put("Add", actionAdd);
		tblInputMap.put(KeyStroke.getKeyStroke("ENTER"), "Add");
	}

	/**
	 * Aggiorna la GUI quando cambia il piatto selezionato
	 *
	 * @param newSelection Il nuovo piatto selezionato (<b>non</b> this.selectedDish)
	 */
	private void onSelectionChange(Dish newSelection) {
		this.selectedDish = newSelection;
		if (newSelection == null) {
			actionAdd.setEnabled(false);
			actionRemove.setEnabled(false);
			actionShowAllergens.setEnabled(false);
			return;
		}
		lblPrice.setText(formatter.valueToString(newSelection.getPrice()));
		txtDesc.setText(newSelection.getDescription());
		actionAdd.setEnabled(true);
		actionRemove.setEnabled(true);
		actionShowAllergens.setEnabled(!this.selectedDish.getAllergens().isEmpty());
	}

	/**
	 * Finalizza l'ordine corrente e lo passa al cuoco
	 */
	private void onFinalize() {
		this.currentOrder.setTableNumber((int) spnrTable.getValue());
		if (!OrderManager.getInstance().getCuocoOrders().contains(currentOrder)) {

			int result = JOptionPane.showConfirmDialog(this,
					"<html>Finalizzare ordine per il tavolo <b>" + spnrTable.getValue() + "</b>?</html>",
					null,
					JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE);

			if (result == JOptionPane.YES_OPTION) {
				OrderManager.getInstance().finalizeOrder(currentOrder);
				this.switchPage(new CamerierePage(this.getFrame()));
			}
		}
	}

	private void onAdd() {
		tblOrder.getModel().addDish(selectedDish);
		actionFinalize.setEnabled(true);
		this.calculateSubtotal();
	}

	private void onRemove() {
		tblOrder.getModel().removeDish(selectedDish);
		actionFinalize.setEnabled(!this.currentOrder.getMap().isEmpty());
		this.calculateSubtotal();
	}

	private void onBack() {
		if (this.currentOrder.getMap().isEmpty()) {
			this.switchPage(new MenuPage(this.getFrame()));
		}
		else {
			int result = JOptionPane.showConfirmDialog(this,
					"Tornando al menù principale l'ordine corrente sarà perso.\n"
					+ "Continuare?",
					null,
					JOptionPane.YES_NO_OPTION,
					JOptionPane.WARNING_MESSAGE);

			if (result == JOptionPane.YES_OPTION) {
				this.switchPage(new MenuPage(this.getFrame()));
			}
		}
	}

	/**
	 * Ricalcola il subtotale e lo stampa in lblSubtot
	 */
	private void calculateSubtotal() {
		lblSubtot.setText("Subtotale: " + formatter.valueToString(this.currentOrder.calculateTotal()));
	}
}
