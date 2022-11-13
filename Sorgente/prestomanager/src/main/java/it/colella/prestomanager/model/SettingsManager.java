package it.colella.prestomanager.model;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

/**
 * Responsabile della lettura e scrittura delle impostazioni su disco fisso.
 */
public class SettingsManager {

	private static final Logger log = LoggerFactory.getLogger(SettingsManager.class);

	private static final SettingsManager instance = new SettingsManager();
	private static final String SETTINGS_FILE_PATH = "settings.json";

	private Map<Setting, String> settings;
	/** Segna se la mappa in memoria volatile è al momento diversa da quella su disco fisso **/
	private boolean settingsDirty;
	/** Segna se è la prima volta che si apre il programma (o dopo un reset) */
	private boolean firstTime;

	private SettingsManager() {
		this.settingsDirty = false;
		this.firstTime = false;

		log.info("Loading settings from {}", Paths.get(SETTINGS_FILE_PATH).toAbsolutePath());

		// Se il file non esiste inizializza default
		if (!Paths.get(SETTINGS_FILE_PATH).toFile().exists()) {
			log.info("Settings file not found, initializing defaults");
			this.firstTime = true;
			this.initDefaults();
			return;
		}

		// Altrimenti leggi da JSON
		ObjectMapper mapper = new ObjectMapper();
		try {
			this.settings = mapper.readValue(
								Paths.get(SETTINGS_FILE_PATH).toFile(),
								new TypeReference<EnumMap<Setting, String>>() {});
		}
		catch (IOException e) {
			log.error("Problem when reading settings, initializing defaults", e);
			this.initDefaults();
		}
	}

	public static SettingsManager getInstance() {
		return instance;
	}

	/**
	 * Salva i piatti su disco fisso, se necessario
	 */
	public void write() {

		if (!this.settingsDirty) {
			log.info("Saving settings unnecessary, skipping");
		}
		else {
			this.writeForced();
		}
	}

	/**
	 * Salva i piatti su disco fisso
	 */
	public void writeForced() {
		log.info("Saving settings in {}", SETTINGS_FILE_PATH);

		// Usiamo un pretty printer per motivi didattici
		ObjectWriter writer = new ObjectMapper().writer(new DefaultPrettyPrinter());

		try {
			writer.writeValue(Paths.get(SETTINGS_FILE_PATH).toFile(), this.settings);
			this.settingsDirty = false;
		}
		catch (IOException e) {
			log.error("Unable to save settings file", e);
		}
	}

	private void initDefaults() {
		this.settings = new EnumMap<>(Setting.class);

		this.settings.putAll(Map.of(
				Setting.NAME, "",
				Setting.VAT, "",
				Setting.ADDRESS, "",
				Setting.PHONE, ""
		));
	}

	/**
	 * Restituisce una mappa read-only delle impostazioni
	 */
	public Map<Setting, String> getSettings() {
		return Collections.unmodifiableMap(this.settings);
	}

	public String get(Setting setting) {
		return this.settings.get(setting);
	}

	public String set(Setting setting, String value) {
		this.settingsDirty = true;
		return this.settings.put(setting, value);
	}

	public boolean isFirstTime() {
		return this.firstTime;
	}

	public void setDoneFirstTime() {
		this.firstTime = false;
	}

}
