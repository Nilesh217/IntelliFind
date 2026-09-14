package com.intellifind.repository;

import com.intellifind.model.FoundItem;
import com.intellifind.model.ItemStatus;
import com.intellifind.model.LostItem;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class ItemRepository {
    private static final String DATA_DIR = "data";
    private static final String LOST_FILE = DATA_DIR + File.separator + "lost_items.csv";
    private static final String FOUND_FILE = DATA_DIR + File.separator + "found_items.csv";

    private final List<LostItem> lostItems = new ArrayList<>();
    private final List<FoundItem> foundItems = new ArrayList<>();

    public ItemRepository() {
        initStorage();
        loadAll();
    }

    private void initStorage() {
        try {
            Files.createDirectories(Paths.get(DATA_DIR));
            File lf = new File(LOST_FILE);
            if (!lf.exists()) lf.createNewFile();
            File ff = new File(FOUND_FILE);
            if (!ff.exists()) ff.createNewFile();
        } catch (IOException e) {
            System.err.println("Warning: Unable to create storage directory: " + e.getMessage());
        }
    }

    public void loadAll() {
        lostItems.clear();
        foundItems.clear();

        // Line-by-line reading with active diagnostics
        int lineNo = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(LOST_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                lineNo++;
                if (!line.trim().isEmpty()) {
                    try {
                        lostItems.add(LostItem.fromCsv(line));
                    } catch (Exception e) {
                        System.err.printf("Warning: Skipping malformed lost record at line %d: %s%n", lineNo, e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Notice: Could not access " + LOST_FILE + " (" + e.getMessage() + ")");
        }

        lineNo = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(FOUND_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                lineNo++;
                if (!line.trim().isEmpty()) {
                    try {
                        foundItems.add(FoundItem.fromCsv(line));
                    } catch (Exception e) {
                        System.err.printf("Warning: Skipping malformed found record at line %d: %s%n", lineNo, e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Notice: Could not access " + FOUND_FILE + " (" + e.getMessage() + ")");
        }
    }

    public synchronized void saveAll() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(LOST_FILE, false))) {
            for (LostItem item : lostItems) pw.println(item.toCsv());
        } catch (IOException e) {
            System.err.println("Critical: Failed to save lost items: " + e.getMessage());
        }

        try (PrintWriter pw = new PrintWriter(new FileWriter(FOUND_FILE, false))) {
            for (FoundItem item : foundItems) pw.println(item.toCsv());
        } catch (IOException e) {
            System.err.println("Critical: Failed to save found items: " + e.getMessage());
        }
    }

    public void addLostItem(LostItem item) {
        if (item == null) throw new IllegalArgumentException("Lost item cannot be null.");
        lostItems.add(item);
        saveAll();
    }

    public void addFoundItem(FoundItem item) {
        if (item == null) throw new IllegalArgumentException("Found item cannot be null.");
        foundItems.add(item);
        saveAll();
    }

    // Repository search helpers
    public Optional<LostItem> findLostById(String id) {
        return lostItems.stream()
                .filter(i -> i.getId().equalsIgnoreCase(id.trim()))
                .findFirst();
    }

    public Optional<FoundItem> findFoundById(String id) {
        return foundItems.stream()
                .filter(i -> i.getId().equalsIgnoreCase(id.trim()))
                .findFirst();
    }

    public List<LostItem> getOpenLostItems() {
        return lostItems.stream()
                .filter(i -> i.getStatus() == ItemStatus.OPEN)
                .collect(Collectors.toList());
    }

    public List<FoundItem> getOpenFoundItems() {
        return foundItems.stream()
                .filter(i -> i.getStatus() == ItemStatus.OPEN)
                .collect(Collectors.toList());
    }

    public List<LostItem> getAllLost() { return Collections.unmodifiableList(lostItems); }
    public List<FoundItem> getAllFound() { return Collections.unmodifiableList(foundItems); }

    // Safe numeric ID parsing
    private int extractNumericId(String id, String prefix) {
        if (id == null || !id.startsWith(prefix)) return 0;
        try {
            return Integer.parseInt(id.substring(prefix.length()));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public int getNextLostId() {
        return lostItems.stream()
                .mapToInt(i -> extractNumericId(i.getId(), "L-"))
                .max().orElse(100) + 1;
    }

    public int getNextFoundId() {
        return foundItems.stream()
                .mapToInt(i -> extractNumericId(i.getId(), "F-"))
                .max().orElse(100) + 1;
    }
}