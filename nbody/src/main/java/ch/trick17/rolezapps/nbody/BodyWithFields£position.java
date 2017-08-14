package ch.trick17.rolezapps.nbody;

public interface BodyWithFields£position {
    
    BodyWithFields $object();
    
    final class Impl extends rolez.lang.Guarded implements BodyWithFields£position {
        
        final BodyWithFields object;
        
        Impl(final BodyWithFields object) {
            super(true);
            this.object = object;
        }
        
        @java.lang.Override
        public BodyWithFields $object() {
            return object;
        }
        
        @java.lang.Override
        protected final java.util.List<BodyWithFields> views() {
            return java.util.Arrays.asList(object);
        }
        
        @java.lang.Override
        protected final BodyWithFields viewLock() {
            return object;
        }
    }
}
