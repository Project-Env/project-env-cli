package io.projectenv.core.cli.arch;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaCall;
import com.tngtech.archunit.core.domain.JavaMethodCall;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import io.projectenv.core.commons.system.EnvironmentVariables;

import static com.tngtech.archunit.core.domain.JavaClass.Predicates.type;
import static com.tngtech.archunit.core.domain.properties.HasName.Predicates.name;
import static com.tngtech.archunit.core.domain.properties.HasOwner.Predicates.With.owner;
import static com.tngtech.archunit.core.domain.properties.HasParameterTypes.Predicates.rawParameterTypes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(packages = "io.projectenv")
class ProjectEnvCliArchRulesTest {

    private static final DescribedPredicate<? super JavaMethodCall> SYSTEM_GETENV = createMethodPredicate(System.class, "getenv");

    @ArchTest
    public static final ArchRule disallowedSystemEnvAccess = noClasses()
            .that().areNotAssignableTo(EnvironmentVariables.class)
            .should().callMethodWhere(SYSTEM_GETENV);

    @ArchTest
    public static final ArchRule allowedSystemEnvAccess = classes()
            .that().areAssignableTo(EnvironmentVariables.class)
            .should().callMethodWhere(SYSTEM_GETENV);

    private static DescribedPredicate<? super JavaMethodCall> createMethodPredicate(Class<?> owner, String methodName, Class<?>... parameterTypes) {
        return JavaCall.Predicates.target(owner(type(owner)))
                .and(JavaCall.Predicates.target(name(methodName)))
                .and(JavaCall.Predicates.target(rawParameterTypes(parameterTypes)));
    }

}