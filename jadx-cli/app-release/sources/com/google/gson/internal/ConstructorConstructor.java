package com.google.gson.internal;

import com.google.gson.InstanceCreator;
import com.google.gson.JsonIOException;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

public final class ConstructorConstructor {
    private final Map<Type, InstanceCreator<?>> instanceCreators;

    public ConstructorConstructor(Map<Type, InstanceCreator<?>> map) {
        this.instanceCreators = map;
    }

    public <T> ObjectConstructor<T> get(TypeToken<T> typeToken) {
        final Type type = typeToken.getType();
        Class rawType = typeToken.getRawType();
        final InstanceCreator instanceCreator = (InstanceCreator) this.instanceCreators.get(type);
        if (instanceCreator != null) {
            return new ObjectConstructor<T>() {
                public T construct() {
                    return instanceCreator.createInstance(type);
                }
            };
        }
        instanceCreator = (InstanceCreator) this.instanceCreators.get(rawType);
        if (instanceCreator != null) {
            return new ObjectConstructor<T>() {
                public T construct() {
                    return instanceCreator.createInstance(type);
                }
            };
        }
        ObjectConstructor<T> newDefaultConstructor = newDefaultConstructor(rawType);
        if (newDefaultConstructor != null) {
            return newDefaultConstructor;
        }
        newDefaultConstructor = newDefaultImplementationConstructor(type, rawType);
        if (newDefaultConstructor != null) {
            return newDefaultConstructor;
        }
        return newUnsafeAllocator(type, rawType);
    }

    private <T> ObjectConstructor<T> newDefaultConstructor(Class<? super T> cls) {
        try {
            final Constructor declaredConstructor = cls.getDeclaredConstructor(new Class[0]);
            if (!declaredConstructor.isAccessible()) {
                declaredConstructor.setAccessible(true);
            }
            return new ObjectConstructor<T>() {
                public T construct() {
                    StringBuilder stringBuilder;
                    try {
                        return declaredConstructor.newInstance(null);
                    } catch (Throwable e) {
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("Failed to invoke ");
                        stringBuilder.append(declaredConstructor);
                        stringBuilder.append(" with no args");
                        throw new RuntimeException(stringBuilder.toString(), e);
                    } catch (InvocationTargetException e2) {
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("Failed to invoke ");
                        stringBuilder.append(declaredConstructor);
                        stringBuilder.append(" with no args");
                        throw new RuntimeException(stringBuilder.toString(), e2.getTargetException());
                    } catch (IllegalAccessException e3) {
                        throw new AssertionError(e3);
                    }
                }
            };
        } catch (NoSuchMethodException unused) {
            return null;
        }
    }

    private <T> ObjectConstructor<T> newDefaultImplementationConstructor(final Type type, Class<? super T> cls) {
        if (Collection.class.isAssignableFrom(cls)) {
            if (SortedSet.class.isAssignableFrom(cls)) {
                return new ObjectConstructor<T>() {
                    public T construct() {
                        return new TreeSet();
                    }
                };
            }
            if (EnumSet.class.isAssignableFrom(cls)) {
                return new ObjectConstructor<T>() {
                    public T construct() {
                        StringBuilder stringBuilder;
                        if (type instanceof ParameterizedType) {
                            Type type = ((ParameterizedType) type).getActualTypeArguments()[0];
                            if (type instanceof Class) {
                                return EnumSet.noneOf((Class) type);
                            }
                            stringBuilder = new StringBuilder();
                            stringBuilder.append("Invalid EnumSet type: ");
                            stringBuilder.append(type.toString());
                            throw new JsonIOException(stringBuilder.toString());
                        }
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("Invalid EnumSet type: ");
                        stringBuilder.append(type.toString());
                        throw new JsonIOException(stringBuilder.toString());
                    }
                };
            }
            if (Set.class.isAssignableFrom(cls)) {
                return new ObjectConstructor<T>() {
                    public T construct() {
                        return new LinkedHashSet();
                    }
                };
            }
            if (Queue.class.isAssignableFrom(cls)) {
                return new ObjectConstructor<T>() {
                    public T construct() {
                        return new LinkedList();
                    }
                };
            }
            return new ObjectConstructor<T>() {
                public T construct() {
                    return new ArrayList();
                }
            };
        } else if (!Map.class.isAssignableFrom(cls)) {
            return null;
        } else {
            if (SortedMap.class.isAssignableFrom(cls)) {
                return new ObjectConstructor<T>() {
                    public T construct() {
                        return new TreeMap();
                    }
                };
            }
            if (!(type instanceof ParameterizedType) || String.class.isAssignableFrom(TypeToken.get(((ParameterizedType) type).getActualTypeArguments()[0]).getRawType())) {
                return new ObjectConstructor<T>() {
                    public T construct() {
                        return new LinkedTreeMap();
                    }
                };
            }
            return new ObjectConstructor<T>() {
                public T construct() {
                    return new LinkedHashMap();
                }
            };
        }
    }

    private <T> ObjectConstructor<T> newUnsafeAllocator(final Type type, final Class<? super T> cls) {
        return new ObjectConstructor<T>() {
            private final UnsafeAllocator unsafeAllocator = UnsafeAllocator.create();

            public T construct() {
                try {
                    return this.unsafeAllocator.newInstance(cls);
                } catch (Throwable e) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("Unable to invoke no-args constructor for ");
                    stringBuilder.append(type);
                    stringBuilder.append(". ");
                    stringBuilder.append("Register an InstanceCreator with Gson for this type may fix this problem.");
                    throw new RuntimeException(stringBuilder.toString(), e);
                }
            }
        };
    }

    public String toString() {
        return this.instanceCreators.toString();
    }
}
