# MJT-26 — Modern Java Technologies

This repository holds my labs, homework, exam practice and final project for
**Modern Java Technologies** (Съвременни Java технологии). It is an elective
course at the Faculty of Mathematics and Informatics (FMI), Sofia University
"St. Kliment Ohridski", and this is the 2025/2026 edition.

The course covers the latest version of the Java language and platform, along
with some of the libraries, tools and technologies most used in practice. It
puts particular emphasis on **automated testing, good design and clean code**.

## Course topics

1. **Introduction to Java**: language basics, types and operators, control
   flow, arrays, strings, tools (IDE, `javac`, `java`, `jshell`, Maven)
2. **OOP with Java**: classes, objects, abstract classes, interfaces, packages
3. **OOP with Java, continued**: static members, enums, wrapper types, `Object`,
   `equals`/`hashCode`, anonymous classes, exceptions
4. **Data structures**: the Collections framework (lists, sets, maps),
   iterators, generics
5. **Good design and clean code**: SOLID and design patterns (Factory,
   Builder, Singleton, Adapter, Proxy, Façade, Decorator, Observer, Command,
   Visitor, Iterator)
6. **Automated testing**: unit, functional and integration tests, JUnit,
   Mockito
7. **Input and output**: streams and handling exceptions when working with them
8. **File system**: `Path`, `File`, archive files
9. **Functional Java**: lambdas, functional interfaces, Stream API
10. **Concurrency**: threads, synchronization, wait/notify, executors, atomics,
    concurrent collections
11. **Network programming**: TCP/UDP, client-server, blocking and non-blocking
    I/O (`java.net`, `java.nio`)
12. **Web basics**: HTTP, REST, JSON, `HttpClient`
13. **The Java platform**: the JVM, memory organization and management

## Projects

The projects are listed in the order they were written. Each folder is a
standalone IntelliJ IDEA project with its code under `src/` and, where there
are tests, under `test/`. Most of the code is in the
`bg.sofia.uni.fmi.mjt.*` packages.

| Folder | Type | What it is | Main topics |
|---|---|---|---|
| [`test`](test) | Setup | Hello-world project for checking the environment | Tooling |
| [`week_1`](week_1) | Lab | Warm-up algorithm problems (task distribution, unique substrings) | Language basics, arrays, strings |
| [`lab2`](lab2) | Lab | Dating-show simulator with contestants and elimination rules | OOP, interfaces, polymorphism |
| [`lab3`](lab3) | Lab | Semester planner for CS and SE students | Abstract classes, enums, custom exceptions |
| [`lab4`](lab4) | Lab | Fitness workout planner | Collections, comparators, sorting |
| [`lab5`](lab5) | Lab | Event bus with subscribers and priorities | Generics, collections, design |
| [`lab6`](lab6) | Lab | File-processing pipeline with stages, steps and a cache | Design patterns, JUnit |
| [`hw1`](hw1) | Homework 1 | **JobMatch**: matches candidates to job postings and recommends skills | Collections, comparators, exceptions, testing |
| [`week7`](week7) | Lab | **ImageKit**: grayscale and Sobel edge detection on images | File system, I/O, JUnit, Mockito |
| [`week8`](week8) | Lab | Analysis of a sales-order dataset from a CSV file | Stream API, lambdas, testing |
| [`week9`](week9) | Lab | Image steganography with producers and consumers | Concurrency, synchronization |
| [`hw2`](hw2) | Homework 2 | **Space Scanner**: statistics on rocket launches and missions from CSV data, with Rijndael (AES) encryption | Streams, I/O, cryptography, testing |
| [`izpitvane_2`](izpitvane_2) | Exam practice | Ping-pong server and a song-lyrics retriever | Network programming |
| [`hw_3`](hw_3) | Homework 3 | **NewsFeed**: client for a REST news API, with paging and error handling | HTTP, REST, JSON (Gson), `HttpClient`, Mockito |
| [`final_project`](final_project) | Course project | **Auth server**: a multi-client authentication server with user registration, login, sessions, admin roles, password hashing and audit logging | NIO (`Selector`, `SocketChannel`), Command pattern, Gson, cryptography, JUnit/Mockito |

## Running a project

Requires a recent JDK. Some projects use preview features such as instance
`main` methods and `IO.println`.

1. Open a project folder (for example `hw2`) in IntelliJ IDEA as its own
   project.
2. Set the project SDK.
3. Projects with tests need **JUnit 5**, and some also need **Mockito** and
   **Gson**. Add these as libraries (IntelliJ can fetch them from Maven).
4. Run `Main` or the tests from the IDE.

IntelliJ configuration files (`.idea/`, `*.iml`) and build output (`out/`) are
not committed.
