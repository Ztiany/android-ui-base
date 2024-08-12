plugins {
    alias(libs.plugins.app.common.library)
    alias(libs.plugins.vanniktech.maven.publisher)
}

android {
    namespace = "com.android.base.ui"

    //如果不想生成某个布局的绑定类，可以在根视图添加 tools:viewBindingIgnore="true" 属性。
    buildFeatures {
        viewBinding = true
    }

    sourceSets {
        getByName("main") {
            java.srcDir("src/github/java")
            res.srcDir("src/github/res")

            java.srcDir("src/androidx/java")
            res.srcDir("src/androidx/res")
        }
    }
}

dependencies {
    // androidx
    api(libs.androidx.appcompat)
    api(libs.androidx.annotations)
    api(libs.androidx.viewpager)
    api(libs.androidx.recyclerview)
    api(libs.google.ui.material)
    api(libs.androidx.ktx)
    // kotlin
    api(libs.kotlin.stdlib)
    // third
    api(libs.ztiany.drawableView)
    // log
    implementation(libs.jakewharton.timber)
}