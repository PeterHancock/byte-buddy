package net.bytebuddy.utility;

import net.bytebuddy.instrumentation.ModifierContributor;
import net.bytebuddy.instrumentation.method.MethodDescription;
import net.bytebuddy.instrumentation.type.TypeDescription;
import net.bytebuddy.modifier.FieldManifestation;
import net.bytebuddy.modifier.Ownership;
import net.bytebuddy.modifier.Visibility;
import net.bytebuddy.test.utility.MockitoRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;
import org.mockito.asm.Opcodes;

import java.io.Serializable;
import java.lang.annotation.Retention;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static net.bytebuddy.utility.ByteBuddyCommons.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsSame.sameInstance;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ByteBuddyCommonsTest {

    private static final String FOO = "foo", BAR = "bar", QUX = "qux", BAZ = "baz", FOOBAR = "foo.bar", PUBLIC = "public";

    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private TypeDescription first, second;

    @Before
    public void setUp() throws Exception {
        when(first.getInternalName()).thenReturn(FOO);
        when(first.isAssignableTo(Throwable.class)).thenReturn(true);
        when(second.getInternalName()).thenReturn(BAR);
        when(second.isAssignableTo(Throwable.class)).thenReturn(false);
    }

    @Test
    public void testNonNull() throws Exception {
        Object object = new Object();
        assertThat(nonNull(object), sameInstance(object));
    }

    @Test(expected = NullPointerException.class)
    public void testNonNullThrowsException() throws Exception {
        nonNull(null);
    }

    @Test
    public void testNonNullArray() throws Exception {
        Object[] object = new Object[]{new Object()};
        assertThat(nonNull(object), sameInstance(object));
    }

    @Test(expected = NullPointerException.class)
    public void testNonNullArrayThrowsException() throws Exception {
        nonNull(new Object[1]);
    }

    @Test
    public void testNonVoid() throws Exception {
        TypeDescription typeDescription = mock(TypeDescription.class);
        assertThat(nonVoid(typeDescription), sameInstance(typeDescription));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNonVoidThrowsException() throws Exception {
        TypeDescription typeDescription = mock(TypeDescription.class);
        when(typeDescription.represents(void.class)).thenReturn(true);
        assertThat(nonVoid(typeDescription), sameInstance(typeDescription));
    }

    @Test
    public void testNonVoidCollection() throws Exception {
        List<TypeDescription> typeDescriptions = Arrays.asList(mock(TypeDescription.class));
        assertThat(nonVoid(typeDescriptions), sameInstance(typeDescriptions));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNonVoidCollectionThrowsException() throws Exception {
        TypeDescription typeDescription = mock(TypeDescription.class);
        when(typeDescription.represents(void.class)).thenReturn(true);
        nonVoid(Arrays.asList(typeDescription));
    }

    @Test
    public void testIsInterface() throws Exception {
        TypeDescription typeDescription = new TypeDescription.ForLoadedType(Runnable.class);
        assertThat(isInterface(typeDescription), is(typeDescription));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIsInterfaceThrowsException() throws Exception {
        isInterface(new TypeDescription.ForLoadedType(Object.class));
    }

    @Test
    public void testIsInterfaceArray() throws Exception {
        TypeDescription typeDescription = new TypeDescription.ForLoadedType(Runnable.class);
        TypeDescription otherTypeDescription = new TypeDescription.ForLoadedType(Serializable.class);
        assertThat(isInterface(new TypeDescription[]{typeDescription, otherTypeDescription}),
                is(new TypeDescription[]{typeDescription, otherTypeDescription}));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIsInterfaceArrayThrowsException() throws Exception {
        isInterface(new TypeDescription[]{new TypeDescription.ForLoadedType(Runnable.class), new TypeDescription.ForLoadedType(Object.class)});
    }

    @Test
    public void testIsInterfaceList() throws Exception {
        TypeDescription typeDescription = new TypeDescription.ForLoadedType(Runnable.class);
        TypeDescription otherTypeDescription = new TypeDescription.ForLoadedType(Serializable.class);
        assertThat(isInterface(Arrays.asList(typeDescription, otherTypeDescription)),
                is(Arrays.asList(typeDescription, otherTypeDescription)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIsInterfaceListThrowsException() throws Exception {
        isInterface(Arrays.asList(new TypeDescription.ForLoadedType(Runnable.class), new TypeDescription.ForLoadedType(Object.class)));
    }

    @Test
    public void testIsAnnotation() throws Exception {
        TypeDescription typeDescription = new TypeDescription.ForLoadedType(Retention.class);
        assertThat(isAnnotation(typeDescription), is(typeDescription));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIsAnnotationThrowsException() throws Exception {
        TypeDescription typeDescription = new TypeDescription.ForLoadedType(Object.class);
        assertThat(isAnnotation(typeDescription), is(typeDescription));
    }

    @Test
    public void testClassIsExtendable() throws Exception {
        TypeDescription typeDescription = new TypeDescription.ForLoadedType(Object.class);
        assertThat(isExtendable(typeDescription), is(typeDescription));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFinalClassIsExtendableThrowsException() throws Exception {
        TypeDescription typeDescription = new TypeDescription.ForLoadedType(String.class);
        isExtendable(typeDescription);
    }

    @Test
    public void testInterfaceIsExtendable() throws Exception {
        TypeDescription typeDescription = new TypeDescription.ForLoadedType(Runnable.class);
        assertThat(isExtendable(typeDescription), is(typeDescription));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPrimitiveIsExtendableThrowsException() throws Exception {
        isExtendable(new TypeDescription.ForLoadedType(int.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testArrayIsExtendableThrowsException() throws Exception {
        isExtendable(new TypeDescription.ForLoadedType(Object[].class));
    }

    @Test
    public void testJoinListAndElement() throws Exception {
        assertThat(join(Arrays.asList(FOO, BAR), QUX), is(Arrays.asList(FOO, BAR, QUX)));
    }

    @Test
    public void testJoinElementAndList() throws Exception {
        assertThat(join(FOO, Arrays.asList(BAR, QUX)), is(Arrays.asList(FOO, BAR, QUX)));
    }

    @Test
    public void testJoinListAndList() throws Exception {
        assertThat(join(Arrays.asList(FOO, BAR), Arrays.asList(QUX, BAZ)), is(Arrays.asList(FOO, BAR, QUX, BAZ)));
    }

    @Test
    public void testIsValidIdentifier() throws Exception {
        assertThat(isValidIdentifier(FOO), is(FOO));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIsValidIdentifierInvalidTokenStartThrowsException() throws Exception {
        isValidIdentifier(MethodDescription.CONSTRUCTOR_INTERNAL_NAME);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIsValidIdentifierInvalidTokenMiddleThrowsException() throws Exception {
        isValidIdentifier(FOO + ">");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIsValidIdentifierAsKeywordThrowsException() throws Exception {
        isValidIdentifier(PUBLIC);
    }

    @Test
    public void testIsValidTypeName() throws Exception {
        assertThat(isValidTypeName(FOOBAR), is(FOOBAR));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIsValidTypeNameThrowsException() throws Exception {
        assertThat(isValidTypeName("." + FOO), is(FOOBAR));
    }

    @Test
    public void testIsNotEmpty() throws Exception {
        List<String> list = Arrays.asList(FOO);
        assertThat(isNotEmpty(list, FOO), sameInstance(list));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIsNotEmptyThrowsException() throws Exception {
        isNotEmpty(Arrays.<String>asList(), FOO);
    }

    @Test
    public void testIsEmpty() throws Exception {
        List<String> list = Collections.emptyList();
        assertThat(isEmpty(list, FOO), sameInstance(list));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIsEmptyThrowsException() throws Exception {
        isEmpty(Arrays.asList(BAR), FOO);
    }

    @Test
    public void testResolveModifierContributors() throws Exception {
        assertThat(resolveModifierContributors(ByteBuddyCommons.FIELD_MODIFIER_MASK,
                FieldManifestation.FINAL,
                Ownership.STATIC,
                Visibility.PRIVATE), is(Opcodes.ACC_FINAL | Opcodes.ACC_STATIC | Opcodes.ACC_PRIVATE));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testResolveModifierContributorsDuplication() throws Exception {
        resolveModifierContributors(Integer.MAX_VALUE, Ownership.STATIC, Ownership.MEMBER);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testResolveModifierContributorsMask() throws Exception {
        resolveModifierContributors(ModifierContributor.EMPTY_MASK, Ownership.STATIC);
    }

    @Test
    public void testUniqueForUniqueTypes() throws Exception {
        assertThat(uniqueTypes(Arrays.asList(first, second)), is(Arrays.asList(first, second)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUniqueForNonUniqueTypes() throws Exception {
        uniqueTypes(Arrays.asList(first, second, first));
    }

    @Test
    public void testIsThrowableForThrowables() throws Exception {
        assertThat(isThrowable(Arrays.asList(first)), is(Arrays.asList(first)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIsThrowableForNonThrowables() throws Exception {
        isThrowable(Arrays.asList(first, second));
    }

    @Test
    public void testConstructorIsHidden() throws Exception {
        assertThat(ByteBuddyCommons.class.getDeclaredConstructors().length, is(1));
        Constructor<?> constructor = ByteBuddyCommons.class.getDeclaredConstructor();
        assertThat(Modifier.isPrivate(constructor.getModifiers()), is(true));
        constructor.setAccessible(true);
        try {
            constructor.newInstance();
            fail();
        } catch (InvocationTargetException e) {
            assertEquals(UnsupportedOperationException.class, e.getCause().getClass());
        }
    }
}
