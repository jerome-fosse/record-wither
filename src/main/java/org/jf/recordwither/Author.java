package org.jf.recordwither;

import java.time.LocalDate;
import java.util.function.Consumer;

//@WitherIgnore
public record Author(String name, String nationality, LocalDate dateOfBirth) {

    // region @Wither — generated, do not edit manually
    public static class Wither {
        private String name;
        private String nationality;
        private LocalDate dateOfBirth;

        private Wither(Author author) {
            name = author.name;
            nationality = author.nationality;
            dateOfBirth = author.dateOfBirth;
        }

        public Author.Wither name(String name) {
            this.name = name;
            return this;
        }

        public Author.Wither nationality(String nationality) {
            this.nationality = nationality;
            return this;
        }

        public Author.Wither dateOfBirth(LocalDate dateOfBirth) {
            this.dateOfBirth = dateOfBirth;
            return this;
        }

        private Author apply() {
            return new Author(name, nationality, dateOfBirth);
        }
    }

    public Author with(Consumer<Wither> consumer) {
        var wither = new Author.Wither(this);
        consumer.accept(wither);
        return wither.apply();
    }
    // endregion @Wither
}
