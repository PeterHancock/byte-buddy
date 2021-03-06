package net.bytebuddy.matcher;

import net.bytebuddy.instrumentation.ByteCodeElement;
import net.bytebuddy.instrumentation.ModifierReviewable;
import net.bytebuddy.instrumentation.attribute.annotation.AnnotatedElement;
import net.bytebuddy.instrumentation.attribute.annotation.AnnotationDescription;
import net.bytebuddy.instrumentation.attribute.annotation.AnnotationList;
import net.bytebuddy.instrumentation.field.FieldDescription;
import net.bytebuddy.instrumentation.method.MethodDescription;
import net.bytebuddy.instrumentation.method.MethodList;
import net.bytebuddy.instrumentation.type.TypeDescription;
import net.bytebuddy.instrumentation.type.TypeList;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.*;

import static net.bytebuddy.utility.ByteBuddyCommons.*;

/**
 * A utility class that contains a human-readable language for creating {@link net.bytebuddy.matcher.ElementMatcher}s.
 */
public final class ElementMatchers {

    /**
     * A private constructor that must not be invoked.
     */
    private ElementMatchers() {
        throw new UnsupportedOperationException();
    }

    /**
     * Matches any {@link java.lang.ClassLoader} but the bootstrap class loader. The returned matcher is a synonym to
     * a matcher matching anything but {@code null}.
     *
     * @param <T> The type of the matched object.
     * @return A matcher that matches any class loader but the bootstrap class loader.
     */
    public static <T extends ClassLoader> ElementMatcher<T> notBootstrap() {
        return not(new NullMatcher<T>());
    }

    /**
     * Matches the given value which can also be {@code null} by the {@link java.lang.Object#equals(Object)} method or
     * by a null-check.
     *
     * @param value The value that is to be matched.
     * @param <T>   The type of the matched object.
     * @return A matcher that matches an exact value.
     */
    public static <T> ElementMatcher.Junction<T> is(Object value) {
        return value == null
                ? new NullMatcher<T>()
                : new EqualityMatcher<T>(value);
    }

    /**
     * Exactly matches a given method as a {@link net.bytebuddy.instrumentation.method.MethodDescription}.
     *
     * @param method The method to match by its description
     * @param <T>    The type of the matched object.
     * @return An element matcher that exactly matches the given method.
     */
    public static <T extends MethodDescription> ElementMatcher.Junction<T> is(Method method) {
        return is(new MethodDescription.ForLoadedMethod(nonNull(method)));
    }

    /**
     * Exactly matches a given constructor as a {@link net.bytebuddy.instrumentation.method.MethodDescription}.
     *
     * @param constructor The constructor to match by its description
     * @param <T>         The type of the matched object.
     * @return An element matcher that exactly matches the given constructor.
     */
    public static <T extends MethodDescription> ElementMatcher.Junction<T> is(Constructor<?> constructor) {
        return is(new MethodDescription.ForLoadedConstructor(nonNull(constructor)));
    }

    /**
     * Exactly matches a given {@link net.bytebuddy.instrumentation.method.MethodDescription}.
     *
     * @param methodDescription The method description to match.
     * @param <T>               The type of the matched object.
     * @return An element matcher that matches the given method description.
     */
    public static <T extends MethodDescription> ElementMatcher.Junction<T> is(MethodDescription methodDescription) {
        return new EqualityMatcher<T>(nonNull(methodDescription));
    }

    /**
     * Exactly matches a given type as a {@link net.bytebuddy.instrumentation.type.TypeDescription}.
     *
     * @param type The type to match by its description
     * @param <T>  The type of the matched object.
     * @return An element matcher that exactly matches the given type.
     */
    public static <T extends TypeDescription> ElementMatcher.Junction<T> is(Class<?> type) {
        return is(new TypeDescription.ForLoadedType(nonNull(type)));
    }

    /**
     * Exactly matches a given {@link net.bytebuddy.instrumentation.type.TypeDescription}.
     *
     * @param typeDescription The type to match by its description
     * @param <T>             The type of the matched object.
     * @return An element matcher that exactly matches the given type.
     */
    public static <T extends TypeDescription> ElementMatcher.Junction<T> is(TypeDescription typeDescription) {
        return new EqualityMatcher<T>(nonNull(typeDescription));
    }

    /**
     * Exactly matches a given annotation as an {@link net.bytebuddy.instrumentation.attribute.annotation.AnnotationDescription}.
     *
     * @param annotation The annotation to match by its description.
     * @param <T>        The type of the matched object.
     * @return An element matcher that exactly matches the given annotation.
     */
    public static <T extends AnnotationDescription> ElementMatcher.Junction<T> is(Annotation annotation) {
        return is(AnnotationDescription.ForLoadedAnnotation.of(nonNull(annotation)));
    }

    /**
     * Exactly matches a given {@link net.bytebuddy.instrumentation.attribute.annotation.AnnotationDescription}.
     *
     * @param annotationDescription The annotation description to match.
     * @param <T>                   The type of the matched object.
     * @return An element matcher that exactly matches the given annotation.
     */
    public static <T extends AnnotationDescription> ElementMatcher.Junction<T> is(AnnotationDescription annotationDescription) {
        return new EqualityMatcher<T>(nonNull(annotationDescription));
    }

    /**
     * Inverts another matcher.
     *
     * @param matcher The matcher to invert.
     * @param <T>     The type of the matched object.
     * @return An inverted version of the given {@code matcher}.
     */
    public static <T> ElementMatcher.Junction<T> not(ElementMatcher<? super T> matcher) {
        return new NegatingMatcher<T>(nonNull(matcher));
    }

    /**
     * Creates a matcher that always returns {@code true}.
     *
     * @param <T> The type of the matched object.
     * @return A matcher that matches anything.
     */
    public static <T> ElementMatcher.Junction<T> any() {
        return new BooleanMatcher<T>(true);
    }

    /**
     * Creates a matcher that always returns {@code false}.
     *
     * @param <T> The type of the matched object.
     * @return A matcher that matches nothing.
     */
    public static <T> ElementMatcher.Junction<T> none() {
        return new BooleanMatcher<T>(false);
    }

    /**
     * Creates a matcher that matches any of the given objects by the {@link java.lang.Object#equals(Object)} method.
     * None of the values must be {@code null}.
     *
     * @param value The input values to be compared against.
     * @param <T>   The type of the matched object.
     * @return A matcher that checks for the equality with any of the given objects.
     */
    public static <T> ElementMatcher.Junction<T> anyOf(Object... value) {
        return anyOf(Arrays.asList(nonNull(value)));
    }

