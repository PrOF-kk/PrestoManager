package it.colella.prestomanager.view;

import static javax.swing.SpringLayout.EAST;
import static javax.swing.SpringLayout.NORTH;
import static javax.swing.SpringLayout.SOUTH;
import static javax.swing.SpringLayout.VERTICAL_CENTER;
import static javax.swing.SpringLayout.WEST;

import java.awt.FlowLayout;

import javax.swing.Action;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.formdev.flatlaf.extras.FlatSVGIcon;

import it.colella.prestomanager.model.Setting;
import it.colella.prestomanager.model.SettingsManager;
import it.colella.prestomanager.util.PhoneNumberAdapter;
import it.colella.prestomanager.util.SimpleAction;
import it.colella.prestomanager.view.component.JShortcutInfoPane;

/**
 * Pagina principale. Consente inoltre di modificare le impostazioni e
 * leggere informazioni sul programma e sulle librerie usate
 */
public class MenuPage extends Page {

	private static final int MAIN_BUTTON_SIZE = 200;

	private JPanel pnlButtonBar;
	private JButton btnChef;
	private JButton btnCameriere;
	private JButton btnCuoco;
	private JButton btnCassa;

	private JButton btnSettings;
	private JButton btnInfo;

	private Action actionChef;
	private Action actionCameriere;
	private Action actionCuoco;
	private Action actionCassa;

	private Action actionSettings;
	private Action actionInfo;

