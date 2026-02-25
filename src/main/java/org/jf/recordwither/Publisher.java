package org.jf.recordwither;

import java.time.LocalDate;
import java.util.function.Consumer;

@Wither
public class Publisher {
    private String name;
    private String country;

    public Publisher(String name, String country) {
        this.name = name;
        this.country = country;
    }

    public void doSomething() {
        @Wither
        record Novel(String title, Author author, Publisher publisher) {
            // region @Wither — generated, do not edit manually
            public static class Wither {
                private String title;
                private Author author;
                private Publisher publisher;

                private Wither(Novel novel) {
                    title = novel.title;
                    author = novel.author;
                    publisher = novel.publisher;
                }

                public Novel.Wither title(String title) {
                    this.title = title;
                    return this;
                }

                public Novel.Wither author(Author author) {
                    this.author = author;
                    return this;
                }

                public Novel.Wither publisher(Publisher publisher) {
                    this.publisher = publisher;
                    return this;
                }

                private Novel apply() {
                    return new Novel(title, author, publisher);
                }
            }

            public Novel with(Consumer<Wither> consumer) {
                var wither = new Novel.Wither(this);
                consumer.accept(wither);
                return wither.apply();
            }
            // endregion @Wither
        }

        var novel = new Novel("The Great Gatsby", new Author("F. Scott Fitzgerald", "American", LocalDate.of(1925, 4, 10)), this);
        System.out.println(novel);
    }
}
