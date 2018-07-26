package com.baianat.app.moltenprocessor

import com.baianat.app.annotations.BindTo
//import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.TypeSpec
import java.io.File
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement

//@AutoService(Processor::class)

@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes("org.kotlin.annotationProcessor.TestAnnotation")
@SupportedOptions(MoltenProcessor.KAPT_KOTLIN_GEN_MOLTEN_NAME)
class MoltenProcessor : AbstractProcessor() {

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        println("getSupportedMoltenAnnotationTypes")
        return mutableSetOf(BindTo::class.java.name)
    }

    override fun getSupportedSourceVersion(): SourceVersion {
        return SourceVersion.latest()
    }

    override fun process(p0: MutableSet<out TypeElement>?, p1: RoundEnvironment?): Boolean {

        p1?.getElementsAnnotatedWith(BindTo::class.java)
                ?.forEach {
                    val className = it.simpleName.toString()
                    println("processing: $className")
                    val pack = processingEnv.elementUtils.getPackageOf(it).toString()
                    generateClass(className, pack)
                }

        return true
    }

    private fun generateClass(className: String, packageName: String) {
        val fileName = "GenerateBindTo"
        val file = FileSpec.builder(packageName, fileName)
                .addType(TypeSpec.classBuilder(fileName)
                        .addFunction(FunSpec.builder("getName")
                                .addStatement("return \"MoltenCake World\"")
                                .build())
                        .build())
                .build()

        val kaptKotlinGeneratedDir = processingEnv.options[KAPT_KOTLIN_GEN_MOLTEN_NAME]
        file.writeTo(File(kaptKotlinGeneratedDir, "$fileName.kt"))
    }

    companion object {
        const val KAPT_KOTLIN_GEN_MOLTEN_NAME = "kapt.kotlin.generated"
    }
}