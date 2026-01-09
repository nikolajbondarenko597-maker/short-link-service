package cli;

import service.LinkService;

import java.util.Scanner;

public class CommandLineInterface {
    private final LinkService linkService = new LinkService();
    private final Scanner scanner = new Scanner(System.in);

    public void start() {
        System.out.println("=== Служба сокращения ссылок ===");
        showHelp();

        while (true) {
            System.out.print("\nВведите команду: ");
            String input = scanner.nextLine().trim();

            if (input.isEmpty()) continue;

            String[] parts = input.split(" ", 3);
            String command = parts[0].toLowerCase();

            switch (command) {
                case "create":
                    handleCreate(parts);
                    break;
                case "go":
                    if (parts.length > 1) linkService.followLink(parts[1]);
                    else System.out.println("Укажите код ссылки.");
                    break;
                case "list":
                    linkService.listMyLinks();
                    break;
                case "delete":
                    if (parts.length > 1) linkService.deleteLink(parts[1]);
                    else System.out.println("Укажите код.");
                    break;
                case "help":
                    showHelp();
                    break;
                case "exit":
                    System.out.println("Выход...");
                    return;
                default:
                    System.out.println("Неизвестная команда. Введите 'help'.");
            }
        }
    }

    private void handleCreate(String[] parts) {
        if (parts.length < 3) {
            System.out.println("Использование: create <url> <limit>");
            return;
        }
        try {
            String url = parts[1];
            int limit = Integer.parseInt(parts[2]);
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                System.out.println("URL должен начинаться с http:// или https://");
                return;
            }
            linkService.createShortLink(url, limit);
        } catch (NumberFormatException e) {
            System.out.println("Лимит должен быть числом.");
        }
    }

    private void showHelp() {
        System.out.println("""
            Команды:
              create <url> <limit> — создать ссылку
              go <code>             — перейти по ссылке
              list                  — показать мои ссылки
              delete <code>         — удалить свою ссылку
              help                  — помощь
              exit                  — выход
            """);
    }
}