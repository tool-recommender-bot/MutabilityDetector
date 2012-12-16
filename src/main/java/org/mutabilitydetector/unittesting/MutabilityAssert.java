/*
 *    Copyright (c) 2008-2011 Graham Allan
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

package org.mutabilitydetector.unittesting;

import static org.mutabilitydetector.ConfigurationBuilder.OUT_OF_THE_BOX_CONFIGURATION;

import java.util.Collections;
import java.util.List;

import org.hamcrest.Matcher;
import org.mutabilitydetector.AnalysisResult;
import org.mutabilitydetector.Configuration;
import org.mutabilitydetector.ConfigurationBuilder;
import org.mutabilitydetector.IsImmutable;
import org.mutabilitydetector.MutabilityReason;
import org.mutabilitydetector.MutableReasonDetail;

/**
 * 
 * <h1>Mutability Detector</h1>
 * <p>
 * <i>Mutability Detector allows you to write a unit test that checks your
 * classes are immutable.</i>
 * </p>
 * <h1>Help Guide</h2>
 * <h2>Contents</h2>
 * <ol>
 * <li>Preamble
 * <ul>
 * <li><a href="#AboutHelpGuide">About this help guide</a></li>
 * <li><a href="#AboutExamples">About these examples</a></li>
 * </ul>
 * </li>
 * <li><a href="#FirstTestCase">Your first test case.</a></li>
 * <li><a href="#ConfiguringTheAssertion">A more specific assertion</a></li>
 * <li><a href="#AllowingAReason">Allowing a reason for mutability</a></li>
 * <ul>
 * <li><a href="#OutOfTheBox">Out-of-the-box Allowed Reasons</a>
 * <ul>
 * <li><a href="AllowingAbstractImmutable">Abstract immutable
 * implementations</a></li>
 * <li><a href="AllowingAbstractImmutableFields">Assigning abstract immutable
 * fields</a></li>
 * </ul>
 * </li>
 * <li><a href="WritingAnAllowedReason">Writing your own allowed reason</a></li>
 * </ul>
 * </li>
 * <li><a href="HardcodingResults">Hardcoding analysis results</a>
 * <ul>
 *   <li><a href="WhyHardcodeResults">Why hardcode results?</a></li>
 *   <li><a href="DifferentAsserter">Creating your own asserter</a></li>
 *   <li><a href="AddingHarcodedResults">Adding hardcoded results</a></li>
 *   <li><a href="TestHardcodedDirectly">Testing class with hardcoded result</a></li>
 * </ul>
 * </li>
 * </ol>
 * 
 * 
 * 
 * 
 * <h4 id="AboutHelpGuide">About this help guide</h4>
 * <p>
 * The help contents here are also available on the <a href=
 * "http://mutabilitydetector.github.com/MutabilityDetector/mvn-site/apidocs/org/mutabilitydetector/unittesting/MutabilityAssert.html"
 * >project's JavaDoc</a>.
 * </p>
 * This style of documentation is used as it provides content suitable for a web
 * page and for offline use in the JavaDoc viewer of your favourite IDE. It has
 * been <strike>shamelessly stolen from</strike> inspired by the Mockito
 * project, thanks guys.
 * 
 * <h4 id="AboutExamples">About these examples</h4> I am assuming JUnit as the
 * unit testing library. However, Mutability Detector should work with any unit
 * testing library that uses the exception mechanism for their assertions, such
 * as TestNG. If Mutability Detector is incompatible with your favourite testing
 * library, please get in touch, and we'll see what we can do about that.
 * 
 * <h3 id="FirstTestCase">Your first test case.</h3>
 * 
 * The most simple assertion you can make will look something like this:
 * 
 * <pre>
 * <code>
 * import static org.mutabilitydetector.unittesting.MutabilityAssert.assertImmutable;
 * 
 * &#064;Test public void checkMyClassIsImmutable() {
 *     assertImmutable(MyClass.class); 
 * }
 * </code>
 * </pre>
 * 
 * <p>
 * This assertion will trigger an analysis of <code>MyClass</code>, passing if
 * found to be immutable, failing if found to be mutable.
 * </p>
 * 
 * 
 * <h3 id="ConfiguringTheAssertion">Configuring the assertion</h3>
 * <p>
 * The method used above is a shortcut for more expressive forms of the
 * assertion, and does not allow any further configuration. An equivalent
 * assertion is:
 * </p>
 * 
 * <pre>
 * <code>
 * import static org.mutabilitydetector.unittesting.MutabilityAssert.assertInstancesOf;
 * import static org.mutabilitydetector.unittesting.MutabilityMatchers.areImmutable;
 * 
 * &#064;Test public void checkMyClassIsImmutable() {
 *     assertInstancesOf(MyClass.class, areImmutable()); 
 * }</code>
 * </pre>
 * 
 * 
 * This is the form that can be used for extra configuration of the assertion.
 * Let's take a look at an assertion that is configured differently. Consider a
 * class which is immutable, except for fields not being declared
 * <code>final</code>. According to <a href="http://jcip.net/">Java Concurrency
 * In Practice</a>, instances of classes like this, as long as they are
 * <i>safely publised</i> are still considered <i>effectively immutable</i>.
 * Please note however, Mutability Detector does not check that objects are
 * safely published. <br />
 * To represent this in a unit test, the assertion would like this:
 * 
 * <pre>
 * <code>
 * import static org.mutabilitydetector.unittesting.MutabilityAssert.assertInstancesOf;
 * import static org.mutabilitydetector.unittesting.MutabilityMatchers.areEffectivelyImmutable;
 * 
 * &#064;Test public void checkMyClassIsImmutable() {
 *     assertInstancesOf(MyClassWhereTheFieldsAreNotFinal.class, areEffectivelyImmutable());
 * }</code>
 * </pre>
 * 
 * See also:
 * <ul>
 * <li>{@link IsImmutable#EFFECTIVELY_IMMUTABLE}</li>
 * </ul>
 * 
 * <p>
 * The second parameter to the method
 * {@link MutabilityAssert#assertInstancesOf(Class, Matcher)} is a
 * <code>Matcher&lt;AnalysisResult&gt;</code>, where <code>Matcher</code> is a
 * <a href="http://code.google.com/p/hamcrest/">hamcrest matcher</a>, and
 * {@link AnalysisResult} is provided by Mutability Detector to represent the
 * result of the static analysis performed on the given class. This means, if
 * none of the out-of-the-box matchers are quite right for your scenario, you
 * can supply your own. Your implementation of {@link Matcher#matches(Object)}
 * should return true for a test pass, false for a test failure.
 * </p>
 * 
 * 
 * <h3 id="AllowingAReason">Allowing a reason</h3>
 * There can also be cases where your class is found to be mutable, but you know
 * for your scenario that it's an acceptable reason. Consider the following
 * class:
 * 
 * <pre>
 * <code>public abstract class AbstractIntHolder {
 *   private final int intField;
 *   
 *   public AbstractIntHolder(int intToStore) {
 *     this.intField = intToStore;
 *   }
 * }</code>
 * </pre>
 * 
 * <p>
 * In this case, if you assert <code>AbstractIntHolder</code> is immutable, the
 * test will fail. This is because AbstractIntHolder can be subclassed, which
 * means clients of this class, who for example, accept parameters of this type
 * and store them to fields, cannot depend on receiving a concrete, immutable
 * object. If, in your code, you know that all subclasses will also be immutable
 * (hopefully you have tests for them too) then you can say that it is okay that
 * this class can be subclassed, because you know all subclasses are immutable
 * as well.
 * </p>
 * <p>
 * Given such a scenario, the way to get your test to pass, and still provide a
 * check that the class doesn't become mutable by some other cause, is to
 * <i>allow a reason</i> for mutability. An example of allowing said reason for
 * <code>AbstractIntHolder</code> could look like this:
 * </p>
 * 
 * <pre>
 * <code>
 * import static org.mutabilitydetector.unittesting.MutabilityAssert.assertInstancesOf;
 * import static org.mutabilitydetector.unittesting.MutabilityMatchers.areEffectivelyImmutable;
 * import static org.mutabilitydetector.unittesting.AllowedReason.allowingForSubclassing;
 * 
 * &#064;Test public void checkMyClassIsImmutable() {
 *     assertInstancesOf(AbstractIntHolder.class, areImmutable(), allowingForSubclassing());
 * }
 * </code>
 * </pre>
 * 
 * This will allow your test to pass, but fail for any other reasons that are
 * introduced, e.g. if someone adds a setter method.
 * 
 * <p>
 * Similar to the <code>Matcher&ltAnalysisResult&gt;</code> parameter, the
 * allowed reason parameter of
 * {@link #assertInstancesOf(Class, Matcher, Matcher)} is a
 * <code>Matcher&lt;{@link MutableReasonDetail}&gt;</code>. Mutability Detector
 * will provide only a few out-of-the-box implementations for this, which are
 * unlikely to cover each scenario where you want to permit a certain aspect of
 * mutability.
 * </p>
 * <h3 id="OutOfTheBox">Out-of-the-box allowed reasons</h3>
 * <p>
 * <h4 id="AllowingAbstractImmutable">Abstract class with immutable
 * implementation</h4>
 * It can be useful to write an abstract class, designed for extension, which is
 * immutable. To ensure that a concrete class B, extending abstract class A is
 * immutable, it is necessary to test that both <code>A</code> and
 * <code>B</code> are immutable. However, if you write the assertion
 * <code>assertImmutable(A.class);</code>, it will fail, as it can be subclassed
 * (see {@link MutabilityReason#CAN_BE_SUBCLASSED}). To specifically allow this,
 * use the allowed reason: {@link AllowedReason#allowingForSubclassing()} <br>
 * <br>
 * For example:<br>
 * <code>assertInstancesOf(A.class, areImmutable(), allowingForSubclassing());</code>
 * 
 * <h4 id="AllowingAbstractImmutableFields">Depending on other classes being
 * immutable</h4>
 * Consider the following code: <code>
 * <pre>
 * public final class MyImmutable {
 *  public final ShouldAlsoBeImmutable field;
 *  
 *  public MyImmutable(ShouldAlsoBeImmutable dependsOnThisBeingImmutable) {
 *      this.field = dependsOnThisBeingImmutable;
 *  }
 * }
 * </pre>
 * <code>
 * 
 * If <code>ShouldAlsoBeImmutable</code> is not a concrete class (an
 * <code>interface</code> or <code>abstract</code> class),
 * <code>assertImmutable(MyImmutable.class);</code> will fail, as there's no
 * guarantee that the runtime implementation of <code>ShouldBeImmutable</code>
 * is actually immutable. A common example is taking a parameter of
 * <code>java.util.List</code>, where you require that it is an immutable
 * implementation, e.g: a copy created with
 * {@link Collections#unmodifiableList(List)}. For this scenario, use
 * {@link AllowedReason#provided(Class)}.
 * 
 * To make the above example pass, use an allowed reason like so:<br>
 * <br>
 * 
 * 
 * <pre>
 * <code>
 * assertInstancesOf(MyImmutable.class, areImmutable(),
 *                   AllowedReason.provided(ShouldAlsoBeImmutable.class).isAlsoImmutable()); 
 * </code>
 * </pre>
 * 
 * 
 * </p>
 * 
 * <h3 id="WritingAnAllowedReason">Writing your own allowed reasons</h3>
 * <p>
 * If none of the out-of-the-box allowed reasons suit your needs, it is possible
 * to supply your own implementation. The allowed reason in the signature of
 * {@link MutabilityAssert#assertInstancesOf(Class, Matcher, Matcher)} is a
 * Hamcrest <code>Matcher&lt;{@link MutableReasonDetail}&gt;</code>. For a
 * mutable class to pass the test, each {@link MutableReasonDetail} of the
 * {@link AnalysisResult} (provided by Mutability Detector) must be matched by
 * at least one allowed reason.
 * </p>
 * 
 * 
 * <h3 id="HardcodingResults">Configuring MutabilityAssert to use Hardcoded
 * Results</h3>
 * 
 * As of version 0.9, Mutability Detector uses a predefined list of hardcoded
 * results, in order to improve the accuracy of the analysis. For example, prior
 * to 0.9, java.lang.String was <a
 * href="https://github.com/MutabilityDetector/MutabilityDetector/issues/4"
 * >considered to be mutable</a>. The out of the box hardcoded results includes
 * a non-exhaustive list of immutable classes from the standard JDK.
 * 
 * See also:
 * <ul>
 * <li>{@link ConfigurationBuilder#JDK_CONFIGURATION}</li>
 * </ul>
 * 
 * <p>
 * 
 * If you have found that Mutability Detector is unable to correctly analyse one
 * of your classes, or a class in a library you use, you may wish to add your
 * class to the list of predefined results. Follow these steps to choose your
 * own predefined list.
 * 
 * <h4 id="WhyHardcodeResults">Why Would You Want To Hardcode Results?</h4>
 * 
 * Imagine a couple of classes like this:
 * <p>
 * 
 * <pre>
 * <code>
 * &#064;Immutable 
 * public final class ActuallyImmutable { 
 *   // is immutable, but like java.lang.String, is incorrectly
 *   // called mutable. 
 * }
 * 
 * &#064;Immutable 
 * public final class UsesActuallyImmutable { 
 *   public final ActuallyImmutable myImmutableField = ...; 
 * }
 * 
 * // in a test case
 * MutabilityAssert.assertImmutable(UsesActuallyImmutable.class); // this test fails
 * </code>
 * </pre>
 * 
 * Because there's an error in the analysis of <code>ActuallyImmutable</code>,
 * this "taints" <code>UsesActuallyImmutable</code>, which will also be
 * considered mutable. Because of the transitive nature of a false positive,
 * this can cause Mutability Detector to think that entire object graphs are
 * mutable when they're not. Hardcoding your own results is a way to overcome
 * incorrect analysis.
 * 
 * <h4 id="DifferentAsserter">Using A Different Asserter</h4>
 * 
 * To be able to hardcode results, you need your own instance of
 * {@link MutabilityAsserter}. Normally assertions are made using the class
 * {@link MutabilityAssert}. To choose different options from this class, you
 * must create and make available your own asserter with its own configuration.
 * Do this by constructing an instance of MutabilityAssert, like so:
 * 
 * <pre>
 * <code>
 * public class SomeClassAccessibleByMyTests { 
 *   public static final MutabilityAsserter MUTABILITY = MutabilityAsserter.configured(...); 
 * } </code>
 * </pre>
 * 
 * This allows your test case to have an assertion like:
 * <pre>
 * <code>// in a test case 
 * MUTABILITY.assertImmutable(MyClass.class);</pre>
 * </code>
 * 
 * <h4 id="AddingHarcodedResults">Hardcoding Analysis Results</h4>
 * 
 * Notice in the above example, I have glossed over the parameters given to the
 * <code>MutabilityAssert.configured()</code> method. The parameter, of type {@link Configuration},
 * is what will contain your hardcoded results. In the following example, To
 * overcome this, instantiate MutabilityAsserter like this:
 * 
 * <pre>
 * <code>
 * // as a field 
 * MutabilityAsserter MUTABILITY = MutabilityAsserter.configured(new ConfigurationBuilder() { 
 *   &#064;Override public void configure() {
 *     hardcodeAsDefinitelyImmutable(ActuallyImmutable.class); 
 *   }
 * });
 * 
 * // in a test case 
 * MUTABILITY.assertImmutable(UsesActuallyImmutable.class); // this now passes
 * </code>
 * </pre>
 * 
 * Now classes which transitively depend on <code>ActuallyImmutable</code> being correctly
 * analysed will not result in false positive results.
 * 
 * <h4 id="TestHardcodedDirectly">Testing Hardcoded Classes Directly</h4>
 * 
 * Using the configuration from above, if we have the assertion:
 * <pre>
 * <code>MUTABILITY.assertImmutable(ActuallyImmutable.class);</code>
 * </pre>
 * 
 * The test case will fail. Even though it's hardcoded, if you test it directly,
 * the result will reflect the real analysis. This is a deliberate choice, to
 * alert you to the possibility that your choice to hardcode a result may no
 * longer be valid. In this case you will want to write a an assertion which
 * allows the specific reasons for failure. You can still use the same asserter
 * you previously created for this, e.g.:
 * 
 * <pre>
 * <code>
 * MUTABILITY.assertInstancesOf(ActuallyImmutable.class, 
 *                              areImmutable(), 
 *                              // configure your "allowed reasons" here 
 *                              );</code>
 * </pre>
 * 
 * 
 * 
 * @author Graham Allan / Grundlefleck at gmail dot com
 * 
 * @see MutabilityMatchers
 * @see AllowedReason
 * @see AnalysisResult
 * @see MutableReasonDetail
 * @see IsImmutable
 * 
 */
