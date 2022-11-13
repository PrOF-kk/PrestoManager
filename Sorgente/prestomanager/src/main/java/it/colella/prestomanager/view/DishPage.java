package it.colella.prestomanager.view;

import static javax.swing.SpringLayout.EAST;
import static javax.swing.SpringLayout.HORIZONTAL_CENTER;
import static javax.swing.SpringLayout.NORTH;
import static javax.swing.SpringLayout.SOUTH;
import static javax.swing.SpringLayout.WEST;

import java.awt.GridLayout;
import java.awt.KeyboardFocusManager;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.formdev.flatlaf.extras.FlatSVGIcon;

import it.colella.prestomanager.model.Allergen;
import it.colella.prestomanager.model.Dish;
import it.colella.prestomanager.model.DishManager;
import it.colella.prestomanager.model.DishType;
import it.colella.prestomanager.util.DocumentSizeFilter;
import it.colella.prestomanager.util.SimpleAction;
import it.colella.prestomanager.view.component.JPriceSpinner;
import it.colella.prestomanager.view.component.JShortcutInfoPane;

/**
 * Pagina per la creazione o modifica di piatti del menù
 */
public class DishPage extends Page {

	private static final Logger log = LoggerFactory.getLogger(DishPage.class);

	private static final int NAME_MAX_LENGTH = 50;
	private static final int DESC_MAX_LENGTH = 1000;

	private static final int MODE_MODIFY = 0;
	private static final int MODE_NEW = 1;
	/**
	 * Determina se stiamo creando un piatto nuovo ({@link #MODE_NEW})
	 * o modificandone uno esistente ({@link #MODE_MODIFY})
	 */
	private final int mode;

	private Dish startingDish;
	private Map<JCheckBox, Allergen> checkboxMap;

	private JPanel pnlLeft;
	private JLabel lblName;
	private JTextField txtName;
	private JLabel lblPrice;
	private JPriceSpinner spnrPrice;
	private JLabel lblType;
	private JComboBox<DishType> cmbType;
	private JLabel lblDesc;
	private JTextArea txtDesc;
	private JLabel lblDescSizeLimit;

	private JPanel pnlRight;
	private JLabel lblAllergens;
	private JPanel pnlCheckBoxes;
	private JButton btnCancel;
	private JButton btnSave;

	private Action actionCancel;
	private Action actionSave;

	/**
	 * Crea una {@link DishPage} per la creazione di un nuovo piatto
	 *
	 * @param frame il JFrame contenitore
	 */
	public DishPage(JFrame frame) {
		this(frame, new Dish(), MODE_NEW);
	}

	/**
	 * Crea una {@link DishPage} per la modifica di un piatto esistente
	 *
	 * @param frame il JFrame contenitore
	 * @param dish  il piatto da modificare
	 */
	public DishPage(JFrame frame, Dish dish) {
		this(frame, dish, MODE_MODIFY);
	}

