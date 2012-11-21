java-alphanum
=============

A `Comparator` that sorts alphanumeric strings in a natural order - numbers are
sorted numerically and other text is sorted by a given comparator.

Sample code
-----------

    Comparator<String> comparator = new AlphanumericComparator(Collator.getInstance(Locale.ENGLISH));
    
    // equality
    assertEquals(0, comparator.compare("", ""));
    assertEquals(0, comparator.compare("abc", "abc"));
    assertEquals(0, comparator.compare("123", "123"));
    assertEquals(0, comparator.compare("abc123", "abc123"));
    
    // empty strings < non-empty
    assertTrue(comparator.compare("", "abc") < 0);
    assertTrue(comparator.compare("abc", "") > 0);
    
    // numbers < non numeric
    assertTrue(comparator.compare("123", "abc") < 0);
    assertTrue(comparator.compare("abc", "123") > 0);
    
    // numbers ordered numerically
    assertTrue(comparator.compare("2", "11") < 0);
    assertTrue(comparator.compare("a2", "a11") < 0);
    
    // leading zeroes
    assertTrue(comparator.compare("02", "11") < 0);
    assertTrue(comparator.compare("02", "002") < 0);
    
    // decimal points ... 
    assertTrue(comparator.compare("1.3", "1.5") < 0);
    
    // ... don't work too well
    assertTrue(comparator.compare("1.3", "1.15") < 0);
	
Maven
-----

Add a `repository` to your `pom.xml`:

    <repositories>
      ...
      <repository>
        <id>amjjd-releases</id>
        <url>https://github.com/amjjd/amjjd-mvn-repo/raw/master/releases/</url>
      </repository>
      ...
    </repositories>

... and a `dependency`:

    <dependencies>
      ...
      <dependency>
        <groupId>com.amjjd</groupId>
        <artifactId>java-alphanum</artifactId>
        <version>0.1</version>
        <scope>compile</scope>
      </dependency>
      ...
    </dependencies>

License
-------

[Apache 2.0](http://www.apache.org/licenses/LICENSE-2.0.html)

