package com.google.gson.internal;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Properties;

/* renamed from: com.google.gson.internal.$Gson$Types */
public final class C$Gson$Types {
    static final Type[] EMPTY_TYPE_ARRAY = new Type[0];

    /* compiled from: $Gson$Types */
    private static final class GenericArrayTypeImpl implements GenericArrayType, Serializable {
        private static final long serialVersionUID = 0;
        private final Type componentType;

        public GenericArrayTypeImpl(Type type) {
            this.componentType = C$Gson$Types.canonicalize(type);
        }

        public Type getGenericComponentType() {
            return this.componentType;
        }

        public boolean equals(Object obj) {
            return (obj instanceof GenericArrayType) && C$Gson$Types.equals(this, (GenericArrayType) obj);
        }

        public int hashCode() {
            return this.componentType.hashCode();
        }

        public String toString() {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(C$Gson$Types.typeToString(this.componentType));
            stringBuilder.append("[]");
            return stringBuilder.toString();
        }
    }

    /* compiled from: $Gson$Types */
    private static final class ParameterizedTypeImpl implements ParameterizedType, Serializable {
        private static final long serialVersionUID = 0;
        private final Type ownerType;
        private final Type rawType;
        private final Type[] typeArguments;

        public ParameterizedTypeImpl(Type type, Type type2, Type... typeArr) {
            int i = 0;
            if (type2 instanceof Class) {
                Class cls = (Class) type2;
                boolean z = true;
                int i2 = (Modifier.isStatic(cls.getModifiers()) || cls.getEnclosingClass() == null) ? z : i;
                if (type == null && i2 == 0) {
                    z = i;
                }
                C$Gson$Preconditions.checkArgument(z);
            }
            if (type == null) {
                type = null;
            } else {
                type = C$Gson$Types.canonicalize(type);
            }
            this.ownerType = type;
            this.rawType = C$Gson$Types.canonicalize(type2);
            this.typeArguments = (Type[]) typeArr.clone();
            while (i < this.typeArguments.length) {
                C$Gson$Preconditions.checkNotNull(this.typeArguments[i]);
                C$Gson$Types.checkNotPrimitive(this.typeArguments[i]);
                this.typeArguments[i] = C$Gson$Types.canonicalize(this.typeArguments[i]);
                i++;
            }
        }

        public Type[] getActualTypeArguments() {
            return (Type[]) this.typeArguments.clone();
        }

        public Type getRawType() {
            return this.rawType;
        }

        public Type getOwnerType() {
            return this.ownerType;
        }

        public boolean equals(Object obj) {
            return (obj instanceof ParameterizedType) && C$Gson$Types.equals(this, (ParameterizedType) obj);
        }

        public int hashCode() {
            return (Arrays.hashCode(this.typeArguments) ^ this.rawType.hashCode()) ^ C$Gson$Types.hashCodeOrZero(this.ownerType);
        }

        public String toString() {
            int i = 1;
            StringBuilder stringBuilder = new StringBuilder(30 * (this.typeArguments.length + i));
            stringBuilder.append(C$Gson$Types.typeToString(this.rawType));
            if (this.typeArguments.length == 0) {
                return stringBuilder.toString();
            }
            stringBuilder.append("<");
            stringBuilder.append(C$Gson$Types.typeToString(this.typeArguments[0]));
            while (i < this.typeArguments.length) {
                stringBuilder.append(", ");
                stringBuilder.append(C$Gson$Types.typeToString(this.typeArguments[i]));
                i++;
            }
            stringBuilder.append(">");
            return stringBuilder.toString();
        }
    }

    /* compiled from: $Gson$Types */
    private static final class WildcardTypeImpl implements WildcardType, Serializable {
        private static final long serialVersionUID = 0;
        private final Type lowerBound;
        private final Type upperBound;

        public WildcardTypeImpl(Type[] typeArr, Type[] typeArr2) {
            boolean z = true;
            boolean z2 = false;
            C$Gson$Preconditions.checkArgument(typeArr2.length <= z ? z : z2);
            C$Gson$Preconditions.checkArgument(typeArr.length == z ? z : z2);
            if (typeArr2.length == z) {
                C$Gson$Preconditions.checkNotNull(typeArr2[z2]);
                C$Gson$Types.checkNotPrimitive(typeArr2[z2]);
                if (typeArr[z2] != Object.class) {
                    z = z2;
                }
                C$Gson$Preconditions.checkArgument(z);
                this.lowerBound = C$Gson$Types.canonicalize(typeArr2[z2]);
                this.upperBound = Object.class;
                return;
            }
            C$Gson$Preconditions.checkNotNull(typeArr[z2]);
            C$Gson$Types.checkNotPrimitive(typeArr[z2]);
            this.lowerBound = null;
            this.upperBound = C$Gson$Types.canonicalize(typeArr[z2]);
        }

