package com.client_progettosicurezza.compiler;

import android.content.Context;
import android.content.pm.ApplicationInfo;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import dalvik.system.DexClassLoader;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtMethod;
import javassist.android.DexFile;
import javassist.android.Log;
import javassist.environment.Environment;

public class Compile {

    private static final String DEX_FILE_NAME_MYCLASSES = "myclasses.dex";
    private static final boolean FORCE_GENRATE_DEX = true;
    private static File dexFile = null;
    private static DexClassLoader dc1;
    private String nameClass;

    private static File dir;
    private static Context context;

    public Compile(File dir, Context context) {
        this.dir = dir;
        this.context = context;
    }

    public void assemblyCompile(String codice) {
        dexFile = new File(dir, DEX_FILE_NAME_MYCLASSES);

        //Log.e("lunghezza: " + codice.length());

        if (!dexFile.exists() || FORCE_GENRATE_DEX) {
            try {
                // generate "xxx.class" file via Javassist.
                CtClass cls = null;

                // assembly class
                boolean isClass = codice.contains("class");
                if (isClass) {
                    Log.e("classe: " + isClass);

                    int indexStartClass = codice.indexOf("class") + 6;
                    int indexEndClass = codice.indexOf("{") - 1;
                    nameClass = codice.substring(indexStartClass, indexEndClass);

                    final ClassPool cp = ClassPool.getDefault(context);
                    cls = cp.makeClass(nameClass);
                }

                // assembly costructor
                boolean isCostructor = codice.contains(cls.getName() + "()");
                if (isCostructor) {
                    Log.e("costruttore: " + isCostructor);

                    final CtConstructor ctor = new CtConstructor(null, cls);
                    ctor.setBody("{}");
                    cls.addConstructor(ctor);
                }

                // assembly methods
                int indexStartMethod = codice.indexOf("// start method") + 15;
                int indexEndMethod = codice.indexOf("// end method");
                String method = codice.substring(indexStartMethod, indexEndMethod);
                Log.e("metodo: " + method);

                final CtMethod m1 = CtMethod.make(method, cls);
                cls.addMethod(m1);
                //cls.writeFile(dir.getAbsolutePath());
                cls.debugWriteFile(dir.getAbsolutePath());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void recompile() throws IOException {
        // convert from "xxx.class" to "xxx.dex"
        final DexFile df = new DexFile();
        final String dexFilePath = dexFile.getAbsolutePath();
        df.addClass(new File(dir, nameClass + ".class"));
        df.writeFile(dexFilePath);
    }

    public void load(File cacheDir, ApplicationInfo applicationInfo, ClassLoader classLoader) {
        dc1 = new DexClassLoader(dexFile.getAbsolutePath(), cacheDir.getAbsolutePath(), applicationInfo.nativeLibraryDir, classLoader);
    }

    public Object run() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {
        final Class<?> classe = dc1.loadClass(nameClass);
        final Constructor<?> costruttore = classe.getConstructor(new Class<?>[0]);
        final Object obj = costruttore.newInstance(new Object[0]);

        return obj;
    }
}
