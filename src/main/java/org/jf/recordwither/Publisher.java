package org.jf.recordwither;

import java.time.LocalDate;

public class Publisher {
    private String name;
    private String country;

    public Publisher(String name, String country) {
        this.name = name;
        this.country = country;
    }

    public void doSomething() {
        record Novel(String title, Author author, Publisher publisher) {
        }

        var novel = new Novel("The Great Gatsby", new Author("F. Scott Fitzgerald", "American", LocalDate.of(1925, 4, 10)), this);
        System.out.println(novel);
    }
}