    /**
     * Creates a matcher that matches any of the given objects by the {@link java.lang.Object#equals(Object)} method.
     * None of the values must be {@code null}.
     *
     * @param values The input values to be compared against.
     * @param <T>    The type of the matched object.
     * @return A matcher that checks for the equality with any of the given objects.
     */
    public static <T> ElementMatcher.Junction<T> anyOf(Iterable<?> values) {
        ElementMatcher.Junction<T> matcher = none();
        for (Object value : values) {
            matcher = matcher.or(is(value));
        }
        return matcher;
    }

    /**
     * Creates a matcher that matches any of the given types as {@link net.bytebuddy.instrumentation.type.TypeDescription}s
     * by the {@link java.lang.Object#equals(Object)} method. None of the values must be {@code null}.
     *
     * @param value The input values to be compared against.
     * @param <T>   The type of the matched object.
     * @return A matcher that checks for the equality with any of the given objects.
     */
    public static <T extends TypeDescription> ElementMatcher.Junction<T> anyOf(Class<?>... value) {
        return anyOf(new TypeList.ForLoadedType(nonNull(value)));
    }

    /**
     * Creates a matcher that matches any of the given constructors as {@link net.bytebuddy.instrumentation.method.MethodDescription}s
     * by the {@link java.lang.Object#equals(Object)} method. None of the values must be {@code null}.
     *
     * @param value The input values to be compared against.
     * @param <T>   The type of the matched object.
     * @return A matcher that checks for the equality with any of the given objects.
     */
    public static <T extends MethodDescription> ElementMatcher.Junction<T> anyOf(Constructor<?>... value) {
        return anyOf(new MethodList.ForLoadedType(nonNull(value), new Method[0]));
    }

    /**
     * Creates a matcher that matches any of the given methods as {@link net.bytebuddy.instrumentation.method.MethodDescription}s
     * by the {@link java.lang.Object#equals(Object)} method. None of the values must be {@code null}.
     *
     * @param value The input values to be compared against.
     * @param <T>   The type of the matched object.
     * @return A matcher that checks for the equality with any of the given objects.
     */
    public static <T extends MethodDescription> ElementMatcher.Junction<T> anyOf(Method... value) {
        return anyOf(new MethodList.ForLoadedType(new Constructor<?>[0], nonNull(value)));
    }

    /**
     * Creates a matcher that matches any of the given annotations as {@link net.bytebuddy.instrumentation.attribute.annotation.AnnotationDescription}s
     * by the {@link java.lang.Object#equals(Object)} method. None of the values must be {@code null}.
     *
     * @param value The input values to be compared against.
     * @param <T>   The type of the matched object.
     * @return A matcher that checks for the equality with any of the given objects.
     */
    public static <T extends AnnotationDescription> ElementMatcher.Junction<T> anyOf(Annotation... value) {
        return anyOf(new AnnotationList.ForLoadedAnnotation(nonNull(value)));
    }

    /**
     * Creates a matcher that matches none of the given objects by the {@link java.lang.Object#equals(Object)} method.
     * None of the values must be {@code null}.
     *
     * @param value The input values to be compared against.
     * @param <T>   The type of the matched object.
     * @return A matcher that checks for the equality with none of the given objects.
     */
    public static <T> ElementMatcher.Junction<T> noneOf(Object... value) {
        return noneOf(Arrays.asList(value));
    }

    /**
     * Creates a matcher that matches none of the given objects by the {@link java.lang.Object#equals(Object)} method.
     * None of the values must be {@code null}.
     *
     * @param values The input values to be compared against.
     * @param <T>    The type of the matched object.
     * @return A matcher that checks for the equality with none of the given objects.
     */
    public static <T> ElementMatcher.Junction<T> noneOf(Iterable<?> values) {
        ElementMatcher.Junction<T> matcher = any();
        for (Object value : values) {
            matcher = matcher.and(not(is(value)));
        }
        return matcher;
    }

    /**
     * Creates a matcher that matches none of the given types as {@link net.bytebuddy.instrumentation.type.TypeDescription}s
     * by the {@link java.lang.Object#equals(Object)} method. None of the values must be {@code null}.
     *
     * @param value The input values to be compared against.
     * @param <T>   The type of the matched object.
     * @return A matcher that checks for the equality with none of the given objects.
     */
    public static <T extends TypeDescription> ElementMatcher.Junction<T> noneOf(Class<?>... value) {
        return noneOf(new TypeList.ForLoadedType(nonNull(value)));
    }

    /**
     * Creates a matcher that matches none of the given constructors as {@link net.bytebuddy.instrumentation.method.MethodDescription}s
     * by the {@link java.lang.Object#equals(Object)} method. None of the values must be {@code null}.
     *
     * @param value The input values to be compared against.
     * @param <T>   The type of the matched object.
     * @return A matcher that checks for the equality with none of the given objects.
     */
    public static <T extends MethodDescription> ElementMatcher.Junction<T> noneOf(Constructor<?>... value) {
        return noneOf(new MethodList.ForLoadedType(nonNull(value), new Method[0]));
    }

    /**
     * Creates a matcher that matches none of the given methods as {@link net.bytebuddy.instrumentation.method.MethodDescription}s
     * by the {@link java.lang.Object#equals(Object)} method. None of the values must be {@code null}.
     *
     * @param value The input values to be compared against.
     * @param <T>   The type of the matched object.
     * @return A matcher that checks for the equality with none of the given objects.
     */
    public static <T extends MethodDescription> ElementMatcher.Junction<T> noneOf(Method... value) {
        return noneOf(new MethodList.ForLoadedType(new Constructor<?>[0], nonNull(value)));
    }

    /**
     * Creates a matcher that matches none of the given annotations as {@link net.bytebuddy.instrumentation.attribute.annotation.AnnotationDescription}s
     * by the {@link java.lang.Object#equals(Object)} method. None of the values must be {@code null}.
     *
     * @param value The input values to be compared against.
     * @param <T>   The type of the matched object.
     * @return A matcher that checks for the equality with any of the given objects.
     */
    public static <T extends AnnotationDescription> ElementMatcher.Junction<T> noneOf(Annotation... value) {
        return noneOf(new AnnotationList.ForLoadedAnnotation(nonNull(value)));
    }

