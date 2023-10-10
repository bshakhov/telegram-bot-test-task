package com.knubisoft.bshakhov.telegrambottesttask.dao.repository;

import com.knubisoft.bshakhov.telegrambottesttask.dao.entity.Crypto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CryptoRepository extends JpaRepository<Crypto, Long> {
}
