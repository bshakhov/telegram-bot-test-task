package com.knubisoft.bshakhov.telegrambottesttask.dao.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "t_user")
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "chat_id", nullable = false)
    private Long chatId;

    @Column(name = "last_command", nullable = false)
    private String lastCommand;

    @Column(name = "update_frequency", nullable = false)
    private int updateFrequency;

    @Column(name = "changing_percent", nullable = false)
    private double changingPercent;


}