    /**
     * Matches a {@link net.bytebuddy.instrumentation.ByteCodeElement} for its exact name.
     *
     * @param name The expected name.
     * @param <T>  The type of the matched object.
     * @return An element matcher for a byte code element's exact name.
     */
    public static <T extends ByteCodeElement> ElementMatcher.Junction<T> named(String name) {
        return new NameMatcher<T>(new StringMatcher(nonNull(name), StringMatcher.Mode.EQUALS_FULLY));
    }

    /**
     * Matches a {@link net.bytebuddy.instrumentation.ByteCodeElement} for its name. The name's
     * capitalization is ignored.
     *
     * @param name The expected name.
     * @param <T>  The type of the matched object.
     * @return An element matcher for a byte code element's name.
     */
    public static <T extends ByteCodeElement> ElementMatcher.Junction<T> namedIgnoreCase(String name) {
        return new NameMatcher<T>(new StringMatcher(nonNull(name), StringMatcher.Mode.EQUALS_FULLY_IGNORE_CASE));
    }

    /**
     * Matches a {@link net.bytebuddy.instrumentation.ByteCodeElement} for its name's prefix.
     *
     * @param prefix The expected name's prefix.
     * @param <T>    The type of the matched object.
     * @return An element matcher for a byte code element's name's prefix.
     */
    public static <T extends ByteCodeElement> ElementMatcher.Junction<T> nameStartsWith(String prefix) {
        return new NameMatcher<T>(new StringMatcher(nonNull(prefix), StringMatcher.Mode.STARTS_WITH));
    }

    /**
     * Matches a {@link net.bytebuddy.instrumentation.ByteCodeElement} for its name's prefix. The name's
     * capitalization is ignored.
     *
     * @param prefix The expected name's prefix.
     * @param <T>    The type of the matched object.
     * @return An element matcher for a byte code element's name's prefix.
     */
    public static <T extends ByteCodeElement> ElementMatcher.Junction<T> nameStartsWithIgnoreCase(String prefix) {
        return new NameMatcher<T>(new StringMatcher(nonNull(prefix), StringMatcher.Mode.STARTS_WITH_IGNORE_CASE));
    }

    /**
     * Matches a {@link net.bytebuddy.instrumentation.ByteCodeElement} for its name's suffix.
     *
     * @param suffix The expected name's suffix.
     * @param <T>    The type of the matched object.
     * @return An element matcher for a byte code element's name's suffix.
     */
    public static <T extends ByteCodeElement> ElementMatcher.Junction<T> nameEndsWith(String suffix) {
        return new NameMatcher<T>(new StringMatcher(nonNull(suffix), StringMatcher.Mode.ENDS_WITH));
    }

    /**
     * Matches a {@link net.bytebuddy.instrumentation.ByteCodeElement} for its name's suffix. The name's
     * capitalization is ignored.
     *
     * @param suffix The expected name's suffix.
     * @param <T>    The type of the matched object.
     * @return An element matcher for a byte code element's name's suffix.
     */
    public static <T extends ByteCodeElement> ElementMatcher.Junction<T> nameEndsWithIgnoreCase(String suffix) {
        return new NameMatcher<T>(new StringMatcher(nonNull(suffix), StringMatcher.Mode.ENDS_WITH_IGNORE_CASE));
    }

    /**
     * Matches a {@link net.bytebuddy.instrumentation.ByteCodeElement} for an infix of its name.
     *
     * @param infix The expected infix of the name.
     * @param <T>   The type of the matched object.
     * @return An element matcher for a byte code element's name's infix.
     */
    public static <T extends ByteCodeElement> ElementMatcher.Junction<T> nameContains(String infix) {
        return new NameMatcher<T>(new StringMatcher(nonNull(infix), StringMatcher.Mode.CONTAINS));
    }

    /**
     * Matches a {@link net.bytebuddy.instrumentation.ByteCodeElement} for an infix of its name. The name's
     * capitalization is ignored.
     *
     * @param infix The expected infix of the name.
     * @param <T>   The type of the matched object.
     * @return An element matcher for a byte code element's name's infix.
     */
    public static <T extends ByteCodeElement> ElementMatcher.Junction<T> nameContainsIgnoreCase(String infix) {
        return new NameMatcher<T>(new StringMatcher(nonNull(infix), StringMatcher.Mode.CONTAINS_IGNORE_CASE));
    }

    /**
     * Matches a {@link net.bytebuddy.instrumentation.ByteCodeElement} name against a regular expression.
     *
     * @param regex The regular expression to match the name against.
     * @param <T>   The type of the matched object.
     * @return An element matcher for a byte code element's name's against the given regular expression.
     */
    public static <T extends ByteCodeElement> ElementMatcher.Junction<T> nameMatches(String regex) {
        return new NameMatcher<T>(new StringMatcher(nonNull(regex), StringMatcher.Mode.MATCHES));
    }

    /**
     * Matches a {@link net.bytebuddy.instrumentation.ByteCodeElement}'s descriptor against a given value.
     *
     * @param descriptor The expected descriptor.
     * @param <T>        The type of the matched object.
     * @return A matcher for the given {@code descriptor}.
     */
    public static <T extends ByteCodeElement> ElementMatcher.Junction<T> hasDescriptor(String descriptor) {
        return new DescriptorMatcher<T>(new StringMatcher(descriptor, StringMatcher.Mode.EQUALS_FULLY));
    }

    /**
     * Matches a {@link net.bytebuddy.instrumentation.ByteCodeElement} for being declared by a given
     * {@link net.bytebuddy.instrumentation.type.TypeDescription}.
     *
     * @param type The type that is expected to declare the matched byte code element.
     * @param <T>  The type of the matched object.
     * @return A matcher for byte code elements being declared by the given {@code type}.
     */
    public static <T extends ByteCodeElement> ElementMatcher.Junction<T> isDeclaredBy(TypeDescription type) {
        return isDeclaredBy(is(nonNull(type)));
    }

    /**
     * Matches a {@link net.bytebuddy.instrumentation.ByteCodeElement} for being declared by a given
     * {@link java.lang.Class}.
     *
     * @param type The type that is expected to declare the matched byte code element.
     * @param <T>  The type of the matched object.
     * @return A matcher for byte code elements being declared by the given {@code type}.
     */
    public static <T extends ByteCodeElement> ElementMatcher.Junction<T> isDeclaredBy(Class<?> type) {
        return isDeclaredBy(new TypeDescription.ForLoadedType(nonNull(type)));
    }

