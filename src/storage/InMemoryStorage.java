package storage;

import model.ShortLink;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryStorage {
    private final Map<String, ShortLink> links = new ConcurrentHashMap<>();
    private final Map<UUID, List<String>> userLinks = new HashMap<>();

    public synchronized void save(ShortLink link) {
        links.put(link.getShortCode(), link);
        userLinks.computeIfAbsent(link.getOwnerId(), k -> new ArrayList<>()).add(link.getShortCode());
    }

    public Optional<ShortLink> findByCode(String code) {
        return Optional.ofNullable(links.get(code));
    }

    public List<ShortLink> getUserLinks(UUID userId) {
        List<String> codes = userLinks.getOrDefault(userId, Collections.emptyList());
        List<ShortLink> result = new ArrayList<>();
        for (String code : codes) {
            Optional<ShortLink> link = findByCode(code);
            if (link.isPresent()) {
                result.add(link.get());
            }
        }
        return result;
    }

    public boolean deleteByCodeAndOwner(String code, UUID ownerId) {
        ShortLink link = links.get(code);
        if (link != null && link.getOwnerId().equals(ownerId)) {
            links.remove(code);
            userLinks.get(ownerId).remove(code);
            return true;
        }
        return false;
    }

    public void cleanupExpired() {
        List<String> expired = new ArrayList<>();
        for (ShortLink link : links.values()) {
            if (link.isExpired() || link.isLimitReached()) {
                expired.add(link.getShortCode());
            }
        }
        for (String code : expired) {
            ShortLink link = links.get(code);
            if (link.isExpired()) {
                System.out.printf("Ссылка %s удалена: истёк срок жизни.\n", code);
            } else if (link.isLimitReached()) {
                System.out.printf("Ссылка %s удалена: достигнут лимит переходов.\n", code);
            }
            links.remove(code);
        }
    }
}