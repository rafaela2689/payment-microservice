package com.wefox.assessment.core.account;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@Entity
@Table(name = "accounts")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Integer accountId;

    private String name;

    @NotNull(message = "Email is mandatory.")
    private String email;

    private Date birthdate;

    private LocalDateTime lastPaymentDate;

    private LocalDateTime createdOn;
}
