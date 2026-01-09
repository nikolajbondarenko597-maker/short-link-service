package service;

import model.ShortLink;
import storage.InMemoryStorage;
import utils.CodeGenerator;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class LinkService {
    private final InMemoryStorage storage = new InMemoryStorage();
    private final UserService userService = new UserService();

    public String createShortLink(String url, int visitLimit) {
        UUID owner = userService.getCurrentUser();
        String code = CodeGenerator.generate(config.Config.getLinkLength());

        while (storage.findByCode(code).isPresent()) {
            code = CodeGenerator.generate(config.Config.getLinkLength());
        }

        ShortLink link = new ShortLink(
                code,
                url,
                owner,
                visitLimit,
                config.Config.getDefaultTTL()
        );

        storage.save(link);

        System.out.printf("Ссылка создана: clck.ru/%s\n", code);
        System.out.printf("Оригинал: %s\n", url);
        System.out.printf("Лимит переходов: %d\n", visitLimit);
        System.out.printf("Жизнь: %.1f дней\n", config.Config.getDefaultTTL() / 86400.0);

        return code;
    }

    public void followLink(String code) {
        Optional<ShortLink> found = storage.findByCode(code);
        if (found.isEmpty()) {
            System.out.println("Ссылка не найдена.");
            return;
        }

        ShortLink link = found.get();

        if (link.isExpired()) {
            System.out.println("Срок действия ссылки истёк.");
            storage.cleanupExpired(); // попробуем удалить
            return;
        }

        if (link.isLimitReached()) {
            System.out.println("Лимит переходов исчерпан.");
            return;
        }

        link.incrementVisits();
        System.out.printf("Переход по ссылке (%d/%d)\n", link.getCurrentVisits(), link.getVisitLimit());

        try {
            java.awt.Desktop.getDesktop().browse(new java.net.URI(link.getOriginalUrl()));
        } catch (Exception e) {
            System.out.println("Не удалось открыть браузер: " + e.getMessage());
        }

        if (link.isLimitReached()) {
            System.out.println("Лимит достигнут. Ссылка будет удалена.");
            storage.cleanupExpired();
        }
    }

    public void listMyLinks() {
        UUID owner = userService.getCurrentUser();
        List<ShortLink> links = storage.getUserLinks(owner);
        if (links.isEmpty()) {
            System.out.println("У вас пока нет ссылок.");
            return;
        }
        System.out.println("\n=== Ваши ссылки ===");
        for (ShortLink link : links) {
            String status = link.isExpired() ? "истекла" :
                    link.isLimitReached() ? "лимит исчерпан" :
                            "активна";
            System.out.printf("clck.ru/%s → %s [%s]\n", link.getShortCode(), status, link.getOriginalUrl());
        }
    }

    public void deleteLink(String code) {
        UUID owner = userService.getCurrentUser();
        if (storage.deleteByCodeAndOwner(code, owner)) {
            System.out.println("Ссылка удалена.");
        } else {
            System.out.println("Ссылка не найдена или вы не владелец.");
        }
    }
}