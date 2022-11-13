package it.colella.prestomanager.view.component;

import java.awt.Component;

import javax.swing.JOptionPane;

import com.formdev.flatlaf.extras.FlatSVGIcon;

/**
 * {@link JOptionPane} contenente una lista di scorciatoie da tastiera per la
 * pagina corrente
 */
public class JShortcutInfoPane {

	public static void showShortcutInfoMessage(Component parentComponent, String... message) {

		if (message.length % 2 != 0) {
			throw new IllegalArgumentException("showShortcutInfoMessage got odd number of message args");
		}

		StringBuilder builder = new StringBuilder("""
				<html>\
				<head><style>\
				table, td {\
					border: 1px solid #aaa;\
				}\
				</style></head>\
				<table>\
				""");
		for (int i = 0; i < message.length; i += 2) {
			builder.append("<tr><td>");
			builder.append(message[i]);
			builder.append("</td><td>");
			builder.append(message[i+1]);
			builder.append("</td></tr>");
		}
		builder.append("</table></html>");

		JOptionPane.showMessageDialog(parentComponent,
				builder,
				"Info scorciatoie",
				JOptionPane.INFORMATION_MESSAGE,
				new FlatSVGIcon("icons/intellij/Keyboard(Color).svg", 64, 64));
	}

	private JShortcutInfoPane() { }

}
