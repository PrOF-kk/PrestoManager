package it.colella.prestomanager.view;

import static javax.swing.SpringLayout.EAST;
import static javax.swing.SpringLayout.HORIZONTAL_CENTER;
import static javax.swing.SpringLayout.NORTH;
import static javax.swing.SpringLayout.SOUTH;
import static javax.swing.SpringLayout.WEST;

import java.util.stream.Collectors;

import javax.swing.Action;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;

import com.formdev.flatlaf.extras.FlatSVGIcon;

import it.colella.prestomanager.model.Allergen;
import it.colella.prestomanager.model.Dish;
import it.colella.prestomanager.model.DishManager;
import it.colella.prestomanager.model.DishType;
import it.colella.prestomanager.util.PriceFormatter;
import it.colella.prestomanager.util.SimpleAction;
import it.colella.prestomanager.view.component.DishListCellRenderer;
import it.colella.prestomanager.view.component.JAllergenInfoPane;
import it.colella.prestomanager.view.component.JMultilineLabel;
import it.colella.prestomanager.view.component.JShortcutInfoPane;
import it.colella.prestomanager.view.component.JTabbedList;

/**
 * Pagina per la visualizzazione del menù del ristorante.
 * La modifica di piatti è effettuata in {@link DishPage}
 */
public class ChefPage extends Page {

	private static final PriceFormatter formatter = new PriceFormatter();

	private JTabbedList<Dish> lstDishes;

	private JLabel lblName;
	private JLabel lblPrice;
	private JMultilineLabel txtDescription;
	private JButton btnAllergens;
	private JButton btnEdit;
	private JButton btnNew;
	private JButton btnDelete;
	private JButton btnBack;

	private Action actionShowAllergens;
	private Action actionEdit;
	private Action actionNew;
	private Action actionDelete;
	private Action actionBack;


	/**
	 * Crea una {@link ChefPage}
	 *
	 * @param frame il JFrame contenitore
	 */
	public ChefPage(JFrame frame) {
		super(frame);
		this.initActions();

		SpringLayout layout = new SpringLayout();
		this.setLayout(layout);


		lstDishes = new JTabbedList<>();
		lblName = new JLabel();
		lblPrice = new JLabel();
		txtDescription = new JMultilineLabel();
		btnAllergens = new JButton(actionShowAllergens);
		btnEdit   = new JButton(actionEdit);
		btnNew    = new JButton(actionNew);
		btnDelete = new JButton(actionDelete);
		btnBack   = new JButton(actionBack);

		this.initTabbedList();

		lstDishes.addListSelectionListener(e -> onSelectionChange());
		layout.putConstraint(NORTH, lstDishes, GENERIC_SPACING, NORTH, this);
		layout.putConstraint(SOUTH, lstDishes, -GENERIC_SPACING, SOUTH, this);
		layout.putConstraint(EAST, lstDishes, 0, HORIZONTAL_CENTER, this);
		layout.putConstraint(WEST, lstDishes, GENERIC_SPACING, WEST, this);

		// Inizializza valori nei label
		this.onSelectionChange();

		lblName.putClientProperty("FlatLaf.styleClass", "h1");
		layout.putConstraint(NORTH, lblName, 0, NORTH, lstDishes);
		layout.putConstraint(WEST, lblName, GENERIC_SPACING, EAST, lstDishes);

		lblPrice.putClientProperty("FlatLaf.styleClass", "h3");
		layout.putConstraint(NORTH, lblPrice, GENERIC_SPACING, SOUTH, lblName);
		layout.putConstraint(WEST, lblPrice, 0, WEST, lblName);

		layout.putConstraint(NORTH, txtDescription, GENERIC_SPACING, SOUTH, lblPrice);
		layout.putConstraint(SOUTH, txtDescription, -GENERIC_SPACING, NORTH, btnAllergens);
		layout.putConstraint(EAST, txtDescription, -GENERIC_SPACING, EAST, this);
		layout.putConstraint(WEST, txtDescription, 0, WEST, lblName);

		btnAllergens.putClientProperty("JButton.buttonType", "borderless");
		btnAllergens.setHorizontalAlignment(SwingConstants.LEFT);
		layout.putConstraint(SOUTH, btnAllergens, -GENERIC_SPACING, NORTH, btnDelete);
		layout.putConstraint(EAST, btnAllergens, -GENERIC_SPACING, EAST, this);
		layout.putConstraint(WEST, btnAllergens, 0, WEST, lblName);

		btnEdit.setText("Modifica");
		btnEdit.setIcon(new FlatSVGIcon("icons/intellij/edit.svg"));
		btnEdit.putClientProperty("FlatLaf.styleClass", "large");
		layout.putConstraint(NORTH, btnEdit, 0, NORTH, btnDelete);
		layout.putConstraint(SOUTH, btnEdit, 0, SOUTH, btnBack);
		layout.putConstraint(EAST, btnEdit, -BUTTON_SPACING, WEST, btnBack);
		layout.putConstraint(WEST, btnEdit, GENERIC_SPACING, EAST, lstDishes);

		btnNew.setIcon(new FlatSVGIcon("icons/intellij/add.svg"));
		layout.putConstraint(NORTH, btnNew, 0, NORTH, btnDelete);
		layout.putConstraint(SOUTH, btnNew, 0, SOUTH, btnDelete);
		layout.putConstraint(EAST, btnNew, -BUTTON_SPACING/2, HORIZONTAL_CENTER, btnBack);
		layout.putConstraint(WEST, btnNew, 0, WEST, btnBack);

		btnDelete.setIcon(new FlatSVGIcon("icons/intellij/gc.svg"));
		layout.putConstraint(SOUTH, btnDelete, -BUTTON_SPACING, NORTH, btnBack);
		layout.putConstraint(EAST, btnDelete, 0, EAST, btnBack);
		layout.putConstraint(WEST, btnDelete, BUTTON_SPACING/2, HORIZONTAL_CENTER, btnBack);
		// Imposta btnDelete (e di conseguenza btnNew) come quadrati
		layout.putConstraint(SpringLayout.HEIGHT, btnDelete, 0, SpringLayout.WIDTH, btnDelete);

		btnBack.setText("Indietro");
		btnBack.setIcon(new FlatSVGIcon("icons/intellij/back.svg"));
		layout.putConstraint(SOUTH, btnBack, -GENERIC_SPACING, SOUTH, this);
		layout.putConstraint(EAST, btnBack, -GENERIC_SPACING, EAST, this);
		// Rendi della stessa altezza di btnNew e btnDelete
		layout.putConstraint(SpringLayout.HEIGHT, btnBack, 0, SpringLayout.HEIGHT, btnDelete);

		add(lstDishes);
		add(lblName);
		add(lblPrice);
		add(txtDescription);
		add(btnAllergens);
		add(btnEdit);
		add(btnNew);
		add(btnDelete);
		add(btnBack);
	}

