package com.javarush.task.task37.task3707;

import java.io.*;
import java.util.*;

public class AmigoSet<E> extends AbstractSet<E> implements Serializable, Cloneable, Set<E> {

    private static final Object PRESENT = new Object(); //это будет наша заглушка.
    private transient HashMap<E, Object> map; //Список ключей будет нашим сэтом, а вместо значений будем пихать в мапу заглушку PRESENT.

    public AmigoSet() {
        this.map = new HashMap<>();
    }

    public AmigoSet(Collection<? extends E> collection) {
        this.map = new HashMap<>((int)Math.max(16, (collection.size()/.75f) + 1));
        for (E e : collection) {
            map.put(e, PRESENT);
        }
    }

    @Override
    public boolean add(E e) {
        int size = map.size();
        map.put(e, PRESENT);
        return map.size() > size;
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return map.containsKey(o);
    }

    @Override
    public boolean remove(Object o) {
        int size = map.size();
        map.remove(o);
        return map.size() < size;
    }

    @Override
    public void clear() {
        map.clear();
    }


    @Override
    public Iterator iterator() {
        return map.keySet().iterator();
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public Object clone() throws CloneNotSupportedException, InternalError {
        try {
            AmigoSet copy = (AmigoSet)super.clone();
            copy.map = (HashMap) map.clone();
            return copy;
        } catch (Exception e) {
            throw new InternalError(e);
        }
    }

    private final void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();

        out.writeInt(HashMapReflectionHelper.callHiddenMethod(map, "capacity"));
        out.writeFloat(HashMapReflectionHelper.callHiddenMethod(map, "loadFactor"));
        out.writeInt(map.size());

        for (Map.Entry entry : map.entrySet()) {
            E element = (E) entry.getKey();
            out.writeObject(element);
            System.out.println("writeObject" + element);
        }
        out.flush();
    }

    private final void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        map = new HashMap<>(in.readInt(), in.readFloat());
        int size = in.readInt();
        for (int i = 0; i < size; i++) {
            E element = (E) in.readObject();
            map.put(element, PRESENT);
            System.out.println("readObject" + element);
        }
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException { //TODO DELETE THIS

        AmigoSet<String> stringAmigoSet = new AmigoSet<>();
        stringAmigoSet.add("111111");
        stringAmigoSet.add("222222");

        ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream("amigoSet.bin"));
        objectOutputStream.writeObject(stringAmigoSet);

        ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream("amigoSet.bin"));
        AmigoSet stringAmigoSetCopy = (AmigoSet) objectInputStream.readObject();


//        System.out.println(amigoSet.equals(amigoSet1));
        System.out.println(stringAmigoSet);
        System.out.println(stringAmigoSetCopy);
        System.out.println("________");
    }
}
