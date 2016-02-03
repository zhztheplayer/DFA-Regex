# DFA-Regex

A pretty fast regex engine built for java using pure DFA.

### Pom Settings
```xml
<dependency>
    <groupId>top.yatt.dfargx</groupId>
    <artifactId>dfargx</artifactId>
    <version>0.2</version>
</dependency>
```

### When to Use This Lib:

- Texts to match are massive
- Look-back or capture groups are not needed
- Performance is highly concerned

### Example
```java
RegexMatcher matcher = new RegexMatcher("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}");
boolean result = matcher.match("192.168.0.255");
```
### Performance Comparison With JDK Regex:

|           | Compiling time | 1,000 matches | 100,000 matches | 10,000,000 matches |
|:---------:|:--------------:|:-------------:|:---------------:|:------------------:|
| DFA-Regex |     183 ms     |      4 ms     |      70 ms      |       6206 ms      |
| JDK-Regex |      0 ms      |     33 ms     |      259 ms     |      12642 ms      |
*test using pattern:* `\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3} - - \[[^\]]+\] "[^"]+" \d+ \d+ "[^"]+" "[^"]+`

Matching is always expected to be completed in O(n) time using this kind of engine.

### Supported Features:
- matching
- searching
- ascii character set
- `*`, `+`, `?`, `{x}`, `{x,y}`, `{x}`
- `.`, `\w`, `\W`, `\s`, `\S`, `\d`, `\D`
- complementary set `[^a]`
- escape characters
- brackets

### Features Not Allowed Yet:
- look back `(?<=)` `(?<!)`
- capture groups
- some zero width tokens `\b`
- looking forward `(?=)`
- anchor points `^` `$`
- ranged set `[0-9]`