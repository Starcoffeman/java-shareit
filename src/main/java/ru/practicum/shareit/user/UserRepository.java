package ru.practicum.shareit.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.user.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);

    @Modifying
    @Query("UPDATE User u SET u.name = :name, u.email = :email WHERE u.id = :userId")
    void updateUser(@Param("userId") long userId, @Param("name") String name, @Param("email") String email);

    @Modifying
    @Query("DELETE FROM User u WHERE u.id = :userId")
    void deleteUserById(@Param("userId") long userId);

    @Query("SELECT MAX(u.id) FROM User u")
    Long getMaxUserId();
}