package ru.clevertec.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Customer {
    private UUID id;
    private String firstName;
    private String lastName;
    private OffsetDateTime dateBirth;
    private List<Order> orders;
}