	private DishPage(JFrame frame, Dish dish, int mode) {
		super(frame);
		this.startingDish = dish;
		this.mode = mode;
		this.initActions();

		log.info("Init DishPage with {}", startingDish);

		setLayout(new GridLayout(0, 2, 0, 0));

		// Sinistra

		SpringLayout leftLayout = new SpringLayout();
		pnlLeft = new JPanel(leftLayout);

		lblName = new JLabel("Nome");
		txtName = new JTextField(startingDish.getName());

		lblPrice = new JLabel("Prezzo");
		spnrPrice = new JPriceSpinner();
		spnrPrice.setValue(startingDish.getPrice());

		lblType = new JLabel("Tipo");
		cmbType = new JComboBox<>(DishType.values());

		lblDesc = new JLabel("Descrizione");
		txtDesc = new JTextArea(startingDish.getDescription());
		lblDescSizeLimit = new JLabel();

		lblName.putClientProperty("FlatLaf.styleClass", "large");
		leftLayout.putConstraint(NORTH, lblName, GENERIC_SPACING, NORTH, pnlLeft);
		leftLayout.putConstraint(WEST, lblName, GENERIC_SPACING, WEST, pnlLeft);

		// Consenti di salvare solo quando il nome è sia non vuoto sia non solo whitespace
		txtName.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void removeUpdate(DocumentEvent e) {
				actionSave.setEnabled(!txtName.getText().isBlank());
			}
			@Override
			public void insertUpdate(DocumentEvent e) {
				actionSave.setEnabled(!txtName.getText().isBlank());
			}
			@Override
			public void changedUpdate(DocumentEvent e) { /* Non ci interessa */ }
		});
		((AbstractDocument) txtName.getDocument()).setDocumentFilter(new DocumentSizeFilter(NAME_MAX_LENGTH));
		leftLayout.putConstraint(NORTH, txtName, BUTTON_SPACING, SOUTH, lblName);
		leftLayout.putConstraint(EAST, txtName, -GENERIC_SPACING, EAST, pnlLeft);
		leftLayout.putConstraint(WEST, txtName, 0, WEST, lblName);

		lblPrice.putClientProperty("FlatLaf.styleClass", "large");
		leftLayout.putConstraint(NORTH, lblPrice, GENERIC_SPACING, SOUTH, txtName);
		leftLayout.putConstraint(WEST, lblPrice, 0, WEST, lblName);

		leftLayout.putConstraint(NORTH, spnrPrice, BUTTON_SPACING, SOUTH, lblPrice);
		leftLayout.putConstraint(EAST, spnrPrice, -BUTTON_SPACING/2, HORIZONTAL_CENTER, txtName);
		leftLayout.putConstraint(WEST, spnrPrice, 0, WEST, lblName);

		lblType.putClientProperty("FlatLaf.styleClass", "large");
		leftLayout.putConstraint(NORTH, lblType, 0, NORTH, lblPrice);
		leftLayout.putConstraint(WEST, lblType, BUTTON_SPACING/2, HORIZONTAL_CENTER, txtName);

		cmbType.setSelectedItem(startingDish.getType());
		leftLayout.putConstraint(NORTH, cmbType, 0, NORTH, spnrPrice);
		leftLayout.putConstraint(SOUTH, cmbType, 0, SOUTH, spnrPrice);
		leftLayout.putConstraint(EAST, cmbType, 0, EAST, txtName);
		leftLayout.putConstraint(WEST, cmbType, 0, WEST, lblType);

		lblDesc.putClientProperty("FlatLaf.styleClass", "large");
		leftLayout.putConstraint(NORTH, lblDesc, GENERIC_SPACING, SOUTH, spnrPrice);
		leftLayout.putConstraint(WEST, lblDesc, 0, WEST, lblName);

		txtDesc.setLineWrap(true);
		txtDesc.setWrapStyleWord(true);
		// Disabilita l'inserimento di 'Tab' e lo usa invece per trasferire il focus
		txtDesc.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, null);
		txtDesc.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, null);
		// Limita lunghezza massima descrizione
		((AbstractDocument) txtDesc.getDocument()).setDocumentFilter(new DocumentSizeFilter(DESC_MAX_LENGTH));
		txtDesc.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void removeUpdate(DocumentEvent e) {
				updateDescLen();
			}
			@Override
			public void insertUpdate(DocumentEvent e) {
				updateDescLen();
			}
			@Override
			public void changedUpdate(DocumentEvent e) {
				updateDescLen();
			}
		});
		leftLayout.putConstraint(NORTH, txtDesc, BUTTON_SPACING, SOUTH, lblDesc);
		leftLayout.putConstraint(SOUTH, txtDesc, -BUTTON_SPACING, NORTH, lblDescSizeLimit);
		leftLayout.putConstraint(EAST, txtDesc, 0, EAST, txtName);
		leftLayout.putConstraint(WEST, txtDesc, 0, WEST, txtName);

		lblDescSizeLimit.putClientProperty("FlatLaf.styleClass", "large");
		lblDescSizeLimit.setHorizontalAlignment(SwingConstants.RIGHT);
		this.updateDescLen();
		leftLayout.putConstraint(SOUTH, lblDescSizeLimit, -GENERIC_SPACING, SOUTH, pnlLeft);
		leftLayout.putConstraint(EAST, lblDescSizeLimit, 0, EAST, txtName);
		leftLayout.putConstraint(WEST, lblDescSizeLimit, 0, WEST, txtName);

		pnlLeft.add(lblName);
		pnlLeft.add(txtName);
		pnlLeft.add(lblPrice);
		pnlLeft.add(spnrPrice);
		pnlLeft.add(lblType);
		pnlLeft.add(cmbType);
		pnlLeft.add(lblDesc);
		pnlLeft.add(txtDesc);
		pnlLeft.add(lblDescSizeLimit);

		// Destra

		SpringLayout rightLayout = new SpringLayout();
		pnlRight = new JPanel(rightLayout);

		lblAllergens = new JLabel("Allergeni");
		pnlCheckBoxes = new JPanel();
		btnCancel = new JButton(actionCancel);
		btnSave = new JButton(actionSave);

		lblAllergens.putClientProperty("FlatLaf.styleClass", "large");
		rightLayout.putConstraint(NORTH, lblAllergens, GENERIC_SPACING, NORTH, pnlRight);
		rightLayout.putConstraint(WEST, lblAllergens, GENERIC_SPACING, WEST, pnlLeft);

		pnlCheckBoxes.setLayout(new BoxLayout(pnlCheckBoxes, BoxLayout.Y_AXIS));
		this.checkboxMap = new HashMap<>(Allergen.values().length);
		for (Allergen a : Allergen.values()) {
			JCheckBox cboxAllergen = new JCheckBox(a.toString(), startingDish.getAllergens().contains(a));
			this.checkboxMap.put(cboxAllergen, a);
			pnlCheckBoxes.add(cboxAllergen);
		}
		rightLayout.putConstraint(NORTH, pnlCheckBoxes, GENERIC_SPACING, SOUTH, lblAllergens);
		rightLayout.putConstraint(SOUTH, pnlCheckBoxes, -GENERIC_SPACING, NORTH, btnSave);
		rightLayout.putConstraint(EAST, pnlCheckBoxes, -GENERIC_SPACING, EAST, pnlRight);
		rightLayout.putConstraint(WEST, pnlCheckBoxes, 0, WEST, lblAllergens);

		btnCancel.setText("Annulla");
		btnCancel.setIcon(new FlatSVGIcon("icons/intellij/undo.svg"));
		rightLayout.putConstraint(SOUTH, btnCancel, 0, SOUTH, btnSave);
		rightLayout.putConstraint(EAST, btnCancel, -BUTTON_SPACING, WEST, btnSave);

		btnSave.setText("Salva");
		btnSave.setIcon(new FlatSVGIcon("icons/intellij/Save(Color).svg"));
		actionSave.setEnabled(!txtName.getText().isBlank());
		rightLayout.putConstraint(SOUTH, btnSave, -GENERIC_SPACING, SOUTH, pnlRight);
		rightLayout.putConstraint(EAST, btnSave, -GENERIC_SPACING, EAST, pnlRight);

		pnlRight.add(lblAllergens);
		pnlRight.add(pnlCheckBoxes);
		pnlRight.add(btnCancel);
		pnlRight.add(btnSave);

		this.add(pnlLeft);
		this.add(pnlRight);

		SwingUtilities.invokeLater(() -> txtName.requestFocusInWindow());
	}

	private void initActions() {
		InputMap glblInputMap = this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);

		actionSave = new SimpleAction(this::onSave);
		this.getActionMap().put("Save", actionSave);
		glblInputMap.put(KeyStroke.getKeyStroke("control S"), "Save");

		actionCancel = new SimpleAction(this::onCancel);
		this.getActionMap().put("Back", actionCancel);
		glblInputMap.put(KeyStroke.getKeyStroke("ESCAPE"), "Back");

		Action actionShortcutInfo = new SimpleAction(() ->
				JShortcutInfoPane.showShortcutInfoMessage(this,
						"Ctrl+S", "Salva",
						"Esc", "Indietro")
		);
		this.getActionMap().put("Shortcuts", actionShortcutInfo);
		glblInputMap.put(KeyStroke.getKeyStroke("F1"), "Shortcuts");
	}

	private void onCancel() {
		Dish newDish = this.buildDishFromInput();

		// Chiedi conferma se il piatto è stato modificato
		if (!startingDish.deepEquals(newDish)) {

			int result = JOptionPane.showConfirmDialog(this,
					"Tornando alla schermata precedente le modifiche correnti verranno ignorate."
					+ "\nContinuare?",
					null,
					JOptionPane.YES_NO_OPTION,
					JOptionPane.WARNING_MESSAGE);

			if (result != JOptionPane.YES_OPTION) {
				return;
			}

		}
		this.switchPage(new ChefPage(this.getFrame()));
	}

	private void onSave() {

		Dish newDish = this.buildDishFromInput();

		if (this.mode == MODE_NEW) {
			// Stiamo aggiungendo un piatto nuovo
			if (DishManager.getInstance().getDishSet().contains(newDish)) {
				JOptionPane.showMessageDialog(this, "Esiste già un piatto con quel nome", null, JOptionPane.ERROR_MESSAGE);
			}
			else {
				DishManager.getInstance().add(newDish);
				this.switchPage(new ChefPage(this.getFrame()));
			}
		}
		else {
			// Stiamo modificando un piatto esistente

			// Rimuovi e riaggiungi il piatto
			// (potrebbe essere stato cambiato qualcosa che non è il nome)
			DishManager.getInstance().remove(startingDish);
			DishManager.getInstance().add(newDish);
			this.switchPage(new ChefPage(this.getFrame()));
		}
	}

	/**
	 * Aggiorna lblDescSizeLimit con (CARATTERI USATI)/(CARATTERI DISPONIBILI)
	 */
	private void updateDescLen() {
		lblDescSizeLimit.setText("%04d/%04d".formatted(txtDesc.getDocument().getLength(), DESC_MAX_LENGTH));
	}

	/**
	 * Crea un {@link Dish} dai valori inseriti dall'utente.
	 * <p>N.B. non è verificata la validità dei dati di input
	 */
	private Dish buildDishFromInput() {
		return new Dish(
				txtName.getText().strip(),

				(Double) spnrPrice.getValue(),

				txtDesc.getText().strip(),

				(DishType) cmbType.getSelectedItem(),

				this.checkboxMap.keySet().stream()
						.filter(JCheckBox::isSelected)
						.map(cb -> checkboxMap.get(cb))
						// Crea un EnumSet ottimizzato anziché un Set generico
						.collect(Collectors.toCollection(() -> EnumSet.noneOf(Allergen.class)))
		);
	}
}
