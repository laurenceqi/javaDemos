import sun.net.www.ParseUtil;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * Created by laurenceqi on 18/3/28.
 */
public class TestClassLoader {

    static class MyClassLoader extends URLClassLoader {
        MyClassLoader() {
            super(new URL[]{});
            final String var1 = System.getProperty("java.class.path");
            final File[] var2 = var1 == null?new File[0]: Utils.getClassPath(var1);
            for (URL url: Utils.pathToURLs(var2)) {
                addURL(url);
            }
        }


        @Override
        public Class<?> loadClass(String name) throws ClassNotFoundException {
            if(name.contains("TestClassLoader"))
                return this.findClass(name);
            return super.loadClass(name);
        }

    }

    static class A {

    }

    public static class B {
        public static B1 b1 = new B1();
    }

    static class B1 {

    }

    static class C {

    }

    static class D {

    }

    static void  printClassloadHierarchy(Class cls) {
        System.out.println(cls.toString());
        for(ClassLoader cl =  cls.getClassLoader() ; cl != null ; cl = cl.getParent()){
            System.out.println("     " + cl.toString());
        }
    }

    public static void main(String[] args) {
        MyClassLoader mcl = new MyClassLoader();
        System.out.println(mcl);

        Thread.currentThread().setContextClassLoader(mcl);

        printClassloadHierarchy(TestClassLoader.class);

        A a = new A();
        printClassloadHierarchy(a.getClass());

        try {
            Class cls = mcl.loadClass("TestClassLoader$B");
            printClassloadHierarchy(cls.newInstance().getClass());
            printClassloadHierarchy(cls.getField("b1").get(cls.newInstance()).getClass());

        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            printClassloadHierarchy(Class.forName("TestClassLoader$C"));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        try {
            printClassloadHierarchy(Class.forName("TestClassLoader$D", true, mcl));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    static class Utils {
        private static URL[] pathToURLs(File[] var0) {
            URL[] var1 = new URL[var0.length];

            for(int var2 = 0; var2 < var0.length; ++var2) {
                var1[var2] = getFileURL(var0[var2]);
            }

            return var1;
        }

        private static File[] getClassPath(String var0) {
            File[] var1;
            if(var0 != null) {
                int var2 = 0;
                int var3 = 1;
                boolean var4 = false;

                int var5;
                int var7;
                for(var5 = 0; (var7 = var0.indexOf(File.pathSeparator, var5)) != -1; var5 = var7 + 1) {
                    ++var3;
                }

                var1 = new File[var3];
                var4 = false;

                for(var5 = 0; (var7 = var0.indexOf(File.pathSeparator, var5)) != -1; var5 = var7 + 1) {
                    if(var7 - var5 > 0) {
                        var1[var2++] = new File(var0.substring(var5, var7));
                    } else {
                        var1[var2++] = new File(".");
                    }
                }

                if(var5 < var0.length()) {
                    var1[var2++] = new File(var0.substring(var5));
                } else {
                    var1[var2++] = new File(".");
                }

                if(var2 != var3) {
                    File[] var6 = new File[var2];
                    System.arraycopy(var1, 0, var6, 0, var2);
                    var1 = var6;
                }
            } else {
                var1 = new File[0];
            }

            return var1;
        }

        static URL getFileURL(File var0) {
            try {
                var0 = var0.getCanonicalFile();
            } catch (IOException var3) {
                ;
            }

            try {
                return ParseUtil.fileToEncodedURL(var0);
            } catch (MalformedURLException var2) {
                throw new InternalError(var2);
            }
        }
    }

}
