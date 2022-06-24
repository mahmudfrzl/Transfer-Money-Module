package org.fm.moneytransfer.model;

import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.math.BigDecimal;
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "accounts")
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@Accessors(chain = true)
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
     Long id;
     String accountNumber;
     String accountName;
     BigDecimal balance;

}