public final class MutabilityAssert {

    private MutabilityAssert() {
    }

    private final static MutabilityAsserter defaultAsserter = MutabilityAsserter.configured(OUT_OF_THE_BOX_CONFIGURATION);

    public static void assertImmutable(Class<?> expectedImmutableClass) {
        defaultAsserter.assertImmutable(expectedImmutableClass);
    }

    public static void assertInstancesOf(Class<?> clazz, Matcher<AnalysisResult> mutabilityMatcher) {
        defaultAsserter.assertInstancesOf(clazz, mutabilityMatcher);
    }

    public static void assertInstancesOf(Class<?> clazz,
                                         Matcher<AnalysisResult> mutabilityMatcher,
                                         Matcher<MutableReasonDetail> allowing) {
        defaultAsserter.assertInstancesOf(clazz, mutabilityMatcher, allowing);
    }

    public static void assertInstancesOf(Class<?> clazz,
                                         Matcher<AnalysisResult> mutabilityMatcher,
                                         Matcher<MutableReasonDetail> allowingFirst,
                                         Matcher<MutableReasonDetail> allowingSecond) {

        defaultAsserter.assertInstancesOf(clazz, mutabilityMatcher, allowingFirst, allowingSecond);
    }

    public static void assertInstancesOf(Class<?> clazz,
                                         Matcher<AnalysisResult> mutabilityMatcher,
                                         Matcher<MutableReasonDetail> allowingFirst,
                                         Matcher<MutableReasonDetail> allowingSecond,
                                         Matcher<MutableReasonDetail> allowingThird) {

        defaultAsserter.assertInstancesOf(clazz, mutabilityMatcher, allowingFirst, allowingSecond, allowingThird);
    }

    public static void assertInstancesOf(Class<?> clazz,
                                         Matcher<AnalysisResult> mutabilityMatcher,
                                         Matcher<MutableReasonDetail> allowingFirst,
                                         Matcher<MutableReasonDetail> allowingSecond,
                                         Matcher<MutableReasonDetail> allowingThird,
                                         Matcher<MutableReasonDetail>... allowingRest) {

        defaultAsserter.assertInstancesOf(clazz,
                                          mutabilityMatcher,
                                          allowingFirst,
                                          allowingSecond,
                                          allowingThird,
                                          allowingRest);
    }

    public static void assertInstancesOf(Class<?> clazz,
                                         Matcher<AnalysisResult> mutabilityMatcher,
                                         Iterable<Matcher<MutableReasonDetail>> allowingAll) {

        defaultAsserter.assertInstancesOf(clazz, mutabilityMatcher, allowingAll);
    }

}
