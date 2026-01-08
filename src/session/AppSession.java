package session;

import Model.User;

public class AppSession {

    private static User currentUser;

    private AppSession() {}

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void login(User user) {
        currentUser = user;
    }

    public static void logout() {
        currentUser = null;
    }

    public static boolean isLoggedIn() {
        return currentUser != null;
    }
}

