package service;

import java.util.UUID;

public class UserService {
    private UUID currentUser;

    public UUID getCurrentUser() {
        if (currentUser == null) {
            currentUser = UUID.randomUUID();
            System.out.println("Ваш UUID: " + currentUser);
            System.out.println("Он будет использоваться для управления вашими ссылками.");
        }
        return currentUser;
    }
}