    /**
     * Matches a {@link net.bytebuddy.instrumentation.ByteCodeElement} for being declared by a
     * {@link net.bytebuddy.instrumentation.type.TypeDescription} that is matched by the given matcher.
     *
     * @param matcher A matcher for the declaring type of the matched byte code element as long as it
     *                is not {@code null}.
     * @param <T>     The type of the matched object.
     * @return A matcher for byte code elements being declared by a type matched by the given {@code matcher}.
     */
    public static <T extends ByteCodeElement> ElementMatcher.Junction<T> isDeclaredBy(ElementMatcher<? super TypeDescription> matcher) {
        return new DeclaringTypeMatcher<T>(nonNull(matcher));
    }

    /**
     * Matches a {@link net.bytebuddy.instrumentation.ByteCodeElement} that is visible to a given {@link java.lang.Class}.
     *
     * @param type The type that a matched byte code element is expected to be visible to.
     * @param <T>  The type of the matched object.
     * @return A matcher for a byte code element to be visible to a given {@code type}.
     */
    public static <T extends ByteCodeElement> ElementMatcher.Junction<T> isVisibleTo(Class<?> type) {
        return isVisibleTo(new TypeDescription.ForLoadedType(nonNull(type)));
    }

    /**
     * Matches a {@link net.bytebuddy.instrumentation.ByteCodeElement} that is visible to a given
     * {@link net.bytebuddy.instrumentation.type.TypeDescription}.
     *
     * @param typeDescription The type that a matched byte code element is expected to be visible to.
     * @param <T>             The type of the matched object.
     * @return A matcher for a byte code element to be visible to a given {@code typeDescription}.
     */
    public static <T extends ByteCodeElement> ElementMatcher.Junction<T> isVisibleTo(TypeDescription typeDescription) {
        return new VisibilityMatcher<T>(typeDescription);
    }

    /**
     * Matches an {@link net.bytebuddy.instrumentation.attribute.annotation.AnnotatedElement} for declared annotations.
     * This matcher does not match inherited annotations which only exist for classes. Use
     * {@link net.bytebuddy.matcher.ElementMatchers#inheritsAnnotation(Class)} for matching inherited annotations.
     *
     * @param type The annotation type to match against.
     * @param <T>  The type of the matched object.
     * @return A matcher that validates that an annotated element is annotated with an annotation of {@code type}.
     */
    public static <T extends AnnotatedElement> ElementMatcher.Junction<T> isAnnotatedWith(Class<? extends Annotation> type) {
        return isAnnotatedWith(new TypeDescription.ForLoadedType(nonNull(type)));
    }

    /**
     * Matches an {@link net.bytebuddy.instrumentation.attribute.annotation.AnnotatedElement} for declared annotations.
     * This matcher does not match inherited annotations which only exist for classes. Use
     * {@link net.bytebuddy.matcher.ElementMatchers#inheritsAnnotation(net.bytebuddy.instrumentation.type.TypeDescription)}
     * for matching inherited annotations.
     *
     * @param typeDescription The annotation type to match against.
     * @param <T>             The type of the matched object.
     * @return A matcher that validates that an annotated element is annotated with an annotation of {@code typeDescription}.
     */
    public static <T extends AnnotatedElement> ElementMatcher.Junction<T> isAnnotatedWith(TypeDescription typeDescription) {
        return isAnnotatedWith(is(isAnnotation(typeDescription)));
    }

    /**
     * Matches an {@link net.bytebuddy.instrumentation.attribute.annotation.AnnotatedElement} for declared annotations.
     * This matcher does not match inherited annotations which only exist for classes. Use
     * {@link net.bytebuddy.matcher.ElementMatchers#inheritsAnnotation(net.bytebuddy.matcher.ElementMatcher)}
     * for matching inherited annotations.
     *
     * @param matcher The matcher to apply to any annotation's type found on the matched annotated element.
     * @param <T>     The type of the matched object.
     * @return A matcher that validates that an annotated element is annotated with an annotation of a type
     * that matches the given {@code matcher}.
     */
    public static <T extends AnnotatedElement> ElementMatcher.Junction<T> isAnnotatedWith(ElementMatcher<? super TypeDescription> matcher) {
        return declaresAnnotation(new AnnotationTypeMatcher<AnnotationDescription>(nonNull(matcher)));
    }

    /**
     * Matches an {@link net.bytebuddy.instrumentation.attribute.annotation.AnnotatedElement} to declare any annotation
     * that matches the given matcher. Note that this matcher does not match inherited annotations that only exist
     * for types. Use {@link net.bytebuddy.matcher.ElementMatchers#inheritsAnnotation(net.bytebuddy.matcher.ElementMatcher)}
     * for matching inherited annotations.
     *
     * @param matcher A matcher to apply on any declared annotation of the matched annotated element.
     * @param <T>     The type of the matched object.
     * @return A matcher that validates that an annotated element is annotated with an annotation that matches
     * the given {@code matcher}.
     */
    public static <T extends AnnotatedElement> ElementMatcher.Junction<T> declaresAnnotation(ElementMatcher<? super AnnotationDescription> matcher) {
        return new DeclaringAnnotationMatcher<T>(new CollectionItemMatcher<AnnotationDescription>(nonNull(matcher)));
    }

    /**
     * Matches a {@link net.bytebuddy.instrumentation.ModifierReviewable} that is {@code public}.
     *
     * @param <T> The type of the matched object.
     * @return A matcher for a {@code public} modifier reviewable.
     */
    public static <T extends ModifierReviewable> ElementMatcher.Junction<T> isPublic() {
        return new ModifierMatcher<T>(ModifierMatcher.Mode.PUBLIC);
    }

    /**
     * Matches a {@link net.bytebuddy.instrumentation.ModifierReviewable} that is {@code protected}.
     *
     * @param <T> The type of the matched object.
     * @return A matcher for a {@code protected} modifier reviewable.
     */
    public static <T extends ModifierReviewable> ElementMatcher.Junction<T> isProtected() {
        return new ModifierMatcher<T>(ModifierMatcher.Mode.PROTECTED);
    }

    /**
     * Matches a {@link net.bytebuddy.instrumentation.ModifierReviewable} that is package-private.
     *
     * @param <T> The type of the matched object.
     * @return A matcher for a package-private modifier reviewable.
     */
    public static <T extends ModifierReviewable> ElementMatcher.Junction<T> isPackagePrivate() {
        return not(isPublic().or(isProtected()).or(isPrivate()));
    }

