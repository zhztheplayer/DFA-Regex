# DFA-Regex

A pretty fast regex engine built for java using pure DFA.

### That is probably why you need this lib:

- You have just few regexes but a huge number or even infinite texts to match;
- You don't need look-back and capture group syntax in you regexes;
- You are using java and really care about the regex performance;

### Here is a simple case:
```java
RegexMatcher matcher = new RegexMatcher("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}");
boolean result = matcher.match("192.168.0.255");
```
### Performance comparison with JDK Regex:

|           | Compiling time | 1,000 matches | 100,000 matches | 10,000,000 matches |
|:---------:|:--------------:|:-------------:|:---------------:|:------------------:|
| DFA-Regex |     183 ms     |      4 ms     |      70 ms      |       6206 ms      |
| JDK-Regex |      0 ms      |     33 ms     |      259 ms     |      12642 ms      |
*test using pattern: \d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3} - - \[[^\]]+\] "[^"]+" \d+ \d+ "[^"]+" "[^"]+"*

By the way, DFA engine has it own special way in compiling part, you can just write regex and use all tokens whatever you want without caring about the performance. Because the performances are always the same to different regexes under a DFA engine.
### These features are allowed now:
- matching
- searching
- ascii character set
- *, +, ?, {x}, {x,y}, {x}
- ., \w, \W, \s, \S, \d, \D
- complementary set like "[^a]"
- escape characters
- brackets

### These syntaxes are not and might be never allowed in this lib:
- look back, such as "(?<=)" and "(?<!)";
- capture groups;
- some zero width tokens, such as "\b";

### These features may be supported in later versions:
- look forward, such as "(?=)";
- anchor points "^", "$";
- range set such as "[0 - 9]"