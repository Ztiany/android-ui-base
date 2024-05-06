plugins {
    alias(libs.plugins.app.common.library)
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
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.annotations)
    implementation(libs.androidx.viewpager)
    implementation(libs.androidx.recyclerview)
    implementation(libs.google.ui.material)
    implementation(libs.androidx.ktx)

    // kotlin
    implementation(libs.kotlin.stdlib)

    // third
    api(libs.ztiany.drawableView)

    // log
    implementation(libs.jakewharton.timber)
}