package com.baianat.app.annotations

@Retention(AnnotationRetention.RUNTIME)
@Target( AnnotationTarget.FIELD, AnnotationTarget.PROPERTY)
annotation class BindTo(@ResId val resId: Int)

