### Installation

------------

We publish the SDK to mavenCentral as an AAR file. Just declare it as dependency in your app level build.gradle file.
```
dependencies {      
         implementation 'io.looployalty.android:loop:1.1'
    }
```

### Dependencies

------------

Add the RX Relay dependency to receive login event from SDK discussed later to your app level build.gradle file
```
dependencies {      
         implementation 'com.jakewharton.rxrelay3:rxrelay:3.0.0'
    }
```

Also please make sure to include the following repository sources in your project level **build.gradle** --> **buildscript {}** block or **pluginManagement{}** and **dependencyResolutionManagement{}** block in **settings.gradle**

```
repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        jcenter()
        maven { url 'https://www.jitpack.io' }
    }
```

In **gradle.properties** make sure to add the following line:
```
android.enableJetifier=true
```

In app level **build.gradle** file make sure to keep the **minSdk** to 24 **defaultConfig{}** block:
```
defaultConfig {
        minSdk 24
    }
```

In the same app level **build.gradle** file please enable **dataBinding** in **android{}** block:
```
dataBinding {
        enabled = true
    }
```

### Initialization

------------

Add the following inside the **application** tags of your **AndroidManifest.xml**:

```
<meta-data  
     android:name="com.lib.loopsdk.INSTANCE_ID"  
     android:value="Your Loop ProjectID"/>  
<meta-data  
     android:name="com.lib.loopsdk.LOOP_ACCESS_KEY"  
     android:value="Your Loop SecretKey"/>
```
Replace "Your Loop ProjectID" and "Your Loop SecretKey" with actual values from your Loop Loyalty Dashboard

### Integration

------------

Add the **android:name** property to the **application** tag in your **AndroidManifest.xml**:
```
<application
        android:name=".App">
```
Here **App** class is your custom made Application class which you need to create subclassing it from Application() class of android if already not created. Then you need to call **LoopInstance.initialize(this)** before super.onCreate() in your custom Application class:

```
class App: Application() {

    override fun onCreate() {
        LoopInstance.initialize(this)
        super.onCreate()
    }
	
}
```
