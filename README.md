# DFA-Regex ![](https://travis-ci.org/zbdzzg/DFA-Regex.svg?branch=master)

A DFA regex engine in java.

### Pom Settings
```xml
<dependency>
    <groupId>top.yatt.dfargx</groupId>
    <artifactId>dfargx</artifactId>
    <version>0.2.1</version>
</dependency>
```
### Introduction

This is a Java DFA regex engine implementation.

- High compatibility -- single jar lib, without 3rd dependencies
- High performance -- O(n) liner time complex, can be used for avoiding [ReDoS](https://en.wikipedia.org/wiki/ReDoS)

### Use Case
```java
RegexMatcher matcher = new RegexMatcher("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}");
boolean result = matcher.match("192.168.0.255"); // return true or false, should be true in this case.
```
### Performance Comparison With JDK Regex:

Below is showing a typical [ReDoS](https://en.wikipedia.org/wiki/ReDoS) attack input against a fragile regex pattern.

Regex | Input | Time Cost (Java Native) | Time Cost (DFA Regex)
:-----:|:------:|:-------------------------:|:------------:
(a\*)\*|aaaaaaaaaaaaaaaaab|42ms|12ms
(a\*)\*|aaaaaaaaaaaaaaaaaaaaaaab|311ms|1ms
(a\*)\*|aaaaaaaaaaaaaaaaaaaaaaaaaaaab|9579ms|1ms

### Supported Features:
- matching
- searching
- ascii character set
- `*`, `+`, `?`, `{x}`, `{x,y}`, `{x}`
- `.`, `\w`, `\W`, `\s`, `\S`, `\d`, `\D`
- complementary set `[^a]`
- escape characters
- brackets

### Todo List:
- [POSIX-Extended Regex](http://www.boost.org/doc/libs/1_44_0/libs/regex/doc/html/boost_regex/syntax/basic_extended.html) syntax support
- Liner time searching