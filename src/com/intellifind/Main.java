package com.intellifind;

import com.intellifind.matcher.SimilarityEngine;
import com.intellifind.model.*;
import com.intellifind.repository.ItemRepository;
import com.intellifind.util.InputValidator;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final ItemRepository repository = new ItemRepository();
    private static final DateTimeFormatter DISPLAY_DATE = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public static void main(String[] args) {
        seedInitialDemoData();

        while (true) {
            printMainMenu();
            int choice = InputValidator.readIntInRange(scanner, "Enter choice (1-9): ", 1, 9);
            switch (choice) {
                case 1 -> reportLostItem();
                case 2 -> reportFoundItem();
                case 3 -> viewItems(true);
                case 4 -> viewItems(false);
                case 5 -> findPotentialMatches();
                case 6 -> searchItems();
                case 7 -> confirmMatchWorkflow();
                case 8 -> generateReport();
                case 9 -> {
                    System.out.println("Exiting IntelliFind. Data persisted.");
                    return;
                }
            }
        }
    }

    private static void printMainMenu() {
        System.out.println("\n==================================================");
        System.out.println("                   INTELLIFIND                    ");
        System.out.println("           LOST & FOUND MATCHING SYSTEM           ");
        System.out.println("==================================================");
        System.out.println("1. Report Lost Item");
        System.out.println("2. Report Found Item");
        System.out.println("3. View Lost Items");
        System.out.println("4. View Found Items");
        System.out.println("5. Run Matching Engine");
        System.out.println("6. Search Records");
        System.out.println("7. Confirm / Resolve Match");
        System.out.println("8. System Analytics & Statistics");
        System.out.println("9. Exit");
        System.out.println("--------------------------------------------------");
    }

    private static void reportLostItem() {
        System.out.println("\n========== REPORT LOST ITEM ==========");
        String category = selectCategory();
        String brand = InputValidator.readNonEmptyString(scanner, "Brand: ");
        String model = InputValidator.readNonEmptyString(scanner, "Model / Sub-type: ");
        String color = InputValidator.readNonEmptyString(scanner, "Color: ");
        String location = InputValidator.readNonEmptyString(scanner, "Location Lost: ");
        LocalDate date = InputValidator.readValidDate(scanner, "Date Lost (DD-MM-YYYY): ");
        String desc = InputValidator.readStringWithMaxLength(scanner, "Description (max 200 chars): ", 200);
        String contact = InputValidator.readNonEmptyString(scanner, "Contact Email/Phone: ");

        String id = "L-" + repository.getNextLostId();
        LostItem item = new LostItem(id, category, brand, model, color, location, date, desc, ItemStatus.OPEN, contact);
        repository.addLostItem(item);

        System.out.println("\n✔ Registered successfully with ID: " + id);
        triggerInstantMatchEvaluation(item);
    }

    private static void reportFoundItem() {
        System.out.println("\n========== REPORT FOUND ITEM ==========");
        String category = selectCategory();
        String brand = InputValidator.readNonEmptyString(scanner, "Brand: ");
        String model = InputValidator.readNonEmptyString(scanner, "Model / Sub-type: ");
        String color = InputValidator.readNonEmptyString(scanner, "Color: ");
        String location = InputValidator.readNonEmptyString(scanner, "Location Found: ");
        LocalDate date = InputValidator.readValidDate(scanner, "Date Found (DD-MM-YYYY): ");
        String desc = InputValidator.readStringWithMaxLength(scanner, "Description (max 200 chars): ", 200);
        String storage = InputValidator.readNonEmptyString(scanner, "Holding Desk/Locker: ");

        String id = "F-" + repository.getNextFoundId();
        FoundItem item = new FoundItem(id, category, brand, model, color, location, date, desc, ItemStatus.OPEN, storage);
        repository.addFoundItem(item);

        System.out.println("\n✔ Registered successfully with ID: " + id);
        triggerFoundInstantMatch(item);
    }

    private static void triggerInstantMatchEvaluation(LostItem lost) {
        System.out.println("Checking available found items for candidate matches...");
        List<MatchResult> candidates = repository.getOpenFoundItems().stream()
                .map(f -> SimilarityEngine.calculate(lost, f))
                .filter(res -> res.getOverallScore() >= 0.50)
                .sorted()
                .collect(Collectors.toList());

        if (candidates.isEmpty()) {
            System.out.println("No immediate matches found.");
            return;
        }
        displayRankedMatches(candidates);
    }

    // Bidirectional instant matching
    private static void triggerFoundInstantMatch(FoundItem found) {
        System.out.println("Checking open lost items for candidate matches...");
        List<MatchResult> candidates = repository.getOpenLostItems().stream()
                .map(l -> SimilarityEngine.calculate(l, found))
                .filter(res -> res.getOverallScore() >= 0.50)
                .sorted()
                .collect(Collectors.toList());

        if (candidates.isEmpty()) {
            System.out.println("No open lost records match this found item currently.");
            return;
        }
        displayRankedMatches(candidates);
    }

    private static void findPotentialMatches() {
        System.out.println("\n========== INTELLIGENT MATCHING ENGINE ==========");
        List<LostItem> openLost = repository.getOpenLostItems();
        if (openLost.isEmpty()) {
            System.out.println("No open lost records to match.");
            return;
        }

        for (int i = 0; i < openLost.size(); i++) {
            LostItem l = openLost.get(i);
            System.out.printf("%d. [%s] %s %s (%s)%n", i + 1, l.getId(), l.getBrand(), l.getModel(), l.getDate().format(DISPLAY_DATE));
        }

        int choice = InputValidator.readIntInRange(scanner, "Select index: ", 1, openLost.size());
        LostItem target = openLost.get(choice - 1);

        List<MatchResult> results = repository.getOpenFoundItems().stream()
                .map(f -> SimilarityEngine.calculate(target, f))
                .sorted()
                .collect(Collectors.toList());

        if (results.isEmpty()) {
            System.out.println("No open found items in records.");
            return;
        }
        displayRankedMatches(results);
    }

    private static void displayRankedMatches(List<MatchResult> results) {
        System.out.println("\n------------------------------------------------------------------");
        System.out.printf("%-4s | %-6s | %-12s | %-10s | %-8s | %-14s%n", "Rank", "ID", "Brand", "Color", "Score", "Verdict");
        System.out.println("------------------------------------------------------------------");
        for (int i = 0; i < results.size(); i++) {
            MatchResult mr = results.get(i);
            double s = mr.getOverallScore();
            String verdict = (s >= 0.85) ? "STRONG MATCH" : (s >= 0.70) ? "POSSIBLE MATCH" : (s >= 0.50) ? "WEAK MATCH" : "UNLIKELY";
            System.out.printf("%-4d | %-6s | %-12s | %-10s | %5.1f%%  | %-14s%n",
                    (i + 1), mr.getFoundItem().getId(), mr.getFoundItem().getBrand(),
                    mr.getFoundItem().getColor(), s * 100, verdict);
        }
        System.out.println("------------------------------------------------------------------");

        if (InputValidator.readYesNo(scanner, "Inspect top match breakdown? (Y/N): ")) {
            displayMatchDetails(results.get(0));
        }
    }

    private static void displayMatchDetails(MatchResult res) {
        LostItem l = res.getLostItem();
        FoundItem f = res.getFoundItem();

        System.out.println("\n╔════════════════════════════════════════════════════════╗");
        System.out.println("║              MATCH CONFIDENCE EXPLANATION              ║");
        System.out.println("╠════════════════════════════════════════════════════════╣");
        System.out.printf("║ Lost: %-16s Found: %-22s║%n", l.getId(), f.getId());
        System.out.printf("║ Score: %5.1f%%                                          ║%n", res.getOverallScore() * 100);
        System.out.println("║────────────────────────────────────────────────────────║");
        printMetricLine("Category    ", res.getCategoryScore());
        printMetricLine("Brand       ", res.getBrandScore());
        printMetricLine("Model       ", res.getModelScore());
        printMetricLine("Color       ", res.getColorScore());
        printMetricLine("Location    ", res.getLocationScore());
        printMetricLine("Date        ", res.getDateScore());
        printMetricLine("Description ", res.getDescScore());
        System.out.println("╚════════════════════════════════════════════════════════╝");
    }

    private static void printMetricLine(String label, double val) {
        String mark = (val >= 0.80) ? "✔" : (val >= 0.50) ? "▲" : "✖";
        System.out.printf("║ %s %s %5.1f%%                                   ║%n", mark, label, val * 100);
    }

    private static void confirmMatchWorkflow() {
        System.out.println("\n========== CONFIRM / RESOLVE MATCH ==========");
        String lostId = InputValidator.readNonEmptyString(scanner, "Enter Lost ID (e.g. L-101): ");
        String foundId = InputValidator.readNonEmptyString(scanner, "Enter Found ID (e.g. F-087): ");

        Optional<LostItem> lostOpt = repository.findLostById(lostId);
        Optional<FoundItem> foundOpt = repository.findFoundById(foundId);

        if (lostOpt.isEmpty() || foundOpt.isEmpty()) {
            System.out.println("Error: One or both record IDs were not found.");
            return;
        }

        LostItem lost = lostOpt.get();
        FoundItem found = foundOpt.get();

        // Recalculate on confirmation to show current evidence
        MatchResult currentMatch = SimilarityEngine.calculate(lost, found);
        displayMatchDetails(currentMatch);

        if (InputValidator.readYesNo(scanner, "Link and mark both items as MATCHED? (Y/N): ")) {
            lost.setStatus(ItemStatus.MATCHED);
            found.setStatus(ItemStatus.MATCHED);
            repository.saveAll();
            System.out.println("✔ Records successfully updated to MATCHED in storage.");
        } else {
            System.out.println("Resolution cancelled.");
        }
    }

    private static String selectCategory() {
        System.out.println("Category:\n1. Electronics\n2. Documents/Cards\n3. Clothing\n4. Accessories\n5. Other");
        int opt = InputValidator.readIntInRange(scanner, "Choice (1-5): ", 1, 5);
        return switch (opt) {
            case 1 -> "Electronics";
            case 2 -> "Documents";
            case 3 -> "Clothing";
            case 4 -> "Accessories";
            default -> "Other";
        };
    }

    private static void viewItems(boolean lost) {
        if (lost) {
            System.out.println("\n========== REGISTERED LOST ITEMS ==========");
            List<LostItem> list = repository.getAllLost();
            if (list.isEmpty()) { System.out.println("No records."); return; }
            System.out.printf("%-7s | %-12s | %-12s | %-10s | %-12s | %-8s%n", "ID", "Category", "Brand", "Color", "Date", "Status");
            System.out.println("----------------------------------------------------------------------");
            for (LostItem i : list) {
                System.out.printf("%-7s | %-12s | %-12s | %-10s | %-12s | %-8s%n",
                        i.getId(), i.getCategory(), i.getBrand(), i.getColor(), i.getDate().format(DISPLAY_DATE), i.getStatus());
            }
        } else {
            System.out.println("\n========== REGISTERED FOUND ITEMS ==========");
            List<FoundItem> list = repository.getAllFound();
            if (list.isEmpty()) { System.out.println("No records."); return; }
            System.out.printf("%-7s | %-12s | %-12s | %-10s | %-12s | %-8s%n", "ID", "Category", "Brand", "Color", "Date", "Status");
            System.out.println("----------------------------------------------------------------------");
            for (FoundItem i : list) {
                System.out.printf("%-7s | %-12s | %-12s | %-10s | %-12s | %-8s%n",
                        i.getId(), i.getCategory(), i.getBrand(), i.getColor(), i.getDate().format(DISPLAY_DATE), i.getStatus());
            }
        }
    }

    private static void searchItems() {
        System.out.println("\n========== SEARCH RECORDS ==========");
        System.out.println("1. By Category\n2. By Brand\n3. By Description Keyword");
        int mode = InputValidator.readIntInRange(scanner, "Choice: ", 1, 3);
        String query = InputValidator.readNonEmptyString(scanner, "Query: ").toLowerCase();

        System.out.println("\nMatching Lost Items:");
        repository.getAllLost().stream()
                .filter(i -> matchesQuery(i, mode, query))
                .forEach(i -> System.out.printf("  [%s] %s %s - %s%n", i.getId(), i.getBrand(), i.getModel(), i.getDescription()));

        System.out.println("\nMatching Found Items:");
        repository.getAllFound().stream()
                .filter(i -> matchesQuery(i, mode, query))
                .forEach(i -> System.out.printf("  [%s] %s %s - %s%n", i.getId(), i.getBrand(), i.getModel(), i.getDescription()));
    }

    private static boolean matchesQuery(Item item, int mode, String query) {
        return switch (mode) {
            case 1 -> item.getCategory().toLowerCase().contains(query);
            case 2 -> item.getBrand().toLowerCase().contains(query);
            case 3 -> item.getDescription().toLowerCase().contains(query);
            default -> false;
        };
    }

    private static void generateReport() {
        System.out.println("\n================ SYSTEM STATISTICS ================");
        List<LostItem> lost = repository.getAllLost();
        List<FoundItem> found = repository.getAllFound();

        long matchedLost = lost.stream().filter(i -> i.getStatus() == ItemStatus.MATCHED).count();
        long matchedFound = found.stream().filter(i -> i.getStatus() == ItemStatus.MATCHED).count();

        System.out.println("Total Registered Lost Items  : " + lost.size());
        System.out.println("Total Registered Found Items : " + found.size());
        System.out.println("Confirmed Matches (Pairs)    : " + Math.min(matchedLost, matchedFound));
        System.out.println("Open Lost Items Remaining    : " + (lost.size() - matchedLost));
        System.out.println("Open Found Items Remaining   : " + (found.size() - matchedFound));
        System.out.println("===================================================");
    }

    private static void seedInitialDemoData() {
        if (repository.getAllLost().isEmpty() && repository.getAllFound().isEmpty()) {
            repository.addLostItem(new LostItem(
                    "L-101", "Electronics", "Lenovo", "IdeaPad 3", "Black",
                    "University Library", LocalDate.of(2026, 9, 10),
                    "Black Lenovo laptop with blue sticker on top right corner",
                    ItemStatus.OPEN, "student1@univ.ac.in"
            ));
            repository.addFoundItem(new FoundItem(
                    "F-087", "Electronics", "Lenovo", "IdeaPad 3", "Black",
                    "Library Reading Room", LocalDate.of(2026, 9, 10),
                    "Black Lenovo notebook discovered with a blue sticker",
                    ItemStatus.OPEN, "Security Desk Desk A"
            ));
        }
    }
}