	private void initActions() {
		InputMap glblInputMap = this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);

		this.actionShowAllergens = new SimpleAction(() -> JAllergenInfoPane.showAllergenInfo(this, lstDishes.getSelectedValue()));

		this.actionEdit = new SimpleAction(() -> this.switchPage(new DishPage(this.getFrame(), lstDishes.getSelectedValue())));
		this.getActionMap().put("Edit", actionEdit);
		glblInputMap.put(KeyStroke.getKeyStroke("ENTER"), "Edit");

		this.actionNew = new SimpleAction(() -> this.switchPage(new DishPage(this.getFrame())));
		this.getActionMap().put("New", actionNew);
		glblInputMap.put(KeyStroke.getKeyStroke("control N"), "New");

		this.actionDelete = new SimpleAction(this::onDelete);
		this.getActionMap().put("Delete", actionDelete);
		glblInputMap.put(KeyStroke.getKeyStroke("DELETE"), "Delete");

		this.actionBack = new SimpleAction(() -> this.switchPage(new MenuPage(this.getFrame())));
		this.getActionMap().put("Back", actionBack);
		glblInputMap.put(KeyStroke.getKeyStroke("ESCAPE"), "Back");

		Action actionShortcutInfo = new SimpleAction(() ->
			JShortcutInfoPane.showShortcutInfoMessage(this,
					"Enter", "Modifica",
					"Ctrl + N", "Crea nuovo",
					"Canc", "Elimina",
					"Esc", "Indietro")
		);
		this.getActionMap().put("Shortcuts", actionShortcutInfo);
		glblInputMap.put(KeyStroke.getKeyStroke("F1"), "Shortcuts");
	}

	private void onSelectionChange() {
		Dish selection = lstDishes.getSelectedValue();
		if (selection == null) {
			actionEdit.setEnabled(false);
			actionDelete.setEnabled(false);
		}
		else {
			lblName.setText(selection.getName());
			lblPrice.setText(formatter.valueToString(selection.getPrice()));
			txtDescription.setText(selection.getDescription());

			this.updateAllergenInfo(selection);

			actionEdit.setEnabled(true);
			actionDelete.setEnabled(true);
		}
	}

	private void onDelete() {
		Dish selectedDish = lstDishes.getSelectedValue();

		int result = JOptionPane.showConfirmDialog(
				this,
				"<html>Eliminare <b>definitivamente</b> " + selectedDish.getName() + "?</html>",
				null,
				JOptionPane.YES_NO_OPTION,
				JOptionPane.WARNING_MESSAGE);

		if (result == JOptionPane.YES_OPTION) {
			DishManager.getInstance().remove(selectedDish);
			this.initTabbedList();
		}
	}

	/**
	 * Inizializza o reinizializza lstDishes
	 */
	private void initTabbedList() {

		int selectedTab = (lstDishes.getSelectedIndex() != -1)
				? lstDishes.getSelectedIndex()
				: 0;

		lstDishes.removeAll();

		DishListCellRenderer renderer = new DishListCellRenderer();

		for (DishType type : DishType.values()) {
			lstDishes.addTab(
					type.toString(),
					DishManager.getInstance().getDishSet().stream()
						.sorted()
						.filter(d -> d.getType() == type)
						.toList(),
					renderer);
		}
		lstDishes.setSelectedIndex(selectedTab);
	}

	private void updateAllergenInfo(Dish d) {
		if (d.getAllergens().isEmpty()) {
			btnAllergens.setText(null);
			actionShowAllergens.setEnabled(false);
		}
		else {
			btnAllergens.setText("Può contenere: "
					+ d.getAllergens().stream()
						.map(Allergen::toString)
						.collect(Collectors.joining(", ")));
			actionShowAllergens.setEnabled(true);
		}
	}
}
