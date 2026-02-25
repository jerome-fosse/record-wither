package org.jf.recordwither;

import java.util.function.Consumer;

public record Book(String title, Author author, Integer year) {

    // region @Wither — generated, do not edit manually
    public static class Wither {
        private String title;
        private Author author;
        private Integer year;

        private Wither(Book book) {
            title = book.title;
            author = book.author;
            year = book.year;
        }

        public Book.Wither title(String title) {
            this.title = title;
            return this;
        }

        public Book.Wither author(Author author) {
            this.author = author;
            return this;
        }

        public Book.Wither year(Integer year) {
            this.year = year;
            return this;
        }

        private Book apply() {
            return new Book(title, author, year);
        }
    }

    public Book with(Consumer<Wither> consumer) {
        var wither = new Book.Wither(this);
        consumer.accept(wither);
        return wither.apply();
    }
    // endregion @Wither
}
