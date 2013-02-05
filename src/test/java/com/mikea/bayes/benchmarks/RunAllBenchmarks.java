package com.mikea.bayes.benchmarks;

import com.google.caliper.Benchmark;
import com.google.caliper.Runner;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.ClassPath;

import javax.annotation.Nullable;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;

import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.toArray;
import static java.util.Arrays.asList;

/**
 * @author mike.aizatsky@gmail.com
 */
public final class RunAllBenchmarks {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        URLClassLoader parentLoader = (URLClassLoader) RunAllBenchmarks.class.getClassLoader();

        URL[] urls = parentLoader.getURLs();
        urls = toArray(filter(asList(urls), new Predicate<URL>() {
            @Override
            public boolean apply(@Nullable URL input) {
                return !input.toString().endsWith(".jnilib");
            }
        }), URL.class);

        ClassPath classPath = ClassPath.from(new URLClassLoader(urls, null));
        ImmutableSet<ClassPath.ClassInfo> classes = classPath.getTopLevelClassesRecursive("com.mikea.bayes.benchmarks");
        for (ClassPath.ClassInfo classInfo : classes) {
            Class<?> clazz = Class.forName(classInfo.getName());
            if (Benchmark.class.isAssignableFrom(clazz)) {
                Class<? extends Benchmark> benchmarkClass = clazz.asSubclass(Benchmark.class);
                System.out.println("Running " + clazz);
                Runner.main(benchmarkClass, args);
            }
        }
    }


}
