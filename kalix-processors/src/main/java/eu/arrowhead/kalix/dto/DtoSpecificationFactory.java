package eu.arrowhead.kalix.dto;

import com.squareup.javapoet.*;
import eu.arrowhead.kalix.dto.types.DtoDescriptor;
import eu.arrowhead.kalix.dto.types.DtoInterface;
import eu.arrowhead.kalix.dto.types.DtoList;
import eu.arrowhead.kalix.dto.types.DtoPrimitiveUnboxed;

import javax.lang.model.element.Modifier;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

public class DtoSpecificationFactory {
    private final DtoSpecificationFormat[] specificationFormats;

    public DtoSpecificationFactory(final DtoSpecificationFormat... specificationFormats) {
        this.specificationFormats = specificationFormats;
    }

    public DtoTargetSpecification createForTarget(final DtoTarget target) throws DtoException {
        final var interfaceType = target.interfaceType();

        final var implementation = TypeSpec.classBuilder(target.simpleName())
            .addJavadoc("{@link $N} Data Transfer Object (DTO).", interfaceType.simpleName())
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
            .addSuperinterface(TypeName.get(interfaceType.asTypeMirror()));

        final var builderSimpleName = interfaceType.simpleName() + "Builder";
        final var builderClassName = ClassName.bestGuess(builderSimpleName);
        final var builder = TypeSpec.classBuilder(builderSimpleName)
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL);

        final var constructor = MethodSpec.constructorBuilder()
            .addParameter(ParameterSpec.builder(builderClassName, "builder")
                .addModifiers(Modifier.FINAL)
                .build());

        target.properties().forEach(property -> {
            final var descriptor = property.descriptor();
            final var name = property.name();
            final var type = property.type() instanceof DtoInterface
                ? ClassName.bestGuess(((DtoInterface) property.type()).targetSimpleName())
                : TypeName.get(property.asTypeMirror());

            implementation.addField(FieldSpec.builder(type, name, Modifier.PRIVATE).build());
            implementation.addMethod(MethodSpec.methodBuilder(name)
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(property.isOptional()
                    ? ParameterizedTypeName.get(ClassName.get(Optional.class), type)
                    : type)
                .addStatement(property.isOptional()
                    ? "return Optional.ofNullable($N)"
                    : "return $N", name)
                .build());

            builder.addField(FieldSpec.builder(type, name).build());
            final var fieldSetter = MethodSpec.methodBuilder(name)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(type, name, Modifier.FINAL)
                .returns(builderClassName)
                .addStatement("this.$1N = $1N", name)
                .addStatement("return this");

            if (descriptor == DtoDescriptor.ARRAY) {
                fieldSetter.varargs();
            }

            builder.addMethod(fieldSetter.build());

            if (descriptor == DtoDescriptor.LIST) {
                final var element = ((DtoList) property.type()).element();
                if (!element.descriptor().isCollection()) {
                    final var elementType = element instanceof DtoInterface
                        ? ClassName.bestGuess(((DtoInterface) element).targetSimpleName())
                        : TypeName.get(element.asTypeMirror());

                    builder.addMethod(MethodSpec.methodBuilder(name)
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(ArrayTypeName.of(elementType), name, Modifier.FINAL)
                        .varargs()
                        .returns(builderClassName)
                        .addStatement("this.$1N = $2T.asList($1N)", name, Arrays.class)
                        .addStatement("return this")
                        .build());
                }
            }

            if (property.isOptional() || property.type() instanceof DtoPrimitiveUnboxed) {
                constructor.addStatement("this.$1N = builder.$1N", name);
            }
            else {
                constructor.addStatement("this.$1N = $2T.requireNonNull(builder.$1N, \"Expected $1N\")",
                    name, Objects.class);
            }
        });


        final var targetFormats = target.formats();
        for (final var specificationFormat : specificationFormats) {
            if (targetFormats.contains(specificationFormat.format())) {
                specificationFormat.implementFor(target, implementation);
            }
        }

        return new DtoTargetSpecification.Builder(target)
            .implementation(implementation
                .addMethod(constructor.build())
                .build())
            .builder(builder
                .addMethod(MethodSpec.methodBuilder("build")
                    .addModifiers(Modifier.PUBLIC)
                    .returns(ClassName.bestGuess(target.simpleName()))
                    .addStatement("return new $N(this)", target.simpleName())
                    .build())
                .build())
            .build();
    }
}
