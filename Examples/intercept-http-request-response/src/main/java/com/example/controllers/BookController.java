package com.example.controllers;

import com.example.entities.Book;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.annotation.RequestScope;

@RequestScope
@RestController
public class BookController {
    @GetMapping("/")
    public Book getBook() {
        return new Book(1, "Bible", 49.90f);
    }

    @PostMapping("/")
    public Book postBook() {
        var a = 10;
        var b = 0;
        var c = a / b;
        return new Book(1, "Bible", 49.90f);
    }

    @GetMapping("/get/book/{book}")
    public Book getBookByPath(@PathVariable String book) {
        System.out.println("Book: " + book);
        return new Book(1, "Bible", 49.90f);
    }

}
