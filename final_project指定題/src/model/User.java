package model;

import java.time.LocalDate;

/**
 * User 類別：代表一個使用者帳號資料
 */
public class User {
    private String id; // 使用者ID
    private String email; // 使用者信箱
    private String password; // 使用者密碼
    private LocalDate birthday; // 使用者生日

    /**
     * 建構子，初始化使用者資料
     * 
     * @param id       使用者ID
     * @param email    使用者信箱
     * @param password 使用者密碼
     * @param birthday 使用者生日
     */
    public User(String id, String email, String password, LocalDate birthday) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.birthday = birthday;
    }

    /**
     * 取得使用者ID
     */
    public String getId() {
        return id;
    }

    /**
     * 取得使用者信箱
     */
    public String getEmail() {
        return email;
    }

    /**
     * 取得使用者密碼
     */
    public String getPassword() {
        return password;
    }

    /**
     * 取得使用者生日
     */
    public LocalDate getBirthday() {
        return birthday;
    }
}
