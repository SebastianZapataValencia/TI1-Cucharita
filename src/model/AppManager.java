package model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AppManager {

	private List<StaffMember> staff;
	private List<Ingredient> inventory;
	private List<Combo> combos;
	private List<Order> orders;
	
	public String STAFF_MEMBERS_DATA = "data/StaffMembersList.txt";
	public String INVENTORY_DATA = "data/InventoryList.txt";
	public String COMBO_DATA_BIN = "data/ComboListBinary.bin";
	public String ORDER_DATA_BIN = "data/OrderListBinary.bin";

	public AppManager() {
		
		staff = new ArrayList<StaffMember>();
		inventory = new ArrayList<Ingredient>();
		combos = new ArrayList<Combo>();
		orders = new ArrayList<Order>();
	}
	
	public List<StaffMember> getStaff() {
		return staff;
	}
	
	public void setStaff(List<StaffMember> staff) {
		this.staff = staff;
	}
	
	public List<Ingredient> getInventory() {
		return inventory;
	}
	
	public void setInventory(List<Ingredient> inventory) {
		this.inventory = inventory;
	}
	
	public List<Combo> getCombos() {
		return combos;
	}
	
	public void setCombos(List<Combo> combos) {
		this.combos = combos;
	}
	
	public List<Order> getOrders() {
		return orders;
	}
	
	public void setOrders(List<Order> orders) {
		this.orders = orders;
	}
	
	
	//_______________________________STAFF________________________________
	
	public boolean correctPassword(String id, String password) {
		
		boolean correct = false;
		
		for(int i = 0; i < staff.size() && !correct; i++) {
			
			if(staff.get(i).getId().equals(id)) {
				
				if(staff.get(i).getPassword().equals(password)) {
					
					correct = true;
				}
			}
		}
		
		return correct;
	}
	
	public boolean addStaffMember(StaffMember m) {

		if(staff.add(m)) {

//			totalBillboards++;
			
			return true;

		} else {

			return false;
		}
	}
	
	public void importStaffData() throws IOException {

//		StaffMembers.removeAll(StaffMembers);

		BufferedReader br = new BufferedReader(new FileReader(STAFF_MEMBERS_DATA));
		String line = br.readLine();

		while(line != null) {

			String [] parts = line.split(";");
			StaffMember  m = new StaffMember(parts[0], parts[1], parts[2], parts[3]);
			addStaffMember(m);
			line = br.readLine();
		}

		br.close();
	}

	public void exportStaffData() throws IOException {

		FileWriter fw = new FileWriter(STAFF_MEMBERS_DATA, false);

		for(int i = 0; i < staff.size(); i++) {

			StaffMember m = staff.get(i);
			fw.write(m.getName() + ";" + m.getId() + ";" + 
					m.getPassword() + ";" + m.getBirthdate() + "\n");
		}

		fw.close();
	}
	
	
	//_______________________________INVENTORY________________________________
	
	public boolean addIngredient(Ingredient i) {
		
		if(inventory.add(i)) {
			
			return true;
			
		} else {
			
			return false;
		}
	}
	
	public List<String> inventoryNamesInList() {
		
		List<String> ingredientNames = new ArrayList<String>();
		
		if(inventory != null) {
			
			for(int i = 0; i < inventory.size(); i++) {
				
				String n = inventory.get(i).getName();
				ingredientNames.add(n);
			}
		}
		
		return ingredientNames;
	}
	
	public void sortInventoryByName() {
		
		Comparator<Ingredient> c1 = new InventoryNameComparator();
		Collections.sort(inventory, c1);
	}
	
	public void importInventoryData() throws IOException {

		BufferedReader br = new BufferedReader(new FileReader(INVENTORY_DATA));
		String line = br.readLine();

		while(line != null) {

			String [] parts = line.split(";");
			
			int quantity = Integer.parseInt(parts[1]);
			
			Ingredient  i = new Ingredient(parts[0], quantity, parts[2]);
			addIngredient(i);
			line = br.readLine();
		}
		
		sortInventoryByName();
		
		br.close();
	}

	public void exportInventoryData() throws IOException {

		FileWriter fw = new FileWriter(INVENTORY_DATA, false);

		for(int i = 0; i < inventory.size(); i++) {

			Ingredient ing = inventory.get(i);
			fw.write(ing.getName() + ";" + ing.getQuantity() + ";" + 
					ing.getUnit() + "\n");
		}

		fw.close();
	}
	
	
	//_______________________________COMBOS________________________________
	
	public boolean addCombo(Combo c) {
		
		if(combos.add(c)) {
			
			return true;
			
		} else {
			
			return false;
		}
	}
	
	public List<Ingredient> ingredientsForCombo(String line) {

		List<Ingredient> list = new ArrayList<Ingredient>();

		String [] ingredients = line.split("\n");

		for(int i = 0; i < ingredients.length; i++) {

			String [] attributes = ingredients[i].split(";");

			int quantity = Integer.parseInt(attributes[1]);

			Ingredient ing = new Ingredient(attributes[0], quantity, attributes[2]);

			list.add(ing);
		}
		
		return list;
	}
	
	public List<String> comboNames() {
		
		List<String> names = new ArrayList<String>();
		
		for(int i = 0; i < combos.size(); i++) {
			
			String n = combos.get(i).getName();
			names.add(n);
		}
		
		return names;
	}
	
	public void saveComboData() throws FileNotFoundException, IOException {

		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(COMBO_DATA_BIN));
		oos.writeObject(combos);
		oos.close();
	}

	@SuppressWarnings("unchecked")
	public boolean loadComboData() throws FileNotFoundException, IOException, ClassNotFoundException {

		File f = new File(COMBO_DATA_BIN);

		boolean isLoaded = false;

		if(f.exists()) {

			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f));
			combos = (List<Combo>) ois.readObject();
			ois.close();
			isLoaded = true;
		}

		return isLoaded;
	}
	
	public boolean itemIsAvailable(String name, int quantity) {
		
		boolean exists = false;
		boolean available = false;
		
		for(int i = 0; i < inventory.size() && !exists; i++) {
			
			if(inventory.get(i).getName().equalsIgnoreCase(name)) {
				
				exists = true;

				if(inventory.get(i).getQuantity() - quantity >= 0) {
					
					available = true;
				}
			}
		}
		
		return available;
	}
	
	
	//_______________________________ORDERS________________________________
	
	public boolean addOrder(Order o) {
		
		if(orders.add(o)) {
			
			return true;
			
		} else {
			
			return false;
		}
	}
	
	public List<Ingredient> comboIngredientsList(String comboName) {
		
		List<Ingredient> list = new ArrayList<Ingredient>();
		boolean exists = false;
		
		for(int i = 0; i < combos.size() && !exists; i++) {
			
			if(combos.get(i).getName().equalsIgnoreCase(comboName)) {
				
				exists = true;
				
				list.addAll(combos.get(i).getIngredients());
			}
		}
		
		return list;
	}
	
	public Combo findThisCombo(String name) {
		
		boolean exists = false;
		
		Combo c = null;
		
		for(int i = 0; i < combos.size() && !exists; i++) {
			
			if(combos.get(i).getName().equalsIgnoreCase(name)) {
				
				c = combos.get(i);
				exists = true;
			}
		}
		
		return c;
	}
	
	public List<Combo> organizeCombosByPriceInsertionSort(List<Combo> list) {

		int j;
		Combo aux;
		
		for(int i = 1; i < list.size(); i++) {
			
			aux = list.get(i);
			j = i - 1;
			
			while(j >= 0 && aux.getPrice() < list.get(j).getPrice()) {
				
				list.set(j + 1, list.get(j));
				j--;
			}
			
			list.set(j + 1, aux);
		}

		return list;
	}
	
	public void changeOrderStatus(String uuid, String newStatus) {
		
		boolean exists = false;
		
		for(int i = 0; i < orders.size() && !exists; i++) {
			
			if(orders.get(i).getUuid().equalsIgnoreCase(uuid)) {
				
				orders.get(i).setStatus(newStatus);
				exists = true;
			}
		}
	}
	
	public void saveOrderData() throws FileNotFoundException, IOException {

		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ORDER_DATA_BIN));
		oos.writeObject(orders);
		oos.close();
	}

	@SuppressWarnings("unchecked")
	public boolean loadOrderData() throws FileNotFoundException, IOException, ClassNotFoundException {

		File f = new File(ORDER_DATA_BIN);

		boolean isLoaded = false;

		if(f.exists()) {

			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f));
			orders = (List<Order>) ois.readObject();
			ois.close();
			isLoaded = true;
		}

		return isLoaded;
	}
	
	public List<String> combosOfAnOrder(String uuid) {
		
		List<String> list = new ArrayList<String>();
		
		boolean exists = false;
		
		for(int i = 0; i < orders.size() && !exists; i++) {
			
			if(orders.get(i).getUuid().equalsIgnoreCase(uuid)) {
				
				for(int j = 0; j < orders.get(i).getCombos().size(); j++) {
					
					list.add(orders.get(i).getCombos().get(j).getName());
				}
			}
		}
		
		return list;
	}
	
//	public List<Ingredient> getTheIngredientsForThisCombo(String name) {
//		
//		List<Ingredient> list = new ArrayList<Ingredient>();
//		
//		for(int i = 0; i < combos.size(); i++) {
//			
//			if(combos.get(i).getName().equalsIgnoreCase(name)) {
//				
//				list.addAll(combos.get(i).getIngredients());
//			}
//		}
//		
//		return list;
//	}
}
