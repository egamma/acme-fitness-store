package com.vmware.acme.catalog;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Testcontainers
@DataJpaTest(excludeFilters = @ComponentScan.Filter(MetricsConfiguration.class))
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ProductRepositoryTests {

    @Container
    private static final PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:14.4-alpine3.16");

    @Autowired
    private ProductRepository productRepository;

    @DynamicPropertySource
    static void sqlserverProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Test
    void findAll() {
        var products = this.productRepository.findAll();
        assertThat(products).hasSize(8);
    }

    @Test
    void findById() {
        var product = this.productRepository.findById("533445d-530e-4a76-9398-5d16713b827b");
        assertThat(product).hasValueSatisfying(p -> {
            assertThat(p.getDescription()).isEqualTo("Magic Yoga Mat!");
            assertThat(p.getImageUrl1()).isEqualTo("/static/images/yogamat_square.jpg");
            assertThat(p.getImageUrl2()).isEqualTo("/static/images/yogamat_thumb2.jpg");
            assertThat(p.getImageUrl3()).isEqualTo("/static/images/yogamat_thumb3.jpg");
        });
    }

    void validateSupplierPhoneNumber(String phoneNumber) {
        // TODO validate the phone number
        // regex pattern for phone number
        String regex = "^(\\+\\d{1,3}[- ]?)?\\d{10}$";
        // compile regex pattern
        Pattern pattern = Pattern.compile(regex);
        // match the given phone number with regex pattern
        Matcher matcher = pattern.matcher(phoneNumber);
        // if the phone number is not valid
        if (!matcher.matches()) {
            // throw an exception
            throw new IllegalArgumentException("Invalid phone number");
        }
        
    }
}
