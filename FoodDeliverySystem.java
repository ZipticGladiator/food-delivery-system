package fooddeliverysystem;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class FoodDeliverySystem {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Welcome to Food Quick - Your Food Delivery System!");

        // Ask the user if they are a customer or a restaurant owner
        System.out.println("Are you a customer or a restaurant owner?");
        System.out.println("1. Customer");
        System.out.println("2. Restaurant Owner");
        System.out.print("Enter your choice (1 or 2): ");
        int userChoice = scanner.nextInt();
        scanner.nextLine();  // Consume the newline character

        if (userChoice == 1) {
            // Customer
            Customer customer = captureCustomerDetails(scanner);
            selectRestaurantAndPlaceOrder(scanner, customer);

        } else if (userChoice == 2) {
            // Restaurant Owner
            Restaurant restaurant = captureRestaurantDetails(scanner);
            // Additional logic for restaurant owner, e.g., menu management, joining the platform, etc.
            System.out.println("Thank you, restaurant details saved!");

        } else {
            System.out.println("Invalid choice. Exiting the program.");
        }

        scanner.close();
    }

    private static Customer captureCustomerDetails(Scanner scanner) {
        System.out.println("\nEnter customer details:");
        System.out.print("Name: ");
        String customerName = scanner.nextLine();
        System.out.print("Address: ");
        String customerAddress = scanner.nextLine();
        System.out.print("City: ");
        String customerCity = scanner.nextLine();

        return new Customer(customerName, customerAddress, customerCity);
    }

    private static Restaurant captureRestaurantDetails(Scanner scanner) {
        System.out.println("\nEnter restaurant details:");
        System.out.print("Name: ");
        String restaurantName = scanner.nextLine();
        System.out.print("Location: ");
        String restaurantLocation = scanner.nextLine();

        return new Restaurant(restaurantName, restaurantLocation);
    }

    private static void selectRestaurantAndPlaceOrder(Scanner scanner, Customer customer) {
        // For simplicity, let's use one hardcoded restaurant.
        Restaurant restaurant = new Restaurant("Sample Restaurant", "Sample Location");

        // Order dishes
        Map<String, Integer> orderedDishes = orderDishes(scanner, restaurant);

        // Read drivers from drivers.txt and find the driver with the smallest load in the correct area
        Driver selectedDriver = findDriver(customer.city);

        // Generate invoice with dish details and total amount
        generateInvoice(customer, restaurant, selectedDriver, orderedDishes);

        System.out.println("\nInvoice generated successfully!");
    }

    private static Map<String, Integer> orderDishes(Scanner scanner, Restaurant restaurant) {
        System.out.println("\nMenu for " + restaurant.name + ":");
        Map<String, Double> menu = createMenu();  // Create a menu with dish names and prices

        Map<String, Integer> orderedDishes = new HashMap<>();

        while (true) {
            System.out.println("\nSelect a dish (type 'done' to finish):");
            for (String dish : menu.keySet()) {
                System.out.println(dish + " - $" + menu.get(dish));
            }

            String selectedDish = scanner.nextLine();

            if (selectedDish.equalsIgnoreCase("done")) {
                break;
            }

            if (menu.containsKey(selectedDish)) {
                System.out.print("Enter quantity: ");
                int quantity = scanner.nextInt();
                scanner.nextLine();  // Consume the newline character

                orderedDishes.put(selectedDish, quantity);
            } else {
                System.out.println("Invalid dish selection. Please try again.");
            }
        }

        return orderedDishes;
    }

    private static Map<String, Double> createMenu() {
        // For simplicity, let's create a hardcoded menu.
        Map<String, Double> menu = new HashMap<>();
        menu.put("Dish 1", 10.0);
        menu.put("Dish 2", 15.0);
        menu.put("Dish 3", 8.0);
        // Add more dishes as needed
        return menu;
    }

    private static void generateInvoice(Customer customer, Restaurant restaurant, Driver driver,
                                        Map<String, Integer> orderedDishes) {
        try {
            FileWriter writer = new FileWriter("invoice.txt");
            writer.write("Invoice\n");
            writer.write("Customer: " + customer.name + "\n");
            writer.write("Address: " + customer.address + ", " + customer.city + "\n");
            writer.write("Restaurant: " + restaurant.name + "\n");

            if (driver != null) {
                writer.write("Driver: " + driver.name + "\n");
                writer.write("Driver Area: " + driver.area + "\n");
                writer.write("Load: " + driver.load + "\n");

                // Add ordered dishes to the invoice
                writer.write("\nOrdered Dishes:\n");
                double totalAmount = 0.0;
                for (String dish : orderedDishes.keySet()) {
                    int quantity = orderedDishes.get(dish);
                    double price = createMenu().get(dish);
                    double subtotal = quantity * price;
                    totalAmount += subtotal;

                    writer.write(dish + " x " + quantity + " - $" + subtotal + "\n");
                }

                // Add total amount to the invoice
                writer.write("\nTotal Amount: $" + totalAmount + "\n");
            } else {
                writer.write("Sorry! Our drivers are too far away from you to be able to deliver to your location.\n");
            }

            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Driver findDriver(String customerCity) {
        try {
            Scanner fileScanner = new Scanner(new File("drivers.txt"));

            Driver selectedDriver = null;
            int minLoad = Integer.MAX_VALUE;

            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                String[] driverInfo = line.split(",");

                if (driverInfo.length >= 3 && !driverInfo[2].trim().isEmpty()) {
                    String driverName = driverInfo[0];
                    String driverArea = driverInfo[1];
                    int driverLoad = Integer.parseInt(driverInfo[2].trim());

                    if (driverArea.equals(customerCity) && driverLoad < minLoad) {
                        minLoad = driverLoad;
                        selectedDriver = new Driver(driverName, driverArea, driverLoad);
                    }
                }
            }

            fileScanner.close();
            return selectedDriver;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
