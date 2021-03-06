package net.bytebuddy.dynamic.scaffold.subclass;

import net.bytebuddy.dynamic.scaffold.MethodRegistry;
import net.bytebuddy.instrumentation.SuperMethodCall;
import net.bytebuddy.instrumentation.attribute.MethodAttributeAppender;
import net.bytebuddy.instrumentation.method.MethodDescription;
import net.bytebuddy.instrumentation.method.MethodList;
import net.bytebuddy.instrumentation.type.TypeDescription;

import static net.bytebuddy.matcher.ElementMatchers.*;

/**
 * A constructor strategy is responsible for creating bootstrap constructors for a
 * {@link SubclassDynamicTypeBuilder}.
 *
 * @see net.bytebuddy.dynamic.scaffold.subclass.ConstructorStrategy.Default
 */
public interface ConstructorStrategy {

    /**
     * Extracts constructors for a given super type. The extracted constructor signatures will then be imitated by the
     * created dynamic type.
     *
     * @param instrumentedType The type for which the constructors should be created.
     * @return A list of constructor descriptions which will be mimicked by the instrumented type of which
     * the {@code superType} is the direct super type of the instrumented type.
     */
    MethodList extractConstructors(TypeDescription instrumentedType);

    /**
     * Returns a method registry that is capable of creating byte code for the constructors that were
     * provided by the
     * {@link net.bytebuddy.dynamic.scaffold.subclass.ConstructorStrategy#extractConstructors(net.bytebuddy.instrumentation.type.TypeDescription)}
     * method of this instance.
     *
     * @param methodRegistry                        The original method registry.
     * @param defaultMethodAttributeAppenderFactory The default method attribute appender factory.
     * @return A method registry that is capable of providing byte code for the constructors that were added by
     * this strategy.
     */
    MethodRegistry inject(MethodRegistry methodRegistry,
                          MethodAttributeAppender.Factory defaultMethodAttributeAppenderFactory);

    /**
     * Default implementations of constructor strategies.
     */
    static enum Default implements ConstructorStrategy {

        /**
         * This strategy is adding no constructors such that the instrumented type will by default not have any. This
         * is legal by Java byte code requirements. However, if no constructor is added manually if this strategy is
         * applied, the type is not constructable without using JVM non-public functionality.
         */
        NO_CONSTRUCTORS {
            @Override
            public MethodList extractConstructors(TypeDescription superType) {
                return new MethodList.Empty();
            }
        },

        /**
         * This strategy is adding a default constructor that calls it's super types default constructor. If no such
         * constructor is defined, an {@link IllegalArgumentException} is thrown. Note that the default constructor
         * needs to be visible to its sub type for this strategy to work.
         */
        DEFAULT_CONSTRUCTOR {
            @Override
            public MethodList extractConstructors(TypeDescription instrumentedType) {
                MethodList methodList = instrumentedType.getSupertype()
                        .getDeclaredMethods()
                        .filter(isConstructor().and(takesArguments(0)).<MethodDescription>and(isVisibleTo(instrumentedType)));
                if (methodList.size() == 1) {
                    return methodList;
                } else {
                    throw new IllegalArgumentException(String.format("%s does not declare a default constructor that " +
                            "is visible to %s", instrumentedType.getSupertype(), instrumentedType));
                }
            }
        },

        /**
         * This strategy is adding all constructors of the instrumented type's super type where each constructor is
         * directly invoking its signature-equivalent super type constructor. Only constructors that are visible to the
         * instrumented type are added, i.e. package-private constructors are only added if the super type is defined
         * in the same package as the instrumented type and private constructors are always skipped.
         */
        IMITATE_SUPER_TYPE {
            @Override
            public MethodList extractConstructors(TypeDescription instrumentedType) {
                return instrumentedType.getSupertype()
                        .getDeclaredMethods()
                        .filter(isConstructor().<MethodDescription>and(isVisibleTo(instrumentedType)));
            }
        },

        /**
         * This strategy is adding all constructors of the instrumented type's super type where each constructor is
         * directly invoking its signature-equivalent super type constructor. Only {@code public} constructors are
         * added.
         */
        IMITATE_SUPER_TYPE_PUBLIC {
            @Override
            public MethodList extractConstructors(TypeDescription instrumentedType) {
                return instrumentedType.getSupertype()
                        .getDeclaredMethods()
                        .filter(isPublic().and(isConstructor()));
            }
        };

        @Override
        public MethodRegistry inject(MethodRegistry methodRegistry,
                                     MethodAttributeAppender.Factory defaultMethodAttributeAppenderFactory) {
            switch (this) {
                case NO_CONSTRUCTORS:
                    return methodRegistry;
                case DEFAULT_CONSTRUCTOR:
                case IMITATE_SUPER_TYPE:
                case IMITATE_SUPER_TYPE_PUBLIC:
                    return methodRegistry.prepend(new MethodRegistry.LatentMethodMatcher.Simple(isConstructor()),
                            SuperMethodCall.INSTANCE,
                            defaultMethodAttributeAppenderFactory);
                default:
                    throw new AssertionError();
            }
        }
    }
}
