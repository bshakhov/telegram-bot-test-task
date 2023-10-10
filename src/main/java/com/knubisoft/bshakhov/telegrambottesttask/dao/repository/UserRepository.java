package com.knubisoft.bshakhov.telegrambottesttask.dao.repository;

import com.knubisoft.bshakhov.telegrambottesttask.dao.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByChatId(Long chatId);

}
