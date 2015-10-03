package com.github.saadfarooq

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Ignore
import org.junit.Test

public class CommonDepsPluginTest {
    @Test
    public void withNoSupportClosure_shouldAddDefaultDependencies() {
        def project = createProject()
        project.evaluate()
        def deps = project.getConfigurations().getByName('compile').getDependencies()
        assert deps.size() == 0
    }

    @Test
    public void withSupportClosure_shouldEnableClosureDependencies() {
        def project = createProject()
        project.commonDeps {
            support {
                design true
                cardview true
            }
        }
        project.evaluate()
        def deps = project.getConfigurations().getByName('compile').getDependencies()
        assert deps.size() ==  2
        assert deps.contains('com.android.support:cardview_v7:23.0.1')
        assert deps.contains('com.android.support:design:23.0.1')
    }

    @Test
    public void withSupportClosure_shouldDisableDefaultDependencies() {
        def project = createProject()
        project.commonDeps {
            support {
                support false
                appcompat false
            }
        }
        project.evaluate()
        def deps = project.getConfigurations().getByName('compile').getDependencies()
        assert deps.size() == 0
    }

    @Test
    @Ignore("Android plugin needs to be applied for this to run but it needs SDK access")
    public void whenDaggerSpecified_shouldAddDaggerDependency_andApplyAptPlugin() throws Exception {
        def project = createProject()
        project.commonDeps {
            dagger 2
        }
        project.evaluate()
        def deps = project.getConfigurations().getByName('compile').getDependencies()
        assert deps.size() ==  1
        assert deps.contains('com.google.dagger:dagger:2.0.1')

        assert project.getPlugins().findPlugin('com.neenbedankt.android-apt') != null
    }

    @Test
    @Ignore("Android plugin needs to be applied for this to run but it needs SDK access")
    public void whenDaggerSpecifiedAsString_shouldAddTheSpecificDaggerVersion() throws Exception {
        def project = createProject()
        project.commonDeps {
            dagger '2.2.2'
        }
        project.evaluate()
        def deps = project.getConfigurations().getByName('compile').getDependencies()
        assert deps.size() ==  1
        assert deps.contains('com.google.dagger:dagger:2.2.2')
    }

    @Test
    public void whenButterKnifeSpecified_shouldAddButterknifeDependency() throws Exception {
        def project = createProject()
        project.commonDeps {
            butterknife true
        }
        project.evaluate()
        def deps = project.getConfigurations().getByName('compile').getDependencies()
        assert deps.size() == 1
        assert deps.contains('com.jakewharton:butterknife:7.0.1')
    }

    @Test
    public void whenButterKnifeVersionSpecified_shouldSpecificButterknifeDependency() throws Exception {
        def project = createProject()
        project.commonDeps {
            butterknife '6.5.0'
            picasso true
        }
        project.evaluate()
        def deps = project.getConfigurations().getByName('compile').getDependencies()
        assert deps.size() == 1
        assert deps.contains('com.jakewharton:butterknife:6.5.0')
    }

    @Test
    public void settingButterknife_false_doesNotAddButterknifeDependency() throws Exception {
        def project = createProject()
        project.commonDeps {
            butterknife false
        }
        project.evaluate()
        def deps = project.getConfigurations().getByName('compile').getDependencies()
        assert deps.size() == 0
        assert !deps.contains('com.jakewharton:butterknife:6.5.0')
    }

    @Test
    void shouldAddRobolectric() throws Exception {
        def project = createProject()
        project.commonDeps {
            testing {
                robolectric '3.0-rc1'
            }
        }
        project.evaluate()
        def deps = project.getConfigurations().getByName('testCompile').getDependencies()
        assert deps.size() == 1
        assert deps.contains("org.robolectric:robolectric:3.0-rc1")
    }

    @Test
    void shouldAddJunit() throws Exception {
        def project = createProject()
        project.commonDeps {
            testing {
                junit true
            }
        }
        project.evaluate()
        def deps = project.getConfigurations().getByName('testCompile').getDependencies()
        assert deps.size() == 1
        assert deps.contains("junit:junit:4.12")
    }

    def createProject() {
        Project project = ProjectBuilder.builder().build()
        project.getPluginManager().apply 'java'
        project.getPluginManager().apply 'com.github.saadfarooq.commondeps'

        project.repositories {
            jcenter()
            def androidHome = System.getenv("ANDROID_HOME")
            maven {
                url "$androidHome/extras/android/m2repository/"
            }
        }
        return project
    }

}