	/**
	 * Crea una {@link MenuPage}
	 *
	 * @param frame il JFrame contenitore
	 */
	public MenuPage(JFrame frame) {
		super(frame);
		this.initActions();

		SpringLayout layout = new SpringLayout();
		setLayout(layout);

		pnlButtonBar = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));

		btnChef = new JButton(actionChef);
		btnCameriere = new JButton(actionCameriere);
		btnCuoco = new JButton(actionCuoco);
		btnCassa = new JButton(actionCassa);

		btnChef.setText("Chef");
		btnCameriere.setText("Cameriere");
		btnCuoco.setText("Cuoco");
		btnCassa.setText("Cassa");

		btnChef.setIcon(     new FlatSVGIcon("icons/miscellanea/light-bulb-idea.svg", MAIN_BUTTON_SIZE, MAIN_BUTTON_SIZE));
		btnCameriere.setIcon(new FlatSVGIcon("icons/miscellanea/notepad.svg", MAIN_BUTTON_SIZE, MAIN_BUTTON_SIZE));
		btnCuoco.setIcon(    new FlatSVGIcon("icons/miscellanea/chef.svg", MAIN_BUTTON_SIZE, MAIN_BUTTON_SIZE));
		btnCassa.setIcon(    new FlatSVGIcon("icons/miscellanea/cash-register.svg", MAIN_BUTTON_SIZE, MAIN_BUTTON_SIZE));

		btnChef.setHorizontalTextPosition(SwingConstants.CENTER);
		btnCameriere.setHorizontalTextPosition(SwingConstants.CENTER);
		btnCuoco.setHorizontalTextPosition(SwingConstants.CENTER);
		btnCassa.setHorizontalTextPosition(SwingConstants.CENTER);

		btnChef.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnCameriere.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnCuoco.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnCassa.setVerticalTextPosition(SwingConstants.BOTTOM);

		pnlButtonBar.add(btnChef);
		pnlButtonBar.add(btnCameriere);
		pnlButtonBar.add(btnCuoco);
		pnlButtonBar.add(btnCassa);

		layout.putConstraint(VERTICAL_CENTER, pnlButtonBar, 0, VERTICAL_CENTER, this);
		layout.putConstraint(EAST, pnlButtonBar, 0, EAST, this);
		layout.putConstraint(WEST, pnlButtonBar, 0, WEST, this);

		btnInfo = new JButton(actionInfo);
		btnSettings = new JButton(actionSettings);

		btnSettings.setText("Impostazioni");
		layout.putConstraint(NORTH, btnSettings, 0, NORTH, btnInfo);
		layout.putConstraint(SOUTH, btnSettings, 0, SOUTH, btnInfo);
		layout.putConstraint(EAST, btnSettings, -BUTTON_SPACING, WEST, btnInfo);

		var questionIcon = UIManager.getIcon("OptionPane.questionIcon");
		btnInfo.setText("Informazioni");
		btnInfo.setIcon(questionIcon);
		layout.putConstraint(SOUTH, btnInfo, -GENERIC_SPACING, SOUTH, this);
		layout.putConstraint(EAST, btnInfo, -GENERIC_SPACING, EAST, this);

		// Imposta icona di btnSettings in base alla grandezza di quella di btnInfo
		// che può variare dal Look and Feel e/o in base alla piattaforma
		int questionIconW = questionIcon.getIconWidth();
		btnSettings.setIcon(new FlatSVGIcon("icons/intellij/gearPlain.svg", questionIconW, questionIconW));

		add(pnlButtonBar);
		add(btnSettings);
		add(btnInfo);

		if (SettingsManager.getInstance().isFirstTime()) {
			// Solo dopo aver mostrato il menù (this)
			SwingUtilities.invokeLater(this::showWelcome);
		}
	}

	private void initActions() {
		InputMap glblInputMap = this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);

		actionChef = new SimpleAction(() -> this.switchPage(new ChefPage(this.getFrame())));
		this.getActionMap().put("Chef", actionChef);
		glblInputMap.put(KeyStroke.getKeyStroke('1'), "Chef");

		actionCameriere = new SimpleAction(() -> this.switchPage(new CamerierePage(this.getFrame())));
		this.getActionMap().put("Cameriere", actionCameriere);
		glblInputMap.put(KeyStroke.getKeyStroke('2'), "Cameriere");

		actionCuoco = new SimpleAction(() -> this.switchPage(new CuocoPage(this.getFrame())));
		this.getActionMap().put("Cuoco", actionCuoco);
		glblInputMap.put(KeyStroke.getKeyStroke('3'), "Cuoco");

		actionCassa = new SimpleAction(() -> this.switchPage(new CassaPage(this.getFrame())));
		this.getActionMap().put("Cassa", actionCassa);
		glblInputMap.put(KeyStroke.getKeyStroke('4'), "Cassa");

		actionSettings = new SimpleAction(this::showSettings);
		this.getActionMap().put("Settings", actionSettings);
		glblInputMap.put(KeyStroke.getKeyStroke('s'), "Settings");

		actionInfo = new SimpleAction(this::showInfo);
		this.getActionMap().put("Info", actionInfo);
		glblInputMap.put(KeyStroke.getKeyStroke('i'), "Info");

		Action actionShortcutInfo = new SimpleAction(() ->
				JShortcutInfoPane.showShortcutInfoMessage(this,
						"1", "Chef",
						"2", "Cameriere",
						"3", "Cuoco",
						"4", "Cassa",
						"S", "Impostazioni",
						"I", "Informazioni",
						"Alt+F4", "Esci")
		);
		this.getActionMap().put("Shortcuts", actionShortcutInfo);
		glblInputMap.put(KeyStroke.getKeyStroke("F1"), "Shortcuts");
	}

	private void showWelcome() {
		JOptionPane.showMessageDialog(this, """
				Benvenuti in PrestoManager.
				Prima di iniziare è consigliato inserire delle informazioni di base sul ristorante.

				In qualsiasi momento potete vedere le scorciatoie da tastiera per la pagina corrente premendo F1.

				Il programma è fornito con dei piatti di esempio preimpostati, è possibile modificarli o eliminarli a piacimento.

				Per qualsiasi problema potete scrivere a Valerio Colella su colella.1951557@studenti.uniroma1.it
				""");
		this.showSettings();
	}

	private void showSettings() {

		SettingsManager settings = SettingsManager.getInstance();

		// Volutamente verifichiamo poco l'input
		var txtName = new JTextField(settings.get(Setting.NAME));
		var txtVat = new JTextField(settings.get(Setting.VAT));
		var txtAddress = new JTextField(settings.get(Setting.ADDRESS));
		var txtPhone = new JTextField(settings.get(Setting.PHONE));

		// Consenti solo alcuni caratteri per il numero di telefono
		txtPhone.addKeyListener(new PhoneNumberAdapter(txtPhone));

		JOptionPane settingsPane = new JOptionPane(
				new Object[] {
						new JLabel("Nome ristorante"),
						txtName,
						new JLabel("Partita IVA"),
						txtVat,
						new JLabel("Indirizzo"),
						txtAddress,
						new JLabel("Telefono"),
						txtPhone
				},
				JOptionPane.PLAIN_MESSAGE,
				JOptionPane.OK_CANCEL_OPTION,
				new FlatSVGIcon("icons/intellij/gearPlain.svg", 64, 64))
		{
			// Forza focus iniziale su txtName
			@Override
			public void selectInitialValue() {
				txtName.requestFocusInWindow();
			}
		};

		settingsPane.createDialog(this, "Impostazioni").setVisible(true);

		if (settingsPane.getValue() == (Integer) JOptionPane.OK_OPTION) {
			settings.set(Setting.NAME, txtName.getText().strip());
			settings.set(Setting.VAT, txtVat.getText().strip());
			settings.set(Setting.ADDRESS, txtAddress.getText().strip());
			settings.set(Setting.PHONE, txtPhone.getText().stripTrailing());
			settings.setDoneFirstTime();
		}

	}

	private void showInfo() {

		// BUG: JOptionPane non accetta correttamente html con newline,
		// usiamo '\' alla fine di ogni riga per rimuoverle
		int result = JOptionPane.showOptionDialog(this, """
				<html>\
				<h1>PrestoManager</h1>\
				<h2>di Valerio Colella (1951557)</h2>\
				<p>Per Metodologie di Programmazione 2021-2022 (Quattrociocchi - Etta)</p>\
				<p>Progetto iniziato a febbraio 2022</p></html>\
				""",
				"Info",
				JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.PLAIN_MESSAGE,
				new FlatSVGIcon("icons/icon.svg", 128, 128),
				new String[] { "Licenze Open Source", "Indietro" },
				"Indietro");

		if (result == JOptionPane.OK_OPTION) {
			JOptionPane.showMessageDialog(this, """
					<html><ul>\
					<li><b>FlatLaf</b> e <b>Jackson</b> sono concesse sotto la <b>Apache License 2.0</b></li>\
					<li><b>svgSalamander</b> è concessa sotto la <b>Simplified BSD License 2.0<b/></li>\
					<li><b>SLF4J e SimpleLogger sono concesse sotto la <b>MIT License</b></li>\
					<li>La classe <b>DocumentSizeFilter</b> (modificata) è concessa sotto la <b>BSD License 2.0</b></li>\
					<li>Le icone in <b>icons/intellij</b> e <b>icons/intellij_modified</b> sono concesse sotto la <b>Apache License 2.0</b></li>\
					<li>Le icone in <b>icons/miscellanea</b> sono <b>Creative Commons 0: Pubblico Dominio</b></li>\
					</ul></html>\
					""",
					"Licenze Open Source",
					JOptionPane.PLAIN_MESSAGE,
					new FlatSVGIcon("icons/intellij/Library.svg", 64, 64));
		}
	}
}