        public Type[] getUpperBounds() {
            Type[] typeArr = new Type[1];
            typeArr[0] = this.upperBound;
            return typeArr;
        }

        public Type[] getLowerBounds() {
            if (this.lowerBound == null) {
                return C$Gson$Types.EMPTY_TYPE_ARRAY;
            }
            return new Type[]{this.lowerBound};
        }

        public boolean equals(Object obj) {
            return (obj instanceof WildcardType) && C$Gson$Types.equals(this, (WildcardType) obj);
        }

        public int hashCode() {
            int i = 31;
            return (this.lowerBound != null ? this.lowerBound.hashCode() + i : 1) ^ (i + this.upperBound.hashCode());
        }

        public String toString() {
            StringBuilder stringBuilder;
            if (this.lowerBound != null) {
                stringBuilder = new StringBuilder();
                stringBuilder.append("? super ");
                stringBuilder.append(C$Gson$Types.typeToString(this.lowerBound));
                return stringBuilder.toString();
            } else if (this.upperBound == Object.class) {
                return "?";
            } else {
                stringBuilder = new StringBuilder();
                stringBuilder.append("? extends ");
                stringBuilder.append(C$Gson$Types.typeToString(this.upperBound));
                return stringBuilder.toString();
            }
        }
    }

    private C$Gson$Types() {
    }

    public static ParameterizedType newParameterizedTypeWithOwner(Type type, Type type2, Type... typeArr) {
        return new ParameterizedTypeImpl(type, type2, typeArr);
    }

    public static GenericArrayType arrayOf(Type type) {
        return new GenericArrayTypeImpl(type);
    }

    public static WildcardType subtypeOf(Type type) {
        return new WildcardTypeImpl(new Type[]{type}, EMPTY_TYPE_ARRAY);
    }

    public static WildcardType supertypeOf(Type type) {
        int i = 1;
        Type[] typeArr = new Type[i];
        int i2 = 0;
        typeArr[i2] = Object.class;
        Type[] typeArr2 = new Type[i];
        typeArr2[i2] = type;
        return new WildcardTypeImpl(typeArr, typeArr2);
    }