    /**
     * Matches a {@link net.bytebuddy.instrumentation.ModifierReviewable} that is {@code private}.
     *
     * @param <T> The type of the matched object.
     * @return A matcher for a {@code private} modifier reviewable.
     */
    public static <T extends ModifierReviewable> ElementMatcher.Junction<T> isPrivate() {
        return new ModifierMatcher<T>(ModifierMatcher.Mode.PRIVATE);
    }

    /**
     * Matches a {@link net.bytebuddy.instrumentation.ModifierReviewable} that is {@code final}.
     *
     * @param <T> The type of the matched object.
     * @return A matcher for a {@code final} modifier reviewable.
     */
    public static <T extends ModifierReviewable> ElementMatcher.Junction<T> isFinal() {
        return new ModifierMatcher<T>(ModifierMatcher.Mode.FINAL);
    }

    /**
     * Matches a {@link net.bytebuddy.instrumentation.ModifierReviewable} that is {@code static}.
     *
     * @param <T> The type of the matched object.
     * @return A matcher for a {@code static} modifier reviewable.
     */
    public static <T extends ModifierReviewable> ElementMatcher.Junction<T> isStatic() {
        return new ModifierMatcher<T>(ModifierMatcher.Mode.STATIC);
    }

    /**
     * Matches a {@link net.bytebuddy.instrumentation.ModifierReviewable} that is synthetic.
     *
     * @param <T> The type of the matched object.
     * @return A matcher for a synthetic modifier reviewable.
     */
    public static <T extends ModifierReviewable> ElementMatcher.Junction<T> isSynthetic() {
        return new ModifierMatcher<T>(ModifierMatcher.Mode.SYNTHETIC);
    }

    /**
     * Matches a {@link net.bytebuddy.instrumentation.method.MethodDescription} that is {@code synchronized}.
     *
     * @param <T> The type of the matched object.
     * @return A matcher for a {@code synchronized} method description.
     */
    public static <T extends MethodDescription> ElementMatcher.Junction<T> isSynchronized() {
        return new ModifierMatcher<T>(ModifierMatcher.Mode.SYNCHRONIZED);
    }

    /**
     * Matches a {@link net.bytebuddy.instrumentation.method.MethodDescription} that is {@code native}.
     *
     * @param <T> The type of the matched object.
     * @return A matcher for a {@code native} method description.
     */
    public static <T extends MethodDescription> ElementMatcher.Junction<T> isNative() {
        return new ModifierMatcher<T>(ModifierMatcher.Mode.NATIVE);
    }

    /**
     * Matches a {@link net.bytebuddy.instrumentation.method.MethodDescription} that is {@code strictfp}.
     *
     * @param <T> The type of the matched object.
     * @return A matcher for a {@code strictfp} method description.
     */
    public static <T extends MethodDescription> ElementMatcher.Junction<T> isStrict() {
        return new ModifierMatcher<T>(ModifierMatcher.Mode.STRICT);
    }

    /**
     * Matches a {@link net.bytebuddy.instrumentation.method.MethodDescription} that is a var-args.
     *
     * @param <T> The type of the matched object.
     * @return A matcher for a var-args method description.
     */
    public static <T extends MethodDescription> ElementMatcher.Junction<T> isVarArgs() {
        return new ModifierMatcher<T>(ModifierMatcher.Mode.VAR_ARGS);
    }

    /**
     * Matches a {@link net.bytebuddy.instrumentation.method.MethodDescription} that is a bridge.
     *
     * @param <T> The type of the matched object.
     * @return A matcher for a bridge method.
     */
    public static <T extends MethodDescription> ElementMatcher.Junction<T> isBridge() {
        return new ModifierMatcher<T>(ModifierMatcher.Mode.BRIDGE);
    }

    /**
     * Matches {@link net.bytebuddy.instrumentation.method.MethodDescription}s that returns a given
     * {@link java.lang.Class}.
     *
     * @param type The type the matched method is expected to return.
     * @param <T>  The type of the matched object.
     * @return An element matcher that matches a given return type for a method description.
     */
    public static <T extends MethodDescription> ElementMatcher.Junction<T> returns(Class<?> type) {
        return returns(new TypeDescription.ForLoadedType(nonNull(type)));
    }

    /**
     * Matches {@link net.bytebuddy.instrumentation.method.MethodDescription}s that returns a given
     * {@link net.bytebuddy.instrumentation.type.TypeDescription}.
     *
     * @param typeDescription The type the matched method is expected to return.
     * @param <T>             The type of the matched object.
     * @return An element matcher that matches a given return type for a method description.
     */
    public static <T extends MethodDescription> ElementMatcher.Junction<T> returns(TypeDescription typeDescription) {
        return returns(is(nonNull(typeDescription)));
    }

    /**
     * Matches {@link net.bytebuddy.instrumentation.method.MethodDescription}s that matches a matched method's
     * return type.
     *
     * @param matcher A matcher to apply onto a matched method's return type.
     * @param <T>     The type of the matched object.
     * @return An element matcher that matches a given return type against another {@code matcher}.
     */
    public static <T extends MethodDescription> ElementMatcher.Junction<T> returns(ElementMatcher<? super TypeDescription> matcher) {
        return new MethodReturnTypeMatcher<T>(nonNull(matcher));
    }

    /**
     * Matches a {@link net.bytebuddy.instrumentation.method.MethodDescription} by its exact parameter types.
     *
     * @param type The parameter types of a method in their order.
     * @param <T>  The type of the matched object.
     * @return An element matcher that matches a method by its exact argument types.
     */
    public static <T extends MethodDescription> ElementMatcher.Junction<T> takesArguments(Class<?>... type) {
        TypeDescription[] typeDescription = new TypeDescription[type.length];
        int index = 0;
        for (Class<?> aType : type) {
            typeDescription[index++] = new TypeDescription.ForLoadedType(nonNull(aType));
        }
        return takesArguments(typeDescription);
    }

    /**
     * Matches a {@link net.bytebuddy.instrumentation.method.MethodDescription} by its exact parameter types.
     *
     * @param typeDescription The parameter types of a method in their order.
     * @param <T>             The type of the matched object.
     * @return An element matcher that matches a method by its exact argument types.
     */
    public static <T extends MethodDescription> ElementMatcher.Junction<T> takesArguments(TypeDescription... typeDescription) {
        return takesArguments((Arrays.asList(typeDescription)));
    }

