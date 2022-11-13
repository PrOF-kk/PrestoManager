package it.colella.prestomanager.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Responsabile del corretto passaggio di ordini tra cameriere, cuoco e cassa
 */
public class OrderManager {

	private static final Logger log = LoggerFactory.getLogger(OrderManager.class);

	private static final OrderManager instance = new OrderManager();

	private final List<Order> cuocoOrders;
	private final Map<Integer, Order> cassaOrders;
	private final List<Order> orderArchive;

	private OrderManager() {
		this.cuocoOrders = new ArrayList<>();
		this.cassaOrders = new HashMap<>();
		this.orderArchive = new ArrayList<>();
	}

	public static OrderManager getInstance() {
		return instance;
	}

	/**
	 * Finalizza un ordine. Non potrà più essere modificato e diventa visibile al cuoco
	 */
	public void finalizeOrder(Order order) {
		log.info("Finalizzazione ordine {}", order);
		cuocoOrders.add(order);
	}

	/**
	 * Evade l'ordine all'indice dato
	 *
	 * @param index l'indice dell'ordine da rimuovere
	 * @throws IndexOutOfBoundsException
	 */
	public void evadi(int index) {
		Order daEvadere = this.cuocoOrders.remove(index);
		int tableNum = daEvadere.getTableNumber();

		this.cassaOrders.putIfAbsent(tableNum, new Order());
		this.cassaOrders.get(tableNum).addAll(daEvadere);

		log.info("Evaso {}", daEvadere);
		log.info("Conto complessivo tavolo [{}]: {}", tableNum, this.cassaOrders.get(tableNum));
	}

	/**
	 * Paga il conto del tavolo tableNum. Non sono effettuati controlli per
	 * determinare se il tavolo può pagare (vedi {@link #isPayable(int)} o meno
	 *
	 * @param tableNum il numero del tavolo da pagare
	 * @throws NoSuchElementException se il tavolo non ha ordini da pagare
	 * @see OrderManager#isPayable(int)
	 */
	public void payTable(int tableNum) {

		if (!cassaOrders.containsKey(tableNum)) {
			throw new NoSuchElementException("No orders for table " + tableNum);
		}

		Order orderToPay = cassaOrders.remove(tableNum);
		orderArchive.add(orderToPay);

		log.info("Pagato {}", orderToPay);
		log.info("Evasi in totale [{}] ordini", this.orderArchive.size());
	}

	/**
	 * Restituisce una fila read-only degli ordini da evadere
	 */
	public List<Order> getCuocoOrders() {
		return Collections.unmodifiableList(this.cuocoOrders);
	}

	/**
	 * Restituisce una mappa read-only degli ordini evasi ma da pagare
	 */
	public Map<Integer, Order> getCassaOrders() {
		return Collections.unmodifiableMap(cassaOrders);
	}

	/**
	 * Restituisce {@code true} se l'ordine è stato evaso ma è da pagare, e non ci sono ordini
	 * dello stesso tavolo che devono essere ancora evasi. Altrimenti {@code false}
	 *
	 * @param o l'ordine
	 * @return {@code true} se l'ordine è pagabile, {@code false} altrimenti
	 */
	public boolean isPayable(Order o) {
		if (!this.cassaOrders.containsValue(o)) {
			return false;
		}

		for (Order q : this.cuocoOrders) {
			if (o.getTableNumber() == q.getTableNumber()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Restituisce {@code true} se il tavolo ha ordini evasi ma da pagare, e non ci
	 * sono ordini dello stesso tavolo che devono essere ancora evasi. Altrimenti
	 * {@code false}
	 *
	 * @param tableNum il numero del tavolo
	 * @return {@code true} se il conto del tavolo è pagabile, {@code false}
	 *         altrimenti
	 */
	public boolean isPayable(int tableNum) {
		if (!this.cassaOrders.containsKey(tableNum)) {
			return false;
		}

		for (Order o : this.cuocoOrders) {

			if (o.getTableNumber() == tableNum) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Restituisce una mappa read-only degli ordini <b>pagabili</b>
	 *
	 * @return mappa degli ordini già evasi ma da pagare, di tavoli senza ordini
	 *         ancora da evadere
	 */
	public Map<Integer, Order> getPayableOrders() {
		return this.cassaOrders.entrySet().stream()
				.filter(entry -> this.isPayable(entry.getValue()))
				.collect(Collectors.toUnmodifiableMap(Entry::getKey, Entry::getValue));
	}

	/**
	 * Restituisce i tavoli <b>pagabili<b>
	 *
	 * @return array di tavoli che hanno tutti gli ordini già evasi
	 */
	public Integer[] getPayableTables() {

		return this.cassaOrders.keySet().stream()
				.filter(this::isPayable)
				.toArray(Integer[]::new);
	}
}
