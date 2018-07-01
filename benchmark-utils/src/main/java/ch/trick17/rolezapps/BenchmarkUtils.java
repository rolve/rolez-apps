package ch.trick17.rolezapps;

import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.Collection;

import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.results.BenchmarkResult;
import org.openjdk.jmh.results.IterationResult;
import org.openjdk.jmh.results.RunResult;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;

public final class BenchmarkUtils {
    
    private BenchmarkUtils() {}
    
    /**
     * Finds the benchmark class given by the <code>baseClass</code> and the
     * <code>implementation</code> string and instantiates it using the given constructor arguments.
     * <p>
     * This method assumes that the name of the benchmark class is equal to the name of the base
     * class, with <code>implementation</code> appended, and that the implementation class extends
     * the base class.
     * <p>
     * Further, it assumes that the implementation class has a public constructor with parameter
     * types corresponding to the types of the given arguments (with wrapper types replaced with
     * their primitive counterparts, e.g., {@link Integer} replaced with <code>int</code>).
     */
    public static <T> T instantiateBenchmark(Class<T> baseClass, String impl, Object... args) {
        Class<?>[] paramTypes = new Class<?>[args.length];
        for(int i = 0; i < args.length; i++)
            paramTypes[i] = primitiveTypeOf(args[i]);
            
        try {
            Class<?> implClass = baseClass.getClassLoader().loadClass(baseClass.getName() + impl);
            return baseClass.cast(implClass.getConstructor(paramTypes).newInstance(args));
        } catch(ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }
    
    private static Class<?> primitiveTypeOf(Object object) {
        Class<?> c = object.getClass();
        
        if(c == Boolean.class)
            return Boolean.TYPE;
        else if(c == Byte.class)
            return Byte.TYPE;
        else if(c == Character.class)
            return Character.TYPE;
        else if(c == Double.class)
            return Double.TYPE;
        else if(c == Float.class)
            return Float.TYPE;
        else if(c == Integer.class)
            return Integer.TYPE;
        else if(c == Long.class)
            return Long.TYPE;
        else if(c == Short.class)
            return Short.TYPE;
        else
            return c;
    }
    
    public static void runAndStoreResults(Options options) {
        try {
            try(PrintStream out = new PrintStream("results.tsv")) {
                Collection<RunResult> runs = new Runner(options).run();
                
                RunResult someRun = runs.iterator().next();
                Collection<String> keys = someRun.getAggregatedResult().getParams().getParamsKeys();
                out.print("benchmark\t");
                for(String key : keys)
                    out.print(key + "\t");
                out.println("time");
                
                for(RunResult run : runs) {
                    BenchmarkResult result = run.getAggregatedResult();
                    String[] benchmarkName = result.getParams().getBenchmark().split("\\.");
                    String shortName = benchmarkName[benchmarkName.length - 1];
                    
                    for(IterationResult iteration : result.getIterationResults()) {
                        out.print(shortName + "\t");
                        for(String key : keys)
                            out.print(result.getParams().getParam(key) + "\t");
                        out.println(iteration.getPrimaryResult().getScore());
                    }
                }
            }
        } catch(IOException | RunnerException e) {
            throw new AssertionError(e);
        }
    }
    
    public static int intValueForParam(Object container, String fieldName) {
        try {
            Field field = getField(container.getClass(), fieldName);
            field.setAccessible(true);
            
            String[] surrogates = field.getAnnotation(Param.class).value();
            int[] values = field.getAnnotation(IntValues.class).value();
            assert surrogates.length == values.length;
            
            Object currentSurrogate = field.get(container);
            for(int i = 0; i < surrogates.length; i++)
                if(surrogates[i].equals(currentSurrogate))
                    return values[i];
            
            throw new AssertionError("no match");
        } catch(IllegalAccessException e) {
            throw new AssertionError(e);
        }
    }

    private static Field getField(Class<?> clazz, String fieldName) {
        try {
            return clazz.getDeclaredField(fieldName);
        } catch(NoSuchFieldException e) {
            return getField(clazz.getSuperclass(), fieldName);
        } catch (NullPointerException e) {
            throw new AssertionError("field " + fieldName + " not found");
        }
    }
}