    /**
     * Matches a {@link net.bytebuddy.instrumentation.method.MethodDescription} by its exact parameter types.
     *
     * @param typeDescriptions The parameter types of a method in their order.
     * @param <T>              The type of the matched object.
     * @return An element matcher that matches a method by its exact argument types.
     */
    public static <T extends MethodDescription> ElementMatcher.Junction<T> takesArguments(Iterable<? extends TypeDescription> typeDescriptions) {
        List<ElementMatcher<? super TypeDescription>> typeMatchers = new LinkedList<ElementMatcher<? super TypeDescription>>();
        for (TypeDescription typeDescription : typeDescriptions) {
            typeMatchers.add(is(nonVoid(typeDescription)));
        }
        return takesArguments(new CollectionOneToOneMatcher<TypeDescription>(typeMatchers));
    }

    /**
     * Matches a {@link net.bytebuddy.instrumentation.method.MethodDescription} by applying an iterable collection
     * of element matcher on any parameter's {@link net.bytebuddy.instrumentation.type.TypeDescription}.
     *
     * @param matchers The matcher that are applied onto the parameter types of the matched method description.
     * @param <T>      The type of the matched object.
     * @return A matcher that matches a method description by applying another element matcher onto each
     * parameter's type.
     */
    public static <T extends MethodDescription> ElementMatcher.Junction<T> takesArguments(ElementMatcher<? super Iterable<? extends TypeDescription>> matchers) {
        return new MethodParameterTypesMatcher<T>(nonNull(matchers));
    }

    /**
     * Matches a {@link net.bytebuddy.instrumentation.method.MethodDescription} by the number of its parameters.
     *
     * @param length The expected length.
     * @param <T>    The type of the matched object.
     * @return A matcher that matches a method description by the number of its parameters.
     */
    public static <T extends MethodDescription> ElementMatcher.Junction<T> takesArguments(int length) {
        return new MethodParameterTypesMatcher<T>(new CollectionSizeMatcher<List<? extends TypeDescription>>(length));
    }

    /**
     * Matches a {@link net.bytebuddy.instrumentation.method.MethodDescription} by its capability to throw a given
     * checked exception. For specifying a non-checked exception, any method is matched.
     *
     * @param exceptionType The type of the exception that should be declared by the method to be matched.
     * @param <T>           The type of the matched object.
     * @return A matcher that matches a method description by its declaration of throwing a checked exception.
     */
    public static <T extends MethodDescription> ElementMatcher.Junction<T> canThrow(Class<? extends Throwable> exceptionType) {
        return canThrow(new TypeDescription.ForLoadedType(nonNull(exceptionType)));
    }

    /**
     * Matches a {@link net.bytebuddy.instrumentation.method.MethodDescription} by its capability to throw a given
     * checked exception. For specifying a non-checked exception, any method is matched.
     *
     * @param exceptionType The type of the exception that should be declared by the method to be matched.
     * @param <T>           The type of the matched object.
     * @return A matcher that matches a method description by its declaration of throwing a checked exception.
     */
    public static <T extends MethodDescription> ElementMatcher.Junction<T> canThrow(TypeDescription exceptionType) {
        if (exceptionType.isAssignableTo(Throwable.class)) {
            return exceptionType.isAssignableTo(RuntimeException.class) || exceptionType.isAssignableTo(Error.class)
                    ? new BooleanMatcher<T>(true)
                    : ElementMatchers.<T>throwing(new CollectionItemMatcher<TypeDescription>(new SubTypeMatcher<TypeDescription>(exceptionType)));
        } else {
            throw new IllegalArgumentException(exceptionType + " is not an exception type");
        }
    }

    /**
     * Matches a {@link net.bytebuddy.instrumentation.method.MethodDescription} by its declared exceptions.
     *
     * @param exceptionMatcher A matcher that is applied by to the declared exceptions.
     * @param <T>              The type of the matched object.
     * @return A matcher that matches a method description by its declared exceptions.
     */
    public static <T extends MethodDescription> ElementMatcher.Junction<T> throwing(ElementMatcher<? super List<? extends TypeDescription>> exceptionMatcher) {
        return new MethodExceptionTypeMatcher<T>(exceptionMatcher);
    }

    /**
     * Only matches method descriptions that represent a {@link java.lang.reflect.Method}.
     *
     * @param <T> The type of the matched object.
     * @return A matcher that only matches method descriptions that represent a Java method.
     */
    public static <T extends MethodDescription> ElementMatcher.Junction<T> isMethod() {
        return new MethodSortMatcher<T>(MethodSortMatcher.Sort.METHOD);
    }

    /**
     * Only matches method descriptions that represent a {@link java.lang.reflect.Constructor}.
     *
     * @param <T> The type of the matched object.
     * @return A matcher that only matches method descriptions that represent a Java constructor.
     */
    public static <T extends MethodDescription> ElementMatcher.Junction<T> isConstructor() {
        return new MethodSortMatcher<T>(MethodSortMatcher.Sort.CONSTRUCTOR);
    }

    /**
     * Only matches method descriptions that represent a {@link java.lang.Class} type initializer.
     *
     * @param <T> The type of the matched object.
     * @return A matcher that only matches method descriptions that represent the type initializer.
     */
    public static <T extends MethodDescription> ElementMatcher.Junction<T> isTypeInitializer() {
        return new MethodSortMatcher<T>(MethodSortMatcher.Sort.TYPE_INITIALIZER);
    }

    /**
     * Only matches method descriptions that represent a visibility bridge. A visibility bridge is a Java bridge
     * method that was inserted by the compiler in order to increase the visibility of a method when inheriting
     * a {@code public} method from a package-private type. In this case, the package-private type's method
     * is declared to be package-private itself such that the bridge method needs to increase the visibility and
     * delegates the call to the original, package-private implementation.
     *
     * @param <T> The type of the matched object.
     * @return A matcher that matches visibility bridge methods.
     */
    public static <T extends MethodDescription> ElementMatcher.Junction<T> isVisibilityBridge() {
        return new MethodSortMatcher<T>(MethodSortMatcher.Sort.VISIBILITY_BRIDGE);
    }

    /**
     * Only matches methods that are overridable, i.e. non-final and dispatched virtually.
     *
     * @param <T> The type of the matched object.
     * @return A matcher that only matches overridable methods.
     */
    public static <T extends MethodDescription> ElementMatcher.Junction<T> isOverridable() {
        return new MethodSortMatcher<T>(MethodSortMatcher.Sort.OVERRIDABLE);
    }

