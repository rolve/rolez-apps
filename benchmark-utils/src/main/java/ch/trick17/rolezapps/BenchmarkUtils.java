package ch.trick17.rolezapps;

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
            paramTypes[i] = convertType(args[i]);
            
        try {
            Class<?> implClass = baseClass.getClassLoader().loadClass(baseClass.getName() + impl);
            return baseClass.cast(implClass.getConstructor(paramTypes).newInstance(args));
        } catch(ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }
    
    private static Class<?> convertType(Object object) {
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
        else if(c.isAnonymousClass())
            return c.getSuperclass();
        else
            return c;
    }
}
