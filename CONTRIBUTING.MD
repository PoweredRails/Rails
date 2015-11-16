# Contributing

We'll always need developers to help us improve Rails. If you're a developer and willing to help out then please don't hestitate -- just make sure you follow our guidelines!

When contributing code to the repository you'll want to create a pull-request, and you may need to create a branch.
If you're adding a new feature, use the naming scheme `feature/name`. Note: the name should be descriptive, but not
excessively. For example: `feature/fix-inventories`.
If you're implementing a fix to an issue, use the scheme `issue/id` -- where id is the id of the GitHub issue.
Quick fixes that require only one commit and only change perhaps a line or two may be done directly on the `dev` branch.
However, if these changes are likely to cause merge issues, ensure you create a new branch.

Overall, our guidelines strongly follow those of [Google's Java Style Guidelines](https://google-styleguide.googlecode.com/svn/trunk/javaguide.html) with a few modifications.

## Modifications & Comments
* Line endings
  * Use Unix line endings when committing (\n)
    * Windows users of Git can do: `git config --global core.autocrlf true`
* Column width
  * 80 for Javadocs
  * 150 for code
  * Feel free to wrap when it will help with readability
* Import order
  * Use Google’s, You will have to configure this in your IDE.
    * IntelliJ IDEA: `Settings → Project Settings → Code Style → Java → Imports`
    * NetBeans: `Options → Editor → Formatting → Java → Imports`
    * Eclipse: `Preferences → Java → Code Style → Organize Imports`
* Indentation
  * Use only four spaces for indentations, do not use two spaces
* Vertical whitespace
  * Place a blank line before the first member of a class (i.e. after `class Example {`)
  * A line after the last member of a class is encouraged but not required
  * Applies to interfaces, enums, etc. as well
* File headers
  * File headers must be surrounded in a block comment (`/* */`) and follow the style in other code files.
* Exceptions
  * For exceptions that are to be ignored, name the exception variable ignored or ignore
* Javadocs
  * Do NOT use `@author`
  * End paragraphs with `</p>`
  * Capitalize the first letter in the descriptions within each "at clause", i.e. `@param name Player to affect`

## Code Conventions
* Annotation usage:
  * All methods that return null MUST be annotated with `@Nullable` (from javax.*)
  * All method parameters that accept null MUST be annotated with `@Nullable`
  * All implemented or overridden methods MUST be annotated with `@Override`
* For parameters, make them final by default unless they are going to be modified

## Examples
It is recommended that you read Google's Java conventions, due to being two very long documents here is some quick start properly formatted example code

``` java
/*
 * Apache Licensing
 */

package com.example.java;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * An Example class with proper formatting
 */
public class Example {

    private static final Logger logger = Logger.getLogger(Example.class);
    private static final Random random = new Random();
    private final List<String> names = new ArrayList<String>(Arrays.asList("Adam", "Bob"));

    /**
     * Returns a random name from a given list.
     *
     * <p>
     * A static instance of {@link Random} is used to generate a random
     * number with the maximum number being the size of the list
     * outcome with a 50% chance. If the specified list is {@code null},
     * the defaultName will be returned
     * </p>
     *
     * @param list An optional list of names which the method will
     *        get the name from
     * @return A random name from the specified list or the default name
     */
    @Nullable
    public String getRandomName(@Nullable List<Strings> list) {
        String defaultName  = "Sam"
        logger.log(Level.INFO, "Random name requested");

        if (list != null) {
            int index = random.nextInt(list.size())
            return list.get(index);
        } else {
            return defaultName;
        }
    }
}
```

Heres an example of a properly formatted code in an interface:

``` java
/*
 * Apache Licensing
 */

package com.example.java;

/**
 * An Example interface with proper formatting
 */
public interface Example extends AnotherInterface, ExampleInterface {

    /**
     * Gets the uuid of the {@link Entity}.
     *
     * @param entity The entity
     * @return The entity uuid
     */
    String getName(Entity entity);

    /**
     * Checks to see if {@link World} contains an {@link Entity}.
     *
     * @return true If world contains the specified entity
     * @throws InvalidWorldException The error caused when a world
     *         does not exist
     */
    boolean containsEntity(World world, Entity entity) throws InvalidWorldException;

    /**
     * Set the health amount.
     *
     * <p>The range of the health depends on the object on which this
     * method is defined. For players in Minecraft, the nominal range is
     * between 0 and 20, inclusive, but the range can be adjusted.</p>
     *
     * <p>Convention dictates that health does not follow below 0 but this
     * convention may be broken.</p>
     *
     * @param health The health to set to
     */
    void setHealth(double health);

}
```
