package ru.otus.crm.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "client")
public class Client implements Cloneable {

    @Id
    @SequenceGenerator(name = "client_gen", sequenceName = "client_seq",
            initialValue = 1, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "client_gen")
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @OneToOne(mappedBy = "client", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Address address;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Phone> phones;

    public Client(String name) {
        this.id = null;
        this.name = name;
    }

    public Client(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Client(String name, Address address, List<Phone> phones) {
        this(null, name, address, phones);
    }

    public Client(Long id, String name, Address address, List<Phone> phones) {
        this.id = id;
        this.name = name;
        this.address = address;
        if (Objects.nonNull(address)) {
            this.address.setClient(this);
        }
        this.phones = phones;
        if (Objects.nonNull(phones)) {
            this.phones.stream()
                    .filter(Objects::nonNull)
                    .forEach(phone -> phone.setClient(this));
        }
    }

    @Override
    public Client clone() {
        var newAddress = Optional.ofNullable(this.address)
                .map(Address::clone)
                .orElse(null);

        var newPhones = Objects.isNull(this.phones) ? null :
                this.phones.stream()
                        .map(phone -> Optional.ofNullable(phone)
                                .map(Phone::clone)
                                .orElse(null))
                        .toList();
        return new Client(this.id, this.name, newAddress, newPhones);
    }

    @Override
    public String toString() {
        return "Client{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
