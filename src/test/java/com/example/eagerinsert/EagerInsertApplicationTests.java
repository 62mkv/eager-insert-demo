package com.example.eagerinsert;

import com.example.eagerinsert.model.Person;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThatCode;

@SpringBootTest
class EagerInsertApplicationTests {

    @Autowired
    R2dbcEntityTemplate template;

    @Test
    void contextLoads() {
        Person person = new Person(1L, "My Name");
        StepVerifier.create(getOrInsert(person))
                .expectNext(person)
                .expectComplete();
    }

    @NotNull
    private Mono<Person> getOrInsert(Person person) {
        return Mono.justOrEmpty(person)
                // this PASSES despite the fact we don't even have "person" table defined anywhere,
                // because "insert result" Mono is never subscribed to, so the datasource
                // is not even instantiated
                .switchIfEmpty(template.insert(person));
    }

}