    public static Type canonicalize(Type type) {
        if (type instanceof Class) {
            Class cls = (Class) type;
            if (cls.isArray()) {
                cls = new GenericArrayTypeImpl(C$Gson$Types.canonicalize(cls.getComponentType()));
            }
            return cls;
        } else if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            return new ParameterizedTypeImpl(parameterizedType.getOwnerType(), parameterizedType.getRawType(), parameterizedType.getActualTypeArguments());
        } else if (type instanceof GenericArrayType) {
            return new GenericArrayTypeImpl(((GenericArrayType) type).getGenericComponentType());
        } else {
            if (!(type instanceof WildcardType)) {
                return type;
            }
            WildcardType wildcardType = (WildcardType) type;
            return new WildcardTypeImpl(wildcardType.getUpperBounds(), wildcardType.getLowerBounds());
        }
    }

    public static Class<?> getRawType(Type type) {
        if (type instanceof Class) {
            return (Class) type;
        }
        if (type instanceof ParameterizedType) {
            type = ((ParameterizedType) type).getRawType();
            C$Gson$Preconditions.checkArgument(type instanceof Class);
            return (Class) type;
        }
        int i = 0;
        if (type instanceof GenericArrayType) {
            return Array.newInstance(C$Gson$Types.getRawType(((GenericArrayType) type).getGenericComponentType()), i).getClass();
        }
        if (type instanceof TypeVariable) {
            return Object.class;
        }
        if (type instanceof WildcardType) {
            return C$Gson$Types.getRawType(((WildcardType) type).getUpperBounds()[i]);
        }
        String str;
        if (type == null) {
            str = "null";
        } else {
            str = type.getClass().getName();
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Expected a Class, ParameterizedType, or GenericArrayType, but <");
        stringBuilder.append(type);
        stringBuilder.append("> is of type ");
        stringBuilder.append(str);
        throw new IllegalArgumentException(stringBuilder.toString());
    }

    static boolean equal(Object obj, Object obj2) {
        return obj == obj2 || (obj != null && obj.equals(obj2));
    }

    public static boolean equals(Type type, Type type2) {
        boolean z = true;
        if (type == type2) {
            return z;
        }
        if (type instanceof Class) {
            return type.equals(type2);
        }
        boolean z2 = false;
        if (type instanceof ParameterizedType) {
            if (!(type2 instanceof ParameterizedType)) {
                return z2;
            }
            ParameterizedType parameterizedType = (ParameterizedType) type;
            ParameterizedType parameterizedType2 = (ParameterizedType) type2;
            if (!(C$Gson$Types.equal(parameterizedType.getOwnerType(), parameterizedType2.getOwnerType()) && parameterizedType.getRawType().equals(parameterizedType2.getRawType()) && Arrays.equals(parameterizedType.getActualTypeArguments(), parameterizedType2.getActualTypeArguments()))) {
                z = z2;
            }
            return z;
        } else if (type instanceof GenericArrayType) {
            if (!(type2 instanceof GenericArrayType)) {
                return z2;
            }
            return C$Gson$Types.equals(((GenericArrayType) type).getGenericComponentType(), ((GenericArrayType) type2).getGenericComponentType());
        } else if (type instanceof WildcardType) {
            if (!(type2 instanceof WildcardType)) {
                return z2;
            }
            WildcardType wildcardType = (WildcardType) type;
            WildcardType wildcardType2 = (WildcardType) type2;
            if (!(Arrays.equals(wildcardType.getUpperBounds(), wildcardType2.getUpperBounds()) && Arrays.equals(wildcardType.getLowerBounds(), wildcardType2.getLowerBounds()))) {
                z = z2;
            }
            return z;
        } else if (!(type instanceof TypeVariable) || !(type2 instanceof TypeVariable)) {
            return z2;
        } else {
            TypeVariable typeVariable = (TypeVariable) type;
            TypeVariable typeVariable2 = (TypeVariable) type2;
            if (!(typeVariable.getGenericDeclaration() == typeVariable2.getGenericDeclaration() && typeVariable.getName().equals(typeVariable2.getName()))) {
                z = z2;
            }
            return z;
        }
    }

    private static int hashCodeOrZero(Object obj) {
        return obj != null ? obj.hashCode() : 0;
    }

    public static String typeToString(Type type) {
        return type instanceof Class ? ((Class) type).getName() : type.toString();
    }

    static Type getGenericSupertype(Type type, Class<?> cls, Class<?> cls2) {
        if (cls2 == cls) {
            return type;
        }
        if (cls2.isInterface()) {
            Class[] interfaces = cls.getInterfaces();
            int length = interfaces.length;
            for (int i = 0; i < length; i++) {
                if (interfaces[i] == cls2) {
                    return cls.getGenericInterfaces()[i];
                }
                if (cls2.isAssignableFrom(interfaces[i])) {
                    return C$Gson$Types.getGenericSupertype(cls.getGenericInterfaces()[i], interfaces[i], cls2);
                }
            }
        }
        if (!cls.isInterface()) {
            while (cls != Object.class) {
                Class<?> superclass = cls.getSuperclass();
                if (superclass == cls2) {
                    return cls.getGenericSuperclass();
                }
                if (cls2.isAssignableFrom(superclass)) {
                    return C$Gson$Types.getGenericSupertype(cls.getGenericSuperclass(), superclass, cls2);
                }
                cls = superclass;
            }
        }
        return cls2;
    }

    static Type getSupertype(Type type, Class<?> cls, Class<?> cls2) {
        C$Gson$Preconditions.checkArgument(cls2.isAssignableFrom(cls));
        return C$Gson$Types.resolve(type, cls, C$Gson$Types.getGenericSupertype(type, cls, cls2));
    }

    public static Type getArrayComponentType(Type type) {
        return type instanceof GenericArrayType ? ((GenericArrayType) type).getGenericComponentType() : ((Class) type).getComponentType();
    }

    public static Type getCollectionElementType(Type type, Class<?> cls) {
        type = C$Gson$Types.getSupertype(type, cls, Collection.class);
        int i = 0;
        if (type instanceof WildcardType) {
            type = ((WildcardType) type).getUpperBounds()[i];
        }
        if (type instanceof ParameterizedType) {
            return ((ParameterizedType) type).getActualTypeArguments()[i];
        }
        return Object.class;
    }

    public static Type[] getMapKeyAndValueTypes(Type type, Class<?> cls) {
        int i = 1;
        int i2 = 0;
        int i3 = 2;
        Type[] typeArr;
        if (type == Properties.class) {
            typeArr = new Type[i3];
            typeArr[i2] = String.class;
            typeArr[i] = String.class;
            return typeArr;
        }
        type = C$Gson$Types.getSupertype(type, cls, Map.class);
        if (type instanceof ParameterizedType) {
            return ((ParameterizedType) type).getActualTypeArguments();
        }
        typeArr = new Type[i3];
        typeArr[i2] = Object.class;
        typeArr[i] = Object.class;
        return typeArr;
    }

    public static Type resolve(Type type, Class<?> cls, Type type2) {
        Type resolveTypeVariable;
        while (type2 instanceof TypeVariable) {
            type2 = (TypeVariable) type2;
            resolveTypeVariable = C$Gson$Types.resolveTypeVariable(type, cls, type2);
            if (resolveTypeVariable == type2) {
                return resolveTypeVariable;
            }
            type2 = resolveTypeVariable;
        }
        if (type2 instanceof Class) {
            resolveTypeVariable = (Class) type2;
            if (resolveTypeVariable.isArray()) {
                type2 = resolveTypeVariable.getComponentType();
                type = C$Gson$Types.resolve(type, cls, type2);
                if (type2 != type) {
                    resolveTypeVariable = C$Gson$Types.arrayOf(type);
                }
                return resolveTypeVariable;
            }
        }
        if (type2 instanceof GenericArrayType) {
            type2 = (GenericArrayType) type2;
            resolveTypeVariable = type2.getGenericComponentType();
            type = C$Gson$Types.resolve(type, cls, resolveTypeVariable);
            if (resolveTypeVariable != type) {
                type2 = C$Gson$Types.arrayOf(type);
            }
            return type2;
        }
        int i = 1;
        int i2 = 0;
        if (type2 instanceof ParameterizedType) {
            type2 = (ParameterizedType) type2;
            resolveTypeVariable = type2.getOwnerType();
            Type resolve = C$Gson$Types.resolve(type, cls, resolveTypeVariable);
            int i3 = resolve != resolveTypeVariable ? i : i2;
            Type[] actualTypeArguments = type2.getActualTypeArguments();
            int length = actualTypeArguments.length;
            while (i2 < length) {
                Type resolve2 = C$Gson$Types.resolve(type, cls, actualTypeArguments[i2]);
                if (resolve2 != actualTypeArguments[i2]) {
                    if (i3 == 0) {
                        actualTypeArguments = (Type[]) actualTypeArguments.clone();
                        i3 = i;
                    }
                    actualTypeArguments[i2] = resolve2;
                }
                i2++;
            }
            if (i3 != 0) {
                type2 = C$Gson$Types.newParameterizedTypeWithOwner(resolve, type2.getRawType(), actualTypeArguments);
            }
            return type2;
        } else if (!(type2 instanceof WildcardType)) {
            return type2;
        } else {
            WildcardType wildcardType = (WildcardType) type2;
            Type[] lowerBounds = wildcardType.getLowerBounds();
            Type[] upperBounds = wildcardType.getUpperBounds();
            if (lowerBounds.length == i) {
                type = C$Gson$Types.resolve(type, cls, lowerBounds[i2]);
                if (type != lowerBounds[i2]) {
                    return C$Gson$Types.supertypeOf(type);
                }
            } else if (upperBounds.length == i) {
                type = C$Gson$Types.resolve(type, cls, upperBounds[i2]);
                if (type != upperBounds[i2]) {
                    return C$Gson$Types.subtypeOf(type);
                }
            }
            return wildcardType;
        }
    }

    static Type resolveTypeVariable(Type type, Class<?> cls, TypeVariable<?> typeVariable) {
        Class declaringClassOf = C$Gson$Types.declaringClassOf(typeVariable);
        if (declaringClassOf == null) {
            return typeVariable;
        }
        type = C$Gson$Types.getGenericSupertype(type, cls, declaringClassOf);
        if (!(type instanceof ParameterizedType)) {
            return typeVariable;
        }
        return ((ParameterizedType) type).getActualTypeArguments()[C$Gson$Types.indexOf(declaringClassOf.getTypeParameters(), typeVariable)];
    }

    private static int indexOf(Object[] objArr, Object obj) {
        for (int i = 0; i < objArr.length; i++) {
            if (obj.equals(objArr[i])) {
                return i;
            }
        }
        throw new NoSuchElementException();
    }

    private static Class<?> declaringClassOf(TypeVariable<?> typeVariable) {
        GenericDeclaration genericDeclaration = typeVariable.getGenericDeclaration();
        return genericDeclaration instanceof Class ? (Class) genericDeclaration : null;
    }

    private static void checkNotPrimitive(Type type) {
        boolean z = ((type instanceof Class) && ((Class) type).isPrimitive()) ? false : true;
        C$Gson$Preconditions.checkArgument(z);
    }
}
