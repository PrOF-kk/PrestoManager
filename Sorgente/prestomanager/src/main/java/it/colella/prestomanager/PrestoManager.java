package it.colella.prestomanager;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Locale;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.extras.FlatSVGIcon;

import it.colella.prestomanager.model.DishManager;
import it.colella.prestomanager.model.OrderManager;
import it.colella.prestomanager.model.SettingsManager;
import it.colella.prestomanager.view.MenuPage;
import it.colella.prestomanager.view.Page;

// TODO export javadoc
// TODO export simplelogger properties
public class PrestoManager {

	public static void main(String[] args) {

		Locale.setDefault(Locale.ITALY);
		// Leggi da disco prima di inizializzare la GUI
		SettingsManager.getInstance();
		DishManager.getInstance();

		SwingUtilities.invokeLater(() -> {

			Logger log = LoggerFactory.getLogger(PrestoManager.class);
			log.debug("PrestoManager starting in {}", System.getProperty("user.dir"));

			FlatLaf.registerCustomDefaultsSource("themes");
			FlatLightLaf.setup();

			JFrame frame = new JFrame("PrestoManager");
			frame.setPreferredSize(new Dimension(1280, 720));
			frame.setIconImage(new FlatSVGIcon("icons/icon.svg").getImage());

			// Richiedi conferma all'uscita se ci sono piatti non evasi o non pagati
			frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
			frame.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent event) {

					if (OrderManager.getInstance().getCuocoOrders().isEmpty() && OrderManager.getInstance().getCassaOrders().isEmpty()) {
						this.saveAndExit();
						return;
					}

					int result = JOptionPane.showConfirmDialog(frame,
							"Sono presenti degli ordini ancora da evadere o da pagare.\nUscire?",
							null,
							JOptionPane.YES_NO_OPTION,
							JOptionPane.WARNING_MESSAGE);

					if (result == JOptionPane.YES_OPTION) {
						this.saveAndExit();
					}
				}

				private void saveAndExit() {
					// Salva i piatti se necessario
					DishManager.getInstance().write();
					// Salva impostazioni se necessario
					SettingsManager.getInstance().write();

					frame.dispose();
					System.exit(0);
				}
			});

			Page.setPage(frame, new MenuPage(frame));

			frame.pack();
			frame.setVisible(true);
		});

		// Stampa tutti gli UIDefaults in ordine di chiave
		// Usato per creare uidefaults.txt, lasciato per dimostrazione
		/*
		UIManager.getDefaults().entrySet().stream()
				.sorted(Comparator.comparing(Object::toString))
				.forEach(entry ->
						System.out.println(
							entry.getKey().toString()
							+ " : "
							+ entry.getValue().toString())
		);
		*/
	}
}