# Clean Tests

This repo contains the code samples for my article on _clean tests_, which you can find on [my blog](https://unseenwizzard.github.io/2019/02/03/cleanTests.html) as well as further down in this readme. 

## Build steps
To build the source code in this repo you will need to have GoogleTest and can use use gradle: 

0) Install GoogleTest
1) Set the following environment variables `GTEST_DIR` and `GTEST_INCLUDE` to point to the main directory of googletest and the include directory respectively (e.g. `/mnt/d/Programming/googletest-release-1.8.1/googletest` and `/mnt/d/Programming/googletest-release-1.8.1/googletest/include`)
2) Clone the project
3) Run `gradle wrapper --gradle-version 4.10` in the projects main folder
4) Run `./gradlew build` to build and run the tests of both the Java and C++ version of the samples

Mainly this exists for you to have a look at the tests, the actual source code just exists to have something to test. 
So check out the [Java tests](https://github.com/UnseenWizzard/cleanTests/tree/master/javasample/src/test/java/cleanTests) (C++ version of the tests coming soon)

----
## Article

> This is adapted from an article I wrote for my colleagues at [incubedIT](http://www.incubedit.com/) after a sprint in which my team focused heavily on testing as we were implementing a large feature, that slightly changed many parts of the existing system. 
> We wanted to share some of the things we did with others, and I wanted to raise awareness of what makes a test useful.

> All the original code sample were specific examples taken from our source code. 
> For this version have been replaced with general examples. You can find a project of these examples on my [cleanTests github repo](https://github.com/UnseenWizzard/cleanTests), where I'll also put a C++ version using GoogleTest soon.

We all agree that having tests is a good thing. 

But we don't just want _more_ tests, we want _good_ tests!

What makes one test better than another though? 

The following is adapted from the _Clean Tests_ chapter _Robert C. Martin's Clean Code_ book. 

## You're an _Author_ Harry!
Readability is important when writing code, but when writing tests it's king. 

_Clean Code_ puts forth the notion that whenever we write software professionally, we are active as authors. 

We're never just hacking away at our keyboards to create code that compiles and does things, we're always writing something, that will be read by someone. 

Whoever reads your code, will be deeply thankful if he can _read_ it, instead of having to _decipher_ what it's actually doing. 
That may be other people who work on the same codebase in the future, and it may be ourselves a few months after we've last changed it. 

So picture yourself needing to work on, and maybe even having to refactor, code that you're not familiar with anymore. 

It has tests, which is a good start. 

It has lots of tests, which is even better. 

This must mean it's well covered and you can be sure about what you just accidentally broke with your latest change. 

But when you go ahead and make your change, you're hit with the sudden realization that things aren't as rosy as you thought.

You broke a test called `assignTask`. 
It calls the method it tests several times and asserts different things before, between and after those calls. 

You need to read and re-read the test and need to check the implementation to be sure what it wants to test and how you broke it. 

In the end one of the intermediate asserts isn't true anymore, but the end result is unaffected by you change. 

The first step we can take to make the life of others easier, and our tests more useful, is focusing on readability. 

Use descriptive names for your test method, as well as for the variables you use in the test.
Where you feel you don't manage to be descriptive with naming alone, leave a comment.

If the reason something failed isn't obvious from the assert output, consider adding a descriptive message.

Applying some of that to the test we just imagined you broke, you can go from 

```java
@Test
    public void assignTask() {
        assertFalse(assignment.hasTaskAssigned(executor));
        assertTrue(storage.getTasks().isEmpty());

        storage.add(new Task(1, "group_A"));
        storage.add(new Task(2, "group_A"));
        storage.add(new Task(1, "group_B"));
        storage.add(new Task(42, "group_C"));

        assertTrue(assignment.assignTaskIfPossible(executor).isPresent());

        Optional<Task> currentTask = assignment.getCurrentlyExecutedTask(executor);

        assertTrue(currentTask.isPresent());
        assertEquals(1, currentTask.get().id);
        assertEquals("group_A", currentTask.get().group);

        assertFalse(assignment.assignTaskIfPossible(executor).isPresent());

        assignment.finishCurrentTask(executor);

        assertTrue(assignment.assignTaskIfPossible(executor).isPresent());

        currentTask = assignment.getCurrentlyExecutedTask(executor);

        assertTrue(currentTask.isPresent());
        assertEquals(2, currentTask.get().id);
        assertEquals("group_A", currentTask.get().group);

        assignment.finishCurrentTask(executor);
        storage.remove(new Task(1, "group_B"));

        assertTrue(assignment.assignTaskIfPossible(executor).isPresent());

        currentTask = assignment.getCurrentlyExecutedTask(executor);

        assertTrue(currentTask.isPresent());
        assertEquals(42, currentTask.get().id);
        assertEquals("group_C", currentTask.get().group);
    }
```
to

```java
@Test
public void executorWithoutGroupGetsFirstAvailableTask() {
    storage.add(TestTasks.GROUP_A_TASK1).add(TestTasks.GROUP_B_TASK1);

    assignment.assignTaskIfPossible(DEFAULT_EXECUTOR);

    assertThat(assignment.getCurrentlyExecutedTask(DEFAULT_EXECUTOR), equalTo(Optional.of(TestTasks.GROUP_A_TASK1)));
}
```

So what changes did I make to this test? 

Obviously I've reduced it heavily, we'll talk more about that in the next section, where we'll see that I actually split it into several individual tests.

While the original test was already somewhat following the same principle, I used spacing to clearly split the test into a `Given, When, Then` structure. First I set up the storage with the basic data `given` for the test, and `when` I try to assign a task, `then` I expect it to be the first available one. 

While not the case in the sample, the original test this was written with made use of Strings that needed to match something used in another test utility class. There as well as in the sample, I've made the test data accessible as a static and well named field, to both aid redability and make future tests as well as changes less error prone. 
Imagine random tests failing, because you've changed or extended something in a utility they happen to use!

As already noted above, I've removed a lot of asserts!  

While some of them have moved into their own testcases, there were two assert basically ensuring that we started from a clean slate, testing that there are no tasks before we start adding them, and that nothing is assigned to the Executor yet. 
I trust the setup method that runs before each test in the class this is from, when it replaces objects and calls several `cleanup()` methods. 

The developer that came before me did not. He added the same calls to `cleanup()` in a method run after every test. And in a few tests. And checked that things were really gone in the setup method and many of the individual tests. 

> You can compare the tests, as well as the setup methods by having a look at [BadTaskAssignmentTest.java](https://github.com/UnseenWizzard/cleanTests/blob/master/javasample/src/test/java/cleanTests/BadTaskAssignmentTest.java) and [CleanTaskAssignmentTest.java](https://github.com/UnseenWizzard/cleanTests/blob/master/javasample/src/test/java/cleanTests/CleanTaskAssignmentTest.java)

## One Concept Per Test - The SRP of Tests

Your tests are now well written, easy to read and understand. 

Still there's more you can do. 

The Single Responsibility Principle applies to testcases as well. 
Aim for small tests, that cover one specific case. 
Those are easier to write and read, and when they fail we can be sure which exact case isn't working. 

Let's look back to the test from before, and asserts I've removed.
The `Bad` test also asserts that an Executor that is currently assigned a Task is not getting another, and that it is getting another one, after it has finished the currently assigned one. 

Those are reasonable tests. And they both test rather specific cases well worth testing on their own in a small, readable and easy to comprehend test. 

```Java
@Test
public void executorWithoutGroupGetsFirstAvailableTask() { ... }

@Test
public void executorCurrentlyExecutingATaskDoesNotGetAnother() {
    storage.add(TestTasks.GROUP_A_TASK1);

    assignment.assignTaskIfPossible(DEFAULT_EXECUTOR);

	assertThat(assignment.assignTaskIfPossible(DEFAULT_EXECUTOR), equalTo(empty()));
}

@Test
public void busyExecutorIsAvailableForTasksAgainAfterFinishingCurrentTask() {
    storage.add(TestTasks.GROUP_A_TASK1);
    assignment.assignTaskIfPossible(DEFAULT_EXECUTOR);

    assertFalse(assignment.availableForAssignment(DEFAULT_EXECUTOR));

    assignment.finishCurrentTask(DEFAULT_EXECUTOR);

    assertTrue(assignment.availableForAssignment(DEFAULT_EXECUTOR));
}
```

## F.I.R.S.T Principle
Robert C. Martin also describes how _clean tests_ should additionally follow the **F.I.R.S.T.** principle, which means they should be: 

**Fast** Test should run quickly. If they're fast you'll run them often, if they're slow you wont. In the time an integration test takes just to bring up a Spring Application, several well written unit tests will be done.

**Independent** Test shouldn't depend on other tests. You need to be able to run tests on their own and in any order. That also means that test should clean up after themselves if necessary. 

**Repeatable** Test should be repeatable. If they pass once on your laptop they should again. And they should behave the same on the CI system, my laptop and anywhere else. 

**Self-Validating** Tests should have a boolean result. Either they pass, or they fail. A test shouldn't be something that requires you to manually check a log-file after running it, as that might make the decision subjective.

**Timely** Tests should be written _just before_ the production code they test (TDD). As you know, when adding tests to existing code, you might find the code is hard to test, or designed in a way that just doesn't allow some tests. Defining your tests right before you start implementation, forces you to write code that is easy to test.

## What can help us write clean tests? 

### TDD
As a concept Test Driven Development enforces writing more and better tests.

To quickly recap the definition given in _Clean Code_, TDD has you follow three laws:

1) Don't write production code before you've written a failing unit test.
2) Don't write more of a unit test than is needed to fail. Not compiling is failing.
3) Don't write more production code than is needed to pass the current failing test.

This locks you into a cycle of incrementally extending your tests and code.

You have to write your test before you know the code you are testing, so you _have to_ test the general concepts, and will most likely write more _descriptive_ than specific test. 

### Java: JUnit, Hamcrest & Mockito

Java offers several great frameworks that can help with making your tests better and simpler than just `jUnit` alone.

[Hamcrest](http://hamcrest.org/JavaHamcrest/tutorial) is a framework that allows you to create (and use existing) 'matchers', which is especially useful when you want to `assert` that certain rules hold.  

[Mockito](https://site.mockito.org/) is a very powerful mocking framework, that allows you to mock and verify interactions with objects to make your integration tests easier. 

> Whenever you feel like you'll need to do something like starting a whole application using Spring for your integration, because of a daunting list of autowired components, consider using Mockito to mock all those objects, and define exactly how those components react when they're called in your test. You prefer fast unit tests for single concepts over huge, slow and tighlty coupled integration tests after all. 

The [CleanTaskAssignmentTest.java](https://github.com/UnseenWizzard/cleanTests/blob/master/javasample/src/test/java/cleanTests/CleanTaskAssignmentTest.java) uses _Hamcrest_ matchers. 

The [MockitoTaskAssignmentTest.java](https://github.com/UnseenWizzard/cleanTests/blob/master/javasample/src/test/java/cleanTests/MockitoTaskAssignmentTest.java) does exactly the same as the `Clean` test, but it additionally uses _Mockito_ to mock the `TaskStorage`.

Especially in the case of these small tests, mocking is not necessary, and when you run the test you'll notice that the one using _Mockito_ takes about _four_ times as long as the version without it. As we want fast tests, we strive to not use _Mockito_ where we don't have to. 

Additionally there's [some good arguments](https://medium.com/javascript-scene/mocking-is-a-code-smell-944a70c90a6a) that with good design you shouldn't need to mock things too ofter. 

### C++: Gtest & GoogleMock 

For testing our C++ code, we use [GoogleTest](https://github.com/google/googletest). 

GoogleTest ships with a mocking framework "inspired by jMock, EasyMock and Hamcrest" which is called [GoogleMock](https://github.com/google/googletest/tree/master/googlemock). 

Spending most of my working time in Java, I haven't gotten to try GoogleMock yet, but colleagues tell me it's nice, yet not as nice as what the Java frameworks can do thanks to reflection and how clean they are to write thanks to annotations. 

At the time of publishing this, I've just started on 'translating' the sample project into C++ in order to learn more about _GoogleTest_ and _Mock_, but you should find C++ samples on GitHub in the future. 