    /**
     * Only matches Java 8 default methods.
     *
     * @param <T> The type of the matched object.
     * @return A matcher that only matches Java 8 default methods.
     */
    public static <T extends MethodDescription> ElementMatcher.Junction<T> isDefaultMethod() {
        return new MethodSortMatcher<T>(MethodSortMatcher.Sort.DEFAULT_METHOD);
    }

    /**
     * Matches a default constructor, i.e. a constructor without arguments.
     *
     * @param <T> The type of the matched object.
     * @return A matcher that matches a default constructor.
     */
    public static <T extends MethodDescription> ElementMatcher.Junction<T> isDefaultConstructor() {
        return isConstructor().and(takesArguments(0));
    }

    /**
     * Only matches the {@link Object#finalize()} method if it was not overridden.
     *
     * @param <T> The type of the matched object.
     * @return A matcher that only matches a non-overridden {@link Object#finalize()} method.
     */
    public static <T extends MethodDescription> ElementMatcher.Junction<T> isDefaultFinalizer() {
        return isFinalizer().and(isDeclaredBy(Object.class));
    }

    /**
     * Only matches the {@link Object#finalize()} method, even if it was overridden.
     *
     * @param <T> The type of the matched object.
     * @return A matcher that only matches the {@link Object#finalize()} method.
     */
    public static <T extends MethodDescription> ElementMatcher.Junction<T> isFinalizer() {
        return named("finalize").and(takesArguments(0)).and(returns(void.class));
    }

    /**
     * Only matches the {@link Object#toString()} method, also if it was overridden.
     *
     * @param <T> The type of the matched object.
     * @return A matcher that only matches the {@link Object#toString()} method.
     */
    public static <T extends MethodDescription> ElementMatcher.Junction<T> isHashCode() {
        return named("hashCode").and(takesArguments(0)).and(returns(int.class));
    }

    /**
     * Only matches the {@link Object#equals(Object)} method, also if it was overridden.
     *
     * @param <T> The type of the matched object.
     * @return A matcher that only matches the {@link Object#equals(Object)} method.
     */
    public static <T extends MethodDescription> ElementMatcher.Junction<T> isEquals() {
        return named("equals").and(takesArguments(Object.class)).and(returns(boolean.class));
    }

    /**
     * Only matches the {@link Object#clone()} method, also if it was overridden.
     *
     * @param <T> The type of the matched object.
     * @return A matcher that only matches the {@link Object#clone()} method.
     */
    public static <T extends MethodDescription> ElementMatcher.Junction<T> isClone() {
        return named("clone").and(takesArguments(0)).and(returns(Object.class));
    }

    /**
     * Only matches the {@link Object#toString()} method, also if it was overridden.
     *
     * @param <T> The type of the matched object.
     * @return A matcher that only matches the {@link Object#toString()} method.
     */
    public static <T extends MethodDescription> ElementMatcher.Junction<T> isToString() {
        return named("toString").and(takesArguments(0)).and(returns(String.class));
    }

    /**
     * Matches any Java bean setter method.
     *
     * @param <T> The type of the matched object.
     * @return A matcher that matches any setter method.
     */
    public static <T extends MethodDescription> ElementMatcher.Junction<T> isSetter() {
        return nameStartsWith("set").and(takesArguments(1)).and(returns(void.class));
    }

    /**
     * Matches any Java bean setter method which takes an argument the given type.
     *
     * @param type The required setter type.
     * @param <T>  The type of the matched object.
     * @return A matcher that matches any setter method.
     */
    public static <T extends MethodDescription> ElementMatcher.Junction<T> isSetter(Class<?> type) {
        return isSetter(new TypeDescription.ForLoadedType(nonNull(type)));
    }

    /**
     * Matches any Java bean setter method which takes an argument the given type.
     *
     * @param typeDescription The required setter type.
     * @param <T>             The type of the matched object.
     * @return A matcher that matches a setter method with the specified argument type.
     */
    public static <T extends MethodDescription> ElementMatcher.Junction<T> isSetter(TypeDescription typeDescription) {
        return isSetter(is(nonVoid(typeDescription)));
    }

    /**
     * Matches any Java bean setter method which takes an argument that matches the supplied matcher.
     *
     * @param matcher A matcher to be allied to a setter method's argument type.
     * @param <T>     The type of the matched object.
     * @return A matcher that matches a setter method with an argument type that matches the supplied matcher.
     */
    public static <T extends MethodDescription> ElementMatcher.Junction<T> isSetter(ElementMatcher<? super TypeDescription> matcher) {
        return isSetter().and(takesArguments(new CollectionOneToOneMatcher<TypeDescription>(Collections.singletonList(nonNull(matcher)))));
    }

    /**
     * Matches any Java bean getter method.
     *
     * @param <T> The type of the matched object.
     * @return A matcher that matches any getter method.
     */
    public static <T extends MethodDescription> ElementMatcher.Junction<T> isGetter() {
        return takesArguments(0).and(not(returns(void.class))).and(nameStartsWith("get")
                .or(nameStartsWith("is").and(returns(anyOf(boolean.class, Boolean.class)))));
    }

    /**
     * Matches any Java bean getter method which returns the given type.
     *
     * @param type The required getter type.
     * @param <T>  The type of the matched object.
     * @return A matcher that matches a getter method with the given type.
     */
    public static <T extends MethodDescription> ElementMatcher.Junction<T> isGetter(Class<?> type) {
        return isGetter(new TypeDescription.ForLoadedType(nonNull(type)));
    }

    /**
     * Matches any Java bean getter method which returns the given type.
     *
     * @param typeDescription The required getter type.
     * @param <T>             The type of the matched object.
     * @return A matcher that matches a getter method with the given type.
     */
    public static <T extends MethodDescription> ElementMatcher.Junction<T> isGetter(TypeDescription typeDescription) {
        return isGetter(is(nonVoid(typeDescription)));
    }

    /**
     * Matches any Java bean getter method which returns an value with a type matches the supplied matcher.
     *
     * @param matcher A matcher to be allied to a getter method's argument type.
     * @param <T>     The type of the matched object.
     * @return A matcher that matches a getter method with a return type that matches the supplied matcher.
     */
    public static <T extends MethodDescription> ElementMatcher.Junction<T> isGetter(ElementMatcher<? super TypeDescription> matcher) {
        return isGetter().and(returns(nonNull(matcher)));
    }

