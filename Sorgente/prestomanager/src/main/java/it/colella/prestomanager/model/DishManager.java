package it.colella.prestomanager.model;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

/**
 * Responsabile della lettura e scrittura di piatti su disco fisso.
 */
public class DishManager {

	private static final Logger log = LoggerFactory.getLogger(DishManager.class);

	private static final DishManager instance = new DishManager();
	private static final String DISH_FILE_PATH = "dishes.json";

	private Set<Dish> dishSet;
	/** Segna se il set in memoria volatile è al momento diverso da quello su disco fisso **/
	private boolean dishSetDirty;

	private DishManager() {
		this.dishSetDirty = false;

		log.info("Loading dishes from {}", Paths.get(DISH_FILE_PATH).toAbsolutePath());

		// Se il file non esiste, init set vuoto
		if (!Paths.get(DISH_FILE_PATH).toFile().exists()) {
			log.info("Dishes file not found, initializing defaults");
			this.dishSet = new HashSet<>();
			return;
		}

		// Altrimenti leggi da JSON
		ObjectMapper mapper = new ObjectMapper();
		try {
			this.dishSet = mapper.readValue(
							Paths.get(DISH_FILE_PATH).toFile(),
							new TypeReference<HashSet<Dish>>() {});
		}
		catch (IOException e) {
			log.error("Problem when reading dishes, initializing defaults", e);
			this.dishSet = new HashSet<>();
		}
	}

	public static DishManager getInstance() {
		return instance;
	}

	/**
	 * Salva i piatti su disco fisso, se necessario
	 */
	public void write() {

		if (!this.dishSetDirty) {
			log.info("Saving dishes unnecessary, skipping");
		}
		else {
			this.writeForced();
		}
	}

	/**
	 * Salva i piatti su disco fisso
	 */
	public void writeForced() {
		log.info("Saving dishes in {}", DISH_FILE_PATH);

		// Usiamo un pretty printer per motivi didattici
		ObjectWriter writer = new ObjectMapper().writer(new DefaultPrettyPrinter());

		try {
			writer.writeValue(Paths.get(DISH_FILE_PATH).toFile(), this.dishSet);
			this.dishSetDirty = false;
		}
		catch (IOException e) {
			log.error("Unable to save dish file", e);
		}
	}

	/**
	 * Restituisce un Set read-only dei piatti
	 */
	public Set<Dish> getDishSet() {
		return Collections.unmodifiableSet(this.dishSet);
	}

	/**
	 * Aggiunge un piatto al menù, se un piatto con lo stesso nome non
	 * esiste già
	 *
	 * @param d il piatto da aggiungere
	 * @return {@code true} se il menù non conteneva già un piatto con lo stesso
	 *         nome, {@code false} altrimenti
	 */
	public boolean add(Dish d) {
		this.dishSetDirty = true;
		return this.dishSet.add(d);
	}

	/**
	 * Rimuove un piatto dal menù, se presente
	 *
	 * @param d il piatto da rimuovere
	 * @return {@code true} se il piatto era effettivamente nel menù, {@code false}
	 *         altrimenti
	 */
	public boolean remove(Dish d) {
		this.dishSetDirty = true;
		return this.dishSet.remove(d);
	}
}
