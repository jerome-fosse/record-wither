package org.jf.recordwither;

import java.time.LocalDate;

public class Test {
    public static void main(String[] args) {
        var author = new Author("John Doe", "French", LocalDate.of(1980, 1, 1));
        var newAuthor = author.with(a -> a.nationality("English"));

        System.out.println(author);
        System.out.println(newAuthor);
    }
}