    /**
     * Matches a <i>specialized</i> version of a given method. This method is characterized by an identical name and
     * by a return type that is a sub type of the given method's return type and by parameter types that are sub types
     * of the the given method's parameter types.
     *
     * @param methodDescription The method description to match.
     * @param <T>               The type of the matched object.
     * @return A matcher that matches a specialized version of the given method.
     */
    public static <T extends MethodDescription> ElementMatcher.Junction<T> isSpecializationOf(MethodDescription methodDescription) {
        TypeList parameterTypes = methodDescription.getParameterTypes();
        List<ElementMatcher<TypeDescription>> matchers = new ArrayList<ElementMatcher<TypeDescription>>(parameterTypes.size());
        for (TypeDescription typeDescription : parameterTypes) {
            matchers.add(isSubTypeOf(typeDescription));
        }
        return (methodDescription.isStatic() ? ElementMatchers.<T>isStatic() : ElementMatchers.<T>not(isStatic()))
                .<T>and(named(methodDescription.getSourceCodeName()))
                .<T>and(returns(isSubTypeOf(methodDescription.getReturnType())))
                .and(takesArguments(new CollectionOneToOneMatcher<TypeDescription>(matchers)));
    }

    /**
     * Matches any type description that is a subtype of the given type.
     *
     * @param type The type to be checked being a super type of the matched type.
     * @param <T>  The type of the matched object.
     * @return A matcher that matches any type description that represents a sub type of the given type.
     */
    public static <T extends TypeDescription> ElementMatcher.Junction<T> isSubTypeOf(Class<?> type) {
        return isSubTypeOf(new TypeDescription.ForLoadedType(nonNull(type)));
    }

    /**
     * Matches any type description that is a subtype of the given type.
     *
     * @param typeDescription The type to be checked being a super type of the matched type.
     * @param <T>             The type of the matched object.
     * @return A matcher that matches any type description that represents a sub type of the given type.
     */
    public static <T extends TypeDescription> ElementMatcher.Junction<T> isSubTypeOf(TypeDescription typeDescription) {
        return new SubTypeMatcher<T>(nonNull(typeDescription));
    }

    /**
     * Matches any type description that is a super type of the given type.
     *
     * @param type The type to be checked being a subtype of the matched type.
     * @param <T>  The type of the matched object.
     * @return A matcher that matches any type description that represents a super type of the given type.
     */
    public static <T extends TypeDescription> ElementMatcher.Junction<T> isSuperTypeOf(Class<?> type) {
        return isSuperTypeOf(new TypeDescription.ForLoadedType(nonNull(type)));
    }

    /**
     * Matches any type description that is a super type of the given type.
     *
     * @param typeDescription The type to be checked being a subtype of the matched type.
     * @param <T>             The type of the matched object.
     * @return A matcher that matches any type description that represents a super type of the given type.
     */
    public static <T extends TypeDescription> ElementMatcher.Junction<T> isSuperTypeOf(TypeDescription typeDescription) {
        return new SuperTypeMatcher<T>(nonNull(typeDescription));
    }

    /**
     * Matches any annotations by their type on a type that declared these annotations or inherited them from its
     * super classes.
     *
     * @param type The annotation type to be matched.
     * @param <T>  The type of the matched object.
     * @return A matcher that matches any inherited annotation by their type.
     */
    public static <T extends TypeDescription> ElementMatcher.Junction<T> inheritsAnnotation(Class<?> type) {
        return inheritsAnnotation(new TypeDescription.ForLoadedType(nonNull(type)));
    }

    /**
     * Matches any annotations by their type on a type that declared these annotations or inherited them from its
     * super classes.
     *
     * @param typeDescription The annotation type to be matched.
     * @param <T>             The type of the matched object.
     * @return A matcher that matches any inherited annotation by their type.
     */
    public static <T extends TypeDescription> ElementMatcher.Junction<T> inheritsAnnotation(TypeDescription typeDescription) {
        return inheritsAnnotation(is(typeDescription));
    }

    /**
     * Matches any annotations by a given matcher on a type that declared these annotations or inherited them from its
     * super classes.
     *
     * @param matcher A matcher to apply onto the inherited annotations.
     * @param <T>     The type of the matched object.
     * @return A matcher that matches any inherited annotation by a given matcher.
     */
    public static <T extends TypeDescription> ElementMatcher.Junction<T> inheritsAnnotation(ElementMatcher<? super TypeDescription> matcher) {
        return hasAnnotation(new AnnotationTypeMatcher<AnnotationDescription>(nonNull(matcher)));
    }

    /**
     * Matches a list of annotations by a given matcher on a type that declared these annotations or inherited them
     * from its super classes.
     *
     * @param matcher A matcher to apply onto a list of inherited annotations.
     * @param <T>     The type of the matched object.
     * @return A matcher that matches a list of inherited annotation by a given matcher.
     */
    public static <T extends TypeDescription> ElementMatcher.Junction<T> hasAnnotation(ElementMatcher<? super AnnotationDescription> matcher) {
        return new InheritedAnnotationMatcher<T>(new CollectionItemMatcher<AnnotationDescription>(nonNull(matcher)));
    }

    /**
     * Matches a type by a another matcher that is applied on any of its declared fields.
     *
     * @param fieldMatcher The matcher that is applied onto each declared field.
     * @param <T>          The type of the matched object.
     * @return A matcher that matches any type where another matcher is matched positively on at least on declared field.
     */
    public static <T extends TypeDescription> ElementMatcher.Junction<T> declaresField(ElementMatcher<? super FieldDescription> fieldMatcher) {
        return new DeclaringFieldMatcher<T>(new CollectionItemMatcher<FieldDescription>(nonNull(fieldMatcher)));
    }

    /**
     * Matches a type by a another matcher that is applied on any of its declared methods.
     *
     * @param methodMatcher The matcher that is applied onto each declared method.
     * @param <T>           The type of the matched object.
     * @return A matcher that matches any type where another matcher is matched positively on at least on declared methods.
     */
    public static <T extends TypeDescription> ElementMatcher.Junction<T> declaresMethod(ElementMatcher<? super MethodDescription> methodMatcher) {
        return new DeclaringMethodMatcher<T>(new CollectionItemMatcher<MethodDescription>(nonNull(methodMatcher)));
    }
}
